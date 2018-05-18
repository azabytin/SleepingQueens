package com.example.azabytin.SleepingQueens;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by azabytin on 06.03.2018.
 */

public class Player implements java.io.Serializable
{
    private final UserCards сards;
    private final List<Card> queenCards;
    private Player opponent;
    boolean canUserPlay;

    public Player( Player o)
    {
        opponent = o;
        сards = new UserCards();
        queenCards = new ArrayList<>();
    }
    public Player( )
    {
        сards = new UserCards();
        queenCards = new ArrayList<>();
    }

    public void play(){
        canUserPlay = false;
        opponent.setCanUserPlay( true);
    }

    public void setPlayAgian(){
        canUserPlay = true;
        opponent.setCanUserPlay( false );
    }

    public UserCards GetCards(){
        return сards;
    }

    public List<Card> GetQueenCards(){
        return queenCards;
    }

    public Player GetOpponent(){
        return opponent;
    }
    public void SetOpponent(Player o){
        opponent = o;
    }

    public void GetBackQueen( PlayCardsStack queenCardsStack ){

        if(queenCards.size() > 0){
            queenCardsStack.Add(queenCards.get(0));
            queenCards.remove(0);
        }
    }

    public void GiveOponentQueen(){
        if(queenCards.size() > 0){
            opponent.GetQueenCards().add(queenCards.get(0));
            queenCards.remove(0);
        }
    }

    private boolean hasDogCatQueen(){
        for( Card card : queenCards){
            if( card.isDogCatQueen()){
                return true;
            }
        }
        return false;
    }

    public void AddQueenCard( PlayCardsStack queensStack)
    {
        Card queenCard = queensStack.Get();

        if( queenCard.isDogCatQueen() && hasDogCatQueen()){
            queensStack.Add(queenCard);
        }
        else{
            queenCards.add(0, queenCard);
        }

        if( queenCard.isRoseQueen() ){
            queenCards.add(0, queensStack.Get());
        }
    }

    public void AddCard( Card card)
    {
        сards.add(0, card);
    }

    public Card GetLastAddedCard( )
    {
        return сards.get(0);
    }

    public int CardsNumber( ) {
        return сards.size();
    }

    void RemovecCard( Card card ){
        сards.remove( card );
    }

    public boolean isCanUserPlay() {
        return canUserPlay;
    }

    public void setCanUserPlay(boolean _canUserPlay) {
        canUserPlay = _canUserPlay;
    }

    public boolean hasWinnigCombination(){
        int value = 0;
        for( Card card: queenCards){
            value += card.getValue();
        }
        return (value >= 50) || (queenCards.size() > 2);
    }

}
