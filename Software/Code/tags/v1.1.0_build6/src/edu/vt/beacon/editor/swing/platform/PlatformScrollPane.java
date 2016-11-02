package edu.vt.beacon.editor.swing.platform;

import java.awt.Component;

import javax.swing.JScrollPane;

import edu.vt.beacon.editor.util.PlatformManager;

public class PlatformScrollPane extends JScrollPane
{
    private static final long serialVersionUID = 1L;
    
    // TODO document constructor
    public PlatformScrollPane()
    {
        this (null);
    }
    
    // TODO document constructor
    public PlatformScrollPane(Component view)
    {
        super (view);
        
        if (PlatformManager.isMacPlatform()) {
            
            setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_ALWAYS);
            setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
        }
    }
}