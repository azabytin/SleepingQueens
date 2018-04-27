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

    public void startNewGame();
    public List<Card> getPlayerQueenCards();
    public List<Card> getOpponentQueenCards();
    public List<Card> getPlayerCards();
    public List<Card> getOpponentCards();
    public Card getLastCard();
    public Card getBeforeLastCard();
    public Winner whoIsWinner();
    public boolean userPlayCards(ArrayList<Card> cardsToPlay);
    public boolean oponentPlayCards(ArrayList<Card> cardsToPlay);
    public boolean canOponentPlay();
    public boolean canUserPlay();
}
