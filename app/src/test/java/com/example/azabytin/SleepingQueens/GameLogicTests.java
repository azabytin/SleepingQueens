package com.example.azabytin.SleepingQueens;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import android.content.SharedPreferences;

import static org.junit.Assert.*;


public class GameLogicTests extends Assert {
    private GameLogic gameLogic = new GameLogic();
    private PlayCardCreater playCardCreater = new PlayCardCreater();


    public GameLogicTests() {
        gameLogic.startNewGame();
    }

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
        assertEquals(5, gameLogic.getPlayerCards().size());
        assertEquals( 5, gameLogic.getOpponentCards().size());
    }
    @Test
    public void testOpponentCantPlayFirst() {
        boolean res =  gameLogic.oponentPlayCards( playCardCreater.CreateKingCard() );
        assertFalse( res );
    }
    @Test
    public void testPlayerCantPlayFirst() {
        boolean res =  gameLogic.userPlayCards( playCardCreater.CreateNumberCard() );
        assertTrue( res );
    }
    @Test
    public void testPlayerCantPlayTwice() {
        gameLogic.userPlayCards( playCardCreater.CreateNumberCard() );
        boolean res =  gameLogic.userPlayCards( playCardCreater.CreateNumberCard() );
        assertFalse( res );
    }
    @Test
    public void testOpponentGetQueenWhenKingPlayed() {
        gameLogic.userPlayCards( playCardCreater.CreateNumberCard() );
        boolean res = gameLogic.oponentPlayCards( playCardCreater.CreateKingCard() );
        assertTrue( res );
        assertEquals(1,  gameLogic.getOpponentQueenCards().size() );
    }

    @Test
    public void testPlayerGetQueenWhenKingPlayed() {
        boolean res = gameLogic.userPlayCards( playCardCreater.CreateKingCard() );
        assertTrue( res );
        assertEquals( 1, gameLogic.getPlayerQueenCards().size() );
    }
}



/*
@RunWith(MockitoJUnitRunner.class)
public class UnitTestSample {

    private static final String FAKE_STRING = "HELLO WORLD";

    @Mock
    Context mMockContext;

    @Test
    public void readStringFromContext_LocalizedString() {
        // Given a mocked Context injected into the object under test...
        when(mMockContext.getString(R.string.hello_word))
                .thenReturn(FAKE_STRING);
        ClassUnderTest myObjectUnderTest = new ClassUnderTest(mMockContext);

        // ...when the string is returned from the object under test...
        String result = myObjectUnderTest.getHelloWorldString();

        // ...then the result should be the expected one.
        assertThat(result, is(FAKE_STRING));
    }
}
*/