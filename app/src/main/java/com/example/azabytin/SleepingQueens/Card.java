package com.example.azabytin.SleepingQueens;

/**
 * Created by azabytin on 16.01.2018.
 */

public class Card implements java.io.Serializable{

    public enum cardType{number, king, queen, stick, magic, knight, dragon, jocker};

    public Card( cardType t, int r )
    {
        this( t, r, 0);
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

    protected int resourceId;
    public int getResourceId() {
        return resourceId;
    }

    protected int value;
    public int getValue() {
        return value;
    }

    protected cardType type;
    protected boolean roseQueen = false;
    protected boolean dogCatQueen = false;

    public void setRoseQueen(){roseQueen = true;}
    public boolean isRoseQueen(){return roseQueen;}

    public void setDogCatQueen(){dogCatQueen = true;}
    public boolean isDogCatQueen(){return dogCatQueen;}

    public boolean isKnight( ){return type == cardType.knight; };
    public boolean isStick( ){return type == cardType.stick; };
    public boolean isMagic( ){return type == cardType.magic; };
    public boolean isDragon( ){return type == cardType.dragon; };
    public boolean isKing( ){return type == cardType.king; };
    public boolean isNumver( ){return type == cardType.number; };
    public boolean isOddNumver( ){
        if(  type == cardType.number ){
            return value % 2 != 0;
        }
        return false;
    };
    public boolean isEvenNumver( ){
        if(  type == cardType.number ){
            return value % 2 == 0;
        }
        return false;
    };

    public boolean isJocker( ){return type == cardType.jocker; };

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
