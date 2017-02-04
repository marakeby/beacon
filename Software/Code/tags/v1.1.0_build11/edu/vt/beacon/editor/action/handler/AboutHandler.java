package edu.vt.beacon.editor.action.handler;

import edu.vt.beacon.editor.about.AboutDialog;
import edu.vt.beacon.editor.action.Action;

import java.awt.event.ActionEvent;

public class AboutHandler implements ActionHandler {

    private static final AboutHandler instance_ = new AboutHandler();

    public static AboutHandler getInstance() {
        return instance_;
    }

    @Override
    public void handle(Action action, ActionEvent event) {
        switch (action.getType()) {
            case ABOUT_ABOUT:
                new AboutDialog(action.getDocument());
                break;
            default:
                throw new IllegalStateException("missing action type case");
        }

    }

}
