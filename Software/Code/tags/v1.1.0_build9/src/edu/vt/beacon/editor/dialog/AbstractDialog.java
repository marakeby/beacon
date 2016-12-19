package edu.vt.beacon.editor.dialog;

import edu.vt.beacon.editor.dialog.shape.ShapeGlyphRunnable;
import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.swing.EditingTextField;
import edu.vt.beacon.editor.util.PlatformManager;
import edu.vt.beacon.graph.glyph.AbstractGlyph;
import edu.vt.beacon.graph.legend.Legend;
import edu.vt.beacon.layer.Layer;
import edu.vt.beacon.map.Map;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public abstract class AbstractDialog extends JDialog implements ActionListener {

    protected static final String LABEL_BUTTON_OK = "OK";
    protected static final String LABEL_BUTTON_CANCEL = "Cancel";

    protected static final String AC_OK_BUTTON = "OK_BUTTON";
    protected static final String AC_CANCEL_BUTTON = "CANCEL_BUTTON";

    protected Document document_;

    protected AbstractDialog(Document document, Dialog owner) {
        super(owner);
        this.document_ = document;
    }

    protected AbstractDialog(Document document, JFrame owner) {
        super(owner);
        this.document_ = document;
    }


    protected JPanel createBasePanel() {
        JPanel panel = new JPanel();
        LayoutManager borderLayout = new BorderLayout();
        panel.setLayout(borderLayout);
        panel.add(createClosingButtonPanel(), BorderLayout.SOUTH);
        return panel;
    }

    private Component createClosingButtonPanel() {
        JComponent panel = new JPanel();
        ((FlowLayout) panel.getLayout()).setAlignment(FlowLayout.RIGHT);
        JButton cancelButton = new JButton(LABEL_BUTTON_CANCEL);
        cancelButton.setActionCommand(AC_CANCEL_BUTTON);
        cancelButton.addActionListener(this);
        JButton okButton = new JButton(LABEL_BUTTON_OK);
        okButton.setActionCommand(AC_OK_BUTTON);
        okButton.addActionListener(this);
        PlatformManager manager = new PlatformManager();

        if (manager.isMacPlatform()) {
            panel.add(cancelButton);
            panel.add(okButton);
        } else {
            panel.add(okButton);
            panel.add(cancelButton);

        }
        return panel;
    }

    protected JPanel getLabelledPanel(Component label, Component field) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2));
        panel.add(label);
        panel.add(field);
        return panel;
    }

    protected EditingTextField getTextField(int size, String actionCommand) {
        EditingTextField tf = new EditingTextField(size);
        tf.setActionCommand(actionCommand);
        tf.addActionListener(this);
        return tf;
    }

    protected JCheckBox getCheckBox(String actionCommand) {
        JCheckBox cb = new JCheckBox();
        cb.setActionCommand(actionCommand);
        cb.addActionListener(this);
        return cb;
    }

    protected JComboBox getComboBox(String[] choices, String actionCommand) {
        JComboBox cb = new JComboBox(choices);
        cb.setActionCommand(actionCommand);
        cb.addActionListener(this);
        return cb;
    }

    protected JButton getButton(String text, String actionCommand) {
        JButton b = new JButton(text);
        b.setActionCommand(actionCommand);
        b.addActionListener(this);
        return b;
    }

    protected JPanel getColorPanel(Color color) {
        JPanel panel = new JPanel();
        Dimension colorPanelSize = new Dimension(40, 20);

        if (color != null) {
            panel.setBackground(color);
        } else {
            panel.setBackground(Color.WHITE);
        }
        panel.setBorder(BorderFactory.createLoweredBevelBorder());
        panel.setPreferredSize(colorPanelSize);

        return panel;
    }

    protected void modifyGlyphs(ShapeGlyphRunnable sgr, AWTEvent e) {
        Layer layer;
        AbstractGlyph glyph;
        Map map = document_.getBrowserMenu().getSelectedMap();

        if (map != null && map.getLegend() != null && map.getLegend().isSelected()) {

            sgr.modifyGlyph(e.getSource(), map.getLegend(), document_);

        } else {

            for (int i = 0; i < map.getLayerCount(); i++) {
                layer = map.getLayerAt(i);
                if (layer.isActive()) {
                    for (int j = 0; j < layer.getGlyphCount(); j++) {
                        glyph = layer.getGlyphAt(j);
                        if (glyph.isSelected()) {
                            sgr.modifyGlyph(e.getSource(), glyph, document_);
                        }
                    }
                }
            }

        }
    }

    public boolean isDefaultOrNoGlyphSelected() {
        Legend legend = document_.getPathway().getMap().getLegend();

        if (countSelectedGlyphs() == 0 && (legend != null && !legend.isSelected())) {
            return true;
        } else {
            return false;
        }

    }

    private int countSelectedGlyphs() {
        Layer layer;
        AbstractGlyph glyph;
        int count = 0;
        Map map = document_.getBrowserMenu().getSelectedMap();
        for (int i = 0; i < map.getLayerCount(); i++) {
            layer = map.getLayerAt(i);
            if (layer.isActive()) {
                for (int j = 0; j < layer.getGlyphCount(); j++) {
                    glyph = layer.getGlyphAt(j);
                    if (glyph.isSelected()) {
                        count++;
                    }
                }
            }
        }
        return count;
    }
}


