package edu.vt.beacon.graph.glyph.node.annotation;

import edu.vt.beacon.graph.glyph.AbstractGlyph;
import edu.vt.beacon.graph.glyph.GlyphType;
import edu.vt.beacon.graph.glyph.node.AbstractNode;
import edu.vt.beacon.graph.glyph.node.auxiliary.Label;
import edu.vt.beacon.graph.glyph.node.auxiliary.Port;

import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class Annotation extends AbstractNode {

    public float offset = 100;

    public final static int SLANT = 10;

    private final static float EPS = 1e-6f;

    private AbstractGlyph target_;

    private CalloutPoint calloutPoint;

    private Path2D.Float foldedCorner;

    private static float calloutWidth = 8;

    public Annotation() {

        super(GlyphType.ANNOTATION);
        offset = 100;

        update();
    }

    public AbstractGlyph getTarget() {
        return target_;
    }

    public void setTarget(AbstractGlyph target) {
        this.target_ = target;

        if (target != null) {

            target.setAnnotation(this);
            calloutPoint = new CalloutPoint(target.getCenterX(), target.getCenterY(), this);

        } else {

            calloutPoint = null;

        }
    }

    public CalloutPoint getCalloutPoint() {
        return calloutPoint;
    }

    public Path2D.Float getFoldedCorner() {
        return foldedCorner;
    }

    @Override
    protected void initializeLabel() {
        label_ = new Label(this, "");
    }

    @Override
    protected void initializePorts() {
        ports_ = new ArrayList<Port>();
    }

    @Override
    protected void setPortCoordinates() {
    }

    @Override
    protected void initializeShape() {
        shape_ = new Path2D.Float();
        foldedCorner = new Path2D.Float();
    }

    @Override
    protected void setShapeCoordinates() {
        Path2D.Float path = (Path2D.Float) shape_;

        if (calloutPoint == null) {

            path.reset();

            path.moveTo(getMinX(), getMinY());
            path.lineTo(getMaxX() - SLANT, getMinY());
            path.lineTo(getMaxX(), getMinY() + SLANT);
            path.lineTo(getMaxX(), getMaxY());
            path.lineTo(getMinX(), getMaxY());

            path.closePath();

        } else {
            createShapeWithCallout(path);
        }


        foldedCorner.reset();

        foldedCorner.moveTo(getMaxX() - SLANT, getMinY());
        foldedCorner.lineTo(getMaxX(), getMinY() + SLANT);
        foldedCorner.lineTo(getMaxX() - SLANT, getMinY() + SLANT);

        foldedCorner.closePath();


    }

    private void createShapeWithCallout(Path2D.Float shape) {
        if (calloutPoint == null)
            return;

        ArrayList<Point2D.Float> pointsOnBoundary = getTwoPointsOnBoundary();

        if (pointsOnBoundary == null) {

            shape.reset();
            shape.moveTo(getMinX(), getMinY());
            shape.lineTo(getMaxX() - SLANT, getMinY());
            shape.lineTo(getMaxX(), getMinY() + SLANT);
            shape.lineTo(getMaxX(), getMaxY());
            shape.lineTo(getMinX(), getMaxY());
            shape.closePath();
            return;

        }

        if (onSameSide(pointsOnBoundary.get(0), pointsOnBoundary.get(1))) {

            if (onTopSide(pointsOnBoundary.get(0))) {

                shape.reset();
                shape.moveTo(getMinX(), getMinY());
                shape.lineTo(pointsOnBoundary.get(0).x, getMinY());
                shape.lineTo(calloutPoint.getCenterX(), calloutPoint.getCenterY());
                shape.lineTo(pointsOnBoundary.get(1).x, getMinY());
                shape.lineTo(getMaxX() - SLANT, getMinY());
                shape.lineTo(getMaxX(), getMinY() + SLANT);
                shape.lineTo(getMaxX(), getMaxY());
                shape.lineTo(getMinX(), getMaxY());
                shape.closePath();

            } else if (onRightSide(pointsOnBoundary.get(0))) {

                shape.reset();
                shape.moveTo(getMinX(), getMinY());
                shape.lineTo(getMaxX() - SLANT, getMinY());
                shape.lineTo(getMaxX(), getMinY() + SLANT);
                shape.lineTo(getMaxX(), pointsOnBoundary.get(0).y);
                shape.lineTo(calloutPoint.getCenterX(), calloutPoint.getCenterY());
                shape.lineTo(getMaxX(), pointsOnBoundary.get(1).y);
                shape.lineTo(getMaxX(), getMaxY());
                shape.lineTo(getMinX(), getMaxY());
                shape.closePath();

            } else if (onBottomSide(pointsOnBoundary.get(0))) {

                shape.reset();
                shape.moveTo(getMinX(), getMinY());
                shape.lineTo(getMaxX() - SLANT, getMinY());
                shape.lineTo(getMaxX(), getMinY() + SLANT);
                shape.lineTo(getMaxX(), getMaxY());
                shape.lineTo(pointsOnBoundary.get(1).x, getMaxY());
                shape.lineTo(calloutPoint.getCenterX(), calloutPoint.getCenterY());
                shape.lineTo(pointsOnBoundary.get(0).x, getMaxY());
                shape.lineTo(getMinX(), getMaxY());
                shape.closePath();

            } else if (onLeftSide(pointsOnBoundary.get(0))) {

                shape.reset();
                shape.moveTo(getMinX(), getMinY());
                shape.lineTo(getMaxX() - SLANT, getMinY());
                shape.lineTo(getMaxX(), getMinY() + SLANT);
                shape.lineTo(getMaxX(), getMaxY());
                shape.lineTo(getMinX(), getMaxY());
                shape.lineTo(getMinX(), pointsOnBoundary.get(1).y);
                shape.lineTo(calloutPoint.getCenterX(), calloutPoint.getCenterY());
                shape.lineTo(getMinX(), pointsOnBoundary.get(0).y);
                shape.closePath();

            }

        } else {

            if (onTopSide(pointsOnBoundary.get(0))) {

                shape.reset();
                shape.moveTo(getMinX(), getMinY());
                shape.lineTo(pointsOnBoundary.get(0).x, getMinY());
                shape.lineTo(calloutPoint.getCenterX(), calloutPoint.getCenterY());
                shape.lineTo(getMaxX(), pointsOnBoundary.get(1).y);
                shape.lineTo(getMaxX(), getMaxY());
                shape.lineTo(getMinX(), getMaxY());
                shape.closePath();

            } else if (onBottomSide(pointsOnBoundary.get(0))) {

                shape.reset();
                shape.moveTo(getMinX(), getMinY());
                shape.lineTo(getMaxX() - SLANT, getMinY());
                shape.lineTo(getMaxX(), getMinY() + SLANT);
                shape.lineTo(getMaxX(), pointsOnBoundary.get(1).y);
                shape.lineTo(calloutPoint.getCenterX(), calloutPoint.getCenterY());
                shape.lineTo(pointsOnBoundary.get(0).x, getMaxY());
                shape.lineTo(getMinX(), getMaxY());
                shape.closePath();

            } else if (onLeftSide(pointsOnBoundary.get(0))) {

                if (onBottomSide(pointsOnBoundary.get(1))) {

                    shape.reset();
                    shape.moveTo(getMinX(), getMinY());
                    shape.lineTo(getMaxX() - SLANT, getMinY());
                    shape.lineTo(getMaxX(), getMinY() + SLANT);
                    shape.lineTo(getMaxX(), getMaxY());
                    shape.lineTo(pointsOnBoundary.get(1).x, getMaxY());
                    shape.lineTo(calloutPoint.getCenterX(), calloutPoint.getCenterY());
                    shape.lineTo(getMinX(), pointsOnBoundary.get(0).y);
                    shape.closePath();

                } else {

                    shape.reset();
                    shape.moveTo(getMinX(), pointsOnBoundary.get(0).y);
                    shape.lineTo(calloutPoint.getCenterX(), calloutPoint.getCenterY());
                    shape.lineTo(pointsOnBoundary.get(1).x, getMinY());
                    shape.lineTo(getMaxX() - SLANT, getMinY());
                    shape.lineTo(getMaxX(), getMinY() + SLANT);
                    shape.lineTo(getMaxX(), getMaxY());
                    shape.lineTo(getMinX(), getMaxY());
                    shape.closePath();

                }

            }

        }

    }

    private boolean onLeftSide(Point2D.Float p) {
        if (p == null)
            return false;

        return Math.abs(p.x - getMinX()) < EPS;
    }

    private boolean onRightSide(Point2D.Float p) {
        if (p == null)
            return false;

        return Math.abs(p.x - getMaxX()) < EPS;
    }

    private boolean onTopSide(Point2D.Float p) {
        if (p == null)
            return false;

        return Math.abs(p.y - getMinY()) < EPS;
    }

    private boolean onBottomSide(Point2D.Float p) {
        if (p == null)
            return false;

        return Math.abs(p.y - getMaxY()) < EPS;
    }

    private boolean onSameSide(Point2D.Float p1, Point2D.Float p2) {
        if (p1 == null || p2 == null)
            return false;

        return Math.abs(p1.x - p2.x) < EPS || Math.abs(p1.y - p2.y) < EPS;
    }

    private ArrayList<Point2D.Float> getTwoPointsOnBoundary() {

        Point2D.Float center1, center2;

        if (calloutPoint.getMaxY() < getMinY() || calloutPoint.getMinY() > getMaxY()) {

            center1 = new Point2D.Float(getCenterX() - calloutWidth / 2.0f, getCenterY());
            center2 = new Point2D.Float(getCenterX() + calloutWidth / 2.0f, getCenterY());

        } else {

            center1 = new Point2D.Float(getCenterX(), getCenterY() - calloutWidth / 2.0f);
            center2 = new Point2D.Float(getCenterX(), getCenterY() + calloutWidth / 2.0f);

        }

        ArrayList<Point2D.Float> results = new ArrayList<Point2D.Float>(2);

        Point2D.Float p1 = getIntersectionPoint(center1);
        Point2D.Float p2 = getIntersectionPoint(center2);

        if (p1 == null || p2 == null)
            return null;

        //Sort point first by x then y
        if (Math.abs(p1.x - p2.x) < EPS) {

            if (p1.y < p2.y) {

                results.add(p1);
                results.add(p2);

            } else {

                results.add(p2);
                results.add(p1);

            }

        } else {

            if (p1.x < p2.x) {

                results.add(p1);
                results.add(p2);

            } else {

                results.add(p2);
                results.add(p1);

            }

        }

        return results;

    }

    private Point2D.Float getIntersectionPoint(Point2D.Float center) {

        Line2D.Float line = new Line2D.Float(center.x, center.y, calloutPoint.getCenterX(), calloutPoint.getCenterY());
        return getIntersectionPoints(line, boundary_);

    }


    private Point2D.Float getIntersectionPoints(Line2D.Float line, Rectangle2D.Float rectangle) {
        // Top line
        Line2D.Float topLine = new Line2D.Float(rectangle.x, rectangle.y, rectangle.x + rectangle.width, rectangle.y);
        if (line.intersectsLine(topLine))
            return getIntersectionPoint(line, topLine);

        // Bottom line
        Line2D.Float bottomLine = new Line2D.Float(rectangle.x, rectangle.y + rectangle.height,
                rectangle.x + rectangle.width, rectangle.y + rectangle.height);
        if (line.intersectsLine(bottomLine))
            return getIntersectionPoint(line, bottomLine);

        // Left side
        Line2D.Float leftLine = new Line2D.Float(rectangle.x, rectangle.y, rectangle.x, rectangle.y + rectangle.height);
        if (line.intersectsLine(leftLine))
            return getIntersectionPoint(line, leftLine);

        // Right side
        Line2D.Float rightLine = new Line2D.Float(rectangle.x + rectangle.width, rectangle.y,
                rectangle.x + rectangle.width, rectangle.y + rectangle.height);
        if (line.intersectsLine(rightLine))
            return getIntersectionPoint(line, rightLine);

        return null;
    }

    private Point2D.Float getIntersectionPoint(Line2D.Float lineA, Line2D.Float lineB) {

        double x1 = lineA.x1;
        double y1 = lineA.y1;
        double x2 = lineA.x2;
        double y2 = lineA.y2;

        double x3 = lineB.x1;
        double y3 = lineB.y1;
        double x4 = lineB.x2;
        double y4 = lineB.y2;

        double d = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        double xi = ((x3 - x4) * (x1 * y2 - y1 * x2) - (x1 - x2) * (x3 * y4 - y3 * x4)) / d;
        double yi = ((y3 - y4) * (x1 * y2 - y1 * x2) - (y1 - y2) * (x3 * y4 - y3 * x4)) / d;


        return new Point2D.Float((float) xi, (float) yi);
    }

}