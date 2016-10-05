package edu.vt.beacon.graph.glyph.node.auxiliary;

import edu.vt.beacon.graph.glyph.GlyphType;
import edu.vt.beacon.graph.glyph.node.AbstractNode;

import java.awt.geom.RoundRectangle2D;

public class MacromoleculeUnit extends AuxiliaryUnit {

    public final static int CORNER_ROUNDNESS = 10;

    public MacromoleculeUnit() {

        super(GlyphType.MACROMOLECULE_UNIT);
        update();

        setOffset(CORNER_ROUNDNESS);
    }

    @Override
    protected void initializeLabel() {
        label_ = new Label(this, "");
    }

    @Override
    protected void initializeShape() {
        shape_ = new RoundRectangle2D.Float();

    }

    @Override
    public void updateShapeCoordinates(AbstractNode node) {

        RoundRectangle2D.Float rectangle = (RoundRectangle2D.Float) shape_;
        rectangle.setRoundRect(node.getMinX() + getMinX() + getOffset() - getPadding() - lineWidth_,
                node.getMinY() + getMinY() - getHeight() / 2 - lineWidth_, getWidth() + 2 * lineWidth_,
                getHeight() + 2 * lineWidth_, CORNER_ROUNDNESS, CORNER_ROUNDNESS);

    }

}