package com.example.azabytin.SleepingQueens;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private iGame gameLogic;
    private Hashtable< Integer, Card> cardButtonToCardHash;
    private ArrayList<Card> selectedCardsToPlay;
    private NetworkGameThread networkGameThread = null;
    private boolean needUpdate = true;
    private ProgressDialog networkConnectProgressDialog = null;


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

    private void setUsedCardButton(int resourceId)
    {
        android.widget.ImageButton button = findViewById( com.example.azabytin.SleepingQueens.R.id.usedStackImage );
        button.setImageResource( resourceId );
    }

    private void setBeforeUsedCardButton(int resourceId)
    {
        android.widget.ImageButton button = findViewById( com.example.azabytin.SleepingQueens.R.id.usedStackImage2 );
        button.setImageResource( resourceId );
    }

    private void CleanUp()
    {
        gameLogic = null;
        cardButtonToCardHash = new Hashtable<>();
        selectedCardsToPlay = new ArrayList<>();

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
        CharSequence[] items = {"Играть с Андроидом", "Играть по сети вдвоем"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выбери режим игры");

        DialogInterface.OnClickListener onGameSelection = (a,item)->{
            if(item==1)
                OnStartTwoPlayerGame();
            else
                OnStartOnePlayerGame();
        };
        builder.setItems(items, onGameSelection);
        builder.setCancelable(false);
        AlertDialog alert = builder.create();
        alert.show();
    }

    private  void OnStartTwoPlayerGame()
    {
        new ClientServerNegotiatorThread().run();
        UpdateCardsView();

        networkConnectProgressDialog = new ProgressDialog(this);
        networkConnectProgressDialog.setMessage("Жду второго игрока... ");
        networkConnectProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        networkConnectProgressDialog.setIndeterminate(true);
        networkConnectProgressDialog.setCancelable(false);

        networkConnectProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Отмена", (dialog, which)-> {
            dialog.dismiss();
            CleanUp();
            onStartNewGame();
        });

        networkConnectProgressDialog.show();

    }
    private  void OnStartOnePlayerGame()
    {
        gameLogic = new GameLogic();
        timerHandler.postDelayed(timerOponentPlay, 0);
        UpdateCardsView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.azabytin.SleepingQueens.R.layout.activity_main);
        Toolbar toolbar = findViewById(com.example.azabytin.SleepingQueens.R.id.toolbar);
        setSupportActionBar(toolbar);
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


    private void showWinMessage(String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle("Игра окончена");
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton( "Новая игра", (dialog, which)-> onStartNewGame() );
        builder.show();
    }

    public void onClickPlay() {

        new AsyncTask<Void, Boolean, Boolean>() {
            @Override
            protected Boolean doInBackground( final Void ... params ) {
                return gameLogic.userPlayCards(selectedCardsToPlay);
            }

            @Override
            protected void onPostExecute( final Boolean result ) {
                if( result ) {
                    selectedCardsToPlay.clear();
                    UpdateCardsView();
                    needUpdate = true;
                }
            }
        }.execute();
    }

    @Override
    public void onClick( View v) {
        try {
            Card card = cardButtonToCardHash.get(v.getId());

            if( selectedCardsToPlay.contains(card)){
                selectedCardsToPlay.remove(card);
            }else{
                selectedCardsToPlay.add(card);
            }
        }
        catch(Exception ignored)
            {}
    }

    private void UpdateCheckedCardsView()
    {
        Button playButton = findViewById(R.id.playButton);
        if (gameLogic != null && gameLogic.canUserPlay()) {
            playButton.setEnabled(true);
        } else {
            playButton.setEnabled(false);
        }

        android.widget.ImageButton button;
        int i;
        for (i = 0; i < 5; i++) {
            int buttonId = com.example.azabytin.SleepingQueens.R.id.cardButton1 + i;
            button = findViewById( buttonId);

            if( selectedCardsToPlay.contains( cardButtonToCardHash.get(buttonId) ))
            {
                button.setBackgroundResource( R.color.colorAccent );
            }
            else
            {
                button.setBackgroundResource( R.color.white);
            }
        }

    }

    private void SetPlayerCardsImages(){

        int i = 0;
        cardButtonToCardHash.clear();
        List<Card> playerCards= gameLogic.getPlayerCards();
        for( Card card : playerCards ){
            cardButtonToCardHash.put(com.example.azabytin.SleepingQueens.R.id.cardButton1 + i++, card );
        }
    }

    private void UpdateCardsView() {
          try{

              setButtonsImages(gameLogic.getPlayerCards(), com.example.azabytin.SleepingQueens.R.id.cardButton1);
              SetPlayerCardsImages();

            if (gameLogic.getLastCard() == null || gameLogic.getLastCard().resourceId == 0 ) {
                setUsedCardButton(com.example.azabytin.SleepingQueens.R.drawable.back);
            }
            else {
                setUsedCardButton(gameLogic.getLastCard().resourceId);
            }

            if (gameLogic.getBeforeLastCard() != null)
                setBeforeUsedCardButton(gameLogic.getBeforeLastCard().resourceId);
            else
                setBeforeUsedCardButton(com.example.azabytin.SleepingQueens.R.drawable.back);

            setButtonsImages(gameLogic.getPlayerQueenCards(), com.example.azabytin.SleepingQueens.R.id.queenCardButton1);
            setButtonsImages(gameLogic.getOpponentQueenCards(), com.example.azabytin.SleepingQueens.R.id.oponentQueenCardButton1);

            if (gameLogic.whoIsWinner() == iGame.Winner.PlayerWinner) {
                CleanUp();
                showWinMessage("Вы выиграли");
            } else if (gameLogic.whoIsWinner() == iGame.Winner.OpponentWinner) {
                CleanUp();
                showWinMessage("Вы проиграли!");
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
        Intent feedbackEmail = new Intent(Intent.ACTION_SEND);
        feedbackEmail.setType("text/email");
        feedbackEmail.putExtra(Intent.EXTRA_EMAIL, new String[] {"azabytin@gmail.com"});
        feedbackEmail.putExtra(Intent.EXTRA_SUBJECT, "Отзыв");
        startActivity(Intent.createChooser(feedbackEmail, "Отправить отзыв ( Нужно выбрать Gmail ):"));

        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle("Отправка отзыва");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setMessage("Для отправки отзыва нужно выбрать Gmail");
        builder.setCancelable(false);
        builder.setPositiveButton( "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }
}


