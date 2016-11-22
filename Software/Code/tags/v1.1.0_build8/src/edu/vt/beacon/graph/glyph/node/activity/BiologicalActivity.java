package edu.vt.beacon.graph.glyph.node.activity;

import edu.vt.beacon.graph.glyph.GlyphType;
import edu.vt.beacon.graph.glyph.node.auxiliary.AuxiliaryUnit;
import edu.vt.beacon.graph.glyph.node.auxiliary.Label;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * Created by ppws on 1/8/16.
 */
public class BiologicalActivity extends AbstractActivity {

    public final static int OFFSET = 30;

    private AuxiliaryUnit auxiliaryUnit;

    /*
    * document constructor
    */
    public BiologicalActivity() {
        super(GlyphType.BIOLOGICAL_ACTIVITY);

        update();
    }

    /*
     * document method
     */
    @Override
    public float getMinWidth() {
        return super.getMinWidth() + getSlantSize() * 2;
    }

    /*
     * document method
     */
    private float getSlantSize() {
        return Math.max(getHeight(), getMinHeight()) / 4.0F;
    }

    /*
     * document method
     */
    @Override
    protected void initializeLabel() {
        label_ = new Label(this, "Activity");
    }

    /*
     * document method
     */
    @Override
    protected void initializeShape() {
        shape_ = new Rectangle2D.Float();
    }

    /*
     * document method
     */
    @Override
    protected void setShapeCoordinates() {
        Rectangle2D.Float rect = (Rectangle2D.Float) shape_;
        rect.setRect(getMinX(), getMinY(), getWidth(), getHeight());

        updatePorts();
    }

    public AuxiliaryUnit getAuxiliaryUnit() {
        return auxiliaryUnit;
    }

    public void setAuxiliaryUnit(AuxiliaryUnit auxiliaryUnit) {
        this.auxiliaryUnit = auxiliaryUnit;
        if (auxiliaryUnit != null)
            auxiliaryUnit.setLineWidth(lineWidth_);
    }

    public void updateShapeCoordinates(Label label) {

        if (label == null)
            return;

        float width = getWidth();
        float labelWidth = label.getWidth();

        if ((width - labelWidth) < OFFSET)
            width = labelWidth + OFFSET;

        Rectangle2D.Float rectangle = (Rectangle2D.Float) shape_;
        rectangle.setRect(getMinX(), getMinY(), width, getHeight());

        setWidth(width);

    }

    @Override
    protected ArrayList<Line2D.Float> createLinesBoundary() {

        ArrayList<Line2D.Float> lines = new ArrayList<Line2D.Float>(4);
        lines.add(new Line2D.Float(getMinX(), getMinY(), getMaxX(), getMinY()));
        lines.add(new Line2D.Float(getMaxX(), getMinY(), getMaxX(), getMaxY()));
        lines.add(new Line2D.Float(getMaxX(), getMaxY(), getMinX(), getMaxY()));
        lines.add(new Line2D.Float(getMinX(), getMaxY(), getMinX(), getMinY()));

        return lines;
    }
}
