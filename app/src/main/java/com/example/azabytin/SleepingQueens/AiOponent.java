package com.example.azabytin.SleepingQueens;

import java.util.ArrayList;

/**
 * Created by azabytin on 21.03.2018.
 */

public class AiOponent {

    protected Player humanPlayer;
    protected Player computerPlayer;

    public AiOponent(Player computerPlayer_, Player humanPlayer_)
    {
        humanPlayer = humanPlayer_;
        computerPlayer = computerPlayer_;
    }


    public  void ChooseCardToPlay(GameState computerGameState,  ArrayList<Card> cardsToPlay )
    {
        Card opponentCard;
        opponentCard = ChooseOponentCardToPlay(computerGameState );
        cardsToPlay.add( opponentCard );
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
}
