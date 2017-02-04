package edu.vt.beacon.graph.legend;

import edu.vt.beacon.graph.AbstractEntity;
import edu.vt.beacon.graph.Orientable;
import edu.vt.beacon.graph.OrientationType;
import edu.vt.beacon.graph.glyph.auxiliary.Bound;
import edu.vt.beacon.graph.glyph.auxiliary.BoundType;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * Created by ppws on 3/14/16.
 */
public class Legend extends AbstractEntity implements Orientable {

    protected ArrayList<Bound> bounds;

    protected float lineWidth;

    protected float margin;

    protected float interEntryDistance;

    protected float colorBarTextDistance;

    private Color backgroundColor;

    private Color foregroundColor;

    protected Font font;

    protected int fontStyle;

    private Color fontColor;

    private OrientationType orientation;

    private float colorBarWidth;

    private float colorBarHeight;

    private ArrayList<LegendEntry> entries;

    // TODO document constructor
    public Legend() {
        lineWidth = 2.0F;
        margin = 10.0F;
        interEntryDistance = 10f;
        colorBarTextDistance = 20f;
        backgroundColor = Color.white;
        foregroundColor = Color.black;

        font = new Font(null, Font.PLAIN, 11);
        fontColor = Color.black;

        colorBarWidth = 20;
        colorBarHeight = 10;

        entries = new ArrayList<LegendEntry>();

        initializeBounds();
    }

    public void addEntry(LegendEntry entry) {
        if (entry == null)
            return;

        entries.add(entry);
        update();
    }

    // TODO document method
    @Override
    public boolean contains(Point2D.Float point) {
        if (isSelected()) {

            for (Bound bound : bounds)
                if (bound.contains(point))

                    return true;
        }

        return super.contains(point);
    }

    @Override
    protected void initializeShape() {
        shape_ = new Rectangle2D.Float();
    }

    // TODO document method
    public Legend copy() {
        Legend legend = new Legend();
        legend.boundary_.height = boundary_.height;
        legend.boundary_.width = boundary_.width;
        legend.boundary_.x = boundary_.x;
        legend.boundary_.y = boundary_.y;
        legend.lineWidth = lineWidth;
        legend.margin = margin;
        legend.interEntryDistance = interEntryDistance;
        legend.colorBarTextDistance = colorBarTextDistance;
        legend.backgroundColor = backgroundColor;
        legend.foregroundColor = foregroundColor;
        legend.fontColor = fontColor;
        legend.font = font.deriveFont(font.getStyle());
        legend.colorBarWidth = colorBarWidth;
        legend.colorBarHeight = colorBarHeight;
        legend.fontStyle = fontStyle;
        legend.orientation = orientation;

        Rectangle2D.Float castShape = (Rectangle2D.Float) shape_;
        ((Rectangle2D.Float) legend.shape_).setRect(castShape.x, castShape.y, castShape.width, castShape.height);

        legend.bounds = new ArrayList<Bound>(bounds.size());
        for (Bound bound : bounds)
            legend.bounds.add(bound.copy());

        legend.entries = new ArrayList<LegendEntry>(entries.size());
        for (LegendEntry entry : entries)
            legend.entries.add(entry.copy(legend));

        return legend;
    }

    public float getAbsMaxX() {
        return getMaxX() + lineWidth / 2.0F;
    }

    public float getAbsMaxY() {
        return getMaxY() + lineWidth / 2.0F;
    }

    public float getAbsMinX() {
        return getMinX() - lineWidth / 2.0F;
    }

    public float getAbsMinY() {
        return getMinY() - lineWidth / 2.0F;
    }

    public ArrayList<LegendEntry> getEntries() {
        return entries;
    }

    public void setEntries(ArrayList<LegendEntry> entries) {
        this.entries = entries;
    }

    public boolean isEmpty() {
        return entries == null || entries.isEmpty();
    }

    // TODO document method
    public Font getFont() {
        return font;
    }

    public int getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(int fontStyle) {
        this.fontStyle = fontStyle;
        update();
    }

    // TODO document method
    public Color getFontColor() {
        return fontColor;
    }

    // TODO document method
    public float getMinHeight() {
        return lineWidth + margin;
    }

    // TODO document method
    public float getMinWidth() {
        return lineWidth + margin;
    }

    public float getColorBarWidth() {
        return colorBarWidth;
    }

