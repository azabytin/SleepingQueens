package com.example.azabytin.SleepingQueens;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;


public class GameLogicTests extends Assert {

    class GameLogicMocked extends GameLogic
    {
        private boolean gameLogicCardsRefillPolicy;
        GameLogicMocked(){
            gameLogicCardsRefillPolicy = false;
        }
        GameLogicMocked( boolean _gameLogicCardsRefillPolicy){
            gameLogicCardsRefillPolicy = _gameLogicCardsRefillPolicy;
        }
        protected void refillCardsFromStack(){
            if( gameLogicCardsRefillPolicy ){
                super.refillCardsFromStack();
            }
        }

        public void fillOpponentCards(Card.cardType[] cardTypes){
            for (Card.cardType cardType: cardTypes) {
                opponent.AddCard( new Card(cardType));
            }
        }
        public void fillUserCards(Card.cardType[] cardTypes){
            for (Card.cardType cardType: cardTypes) {
                player.AddCard( new Card(cardType));
            }
        }
        public void fillOpponentCards(Card.cardType cardType){
            opponent.AddCard( new Card(cardType));
        }
        public void fillUserCards(Card.cardType cardType){
            player.AddCard( new Card(cardType));
        }
        public boolean userPlayCard( Card.cardType cardType ){
            ArrayList<Card> res =  new ArrayList<Card>();
            res.add(new Card( cardType));
            return this.userPlayCards( res );
        }
        public boolean opponentPlayCard( Card.cardType cardType ){
            ArrayList<Card> res =  new ArrayList<Card>();
            res.add(new Card( cardType));
            return this.oponentPlayCards( res );
        }
        public boolean userPlayCard( Card card ){
            ArrayList<Card> res =  new ArrayList<Card>();
            res.add(card);
            return this.userPlayCards( res );
        }
        public boolean opponentPlayCard( Card card ){
            ArrayList<Card> res =  new ArrayList<Card>();
            res.add(card);            return this.oponentPlayCards( res );
        }
        public Card  getAnyUserCard( ){
            return getPlayerCards().get(0);
        }
        public Card  getAnyOpponentCard( ){
            return getOpponentCards().get(0);
        }

    }


    private GameLogicMocked mockedGameLogic = new GameLogicMocked();
    private GameLogicMocked gameLogic = new GameLogicMocked();
    private PlayCardCreater playCardCreater = new PlayCardCreater();

    @Before
    public void InitTest(){
        mockedGameLogic.startNewGame();
        gameLogic.startNewGame();
    }
    @After
    public void clean() {
    }

    @Test
    public void testPlayersDontHaveQueensAtTheBeginning() {
        assertEquals( 0, mockedGameLogic.getPlayerQueenCards().size());
        assertEquals( 0, mockedGameLogic.getOpponentQueenCards().size());
    }
    @Test
    public void testPlayersHave5CardsAtTheBeginning() {
        GameLogic gameLogic = new GameLogic();
        gameLogic.startNewGame();
        assertEquals(5, gameLogic.getPlayerCards().size());
        assertEquals( 5, gameLogic.getOpponentCards().size());
    }
    @Test
    public void testOpponentCantPlayFirst() {
        mockedGameLogic.fillOpponentCards(new Card.cardType[]{Card.cardType.king} );
        boolean res =  mockedGameLogic.opponentPlayCard( Card.cardType.king);
        assertFalse( res );
    }
    @Test
    public void testPlayerCantPlayFirst() {
        mockedGameLogic.fillUserCards(new Card.cardType[]{Card.cardType.number} );
        boolean res =  mockedGameLogic.userPlayCard( Card.cardType.number );
        assertTrue( res );
    }
    @Test
    public void testPlayerCantPlayTwice() {
        mockedGameLogic.fillUserCards( Card.cardType.number );
        mockedGameLogic.fillUserCards( Card.cardType.number );

        mockedGameLogic.userPlayCard( Card.cardType.number );
        boolean res =  mockedGameLogic.userPlayCard( Card.cardType.number );
        assertFalse( res );
    }
    @Test
    public void testPlayersAlwaysHave5Cards() {
        GameLogicMocked gl = new GameLogicMocked(true);
        gl.startNewGame();
        gl.userPlayCard( gl.getAnyUserCard() );
        assertEquals(5,  gl.getOpponentCards().size() );
        assertEquals(5,  gl.getPlayerCards().size() );
    }
    @Test
    public void testOpponentGetQueenWhenKingPlayed() {
        mockedGameLogic.fillUserCards(new Card.cardType[]{Card.cardType.number} );
        mockedGameLogic.userPlayCard( Card.cardType.number );

        mockedGameLogic.fillOpponentCards(new Card.cardType[]{Card.cardType.king} );
        boolean res = mockedGameLogic.opponentPlayCard( Card.cardType.king);
        assertTrue( res );

        int expectedValue = 1;
        if(mockedGameLogic.getOpponentQueenCards().size() > 1){
            for( Card card : mockedGameLogic.getOpponentQueenCards()){
                if( card.isRoseQueen()){
                    expectedValue = 2;
                }
            }
        }

        assertEquals(expectedValue,  mockedGameLogic.getOpponentQueenCards().size() );
    }

    @Test
    public void testPlayerGetQueenWhenKingPlayed() {
        mockedGameLogic.fillUserCards(Card.cardType.king );
        boolean res = mockedGameLogic.userPlayCard( Card.cardType.king );
        assertTrue( res );

        int expectedValue = 1;
        if(mockedGameLogic.getPlayerQueenCards().size() > 1){
            for( Card card : mockedGameLogic.getOpponentQueenCards()){
                if( card.isRoseQueen()){
                    expectedValue = 2;
                }
            }
        }

        assertEquals( expectedValue, mockedGameLogic.getPlayerQueenCards().size() );
    }

