package com.example.azabytin.SleepingQueens;

import java.util.List;

/**
 * Created by azabytin on 23.01.2018.
 */

public class Utils {
    static public void MoveFirstElementToAnotherStorage(List<Card> wherePut, List<Card> fromPut )
    {
        wherePut.add( fromPut.get( 0 ) );
        fromPut.remove( 0 );
    }
}
