package edu.vt.beacon.graph.glyph;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.vt.beacon.graph.AbstractEntity;
import edu.vt.beacon.graph.glyph.auxiliary.Bound;
import edu.vt.beacon.graph.glyph.node.annotation.Annotation;
import edu.vt.beacon.graph.glyph.node.annotation.CalloutPoint;
import edu.vt.beacon.layer.Layer;

public abstract class AbstractGlyph extends AbstractEntity
{
    protected ArrayList<Bound> bounds_;

    protected float lineWidth_;

    protected float padding_;

    private boolean isHypothetical_;

    private Color backgroundColor_;

    private Color foregroundColor_;

    private GlyphType type_;

    private Layer layer_;

    // Gene info
    private String pubMedID_;

    private String notes_;

    private Annotation annotation;

    // TODO document constructor
    protected AbstractGlyph(GlyphType type)
    {
        lineWidth_ = 1.0F;
        padding_ = 0.0F;
        backgroundColor_ = Color.white;
        foregroundColor_ = Color.black;
        type_ = type;

        initializeBounds();
    }

    // TODO document method
    @Override
    public boolean contains(Point2D.Float point)
    {
        if (isSelected()) {

            for (Bound bound : bounds_)
                if (bound.contains(point))

                    return true;
        }

        return super.contains(point);
    }

    // TODO document method
    public AbstractGlyph copy()
    {
        AbstractGlyph glyph = type_.newGlyph();
        glyph.boundary_.height = boundary_.height;
        glyph.boundary_.width = boundary_.width;
        glyph.boundary_.x = boundary_.x;
        glyph.boundary_.y = boundary_.y;
        glyph.lineWidth_ = lineWidth_;
        glyph.padding_ = padding_;
        glyph.isHypothetical_ = isHypothetical_;
        glyph.backgroundColor_ = backgroundColor_;
        glyph.foregroundColor_ = foregroundColor_;
        glyph.annotation = annotation;

        return glyph;
    }

    // TODO document method
    public void delete()
    {
        if (layer_ != null)
            layer_.remove(this);
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }

    // TODO document method
    public float getAbsMaxX()
    {
        return getMaxX();
    }

    // TODO document method
    public float getAbsMaxY()
    {
        return getMaxY();
    }

    // TODO document method
    public float getAbsMinX()
    {
        return getMinX();
    }

    // TODO document method
    public float getAbsMinY()
    {
        return getMinY();
    }

    // TODO document method
    public Color getBackgroundColor()
    {
        return backgroundColor_;
    }

    // TODO document method
    public Bound getBoundAt(int index)
    {
        return bounds_.get(index);
    }

    // TODO document method
    public int getBoundCount()
    {
        return bounds_.size();
    }

    // TODO document method
    public Color getForegroundColor()
    {
        return foregroundColor_;
    }

    // TODO document method
    public Layer getLayer()
    {
        return layer_;
    }

    // TODO document method
    public float getLineWidth()
    {
        return lineWidth_;
    }

    // TODO document method
    public float getPadding()
    {
        return padding_;
    }

    // TODO document method
    public GlyphType getType()
    {
        return type_;
    }

    // TODO document method
    protected abstract void initializeBounds();

    // TODO document method
    public boolean isHypothetical()
    {
        return isHypothetical_;
    }


    // TODO document method
    public void setBackgroundColor(Color color)
    {
        backgroundColor_ = color;
    }

    // TODO document method
    @Override
    protected void setBoundary()
    {
        if (getAbsMinX() < 0 || getAbsMinY() < 0)
            if (layer_ != null && layer_.getMap() != null)
                layer_.getMap().invalidate(this);
    }

    // TODO document method
    protected abstract void setBoundCoordinates();

    // TODO document method
    public void setForegroundColor(Color color)
    {
        foregroundColor_ = color;
    }

    // TODO document method
    public void setHypothetical(boolean isHypothetical)
    {
        isHypothetical_ = isHypothetical;
    }

    // TODO document method
    public void setLayer(Layer layer)
    {
        layer_ = layer;
    }

    // TODO document method
    public void setLineWidth(float width)
    {
        lineWidth_ = width;

        update();
    }

    // TODO document method
    public void setPadding(float padding)
    {
        padding_ = padding;

        update();
    }

    @Override
    public void move(float deltaX, float deltaY) {
        super.move(deltaX, deltaY);
        if (annotation != null && annotation.getCalloutPoint() != null) {

            CalloutPoint calloutPoint = annotation.getCalloutPoint();
            calloutPoint.move(deltaX, deltaY);

        }

    }

    // TODO document method
    @Override
    public void update()
    {
        super.update();

        setBoundCoordinates();
    }

	public String getPubMedID() {
		return pubMedID_;
	}

	public void setPubMedID(String pubMedID_) {
		this.pubMedID_ = pubMedID_;
	}

	public String getNotes() {
		return notes_;
	}

	public void setNotes(String notes_) {
		this.notes_ = notes_;
	}

}