package edu.vt.beacon.graph.glyph.node.annotation;

import edu.vt.beacon.graph.AbstractEntity;
import edu.vt.beacon.graph.legend.Legend;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Created by mostafa on 4/2/16.
 */
public class CalloutPoint extends AbstractEntity {

    private static float SIZE = 7.0F;
    private Annotation annotation;

    public CalloutPoint(float centerX, float centerY, Annotation annotation) {
        super();
        setBoundary(centerX, centerY);
        setSelected(false);
        this.annotation = annotation;
    }

    @Override
    protected void initializeShape() {
        shape_ = new Rectangle2D.Float();
    }

    @Override
    protected void setBoundary() {
        boundary_.width = SIZE;
        boundary_.height = SIZE;
    }

    protected void setBoundary(float centerX, float centerY) {
        boundary_.x = centerX - SIZE / 2.0f;
        boundary_.y = centerY - SIZE / 2.0f;
        boundary_.width = SIZE;
        boundary_.height = SIZE;
        setShapeCoordinates();
    }

    @Override
    protected void setShapeCoordinates() {
        Rectangle2D.Float rect = (Rectangle2D.Float) shape_;
        rect.setRect(getMinX(), getMinY(), getWidth(), getHeight());
    }

    @Override
    public void move(float deltaX, float deltaY) {
        boundary_.x = Math.max(annotation.getTarget().getMinX(), Math.min(boundary_.x + deltaX,
                annotation.getTarget().getMaxX() - getWidth()));

        boundary_.y = Math.max(annotation.getTarget().getMinY(), Math.min(boundary_.y + deltaY,
                annotation.getTarget().getMaxY() - getHeight()));

        setShapeCoordinates();

        annotation.update();
    }
}
