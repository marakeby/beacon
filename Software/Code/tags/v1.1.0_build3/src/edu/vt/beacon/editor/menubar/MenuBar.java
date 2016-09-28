package edu.vt.beacon.editor.menubar;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.menu.*;

public class MenuBar extends JMenuBar
{
    private static final long serialVersionUID = 1L;
    
    // FIXME complete constructor
    public MenuBar(Document document)
    {
    	add(new FileMenu(document));
        add(new EditMenu(document));
        add(new ViewMenu(document));
        add(new FormatMenu(document));
        add(new GlyphsMenu(document));
        add(new AboutMenu(document));
        
    }
    
    // TODO document method
    public JMenu getMenu(String text)
    {
        for (int i = 0; i < getMenuCount(); i++)
            if (getMenu(i).getText().equals(text))
                
                return getMenu(i);
        
        return null;
    }
}