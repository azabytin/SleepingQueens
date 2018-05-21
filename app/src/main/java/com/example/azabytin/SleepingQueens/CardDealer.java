package com.example.azabytin.SleepingQueens;

import java.util.ArrayList;

public class CardDealer {

    private PlayCardsStack playCardsStack;
    private PlayCardsStack queenCardsStack;
    private ArrayList<Card> playedCards;

    public CardDealer(){
        shuffleCards();
    }
    public void shuffleCards(){
        queenCardsStack = new PlayCardsStack( new QueenCardCreater());
        playCardsStack = new PlayCardsStack( new PlayCardCreater());
        playedCards = new ArrayList<>();
    }

    public void refillAllPlayersCardsFromStack(Player player){
        if(player.CardsNumber()  < 5){
            player.AddCard(playCardsStack.Get());
            refillAllPlayersCardsFromStack( player );
        }
    }
    public void flushCardFromPlayer(Card card, Player player){
        playedCards.add(0, card);
        player.RemovecCard( card );
    }

    public void takeQueenFromPlayer(Player player){
        Card queenCard = player.GetQueen();
        if(queenCard != null){
            queenCardsStack.Add(queenCard);
        }
    }

    public void givePlayerQueen(Player player){

        Card queenCard = queenCardsStack.Get();

        if( queenCard.isDogCatQueen() && player.hasDogCatQueen()){
            queenCardsStack.Add(queenCard);
        }
        else{
            player.addQueenCard(queenCard);
        }

        if( queenCard.isRoseQueen() ){
            givePlayerQueen(player);
        }
    }

    public Card getLastCard(){
        if( playedCards.size() > 0 ) {
            return playedCards.get( 0 );
        }
        return null;
    }

    public Card getBeforeLastCard(){
        if( playedCards.size() > 1 ) {
            return playedCards.get(1);
        }
        return null;
    }

}
