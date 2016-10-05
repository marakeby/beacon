package edu.vt.beacon.graph.glyph.node.activity;

import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;

import edu.vt.beacon.graph.glyph.GlyphType;
import edu.vt.beacon.graph.glyph.node.auxiliary.Label;

public class Phenotype extends AbstractActivity {

    /*
     * document constructor
     */
    public Phenotype() {
        super(GlyphType.PHENOTYPE);

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
        return getSlantSize(getWidth(), getHeight());
    }

    private float getSlantSize(float width, float height) {
        return Math.min(width / 4.0F, Math.max(height, getMinHeight()) / 4.0F);
    }

    /*
     * document method
     */
    @Override
    protected void initializeLabel() {
        label_ = new Label(this, "");
    }

    /*
     * document method
     */
    @Override
    protected void initializeShape() {
        shape_ = new Path2D.Float();
    }

    /*
     * document method
     */
    @Override
    protected void setShapeCoordinates() {
        Path2D.Float path = (Path2D.Float) shape_;
        path.reset();

        float slant = getSlantSize();

        path.moveTo(getMinX() + slant, getMinY());
        path.lineTo(getMaxX() - slant, getMinY());
        path.lineTo(getMaxX(), getCenterY());
        path.lineTo(getMaxX() - slant, getMaxY());
        path.lineTo(getMinX() + slant, getMaxY());
        path.lineTo(getMinX(), getCenterY());

        path.closePath();

        updatePorts();
    }

    @Override
    protected ArrayList<Line2D.Float> createLinesBoundary() {
        float slant = getSlantSize();

        ArrayList<Line2D.Float> lines = new ArrayList<Line2D.Float>(6);
        lines.add(new Line2D.Float(getMinX() + slant, getMinY(), getMaxX() - slant, getMinY()));
        lines.add(new Line2D.Float(getMaxX() - slant, getMinY(), getMaxX(), getCenterY()));
        lines.add(new Line2D.Float(getMaxX(), getCenterY(), getMaxX() - slant, getMaxY()));
        lines.add(new Line2D.Float(getMaxX() - slant, getMaxY(), getMinX() + slant, getMaxY()));
        lines.add(new Line2D.Float(getMinX() + slant, getMaxY(), getMinX(), getCenterY()));
        lines.add(new Line2D.Float(getMinX(), getCenterY(), getMinX() + slant, getMinY()));

        return lines;
    }
}