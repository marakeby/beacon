package edu.vt.beacon.graph;

import edu.vt.beacon.util.IdGenerator;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public abstract class AbstractEntity
{
    private String id;

    protected Rectangle2D.Float boundary_;
    
    protected Shape shape_;

    private boolean isSelected_;

    // TODO document constructor
    protected AbstractEntity()
    {
        setId(IdGenerator.generate());

        boundary_ = new Rectangle2D.Float();
        
        initializeShape();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // TODO document method
    public boolean contains(Point2D.Float point)
    {
        return intersects(point);
    }

    // use this to find if a point is close enough to the center of this Entity
    public boolean closeTo(Point2D.Float point, float offset)
    {
        System.out.println(this.boundary_);
        Rectangle2D.Float rec = this.boundary_;
        Point2D.Float center = new Point2D.Float((float)rec.getCenterX(),(float) rec.getCenterY());

        return ( center.distance(point) <= offset) ;

    }

    // TODO document method
    public Rectangle2D.Float getBoundary()
    {
        return boundary_;
    }
    
    // TODO document method
    public float getCenterX()
    {
        return (float) boundary_.getCenterX();
    }
    
    // TODO document method
    public float getCenterY()
    {
        return (float) boundary_.getCenterY();
    }
    
    // TODO document method
    public float getHeight()
    {
        return (float) boundary_.getHeight();
    }
    
    // TODO document method
    public float getMaxX()
    {
        return (float) boundary_.getMaxX();
    }
    
    // TODO document method
    public float getMaxY()
    {
        return (float) boundary_.getMaxY();
    }
    
    // TODO document method
    public float getMinX()
    {
        return (float) boundary_.getMinX();
    }
    
    // TODO document method
    public float getMinY()
    {
        return (float) boundary_.getMinY();
    }
    
    // TODO document method
    public Shape getShape()
    {
        return shape_;
    }
    
    // TODO document method
    public float getWidth()
    {
        return (float) boundary_.getWidth();
    }
    
    // TODO document method
    protected abstract void initializeShape();
    
    // TODO document method
    public boolean intersects(Point2D.Float point)
    {
        return shape_.contains(point);
    }

    // TODO document method
    public void move(float deltaX, float deltaY)
    {
        boundary_.x += deltaX;
        boundary_.y += deltaY;
        
        setShapeCoordinates();
    }
    
    // TODO document method
    protected abstract void setBoundary();
    
    // TODO document method
    public void setCenter(Point2D.Float point)
    {
        setCenter(point.x, point.y);
    }
    
    // TODO document method
    public void setCenter(float x, float y)
    {
        move(x - getCenterX(), y - getCenterY());
    }

    public boolean isSelected() {
        return isSelected_;
    }

    public void setSelected(boolean selected) {
        isSelected_ = selected;
    }

    // TODO document method
    public void setHeight(float height)
    {
        boundary_.height = height;
        
        update();
    }
    
    // TODO document method
    protected abstract void setShapeCoordinates();
    
    // TODO document method
    public void setWidth(float width)
    {
        boundary_.width = width;
        
        update();
    }
    
    // TODO document method
    public void update()
    {
        setBoundary();
        setShapeCoordinates();
    }
}