package com.example.azabytin.SleepingQueens;

import android.os.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by azabytin on 04.04.2018.
 */

public class ClientGameLogic implements iGameLogic {

    protected List<Card> ComputerQueenCards;
    protected List<Card> HumanQueenCards;
    protected List<Card> HumanCards;
    protected List<Card> ComputerCards;
    Card LastCard;
    Card BeforeLastCard;
    int hasWinner;
    boolean canUserPlay;
    iGameLogic serverLogic;

    BlockingQueue<Message> messaQequeue;


    public ClientGameLogic(BlockingQueue<Message> _messaQequeue)
    {
        messaQequeue = _messaQequeue;
    }

    public void Init( iGameLogic _serverLogic){
        HumanCards = _serverLogic.getComputerCards();
            ComputerQueenCards = _serverLogic.getHumanQueenCards();
            HumanQueenCards = _serverLogic.getComputerQueenCards();
            LastCard = _serverLogic.getLastCard();
            BeforeLastCard = _serverLogic.getBeforeLastCard();
            hasWinner = _serverLogic.hasWinner();
            canUserPlay = _serverLogic.canOponentPlay();

        ComputerCards = _serverLogic.getHumanCards();
        serverLogic         = _serverLogic;
    }

    public void startNewGame(){}
    public List<Card> getComputerCardsArray(){return null;}

    public List<Card> getHumanQueenCards(){
        return HumanQueenCards;
    }
    public List<Card> getComputerQueenCards(){
        return ComputerQueenCards;
    }
    public List<Card> getHumanCards(){
        return HumanCards;
    }
    public List<Card> getComputerCards(){
        return ComputerCards;
    }
    public Card getLastCard(){
        return LastCard;
    }
    public Card getBeforeLastCard(){
        return BeforeLastCard;
    }
    public int hasWinner(){
        return hasWinner;
    }
    public boolean userPlayCard(ArrayList<Card> _cardsToPlay){

        ArrayList<Card> tmp = new ArrayList<Card>();
        tmp.addAll(_cardsToPlay);
        Message message = new Message();
        message.obj = tmp;
        messaQequeue.add( message );
        return true;
    }

    public boolean oponentPlayCard(ArrayList<Card> cardsToPlay){
        return false;
    }
    public boolean canOponentPlay(){
        return false;
    }
    public boolean canUserPlay(){
        return canUserPlay;
    }
}
