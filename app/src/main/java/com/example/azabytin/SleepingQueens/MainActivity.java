package com.example.azabytin.SleepingQueens;

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

    protected iGameLogic gameLogic;
    protected Hashtable< Integer, Card> viewToCardHash;
    protected ArrayList<Card> cardsToPlay;
    UdpTaskSocket udpTask = null;

    protected Handler udpHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if( msg.obj instanceof ArrayList){
                    gameLogic.oponentPlayCards((ArrayList<Card>) msg.obj);
                    UpdateCardsView();
                }
                else {
                    boolean updateView = false;
                    if( gameLogic == null ){
                        updateView = true;
                    }
                    gameLogic = (iGameLogic) msg.obj;
                    gameLogic.startNewGame();
                    if(updateView) {
                        UpdateCardsView();
                    }
                }
            }
    } ;

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


    private class PlayCardsTask extends AsyncTask< ArrayList<Card>, Integer, Boolean>{

        protected Boolean doInBackground(ArrayList<Card>...  cardsToPlay) {
            return gameLogic.userPlayCards( cardsToPlay[0] ) ;
        }

        protected void onPostExecute(Boolean result) {
            if( result){
                cardsToPlay.clear();
            }
        }
    }

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

    protected void onStartNewGame()
    {
        gameLogic = null;
        viewToCardHash = new Hashtable<Integer, Card>();
        cardsToPlay = new ArrayList<Card>();

        if( udpTask != null ){
            udpTask.interrupt();
        }

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
        udpTask = new UdpTaskSocket(udpHandler);
        Thread thread = new Thread(udpTask);
        thread.start();
        UpdateCardsView();
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
        Toolbar toolbar = (Toolbar) findViewById(com.example.azabytin.SleepingQueens.R.id.toolbar);
        setSupportActionBar(toolbar);
        timerHandler.postDelayed(timerRunnable, 0);
        onStartNewGame();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);;
        builder.setTitle("Игра окончена");
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton( "Новая игра", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                onStartNewGame();
            }

            ;
        });
        builder.show();
    }

    public void onClickPlay( View v) {
        if( gameLogic.userPlayCards( cardsToPlay ) ){
            cardsToPlay.clear();
            UpdateCardsView();
        }
    }

        @Override
    public void onClick( View v) {
        try {
            Card card = viewToCardHash.get(v.getId());

            if( cardsToPlay.contains(card)){
                cardsToPlay.remove(card);
            }else{
                cardsToPlay.add(card);
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

            if( cardsToPlay.contains( viewToCardHash.get(buttonId) ))
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
        viewToCardHash.clear();
        List<Card> playerCards= gameLogic.getPlayerCards();
        for( Card card : playerCards ){
            viewToCardHash.put(com.example.azabytin.SleepingQueens.R.id.cardButton1 + i++, card );
        }
    }

    protected void UpdateCardsView() {
          try{

              setButtonsImages(gameLogic.getPlayerCards(), com.example.azabytin.SleepingQueens.R.id.cardButton1);
              SetPlayerCardsImages();

            if (gameLogic.getLastCard() != null)
                setUsedCardButton(gameLogic.getLastCard().resourceId);
            else
                setUsedCardButton(com.example.azabytin.SleepingQueens.R.drawable.back);

            if (gameLogic.getBeforeLastCard() != null)
                setBeforeUsedCardButton(gameLogic.getBeforeLastCard().resourceId);
            else
                setBeforeUsedCardButton(com.example.azabytin.SleepingQueens.R.drawable.back);

            setButtonsImages(gameLogic.getPlayerQueenCards(), com.example.azabytin.SleepingQueens.R.id.queenCardButton1);
            setButtonsImages(gameLogic.getOpponentQueenCards(), com.example.azabytin.SleepingQueens.R.id.oponentQueenCardButton1);

            if (gameLogic.whoIsWinner() == iGameLogic.Winner.PlayerWinner) {
                showWinMessage("Вы выиграли");
            } else if (gameLogic.whoIsWinner() == iGameLogic.Winner.OpponentWinner) {
                showWinMessage("Вы проиграли!");
            }
        }catch(Exception e){}

    }

    @Override
    public void onPause() {
        super.onPause();
        //timerHandler.removeCallbacks(timerRunnable);
    }
    @Override
    public void onStop(){
        super.onStop();
    }

    public void onSendfeedbackButton(View v)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);;
        builder.setTitle("Отправка отзыва");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setNeutralButton(android.R.string.ok, null);
        builder.setMessage("Для отправки отзыва нужно выбрать Gmail");
        builder.show();

        Intent feedbackEmail = new Intent(Intent.ACTION_SEND);
        feedbackEmail.setType("text/email");
        feedbackEmail.putExtra(Intent.EXTRA_EMAIL, new String[] {"azabytin@gmail.com"});
        feedbackEmail.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
        startActivity(Intent.createChooser(feedbackEmail, "Send Feedback:"));

    }
}


