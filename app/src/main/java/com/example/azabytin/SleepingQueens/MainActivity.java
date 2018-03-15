package com.example.azabytin.SleepingQueens;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    protected GameLogic gameLogic;
    protected Hashtable< View, Card> viewToCardHash;
    protected ArrayList<Card> cardsToPlay;

    protected Runnable udpRunnable = new UdpTask();

    protected Handler timerHandler = new Handler();
    protected Runnable timerRunnable = new Runnable()  {

        @Override
        public void run() {
            UpdateCardsView();
            timerHandler.postDelayed(this, 300);
        }
    };


    protected void setPlayCardButton( int buttonId, Card card )
    {
        android.widget.ImageButton button = findViewById( buttonId );
        viewToCardHash.put(button, card);
        button.setImageResource(card.resourceId);
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
        gameLogic = new GameLogic();
        viewToCardHash = new Hashtable<View, Card>();
        cardsToPlay = new ArrayList<Card>();

        gameLogic.startNewGame();
        setButtonsImages( gameLogic.getHumanCards(), com.example.azabytin.SleepingQueens.R.id.cardButton1 );
        setButtonsImages( gameLogic.getHumanQueenCards(), com.example.azabytin.SleepingQueens.R.id.queenCardButton1 );

        timerHandler.postDelayed(timerRunnable, 0);
        Thread thread = new Thread(new UdpTask());
        thread.start();
        try {
            thread.join();
        }
        catch (InterruptedException e) { e.printStackTrace(); }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.azabytin.SleepingQueens.R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(com.example.azabytin.SleepingQueens.R.id.toolbar);
        setSupportActionBar(toolbar);
        onStartNewGame();
        //String s = NetUtils.getIPAddress(true);
    }

    protected void setButtonsImages( List<Card> cards, int firstButton )
    {
       android.widget.ImageButton button;
        int i;
        for (i = 0; i < cards.size(); i++) {
            button = findViewById( firstButton + i);
            button.setImageResource(cards.get(i).resourceId);
            viewToCardHash.put( button, cards.get( i ));
            if( cards.get( i ).isMarkedToPlay() )
            {
                button.setBackgroundResource( R.color.colorAccent );
            }
            else
            {
                button.setBackgroundResource( R.color.white);
            }
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
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setNeutralButton(android.R.string.ok, null);
        builder.setMessage(message);
        builder.show();
    }

    public void onClickPlay( View v) {
        if( gameLogic.userPlayCard( cardsToPlay ) ){
            cardsToPlay.clear();
        }
    }

        @Override
    public void onClick( View v) {
        Card card = viewToCardHash.get(v);
        card.setMarkedToPlay( !card.isMarkedToPlay() );

        if( card.isMarkedToPlay() )  {
            cardsToPlay.add( card );
        }
        else {
            cardsToPlay.remove( card );
        }
    }

    protected void UpdateCardsView()
    {
        setButtonsImages( gameLogic.getHumanCards(), com.example.azabytin.SleepingQueens.R.id.cardButton1 );

        if( gameLogic.getLastCard() != null )
            setUsedCardButton( gameLogic.getLastCard().resourceId );
        else
            setUsedCardButton( com.example.azabytin.SleepingQueens.R.drawable.back );

        if( gameLogic.getBeforeLastCard() != null )
            setBeforeUsedCardButton( gameLogic.getBeforeLastCard().resourceId );
        else
            setBeforeUsedCardButton( com.example.azabytin.SleepingQueens.R.drawable.back );

        setButtonsImages( gameLogic.getHumanQueenCards(), com.example.azabytin.SleepingQueens.R.id.queenCardButton1 );
        setButtonsImages( gameLogic.getComputerQueenCards(), com.example.azabytin.SleepingQueens.R.id.oponentQueenCardButton1 );

        if(gameLogic.hasWinner() == 1){
            timerHandler.removeCallbacks(timerRunnable);
            showWinMessage("Вы выиграли");
            onStartNewGame();
            UpdateCardsView();
        }
        else if (gameLogic.hasWinner() == 2){
            timerHandler.removeCallbacks(timerRunnable);
            showWinMessage("Вы проиграли!");
            onStartNewGame();
            UpdateCardsView();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        //timerHandler.removeCallbacks(timerRunnable);
    }

}


