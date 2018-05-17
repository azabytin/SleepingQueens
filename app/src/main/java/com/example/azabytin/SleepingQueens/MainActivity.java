package com.example.azabytin.SleepingQueens;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private iGame gameLogic;
    private NetworkGameThread networkGameThread = null;
    private boolean needUpdate = true;
    private ProgressDialog networkConnectProgressDialog = null;
    private ButtonUpdater buttonUpdater;

    private CardsProcessor cardProcessor;
    private Button playButton = findViewById(R.id.playButton);

    class ClientServerNegotiatorThread extends Thread {
        @Override
        public void run() {
            ClientServerNegotiator clientServerNegotiator = new ClientServerNegotiator();
            try {
                while( !Thread.interrupted() && clientServerNegotiator.WaitForNewPeer() > 1 ){
                    Thread.sleep(10);
                }
            } catch (Exception ignored){
            }
            runOnUiThread( ()-> onNetworkPeerFound( clientServerNegotiator )) ;
        }
    }

    class NetworkGameThread extends Thread {

        private final AtomicInteger loopCount = new AtomicInteger(Integer.MAX_VALUE);
        private final GameLogic serverThreadGameLogic;

        void stopThread() {
            loopCount.set(1);
        }

        NetworkGameThread(GameLogic gameLogic){
            serverThreadGameLogic = gameLogic;
        }
        @Override
        public void run() {

            ServerSocketSerializer serverSerializer;
            try {
                serverSerializer = new ServerSocketSerializer();
            }catch (Exception e){
                return;
            }

            while (isRunning()) {
                try {

                    serverSerializer.accept();
                    serverSerializer.writeGameLogic(serverThreadGameLogic);

                    ArrayList<Card> cardsToPlay = serverSerializer.readCardsToPlay();
                    runOnUiThread( ()-> {
                        if(gameLogic.oponentPlayCards(cardsToPlay)) {
                            UpdateCardsView();
                        }
                    }) ;

                } catch (Exception e) {
                    //Log.e("NetworkGameThread", "serverLoop::Exception");
                }
            }
            serverSerializer.close();
        }
        private boolean isRunning() {
            return loopCount.decrementAndGet() > 0;
        }
    }

    private void onNetworkPeerFound(ClientServerNegotiator clientServerNegotiator){

        gameLogic = new GameLogic();

        if( networkConnectProgressDialog != null ){
            networkConnectProgressDialog.dismiss();
            networkConnectProgressDialog = null;
        }

        if( clientServerNegotiator.getGameType() == ClientServerNegotiator.GameType.ServerGame ){
            networkGameThread = new NetworkGameThread( new GameLogic() );
            networkGameThread.run();
        }

        gameLogic = new GameState( clientServerNegotiator.getServerHostName() );

        cardProcessor = new CardsProcessor( gameLogic );
        timerHandler.postDelayed(executeUpdateClientGameState, 1000);
        UpdateCardsView();
    }

    private final Runnable executeUpdateClientGameState = new Runnable()  {

        @Override
        public void run(){

            GameState gameState = (GameState) gameLogic;
            gameState.Update();
            if(needUpdate || gameLogic.canUserPlay()){
                UpdateCardsView();
            }
            timerHandler.postDelayed(this, 1000);
        }
    };

    private final Handler timerHandler = new Handler();
    private final Runnable timerRunnable = new Runnable()  {

        @Override
        public void run() {
            UpdateCardsView();
            timerHandler.postDelayed(this, 100);
        }
    };

    private final Runnable timerOponentPlay = new Runnable()  {

        @Override
        public void run() {
            if( gameLogic != null && gameLogic.canOponentPlay() ){
                gameLogic.oponentPlayCards( new ArrayList<>());
                UpdateCardsView();
            }
            timerHandler.postDelayed(this, 2000);
        }
    };

    private void CleanUp()
    {
        gameLogic = null;
        cardProcessor.reset();

        if( networkGameThread != null ){
            networkGameThread.stopThread();
            try{
                networkGameThread.join();
                networkGameThread = null;
            }catch (Exception ignored){}
        }
    }

    private void onStartNewGame()
    {
        CleanUp();
        DialogsBuilder.buildNetworkConnectProgressDialog(this, (a,item)->{
                    if(item==1)
                        OnStartTwoPlayerGame();
                    else
                        OnStartOnePlayerGame();
                }
        ).show();

    }

    private  void OnStartTwoPlayerGame()
    {
        //new ClientServerNegotiatorThread().start();
        UpdateCardsView();
        DialogsBuilder.buildNetworkConnectProgressDialog(this, (dialog, which)-> {
                    dialog.dismiss();
                    CleanUp();
                    onStartNewGame();
                    }
                ).show();

    }
    private  void OnStartOnePlayerGame()
    {
        gameLogic = new GameLogic();
        cardProcessor = new CardsProcessor( gameLogic );
        timerHandler.postDelayed(timerOponentPlay, 0);
        UpdateCardsView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.azabytin.SleepingQueens.R.layout.activity_main);
        Toolbar toolbar = findViewById(com.example.azabytin.SleepingQueens.R.id.toolbar);
        setSupportActionBar(toolbar);

        buttonUpdater = new ButtonUpdater( this, cardProcessor);

        timerHandler.postDelayed(timerRunnable, 0);
    }


    public void onClickPlay() {

        new AsyncTask<Void, Boolean, Boolean>() {
            @Override
            protected Boolean doInBackground( final Void ... params ) {
                return gameLogic.userPlayCards(cardProcessor.getCardsToPlay());
            }

            @Override
            protected void onPostExecute( final Boolean result ) {
                cardProcessor.cleartCardsToPlay();
                if( result ) {
                    UpdateCardsView();
                    needUpdate = true;
                }
            }
        }.execute();
    }

    @Override
    public void onClick( View v) {
        cardProcessor.onButtonClick(v);
    }

    private void UpdateCardsView() {
          try{

              cardProcessor.updatePlayerCards();
              buttonUpdater.run();
              playButton.setEnabled( gameLogic != null && gameLogic.canUserPlay()) ;

              if (gameLogic.whoIsWinner() == iGame.Winner.PlayerWinner) {
                CleanUp();
                DialogsBuilder.buildGameoverDialog(this, "Вы выиграли", (dialog, which)-> onStartNewGame());
            } else if (gameLogic.whoIsWinner() == iGame.Winner.OpponentWinner) {
                CleanUp();
                DialogsBuilder.buildGameoverDialog(this, "Вы проиграли!", (dialog, which)-> onStartNewGame());
            }
        }catch(Exception ignored){}

    }

    @Override
    public void onResume(){
        super.onResume();
        onStartNewGame();
        }

    public void onSendfeedbackButton(View v)
    {
        DialogsBuilder.buildSendFeedbackDialog(this).show();
    }
}


