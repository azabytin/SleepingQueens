package com.example.azabytin.SleepingQueens;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by azabytin on 06.03.2018.
 */

public class Player implements java.io.Serializable
{
    protected UserCards сards;
    protected List<Card> queenCards;
    protected Player opponent;
    protected boolean canUserPlay;

    public Player( Player o)
    {
        opponent = o;
        сards = new UserCards();
        queenCards = new ArrayList<Card>();
    }
    public Player( )
    {
        сards = new UserCards();
        queenCards = new ArrayList<Card>();
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

    public boolean hasThisNumber(Card card){
        return сards.hasThisNumber(card);
    }

    public void AddQueenCard( Card card)
    {
        queenCards.add(0, card);
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

    Card peekUserCard(){
        return сards.get(0);
    }

    public boolean isCanUserPlay() {
        return canUserPlay;
    }

    public void setCanUserPlay(boolean _canUserPlay) {
        canUserPlay = _canUserPlay;
    }
}
