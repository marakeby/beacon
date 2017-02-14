package edu.vt.beacon.editor.canvas;

import edu.vt.beacon.editor.command.Command;
import edu.vt.beacon.editor.command.CommandType;
import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.document.DocumentState;
import edu.vt.beacon.editor.util.ClipBoardManager;
import edu.vt.beacon.graph.glyph.AbstractGlyph;
import edu.vt.beacon.graph.glyph.arc.AbstractArc;
import edu.vt.beacon.graph.glyph.node.AbstractNode;
import edu.vt.beacon.graph.glyph.node.auxiliary.Port;
import edu.vt.beacon.graph.glyph.node.submap.Submap;
import edu.vt.beacon.graph.glyph.node.submap.Tag;
import edu.vt.beacon.graph.glyph.node.submap.Terminal;
import edu.vt.beacon.layer.Layer;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ppws on 1/11/16.
 */
public class CanvasKeyListener extends KeyAdapter {

    private Document document_;

    public CanvasKeyListener(Document document) {
        document_ = document;
    }


    @Override
    public void keyPressed(KeyEvent e) {
//        System.out.println("Key pressed");
        if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
            document_.getCanvas().setState(CanvasStateType.PORT_SHOWING);
            document_.getCanvas().repaint();
        }

        //Ctrl + C is a shortcut for copy
        if (e.getKeyCode() == KeyEvent.VK_C && e.isControlDown() && !e.isShiftDown()) {

            ClipBoardManager.copy(document_.getBrowserMenu().getSelectedMap());
            return;
        }

        //Ctrl + V is a shortcut for paste
        if (e.getKeyCode() == KeyEvent.VK_V && e.isControlDown() && !e.isShiftDown()) {
            paste();
            return;
        }

        //Ctrl + Z is a shortcut for undo
        if (e.getKeyCode() == KeyEvent.VK_Z && e.isControlDown() && !e.isShiftDown()) {
//            System.out.println("UNDOOOOOOŌ");
//            document_.getStateManager().undo();
//            document_.getState().apply(document_);
            document_.undo();
            return;
        }


        //Ctrl + Y OR Ctrl + Shift + Z are the shortcuts for redo
        if ((e.getKeyCode() == KeyEvent.VK_Y && e.isControlDown() && !e.isShiftDown()) ||
                e.getKeyCode() == KeyEvent.VK_Z && e.isControlDown() && e.isShiftDown()) {
//            document_.getStateManager().redo();
            document_.redo();
            return;
        }

        ArrayList<AbstractGlyph> selectedGlyphs = document_.getBrowserMenu().getSelectedMap().getSelectedGlyphs();

        if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {

            if (selectedGlyphs == null || selectedGlyphs.isEmpty())
                return;

            document_.setChanged(true);
            new DocumentState(document_, "Delete Glyph(s)", false);
            removeArcConstraints(selectedGlyphs);
            document_.getPathway().removeGlyphs(selectedGlyphs, document_.getBrowserMenu().getSelectedMap());

            if (selectedGlyphs != null)
                for (AbstractGlyph glyph : selectedGlyphs) {
                    if (glyph instanceof Submap)
                        document_.getBrowserMenu().removeSubmap((Submap) glyph);
                    if (glyph instanceof Terminal){
                        Terminal t = (Terminal)glyph;
                        if (t.getParent() !=null)
                            t.getParent().removeTerminal(t);
                    }
                    if (glyph instanceof Tag){
                        Tag t = (Tag)glyph;
                        if (t.getParent() !=null)
                            t.getParent().removeTag(t);
                    }

                }

            document_.getCanvas().repaint();
        }

    }

    private void paste() {

        if (document_.getBrowserMenu().getSelectedMap().getLayers() == null ||
                document_.getBrowserMenu().getSelectedMap().getLayerCount() == 0)
            return;

        if (document_.getLayersMenu().getSelectedLayer() == null
                && document_.getBrowserMenu().getSelectedMap().getLayerCount() > 1)
            return;

        Layer selectedLayer = document_.getLayersMenu().getSelectedLayer();

        if (selectedLayer == null)
            selectedLayer = document_.getBrowserMenu().getSelectedMap().getLayerAt(0);

        ArrayList<AbstractGlyph> glyphs = ClipBoardManager.getGlyphs();
        //deselect copied items
        for (AbstractGlyph glyph : selectedLayer.getGlyphs()) glyph.setSelected(false);
        for (AbstractGlyph glyph : glyphs) {

            selectedLayer.add(glyph);

            if (glyph instanceof Submap)
                document_.getBrowserMenu().addSubmap((Submap) glyph, document_.getBrowserMenu().getSelectedMap());

        }

        document_.getStateManager().insert(new Command(CommandType.PASTING__GLYPHS, document_.getPathway().copy(),
                document_.getCanvas().getZoomFactor(), new Date().getTime()));
        new DocumentState(document_, "pasting glyph(s)", false);
        document_.getCanvas().repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
            document_.getCanvas().setState(CanvasStateType.NORMAL);
            document_.getCanvas().repaint();
        }

        super.keyReleased(e);
    }

    private void removeArcConstraints(ArrayList<AbstractGlyph> glyphs) {
        if (glyphs == null || glyphs.isEmpty())
            return;

        ArrayList<AbstractArc> arcs = new ArrayList<AbstractArc>();
        ArrayList<Boolean> isSource = new ArrayList<Boolean>();

        for (AbstractGlyph glyph : glyphs)
            if (glyph instanceof AbstractNode)
                for (Port port : ((AbstractNode) glyph).getPorts())
                    for (AbstractArc arc : port.getArcs())
                        if (arc.getSourcePort() == port) {
                            arcs.add(arc);
                            isSource.add(true);
                        } else if (arc.getTargetPort() == port) {
                            arcs.add(arc);
                            isSource.add(false);
                        }

        for (int i = 0; i < arcs.size(); i++)
            if (isSource.get(i))
                arcs.get(i).setSourcePort(null);
            else
                arcs.get(i).setTargetPort(null);

    }

}
