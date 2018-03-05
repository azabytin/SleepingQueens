package com.example.azabytin.SleepingQueens;

/**
 * Created by azabytin on 19.01.2018.
 */

class GameState
{
    protected GameLogic.userType userType;
    protected GameLogic gameLogic;

    public GameState( GameState state)
    {
        userType = state.userType;
        gameLogic = state.gameLogic;
    }

    public GameState( GameLogic.userType type, GameLogic logic )
    {
        userType = type;
        gameLogic = logic;
    }

    public boolean TurnEnded(){
        return false;
    }

    public GameState PlayCard(Card card)
    {
        return this;
    }


}
class GameStateIdle extends GameState
{
    public GameStateIdle( GameLogic.userType type, GameLogic logic )
    {
        super(type, logic);
    }

    public GameStateIdle( GameState state )
    {
        super(state);
    }

    public boolean TurnEnded(){
        return true;
    }

    public GameState PlayCard(Card card ) {

        if( card.isMagic() ){
            return new GameStateMagicAttacted( this );
        }

        if( card.isKnight() ){
            return new GameStateKnightAttacked( this );
        }
        return new GameStateWaitForСard( this );
    }
}

class GameStateWaitForСard extends GameState
{
    public GameStateWaitForСard( GameLogic.userType type, GameLogic logic )
    {
        super(type, logic);
    }

    public GameStateWaitForСard(GameState state )
    {
        super(state);
    }

    public GameState PlayCard(Card card) {

        if( gameLogic.playCardFromGameState( userType, card ) ){
            return new GameStateWaitForСard( this );
        }
        return new GameStateIdle( this );
    }
}

class GameStateMagicAttacted extends GameState
{
    public GameStateMagicAttacted( GameLogic.userType type, GameLogic logic )
    {
        super(type, logic);
    }

    public GameStateMagicAttacted( GameState state )
    {
        super(state);
    }

    public GameState PlayCard(Card card) {

        if (card.isStick()) {
            gameLogic.removeCard( userType, card);
            return new GameStateWaitForСard( this );
        }
        gameLogic.getBackQueen( userType );

        if( gameLogic.playCardFromGameState( userType, card ) ){
            return new GameStateWaitForСard( this );
        }
        return new GameStateIdle( this );
    }
}

class GameStateKnightAttacked extends GameState
{
    public GameStateKnightAttacked( GameLogic.userType type, GameLogic logic )
    {
        super(type, logic);
    }

    public GameStateKnightAttacked( GameState state )
    {
        super(state);
    }

    public GameState PlayCard(Card card) {

        if (card.isDragon()) {
            gameLogic.removeCard( userType, card);
            return new GameStateWaitForСard( this );
        }
        gameLogic.giveOponentQueen(userType);

        if (gameLogic.playCardFromGameState(userType, card)) {
            return new GameStateWaitForСard( this );
        }
        return new GameStateIdle( this );
    }
}