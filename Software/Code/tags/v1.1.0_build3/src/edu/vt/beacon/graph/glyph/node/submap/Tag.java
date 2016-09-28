package edu.vt.beacon.graph.glyph.node.submap;

import edu.vt.beacon.graph.glyph.GlyphType;
import edu.vt.beacon.graph.glyph.node.auxiliary.Port;
import edu.vt.beacon.graph.glyph.node.auxiliary.PortType;

import java.util.ArrayList;

public class Tag extends AbstractTagTerminal {

    public Tag(Submap parent) {
        super(GlyphType.TAG, parent);
    }

    @Override
    protected void initializePorts() {
        ports_ = new ArrayList<Port>();

        if (getOrientation() != null) {

            switch (getOrientation()) {
                case RIGHT:
                    ports_.add(new Port(this, PortType.LEFT));
                    ports_.add(new Port(this, PortType.RIGHT));
                    break;

                case LEFT:
                    ports_.add(new Port(this, PortType.RIGHT));
                    ports_.add(new Port(this, PortType.LEFT));
                    break;

                case UP:
                    ports_.add(new Port(this, PortType.BOTTOM));
                    ports_.add(new Port(this, PortType.TOP));
                    break;

                case DOWN:
                    ports_.add(new Port(this, PortType.TOP));
                    ports_.add(new Port(this, PortType.BOTTOM));
                    break;
            }

        } else {

            ports_.add(new Port(this, PortType.LEFT));
            ports_.add(new Port(this, PortType.RIGHT));

        }
    }

    @Override
    protected void setPortCoordinates() {

        if (getOrientation() != null) {

            switch (getOrientation()) {
                case RIGHT:
                    ports_.get(0).setCenter(getMinX(), getCenterY());
                    ports_.get(1).setCenter(getMaxX(), getCenterY());
                    break;

                case LEFT:
                    ports_.get(0).setCenter(getMaxX(), getCenterY());
                    ports_.get(1).setCenter(getMinX(), getCenterY());
                    break;

                case UP:
                    ports_.get(0).setCenter(getCenterX(), getMaxY());
                    ports_.get(1).setCenter(getCenterX(), getMinY());
                    break;

                case DOWN:
                    ports_.get(0).setCenter(getCenterX(), getMinY());
                    ports_.get(1).setCenter(getCenterX(), getMaxY());
                    break;
            }

        } else {

            ports_.get(0).setCenter(getMinX(), getCenterY());
            ports_.get(1).setCenter(getMaxX(), getCenterY());

        }
    }

}