package edu.vt.beacon.simulation.model.parser;

public class ParseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String message;
	


	/**
	 * Creates a new parser exception highlighting
	 * the position of the error in the input.
	 * <error> is the error message.
	 * <input> is the input string.
	 * <position> denotes the position of the error in the input string.
	 */
	ParseException(String error, String input, int position)
	{
		super();
		String out = "";
		out += "Parsing error: " + error + "\n";
		out += input + "\n";
		for (int i = 0; i < position; ++i)
			out += " ";
		out += "^";
		this.message = out;
		this.initCause(new Throwable(message));
		
	}


}