    public void setColorBarWidth(float colorBarWidth) {
        this.colorBarWidth = colorBarWidth;
        update();
    }

    public float getColorBarHeight() {
        return colorBarHeight;
    }

    public void setColorBarHeight(float colorBarHeight) {
        this.colorBarHeight = colorBarHeight;
        update();
    }

    // TODO document method
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    // TODO document method
    public Bound getBoundAt(int index) {
        return bounds.get(index);
    }

    // TODO document method
    public int getBoundCount() {
        return bounds.size();
    }

    // TODO document method
    public Color getForegroundColor() {
        return foregroundColor;
    }

    // TODO document method
    public float getLineWidth() {
        return lineWidth;
    }

    // TODO document method
    public float getMargin() {
        return margin;
    }

    public void setMargin(float margin) {
        this.margin = margin;
        update();
    }

    public float getInterEntryDistance() {
        return interEntryDistance;
    }

    public void setInterEntryDistance(float interEntryDistance) {
        this.interEntryDistance = interEntryDistance;
        update();
    }

    public float getColorBarTextDistance() {
        return colorBarTextDistance;
    }

    public void setColorBarTextDistance(float colorBarTextDistance) {
        this.colorBarTextDistance = colorBarTextDistance;
        update();
    }

    // TODO document method
    protected void initializeBounds() {
        bounds = new ArrayList<Bound>();
        bounds.add(new Bound(BoundType.NORTHWEST));
        bounds.add(new Bound(BoundType.SOUTHWEST));
        bounds.add(new Bound(BoundType.SOUTHEAST));
        bounds.add(new Bound(BoundType.NORTHEAST));
    }

    // TODO document method
    public void setBackgroundColor(Color color) {
        backgroundColor = color;
    }

    // TODO document method
    @Override
    protected void setBoundary() {
        boundary_.width = 0;
        boundary_.height = 0;

        for (LegendEntry entry : entries) {
            boundary_.width = Math.max(boundary_.width, entry.getWidth());
            boundary_.height += entry.getHeight();
        }

        boundary_.width += 2 * margin;
        boundary_.height += 2 * margin;

        if (entries.size() > 0)
            boundary_.height += (entries.size() - 1) * interEntryDistance;
    }

    @Override
    protected void setShapeCoordinates() {
        Rectangle2D.Float rect = (Rectangle2D.Float) shape_;
        rect.setRect(getMinX(), getMinY(), boundary_.width, boundary_.height);
        boundary_.setRect(getMinX(), getMinY(), boundary_.width, boundary_.height);

        float currentX = getMinX() + margin;
        float currentY = getMinY() + margin;

        for (LegendEntry entry : entries) {
            entry.update();
        }

        for (LegendEntry entry : entries) {

            ((Rectangle2D.Float) entry.getShape()).setRect(currentX, currentY, entry.getWidth(), entry.getHeight());
            entry.getBoundary().setRect(currentX, currentY, entry.getWidth(), entry.getHeight());
            entry.getColorBar().setRect(currentX, currentY + entry.getHeight() / 2.0 - colorBarHeight / 2.0, colorBarWidth, colorBarHeight);

            currentY += interEntryDistance + entry.getHeight();
        }

    }

    @Override
    public void move(float deltaX, float deltaY) {
        super.move(deltaX, deltaY);

        for (Bound bound : bounds)
            bound.move(deltaX, deltaY);
    }

    // TODO document method
    protected void setBoundCoordinates() {
        bounds.get(0).setCenter(getMinX(), getMinY());
        bounds.get(1).setCenter(getMinX(), getMaxY());
        bounds.get(2).setCenter(getMaxX(), getMaxY());
        bounds.get(3).setCenter(getMaxX(), getMinY());
    }

    public void setFont(Font font) {
        this.font = font;
        update();
    }

    public void setFontColor(Color color) {
        fontColor = color;
        update();
    }

    @Override
    public OrientationType getOrientation() {
        return orientation;
    }

    @Override
    public void setOrientation(OrientationType orientation) {
        this.orientation = orientation;

        update();
    }

    // TODO document method
    public void setForegroundColor(Color color) {
        foregroundColor = color;
    }

    // TODO document method
    public void setLineWidth(float width) {
        lineWidth = width;
        update();
    }

    // TODO document method
    @Override
    public void update() {

        if (entries != null)
            for (LegendEntry entry : entries)
                entry.update();

        super.update();
        setBoundCoordinates();

    }
}
