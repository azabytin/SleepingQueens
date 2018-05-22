package com.example.azabytin.SleepingQueens;

import static com.example.azabytin.SleepingQueens.Card.cardType.none;

/**
 * Created by azabytin on 16.01.2018.
 */

public class Card implements java.io.Serializable{

    public enum cardType{none, number, king, queen, stick, magic, knight, dragon, jocker}

    public Card( cardType cardType, int resourceId )
    {
        this( cardType, resourceId, 0);
    }

    public Card( int resourceId )
    {
        this( none, resourceId, 0);
    }
    public Card( cardType t)
    {
        this( t, 0, 0);
    }

    public Card( cardType cardType, int resourceId, int cardValue )
    {
      this.cardType = cardType;
      this.resourceId = resourceId;
      this.cardValue = cardValue;
    }

    final int resourceId;

    private final int cardValue;
    public int getCardValue() {
        return cardValue;
    }

    private final cardType cardType;
    private boolean roseQueen = false;
    private boolean dogCatQueen = false;

    public void setRoseQueen(){roseQueen = true;}
    public boolean isRoseQueen(){return roseQueen;}

    public void setDogCatQueen(){dogCatQueen = true;}
    public boolean isDogCatQueen(){return dogCatQueen;}

    public boolean isKnight( ){return cardType == cardType.knight; }
    public boolean isStick( ){return cardType == cardType.stick; }
    public boolean isMagic( ){return cardType == cardType.magic; }
    public boolean isDragon( ){return cardType == cardType.dragon; }
    public boolean isKing( ){return cardType == cardType.king; }
    public boolean isNumber( ){return cardType == cardType.number; }
    public boolean isOddNumber( ){
        return cardType == cardType.number && cardValue % 2 != 0;
    }
    public boolean isEvenNumber( ) {
        return cardType == cardType.number && cardValue % 2 == 0;
    }

    public boolean isJocker( ){return cardType == cardType.jocker; }

    public cardType getCardType() {

        return cardType;
    }

    @Override
    public boolean equals(Object o) {
        boolean isEqual=false;
        if(o!=null && o instanceof Card) {
            Card card = (Card)o;
            isEqual=(this.resourceId==card.resourceId) && (this.cardType ==card.cardType) && (this.cardValue ==card.cardValue);
        }
        return isEqual;
    }

}
