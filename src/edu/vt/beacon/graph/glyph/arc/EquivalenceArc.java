package edu.vt.beacon.graph.glyph.arc;

import edu.vt.beacon.graph.glyph.GlyphType;
import edu.vt.beacon.graph.glyph.node.auxiliary.Port;

public class EquivalenceArc extends AbstractArc {
    // FIXME complete constructor
    public EquivalenceArc() {
        super(GlyphType.EQUIVALENCE_ARC);

        update();
    }

    @Override
    public boolean isValidSource(Port port) {

        if (port == null || port.getParent() == null || port.getParent().getType() == null)
            return false;

        GlyphType type = port.getParent().getType();

        return type == GlyphType.BIOLOGICAL_ACTIVITY || type == GlyphType.PHENOTYPE;

    }

    @Override
    public boolean isValidTarget(Port port) {

        if (port == null || port.getParent() == null || port.getParent().getType() == null)
            return false;

        GlyphType type = port.getParent().getType();

        return type == GlyphType.TAG || type == GlyphType.SUBMAP;

    }
}