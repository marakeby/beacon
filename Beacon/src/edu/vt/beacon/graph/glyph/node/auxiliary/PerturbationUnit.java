package edu.vt.beacon.graph.glyph.node.auxiliary;

import edu.vt.beacon.graph.glyph.GlyphType;
import edu.vt.beacon.graph.glyph.node.AbstractNode;

import java.awt.geom.Path2D;

public class PerturbationUnit extends AuxiliaryUnit {

    public final static int SLANT = 10;

    public PerturbationUnit() {

        super(GlyphType.PERTURBATION_UNIT);
        update();

        setOffset(SLANT);
        setTextOffset(10);
    }

    @Override
    protected void initializeLabel() {
        label_ = new Label(this, "");
    }

    @Override
    protected void initializeShape() {
        shape_ = new Path2D.Float();
    }

    @Override
    public void updateShapeCoordinates(AbstractNode node) {

        Path2D.Float path = (Path2D.Float) shape_;

        path.reset();

        float minX = node.getMinX() + getMinX() + getOffset() - getPadding() - lineWidth_;
        float maxX = minX + getWidth() + 2 * SLANT + 2 * lineWidth_;
        float minY = node.getMinY() + getMinY() - getHeight() / 2 - lineWidth_;
        float maxY = minY + getHeight() + 2 * lineWidth_;

        path.moveTo(minX, minY);
        path.lineTo(maxX, minY);
        path.lineTo(maxX - SLANT, node.getMinY());
        path.lineTo(maxX, maxY);
        path.lineTo(minX, maxY);
        path.lineTo(minX + SLANT, node.getMinY());

        path.closePath();


    }

}