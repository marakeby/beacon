package edu.vt.beacon.editor.dialog.shape;

import edu.vt.beacon.editor.dialog.BColorChooser;
import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.graph.glyph.AbstractGlyph;
import edu.vt.beacon.graph.glyph.node.AbstractNode;
import edu.vt.beacon.graph.glyph.node.activity.BiologicalActivity;
import edu.vt.beacon.graph.glyph.node.auxiliary.AuxiliaryUnit;
import edu.vt.beacon.graph.glyph.node.container.Compartment;
import edu.vt.beacon.layer.Layer;
import edu.vt.beacon.map.Map;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ShapeColorListener implements MouseListener {

    private String type;  // color type
    private Document document_;
    private JComponent target_;
    private JDialog owner_;
    private boolean isFill_ = false;
    private boolean isAuxiliaryUnit = false;

    public ShapeColorListener(String type, boolean isAuxiliaryUnit, Document document,
                              JComponent target, JDialog owner) {

        // set the color type
        this.type = type;
        this.document_ = document;
        this.target_ = target;
        this.owner_ = owner;
        this.isAuxiliaryUnit = isAuxiliaryUnit;
    }

    public ShapeColorListener(String type, boolean isAuxiliaryUnit, Document document,
                              JComponent target, JDialog owner, boolean isFill) {

        // set the color type
        this.type = type;
        this.document_ = document;
        this.target_ = target;
        this.owner_ = owner;
        this.isFill_ = isFill;
        this.isAuxiliaryUnit = isAuxiliaryUnit;
    }

    @Override
    public void mouseClicked(MouseEvent me) {
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }

    @Override
    public void mousePressed(MouseEvent me) {

        // show a new color chooser for the selected color panel
        BColorChooser.showNodeDialog(owner_, Color.WHITE, Color.WHITE,
                target_.getBackground(),
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames(null)[0],
                24, (JPanel) me.getSource(), type);


        if (me.getSource().equals(target_)) {

            Color color = ((JComponent) me.getSource()).getBackground();

            Layer layer;
            AbstractGlyph glyph;
            AbstractNode node;

            Map map = document_.getBrowserMenu().getSelectedMap();

            if (((ShapeDialog) owner_).isDefaultOrNoGlyphSelected()) {
                if (!isAuxiliaryUnit) {

                    if (isFill_) {
                        document_.put("glyph.background", color);
                    } else {
                        document_.put("glyph.foreground", color);
                    }

                }
            } else {

            }

            if (map.getLegend() != null && map.getLegend().isSelected()) {

                if (!isAuxiliaryUnit) {
                    if (isFill_)
                        map.getLegend().setBackgroundColor(color);
                    else
                        map.getLegend().setForegroundColor(color);
                }

            } else {

                for (int i = 0; i < map.getLayerCount(); i++) {
                    layer = map.getLayerAt(i);
                    if (layer.isActive()) {
                        for (int j = 0; j < layer.getGlyphCount(); j++) {
                            glyph = layer.getGlyphAt(j);
                            if (glyph.isSelected()) {

                                if (!isAuxiliaryUnit) {

                                    if (isFill_) {
                                        glyph.setBackgroundColor(color);
                                    } else {
                                        glyph.setForegroundColor(color);
                                    }

                                } else {

                                    AuxiliaryUnit aux = null;

                                    if (glyph instanceof BiologicalActivity)
                                        aux = ((BiologicalActivity) glyph).getAuxiliaryUnit();

                                    else if (glyph instanceof Compartment)
                                        aux = ((Compartment) glyph).getCompartmentUnit();

                                    if (aux == null)
                                        continue;

                                    if (isFill_) {
                                        aux.setBackgroundColor(color);

                                    } else {
                                        aux.setForegroundColor(color);
                                    }

                                }
                            }
                        }
                    }
                }

            }

            document_.getCanvas().repaint();

        }


    }

    @Override
    public void mouseReleased(MouseEvent me) {
    }
}


