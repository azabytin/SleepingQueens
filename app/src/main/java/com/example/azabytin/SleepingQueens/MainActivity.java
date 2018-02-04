package com.example.azabytin.SleepingQueens;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;


import java.util.Hashtable;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    protected GameLogic gameLogic;
    protected Hashtable< View, Card> viewToCardHash;

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

        gameLogic.startNewGame();
        setButtonsImages( gameLogic.getUserCards(), com.example.azabytin.SleepingQueens.R.id.cardButton1 );
        setButtonsImages( gameLogic.getUserQueenCards(), com.example.azabytin.SleepingQueens.R.id.queenCardButton1 );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.azabytin.SleepingQueens.R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(com.example.azabytin.SleepingQueens.R.id.toolbar);
        setSupportActionBar(toolbar);
        onStartNewGame();
    }

    protected void setButtonsImages( List<Card> cards, int firstButton )
    {
       android.widget.ImageButton button;
        int i;
        for (i = 0; i < cards.size(); i++) {
            button = findViewById( firstButton + i);
            button.setImageResource(cards.get(i).resourceId);
            viewToCardHash.put( button, cards.get( i ));
        }
        for (; i < 5; i++) {
            button = findViewById( firstButton + i);
            button.setImageResource(com.example.azabytin.SleepingQueens.R.drawable.empty);
        }
    }

    @Override
    public void onClick( View v) {

        gameLogic.userPlayCard( viewToCardHash.get(v) );
        UpdateCardsView();

        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);;
        builder.setTitle("Игра окончена");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setNeutralButton(android.R.string.ok, null);
        if(gameLogic.hasWinner() == 1){
            builder.setMessage("Вы победили!");
            builder.show();
            onStartNewGame();
            UpdateCardsView();
        }
        else if (gameLogic.hasWinner() == 2){
            builder.setMessage("Вы проиграли!");
            builder.show();
            onStartNewGame();
            UpdateCardsView();
        }
    }

    protected void UpdateCardsView()
    {
        Log.d("GUI", "UpdateCardsView()");
        setButtonsImages( gameLogic.getUserCards(), com.example.azabytin.SleepingQueens.R.id.cardButton1 );

        if( gameLogic.getLastCard() != null )
            setUsedCardButton( gameLogic.getLastCard().resourceId );
        else
            setUsedCardButton( com.example.azabytin.SleepingQueens.R.drawable.back );

        if( gameLogic.getBeforeLastCard() != null )
            setBeforeUsedCardButton( gameLogic.getBeforeLastCard().resourceId );
        else
            setBeforeUsedCardButton( com.example.azabytin.SleepingQueens.R.drawable.back );

        setButtonsImages( gameLogic.getUserQueenCards(), com.example.azabytin.SleepingQueens.R.id.queenCardButton1 );
        setButtonsImages( gameLogic.getOponentQueenCards(), com.example.azabytin.SleepingQueens.R.id.oponentQueenCardButton1 );
    }
}
