package edu.vt.beacon.editor.document;

import edu.vt.beacon.editor.menu.EditMenu;
import edu.vt.beacon.editor.util.ActionManager;
import edu.vt.beacon.pathway.Pathway;

public class DocumentState
{
    private boolean isStacking_;
    
    private DocumentState next_;
    
    private DocumentState previous_;
    
    private Pathway pathway_;
    
    private String text_;
    
    // TODO document constructor
    public DocumentState(Document document)
    {
        this (document, "", false);
    }
    
    // FIXME complete constructor
    public DocumentState(Document document, String text, boolean isStacking)
    {
        isStacking_ = isStacking;
        text_ = text;
        pathway_ = document.getPathway().copy();
        
        DocumentState state = document.getState();
        
        if (state != null) {
            
            if (state.isStacking_ && isStacking)
                if (state.text_.equals(text))
                    if (state.previous_ != null)
                        state = state.previous_;
            
            previous_ = state;
            state.next_ = this;
        }
        
        document.setState(this);
        
        ActionManager.updateEditActions(document);
        ((EditMenu) document.getMenuBar().getMenu("Edit")).refresh();
    }
    
    // FIXME complete method
    public void apply(Document document)
    {
        document.setState(this);
        document.setPathway(pathway_.copy());
        
        document.getBrowserMenu().refresh();
        document.getLayersMenu().refresh();
        document.getCanvas().repaint();
        
        ((EditMenu) document.getMenuBar().getMenu("Edit")).refresh();
    }
    
    // TODO document method
    public DocumentState getNext()
    {
        return next_;
    }
    
    // TODO document method
    public DocumentState getPrevious()
    {
        return previous_;
    }
    
    // TODO document method
    public String getText()
    {
        return text_;
    }
}