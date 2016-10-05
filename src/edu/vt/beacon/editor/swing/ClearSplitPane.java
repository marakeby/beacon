package edu.vt.beacon.editor.swing;

import javax.swing.BorderFactory;
import javax.swing.JSplitPane;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class ClearSplitPane extends JSplitPane
{
    private static final long serialVersionUID = 1L;
    
    // TODO document constructor
    public ClearSplitPane()
    {
        this (HORIZONTAL_SPLIT);
    }
    
    // TODO document constructor
    public ClearSplitPane(int orientation)
    {
        setBorder(BorderFactory.createEmptyBorder());
        setDividerSize(5);
        setOpaque(false);
        setOrientation(orientation);
        setUI(new ClearSplitPaneUI());
    }
    
    private class ClearSplitPaneUI extends BasicSplitPaneUI
    {
        // TODO document method
        @Override
        public BasicSplitPaneDivider createDefaultDivider()
        {
            BasicSplitPaneDivider divider = super.createDefaultDivider();
            divider.setBorder(null);
            
            return divider;
        }
    }
}