package com.example.azabytin.SleepingQueens;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by azabytin on 20.03.2018.
 */

public class iGame {
    enum Winner
    {
        PlayerWinner,
        OpponentWinner,
        NoWinner
    }

    public void startNewGame(){}

    public List<Card> getPlayerQueenCards(){return new ArrayList<>();}

    public List<Card> getOpponentQueenCards(){return new ArrayList<>();}

    public List<Card> getPlayerCards(){return new ArrayList<>();}

    public List<Card> getOpponentCards(){return new ArrayList<>();}

    public Card getLastCard(){return null;}

    public Card getBeforeLastCard(){return null;}

    public Winner whoIsWinner(){return Winner.NoWinner;}

    public boolean userPlayCards(ArrayList<Card> cardsToPlay){return false;}

    public boolean oponentPlayCards(ArrayList<Card> cardsToPlay){return false;}

    public boolean oponentPlayCards(){return oponentPlayCards(new ArrayList<>());}

    public boolean canOponentPlay(){return false;}

    public boolean canUserPlay(){return false;}
}
