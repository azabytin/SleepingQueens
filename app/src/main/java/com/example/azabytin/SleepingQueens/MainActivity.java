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
    private final ArrayList<ImageButton> userCardsButtons = new ArrayList<>();

    private ImageButton usedCardBbutton;
    private ImageButton beforeUsedCardBbutton;

    private CardsProcessor cardProcessor;

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
            UpdateCheckedCardsView();
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

        for (int i = 0; i < 5; i++) {
            int buttonId = com.example.azabytin.SleepingQueens.R.id.cardButton1 + i;
            android.widget.ImageButton button = findViewById( buttonId);
            userCardsButtons.add(button);
        }
        usedCardBbutton = findViewById( com.example.azabytin.SleepingQueens.R.id.usedStackImage );
        beforeUsedCardBbutton = findViewById( com.example.azabytin.SleepingQueens.R.id.usedStackImage2 );


        timerHandler.postDelayed(timerRunnable, 0);
    }

    private void setButtonsImages(List<Card> cards, int firstButton)
    {
       android.widget.ImageButton button;
        int i;
        for (i = 0; i < cards.size(); i++) {
            button = findViewById( firstButton + i);
            button.setImageResource(cards.get(i).resourceId);
        }
        for (; i < 5; i++) {
            button = findViewById( firstButton + i);
            button.setImageResource(com.example.azabytin.SleepingQueens.R.drawable.empty);
        }
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

    private void UpdateCheckedCardsView()
    {
        Button playButton = findViewById(R.id.playButton);
        playButton.setEnabled( gameLogic != null && gameLogic.canUserPlay()) ;

        for (ImageButton button : userCardsButtons){
            if( cardProcessor.isCardSelected( button) ){
                button.setBackgroundResource( R.color.colorAccent );
            } else{
                button.setBackgroundResource( R.color.white);
            }
        }
    }

    private void UpdateCardsView() {
          try{

              setButtonsImages(gameLogic.getPlayerCards(), com.example.azabytin.SleepingQueens.R.id.cardButton1);
              cardProcessor.updatePlayerCards();

              usedCardBbutton.setImageResource( cardProcessor.getUsedCardResourceId() );
              beforeUsedCardBbutton.setImageResource( cardProcessor.getBeforeUsedCardResourceId() );

            setButtonsImages(gameLogic.getPlayerQueenCards(), com.example.azabytin.SleepingQueens.R.id.queenCardButton1);
            setButtonsImages(gameLogic.getOpponentQueenCards(), com.example.azabytin.SleepingQueens.R.id.oponentQueenCardButton1);

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
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onStop(){
        super.onStop();
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


