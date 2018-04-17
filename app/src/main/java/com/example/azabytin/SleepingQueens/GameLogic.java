package com.example.azabytin.SleepingQueens;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by azabytin on 16.01.2018.
 */

public class GameLogic implements iGameLogic, java.io.Serializable {

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

    refillCardsFromStack(player);
    refillCardsFromStack(opponent);
}

    protected void refillCardsFromStack(Player player){

        player.AddCard(playCardsStack.Get());

        if(player.CardsNumber()  < 5){
            refillCardsFromStack( player );
        }
    }

    protected void DropPlayerCard(Card card, Player player){
        player.RemovecCard( card );

        playedCards.add(card);
    }

    protected void OnGetBackQueen( Player player){

        player.GetBackQueen( queenCardsStack );
    }

    protected void OnGiveOponentQueen( Player player){

        player.GiveOponentQueen();
    }

    public void playCard(Player player, Card card )
    {
        DropPlayerCard( card, player);
        refillCardsFromStack( player );

        if(getLastCard().isDragon() && !card.isKnight()){
            player.GiveOponentQueen();
        }

        if(getLastCard().isMagic() && !card.isStick()){
            player.GetBackQueen(queenCardsStack);
        }

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

        for (Card card: cardsToPlay) {
            playCard( player, card );
        }

        return true;
    }

    public boolean oponentPlayCards(ArrayList<Card> cardsToPlay){

        if( cardsToPlay.size() == 0){
            AiOponent ai = new AiOponent(opponent, player);
            ai.ChooseOponentCardToPlay(getLastCard().getType(), cardsToPlay );
        }

        if( !IsCardsValidToPlay( cardsToPlay ) )
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
    public Card getLastCard() {  return playedCards.get(0);
    }
    public Card getBeforeLastCard() {  return playedCards.get(1); }
    public iGameLogic.Winner whoIsWinner()
    {
        if( player.GetQueenCards().size()>4 )
            return Winner.PlayerWinner;
        else if( opponent.GetQueenCards().size()>4)
            return Winner.OpponentWinner;

        return Winner.NoWinner;
    }
}
