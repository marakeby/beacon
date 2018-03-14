package edu.vt.beacon.editor.menu;

import java.awt.event.KeyEvent;

import edu.vt.beacon.editor.action.ActionType;
import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.swing.platform.PlatformMenuItem;

public class GlyphsMenu extends AbstractMenu
{
	private static final long serialVersionUID = 1L;

	private PlatformMenuItem annotationItem;
	private PlatformMenuItem geneItem;
	private PlatformMenuItem findItem;

	// FIXME complete constructor
	public GlyphsMenu(Document document)
	{
		super (document, "Glyphs", KeyEvent.VK_G);
	}
	
	// FIXME complete method
	@Override
	protected void buildMenu()
	{
		 annotationItem = new PlatformMenuItem();
		 geneItem = new PlatformMenuItem();
		annotationItem.setAction(document_.getAction( ActionType.GLYPHS_ANNOTATION));
		geneItem.setAction(document_.getAction( ActionType.GLYPHS_GENE));

		findItem = new PlatformMenuItem();
        
//		add(annotationItem);
		add(geneItem);
		add(findItem);
	}
	public void registerActions(Document doc)
	{

		annotationItem.setAction(document_.getAction( ActionType.GLYPHS_ANNOTATION));
		geneItem.setAction(document_.getAction( ActionType.GLYPHS_GENE));
		findItem.setAction(document_.getAction(ActionType.GLYPHS_FIND));
	}
}


