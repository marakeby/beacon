package edu.vt.beacon.editor.dialog.shape;

import javax.swing.JComponent;

import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.graph.AbstractEntity;
import edu.vt.beacon.graph.glyph.AbstractGlyph;

public interface ShapeGlyphRunnable {
	
	public abstract void modifyGlyph(Object source, AbstractEntity entity, Document document_);

}
