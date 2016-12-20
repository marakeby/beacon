package edu.vt.beacon.graph.glyph.node.auxiliary;

import edu.vt.beacon.graph.glyph.GlyphType;
import edu.vt.beacon.graph.glyph.node.AbstractNode;
import org.sbgn.GlyphClazz;

import java.awt.geom.Ellipse2D;

public class UnspecifiedEntityUnit extends AuxiliaryUnit {

    public final static int OFFSET = 10;

    public UnspecifiedEntityUnit() {

        super(GlyphType.UNSPECIFIED_ENTITY_UNIT);
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
        ellipse.setFrame(node.getMinX() + getMinX() + getOffset() - getPadding() - lineWidth_,
                node.getMinY() + getMinY() - getHeight() / 2 - lineWidth_,
                getWidth() + 2 * lineWidth_, getHeight() + 2 * lineWidth_);

    }

}