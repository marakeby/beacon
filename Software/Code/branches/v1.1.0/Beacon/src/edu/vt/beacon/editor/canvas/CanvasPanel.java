package edu.vt.beacon.editor.canvas;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.swing.platform.PlatformScrollPane;
import edu.vt.beacon.editor.util.RenderingManager;
import edu.vt.beacon.graph.AbstractEntity;
import edu.vt.beacon.graph.glyph.AbstractGlyph;

public class CanvasPanel extends JPanel
{
    private static final long serialVersionUID = 1L;
    
    private CanvasMouseListener mouseListener_;
    
    private CanvasKeyListener keyListener_;

    private CanvasStateType state_;
    
    private Document document_;
    
    private PlatformScrollPane canvasScroll_;
    
    private Rectangle2D.Float selectionBox_;

    private float zoomFactor;
    
    // FIXME complete constructor
    public CanvasPanel(Document document)
    {
        document_ = document;
        canvasScroll_ = new PlatformScrollPane(this);
        canvasScroll_.setFocusable(true);
        selectionBox_ = new Rectangle2D.Float();
        zoomFactor = 1;

        initializeListeners();
    }
    public void setDocument(Document doc)
    {
        System.out.println("canvas setDoc");
        document_ = doc;
//        canvasScroll_ = new PlatformScrollPane(this);
        canvasScroll_.setFocusable(true);
        selectionBox_ = new Rectangle2D.Float();
//        zoomFactor = 1;

//        canvasScroll_ = new PlatformScrollPane(this);

        for (MouseListener m : this.getMouseListeners())
            this.removeMouseListener(m);

        for (MouseMotionListener m : this.getMouseMotionListeners())
            this.removeMouseMotionListener(m);
//            removeMouseMotionListener(m);

        for (KeyListener m : this.getKeyListeners())
            this.removeKeyListener(m);
//            removeKeyListener(m);

//        this.getScrollPane().removeKeyListener();
        for (KeyListener m : this.getScrollPane().getKeyListeners())
            this.getScrollPane().removeKeyListener(m);
//            removeKeyListener(m);


        initializeListeners();
    }
    
    // TODO document method
    public AbstractEntity getActiveGlyph()
    {
        return mouseListener_.getActiveGlyph();
    }
    
    // TODO document method
    @Override
    public Color getBackground()
    {
        return (document_ != null) ? document_.getColor("canvas.color")
                                   : null;
    }
    
    // FIXME complete method
    @Override
    public Dimension getPreferredSize()
    {
        Dimension size = super.getPreferredSize();
        
        Rectangle2D.Float activeBoundary_ = document_.getContextManager()
            .getBoundaryContext().getActiveBoundary();
        
        if (!Float.isNaN(activeBoundary_.width))
            if (activeBoundary_.getMaxX() > size.getWidth())
                size.setSize(activeBoundary_.getWidth(), size.getHeight());
        
        if (!Float.isNaN(activeBoundary_.height))
            if (activeBoundary_.getMaxY() > size.getHeight())
                size.setSize(size.getWidth(), activeBoundary_.getHeight());

        size.setSize((size.getWidth() + 200) * zoomFactor, (size.getHeight() + 200) * zoomFactor);

        return size;
    }
    
    // TODO document method
    public PlatformScrollPane getScrollPane()
    {
        return canvasScroll_;
    }
    
    // TODO document method
    public Rectangle2D.Float getSelectionBox()
    {
        return selectionBox_;
    }
    
    // TODO document method
    protected CanvasStateType getState()
    {
        return state_;
    }
    
    // FIXME complete method
    private void initializeListeners()
    {
        mouseListener_ = new CanvasMouseListener(document_);

        addMouseListener(mouseListener_);
        addMouseMotionListener(mouseListener_);

        keyListener_ = new CanvasKeyListener(document_);
        canvasScroll_.addKeyListener(keyListener_);
    }
    
    // TODO document method
    public boolean isPointMoving()
    {
        return state_ == CanvasStateType.POINT_MOVING;
    }
    
    // TODO document method
    public boolean isPortChanging()
    {
        return isSourceChanging() || isTargetChanging();
    }
    
    // TODO document method
    public boolean isSelectionDrawing()
    {
        return state_ == CanvasStateType.SELECTION_DRAWING;
    }
    
    // TODO document method
    public boolean isSelectionMoving()
    {
        return state_ == CanvasStateType.SELECTION_MOVING;
    }
    
    // TODO document method
    public boolean isSelectionSizing()
    {
        return state_ == CanvasStateType.SELECTION_SIZING;
    }
    
    // TODO document method
    public boolean isSourceChanging()
    {
        return state_ == CanvasStateType.SOURCE_CHANGING;
    }
    
    // TODO document method
    public boolean isTargetChanging()
    {
        return state_ == CanvasStateType.TARGET_CHANGING;
    }
    
    public boolean isPortShowing()
    {
        return state_ == CanvasStateType.PORT_SHOWING;
    }

    // FIXME complete method
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        
        RenderingManager.render(document_, g);
        
        revalidate();
    }

    // TODO document method
    protected void setState(CanvasStateType state)
    {
        state_ = state;
    }

    public float getZoomFactor() {
        return zoomFactor;
    }

    public void setZoomFactor(float zoomFactor) {
        this.zoomFactor = zoomFactor;
        repaint();
    }
}