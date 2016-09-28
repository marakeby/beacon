package edu.vt.beacon.graph.glyph.node.auxiliary;

import edu.vt.beacon.graph.glyph.GlyphType;
import edu.vt.beacon.graph.glyph.node.AbstractNode;

import java.awt.geom.Ellipse2D;

public class SimpleChemicalUnit extends AuxiliaryUnit {

    public final static int OFFSET = 10;

    public SimpleChemicalUnit() {

        super(GlyphType.SIMPLE_CHEMICAL_UNIT);
        update();

        setOffset(OFFSET);
    }

    @Override
    protected void initializeLabel() {
        label_ = new Label(this, "");
    }

    @Override
    protected void initializeShape() {
        shape_ = new Ellipse2D.Float();
    }

    @Override
    public void updateShapeCoordinates(AbstractNode node) {

        Ellipse2D.Float ellipse = (Ellipse2D.Float) shape_;
        float radius = Math.max(getWidth(), getHeight());
        ellipse.setFrame(node.getMinX() + getMinX() + getOffset() - getPadding() - lineWidth_,
                node.getMinY() + getMinY() - radius / 2 - lineWidth_,
                radius + 2 * lineWidth_, radius + 2 * lineWidth_);

    }

}