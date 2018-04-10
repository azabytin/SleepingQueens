package com.example.azabytin.SleepingQueens;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by azabytin on 20.03.2018.
 */

public interface iGameLogic {
    public void startNewGame();
    public List<Card> getHumanQueenCards();
    public List<Card> getComputerQueenCards();
    public Card[] getComputerCardsArray();
    public List<Card> getHumanCards();
    public List<Card> getComputerCards();
    public Card getLastCard();
    public Card getBeforeLastCard();
    public int hasWinner();
    public boolean userPlayCard(ArrayList<Card> cardsToPlay);
    public boolean oponentPlayCard(ArrayList<Card> cardsToPlay);
    public boolean canOponentPlay();
    public boolean canUserPlay();
}
