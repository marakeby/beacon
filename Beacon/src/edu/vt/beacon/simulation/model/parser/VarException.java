package edu.vt.beacon.simulation.model.parser;

public class VarException extends Exception {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new parser exception highlighting
	 * the position of the error in the input.
	 * <error> is the error message.
	 * <input> is the input string.
	 * <position> denotes the position of the error in the input string.
	 */
	VarException(String error)
	{
		super("Parsing error: " + error + "\n");
	}
}
