package edu.vt.beacon.editor.swing.laf;

import java.awt.Color;
import java.awt.Font;

import javax.swing.tree.DefaultTreeCellRenderer;

public interface Skinnable
{
    public static final Color COLOR_BACKGROUND
        = Color.getHSBColor(0.0F, 0.0F, 0.0F);
    
    public static final Color COLOR_FOREGROUND
        = Color.getHSBColor(0.0F, 0.0F, 1.0F);
    
    public static final Font FONT_MEDIUM = new Font(null, Font.PLAIN, 11);
    
    public static final DefaultTreeCellRenderer SELECTION_RENDERER
        = new DefaultTreeCellRenderer();
}