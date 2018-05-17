package com.example.azabytin.SleepingQueens;

/**
 * Created by azabytin on 16.01.2018.
 */

public class Card implements java.io.Serializable{

    public enum cardType{none, number, king, queen, stick, magic, knight, dragon, jocker}

    public Card( cardType t, int r )
    {
        this( t, r, 0);
    }

    public Card( int r )
    {
        this( cardType.none, r, 0);
    }
    public Card( cardType t)
    {
        this( t, 0, 0);
    }

    public Card( cardType t, int r, int v )
    {
      type = t;
      resourceId = r;
      value = v;
    }

    final int resourceId;

    private final int value;
    public int getValue() {
        return value;
    }

    private final cardType type;
    private boolean roseQueen = false;
    private boolean dogCatQueen = false;

    public void setRoseQueen(){roseQueen = true;}
    public boolean isRoseQueen(){return roseQueen;}

    public void setDogCatQueen(){dogCatQueen = true;}
    public boolean isDogCatQueen(){return dogCatQueen;}

    public boolean isKnight( ){return type == cardType.knight; }
    public boolean isStick( ){return type == cardType.stick; }
    public boolean isMagic( ){return type == cardType.magic; }
    public boolean isDragon( ){return type == cardType.dragon; }
    public boolean isKing( ){return type == cardType.king; }
    public boolean isNumver( ){return type == cardType.number; }
    public boolean isOddNumver( ){
        return type == cardType.number && value % 2 != 0;
    }
    public boolean isEvenNumver( ) {
        return type == cardType.number && value % 2 == 0;
    }

    public boolean isJocker( ){return type == cardType.jocker; }

    public cardType getType() {

        return type;
    }

    @Override
    public boolean equals(Object o) {
        boolean isEqual=false;
        if(o!=null && o instanceof Card) {
            Card card = (Card)o;
            isEqual=(this.resourceId==card.resourceId) && (this.type==card.type) && (this.value==card.value);
        }
        return isEqual;
    }

}
