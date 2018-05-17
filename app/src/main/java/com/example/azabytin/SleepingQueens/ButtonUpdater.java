package com.example.azabytin.SleepingQueens;

import android.app.Activity;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

public class ButtonUpdater implements Runnable{

    private Activity mainActivity;
    private CardsProcessor cardProcessor;
    private final ArrayList<Runnable> buttonsUpdaters= new ArrayList<>();

    ImageButton usedCardBbutton = mainActivity.findViewById( com.example.azabytin.SleepingQueens.R.id.usedStackImage );
    ImageButton beforeUsedCardBbutton = mainActivity.findViewById( com.example.azabytin.SleepingQueens.R.id.usedStackImage2 );

    public ButtonUpdater(Activity _mainActivity, CardsProcessor _cardProcessor){
        mainActivity = _mainActivity;
        cardProcessor= _cardProcessor;

        buttonsUpdaters.add(  ()-> usedCardBbutton.setImageResource( cardProcessor.getUsedCardResourceId() ));
        buttonsUpdaters.add(  ()-> usedCardBbutton.setImageResource( cardProcessor.getBeforeUsedCardResourceId() ));
    }

    private void setButtonsImages(List<Card> cards, int firstButton)
    {
        for (int i = 0; i < cards.size(); i++) {
            ImageButton button = mainActivity.findViewById( firstButton + i);
            button.setImageResource(cards.get(i).resourceId);

            if( cardProcessor.isCardSelected( button) ){
                button.setBackgroundResource( R.color.colorAccent );
            } else{
                button.setBackgroundResource( R.color.white);
            }
        }
    }

    @Override
    public void run(){
        setButtonsImages(cardProcessor.getPlayerCards(), com.example.azabytin.SleepingQueens.R.id.cardButton1);
        setButtonsImages(cardProcessor.getPlayerQueenCards(), com.example.azabytin.SleepingQueens.R.id.queenCardButton1);
        setButtonsImages(cardProcessor.getOpponentQueenCards(), com.example.azabytin.SleepingQueens.R.id.oponentQueenCardButton1);

        for (Runnable updater : buttonsUpdaters ) {
            updater.run();
        }
    }
}
