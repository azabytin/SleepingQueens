package com.example.azabytin.SleepingQueens;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by azabytin on 16.01.2018.
 */

public class PlayCardCreater implements CardCreater {

    public ArrayList<Card> CreateKingCard(){
        ArrayList<Card> res =  new ArrayList<Card>();
        res.add(new Card( Card.cardType.king, com.example.azabytin.SleepingQueens.R.drawable.king1 ));
        return res;
    }
    public ArrayList<Card> CreateNumberCard(){
        ArrayList<Card> res =  new ArrayList<Card>();
        res.add(new Card( Card.cardType.number, com.example.azabytin.SleepingQueens.R.drawable.card1, 1 ));
        return res;
    }
    public List<Card> createPlayCards()
    {
        List<Card> cards= new ArrayList<Card>();

        cards.add( new Card( Card.cardType.number, com.example.azabytin.SleepingQueens.R.drawable.card1, 1 ) );
        cards.add( new Card( Card.cardType.number, com.example.azabytin.SleepingQueens.R.drawable.card2, 2 ) );
        cards.add( new Card( Card.cardType.number, com.example.azabytin.SleepingQueens.R.drawable.card3, 3 ) );
        cards.add( new Card( Card.cardType.number, com.example.azabytin.SleepingQueens.R.drawable.card4, 4 ) );
        cards.add( new Card( Card.cardType.number, com.example.azabytin.SleepingQueens.R.drawable.card5, 5 ) );
        cards.add( new Card( Card.cardType.number, com.example.azabytin.SleepingQueens.R.drawable.card6, 6 ) );
        cards.add( new Card( Card.cardType.number, com.example.azabytin.SleepingQueens.R.drawable.card7, 7 ) );
        cards.add( new Card( Card.cardType.number, com.example.azabytin.SleepingQueens.R.drawable.card8, 8 ) );
        cards.add( new Card( Card.cardType.number, com.example.azabytin.SleepingQueens.R.drawable.card9, 9 ) );
        cards.add( new Card( Card.cardType.number, com.example.azabytin.SleepingQueens.R.drawable.card10, 10 ) );

        cards.add( new Card( Card.cardType.king, com.example.azabytin.SleepingQueens.R.drawable.king1 ) );
        cards.add( new Card( Card.cardType.king, com.example.azabytin.SleepingQueens.R.drawable.king2 ) );
        cards.add( new Card( Card.cardType.king, com.example.azabytin.SleepingQueens.R.drawable.king3 ) );
        cards.add( new Card( Card.cardType.king, com.example.azabytin.SleepingQueens.R.drawable.king4 ) );
        cards.add( new Card( Card.cardType.king, com.example.azabytin.SleepingQueens.R.drawable.king5 ) );
        cards.add( new Card( Card.cardType.king, com.example.azabytin.SleepingQueens.R.drawable.king6 ) );
        cards.add( new Card( Card.cardType.king, com.example.azabytin.SleepingQueens.R.drawable.king7 ) );
        cards.add( new Card( Card.cardType.king, com.example.azabytin.SleepingQueens.R.drawable.king8 ) );

        cards.add( new Card( Card.cardType.knight, com.example.azabytin.SleepingQueens.R.drawable.knight ) );
        cards.add( new Card( Card.cardType.stick, com.example.azabytin.SleepingQueens.R.drawable.stick ) );
        cards.add( new Card( Card.cardType.dragon, com.example.azabytin.SleepingQueens.R.drawable.dragon ) );
        cards.add( new Card( Card.cardType.jocker, com.example.azabytin.SleepingQueens.R.drawable.jocker ) );
        cards.add( new Card( Card.cardType.magic, com.example.azabytin.SleepingQueens.R.drawable.magic ) );

        return cards;
    }

}

