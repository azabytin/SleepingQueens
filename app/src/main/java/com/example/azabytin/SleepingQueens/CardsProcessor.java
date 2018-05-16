package com.example.azabytin.SleepingQueens;

import android.view.View;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class CardsProcessor {

    private Hashtable< Integer, Card> cardButtonToCardHash;
    private ArrayList<Card> selectedCardsToPlay;
    private iGame game;

    public CardsProcessor( iGame _game){
        game = _game;
        reset();
    }
    public void reset(){
        cardButtonToCardHash = new Hashtable<>();
        selectedCardsToPlay = new ArrayList<>();
    }

    public int getPlayerCardResourceId( Card card){
        return 0;
    }

    public int getPlayerCardResourceId( View button){
        return 0;
    }

    public int getUsedCardResourceId( ){
        if (game.getLastCard() == null || game.getLastCard().resourceId == 0 ) {
            return com.example.azabytin.SleepingQueens.R.drawable.back;
        }
        else {
            return game.getLastCard().resourceId;
        }
    }

    public int getBeforeUsedCardResourceId( ){
        if (game.getBeforeLastCard() != null)
            return game.getBeforeLastCard().resourceId;
        else
            return com.example.azabytin.SleepingQueens.R.drawable.back;

    }

    public void updatePlayerCards(){
        int i = 0;
        cardButtonToCardHash.clear();
        List<Card> playerCards= game.getPlayerCards();
        for( Card card : playerCards ){
            cardButtonToCardHash.put(com.example.azabytin.SleepingQueens.R.id.cardButton1 + i++, card );
        }

    }

    public ArrayList<Card> getCardsToPlay(){
        return selectedCardsToPlay;
    }

    public void cleartCardsToPlay(){
        selectedCardsToPlay.clear();
    }


    public boolean isCardSelected( View button){
        return selectedCardsToPlay.contains( cardButtonToCardHash.get(button));
    }

    public void onButtonClick( View button){
        try {
            Card card = cardButtonToCardHash.get(button);

            if( selectedCardsToPlay.contains(card)){
                selectedCardsToPlay.remove(card);
            }else{
                selectedCardsToPlay.add(card);
            }
        }
        catch(Exception ignored)
        {}
    }
}
