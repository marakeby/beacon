package edu.vt.beacon.graph.glyph.node.auxiliary;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.vt.beacon.graph.AbstractEntity;
import edu.vt.beacon.graph.glyph.arc.AbstractArc;
import edu.vt.beacon.graph.glyph.node.AbstractNode;

public class Port extends AbstractEntity
{
    private static float size_ = 10.0F;
    
    private AbstractNode parent_;
    
    private ArrayList<AbstractArc> arcs_;
    
    private PortType type_;
    
    // TODO document constructor
    public Port(AbstractNode node, PortType type)
    {
        parent_ = node;
        arcs_ = new ArrayList<AbstractArc>();
        type_ = type;
        
        update();
    }
    
    // TODO document method
    public void add(AbstractArc arc)
    {
        arcs_.add(arc);
    }
    
    // TODO document method
    public AbstractArc getArcAt(int index)
    {
        return arcs_.get(index);
    }
    
    public ArrayList<AbstractArc> getArcs()
    {
        return arcs_;
    }

    // TODO document method
    public int getArcCount()
    {
        return arcs_.size();
    }
    
    // TODO document method
    public AbstractNode getParent()
    {
        return parent_;
    }
    
    // TODO document method
    public PortType getType()
    {
        return type_;
    }
    
    // TODO document method
    @Override
    protected void initializeShape()
    {
        shape_ = new Path2D.Float();
    }
    
    // TODO document method
    @Override
    public boolean intersects(Point2D.Float point)
    {
        return boundary_.contains(point);
    }
    
    // TODO document method
    @Override
    public void move(float deltaX, float deltaY)
    {
        super.move(deltaX, deltaY);
        
        for (AbstractArc arc : arcs_)
            arc.update();
    }
    
    // TODO document method
    public void remove(AbstractArc arc)
    {
        arcs_.remove(arc);
    }
    
    // TODO document method
    @Override
    protected void setBoundary()
    {
        boundary_.width = size_;
        boundary_.height = size_;
    }
    
    // TODO document method
    @Override
    protected void setShapeCoordinates()
    {
        Path2D.Float path = (Path2D.Float) shape_;
        path.reset();
        
        path.moveTo(getMinX(), getMinY());
        path.lineTo(getMaxX(), getMaxY());
        path.moveTo(getMinX(), getMaxY());
        path.lineTo(getMaxX(), getMinY());
    }
    
    // TODO document method
    public void setType(PortType type)
    {
        type_ = type;
    }
}