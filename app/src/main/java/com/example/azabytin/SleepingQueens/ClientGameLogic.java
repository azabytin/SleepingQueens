package com.example.azabytin.SleepingQueens;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by azabytin on 04.04.2018.
 */

public class ClientGameLogic implements iGameLogic {

    protected iGameLogic serverLogic;

    public ClientGameLogic( iGameLogic _serverLogic){
        serverLogic = _serverLogic;
    }

    public void startNewGame(){}

    public List<Card> getHumanQueenCards(){
        return serverLogic.getComputerQueenCards();
    }
    public List<Card> getComputerQueenCards(){
        return serverLogic.getHumanQueenCards();
    }
    public List<Card> getHumanCards(){
        return serverLogic.getComputerCards();
    }
    public List<Card> getComputerCards(){
        return serverLogic.getHumanCards();
    }
    public Card getLastCard(){
        return serverLogic.getLastCard();
    }
    public Card getBeforeLastCard(){
        return serverLogic.getBeforeLastCard();
    }
    public int hasWinner(){
        return serverLogic.hasWinner();
    }
    public boolean userPlayCard(ArrayList<Card> cardsToPlay){
        return serverLogic.oponentPlayCard( cardsToPlay );
    }
    public boolean oponentPlayCard(ArrayList<Card> cardsToPlay){
        return false;
    }
    public boolean canOponentPlay(){
        return false;
    }
    public boolean canUserPlay(){
        return serverLogic.canOponentPlay();
    }
}
