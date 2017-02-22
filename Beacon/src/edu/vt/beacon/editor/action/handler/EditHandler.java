package edu.vt.beacon.editor.action.handler;

import edu.vt.beacon.editor.action.Action;
import edu.vt.beacon.editor.action.ActionType;
import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.document.DocumentState;
import edu.vt.beacon.graph.glyph.AbstractGlyph;
import edu.vt.beacon.graph.glyph.node.annotation.Annotation;
import edu.vt.beacon.graph.glyph.node.container.Compartment;
import edu.vt.beacon.graph.glyph.node.submap.Submap;
import edu.vt.beacon.graph.glyph.node.submap.Terminal;
import edu.vt.beacon.layer.Layer;
import edu.vt.beacon.map.Map;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class EditHandler
        implements ActionHandler {
    private static final EditHandler instance_ = new EditHandler();

    // TODO document method
    public static EditHandler getInstance() {
        return instance_;
    }

    // FIXME complete method
    @Override
    public void handle(Action action, ActionEvent event) {
        switch (action.getType()) {

            case EDIT_REDO:
                redo(action.getDocument());
                break;

            case EDIT_UNDO:
                undo(action.getDocument());
                break;

            case EDIT_ALIGNMENT:
                break;

            case EDIT_LEFT_ALIGNMENT:
                align(action, ActionType.EDIT_LEFT_ALIGNMENT);
                break;

            case EDIT_RIGHT_ALIGNMENT:
                align(action, ActionType.EDIT_RIGHT_ALIGNMENT);
                break;

            case EDIT_TOP_ALIGNMENT:
                align(action, ActionType.EDIT_TOP_ALIGNMENT);
                break;

            case EDIT_BOTTOM_ALIGNMENT:
                align(action, ActionType.EDIT_BOTTOM_ALIGNMENT);
                break;

            default:
                throw new IllegalStateException("missing action type case");
        }
    }

    private void align(Action action, ActionType type) {

        if (type != ActionType.EDIT_BOTTOM_ALIGNMENT && type != ActionType.EDIT_TOP_ALIGNMENT &&
                type != ActionType.EDIT_LEFT_ALIGNMENT && type != ActionType.EDIT_RIGHT_ALIGNMENT)
            return;

        ArrayList<AbstractGlyph> selectedGlyphs = action.getDocument().getBrowserMenu().getSelectedMap().getSelectedGlyphs();
        if (selectedGlyphs == null || selectedGlyphs.size() <= 1)
            return;

        float movementCoordinate = 0;
        if (type == ActionType.EDIT_BOTTOM_ALIGNMENT || type == ActionType.EDIT_RIGHT_ALIGNMENT)
            movementCoordinate = Float.MIN_VALUE;
        else
            movementCoordinate = Float.MAX_VALUE;

        for (AbstractGlyph glyph : selectedGlyphs) {

            if (type == ActionType.EDIT_LEFT_ALIGNMENT && movementCoordinate > glyph.getMinX())
                movementCoordinate = glyph.getMinX();

            else if (type == ActionType.EDIT_RIGHT_ALIGNMENT && movementCoordinate < glyph.getMaxX())
                movementCoordinate = glyph.getMaxX();

            else if (type == ActionType.EDIT_TOP_ALIGNMENT && movementCoordinate > glyph.getMinY())
                movementCoordinate = glyph.getMinY();

            else if (type == ActionType.EDIT_BOTTOM_ALIGNMENT && movementCoordinate < glyph.getMaxY())
                movementCoordinate = glyph.getMaxY();

        }

        for (AbstractGlyph glyph : selectedGlyphs) {

            if (type == ActionType.EDIT_LEFT_ALIGNMENT)
                glyph.move(movementCoordinate - glyph.getMinX(), 0);

            else if (type == ActionType.EDIT_RIGHT_ALIGNMENT)
                glyph.move(movementCoordinate - glyph.getMaxX(), 0);

            else if (type == ActionType.EDIT_TOP_ALIGNMENT)
                glyph.move(0, movementCoordinate - glyph.getMinY());

            else if (type == ActionType.EDIT_BOTTOM_ALIGNMENT)
                glyph.move(0, movementCoordinate - glyph.getMaxY());

        }

        action.getDocument().setChanged(true);
        new DocumentState(action.getDocument(), "Alignment", false);
        action.getDocument().getCanvas().repaint();

    }

    // TODO document method
    public void redo(Document document) {
//        if (document.getState().getNext() != null)
//            document.getState().getNext().apply(document);
        document.redo();
    }

    // TODO document method
    public void selectNone(Document document, boolean shouldRepaint) {
        Layer layer;
        Map map = document.getBrowserMenu().getSelectedMap();

        for (int i = 0; i < map.getLayerCount(); i++) {

            layer = map.getLayerAt(i);

            if (layer.isActive()) {

                for (int j = 0; j < layer.getGlyphCount(); j++) {

                    AbstractGlyph glyph = layer.getGlyphAt(j);
                    glyph.setSelected(false);

                    if (glyph instanceof Compartment)
                        ((Compartment) glyph).getLabel().setSelected(false);

                    else if (glyph instanceof Annotation && ((Annotation) glyph).getCalloutPoint() != null)
                        ((Annotation) glyph).getCalloutPoint().setSelected(false);

                    else if (glyph instanceof Submap && ((Submap) glyph).getTerminals() != null) {
                        for (Terminal terminal : ((Submap) glyph).getTerminals())
                            terminal.setSelected(false);
                    }

                }
            }
        }

        if (shouldRepaint)
            document.getCanvas().repaint();
    }

    // TODO document method
    public void undo(Document document) {
//        if (document.getState().getPrevious() != null)
//            document.getState().getPrevious().apply(document);
        document.undo();
    }
}