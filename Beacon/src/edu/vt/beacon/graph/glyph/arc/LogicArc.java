package edu.vt.beacon.graph.glyph.arc;

import edu.vt.beacon.graph.glyph.GlyphType;
import edu.vt.beacon.graph.glyph.node.auxiliary.Port;

public class LogicArc extends AbstractArc {
    /*
     * document constructor
     */
    public LogicArc() {
        super(GlyphType.LOGIC_ARC);

        update();
    }

    @Override
    public boolean isValidSource(Port port) {

        if (port == null || port.getParent() == null || port.getParent().getType() == null)
            return false;

        return port.getParent().getType() == GlyphType.BIOLOGICAL_ACTIVITY;

    }

    @Override
    public boolean isValidTarget(Port port) {

        if (port == null || port.getParent() == null || port.getParent().getType() == null)
            return false;

        GlyphType type = port.getParent().getType();

        if (type == GlyphType.AND || type == GlyphType.OR)
            return true;

        if (type == GlyphType.NOT || type == GlyphType.DELAY) {

            for (int portIndex = 0; portIndex < port.getParent().getPortCount(); portIndex++)
                for (int arcIndex = 0; arcIndex < port.getParent().getPortAt(portIndex).getArcCount(); arcIndex++)
                    if (port.getParent().getPortAt(portIndex).getArcAt(arcIndex).getTargetPort() == port.getParent().getPortAt(portIndex) &&
                            port.getParent().getPortAt(portIndex).getArcAt(arcIndex) != this &&
                            port.getParent().getPortAt(portIndex).getArcAt(arcIndex) instanceof LogicArc)
                        return false;

            return true;
        }

        return false;
    }
}