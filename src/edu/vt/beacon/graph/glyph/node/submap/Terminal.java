package edu.vt.beacon.graph.glyph.node.submap;

import edu.vt.beacon.graph.OrientationType;
import edu.vt.beacon.graph.glyph.GlyphType;
import edu.vt.beacon.graph.glyph.node.auxiliary.Port;

import java.util.ArrayList;

/**
 * Created by ppws on 4/10/16.
 */
public class Terminal extends AbstractTagTerminal {

    public Terminal(Submap parent) {
        super(GlyphType.TERMINAL, parent);
    }

    @Override
    public void move(float deltaX, float deltaY) {

        if (getOrientation() == OrientationType.DOWN || getOrientation() == OrientationType.UP) {

            if (getOrientation() == OrientationType.DOWN)
                deltaY = getParent().getMinY() - getMinY();

            else if (getOrientation() == OrientationType.UP)
                deltaY = getParent().getMaxY() - getMaxY();

            if (deltaX < 0 && (getMinX() + deltaX) < getParent().getMinX())
                deltaX = getParent().getMinX() - getMinX();

            else if (deltaX > 0 && (getMaxX() + deltaX) > getParent().getMaxX())
                deltaX = getParent().getMaxX() - getMaxX();

        } else if (getOrientation() == OrientationType.RIGHT || getOrientation() == OrientationType.LEFT) {

            if (getOrientation() == OrientationType.RIGHT)
                deltaX = getParent().getMinX() - getMinX();

            else if (getOrientation() == OrientationType.LEFT)
                deltaX = getParent().getMaxX() - getMaxX();

            if (deltaY < 0 && (getMinY() + deltaY) < getParent().getMinY())
                deltaY = getParent().getMinY() - getMinY();

            else if (deltaY > 0 && (getMaxY() + deltaY) > getParent().getMaxY())
                deltaY = getParent().getMaxY() - getMaxY();

        }

        super.move(deltaX, deltaY);
        getParent().updatePortsLocation();
    }

    public void moveIntoSubmap(float deltaX, float deltaY) {

        super.move(deltaX, deltaY);
        move(0, 0);

    }

    @Override
    protected void initializePorts() {
        ports_ = new ArrayList<Port>();
    }

    @Override
    protected void setPortCoordinates() {
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height);
        if (getParent() != null)
            getParent().updatePortsLocation();
    }

    @Override
    public void setWidth(float width) {
        super.setWidth(width);
        if (getParent() != null)
            getParent().updatePortsLocation();
    }
}
