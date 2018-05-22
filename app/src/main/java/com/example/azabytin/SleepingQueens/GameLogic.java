package com.example.azabytin.SleepingQueens;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by azabytin on 16.01.2018.
 */

public class GameLogic extends iGame implements java.io.Serializable {


    private CardDealer cardDealer;

    protected Player player;
    protected Player opponent;

    public GameLogic(CardDealer cardDealer){
        this.cardDealer = cardDealer;
        startNewGame();
    }

    public void startNewGame()
    {
        cardDealer.shuffleCards();

        player = new Player();
        opponent = new Player(player);
        player.setCanUserPlay(true);
        opponent.setCanUserPlay(false);
        player.SetOpponent(this.opponent);

        cardDealer.refillAllPlayersCardsFromStack(player);
        cardDealer.refillAllPlayersCardsFromStack(opponent);
    }

    private void playCard(Player player, Card card)
    {
        player.play();

        if(getLastCard()!= null && getLastCard().isKnight() ) {
            if( card.isDragon()) {
                player.setPlayAgian();
            }
            else{
                player.GiveOponentQueen();
            }
        }

        if(getLastCard()!= null && getLastCard().isMagic() ){
            if( card.isStick() ){
                player.setPlayAgian();
            }
            else {
                cardDealer.takeQueenFromPlayer(player);
            }
        }

        if( card.isKing()){
            cardDealer.givePlayerQueen(player);
        }

        cardDealer.flushCardFromPlayer( card, player);
        cardDealer.refillAllPlayersCardsFromStack(player);
        cardDealer.refillAllPlayersCardsFromStack(opponent);

        if( card.isJocker()){
            if(  player.getLastAddedCard().isOddNumber() ){
                cardDealer.givePlayerQueen(player);
            }
            if(  player.getLastAddedCard().isEvenNumber() ){
                cardDealer.givePlayerQueen( player.getOpponent() );
            }
        }
    }

    private boolean isCardsCanBePlayed(Player player, ArrayList<Card> cardsToPlay)
    {
        if( !player.canUserPlay ){
            return false;
        }

        for (Card card: cardsToPlay) {
            if(player.GetCards().indexOf( card ) == -1){
                return false;
            }
        }

        if(cardsToPlay.size() == 1){
            return true;
        }

        if(cardsToPlay.size() == 2){
            if( cardsToPlay.get(0).getCardValue() == cardsToPlay.get(1).getCardValue() )
                return true;
        }

        if(cardsToPlay.size() == 3){
            if( (cardsToPlay.get(0).getCardValue() + cardsToPlay.get(1).getCardValue() ) == cardsToPlay.get(2).getCardValue() )
                return true;
        }

        if(cardsToPlay.size() == 0){
            return false;
        }
        return false;
    }

    public boolean userPlayCards(ArrayList<Card> cardsToPlay)
    {
        if( !isCardsCanBePlayed( player, cardsToPlay ) )
            return false;

        for (Card card: cardsToPlay) {
            playCard( player, card );
        }

        return true;
    }

    public boolean oponentPlayCards(ArrayList<Card> cardsToPlay){

        if( cardsToPlay == null || !canOponentPlay()){
            return false;
        }

        if( cardsToPlay.size() == 0){
            AiOponent ai = new AiOponent(opponent, player);
            ai.ChooseOponentCardToPlay(getLastCard().getCardType(), cardsToPlay );
        }

        if( !isCardsCanBePlayed( opponent, cardsToPlay ) )
            return false;

        for (Card card: cardsToPlay) {
            playCard( opponent, card );
        }

        return true;
    }


    public boolean canOponentPlay(){
        return opponent.isCanUserPlay();
    }

    public boolean canUserPlay(){
        return player.isCanUserPlay();
    }

    public List<Card> getPlayerQueenCards() {
        return player.GetQueenCards();
    }
    public List<Card> getOpponentQueenCards() {   return opponent.GetQueenCards();}
    public List<Card> getOpponentCards() {
        return opponent.GetCards();
    }

    public List<Card> getPlayerCards() {
        return player.GetCards();
    }
    public Card getLastCard() {
        return cardDealer.getLastCard();
    }
    public Card getBeforeLastCard() {
        return cardDealer.getBeforeLastCard();
    }
    public iGame.Winner whoIsWinner()
    {
        if( player.hasWinnigCombination() )
            return Winner.PlayerWinner;
        else if( opponent.hasWinnigCombination() )
            return Winner.OpponentWinner;

        return Winner.NoWinner;
    }
}
