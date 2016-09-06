package edu.vt.beacon.graph.glyph.auxiliary;

import java.awt.Cursor;

public enum BoundType
{
    NORTHEAST (Cursor.NE_RESIZE_CURSOR),
    NORTHWEST (Cursor.NW_RESIZE_CURSOR),
    POINT     (Cursor.MOVE_CURSOR),
    SOUTHEAST (Cursor.SE_RESIZE_CURSOR),
    SOUTHWEST (Cursor.SW_RESIZE_CURSOR);
    
    private int cursorType_;
    
    // TODO document constructor
    private BoundType(int cursorType)
    {
        cursorType_ = cursorType;
    }
    
    // TODO document method
    public int getCursorType()
    {
        return cursorType_;
    }
}