package edu.vt.beacon.graph.glyph.node.auxiliary;

import edu.vt.beacon.graph.glyph.AbstractGlyph;
import edu.vt.beacon.graph.glyph.GlyphType;
import edu.vt.beacon.graph.glyph.auxiliary.Bound;
import edu.vt.beacon.graph.glyph.auxiliary.BoundType;
import edu.vt.beacon.graph.glyph.node.AbstractNode;

import java.awt.*;
import java.util.ArrayList;

public abstract class AuxiliaryUnit extends AbstractGlyph {

    protected Font font_;

    protected Label label_;

    private Color fontColor_;

    private float offset;

    private float textOffset;

    // TODO document constructor
    protected AuxiliaryUnit(GlyphType type) {
        super(type);

        textOffset = 0;
        font_ = new Font(null, Font.PLAIN, 11);
        fontColor_ = Color.black;

        initializeLabel();
    }

    public abstract void updateShapeCoordinates(AbstractNode node);

    // TODO document method
    @Override
    public AuxiliaryUnit copy() {
        AuxiliaryUnit node = (AuxiliaryUnit) super.copy();
        node.fontColor_ = fontColor_;
        node.font_ = font_.deriveFont(font_.getStyle());
        node.label_.setText(label_.getText());

        node.update();

        return node;
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
    public String getText() {
        return label_.getText();
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
    @Override
    public void move(float deltaX, float deltaY) {
        super.move(deltaX, deltaY);

        for (Bound bound : bounds_)
            bound.move(deltaX, deltaY);

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

    // TODO document method
    protected void setLabelCoordinates() {
        label_.setCenter(getCenterX(), getCenterY());
    }


    // TODO document method
    public void setText(String text) {
        label_.setText(text);

        update();
    }

    // TODO document method
    @Override
    public void update() {
        super.update();

        setLabelCoordinates();
    }

    @Override
    protected void setShapeCoordinates() {}

    public float getOffset() {
        return offset;
    }

    public void setOffset(float offset) {
        this.offset = offset;
    }

    public float getTextOffset() {
        return textOffset;
    }

    public void setTextOffset(float textOffset) {
        this.textOffset = textOffset;
    }
}