package edu.vt.beacon.editor.menu;

import java.awt.event.KeyEvent;

import edu.vt.beacon.editor.action.ActionType;
import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.document.DocumentState;
import edu.vt.beacon.editor.swing.platform.PlatformMenuItem;

import javax.swing.*;

public class EditMenu extends AbstractMenu
{
    private static final long serialVersionUID = 1L;
    
    private PlatformMenuItem redoItem_;
    
    private PlatformMenuItem undoItem_;

    private JMenu alignmentItem_;

    // TODO document constructor
    public EditMenu(Document document)
    {
        super (document, "Edit", KeyEvent.VK_E);
        
        refresh();
    }
    
    // FIXME complete method
    @Override
    protected void buildMenu()
    {
        redoItem_ = new PlatformMenuItem();
        undoItem_ = new PlatformMenuItem();
        alignmentItem_ = new JMenu("Alignment...");
        
//        redoItem_.setAction(document_.getAction(ActionType.EDIT_REDO));
//        undoItem_.setAction(document_.getAction(ActionType.EDIT_UNDO));
//        alignmentItem_.setAction(document_.getAction(ActionType.EDIT_ALIGNMENT));

        populateAlignmentMenuItem();

        add(undoItem_);
        add(redoItem_);
        add(alignmentItem_);
    }
    public void registerActions(Document doc)
    {
        System.out.println("register Edit menu doc "+ doc.getFile().getAbsolutePath());
        redoItem_.removeAll();
        undoItem_.removeAll();
        alignmentItem_.removeAll();

        redoItem_.setAction(document_.getAction(ActionType.EDIT_REDO));
        undoItem_.setAction(document_.getAction(ActionType.EDIT_UNDO));
        alignmentItem_.setAction(document_.getAction(ActionType.EDIT_ALIGNMENT));
        populateAlignmentMenuItem();

    }

    private void populateAlignmentMenuItem() {
        if (alignmentItem_ == null)
            return;

        PlatformMenuItem item = new PlatformMenuItem();
        item.setAction(document_.getAction(ActionType.EDIT_LEFT_ALIGNMENT));
        alignmentItem_.add(item);

        item = new PlatformMenuItem();
        item.setAction(document_.getAction(ActionType.EDIT_RIGHT_ALIGNMENT));
        alignmentItem_.add(item);

        item = new PlatformMenuItem();
        item.setAction(document_.getAction(ActionType.EDIT_TOP_ALIGNMENT));
        alignmentItem_.add(item);

        item = new PlatformMenuItem();
        item.setAction(document_.getAction(ActionType.EDIT_BOTTOM_ALIGNMENT));
        alignmentItem_.add(item);

    }



    // TODO document method
    public void refresh()
    {
        redoItem_.setText("Can't Redo");
        undoItem_.setText("Can't Undo");
        
        DocumentState state = document_.getState();

        if (state.getNext() != null)
            redoItem_.setText("Redo " + state.getNext().getText());

        if (state.getPrevious() != null)
            undoItem_.setText("Undo " + state.getText());
    }
}