package edu.vt.beacon.editor.action.handler;

import edu.vt.beacon.editor.about.AboutDialog;
import edu.vt.beacon.editor.action.Action;

import java.awt.event.ActionEvent;

public class SimulateHandler implements ActionHandler {

    private static final SimulateHandler instance_ = new SimulateHandler();

    public static SimulateHandler getInstance() {
        return instance_;
    }

    @Override
    public void handle(Action action, ActionEvent event) {
        switch (action.getType()) {
            case SIMULATE_SIMULATE:
                new AboutDialog(action.getDocument());
                break;
            default:
                throw new IllegalStateException("missing action type case");
        }

    }

}
