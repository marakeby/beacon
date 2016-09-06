package edu.vt.beacon.graph.glyph.node.auxiliary;

import edu.vt.beacon.graph.glyph.GlyphType;
import edu.vt.beacon.graph.glyph.node.AbstractNode;

import java.awt.geom.Path2D;

public class ComplexUnit extends AuxiliaryUnit {

    public final static int SLANT = 3;
    public final static int OFFSET = 10;

    public ComplexUnit() {

        super(GlyphType.COMPLEX_UNIT);
        update();

        setOffset(OFFSET);
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
        float maxX = minX + getWidth() + 2 * lineWidth_;
        float minY = node.getMinY() + getMinY() - getHeight() / 2 - lineWidth_;
        float maxY = minY + getHeight() + 2 * lineWidth_;

        path.moveTo(minX, minY + SLANT);
        path.lineTo(minX + SLANT, minY);
        path.lineTo(maxX - SLANT, minY);
        path.lineTo(maxX, minY + SLANT);
        path.lineTo(maxX, maxY - SLANT);
        path.lineTo(maxX - SLANT, maxY);
        path.lineTo(minX + SLANT, maxY);
        path.lineTo(minX, maxY - SLANT);

        path.closePath();

    }

}