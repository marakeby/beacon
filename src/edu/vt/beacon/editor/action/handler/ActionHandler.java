package edu.vt.beacon.editor.action.handler;

import java.awt.event.ActionEvent;

import edu.vt.beacon.editor.action.Action;

public interface ActionHandler
{
    // TODO document method
    public abstract void handle(Action action, ActionEvent event);
}