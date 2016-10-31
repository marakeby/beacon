package edu.vt.beacon.editor.swing;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;

public class IconButton extends JButton
{
    private static final long serialVersionUID = 1L;
    
    // TODO document constructor
    public IconButton()
    {
        this (null);
    }
    
    // TODO document constructor
    public IconButton(Icon icon)
    {
        setBorder(BorderFactory.createEmptyBorder());
        setContentAreaFilled(false);
        setFocusable(false);
        setIcon(icon);
    }
    
    // TODO document method
    @Override
    public Icon getPressedIcon()
    {
        return getDisabledIcon();
    }
}