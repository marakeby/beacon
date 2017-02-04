package edu.vt.beacon.graph.glyph.arc;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import edu.vt.beacon.graph.glyph.GlyphType;
import edu.vt.beacon.graph.glyph.node.auxiliary.Port;
import edu.vt.beacon.util.ArcManager;

public class NecessaryStimulation extends AbstractArc
{
    /*
     * document constructor
     */
    public NecessaryStimulation()
    {
        super (GlyphType.NECESSARY_STIMULATION);
        
        update();
    }
    
    /*
     * document method
     */
    private void setEndCapCoordinates()
    {
        endCap_.reset();
        
        float cap = getEndCapSize();
        Point2D.Float targetPoint = points_.get(points_.size() - 1);
        
        endCap_.moveTo(targetPoint.x, targetPoint.y);
        endCap_.lineTo(targetPoint.x - cap, targetPoint.y - cap);
        endCap_.lineTo(targetPoint.x - cap, targetPoint.y + cap);
        
        endCap_.closePath();
        
        endCap_.moveTo(targetPoint.x - cap - lineWidth_ * 2.0F,
                       targetPoint.y - 1.2 * cap);
//        endCap_.lineTo(targetPoint.x - cap - lineWidth_ * 2.0F,
//                       targetPoint.y - lineWidth_ / 2.0F);
//        endCap_.moveTo(targetPoint.x - cap - lineWidth_ * 2.0F,
//                       targetPoint.y + lineWidth_ / 2.0F);
        endCap_.lineTo(targetPoint.x - cap - lineWidth_ * 2.0F,
                       targetPoint.y + 1.2 * cap);
    }

    @Override
    public boolean isValidSource(Port port) {

        if (port == null || port.getParent() == null || port.getParent().getType() == null)
            return false;

        GlyphType type = port.getParent().getType();

        if (type == GlyphType.BIOLOGICAL_ACTIVITY)
            return true;

        else if (type == GlyphType.PHENOTYPE || type == GlyphType.TAG || type == GlyphType.SUBMAP)
            return false;

        for (int portIndex = 0; portIndex < port.getParent().getPortCount(); portIndex++)
            for (int arcIndex = 0; arcIndex < port.getParent().getPortAt(portIndex).getArcCount(); arcIndex++)
                if (port.getParent().getPortAt(portIndex).getArcAt(arcIndex).getSourcePort() == port.getParent().getPortAt(portIndex) &&
                        port.getParent().getPortAt(portIndex).getArcAt(arcIndex) != this &&
                        port.getParent().getPortAt(portIndex).getArcAt(arcIndex) instanceof PositiveInfluence)
                    return false;

        return true;

    }

    @Override
    public boolean isValidTarget(Port port) {

        if (port == null || port.getParent() == null || port.getParent().getType() == null)
            return false;

        GlyphType type = port.getParent().getType();

        return type == GlyphType.BIOLOGICAL_ACTIVITY || type == GlyphType.PHENOTYPE;

    }

    /*
         * document method
         */
    @Override
    protected void setShapeCoordinates()
    {
        Point2D.Float sourcePoint = points_.get(0);
        Point2D.Float targetPoint = points_.get(points_.size() - 1);
        
        ArcManager.setPointCoordinates(targetPoint, sourcePoint);
        
        for (int i = 0; i < Math.floor(lineWidth_ / 2.0F); i++)
            ArcManager.next();
        
        Path2D.Float path = (Path2D.Float) shape_;
        path.reset();
        
        path.moveTo(points_.get(0).x, points_.get(0).y);
        
        for (int i = 1; i < points_.size() - 1; i++)
            path.lineTo(points_.get(i).x, points_.get(i).y);
        
        path.lineTo(ArcManager.getCurrentPoint().x,
                    ArcManager.getCurrentPoint().y);
        
        setEndCapCoordinates();
    }
}