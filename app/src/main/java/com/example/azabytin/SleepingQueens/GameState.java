package com.example.azabytin.SleepingQueens;

/**
 * Created by azabytin on 19.01.2018.
 */

public enum GameState {

    IDLE {
        GameState PlayCard(Card card, GameLogic.userType type, GameLogic gameLogic) {

            if( card.isMagic() ){
                return MAGIC_ATTACKED;
            }

            if( card.isKnight() ){
                return KNIGHT_ATTACKED;
            }
            return WAIT_FOR_CARD;
        }
    },
    WAIT_FOR_CARD {
        GameState PlayCard(Card card, GameLogic.userType type, GameLogic gameLogic) {

            if( gameLogic.playCardFromGameState( type, card ) ){
                return WAIT_FOR_CARD;
            }

            return IDLE;
        }
    },
    MAGIC_ATTACKED {
        GameState PlayCard(Card card, GameLogic.userType type, GameLogic gameLogic) {

            if (card.isStick()) {
                return WAIT_FOR_CARD;
            }

            gameLogic.getBackQueen( type );
            return IDLE;
        }
    },
    KNIGHT_ATTACKED {
        GameState PlayCard(Card card, GameLogic.userType type, GameLogic gameLogic) {

            if (card.isDragon()) {
                return WAIT_FOR_CARD;
            }

            gameLogic.giveOponentQueen( type );
            return IDLE;
        }

    };
    GameState PlayCard( Card card, GameLogic.userType type, GameLogic gameLogic ){ return this;};

}

