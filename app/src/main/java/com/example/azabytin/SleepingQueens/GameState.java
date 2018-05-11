package com.example.azabytin.SleepingQueens;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by azabytin on 04.04.2018.
 */

public class GameState implements iGame {

    protected List<Card> ComputerQueenCards;
    protected List<Card> HumanQueenCards;
    protected List<Card> HumanCards;
    protected List<Card> ComputerCards;
    Card LastCard;
    Card BeforeLastCard;
    iGame.Winner whoWinner;
    boolean canUserPlay;
    iGame serverLogic;

    String serverHost;

    public GameState( String _serverHost)
    {
        serverHost = _serverHost;
    }

    private void InitFromGameLogic(iGame _serverLogic){
        HumanCards = _serverLogic.getOpponentCards();
            ComputerQueenCards = _serverLogic.getPlayerQueenCards();
            HumanQueenCards = _serverLogic.getOpponentQueenCards();
            LastCard = _serverLogic.getLastCard();
            BeforeLastCard = _serverLogic.getBeforeLastCard();
            whoWinner = _serverLogic.whoIsWinner();
            canUserPlay = _serverLogic.canOponentPlay();

        ComputerCards = _serverLogic.getPlayerCards();
        serverLogic         = _serverLogic;
    }

    public void startNewGame(){}
    public List<Card> getComputerCardsArray(){return null;}

    public List<Card> getPlayerQueenCards(){
        return HumanQueenCards;
    }
    public List<Card> getOpponentQueenCards(){
        return ComputerQueenCards;
    }
    public List<Card> getPlayerCards(){
        return HumanCards;
    }
    public List<Card> getOpponentCards(){
        return ComputerCards;
    }
    public Card getLastCard(){
        return LastCard;
    }
    public Card getBeforeLastCard(){
        return BeforeLastCard;
    }
    public iGame.Winner whoIsWinner(){
        if( whoWinner == Winner.PlayerWinner )
            return Winner.OpponentWinner;

        if( whoWinner == Winner.OpponentWinner)
            return Winner.PlayerWinner;

        return whoWinner;
    }
    public boolean userPlayCards(ArrayList<Card> cardsToPlay){

        new AsyncTask<Void, Boolean, Boolean>() {
            @Override
            protected Boolean doInBackground(final Void... params) {
                try {
                    new ClientSocketSerializer(serverHost).writeCardsToPlay(cardsToPlay);
                }catch (Exception e){}
                return true;
            }
        }.execute();

        Log.i("ClientGameLogic", "Send cards to play");
        return true;
    }

    public void Update(){

        new AsyncTask<Void, Boolean, GameLogic>() {
            @Override
            protected GameLogic doInBackground(final Void... params) {
                try {
                    return new ClientSocketSerializer(serverHost).readGameLogic();
                }catch (Exception e){}
                return null;
            }

            @Override
            protected void onPostExecute( final GameLogic result ) {
                if( result != null ) {
                    InitFromGameLogic( result );
                }
            }

        }.execute();

    }
    public boolean oponentPlayCards(ArrayList<Card> cardsToPlay){
        return false;
    }
    public boolean canOponentPlay(){
        return false;
    }
    public boolean canUserPlay(){
        return canUserPlay;
    }
}
