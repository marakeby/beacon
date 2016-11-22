package edu.vt.beacon.graph.glyph.node.operator;

import edu.vt.beacon.graph.Orientable;
import edu.vt.beacon.graph.OrientationType;
import edu.vt.beacon.graph.glyph.GlyphType;
import edu.vt.beacon.graph.glyph.node.AbstractNode;
import edu.vt.beacon.graph.glyph.node.auxiliary.Label;
import edu.vt.beacon.graph.glyph.node.auxiliary.Port;
import edu.vt.beacon.graph.glyph.node.auxiliary.PortType;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;

public abstract class AbstractOperator extends AbstractNode
        implements Orientable {
    private Label sizeLabel_;

    private Path2D.Float portLine_;

    /*
     * document constructor
     */
    protected AbstractOperator(GlyphType type) {
        super(type);

        sizeLabel_ = new Label(this, "AND");
        portLine_ = new Path2D.Float();
        setOrientation(OrientationType.RIGHT);
    }

    /*
     * document method
     */
    @Override
    public AbstractOperator copy() {
        AbstractOperator operator = (AbstractOperator) super.copy();
        operator.setOrientation(getOrientation());

        operator.update();

        return operator;
    }

    /*
     * document method
     */
    @Override
    public float getAbsMaxX() {
        return getOrientation().isHorizontal()
                ? getMaxX() + getWidth() / 4.0F
                : getMaxX();
    }

    /*
     * document method
     */
    @Override
    public float getAbsMaxY() {
        return getOrientation().isVertical()
                ? getMaxY() + getHeight() / 4.0F
                : getMaxY();
    }

    /*
     * document method
     */
    @Override
    public float getAbsMinX() {
        return getOrientation().isHorizontal()
                ? getMinX() - getWidth() / 4.0F
                : getMinX();
    }

    /*
     * document method
     */
    @Override
    public float getAbsMinY() {
        return getOrientation().isVertical()
                ? getMinY() - getHeight() / 4.0F
                : getMinY();
    }

    /*
     * document method
     */
    @Override
    public float getMinHeight() {
        return Math.max(sizeLabel_.getHeight(), sizeLabel_.getWidth()) +
                lineWidth_ + padding_;
    }

    /*
     * document method
     */
    @Override
    public float getMinWidth() {
        return Math.max(sizeLabel_.getHeight(), sizeLabel_.getWidth()) +
                lineWidth_ + padding_;
    }

    /*
     * document method
     */
    public Path2D.Float getPortLine() {
        return portLine_;
    }

    /*
     * document method
     */
    @Override
    protected void initializePorts() {
        ports_ = new ArrayList<Port>();
        ports_.add(new Port(this, PortType.LEFT));
        ports_.add(new Port(this, PortType.RIGHT));
    }

    /*
     * document method
     */
    @Override
    protected void initializeShape() {
        shape_ = new Ellipse2D.Float();
    }

    @Override
    public void setFont(Font font) {
        font_ = font;
        label_.update();
        sizeLabel_.update();

        update();
    }

    @Override
    protected void setPortCoordinates() {

        if (getOrientation().isHorizontal()){
            ports_.get(1).setCenter(getAbsMaxX(), getCenterY());
            ports_.get(0).setCenter(getAbsMinX(), getCenterY());
        }

        if (getOrientation().isVertical()){
            ports_.get(1).setCenter(getCenterX(), getAbsMaxY());
            ports_.get(0).setCenter(getCenterX(), getAbsMinY());
        }

        switch (getOrientation()) {

            case DOWN:
                ports_.get(0).setCenter(getCenterX(), getAbsMinY());
                ports_.get(1).setCenter(getCenterX(), getAbsMaxY());
                break;

            case LEFT:
                ports_.get(0).setCenter(getAbsMaxX(), getCenterY());
                ports_.get(1).setCenter(getAbsMinX(), getCenterY());
                break;

            case RIGHT:
                ports_.get(0).setCenter(getAbsMinX(), getCenterY());
                ports_.get(1).setCenter(getAbsMaxX(), getCenterY());
                break;

            case UP:
                ports_.get(0).setCenter(getCenterX(), getAbsMaxY());
                ports_.get(1).setCenter(getCenterX(), getAbsMinY());
                break;
        }


        if (getOrientation() == OrientationType.DOWN || getOrientation() == OrientationType.UP)
            orientation_ = OrientationType.VERTICAL;

        else if (getOrientation() == OrientationType.LEFT || getOrientation() == OrientationType.RIGHT)
            orientation_ = OrientationType.HORIZONTAL;

    }

    /*
     * document method
     */
    @Override
    protected void setShapeCoordinates() {
        Ellipse2D.Float ellipse = (Ellipse2D.Float) shape_;
        ellipse.setFrame(getMinX(), getMinY(), getWidth(), getHeight());

        portLine_.reset();

        if (getOrientation().isHorizontal()) {

            portLine_.moveTo(getAbsMinX(), getCenterY());
            portLine_.lineTo(super.getAbsMinX(), getCenterY());
            portLine_.moveTo(super.getAbsMaxX(), getCenterY());
            portLine_.lineTo(getAbsMaxX(), getCenterY());
        } else {

            portLine_.moveTo(getCenterX(), getAbsMinY());
            portLine_.lineTo(getCenterX(), super.getAbsMinY());
            portLine_.moveTo(getCenterX(), super.getAbsMaxY());
            portLine_.lineTo(getCenterX(), getAbsMaxY());
        }
    }
}