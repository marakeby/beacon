package edu.vt.beacon.editor.menu;

import javax.swing.JMenu;

import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.util.PlatformManager;

public abstract class AbstractMenu extends JMenu
{
    private static final long serialVersionUID = 1L;
    
    protected Document document_;
    
    // TODO document constructor
    protected AbstractMenu(Document document, String text, int mnemonic)
    {
        super (text);
        
        if (!PlatformManager.isMacPlatform())
            setMnemonic(mnemonic);
        
        document_ = document;
        
        buildMenu();
        registerActions(document);
        repaint();
    }

    public void setDocument(Document doc){

        document_ = doc;
        registerActions(doc);
        repaint();
    }
    // TODO document method
    protected abstract void buildMenu();
    protected abstract void registerActions(Document doc);
}