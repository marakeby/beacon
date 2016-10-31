package edu.vt.beacon.editor.dialog.canvas;

import java.awt.Color;

import edu.vt.beacon.editor.document.Document;

public class CanvasDialogState {
	
	private static final String KEY_COLOR_GRID_MAJOR = "color.grid.major";
	private static final String KEY_COLOR_GRID_MINOR = "color.grid.minor";
	private static final String KEY_COLOR_CANVAS = "canvas.color";
	private static final String KEY_COLOR_SELECTION = "glyph.selection";
	private static final String KEY_SPACING_GRID_MAJOR = "grid.major";
	private static final String KEY_SPACING_GRID_MINOR = "grid.minor";
	
	private Color majorGridColor_;
	private Color minorGridColor_;
	private Color bkgColor_;
	private Color selectionColor_;
	private float majorGridSpacing_;
	private float minorGridSpacing_;
	
	private Color initialMajorGridColor_;
	private Color initialMinorGridColor_;
	private Color initialBkgColor_;
	private Color initialSelectionColor_;
	private float initialMajorGridSpacing_;
	private float initialMinorGridSpacing_;
	
	private Document document_;
	
	public CanvasDialogState(Document document) {
		
		document_ = document;
		
		initialMajorGridColor_ = document_.getColor(KEY_COLOR_GRID_MAJOR);
		initialMinorGridColor_ = document_.getColor(KEY_COLOR_GRID_MINOR);
		initialBkgColor_ = document_.getColor(KEY_COLOR_CANVAS);
		initialSelectionColor_ = document_.getColor(KEY_COLOR_SELECTION);
		initialMajorGridSpacing_ = document_.getFloat(KEY_SPACING_GRID_MAJOR);
		initialMinorGridSpacing_ = document_.getFloat(KEY_SPACING_GRID_MINOR);
		
		majorGridColor_ = initialMajorGridColor_;
		minorGridColor_ = initialMinorGridColor_;
		bkgColor_ = initialBkgColor_;
		selectionColor_ = initialSelectionColor_;
		majorGridSpacing_ = initialMajorGridSpacing_;
		minorGridSpacing_ = initialMinorGridSpacing_;
		
	}
	
	public void rollback() {
		document_.put(KEY_COLOR_GRID_MAJOR, initialMajorGridColor_);
		document_.put(KEY_COLOR_GRID_MINOR, initialMinorGridColor_);
		document_.put(KEY_COLOR_CANVAS, initialBkgColor_);
		document_.put(KEY_COLOR_SELECTION, initialSelectionColor_);	
		document_.put(KEY_SPACING_GRID_MAJOR, initialMajorGridSpacing_);
		document_.put(KEY_SPACING_GRID_MINOR, initialMinorGridSpacing_);
		document_.getCanvas().repaint();
	}

	public Color getMajorGridColor() {
		return majorGridColor_;
	}

	public void setMajorGridColor(Color majorGridColor_) {
		this.majorGridColor_ = majorGridColor_;
	}

	public Color getMinorGridColor() {
		return minorGridColor_;
	}

	public void setMinorGridColor(Color minorGridColor_) {
		this.minorGridColor_ = minorGridColor_;
	}

	public Color getBkgColor() {
		return bkgColor_;
	}

	public void setBkgColor(Color bkgColor_) {
		this.bkgColor_ = bkgColor_;
	}

	public Color getSelectionColor() {
		return selectionColor_;
	}

	public void setSelectionColor(Color selectionColor_) {
		this.selectionColor_ = selectionColor_;
	}

	public float getMajorGridSpacing() {
		return majorGridSpacing_;
	}

	public void setMajorGridSpacing(float majorGridSpacing_) {
		this.majorGridSpacing_ = majorGridSpacing_;
	}

	public float getMinorGridSpacing() {
		return minorGridSpacing_;
	}

	public void setMinorGridSpacing(float minorGridSpacing_) {
		this.minorGridSpacing_ = minorGridSpacing_;
	}
	
	

}
