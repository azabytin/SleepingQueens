package com.example.azabytin.SleepingQueens;

import java.util.Collections;
import java.util.List;

/**
 * Created by azabytin on 06.03.2018.
 */

public class PlayCardsStack  implements java.io.Serializable{

    protected List<Card> cards;
    protected transient CardCreater cardCreater;

    public PlayCardsStack( CardCreater с ){
        cardCreater = с;
        cards = cardCreater.createPlayCards();
    }

    public Card Get(){
        if(  cards.size() == 0 ){
            cards = cardCreater.createPlayCards();
            Collections.shuffle( cards );
        }
        Card card = cards.get(0);
        cards.remove( 0 );
        return card;
    }
    public void Add( Card card ){

    }

}
