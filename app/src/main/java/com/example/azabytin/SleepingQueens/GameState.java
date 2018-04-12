package com.example.azabytin.SleepingQueens;

/**
 * Created by azabytin on 19.01.2018.
 */

class GameState implements java.io.Serializable
{
    protected Player player;
    protected GameLogic gameLogic;

    public GameState( GameState state)
    {
        player = state.player;
        gameLogic = state.gameLogic;
    }

    public GameState( Player p, GameLogic logic )
    {
        player = p;
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
    public GameStateIdle( Player player, GameLogic logic )
    {
        super(player, logic);
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
    public GameStateWaitForСard( Player player, GameLogic logic )
    {
        super(player, logic);
    }

    public GameStateWaitForСard(GameState state )
    {
        super(state);
    }

    public GameState PlayCard(Card card) {

        gameLogic.playCardFromGameState( player, card );
        return new GameStateWaitForСard( this );

    }
}

class GameStateMagicAttacted extends GameState
{
    public GameStateMagicAttacted( Player player, GameLogic logic )
    {
        super(player, logic);
    }

    public GameStateMagicAttacted( GameState state )
    {
        super(state);
    }

    public GameState PlayCard(Card card) {

        if (card.isStick()) {
            gameLogic.removeCardFromPlayer( card, player);
            return new GameStateWaitForСard( this );
        }
        gameLogic.OnGetBackQueen( player );

        gameLogic.playCardFromGameState( player, card );
        return new GameStateWaitForСard( this );
    }
}

class GameStateKnightAttacked extends GameState
{
    public GameStateKnightAttacked( Player player, GameLogic logic )
    {
        super(player, logic);
    }

    public GameStateKnightAttacked( GameState state )
    {
        super(state);
    }

    public GameState PlayCard(Card card) {

        if (card.isDragon()) {
            gameLogic.removeCardFromPlayer( card, player);
            return new GameStateWaitForСard( this );
        }
        gameLogic.OnGiveOponentQueen(player);

        gameLogic.playCardFromGameState(player, card);
        return new GameStateWaitForСard( this );
    }
}