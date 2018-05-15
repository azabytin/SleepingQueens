package com.example.azabytin.SleepingQueens;

import java.util.ArrayList;

/**
 * Created by azabytin on 31.01.2018.
 */

public class UserCards extends ArrayList<Card> implements java.io.Serializable{

    public Card GetKnight()
    {
        for (Card card : this){
            if( card.isKnight()  )
                return card;
        }
        return null;
    }

    public Card GetNumber()
    {
        for (Card card : this){
            if( card.isNumver()  )
                return card;
        }
        return null;
    }

    public Card GetKing()
    {
        for (Card card : this){
            if( card.isKing()  )
                return card;
        }
        return null;
    }
    public Card GetMagic()
    {
        for (Card card : this){
            if( card.isMagic()  )
                return card;
        }
        return null;
    }
    public Card GetDragon()
    {
        for (Card card : this){
            if( card.isDragon()  )
                return card;
        }
        return null;
    }
    public Card GetStick()
    {
        for (Card card : this){
            if( card.isStick()  )
                return card;
        }
        return null;
    }


}
