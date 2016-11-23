package edu.vt.beacon.graph.glyph.node.auxiliary;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.JLabel;

import edu.vt.beacon.graph.AbstractEntity;
import edu.vt.beacon.graph.glyph.AbstractGlyph;
import edu.vt.beacon.graph.glyph.node.AbstractNode;

public class Label extends AbstractEntity {
    private AbstractGlyph parent_;

    private ArrayList<String> lines_;

    // TODO document constructor
    public Label(AbstractGlyph glyph, String text) {
        parent_ = glyph;
        setSelected(false);
        lines_ = new ArrayList<String>();

        setText(text);
    }

    // TODO document method
    public String getLineAt(int index) {
        return lines_.get(index);
    }

    // TODO document method
    public int getLineCount() {
        return lines_.size();
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
    }

    // TODO document method
    @Override
    protected void setBoundary() {
        JLabel sizeLabel = new JLabel(" ");

        if (parent_ instanceof AbstractNode)
            sizeLabel.setFont(((AbstractNode) parent_).getFont());

        else if (parent_ instanceof AuxiliaryUnit)
            sizeLabel.setFont(((AuxiliaryUnit) parent_).getFont());

        else
            return;

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
    }

    public void move(float deltaX, float deltaY) {
        boundary_.x = Math.max(parent_.getMinX(), Math.min(boundary_.x + deltaX, parent_.getMaxX() - getWidth()));
        boundary_.y = Math.max(parent_.getMinY(), Math.min(boundary_.y + deltaY, parent_.getMaxY() - getHeight()));

        setShapeCoordinates();
    }

    // TODO document method
    @Override
    protected void setShapeCoordinates() {
        Rectangle2D.Float rectangle = (Rectangle2D.Float) shape_;
        rectangle.setFrame(getMinX(), getMinY(), getWidth(), getHeight());
    }

    // TODO document method
    public void setText(String text) {
        lines_.clear();

        StringTokenizer lineTokenizer = new StringTokenizer(text, "\n");

        while (lineTokenizer.hasMoreTokens())
            lines_.add(lineTokenizer.nextToken().trim());

        update();
    }

    public void updateShapeCoordinates(AbstractNode node) {

        boundary_.setRect(node.getMinX() + ((AuxiliaryUnit) parent_).getOffset() + ((AuxiliaryUnit) parent_).getTextOffset(), node.getMinY() - getHeight() / 2, getWidth(), getHeight());

    }

}