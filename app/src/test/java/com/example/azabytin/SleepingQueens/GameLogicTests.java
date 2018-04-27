package com.example.azabytin.SleepingQueens;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;


public class GameLogicTests extends Assert {

    class GameLogicMocked extends GameLogic
    {
        protected void refillCardsFromStack(){
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
    }


    private GameLogicMocked gameLogic = new GameLogicMocked();
    private PlayCardCreater playCardCreater = new PlayCardCreater();

    @Before
    public void InitTest(){
        gameLogic.startNewGame();
    }
    @After
    public void clean() {
    }

    @Test
    public void testPlayersDontHaveQueensAtTheBeginning() {
        assertEquals( 0, gameLogic.getPlayerQueenCards().size());
        assertEquals( 0, gameLogic.getOpponentQueenCards().size());
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
        gameLogic.fillOpponentCards(new Card.cardType[]{Card.cardType.king} );
        boolean res =  gameLogic.opponentPlayCard( Card.cardType.king);
        assertFalse( res );
    }
    @Test
    public void testPlayerCantPlayFirst() {
        gameLogic.fillUserCards(new Card.cardType[]{Card.cardType.number} );
        boolean res =  gameLogic.userPlayCard( Card.cardType.number );
        assertTrue( res );
    }
    @Test
    public void testPlayerCantPlayTwice() {
        gameLogic.fillUserCards( Card.cardType.number );
        gameLogic.fillUserCards( Card.cardType.number );

        gameLogic.userPlayCard( Card.cardType.number );
        boolean res =  gameLogic.userPlayCard( Card.cardType.number );
        assertFalse( res );
    }
    @Test
    public void testOpponentGetQueenWhenKingPlayed() {
        gameLogic.fillUserCards(new Card.cardType[]{Card.cardType.number} );
        gameLogic.userPlayCard( Card.cardType.number );

        gameLogic.fillOpponentCards(new Card.cardType[]{Card.cardType.king} );
        boolean res = gameLogic.opponentPlayCard( Card.cardType.king);
        assertTrue( res );
        assertEquals(1,  gameLogic.getOpponentQueenCards().size() );
    }

    @Test
    public void testPlayerGetQueenWhenKingPlayed() {
        gameLogic.fillUserCards(new Card.cardType[]{Card.cardType.king} );
        boolean res = gameLogic.userPlayCard( Card.cardType.king );
        assertTrue( res );
        assertEquals( 1, gameLogic.getPlayerQueenCards().size() );
    }

    @Test
    public void testSuccessfulAttackWithDragon() {
        gameLogic.fillUserCards( Card.cardType.king );
        gameLogic.fillUserCards( Card.cardType.number );
        gameLogic.fillOpponentCards( Card.cardType.dragon );

        gameLogic.userPlayCard( Card.cardType.king );
        gameLogic.opponentPlayCard( Card.cardType.dragon);
        gameLogic.userPlayCard( Card.cardType.number );

        assertEquals( 0, gameLogic.getPlayerQueenCards().size() );
        assertEquals( 1, gameLogic.getOpponentQueenCards().size() );
    }
    @Test
    public void testNotSuccessfulAttackWithDragon() {
        gameLogic.fillUserCards( Card.cardType.number );
        gameLogic.fillUserCards( Card.cardType.dragon);

        gameLogic.fillOpponentCards( Card.cardType.king);
        gameLogic.fillOpponentCards( Card.cardType.knight);

        gameLogic.userPlayCard( Card.cardType.number );
        gameLogic.opponentPlayCard( Card.cardType.king);
        gameLogic.userPlayCard( Card.cardType.dragon );
        gameLogic.oponentPlayCards( new ArrayList<Card>());

        assertEquals( 0, gameLogic.getPlayerQueenCards().size() );
        assertEquals( 1, gameLogic.getOpponentQueenCards().size() );
    }
    @Test
    public void testSuccessfulAttackWithMagic() {
        gameLogic.fillUserCards( Card.cardType.number );
        gameLogic.fillUserCards( Card.cardType.magic);

        gameLogic.fillOpponentCards( Card.cardType.king);
        gameLogic.fillOpponentCards( Card.cardType.number);

        gameLogic.userPlayCard( Card.cardType.number );
        gameLogic.opponentPlayCard( Card.cardType.king);
        gameLogic.userPlayCard( Card.cardType.magic );
        gameLogic.oponentPlayCards( new ArrayList<Card>());

        assertEquals( 0, gameLogic.getPlayerQueenCards().size() );
        assertEquals( 0, gameLogic.getOpponentQueenCards().size() );
    }
    @Test
    public void testNotSuccessfulAttackWithMagic() {
        gameLogic.fillUserCards( Card.cardType.number );
        gameLogic.fillUserCards( Card.cardType.magic);

        gameLogic.fillOpponentCards( Card.cardType.king);
        gameLogic.fillOpponentCards( Card.cardType.stick);

        gameLogic.userPlayCard( Card.cardType.number );
        gameLogic.opponentPlayCard( Card.cardType.king);
        gameLogic.userPlayCard( Card.cardType.magic );
        gameLogic.oponentPlayCards( new ArrayList<Card>());

        assertEquals( 0, gameLogic.getPlayerQueenCards().size() );
        assertEquals( 1, gameLogic.getOpponentQueenCards().size() );
    }

}

