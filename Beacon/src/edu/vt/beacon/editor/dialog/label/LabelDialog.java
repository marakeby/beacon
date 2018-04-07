package edu.vt.beacon.editor.dialog.label;

import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.document.DocumentState;
import edu.vt.beacon.graph.glyph.AbstractGlyph;
import edu.vt.beacon.graph.glyph.node.AbstractNode;
import edu.vt.beacon.graph.glyph.node.operator.And;
import edu.vt.beacon.graph.glyph.node.operator.Delay;
import edu.vt.beacon.graph.glyph.node.operator.Not;
import edu.vt.beacon.graph.glyph.node.operator.Or;
import edu.vt.beacon.graph.glyph.node.submap.Submap;
import edu.vt.beacon.graph.glyph.node.submap.Tag;
import edu.vt.beacon.graph.glyph.node.submap.Terminal;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by ppws on 3/31/16.
 */
public class LabelDialog {

    public static void createDialog(Document document) {

        if (document == null || document.getPathway() == null)
            return;

        ArrayList<AbstractGlyph> selectedGlyphs = document.getBrowserMenu().getSelectedMap().getSelectedGlyphs();

        if (selectedGlyphs == null || selectedGlyphs.isEmpty() || selectedGlyphs.size() > 1
                || !(selectedGlyphs.get(0) instanceof AbstractNode))
            return;

        AbstractNode selectedNode = (AbstractNode) selectedGlyphs.get(0);
        //if Node is logical operator, then label cannot be changed
        if (selectedNode instanceof And || selectedNode instanceof Or || selectedNode instanceof Not || selectedNode instanceof Delay)
            return;
        String label = JOptionPane.showInputDialog(null, "Enter label:", selectedNode.getText());

        if (label != null) {

            selectedNode.setText(label);
            selectedNode.update();
            new DocumentState(document, "Label", false);
            document.getCanvas().repaint();

            if (selectedNode instanceof Tag) {

                Submap correspondingSubmap = ((Tag) selectedNode).getParent();
                if (correspondingSubmap != null)
                    correspondingSubmap.getTagToTerminalMapping().get(selectedNode).setText(label);

            } else if (selectedNode instanceof Terminal) {

                Submap correspondingSubmap = ((Terminal) selectedNode).getParent();
                if (correspondingSubmap != null)
                    correspondingSubmap.getTerminalToTagMapping().get(selectedNode).setText(label);

            }
        }

    }

}
