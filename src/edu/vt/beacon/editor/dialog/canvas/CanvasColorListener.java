package edu.vt.beacon.editor.dialog.canvas;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;

import edu.vt.beacon.editor.dialog.BColorChooser;
import edu.vt.beacon.editor.document.Document;

public class CanvasColorListener implements MouseListener {
	
	public static final String ACTION_MAJOR_GRID_COLOR = "MAJOR_GRID";
	public static final String ACTION_MINOR_GRID_COLOR = "MINOR_GRID";
	public static final String ACTION_BACKGROUND_COLOR = "BACKGROUND";
	public static final String ACTION_SELECTED_COLOR = "SELECTED";
	    
    private String type;  // color type
    private Document document_;
    private JComponent target_;
    private JDialog owner_;
    private String actionType_ = "";
	    
    public CanvasColorListener(String type) {
        
        // set the color type
        this.type = type;
    }
    
    public CanvasColorListener(String type, Document document, 
    		JComponent target, JDialog owner ) {
        
        // set the color type
        this.type = type;
        this.document_ = document;
        this.target_ = target;
        this.owner_ = owner;
    }
    
    public CanvasColorListener(String type, Document document, 
    		JComponent target, JDialog owner, String actionType) {
        
        // set the color type
        this.type = type;
        this.document_ = document;
        this.target_ = target;
        this.owner_ = owner;
        this.actionType_ = actionType;
    }
    
    @Override
    public void mouseClicked(MouseEvent me) {}
    
    @Override
    public void mouseEntered(MouseEvent me) {}
    
    @Override
    public void mouseExited(MouseEvent me) {}
    
    @Override
    public void mousePressed(MouseEvent me) {
        
        // show a new color chooser for the selected color panel
        BColorChooser.showNodeDialog(owner_, Color.WHITE, Color.WHITE, 
        		target_.getBackground(), 
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames(null)[0],
                24, (JPanel) me.getSource(), type);
                
        
        if (me.getSource().equals(target_)){
        	
        	Color color = ((JComponent)me.getSource()).getBackground();
        	
            if (actionType_.equals(ACTION_MAJOR_GRID_COLOR))
            {
            	((CanvasDialog)owner_).getCanvasDialogState().setMajorGridColor(color);
            	document_.put("color.grid.major" , color);
            }
            else if (actionType_.equals(ACTION_MINOR_GRID_COLOR))
            {
            	((CanvasDialog)owner_).getCanvasDialogState().setMinorGridColor(color);
            	document_.put("color.grid.minor" , color);
            }
            else if (actionType_.equals(ACTION_BACKGROUND_COLOR))
            {
            	((CanvasDialog)owner_).getCanvasDialogState().setBkgColor(color);
            	document_.put("canvas.color" , color);
            }
            else if (actionType_.equals(ACTION_SELECTED_COLOR))
            {
            	((CanvasDialog)owner_).getCanvasDialogState().setSelectionColor(color);
            	document_.put("glyph.selection" , color);
            }
            
            document_.getCanvas().repaint();
        	
        }
        
        
    }
    
    @Override
    public void mouseReleased(MouseEvent me) {}
}


