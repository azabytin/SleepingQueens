package com.example.azabytin.SleepingQueens;

import android.util.Log;

import java.util.Collections;
import java.util.List;

/**
 * Created by azabytin on 16.01.2018.
 */

public class GameLogic {

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

    Player    humanPlayer = new Player();
    Player    computerPlayer = new Player( humanPlayer );
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

    public boolean playCardFromGameState(Player player, Card card )
    {
        removeCardFromPlayer( card, player);

        boolean hasPair = player.hasThisNumber(card);

        if( !hasPair){
            refillCardsFromStack( player );
        }
        if( card.isKing()){
            player.AddQueenCard( queenCardsStack.Get() );
        }


        if( card.isJocker()){
            Log.d("GameLogic", "playCardFromGameState() Jocker played");

            if(  player.GetLastAddedCard().isOddNumver() ){
                Log.d("GameLogic", "playCardFromGameState() Jocker played give player queen");
                player.AddQueenCard( queenCardsStack.Get() );
            }
            if(  player.GetLastAddedCard().isEvenNumver() ){
                Log.d("GameLogic", "playCardFromGameState() Jocker played give opponent queen");
                player.GetOpponent().AddQueenCard( queenCardsStack.Get() );
            }
        }

        return hasPair;
    }

    public void userPlayCard( Card userCard)
    {
        humanGameState = humanGameState.PlayCard( userCard );

        if( humanGameState.TurnEnded()  ){

            computerGameState = computerGameState.PlayCard( userCard );

            Card opponentCard;
            do {
                opponentCard = ChooseOponentCardToPlay(computerGameState);
                computerGameState = computerGameState.PlayCard(opponentCard );
            }while ( computerGameState.TurnEnded() == false );

            humanGameState = humanGameState.PlayCard( opponentCard );
        }
    }

    protected Card ChooseOponentCardToPlay( GameStateKnightAttacked state)
    {
        if( computerPlayer.GetCards().GetDragon() != null )
            return computerPlayer.GetCards().GetDragon();

        return ChooseOponentCardToPlay( (GameState)state );
    }

    protected Card ChooseOponentCardToPlay( GameStateMagicAttacted state)
    {
        if( computerPlayer.GetCards().GetStick() != null )
            return computerPlayer.GetCards().GetStick();

        return ChooseOponentCardToPlay( (GameState)state );
    }

    protected Card ChooseOponentCardToPlay( GameState state)
    {
        if( humanPlayer.GetQueenCards().size() > 0 ) {
            if (computerPlayer.GetCards().GetKnight() != null)
                return computerPlayer.GetCards().GetKnight();

            if (computerPlayer.GetCards().GetMagic() != null)
                return computerPlayer.GetCards().GetMagic();
        }

        if( computerPlayer.GetCards().GetKing() != null )
            return computerPlayer.GetCards().GetKing();

        if( computerPlayer.GetCards().GetNumber() != null )
            return computerPlayer.GetCards().GetNumber();

        return computerPlayer.GetCards().get( 0 );
    }

    public List<Card> getHumanQueenCards() { return humanPlayer.GetQueenCards();
    }
    public List<Card> getComputerQueenCards() {   return computerPlayer.GetQueenCards();}
    public List<Card> getHumanCards() {
        return humanPlayer.GetCards();
    }
    public Card getLastCard() {  return lastCard; }
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
