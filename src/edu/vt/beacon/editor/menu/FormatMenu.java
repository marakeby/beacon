package edu.vt.beacon.editor.menu;

import java.awt.event.KeyEvent;

import edu.vt.beacon.editor.action.ActionType;
import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.swing.platform.PlatformMenuItem;

public class FormatMenu extends AbstractMenu
{
	private static final long serialVersionUID = 1L;
	
	// FIXME complete constructor
	public FormatMenu(Document document)
	{
		super (document, "Format", KeyEvent.VK_T);
	}
	
	// FIXME complete method
	@Override
	protected void buildMenu()
	{
		PlatformMenuItem legendItem = new PlatformMenuItem();
		PlatformMenuItem canvasDialogItem = new PlatformMenuItem();
		PlatformMenuItem fontDialogItem = new PlatformMenuItem();
		PlatformMenuItem shapeDialogItem = new PlatformMenuItem();
		PlatformMenuItem labelDialogItem = new PlatformMenuItem();

		legendItem.setAction(document_.getAction( ActionType.FORMAT_LEGEND));
		canvasDialogItem.setAction(document_.getAction( ActionType.FORMAT_CANVAS));
		fontDialogItem.setAction(document_.getAction( ActionType.FORMAT_FONT));
		shapeDialogItem.setAction(document_.getAction( ActionType.FORMAT_SHAPE));
		labelDialogItem.setAction(document_.getAction( ActionType.FORMAT_LABEL));

		add(labelDialogItem);
		add(fontDialogItem);
		add(shapeDialogItem);
		add(canvasDialogItem);
		add(legendItem);
	}
}
