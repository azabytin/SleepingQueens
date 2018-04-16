package com.example.azabytin.SleepingQueens;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by azabytin on 16.01.2018.
 */

public class GameLogic implements iGameLogic, java.io.Serializable {

    protected PlayCardsStack playCardsStack;
    protected PlayCardsStack queenCardsStack;

    Card lastCard;
    Card beforeLastCard;

    protected GameState playerGameState;
    protected GameState opponentGameState;
    protected Player humanPlayer;
    protected Player computerPlayer;


public void startNewGame()
{
    queenCardsStack = new PlayCardsStack( new QueenCardCreater());
    playCardsStack = new PlayCardsStack( new PlayCardCreater());

    humanPlayer = new Player();
    computerPlayer = new Player( humanPlayer );
    humanPlayer.SetOpponent(this.computerPlayer);

    refillCardsFromStack( humanPlayer );
    refillCardsFromStack( computerPlayer );

    playerGameState = new GameStateWaitForСard(humanPlayer, this );
    opponentGameState = new GameStateIdle(computerPlayer, this);
}

    protected void refillCardsFromStack(Player player){

        player.AddCard(playCardsStack.Get());

        if(player.CardsNumber()  < 5){
            refillCardsFromStack( player );
        }
    }

    protected void removeCardFromPlayer( Card card, Player player){
        player.RemovecCard( card );

        beforeLastCard = lastCard;
        lastCard = card;
    }


    protected void OnGetBackQueen( Player player){

        player.GetBackQueen( queenCardsStack );
    }

    protected void OnGiveOponentQueen( Player player){

        player.GiveOponentQueen();
    }

    public void playCardFromGameState(Player player, Card card )
    {
        removeCardFromPlayer( card, player);
        refillCardsFromStack( player );

        if( card.isKing()){
            player.AddQueenCard( queenCardsStack.Get() );
        }

        if( card.isJocker()){
            if(  player.GetLastAddedCard().isOddNumver() ){
                player.AddQueenCard( queenCardsStack.Get() );
            }
            if(  player.GetLastAddedCard().isEvenNumver() ){
                player.GetOpponent().AddQueenCard( queenCardsStack.Get() );
            }
        }
    }

    protected boolean IsCardsValidToPlay(ArrayList<Card> cardsToPlay)
    {
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

        return false;
    }

    public boolean userPlayCards(ArrayList<Card> cardsToPlay)
    {
        if( !IsCardsValidToPlay( cardsToPlay ) )
            return false;

        opponentGameState = opponentGameState.PlayCard( cardsToPlay.get(0) );

        for (Card card: cardsToPlay) {
            playerGameState = playerGameState.PlayCard( card );
        }

        playerGameState = new GameStateIdle(playerGameState);
        opponentGameState = new GameStateWaitForСard(opponentGameState);

        return true;
    }

    public boolean oponentPlayCards(ArrayList<Card> cardsToPlay){

        if( cardsToPlay.size() == 0){
            AiOponent ai = new AiOponent(computerPlayer, humanPlayer);
            ai.ChooseCardToPlay(opponentGameState, cardsToPlay );
        }

        if( !IsCardsValidToPlay( cardsToPlay ) )
            return false;

        playerGameState = playerGameState.PlayCard( cardsToPlay.get(0) );

        for (Card card: cardsToPlay) {
           opponentGameState = opponentGameState.PlayCard( card );
        }

        opponentGameState = new GameStateIdle(opponentGameState);
        playerGameState = new GameStateWaitForСard(playerGameState);

        return true;
    }


    public boolean canOponentPlay(){
        boolean res = playerGameState.getClass().toString().contains("GameStateIdle");
        return res;
    }

    public boolean canUserPlay(){
        return !canOponentPlay();
    }

    public List<Card> getPlayerQueenCards() {
        return humanPlayer.GetQueenCards();
    }
    public List<Card> getOpponentQueenCards() {   return computerPlayer.GetQueenCards();}
    public List<Card> getOpponentCards() {
        return computerPlayer.GetCards();
    }

    public List<Card> getPlayerCards() {
        return humanPlayer.GetCards();
    }
    public Card getLastCard() {  return lastCard;
    }
    public Card getBeforeLastCard() {  return beforeLastCard; }
    public iGameLogic.Winner whoIsWinner()
    {
        if( humanPlayer.GetQueenCards().size()>4 )
            return Winner.PlayerWinner;
        else if( computerPlayer.GetQueenCards().size()>4)
            return Winner.OpponentWinner;

        return Winner.NoWinner;
    }
}
