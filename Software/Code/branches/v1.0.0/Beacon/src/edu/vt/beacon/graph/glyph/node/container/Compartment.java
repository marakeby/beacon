package edu.vt.beacon.graph.glyph.node.container;

import edu.vt.beacon.graph.glyph.AbstractGlyph;
import edu.vt.beacon.graph.glyph.GlyphType;
import edu.vt.beacon.graph.glyph.node.AbstractNode;
import edu.vt.beacon.graph.glyph.node.auxiliary.CompartmentUnit;
import edu.vt.beacon.graph.glyph.node.auxiliary.Label;
import edu.vt.beacon.graph.glyph.node.auxiliary.Port;
import edu.vt.beacon.layer.Layer;

import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

/**
 * Created by ppws on 1/11/16.
 */
public class Compartment extends AbstractNode {

    public final static int CORNER_ROUNDNESS = 30;

    private CompartmentUnit compartmentUnit;

    private int renderingOrder;

    private boolean transparent;

    public Compartment() {

        super(GlyphType.COMPARTMENT);

        transparent = false;
        update();
    }

    @Override
    public void move(float deltaX, float deltaY) {

        moveInnerGlyphs(deltaX, deltaY);
        super.move(deltaX, deltaY);

    }

    public void dontMoveInnerGlyphs(float deltaX, float deltaY) {

        super.move(deltaX, deltaY);

    }


    private void moveInnerGlyphs(float deltaX, float deltaY) {

        ArrayList<Layer> layers = getLayer().getMap().getLayers();

        for (Layer layer : layers)
            for (AbstractGlyph glyph : layer.getGlyphs())
                if (isInsideCompartment(glyph)) {

                    if (glyph instanceof Compartment)
                        ((Compartment) glyph).dontMoveInnerGlyphs(deltaX, deltaY);
                    else
                        glyph.move(deltaX, deltaY);

                }

    }

    private boolean isInsideCompartment(AbstractGlyph glyph) {

        return glyph != null && glyph != this &&
                contains(new Point2D.Float(glyph.getMinX(), glyph.getMinY())) &&
                contains(new Point2D.Float(glyph.getMinX(), glyph.getMaxY())) &&
                contains(new Point2D.Float(glyph.getMaxX(), glyph.getMinY())) &&
                contains(new Point2D.Float(glyph.getMaxX(), glyph.getMaxY()));

    }

    public boolean isTransparent() {
        return transparent;
    }

    public void setTransparent(boolean transparent) {
        this.transparent = transparent;
    }

    public int getRenderingOrder() {
        return renderingOrder;
    }

    public void setRenderingOrder(int renderingOrder) {
        this.renderingOrder = renderingOrder;
    }

    @Override
    protected void initializeLabel() {
        label_ = new Label(this, "");
    }

    @Override
    protected void initializePorts() {
        ports_ = new ArrayList<Port>();
    }

    @Override
    protected void setPortCoordinates() {
    }

    @Override
    protected void initializeShape() {
        shape_ = new RoundRectangle2D.Float();
    }

    @Override
    protected void setShapeCoordinates() {
        RoundRectangle2D.Float rectangle = (RoundRectangle2D.Float) shape_;
        rectangle.setRoundRect(getMinX(), getMinY(), getWidth(), getHeight(), CORNER_ROUNDNESS, CORNER_ROUNDNESS);
    }

    public CompartmentUnit getCompartmentUnit() {
        return compartmentUnit;
    }

    public void setCompartmentUnit(CompartmentUnit compartmentUnit) {
        this.compartmentUnit = compartmentUnit;
        if (compartmentUnit != null)
            compartmentUnit.setLineWidth(lineWidth_);
    }

    public void updateShapeCoordinates(Label label) {

        if (label == null)
            return;

        float width = getWidth();
        float labelWidth = label.getWidth();

        if ((width - labelWidth) < CORNER_ROUNDNESS)
            width = labelWidth + CORNER_ROUNDNESS;

        RoundRectangle2D.Float rectangle = (RoundRectangle2D.Float) shape_;
        rectangle.setRoundRect(getMinX(), getMinY(), width, getHeight(), CORNER_ROUNDNESS, CORNER_ROUNDNESS);

        setWidth(width);

    }

}
