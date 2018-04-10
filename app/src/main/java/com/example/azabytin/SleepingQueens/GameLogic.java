package com.example.azabytin.SleepingQueens;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by azabytin on 16.01.2018.
 */

public class GameLogic implements iGameLogic {

    protected PlayCardsStack playCardsStack;
    protected PlayCardsStack queenCardsStack;

    Card lastCard;
    Card beforeLastCard;

    protected GameState humanGameState;
    protected GameState computerGameState;
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

    humanGameState = new GameStateWaitForСard(humanPlayer, this );
    computerGameState = new GameStateIdle(computerPlayer, this);
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

        return true;
    }

    public boolean userPlayCard(ArrayList<Card> cardsToPlay)
    {
        if( !IsCardsValidToPlay( cardsToPlay ) )
            return false;

        computerGameState = computerGameState.PlayCard( cardsToPlay.get(0) );

        for (Card card: cardsToPlay) {
            humanGameState = humanGameState.PlayCard( card );
        }

        humanGameState = new GameStateIdle( humanGameState );
        computerGameState = new GameStateWaitForСard( computerGameState );

        return true;
    }

    public boolean oponentPlayCard(ArrayList<Card> cardsToPlay){

        if( cardsToPlay.size() == 0){
            AiOponent ai = new AiOponent(computerPlayer, humanPlayer);
            ai.ChooseCardToPlay( computerGameState, cardsToPlay );
        }

        if( !IsCardsValidToPlay( cardsToPlay ) )
            return false;

        humanGameState = humanGameState.PlayCard( cardsToPlay.get(0) );

        for (Card card: cardsToPlay) {
           computerGameState = computerGameState.PlayCard( card );
        }

        computerGameState= new GameStateIdle( computerGameState);
        humanGameState = new GameStateWaitForСard( humanGameState  );

        return true;
    }


    public boolean canOponentPlay(){
        boolean res =humanGameState.getClass().toString().contains("GameStateIdle");
        return res;
    }

    public boolean canUserPlay(){
        return !canOponentPlay();
    }

    public List<Card> getHumanQueenCards() {
        return humanPlayer.GetQueenCards();
    }
    public List<Card> getComputerQueenCards() {   return computerPlayer.GetQueenCards();}
    public List<Card> getComputerCards() {
        return computerPlayer.GetCards();
    }

    public Card[] getComputerCardsArray(){
        Card[] res = new Card[5];
        res[ 0 ] = getComputerCards().get(0);
        return res;
    }

    public List<Card> getHumanCards() {
        return humanPlayer.GetCards();
    }
    public Card getLastCard() {  return lastCard;
    }
    public Card getBeforeLastCard() {  return beforeLastCard; }
    public int hasWinner()
    {
        if( humanPlayer.GetQueenCards().size()>4 )
            return 1;
        else if( computerPlayer.GetQueenCards().size()>4)
            return 2;

        return 0;
    }
}
