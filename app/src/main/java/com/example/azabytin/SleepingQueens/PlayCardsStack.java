package com.example.azabytin.SleepingQueens;

import java.util.Collections;
import java.util.List;

/**
 * Created by azabytin on 06.03.2018.
 */

class PlayCardsStack  implements java.io.Serializable{

    private List<Card> cards;
    private final transient CardCreater cardCreater;

    public PlayCardsStack( CardCreater cardCreater ){
        this.cardCreater = cardCreater;
    }

    public Card Get(){
        if( cards==null || cards.size() == 0 ){
            cards = cardCreater.createPlayCards();
            Collections.shuffle( cards );
            Collections.shuffle( cards );
            Collections.shuffle( cards );
        }
        Card card = cards.get(0);
        cards.remove( 0 );
        return card;
    }
    public void Add( Card card ){
        cards.add(card);
        Collections.shuffle( cards );
    }

}
