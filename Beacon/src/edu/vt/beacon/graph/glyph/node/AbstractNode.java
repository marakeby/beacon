package edu.vt.beacon.graph.glyph.node;

import edu.vt.beacon.graph.AbstractEntity;
import edu.vt.beacon.graph.Orientable;
import edu.vt.beacon.graph.OrientationType;
import edu.vt.beacon.graph.glyph.AbstractGlyph;
import edu.vt.beacon.graph.glyph.GlyphType;
import edu.vt.beacon.graph.glyph.arc.AbstractArc;
import edu.vt.beacon.graph.glyph.auxiliary.Bound;
import edu.vt.beacon.graph.glyph.auxiliary.BoundType;
import edu.vt.beacon.graph.glyph.node.auxiliary.Label;
import edu.vt.beacon.graph.glyph.node.auxiliary.Port;
import edu.vt.beacon.graph.glyph.node.container.Compartment;
import edu.vt.beacon.graph.glyph.node.operator.AbstractOperator;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public abstract class AbstractNode extends AbstractGlyph implements Orientable {
    protected ArrayList<Port> ports_;

    protected Font font_;

    protected Label label_;

    private boolean isPortRendering_;

    private Color fontColor_;

    protected OrientationType orientation_;

    private Compartment parentCompartment;

    private boolean centeredLabel;

    // TODO document constructor
    protected AbstractNode(GlyphType type) {
        super(type);
        centeredLabel = true;

        font_ = new Font(null, Font.PLAIN, 11);
        fontColor_ = Color.black;

        initializeLabel();
        initializePorts();
    }

    // TODO document method
    @Override
    public AbstractNode copy() {
        AbstractNode node = (AbstractNode) super.copy();
        node.fontColor_ = fontColor_;
        node.font_ = font_.deriveFont(font_.getStyle());
        node.label_.setText(label_.getText());

        node.update();

        return node;
    }

    // TODO document method
    @Override
    public boolean contains(Point2D.Float point) {
        if (isPortRendering_) {

            for (Port port : ports_)
                if (port.contains(point))

                    return true;
        }

        return super.contains(point);
    }

    // TODO document method
    @Override
    public void delete() {
        AbstractArc arc;

        for (Port port : ports_) {

            for (int i = 0; i < port.getArcCount(); i++) {

                arc = port.getArcAt(i);

                if (arc.getSourcePort().equals(port))
                    arc.setSourcePort(null);

                if (arc.getTargetPort().equals(port))
                    arc.setTargetPort(null);
            }
        }

        super.delete();
    }
    public ArrayList<AbstractArc> getInputArcs(){
        ArrayList<AbstractArc> arcs = new ArrayList<AbstractArc>();
        for(Port p: this.getPorts())
        {
            for(AbstractArc arc : p.getArcs())
                if (arc.getTarget().equals(this))
                    arcs.add(arc);
        }
        return arcs;
    }

    public Compartment getParentCompartment() {
        return parentCompartment;
    }

    public void setParentCompartment(Compartment parentCompartment) {
        this.parentCompartment = parentCompartment;

        if (parentCompartment != null)
            parentCompartment.addNode(this);
    }

    // TODO document method
    @Override
    public float getAbsMaxX() {
        return getMaxX() + lineWidth_ / 2.0F;
    }

    // TODO document method
    @Override
    public float getAbsMaxY() {
        return getMaxY() + lineWidth_ / 2.0F;
    }

    // TODO document method
    @Override
    public float getAbsMinX() {
        return getMinX() - lineWidth_ / 2.0F;
    }

    // TODO document method
    @Override
    public float getAbsMinY() {
        return getMinY() - lineWidth_ / 2.0F;
    }

    // TODO document method
    public Font getFont() {
        return font_;
    }

    // TODO document method
    public Color getFontColor() {
        return fontColor_;
    }

    // TODO document method
    public Label getLabel() {
        return label_;
    }

    // TODO document method
    public float getMinHeight() {
        return label_.getHeight() + lineWidth_ + padding_;
    }

    // TODO document method
    public float getMinWidth() {
        return label_.getWidth() + lineWidth_ + padding_;
    }

    // TODO document method
    public Port getPortAt(int index) {
        return ports_.get(index);
    }

    public ArrayList<Port> getPorts() {
        return ports_;
    }

    // TODO document method
    public int getPortCount() {
        return ports_.size();
    }

    // TODO document method
    public String getText() {
        return label_.getText();
    }

    public boolean isCenteredLabel() {
        return centeredLabel;
    }

    public void setCenteredLabel(boolean centeredLabel) {
        this.centeredLabel = centeredLabel;
    }

    // TODO document method
    @Override
    protected void initializeBounds() {
        bounds_ = new ArrayList<Bound>();
        bounds_.add(new Bound(BoundType.NORTHWEST));
        bounds_.add(new Bound(BoundType.SOUTHWEST));
        bounds_.add(new Bound(BoundType.SOUTHEAST));
        bounds_.add(new Bound(BoundType.NORTHEAST));
    }

    // TODO document method
    protected abstract void initializeLabel();

    // TODO document method
    protected abstract void initializePorts();

    // TODO document method
    public boolean isPortRendering() {
        return isPortRendering_;
    }

    // TODO document method
    @Override
    public void move(float deltaX, float deltaY) {
        super.move(deltaX, deltaY);
        for (Bound bound : bounds_)
            bound.move(deltaX, deltaY);

        for (Port port : ports_)
            port.move(deltaX, deltaY);

        label_.move(deltaX, deltaY);

        super.setBoundary();
    }

    // TODO document method
    @Override
    protected void setBoundary() {
        boundary_.width = Math.max(getWidth(), getMinWidth());
        boundary_.height = Math.max(getHeight(), getMinHeight());

        super.setBoundary();
    }

    // TODO document method
    @Override
    protected void setBoundCoordinates() {
        bounds_.get(0).setCenter(getMinX(), getMinY());
        bounds_.get(1).setCenter(getMinX(), getMaxY());
        bounds_.get(2).setCenter(getMaxX(), getMaxY());
        bounds_.get(3).setCenter(getMaxX(), getMinY());
    }

    // TODO document method
    public void setFont(Font font) {
        font_ = font;
        label_.update();

        update();
    }

    // TODO document method
    public void setFontColor(Color color) {
        fontColor_ = color;
    }

    @Override
    public OrientationType getOrientation() {
        return orientation_;
    }

    @Override
    public void setOrientation(OrientationType orientation) {
        orientation_ = orientation;

        if (!(this instanceof AbstractOperator) && orientation != null && getOrientation() != orientation) {

            int rotationAngle = (orientation.ordinal() - getOrientation().ordinal()) * 90;
            rotate(rotationAngle);

        }

        update();

    }

    private void rotate(int angle) {

        for (Port port: ports_)
            rotate(port, angle, getCenterX(), getCenterY());

        rotate(label_, angle, getCenterX(), getCenterY());

        for (Bound bound: bounds_)
            rotate(bound, angle, getCenterX(), getCenterY());


    }

    private void rotate(AbstractEntity entity, int angle, float originX, float originY) {

        float relativeX = entity.getCenterX() - originX;
        float relativeY = entity.getCenterY() - originY;
        float newX = 0;
        float newY = 0;


        if (angle == -90 || angle == 270) {

            newX = -relativeY + originX;
            newY = relativeX + originY;

        } else if (angle == 90 || angle == -270) {

            newX = relativeY + originX;
            newY = -relativeX + originY;

        } else if (angle == -180 || angle == 180) {

            newX = -relativeX + originX;
            newY = -relativeY + originY;

        }

        entity.move(newX - entity.getCenterX(), newY - entity.getCenterY());

    }

    // TODO document method
    protected void setLabelCoordinates() {
        if (centeredLabel)
            label_.setCenter(getCenterX(), getCenterY());
    }

    // TODO document method
    protected abstract void setPortCoordinates();

    // TODO document method
    public void setPortRendering(boolean isPortRendering) {
        isPortRendering_ = isPortRendering;
    }

    // TODO document method
    public void setText(String text) {
        label_.setText(text);

        update();
    }

    public void setLabelLocation(float x, float y, float width, float height) {
        centeredLabel = false;
        label_.getBoundary().setRect(x, y, width, height);
    }

    // TODO document method
    @Override
    public void update() {
        super.update();

        setLabelCoordinates();
        setPortCoordinates();
    }

    public Boolean isInputNode(){

            Boolean output = true;
            for(Port p: this.getPorts())
            {
                for(AbstractArc arc : p.getArcs())
                    if (arc.getTarget().equals(this))
                        output=false;
            }
            return output;

    }
}