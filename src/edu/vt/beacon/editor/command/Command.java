package edu.vt.beacon.editor.command;

import edu.vt.beacon.pathway.Pathway;

/**
 * Created by mostafa on 3/23/16.
 */
public class Command {

    private CommandType type;
    private State currentState;
    private Long executionTime;

    public Command(CommandType type, State currentState, Long executionTime) {
        this.type = type;
        this.executionTime = executionTime;
    }

    public Command(CommandType type, Pathway pathway, float zoomFactor, Long executionTime) {
        this.type = type;
        currentState = new State(pathway, zoomFactor);
        this.executionTime = executionTime;
    }

    public CommandType getType() {
        return type;
    }

    public State getCurrentState() {
        return currentState;
    }

    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

    public Long getExecutionTime() {
        return executionTime;
    }
}
