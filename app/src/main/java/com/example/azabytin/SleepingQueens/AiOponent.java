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

    protected void ChooseOponentCardToPlay( Card.cardType enemyLastCard,  ArrayList<Card> cardsToPlay )
    {
        if(enemyLastCard == Card.cardType.magic && computerPlayer.GetCards().GetStick() != null){
            cardsToPlay.add(computerPlayer.GetCards().GetStick());
            return;
        }

        if(enemyLastCard == Card.cardType.knight && computerPlayer.GetCards().GetDragon() != null){
            cardsToPlay.add(computerPlayer.GetCards().GetDragon());
            return;
        }

        if( humanPlayer.GetQueenCards().size() > 0 ) {
            if (computerPlayer.GetCards().GetKnight() != null) {
                cardsToPlay.add(computerPlayer.GetCards().GetKnight());
                return;
            }

            if (computerPlayer.GetCards().GetMagic() != null) {
                cardsToPlay.add(computerPlayer.GetCards().GetMagic());
                return;
            }
        }

        if( computerPlayer.GetCards().GetKing() != null ) {
            cardsToPlay.add(computerPlayer.GetCards().GetKing());
            return;
        }

        if( computerPlayer.GetCards().GetNumber() != null ) {
            cardsToPlay.add(computerPlayer.GetCards().GetNumber());
            return;
        }

        cardsToPlay.add( computerPlayer.GetCards().get( 0 ) );
    }
}
