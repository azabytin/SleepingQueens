package com.example.azabytin.SleepingQueens;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by azabytin on 16.01.2018.
 */

public class GameLogic {

    protected List<Card> playCardsStack;
    protected List<Card> queenCardsStack;

    protected UserCards userCards;
    protected List<Card> userQueenCards;
    protected UserCards oponentCards;
    protected List<Card> oponentQueenCards;
    Card lastCard;
    Card beforeLastCard;

    protected GameState userGameState;
    protected GameState oponentGameState;


    enum userType{
        USER
        {
            public userType other() {
                return OPPONENT;
            };
        },
        OPPONENT
                {
                    public userType other() {
                        return USER;
                    };
                };

        public userType other() {
            return null;
        }};

public void startNewGame()
{
    queenCardsStack = CardFactory.createQueenCards();
    Collections.shuffle( queenCardsStack );
    userQueenCards = new ArrayList<Card>();
    oponentQueenCards = new ArrayList<Card>();

    userCards = new UserCards();
    oponentCards = new UserCards();

    userCards.clear();
    oponentCards.clear();

    userCards.add( new Card( Card.cardType.magic, com.example.azabytin.SleepingQueens.R.drawable.magic, 0 ) );
    userCards.add( new Card( Card.cardType.jocker, com.example.azabytin.SleepingQueens.R.drawable.jocker, 0 ) );
    oponentCards.add( new Card( Card.cardType.knight, com.example.azabytin.SleepingQueens.R.drawable.knight, 0 ) );

    refillCardsFromStack( userType.USER );
    refillCardsFromStack( userType.OPPONENT);

    userGameState = GameState.WAIT_FOR_CARD;
    oponentGameState = GameState.IDLE;
}

    protected void refillCardsFromStack(userType type){

        if( playCardsStack == null || playCardsStack.size() == 0 ){
            playCardsStack = CardFactory.createPlayCards();
            Collections.shuffle( playCardsStack );
        }

        if( type == userType.USER ) {
            userCards.add(0,playCardsStack.get(0));
            playCardsStack.remove(0);
            if(userCards.size() < 5){
                refillCardsFromStack( type );
            }
        }
        else if ( type == userType.OPPONENT){
            oponentCards.add(0,playCardsStack.get(0));
            playCardsStack.remove(0);
            if(oponentCards.size() < 5){
                refillCardsFromStack( type );
            }
        }
    }
    protected void removeCard( userType type, Card card){

        if( type == userType.USER ) {
            userCards.remove(card);
        }
        else if ( type == userType.OPPONENT){
            oponentCards.remove(card);
        }

        beforeLastCard = lastCard;
        lastCard = card;
    }
    protected Card peekUserCard( userType type){

        if( type == userType.USER ) {
            return userCards.get(0);
        }
        else if ( type == userType.OPPONENT){
            return oponentCards.get(0);
        }
        return null;
    }

     protected void getQueen( userType type){

        if( type == userType.USER ) {
            userQueenCards.add(queenCardsStack.get(0));
            queenCardsStack.remove(0);
        }
        if ( type == userType.OPPONENT){
            oponentQueenCards.add(queenCardsStack.get(0));
            queenCardsStack.remove(0);
        }
    }

    protected void getBackQueen( userType type){

        if( type == userType.USER && userQueenCards.size()>0) {

            queenCardsStack.add(userQueenCards.get(0));
            userQueenCards.remove(0);
        }
        else if ( type == userType.OPPONENT && oponentQueenCards.size()>0){
            queenCardsStack.add(oponentQueenCards.get(0));
            oponentQueenCards.remove(0);
        }
    }

    protected void giveOponentQueen( userType type){

        if( type == userType.USER && userQueenCards.size()>0) {
            Log.d("GameLogic", "giveOponentQueen( USER )");
            oponentQueenCards.add(userQueenCards.get(0));
            userQueenCards.remove(0);
        }
        else if ( type == userType.OPPONENT && oponentQueenCards.size()>0){
            Log.d("GameLogic", "giveOponentQueen( OPPONENT )");
            userQueenCards.add(oponentQueenCards.get(0));
            oponentQueenCards.remove(0);
        }
    }

    public boolean playCardFromGameState(GameLogic.userType type, Card card )
    {
        removeCard( type, card);

        boolean hasPair = false;

        if( type == userType.USER ) {
            hasPair = userCards.hasThisNumber(card);
        }
        else if ( type == userType.OPPONENT){
            hasPair =  oponentCards.hasThisNumber(card);
        }

        if( !hasPair){
            refillCardsFromStack(type);
        }
        if( card.isKing()){
            getQueen( type );
        }


        if( card.isJocker()){
            Log.d("GameLogic", "playCardFromGameState() Jocker played");

            if(  peekUserCard( type ).isOddNumver() ){
                Log.d("GameLogic", "playCardFromGameState() Jocker played give player queen");
                getQueen( type );
            }
            if(  peekUserCard( type ).isEvenNumver() ){
                Log.d("GameLogic", "playCardFromGameState() Jocker played give opponent queen");
                getQueen( type.other() );
            }
        }

        return hasPair;
    }

    public void userPlayCard( Card card)
    {
        userGameState = userGameState.PlayCard( card, userType.USER, this );

        if( userGameState == GameState.IDLE ){
            ///
            if( (oponentGameState == GameState.WAIT_FOR_CARD) && card.isKing()) {
                oponentGameState = oponentGameState;
            }
            ///
            oponentGameState = oponentGameState.PlayCard( card, userType.OPPONENT, this );

            Card oponentPlayCard;
            do {
                oponentPlayCard = ChooseOponentCardToPlay( oponentGameState );
                oponentGameState = oponentGameState.PlayCard(oponentPlayCard, userType.OPPONENT, this );
            }while ( oponentGameState != GameState.IDLE );

            userGameState = userGameState.PlayCard( oponentPlayCard, userType.USER, this );
        }
    }

    protected Card ChooseOponentCardToPlay( GameState state)
    {
        if( (oponentGameState == GameState.KNIGHT_ATTACKED) && (oponentCards.GetDragon() != null) )
            return oponentCards.GetDragon();

        if( (oponentGameState == GameState.MAGIC_ATTACKED) && (oponentCards.GetStick() != null) )
            return oponentCards.GetStick();

        if( userQueenCards.size() > 0 ) {
            if (oponentCards.GetKnight() != null)
                return oponentCards.GetKnight();

            if (oponentCards.GetMagic() != null)
                return oponentCards.GetMagic();
        }

        if( oponentCards.GetKing() != null )
            return oponentCards.GetKing();

        if( oponentCards.GetNumber() != null )
            return oponentCards.GetNumber();

        return oponentCards.get( 0 );
    }

    public List<Card> getUserQueenCards() {
        return userQueenCards;
    }
    public List<Card> getOponentQueenCards() {
        return oponentQueenCards;
    }
    public List<Card> getUserCards() {
        return userCards;
    }
    public Card getLastCard() {  return lastCard; }
    public Card getBeforeLastCard() {  return beforeLastCard; }
    public int hasWinner()
    {
        if( userQueenCards.size()>4 )
            return 1;
        else if( oponentQueenCards.size()>4)
            return 2;

        return 0;
    }
}
