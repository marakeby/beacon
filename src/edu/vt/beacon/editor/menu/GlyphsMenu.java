package edu.vt.beacon.editor.menu;

import java.awt.event.KeyEvent;

import edu.vt.beacon.editor.action.ActionType;
import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.swing.platform.PlatformMenuItem;

public class GlyphsMenu extends AbstractMenu
{
	private static final long serialVersionUID = 1L;
	
	// FIXME complete constructor
	public GlyphsMenu(Document document)
	{
		super (document, "Glyphs", KeyEvent.VK_G);
	}
	
	// FIXME complete method
	@Override
	protected void buildMenu()
	{
		PlatformMenuItem annotationItem = new PlatformMenuItem();
		PlatformMenuItem geneItem = new PlatformMenuItem();
		
		annotationItem.setAction(document_.getAction( ActionType.GLYPHS_ANNOTATION));
		geneItem.setAction(document_.getAction( ActionType.GLYPHS_GENE));
        
//		add(annotationItem);
		add(geneItem);
	}
}


