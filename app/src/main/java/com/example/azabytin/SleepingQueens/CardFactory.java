package com.example.azabytin.SleepingQueens;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by azabytin on 16.01.2018.
 */

public class CardFactory {

    static public List<Card> createPlayCards()
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

    static public List<Card> createQueenCards()
    {
        List<Card> cards= new ArrayList<Card>();

        cards.add( new Card( Card.cardType.queen, com.example.azabytin.SleepingQueens.R.drawable.queen1, 10 ) );
        cards.add( new Card( Card.cardType.queen, com.example.azabytin.SleepingQueens.R.drawable.queen2, 5 ) );
        cards.add( new Card( Card.cardType.queen, com.example.azabytin.SleepingQueens.R.drawable.queen3, 15 ) );
        cards.add( new Card( Card.cardType.queen, com.example.azabytin.SleepingQueens.R.drawable.queen4, 5 ) );
        cards.add( new Card( Card.cardType.queen, com.example.azabytin.SleepingQueens.R.drawable.queen5, 10 ) );
        cards.add( new Card( Card.cardType.queen, com.example.azabytin.SleepingQueens.R.drawable.queen6, 5 ) );
        cards.add( new Card( Card.cardType.queen, com.example.azabytin.SleepingQueens.R.drawable.queen7, 5 ) );
        cards.add( new Card( Card.cardType.queen, com.example.azabytin.SleepingQueens.R.drawable.queen8, 10 ) );
        cards.add( new Card( Card.cardType.queen, com.example.azabytin.SleepingQueens.R.drawable.queen9, 10 ) );
        cards.add( new Card( Card.cardType.queen, com.example.azabytin.SleepingQueens.R.drawable.queen10, 15 ) );
        cards.add( new Card( Card.cardType.queen, com.example.azabytin.SleepingQueens.R.drawable.queen11, 20 ) );
        cards.add( new Card( Card.cardType.queen, com.example.azabytin.SleepingQueens.R.drawable.queen12, 15 ) );

        return cards;
    }

}
