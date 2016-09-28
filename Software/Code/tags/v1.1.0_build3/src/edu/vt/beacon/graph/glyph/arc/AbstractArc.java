package edu.vt.beacon.graph.glyph.arc;

import edu.vt.beacon.graph.glyph.AbstractGlyph;
import edu.vt.beacon.graph.glyph.GlyphType;
import edu.vt.beacon.graph.glyph.auxiliary.Bound;
import edu.vt.beacon.graph.glyph.auxiliary.BoundType;
import edu.vt.beacon.graph.glyph.node.AbstractNode;
import edu.vt.beacon.graph.glyph.node.annotation.CalloutPoint;
import edu.vt.beacon.graph.glyph.node.auxiliary.Port;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public abstract class AbstractArc extends AbstractGlyph {
    protected ArrayList<Point2D.Float> points_;

    protected Path2D.Float endCap_;

    private Port sourcePort_;

    private Port targetPort_;

    // FIXME complete constructor
    protected AbstractArc(GlyphType type) {
        super(type);

        endCap_ = new Path2D.Float();

        initializePoints();
    }

    // TODO document method
    @Override
    public AbstractArc copy() {
        AbstractArc arc = (AbstractArc) super.copy();
        arc.bounds_.clear();
        arc.points_.clear();

        for (Point2D.Float point : points_) {

            arc.bounds_.add(new Bound(BoundType.POINT));
            arc.points_.add(new Point2D.Float(point.x, point.y));
        }

        arc.update();

        return arc;
    }

    // TODO document method
    @Override
    public void delete() {
        if (sourcePort_ != null)
            sourcePort_.remove(this);

        if (targetPort_ != null)
            targetPort_.remove(this);

        super.delete();
    }

    /*
     * document
     */
    public Path2D.Float getEndCap() {
        return endCap_;
    }

    // FIXME complete method
    protected float getEndCapSize() {
        return lineWidth_ + padding_ / 4.0F;
    }

    // TODO document method
    public Point2D.Float getPointAt(int index) {
        return points_.get(index);
    }

    public ArrayList<Point2D.Float> getPoints() {
        return points_;
    }

    // TODO document method
    public int getPointCount() {
        return points_.size();
    }

    // TODO document method
    public AbstractNode getSource() {
        return (sourcePort_ != null) ? sourcePort_.getParent() : null;
    }

    // TODO document method
    public Port getSourcePort() {
        return sourcePort_;
    }

    // TODO document method
    public AbstractNode getTarget() {
        return (targetPort_ != null) ? targetPort_.getParent() : null;
    }

    // TODO document method
    public Port getTargetPort() {
        return targetPort_;
    }

    // TODO document method
    @Override
    protected void initializeBounds() {
        bounds_ = new ArrayList<Bound>();
        bounds_.add(new Bound(BoundType.POINT));
        bounds_.add(new Bound(BoundType.POINT));
    }

    // FIXME complete method
    private void initializePoints() {
        points_ = new ArrayList<Point2D.Float>();
        points_.add(new Point2D.Float());
        points_.add(new Point2D.Float(100, 0));
    }

    // TODO document method
    @Override
    protected void initializeShape() {
        shape_ = new Path2D.Float();
    }

    // TODO document method
    @Override
    public boolean intersects(Point2D.Float point) {
        Line2D.Float segment = new Line2D.Float();

        for (int i = 0; i < points_.size() - 1; i++) {

            segment.setLine(points_.get(i), points_.get(i + 1));

            if (segment.ptSegDist(point) <= Math.max(lineWidth_ * 1.1, 10))

                return true;
        }

        return false;
    }

    // FIXME complete method
    public abstract boolean isValidSource(Port port);

    // FIXME complete method
    public abstract boolean isValidTarget(Port port);


    public void move_without_dependents(float deltaX, float deltaY){
        move_self(deltaX, deltaY);
    }


    private void move_dependents(float deltaX, float deltaY){
//        System.out.println("moving arc without dependents" + deltaX + " " +  deltaY);
        if (getSource() != null)
            getSource().move(deltaX, deltaY);

        else {

            points_.get(0).x += deltaX;
            points_.get(0).y += deltaY;

        }

        if (getTarget() != null)
            getTarget().move(deltaX, deltaY);

        else {

            points_.get(points_.size() - 1).x += deltaX;
            points_.get(points_.size() - 1).y += deltaY;

        }
    }


    private void move_self(float deltaX, float deltaY){
        for (int i = 1; i < (points_.size() - 1); i++) {
//            System.out.println("moving point on arc " + i + "  deltaX " + deltaX + "deltaY " +  deltaY);
            points_.get(i).x += deltaX;
            points_.get(i).y += deltaY;
        }

        update();

        if (getAnnotation() != null && getAnnotation().getCalloutPoint() != null) {

            CalloutPoint calloutPoint = getAnnotation().getCalloutPoint();
            calloutPoint.move(deltaX, deltaY);

        }
    }

    // TODO document method
    @Override
    public void move(float deltaX, float deltaY) {
//        System.out.println("moving arc " + deltaX + " " +  deltaY);
        move_dependents(deltaX, deltaY);
        move_self(deltaX, deltaY);

    }

    // TODO document method
    @Override
    protected void setBoundary() {
        float maxX = points_.get(0).x;
        float maxY = points_.get(0).y;
        float minX = points_.get(0).x;
        float minY = points_.get(0).y;

        for (int i = 1; i < points_.size(); i++) {

            maxX = Math.max(maxX, points_.get(i).x);
            maxY = Math.max(maxY, points_.get(i).y);
            minX = Math.min(minX, points_.get(i).x);
            minY = Math.min(minY, points_.get(i).y);
        }

        boundary_.height = Math.max(maxY - minY, 1.0F);
        boundary_.width = Math.max(maxX - minX, 1.0F);
        boundary_.x = minX;
        boundary_.y = minY;

        super.setBoundary();
    }

    // TODO document method
    @Override
    protected void setBoundCoordinates() {
        for (int i = 0; i < bounds_.size(); i++)
            bounds_.get(i).setCenter(points_.get(i));
    }

    // TODO document method
    @Override
    protected void setShapeCoordinates() {
        Path2D.Float path = (Path2D.Float) shape_;
        path.reset();

        path.moveTo(points_.get(0).x, points_.get(0).y);

        for (int i = 1; i < points_.size(); i++)
            path.lineTo(points_.get(i).x, points_.get(i).y);
    }

    // FIXME complete method
    private void setSourcePoint() {
        if (sourcePort_ == null)

            return;

        Point2D.Float sourcePoint = points_.get(0);
        sourcePoint.x = sourcePort_.getCenterX();
        sourcePoint.y = sourcePort_.getCenterY();
    }

    // TODO document method
    public void setSourcePort(Port sourcePort) {
        if (sourcePort_ != null)
            sourcePort_.remove(this);

        if (sourcePort != null)
            sourcePort.add(this);

        sourcePort_ = sourcePort;

        update();
    }

    // FIXME complete method
    private void setTargetPoint() {
        if (targetPort_ == null)

            return;

        Point2D.Float targetPoint = points_.get(points_.size() - 1);
        targetPoint.x = targetPort_.getCenterX();
        targetPoint.y = targetPort_.getCenterY();
    }

    // TODO document method
    public void setTargetPort(Port targetPort) {
        if (targetPort_ != null)
            targetPort_.remove(this);

        if (targetPort != null)
            targetPort.add(this);

        targetPort_ = targetPort;

        update();
    }

    public void resetTargetPoint() {
        points_.remove(points_.size() - 1);
    }

    public void resetSourcePoint() {
        points_.remove(0);
    }

    // TODO document method
    @Override
    public void update() {
        setSourcePoint();
        setTargetPoint();

        super.update();
    }

    public boolean addPoint(Point2D.Float point) {
        if (point == null)
            return false;

        // create the current edge segment line
        Line2D.Float segment = new Line2D.Float();

        // flag that the given point is not added
        boolean pointAdded = false;

        // for each edge segment
        for (int i = 0; i < points_.size() - 1; i++) {

            // set the current edge segment
            segment.setLine(points_.get(i).x, points_.get(i).y,
                    points_.get(i + 1).x, points_.get(i + 1).y);

            // if the segment intersects the given point
            if (segment.ptSegDist(point) <= lineWidth_ / 2.0) {

                // add the point in the current edge segment
                points_.add(i + 1, getBestPoint(point, segment));

                // flag that the given point is added
                pointAdded = true;

                // break from the loop
                break;
            }
        }

        // if the given point is not added
        if (!pointAdded)
            // return that the point is not added
            return false;

        // add a new point box for the point
//        pointBoxes.add(new BoundBox());

        incrementBounds();
        update();
        // return that the point is added
        return true;
    }

    public boolean incrementBounds() {
        return bounds_.add(new Bound(BoundType.POINT));
    }

    public boolean removePoint(Point2D.Float point) {
        if (point == null)
            return false;

        boolean pointRemoved = points_.remove(point);

        if (!pointRemoved)
            return false;

        bounds_.remove(0);
        update();
        // return that the point is added
        return true;
    }

    private Point2D.Float getBestPoint(Point2D.Float point, Line2D.Float line) {
        if (point == null)
            return null;

        double minDist = Double.MAX_VALUE;
        Point2D.Float bestPoint = new Point2D.Float(point.x, point.y);

        for (float i = -lineWidth_; i <= lineWidth_; i++)
            for (float j = -lineWidth_; j <= lineWidth_; j++) {
                Point2D.Float p = new Point2D.Float(point.x + i, point.y + j);
                double d = line.ptSegDist(p);
                if (d < minDist) {
                    bestPoint = p;
                    minDist = d;
                }
            }

        return bestPoint;
    }
}