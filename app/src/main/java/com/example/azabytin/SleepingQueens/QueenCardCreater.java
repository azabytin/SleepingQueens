package com.example.azabytin.SleepingQueens;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by azabytin on 06.03.2018.
 */

public class QueenCardCreater implements CardCreater {
    public List<Card> createPlayCards()
    {
        List<Card> cards= new ArrayList<>();

        cards.add( new Card( Card.cardType.queen, com.example.azabytin.SleepingQueens.R.drawable.queen1, 10 ) );
        cards.add( new Card( Card.cardType.queen, com.example.azabytin.SleepingQueens.R.drawable.queen2, 5 ) );
        cards.add( new Card( Card.cardType.queen, com.example.azabytin.SleepingQueens.R.drawable.queen3, 15 ) );
        Card card = new Card( Card.cardType.queen, com.example.azabytin.SleepingQueens.R.drawable.queen4, 5 );
        card.setRoseQueen();
        cards.add( card );
        cards.add( new Card( Card.cardType.queen, com.example.azabytin.SleepingQueens.R.drawable.queen5, 10 ) );
        cards.add( new Card( Card.cardType.queen, com.example.azabytin.SleepingQueens.R.drawable.queen6, 5 ) );
        cards.add( new Card( Card.cardType.queen, com.example.azabytin.SleepingQueens.R.drawable.queen7, 5 ) );
        cards.add( new Card( Card.cardType.queen, com.example.azabytin.SleepingQueens.R.drawable.queen8, 10 ) );
        cards.add( new Card( Card.cardType.queen, com.example.azabytin.SleepingQueens.R.drawable.queen9, 10 ) );
        cards.add( new Card( Card.cardType.queen, com.example.azabytin.SleepingQueens.R.drawable.queen11, 20 ) );

        card = new Card( Card.cardType.queen, com.example.azabytin.SleepingQueens.R.drawable.queen10, 15 );
        card.setDogCatQueen();
        cards.add( card );
        card = new Card( Card.cardType.queen, com.example.azabytin.SleepingQueens.R.drawable.queen12, 15 );
        card.setDogCatQueen();
        cards.add( card );

        return cards;
    }
}