package edu.vt.beacon.graph.legend;

import edu.vt.beacon.graph.AbstractEntity;

import javax.swing.*;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by mostafa on 3/16/16.
 */
@XmlType(name="LegendEntry", namespace = "https://bioinformatics.cs.vt.edu")
public class LegendEntry extends AbstractEntity {

    @XmlTransient
    private Legend parent_;

    private ArrayList<String> lines_;

    private Rectangle2D.Float colorBar;

    private Color color;

    public LegendEntry(){
        lines_ = new ArrayList<String>();
    }

    public void setParent(Legend p){this.parent_ = p;}
    // TODO document constructor
    public LegendEntry(Legend parent, Color color, String text) {
        parent_ = parent;
        lines_ = new ArrayList<String>();
        this.color = color;

        setSelected(false);
        setText(text);
    }

    private LegendEntry(Legend parent) {
        parent_ = parent;
        lines_ = new ArrayList<String>();
    }
    @XmlTransient
    public Legend getParent() {
        return parent_;
    }

    @XmlJavaTypeAdapter(Legend.ColorAdapter.class)
    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Rectangle2D.Float getColorBar() {
        return colorBar;
    }

    // TODO document method
    public String getLineAt(int index) {
        return lines_.get(index);
    }

    // TODO document method
    public int getLineCount() {
        return lines_.size();
    }

    public LegendEntry copy(Legend legend) {
        LegendEntry entry = new LegendEntry(legend);

        entry.setId(getId());

        entry.boundary_.setRect(boundary_.x, boundary_.y, boundary_.width, boundary_.height);
        Rectangle2D.Float castShape = (Rectangle2D.Float) shape_;
        ((Rectangle2D.Float) entry.shape_).setRect(castShape.x, castShape.y, castShape.width, castShape.height);
        entry.setSelected(isSelected());


        entry.color = new Color(color.getRGB());
        entry.colorBar = new Rectangle2D.Float(colorBar.x, colorBar.y, colorBar.width, colorBar.height);

        for (String line : lines_)
            entry.lines_.add(line + "");

        return entry;
    }

    // TODO document method
    public String getText() {
        String text = "";

        for (int i = 0; i < lines_.size(); i++) {

            text += lines_.get(i);

            if (i < lines_.size() - 1)
                text += "\n";
        }

        return text;
    }

    // TODO document method
    @Override
    protected void initializeShape() {
        shape_ = new Rectangle2D.Float();
        colorBar = new Rectangle2D.Float();
    }

    // TODO document method
    @Override
    protected void setBoundary() {
        JLabel sizeLabel = new JLabel(" ");

        if(parent_ ==null)
            return;
        sizeLabel.setFont(parent_.getFont());

        boundary_.width = (float) sizeLabel.getPreferredSize().getWidth();
        boundary_.height = (float) sizeLabel.getPreferredSize().getHeight();

        if (!lines_.isEmpty())
            boundary_.height = 0.0F;

        for (String line : lines_) {

            sizeLabel.setText(line);

            boundary_.width = Math.max(getWidth(),
                    (float) sizeLabel.getPreferredSize().getWidth());
            boundary_.height +=
                    (float) sizeLabel.getPreferredSize().getHeight();
        }

        boundary_.width += parent_.getColorBarWidth() + parent_.getColorBarTextDistance();
        boundary_.height = Math.max(boundary_.height, parent_.getColorBarHeight());
    }

    public void move(float deltaX, float deltaY) {
        boundary_.x = Math.max(parent_.getMinX(), Math.min(boundary_.x + deltaX, parent_.getMaxX() - getWidth()));
        boundary_.y = Math.max(parent_.getMinY(), Math.min(boundary_.y + deltaY, parent_.getMaxY() - getHeight()));

        setShapeCoordinates();
    }

    // TODO document method
    @Override
    protected void setShapeCoordinates() {
    }

    // TODO document method
    public void setText(String text) {
        lines_.clear();

        StringTokenizer lineTokenizer = new StringTokenizer(text, "\n");

        while (lineTokenizer.hasMoreTokens())
            lines_.add(lineTokenizer.nextToken().trim());

        update();
    }
}
