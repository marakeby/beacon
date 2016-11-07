package edu.vt.beacon.editor.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import edu.vt.beacon.editor.document.Document;

public class Action extends AbstractAction
{
    private static final long serialVersionUID = 1L;
    
    private ActionType type_;
    
    private Document document_;
    
    // FIXME complete constructor
    public Action(Document document, ActionType type)
    {
        putValue(NAME, type.getText());
        
        type_ = type;
        document_ = document;
    }
    
    // TODO document method
    @Override
    public void actionPerformed(ActionEvent event)
    {
        type_.getHandler().handle(this, event);
    }
    
    // TODO document method
    public Document getDocument()
    {
        return document_;
    }
    
    // TODO document method
    public ActionType getType()
    {
        return type_;
    }
}