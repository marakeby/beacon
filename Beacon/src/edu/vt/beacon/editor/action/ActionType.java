package edu.vt.beacon.editor.action;

import edu.vt.beacon.editor.action.handler.*;

public enum ActionType
{
	EDIT_REDO              (""),
    EDIT_UNDO              (""),
    EDIT_ALIGNMENT         ("Alignment"),
    EDIT_RIGHT_ALIGNMENT   ("Right"),
    EDIT_LEFT_ALIGNMENT    ("Left"),
    EDIT_TOP_ALIGNMENT     ("Top"),
    EDIT_BOTTOM_ALIGNMENT  ("Bottom"),

    FILE_PROPERTIES 	  ("Properties..."),
    FILE_PREFERENCES 	  ("Preferences..."),
    FILE_NEW 	  ("New"),
    FILE_OPEN 	          ("Open..."),
    FILE_SAVE 	          ("Save"),
    FILE_SAVE_AS 	          ("Save As..."),
    FILE_EXPORT	          ("Export..."),
    FILE_BACK_COMP_IMPORT ("Import From Previous Version..."),

	VIEW_GRID_LINES 	("Grid Lines..."),
	VIEW_ZOOM_IN  		("Zoom In..."),
	VIEW_ZOOM_OUT  		("Zoom out..."),
	VIEW_ZOOM  		    ("Zoom..."),
	VIEW_ZOOM_25  		("25%"),
	VIEW_ZOOM_50  		("50%"),
	VIEW_ZOOM_75  		("75%"),
	VIEW_ZOOM_100  		("100%"),
	VIEW_ZOOM_125  		("125%"),
	VIEW_ZOOM_150  		("150%"),
	VIEW_ZOOM_175  		("175%"),
	VIEW_ZOOM_200  		("200%"),

	FORMAT_LEGEND  		("Legend..."),
	FORMAT_CANVAS		("Canvas..."),
	FORMAT_SHAPE		("Shape..."),
	FORMAT_FONT			("Font..."),
	FORMAT_LABEL		("Label..."),

	GLYPHS_ANNOTATION 	("Annotation..."),
	GLYPHS_GENE  		("Genes..."),
	
	ABOUT_ABOUT			("About Beacon");

    private String text_;
    
    // FIXME complete constructor
    private ActionType(String text)
    {
        text_ = text;
    }
    
    // FIXME complete method
    public ActionHandler getHandler()
    {
        if (name().startsWith("FILE"))
            return FileHandler.getInstance();
        
        if (name().startsWith("EDIT"))
            return EditHandler.getInstance();
        
        if (name().startsWith("VIEW"))
            return ViewHandler.getInstance();

        if (name().startsWith("FORMAT"))
            return FormatHandler.getInstance();
        
        if (name().startsWith("GLYPHS"))
            return GlyphsHandler.getInstance();
        
        if (name().startsWith("ABOUT"))
            return AboutHandler.getInstance();
        
        throw new IllegalStateException("missing action handler case");
    }
    
    // TODO document method
    public String getText()
    {
        return text_;
    }
}