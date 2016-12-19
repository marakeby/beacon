package edu.vt.beacon.editor.context;

import java.awt.geom.Rectangle2D;

import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.graph.glyph.AbstractGlyph;

public class BoundaryContext extends AbstractContext
{
    private Rectangle2D.Float activeBoundary_;
    
    private Rectangle2D.Float boundary_;
    
    private Rectangle2D.Float selectedBoundary_;
    
    // FIXME complete constructor
    public BoundaryContext(Document document)
    {
        super (document);
        
        activeBoundary_ = new Rectangle2D.Float();
        boundary_ = new Rectangle2D.Float();
        selectedBoundary_ = new Rectangle2D.Float();
    }
    
    // TODO document method
    public Rectangle2D.Float getActiveBoundary()
    {
        return activeBoundary_;
    }
    
    // TODO document method
    public Rectangle2D.Float getBoundary()
    {
        return boundary_;
    }
    
    // TODO document method
    public Rectangle2D.Float getSelectedBoundary()
    {
        return selectedBoundary_;
    }
    
    // TODO document method
    @Override
    public void process(AbstractGlyph glyph, float zoomFactor)
    {
        float minX = boundary_.x;
        
        if (Float.isNaN(minX) || zoomFactor * glyph.getAbsMinX() < minX)
            minX = glyph.getAbsMinX();
        
        float minY = boundary_.y;
        
        if (Float.isNaN(minY) || zoomFactor * glyph.getAbsMinY() < minY)
            minY = glyph.getAbsMinY();
        
        float width = boundary_.width;
        
        if (Float.isNaN(width) || zoomFactor * glyph.getAbsMaxX() > width)
            width = glyph.getAbsMaxX();
        
        float height = boundary_.height;
        
        if (Float.isNaN(height) || zoomFactor * glyph.getAbsMaxY() > height)
            height = glyph.getAbsMaxY();
        
        boundary_.setFrame(minX, minY, width, height);
        
        if (glyph.getLayer().isActive())
            processActive(glyph, zoomFactor);
    }
    
    // TODO document method
    private void processActive(AbstractGlyph glyph, float zoomFactor)
    {
        float minX = activeBoundary_.x;
        
        if (Float.isNaN(minX) || zoomFactor * glyph.getAbsMinX() < minX)
            minX = glyph.getAbsMinX();
        
        float minY = activeBoundary_.y;
        
        if (Float.isNaN(minY) || zoomFactor * glyph.getAbsMinY() < minY)
            minY = glyph.getAbsMinY();
        
        float width = activeBoundary_.width;
        
        if (Float.isNaN(width) || zoomFactor * glyph.getAbsMaxX() > width)
            width = glyph.getAbsMaxX();
        
        float height = activeBoundary_.height;
        
        if (Float.isNaN(height) || zoomFactor * glyph.getAbsMaxY() > height)
            height = glyph.getAbsMaxY();
        
        activeBoundary_.setFrame(minX, minY, width, height);
        
        if (glyph.isSelected())
            processSelected(glyph, zoomFactor);
    }
    
    // TODO document method
    public void processSelected(AbstractGlyph glyph, float zoomFactor)
    {
        float minX = selectedBoundary_.x;
        
        if (Float.isNaN(minX) || zoomFactor * glyph.getAbsMinX() < minX)
            minX = glyph.getAbsMinX();
        
        float minY = selectedBoundary_.y;
        
        if (Float.isNaN(minY) || zoomFactor * glyph.getAbsMinY() < minY)
            minY = glyph.getAbsMinY();
        
        float width = selectedBoundary_.width;
        
        if (Float.isNaN(width) || zoomFactor * glyph.getAbsMaxX() > width)
            width = glyph.getAbsMaxX();
        
        float height = selectedBoundary_.height;
        
        if (Float.isNaN(height) || zoomFactor * glyph.getAbsMaxY() > height)
            height = glyph.getAbsMaxY();
        
        selectedBoundary_.setFrame(minX, minY, width, height);
    }
    
    // FIXME complete method
    @Override
    public void reset()
    {
        activeBoundary_.setFrame(
            Float.NaN, Float.NaN, Float.NaN, Float.NaN);
        boundary_.setFrame(
            Float.NaN, Float.NaN, Float.NaN, Float.NaN);
        selectedBoundary_.setFrame(
            Float.NaN, Float.NaN, Float.NaN, Float.NaN);
    }
}