    @Test
    public void testSuccessfulAttackWithDragon() {
        mockedGameLogic.fillUserCards( Card.cardType.king );
        mockedGameLogic.fillUserCards( Card.cardType.number );
        mockedGameLogic.fillOpponentCards( Card.cardType.knight );

        mockedGameLogic.userPlayCard( Card.cardType.king );
        mockedGameLogic.opponentPlayCard( Card.cardType.knight);

        int latestQueensNumber = mockedGameLogic.getPlayerQueenCards().size();
        mockedGameLogic.userPlayCard( Card.cardType.number );

        assertEquals( latestQueensNumber-1, mockedGameLogic.getPlayerQueenCards().size() );
        assertEquals( 1, mockedGameLogic.getOpponentQueenCards().size() );
    }
    @Test
    public void testNotSuccessfulAttackWithDragon() {
        mockedGameLogic.fillUserCards( Card.cardType.number );
        mockedGameLogic.fillUserCards( Card.cardType.knight);

        mockedGameLogic.fillOpponentCards( Card.cardType.king);
        mockedGameLogic.fillOpponentCards( Card.cardType.dragon);

        mockedGameLogic.userPlayCard( Card.cardType.number );
        mockedGameLogic.opponentPlayCard( Card.cardType.king);
        mockedGameLogic.userPlayCard( Card.cardType.knight );

        int latestQueensNumber = mockedGameLogic.getOpponentQueenCards().size();

        mockedGameLogic.oponentPlayCards( new ArrayList<Card>());

        assertEquals( 0, mockedGameLogic.getPlayerQueenCards().size() );
        assertEquals( latestQueensNumber, mockedGameLogic.getOpponentQueenCards().size() );

        assertTrue(mockedGameLogic.canOponentPlay());
        assertFalse(mockedGameLogic.canUserPlay());
    }
    @Test
    public void testSuccessfulAttackWithMagic() {
        mockedGameLogic.fillUserCards( Card.cardType.number );
        mockedGameLogic.fillUserCards( Card.cardType.magic);

        mockedGameLogic.fillOpponentCards( Card.cardType.king);
        mockedGameLogic.fillOpponentCards( Card.cardType.number);

        mockedGameLogic.userPlayCard( Card.cardType.number );
        mockedGameLogic.opponentPlayCard( Card.cardType.king);
        mockedGameLogic.userPlayCard( Card.cardType.magic );

        int latestQueensNumber = mockedGameLogic.getOpponentQueenCards().size();
        mockedGameLogic.oponentPlayCards( new ArrayList<Card>());

        assertEquals( latestQueensNumber-1, mockedGameLogic.getOpponentQueenCards().size() );
        assertEquals( 0, mockedGameLogic.getPlayerQueenCards().size() );
    }
    @Test
    public void testNotSuccessfulAttackWithMagic() {
        mockedGameLogic.fillUserCards( Card.cardType.number );
        mockedGameLogic.fillUserCards( Card.cardType.magic);

        mockedGameLogic.fillOpponentCards( Card.cardType.king);
        mockedGameLogic.fillOpponentCards( Card.cardType.stick);

        mockedGameLogic.userPlayCard( Card.cardType.number );
        mockedGameLogic.opponentPlayCard( Card.cardType.king);
        mockedGameLogic.userPlayCard( Card.cardType.magic );

        int latestQueensNumber = mockedGameLogic.getOpponentQueenCards().size();

        mockedGameLogic.oponentPlayCards( new ArrayList<Card>());

        assertEquals( 0, mockedGameLogic.getPlayerQueenCards().size() );
        assertEquals( latestQueensNumber, mockedGameLogic.getOpponentQueenCards().size() );
        assertTrue(mockedGameLogic.canOponentPlay());
        assertFalse(mockedGameLogic.canUserPlay());
    }
    @Test
    public void test5QueensWinner() {
        mockedGameLogic.fillUserCards( Card.cardType.king );
        mockedGameLogic.fillUserCards( Card.cardType.king );
        mockedGameLogic.fillUserCards( Card.cardType.king );
        mockedGameLogic.fillUserCards( Card.cardType.king );
        mockedGameLogic.fillUserCards( Card.cardType.king );
        mockedGameLogic.fillUserCards( Card.cardType.king );

        mockedGameLogic.fillOpponentCards( Card.cardType.number);
        mockedGameLogic.fillOpponentCards( Card.cardType.number);
        mockedGameLogic.fillOpponentCards( Card.cardType.number);
        mockedGameLogic.fillOpponentCards( Card.cardType.number);

        mockedGameLogic.userPlayCard( Card.cardType.king );
        mockedGameLogic.opponentPlayCard( Card.cardType.number);
        mockedGameLogic.userPlayCard( Card.cardType.king );
        mockedGameLogic.opponentPlayCard( Card.cardType.number);
        mockedGameLogic.userPlayCard( Card.cardType.king );
        mockedGameLogic.opponentPlayCard( Card.cardType.number);
        mockedGameLogic.userPlayCard( Card.cardType.king );
        mockedGameLogic.opponentPlayCard( Card.cardType.number);
        mockedGameLogic.userPlayCard( Card.cardType.king );
        mockedGameLogic.opponentPlayCard( Card.cardType.number);

        assertEquals( iGame.Winner.PlayerWinner, mockedGameLogic.whoIsWinner() );
    }


}

