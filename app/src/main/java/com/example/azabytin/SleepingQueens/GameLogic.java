package com.example.azabytin.SleepingQueens;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by azabytin on 16.01.2018.
 */

public class GameLogic implements iGame, java.io.Serializable {

    protected PlayCardsStack playCardsStack;
    protected PlayCardsStack queenCardsStack;

    protected Player player;
    protected Player opponent;

    protected ArrayList<Card> playedCards = new ArrayList<>();

    public void startNewGame()
    {
        queenCardsStack = new PlayCardsStack( new QueenCardCreater());
        playCardsStack = new PlayCardsStack( new PlayCardCreater());

        player = new Player();
        opponent = new Player(player);
        player.setCanUserPlay(true);
        opponent.setCanUserPlay(false);
        player.SetOpponent(this.opponent);

        refillCardsFromStack();
    }

    protected void refillCardsFromStack(){
        refillCardsFromStack(player);
        refillCardsFromStack(opponent);
    }

    protected void refillCardsFromStack(Player player){
        if(player.CardsNumber()  < 5){
            player.AddCard(playCardsStack.Get());
            refillCardsFromStack( player );
        }
    }

    protected void DropPlayerCard(Card card, Player player){
        playedCards.add(0, card);
        player.RemovecCard( card );
    }

    protected void OnGetBackQueen( Player player){

        player.GetBackQueen( queenCardsStack );
    }

    protected void OnGiveOponentQueen( Player player){

        player.GiveOponentQueen();
    }

    public void playCard(Player player, Card card )
    {
        if(getLastCard()!= null && getLastCard().isKnight() && !card.isDragon()){
            player.GiveOponentQueen();
        }

        if(getLastCard()!= null && getLastCard().isMagic() && !card.isStick()){
            player.GetBackQueen(queenCardsStack);
        }

        if( card.isKing()){
            player.AddQueenCard( queenCardsStack.Get() );
        }

        DropPlayerCard( card, player);
        refillCardsFromStack( );

        if( card.isJocker()){
            if(  player.GetLastAddedCard().isOddNumver() ){
                player.AddQueenCard( queenCardsStack.Get() );
            }
            if(  player.GetLastAddedCard().isEvenNumver() ){
                player.GetOpponent().AddQueenCard( queenCardsStack.Get() );
            }
        }
    }

    protected boolean IsCardsCanBePlayed(Player player, ArrayList<Card> cardsToPlay)
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
            if( cardsToPlay.get(0).getValue() == cardsToPlay.get(1).getValue() )
                return true;
        }

        if(cardsToPlay.size() == 3){
            if( (cardsToPlay.get(0).getValue() + cardsToPlay.get(1).getValue() ) == cardsToPlay.get(2).getValue() )
                return true;
        }

        if(cardsToPlay.size() == 0){
            return false;
        }
        return false;
    }

    public boolean userPlayCards(ArrayList<Card> cardsToPlay)
    {
        if( !IsCardsCanBePlayed( player, cardsToPlay ) )
            return false;

        for (Card card: cardsToPlay) {
            playCard( player, card );
        }

        player.setCanUserPlay(false);
        opponent.setCanUserPlay(true);

        return true;
    }

    public boolean oponentPlayCards(ArrayList<Card> cardsToPlay){

        if( cardsToPlay.size() == 0){
            AiOponent ai = new AiOponent(opponent, player);
            ai.ChooseOponentCardToPlay(getLastCard().getType(), cardsToPlay );
        }

        if( !IsCardsCanBePlayed( opponent, cardsToPlay ) )
            return false;

        for (Card card: cardsToPlay) {
            playCard( opponent, card );
        }

        player.setCanUserPlay(true);
        opponent.setCanUserPlay(false);

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
        if( playedCards.size() > 0 ) {
            return playedCards.get( 0 );
        }
        return null;
    }
    public Card getBeforeLastCard() {
        if( playedCards.size() > 1 ) {
            return playedCards.get(1);
        }
        return null;
}
    public iGame.Winner whoIsWinner()
    {
        if( player.hasWinCombination() )
            return Winner.PlayerWinner;
        else if( opponent.hasWinCombination() )
            return Winner.OpponentWinner;

        return Winner.NoWinner;
    }
}
