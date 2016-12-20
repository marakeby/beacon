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

//        return port.getParent().getType() == GlyphType.BIOLOGICAL_ACTIVITY;
        GlyphType type = port.getParent().getType();

        if (type == GlyphType.BIOLOGICAL_ACTIVITY)
            return true;

        else if (type == GlyphType.PHENOTYPE || type == GlyphType.TAG || type == GlyphType.SUBMAP)
            return false;

//        Ther eis a contradiction in SBGN AF standard regarding the connection of logical arc (http://biecoll.ub.uni-bielefeld.de/volltexte/2015/5369/pdf/jib_265.pdf)
//        section 3.3.1 includes an incidence matrix that shoes that logical arc can connect only biological activites to logical operators.
//        section 2.8.5 says the logical arc can start from biological activity or any logical operator.
        if (type == GlyphType.NOT || type == GlyphType.DELAY || type == GlyphType.AND || type == GlyphType.OR) {
            for (int portIndex = 0; portIndex < port.getParent().getPortCount(); portIndex++)
                for (int arcIndex = 0; arcIndex < port.getParent().getPortAt(portIndex).getArcCount(); arcIndex++)
                    if (port.getParent().getPortAt(portIndex).getArcAt(arcIndex).getSourcePort() == port.getParent().getPortAt(portIndex) &&
                            port.getParent().getPortAt(portIndex).getArcAt(arcIndex) != this &&
                            port.getParent().getPortAt(portIndex).getArcAt(arcIndex) instanceof LogicArc)
                        return false;

            return true;
        }
        return false;
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