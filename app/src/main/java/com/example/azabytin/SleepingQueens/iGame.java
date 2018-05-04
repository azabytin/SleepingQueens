package com.example.azabytin.SleepingQueens;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by azabytin on 20.03.2018.
 */

public interface iGame {
    enum Winner
    {
        PlayerWinner,
        OpponentWinner,
        NoWinner
    }

    void startNewGame();
    List<Card> getPlayerQueenCards();
    List<Card> getOpponentQueenCards();
    List<Card> getPlayerCards();
    List<Card> getOpponentCards();
    Card getLastCard();
    Card getBeforeLastCard();
    Winner whoIsWinner();
    boolean userPlayCards(ArrayList<Card> cardsToPlay);
    boolean oponentPlayCards(ArrayList<Card> cardsToPlay);
    boolean canOponentPlay();
    boolean canUserPlay();
}
