package edu.vt.beacon.graph;

public enum OrientationType
{
    LEFT          ("left"),
    DOWN          ("down"),
    RIGHT         ("right"),
    UP            ("up"),
    HORIZONTAL    ("horizontal"),
    VERTICAL      ("vertical");

    private String value;

    private OrientationType(String v) {
        value = v;
    }

    public String toString() {
        return value;
    }
    /*
         * document method
         */
    public boolean isHorizontal()
    {
        return this == LEFT || this == RIGHT || this == HORIZONTAL;
    }
    
    /*
     * document method
     */
    public boolean isVertical()
    {
        return this == DOWN || this == UP || this == VERTICAL;
    }
}