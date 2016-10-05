package edu.vt.beacon.graph.glyph.node.auxiliary;

import edu.vt.beacon.graph.glyph.GlyphType;
import edu.vt.beacon.graph.glyph.node.AbstractNode;
import edu.vt.beacon.graph.glyph.node.container.Compartment;

import java.awt.geom.Rectangle2D;

public class CompartmentUnit extends AuxiliaryUnit {

    public CompartmentUnit() {

        super(GlyphType.COMPARTMENT_UNIT);
        update();

        setOffset(Compartment.CORNER_ROUNDNESS / 2);
    }

    @Override
    protected void initializeLabel() {
        label_ = new Label(this, "");
    }

    @Override
    protected void initializeShape() {
        shape_ = new Rectangle2D.Float();

    }

    @Override
    public void updateShapeCoordinates(AbstractNode node) {

        Rectangle2D.Float rectangle = (Rectangle2D.Float) shape_;
        rectangle.setRect(node.getMinX() + getMinX() + getOffset() - getPadding() - lineWidth_,
                node.getMinY() + getMinY() - getHeight() / 2 - lineWidth_,
                getWidth() + 2 * lineWidth_, getHeight() + 2 * lineWidth_);

    }

}