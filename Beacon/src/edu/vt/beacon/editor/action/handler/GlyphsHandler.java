package edu.vt.beacon.editor.action.handler;

import java.awt.event.ActionEvent;

import edu.vt.beacon.editor.action.Action;
import edu.vt.beacon.editor.dialog.AnnotationsDialog;
import edu.vt.beacon.editor.dialog.GeneDialog;
import edu.vt.beacon.editor.document.Document;

public class GlyphsHandler
    implements ActionHandler
{
    private static final GlyphsHandler instance_ = new GlyphsHandler();
    
    // TODO document method
    public static GlyphsHandler getInstance()
    {
        return instance_;
    }
    
    // FIXME complete method
    @Override
    public void handle(Action action, ActionEvent event)
    {
    	switch (action.getType()) {        
        	case GLYPHS_ANNOTATION:
        		new AnnotationsDialog(action.getDocument());
        		break;
        	case GLYPHS_GENE:
        		new GeneDialog(action.getDocument());
        		break;
            case GLYPHS_FIND:
                processFind(action);
                break;
        	default :
        		throw new IllegalStateException("missing action type case");
    	}        
    }

    private void processFind(Action action) {
        Document doc = action.getDocument();
        if (doc !=null)
        {
            if (doc.getFrame() != null) {
                doc.getFrame().find();
            }
        }
    }
    
}
