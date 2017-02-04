package edu.vt.beacon.graph.glyph.node.submap;

import edu.vt.beacon.graph.OrientationType;
import edu.vt.beacon.graph.glyph.GlyphType;
import edu.vt.beacon.graph.glyph.node.AbstractNode;
import edu.vt.beacon.graph.glyph.node.auxiliary.Label;
import edu.vt.beacon.graph.glyph.node.auxiliary.Port;
import edu.vt.beacon.graph.glyph.node.auxiliary.PortType;

import java.awt.geom.Path2D;
import java.util.ArrayList;

public abstract class AbstractTagTerminal extends AbstractNode {

    private Submap parent;

    public AbstractTagTerminal(GlyphType type, Submap parent) {
        super(type);
        this.parent = parent;
        setOrientation(OrientationType.RIGHT);
        setWidth(30);
        setHeight(10);

        update();
    }

    public Submap getParent() {
        return parent;
    }

    public void setParent(Submap parent) {
        this.parent = parent;
    }

    protected float getSlantSize() {
        return getSlantSize(getWidth(), getHeight());
    }

    private float getSlantSize(float width, float height) {
        return Math.min(width / 4.0F, Math.max(height, getMinHeight()) / 4.0F);
    }

    /*
     * document
     */
    @Override
    protected void initializeLabel() {
        label_ = new Label(this, "");
    }

    /*
     * document
     */
    @Override
    protected void initializePorts() {
        ports_ = new ArrayList<Port>();
        ports_.add(new Port(this, PortType.LEFT));
        ports_.add(new Port(this, PortType.RIGHT));
    }

    /*
     * document
     */
    @Override
    protected void initializeShape() {
        shape_ = new Path2D.Float();
    }

    /*
     * document
     */
    @Override
    protected void setShapeCoordinates() {
        Path2D.Float path = (Path2D.Float) shape_;
        path.reset();

        float slant = getSlantSize();

        switch (getOrientation()) {
            case RIGHT:
                path.moveTo(getMinX(), getMinY());
                path.lineTo(getMaxX() - slant, getMinY());
                path.lineTo(getMaxX(), getCenterY());
                path.lineTo(getMaxX() - slant, getMaxY());
                path.lineTo(getMinX(), getMaxY());
                break;

            case LEFT:
                path.moveTo(getMaxX(), getMinY());
                path.lineTo(getMinX() + slant, getMinY());
                path.lineTo(getMinX(), getCenterY());
                path.lineTo(getMinX() + slant, getMaxY());
                path.lineTo(getMaxX(), getMaxY());
                break;

            case UP:
                path.moveTo(getMaxX(), getMaxY());
                path.lineTo(getMaxX(), getMinY() + slant);
                path.lineTo(getCenterX(), getMinY());
                path.lineTo(getMinX(), getMinY() + slant);
                path.lineTo(getMinX(), getMaxY());
                break;

            case DOWN:
                path.moveTo(getMaxX(), getMinY());
                path.lineTo(getMaxX(), getMaxY() - slant);
                path.lineTo(getCenterX(), getMaxY());
                path.lineTo(getMinX(), getMaxY() - slant);
                path.lineTo(getMinX(), getMinY());
                break;
        }

        path.closePath();
    }
}