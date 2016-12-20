package edu.vt.beacon.editor.action.handler;

import edu.vt.beacon.editor.action.Action;
import edu.vt.beacon.editor.dialog.canvas.CanvasDialog;
import edu.vt.beacon.editor.dialog.font.FontDialog;
import edu.vt.beacon.editor.dialog.label.LabelDialog;
import edu.vt.beacon.editor.dialog.legend.LegendDialog;
import edu.vt.beacon.editor.dialog.shape.ShapeDialog;

import java.awt.event.ActionEvent;

public class FormatHandler
        implements ActionHandler {
    private static final FormatHandler instance_ = new FormatHandler();

    // TODO document method
    public static FormatHandler getInstance() {
        return instance_;
    }

    // FIXME complete method
    @Override
    public void handle(Action action, ActionEvent event) {
        switch (action.getType()) {
            case FORMAT_LEGEND:
                new LegendDialog(action.getDocument());
                break;
            case FORMAT_CANVAS:
                new CanvasDialog(action.getDocument());
                break;
            case FORMAT_SHAPE:
                new ShapeDialog(action.getDocument());
                break;
            case FORMAT_FONT:
                new FontDialog(action.getDocument());
                break;
            case FORMAT_LABEL:
                LabelDialog.createDialog(action.getDocument());
                break;
            default:
                throw new IllegalStateException("missing action type case");
        }
    }
}
