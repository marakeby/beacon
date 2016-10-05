package edu.vt.beacon.editor.palette;

import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.resources.icons.IconType;
import edu.vt.beacon.editor.swing.IconButton;
import edu.vt.beacon.editor.swing.laf.Skinnable;
import edu.vt.beacon.graph.glyph.GlyphType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;

public class PalettePanel extends JPanel
        implements ActionListener, ComponentListener, Skinnable {
    private static GlyphType[] buttonTypes_;

    private static final long serialVersionUID = 1L;

    private IconButton expandButton_;

    private JPanel buttonPanel_;

    private PaletteButton[] buttons_;

    private PalettePopupMenu popupMenu_;

    // TODO document constructor
    public PalettePanel(Document document) {
        setBackground(COLOR_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        setLayout(new BorderLayout());

        popupMenu_ = new PalettePopupMenu(this);

        buildButtonPanel(document);
        buildExpansionPanel();
    }

    // TODO document method
    @Override
    public void actionPerformed(ActionEvent event) {
        popupMenu_.refresh();
        popupMenu_.show(this, getWidth(), getHeight() -
                popupMenu_.getPreferredSize().height);
    }

    // TODO document method
    private void buildButtonPanel(Document document) {
        buttonPanel_ = new JPanel();
        buttonPanel_.setLayout(new BoxLayout(buttonPanel_, BoxLayout.Y_AXIS));
        buttonPanel_.setOpaque(false);

        if (buttonTypes_ == null)
            initializeButtonTypes();

        Dimension spacingSize = new Dimension(0, 20);
        buttons_ = new PaletteButton[buttonTypes_.length];

        for (int i = 0; i < buttons_.length; i++) {

            buttons_[i] = new PaletteButton(document, buttonTypes_[i]);
            buttonPanel_.add(buttons_[i]);


            if (i < buttons_.length - 1)
                buttonPanel_.add(Box.createRigidArea(spacingSize));
        }

        buttonPanel_.addComponentListener(this);

        add(buttonPanel_);
    }

    // TODO document method
    private void buildExpansionPanel() {
        expandButton_ = new IconButton(IconType.ARROW_EXPAND.getIcon());
        expandButton_.setToolTipText("Show Hidden");

        expandButton_.addActionListener(this);

        JPanel expansionPanel = new JPanel();
        expansionPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        expansionPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        expansionPanel.setOpaque(false);

        expansionPanel.add(expandButton_);

        add(expansionPanel, BorderLayout.SOUTH);
    }

    // FIXME complete method
    @Override
    public void componentHidden(ComponentEvent event) {
    }

    // FIXME complete method
    @Override
    public void componentMoved(ComponentEvent event) {
    }

    // FIXME complete method
    @Override
    public void componentResized(ComponentEvent event) {
        PaletteButton button = buttons_[buttons_.length - 1];
        expandButton_.setVisible(button.getY() + button.getHeight() >
                buttonPanel_.getVisibleRect().height);
    }

    // FIXME complete method
    @Override
    public void componentShown(ComponentEvent event) {
    }

    // TODO document method
    protected ArrayList<PaletteButton> getHiddenButtons() {
        int maxHeight = buttonPanel_.getVisibleRect().height;
        ArrayList<PaletteButton> hidden = new ArrayList<PaletteButton>();

        for (PaletteButton button : buttons_)
            if (button.getY() + button.getHeight() > maxHeight)
                hidden.add(button);

        return hidden;
    }

    // TODO document method
    public PaletteButton getSelectedButton() {
        for (PaletteButton button : buttons_)
            if (button.isSelected())

                return button;

        return null;
    }

    public void enableTagButton(boolean enable) {

        if (buttons_ != null)
            for (PaletteButton button : buttons_)
                if (button != null && button.getGlyphType() == GlyphType.TAG)
                    button.setEnabled(enable);

    }

    // TODO document method
    private static void initializeButtonTypes() {
        buttonTypes_ = new GlyphType[15];
//        buttonTypes_[0] = GlyphType.BIOLOGICAL_ACTIVITY;
//        buttonTypes_[1] = GlyphType.PHENOTYPE;
//        buttonTypes_[2] = GlyphType.COMPARTMENT;
//        buttonTypes_[3] = GlyphType.SUBMAP;
//        buttonTypes_[4] = GlyphType.TAG;
//        buttonTypes_[5] = GlyphType.AND;
//        buttonTypes_[6] = GlyphType.DELAY;
//        buttonTypes_[7] = GlyphType.NOT;
//        buttonTypes_[8] = GlyphType.OR;
//        buttonTypes_[9] = GlyphType.NECESSARY_STIMULATION;
//        buttonTypes_[10] = GlyphType.NEGATIVE_INFLUENCE;
//        buttonTypes_[11] = GlyphType.POSITIVE_INFLUENCE;
//        buttonTypes_[12] = GlyphType.UNKNOWN_INFLUENCE;
//        buttonTypes_[13] = GlyphType.LOGIC_ARC;
//        buttonTypes_[14] = GlyphType.EQUIVALENCE_ARC;

        buttonTypes_[0] = GlyphType.BIOLOGICAL_ACTIVITY;
        buttonTypes_[1] = GlyphType.PHENOTYPE;
        buttonTypes_[2] = GlyphType.COMPARTMENT;
        buttonTypes_[3] = GlyphType.AND;
        buttonTypes_[4] = GlyphType.DELAY;
        buttonTypes_[5] = GlyphType.NOT;
        buttonTypes_[6] = GlyphType.OR;
        buttonTypes_[7] = GlyphType.NECESSARY_STIMULATION;
        buttonTypes_[8] = GlyphType.NEGATIVE_INFLUENCE;
        buttonTypes_[9] = GlyphType.POSITIVE_INFLUENCE;
        buttonTypes_[10] = GlyphType.UNKNOWN_INFLUENCE;
        buttonTypes_[11] = GlyphType.LOGIC_ARC;
        buttonTypes_[12] = GlyphType.SUBMAP;
        buttonTypes_[13] = GlyphType.TAG;
        buttonTypes_[14] = GlyphType.EQUIVALENCE_ARC;
    }
}