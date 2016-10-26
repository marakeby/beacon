package edu.vt.beacon.editor.menubar;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.menu.*;

public class MenuBar extends JMenuBar
{
    private static final long serialVersionUID = 1L;
    
    // FIXME complete constructor
    private FileMenu fileMenu;
    private EditMenu editMenu;
    private ViewMenu viewMenu;
    private FormatMenu formatMenu;
    private GlyphsMenu glyphsMenu;
    private AboutMenu aboutMenu;

    public MenuBar(Document document)
    {
        fileMenu = new FileMenu(document);
        editMenu = new EditMenu(document);
        viewMenu = new ViewMenu(document);
        formatMenu = new FormatMenu(document);
        glyphsMenu = new GlyphsMenu(document);
        aboutMenu = new AboutMenu(document);

        add(fileMenu);
        add(editMenu);
        add(viewMenu);
        add(formatMenu);
        add(glyphsMenu);
        add(aboutMenu);
//    	add(new FileMenu(document));
//        add(new EditMenu(document));
//        add(new ViewMenu(document));
//        add(new FormatMenu(document));
//        add(new GlyphsMenu(document));
//        add(new AboutMenu(document));
        
    }
    public void setDocument(Document doc){
        fileMenu.setDocument(doc);
        editMenu.setDocument(doc);
        viewMenu.setDocument(doc);
        formatMenu.setDocument(doc);
        glyphsMenu.setDocument(doc);
        aboutMenu.setDocument(doc);
        editMenu.refresh();
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