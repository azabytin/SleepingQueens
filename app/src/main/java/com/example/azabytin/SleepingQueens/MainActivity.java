package com.example.azabytin.SleepingQueens;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    protected iGame gameLogic;
    protected Hashtable< Integer, Card> cardButtonToCardHash;
    protected ArrayList<Card> selectedCardsToPlay;
    NetworkGameThread networkGameThread = null;
    boolean needUpdate = true;
    protected ProgressDialog networkConnectProgressDialog = null;

    public class executePlayCards implements Runnable{

        private ArrayList<Card> playCards;
        executePlayCards(ArrayList<Card> _playCards){
            playCards = _playCards;
        }

        public void run(){
            gameLogic.oponentPlayCards(playCards);
            UpdateCardsView();
        }

    }
    public class executeInitServerGameLogic implements Runnable{

        private GameLogic game;
        executeInitServerGameLogic(GameLogic _game){
            game = _game;
        }

        public void run(){
            if( game != null) {
                gameLogic = game;
                gameLogic.startNewGame();
                UpdateCardsView();
            }
            if( networkConnectProgressDialog != null ){
                networkConnectProgressDialog.dismiss();
                networkConnectProgressDialog = null;
            }
        }

    }

    public class executeUpdateClientGameLogic implements Runnable{

        private iGame game;
        executeUpdateClientGameLogic(iGame _game){
            game = _game;
        }

        public void run(){
            if( networkGameThread == null ) {
                return;
            }

            gameLogic = game;
            if(needUpdate || gameLogic.canUserPlay()){
                UpdateCardsView();
            }

            if( networkConnectProgressDialog != null ){
                networkConnectProgressDialog.dismiss();
                networkConnectProgressDialog = null;
            }
        }
    }
    protected Handler udpHandler = new Handler();

    protected Handler timerHandler = new Handler();
    protected Runnable timerRunnable = new Runnable()  {

        @Override
        public void run() {
            UpdateCheckedCardsView();
            timerHandler.postDelayed(this, 100);
        }
    };

    protected Runnable timerRunnableOponentPlay = new Runnable()  {

        @Override
        public void run() {
            if( gameLogic != null && gameLogic.canOponentPlay() ){
                gameLogic.oponentPlayCards( new ArrayList<Card>());
                UpdateCardsView();
            }
            timerHandler.postDelayed(this, 2000);
        }
    };

    protected void setUsedCardButton( int resourceId)
    {
        android.widget.ImageButton button = findViewById( com.example.azabytin.SleepingQueens.R.id.usedStackImage );
        button.setImageResource( resourceId );
    }

    protected void setBeforeUsedCardButton( int resourceId)
    {
        android.widget.ImageButton button = findViewById( com.example.azabytin.SleepingQueens.R.id.usedStackImage2 );
        button.setImageResource( resourceId );
    }

    protected void CleanUp()
    {
        gameLogic = null;
        cardButtonToCardHash = new Hashtable<Integer, Card>();
        selectedCardsToPlay = new ArrayList<Card>();

        if( networkGameThread != null ){
            networkGameThread.stopThread();
            try{
                networkGameThread.join();
                networkGameThread = null;
            }catch (Exception e){}
        }
    }
    protected void onStartNewGame()
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
    protected  void OnStartTwoPlayerGame()
    {
        networkGameThread = new NetworkGameThread(this,udpHandler, new GameLogic());
        Thread thread = new Thread(networkGameThread);
        thread.start();
        UpdateCardsView();

        networkConnectProgressDialog = new ProgressDialog(this);
        networkConnectProgressDialog.setMessage("Жду второго игрока... ");
        networkConnectProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        networkConnectProgressDialog.setIndeterminate(true);
        networkConnectProgressDialog.setCancelable(false);
        Message msg = new Message();

        networkConnectProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                CleanUp();
                onStartNewGame();
            }
        });

        networkConnectProgressDialog.show();

    }
    protected  void OnStartOnePlayerGame()
    {
        gameLogic = new GameLogic();
        gameLogic.startNewGame();
        timerHandler.postDelayed(timerRunnableOponentPlay, 0);
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

    protected void setButtonsImages( List<Card> cards, int firstButton )
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


    public void showWinMessage( String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle("Игра окончена");
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton( "Новая игра", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                onStartNewGame();
            }


        });
        builder.show();
    }

    public void onClickPlay( View v) {

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
        catch(Exception e)
            {}
    }

    protected void UpdateCheckedCardsView()
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

    protected void SetPlayerCardsImages(){

        int i = 0;
        cardButtonToCardHash.clear();
        List<Card> playerCards= gameLogic.getPlayerCards();
        for( Card card : playerCards ){
            cardButtonToCardHash.put(com.example.azabytin.SleepingQueens.R.id.cardButton1 + i++, card );
        }
    }

    protected void UpdateCardsView() {
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
        }catch(Exception e){}

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

        //builder.show();
    }
}


