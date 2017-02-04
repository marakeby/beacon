package edu.vt.beacon.graph.glyph.auxiliary;

import edu.vt.beacon.graph.AbstractEntity;

import java.awt.geom.Rectangle2D;

public class Bound extends AbstractEntity {
    private static float size_ = 10.0F;

    private BoundType type_;

    // TODO document constructor
    public Bound(BoundType type) {
        type_ = type;

        update();
    }

    private Bound() {
    }

    public Bound copy() {
        Bound bound = new Bound();
        bound.setId(getId());
        bound.type_ = type_;
        bound.boundary_.setRect(boundary_.x, boundary_.y, boundary_.width, boundary_.height);
        bound.setSelected(isSelected());

        Rectangle2D.Float castShape = (Rectangle2D.Float) shape_;
        ((Rectangle2D.Float) bound.shape_).setRect(castShape.x, castShape.y, castShape.width, castShape.height);

        return bound;
    }

    // TODO document method
    public BoundType getType() {
        return type_;
    }

    // TODO document method
    @Override
    protected void initializeShape() {
        shape_ = new Rectangle2D.Float();
    }

    // TODO document method
    @Override
    protected void setBoundary() {
        boundary_.width = size_;
        boundary_.height = size_;
    }

    // TODO document method
    @Override
    protected void setShapeCoordinates() {
        Rectangle2D.Float rectangle = (Rectangle2D.Float) shape_;
        rectangle.setFrame(getMinX(), getMinY(), getWidth(), getHeight());
    }
}