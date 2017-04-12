package edu.vt.beacon.simulation.model.parser;

public class Symbol {

	// the type of the symbol
		public SymbolType type;
	    
		// additional information on a symbol,
		// e.g. the index of the associated variable
		public int additionalInfo;
	    
		// the start position of the symbol in the input string
		public int start;
	    
		/**
		 * Creates a new symbol with the following parameters:
		 * <type>: the type of the symbol
		 * <additionalInfo: additional information on a symbol,
		 * e.g. the index of the associated variable
		 * <start>: the start position of the symbol in the input string
		 */
		Symbol(SymbolType type, int additionalInfo, int start)
		{
			this.type = type;
			this.additionalInfo = additionalInfo;
			this.start = start;
		}
}
