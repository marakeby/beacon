package edu.vt.beacon.editor.command;

import edu.vt.beacon.pathway.Pathway;

/**
 * Created by mostafa on 3/23/16.
 */
public class State {

    private Pathway pathway;
    private float zoomFactor;

    public State(Pathway pathway, float zoomFactor) {
        this.pathway = pathway;
        this.zoomFactor = zoomFactor;
    }

    public Pathway getPathway() {
        return pathway;
    }

    public float getZoomFactor() {
        return zoomFactor;
    }
}
