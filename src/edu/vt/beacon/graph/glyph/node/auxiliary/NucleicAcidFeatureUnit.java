package edu.vt.beacon.graph.glyph.node.auxiliary;

import edu.vt.beacon.graph.glyph.GlyphType;
import edu.vt.beacon.graph.glyph.node.AbstractNode;

import java.awt.geom.Path2D;

public class NucleicAcidFeatureUnit extends AuxiliaryUnit {

    public final static int SLANT = 6;
    public final static int OFFSET = 10;

    public NucleicAcidFeatureUnit() {

        super(GlyphType.NUCLEIC_ACID_FEATURE_UNIT);
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

        path.moveTo(minX, minY);
        path.lineTo(maxX, minY);
        path.lineTo(maxX, maxY - SLANT);
        path.quadTo(maxX, maxY, maxX - SLANT, maxY);
        path.lineTo(minX + SLANT, maxY);
        path.quadTo(minX, maxY, minX, maxY - SLANT);

        path.closePath();

    }

}