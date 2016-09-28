package edu.vt.beacon.editor.util;

import edu.vt.beacon.editor.command.Command;
import edu.vt.beacon.editor.command.CommandType;
import edu.vt.beacon.editor.command.State;
import edu.vt.beacon.editor.document.Document;

import java.util.LinkedList;

/**
 * Created by mostafa on 3/23/16.
 */
public class StateManager {

    private int historyCapacity = 100;
    private LinkedList<Command> commandHistory;
    private static StateManager singleton = null;
    private int currentStateIndex;
    private static Document document;

    public static StateManager getInstance(Document document) {
        if (singleton == null) singleton = new StateManager();

        StateManager.document = document;
        return singleton;
    }

    private StateManager() {
        commandHistory = new LinkedList<Command>();
        currentStateIndex = 0;
    }

    public void undo() {
        if (currentStateIndex > 0)
            currentStateIndex--;

        applyState();
    }

    public void redo() {
        if (currentStateIndex < (commandHistory.size() - 1))
            currentStateIndex++;

        applyState();
    }

    private void applyState() {
        if (currentStateIndex == 0 && commandHistory.isEmpty())
            return;

        if (document == null)
            return;

        State state = commandHistory.get(currentStateIndex).getCurrentState();
        document.setPathway(state.getPathway().copy());
        document.getCanvas().setZoomFactor(state.getZoomFactor());

        document.refresh();
    }

    public void insert(Command command) {
        if (command == null)
            return;

        if (command.getType() == CommandType.SAVING_PATHWAY)
            return;

        if (command.getType() == CommandType.OPENING_PATHWAY || command.getType() == CommandType.CREATING_PATHWAY) {
            commandHistory.clear();
            addToCommandHistory(command, false);
            return;
        }

        if (currentStateIndex == (commandHistory.size() - 1)) {

            if (command.getType() == CommandType.DRAGGING && commandHistory.getLast().getType() == CommandType.DRAGGING) {

                if (shouldSaveNewDragging(command, commandHistory.getLast()))
                    addToCommandHistory(command, false);
                else
                    addToCommandHistory(command, true);

            } else
                addToCommandHistory(command, false);

        } else {

            for (int i = currentStateIndex + 1; i < commandHistory.size();)
                commandHistory.removeLast();

            addToCommandHistory(command, false);

        }

    }

    private void addToCommandHistory(Command command, boolean removeLast) {
        if (command == null || command.getCurrentState() == null)
            return;

        if (removeLast) {
            commandHistory.removeLast();
            commandHistory.add(command);
            return;
        }

        if (commandHistory.size() < historyCapacity) {
            commandHistory.add(command);
            if (commandHistory.size() > 1)
                currentStateIndex++;
            return;
        }

        commandHistory.removeFirst();
        commandHistory.add(command);
    }

    private boolean shouldSaveNewDragging(Command newDraggingCommand, Command oldDraggingCommand) {
        int timeDifferenceInMilliSeconds = 2000;

        if (newDraggingCommand == null)
            return false;

        if (oldDraggingCommand == null || oldDraggingCommand.getType() != CommandType.DRAGGING)
            return true;

        return (newDraggingCommand.getExecutionTime() - oldDraggingCommand.getExecutionTime()) > timeDifferenceInMilliSeconds;
    }
}
