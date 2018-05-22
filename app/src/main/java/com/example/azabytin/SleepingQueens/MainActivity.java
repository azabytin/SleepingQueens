package com.example.azabytin.SleepingQueens;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private iGame gameLogic;
    private NetworkGameThread networkGameThread = null;
    private ButtonUpdater buttonUpdater = null;

    private CardsProcessor cardProcessor = null;
    private Button playButton = null;

    class ClientServerNegotiatorThread extends Thread {
        @Override
        public void run() {

            try {
                ClientServerNegotiator clientServerNegotiator = new ClientServerNegotiator();
                while( !Thread.interrupted() && clientServerNegotiator.checkForNewPeer() < 2 ){
                    Thread.sleep(100);
                }

                runOnUiThread( ()-> onNetworkPeerFound( clientServerNegotiator )) ;
            } catch (Exception ignored){
            }
        }
    }

    class NetworkGameThread extends Thread {

        private final iGame serverThreadGameLogic;

        NetworkGameThread(iGame gameLogic){
            serverThreadGameLogic = gameLogic;
        }
        @Override
        public void run() {

            ServerSocketSerializer serverSerializer;
            try {
                serverSerializer = new ServerSocketSerializer();
                while (!Thread.interrupted()) {
                    try {

                        serverSerializer.accept();
                        serverSerializer.writeGameLogic(serverThreadGameLogic);

                        ArrayList<Card> cardsToPlay = serverSerializer.readCardsToPlay();
                        runOnUiThread( ()-> gameLogic.oponentPlayCards(cardsToPlay));
                        Thread.sleep(100);

                    } catch (Exception ignored) {
                    }
                }
                serverSerializer.accept();
                serverSerializer.writeGameLogic(serverThreadGameLogic);

            }catch (Exception e){
                return;
            }


            serverSerializer.close();
        }
    }

    private void onNetworkPeerFound(ClientServerNegotiator clientServerNegotiator){

        DialogsBuilder.dismissNetworkConnectProgressDialog();

        if( clientServerNegotiator.getGameType() == ClientServerNegotiator.GameType.ServerGame ){
            gameLogic = new GameLogic( new CardDealer() );
            networkGameThread = new NetworkGameThread( gameLogic );
            networkGameThread.start();
        }else {
            gameLogic = new GameState( clientServerNegotiator.getServerHostName() );
            timerHandler.postDelayed(executeUpdateClientGameState, 100);
        }

        cardProcessor.setGame( gameLogic );
        timerHandler.postDelayed(updateTimerRunnable, 100);
    }

    private final Runnable executeUpdateClientGameState = new Runnable()  {

        @Override
        public void run(){
            timerHandler.postDelayed(this, 1000);
            GameState gameState = (GameState) gameLogic;
            gameState.Update();
        }
    };

    private final Handler timerHandler = new Handler();
    private final Runnable updateTimerRunnable = new Runnable()  {

        @Override
        public void run() {
            timerHandler.postDelayed(this, 100);
            runOnUiThread(()-> doUpdateCardsView());
        }
    };

        private final Runnable timerOponentPlayRunnable = new Runnable()  {

        @Override
        public void run() {
            timerHandler.postDelayed(this, 2000);
            runOnUiThread(()-> gameLogic.oponentPlayCards( ));
        }
    };

    private void stopTimersAndThreads()
    {
        timerHandler.removeCallbacks(updateTimerRunnable);
        timerHandler.removeCallbacks(timerOponentPlayRunnable);
        timerHandler.removeCallbacks(executeUpdateClientGameState);
        if( networkGameThread != null ) {
            networkGameThread.interrupt();
        }
    }

    private void doStartNewGame()
    {
        stopTimersAndThreads();
        DialogsBuilder.buildGameTypeSelectorDialog(this, (a,item)->{
                    if(item==1)
                        onStartTwoPlayerGame();
                    else
                        onStartOnePlayerGame();
                }
        ).show();

    }

    private  void onStartTwoPlayerGame()
    {
        cardProcessor = new CardsProcessor( new iGame());
        buttonUpdater = new ButtonUpdater( this, cardProcessor);
        playButton = findViewById(R.id.playButton);

        new ClientServerNegotiatorThread().start();
        DialogsBuilder.buildNetworkConnectProgressDialog(this, (dialog, which)-> {
                    dialog.dismiss();
                    doStartNewGame();
                    }
                ).show();

    }
    private  void onStartOnePlayerGame()
    {
        gameLogic = new GameLogic( new CardDealer() );
        cardProcessor = new CardsProcessor( gameLogic );
        buttonUpdater = new ButtonUpdater( this, cardProcessor);
        playButton = findViewById(R.id.playButton);

        timerHandler.postDelayed(updateTimerRunnable, 100);
        timerHandler.postDelayed(timerOponentPlayRunnable, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.azabytin.SleepingQueens.R.layout.activity_main);
        Toolbar toolbar = findViewById(com.example.azabytin.SleepingQueens.R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    public void onClickPlay(View v) {

        new AsyncTask<Void, Boolean, Boolean>() {
            @Override
            protected Boolean doInBackground( final Void ... params ) {
                return gameLogic.userPlayCards(cardProcessor.getCardsToPlay());
            }

            @Override
            protected void onPostExecute( final Boolean result ) {
                cardProcessor.cleartCardsToPlay();
            }
        }.execute();
    }

    @Override
    public void onClick( View v) {
        cardProcessor.onButtonClick(v);
    }

    private void doUpdateCardsView() {
          try{
              cardProcessor.updatePlayerCards();
              buttonUpdater.run();
              playButton.setEnabled( gameLogic != null && gameLogic.canUserPlay()) ;

              if (gameLogic.whoIsWinner() == iGame.Winner.PlayerWinner) {
                  stopTimersAndThreads();
                DialogsBuilder.buildGameoverDialog(this, "Вы выиграли", (dialog, which)-> doStartNewGame()).show();
            } else if (gameLogic.whoIsWinner() == iGame.Winner.OpponentWinner) {
                  stopTimersAndThreads();
                DialogsBuilder.buildGameoverDialog(this, "Вы проиграли!", (dialog, which)-> doStartNewGame()).show();
            }
        }catch(Exception ignored){}

    }

    @Override
    public void onResume(){
        super.onResume();
        doStartNewGame();
        }

    public void onSendfeedbackButton(View v)
    {
        DialogsBuilder.buildSendFeedbackDialog(this).show();
    }
}


