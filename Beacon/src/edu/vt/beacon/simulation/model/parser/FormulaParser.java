package edu.vt.beacon.simulation.model.parser;

import edu.vt.beacon.simulation.model.tree.*;

import java.util.HashMap;
import java.util.Vector;

public class FormulaParser {
	
	HashMap<Integer,String> varNames;
    
	// The currently processed input string
	String currentInput;
    
	// A list of symbols identified in the input
	Vector<Symbol> symbols;
	
    
	// The index of the currently processed symbol in the list
	int symbolIndex;
	
	
	public FormulaParser(HashMap<Integer,String> varNames)
	{
		this.varNames = varNames;
		symbols = new Vector<Symbol>();
		varNames = new HashMap<Integer,String>();
	}
	/**
	 * Remove all symbols
	 */
	public void clearSymbols()
	{
		symbols.clear();
	}
	
	/**
	 * Previews the next symbol to process without
	 * moving the symbol pointer
	 * @throws ParseException 
	 */
	public Symbol previewSym() throws ParseException
	{
		if (symbolIndex == symbols.size() - 1)
			throw new ParseException("Unexpected end!", currentInput, currentInput.length() - 1);
		
		return symbols.elementAt(symbolIndex+1);
	}
	
	/**
	 * Reads the next symbol in the symbol list
	 * and move the symbol pointer.
	 * @throws ParseException 
	 */
	public Symbol popSym() throws ParseException
	{
		if (symbolIndex == symbols.size() - 1)
			throw new ParseException("Unexpected end!", currentInput, currentInput.length() - 1);
		return symbols.elementAt(++symbolIndex);
	}
	
	/**
	 * Returns the last symbol read.
	 * @throws ParseException 
	 */
	public Symbol currentSym() throws ParseException
	{
		if (symbolIndex == symbols.size())
			throw new ParseException("Unexpected end!", currentInput, currentInput.length() - 1);
		return symbols.elementAt(symbolIndex);
	}
	
	/**
	 * Determines whether all symbols have been read.
	 */
	public boolean eof()
	{
		return (symbolIndex == symbols.size() - 1);
	}
	
	/**
	 * Scans the input string for symbols, and fills
	 * the symbol list.
	 * @throws ParseException 
	 */
	public void scan() throws ParseException
	{
		symbolIndex = 0;
	    boolean tempMode = false;
		clearSymbols();
		int pos = 0;
		while (pos < currentInput.length())
		{
			// first, check for one-character symbols
			if (currentInput.charAt(pos) == ' ')
	            // ignore whitespace
				++pos;
	        else if (currentInput.charAt(pos) == '=')
	            //ignore "="
	            ++pos;
	        else if (currentInput.charAt(pos) == '.')
	            //recognize dashes
	        {
	            symbols.addElement(new Symbol(SymbolType.ST_DASH,0,pos));
	            ++pos;
	        }
	        else if (currentInput.charAt(pos) == ',')
	            //recognize comma
	        {
	            symbols.addElement(new Symbol(SymbolType.ST_COMMA,0,pos));
	            ++pos;
	        }
	        else if (currentInput.charAt(pos) == '(')
	            // recognize opening bracket
	        {
	            symbols.addElement(new Symbol(SymbolType.ST_OPENBRACKET,0,pos));
	            ++pos;
	        }
	        else if (currentInput.charAt(pos) == ')')
	            // recognize closing bracket
	        {
	            symbols.addElement(new Symbol(SymbolType.ST_CLOSEBRACKET,0,pos));
	            ++pos;
	        }
	        else if (currentInput.charAt(pos) == '&')
	            // recognize AND operator
	        {
	            symbols.addElement(new Symbol(SymbolType.ST_OPERATOR,OperatorType.OP_AND.no,pos));
	            ++pos;
	        }
	        else if (currentInput.charAt(pos) == '|')
	            // recognize OR operator
	        {
	            symbols.addElement(new Symbol(SymbolType.ST_OPERATOR,OperatorType.OP_OR.no,pos));
	            ++pos;
	        }
	        else if (currentInput.charAt(pos) == '!')
	            // recognize NOT operator
	        {
	            symbols.addElement(new Symbol(SymbolType.ST_NEGATION,0,pos));
	            ++pos;
	        }
	        else if (currentInput.charAt(pos) - '0' >= 0  && currentInput.charAt(pos) - '0' <= 9)
	            // recognize TRUE
	        {
	            symbols.addElement(new Symbol(SymbolType.ST_NUMBER,currentInput.charAt(pos) - '0',pos));
	            ++pos;
	        }
	        else if (currentInput.charAt(pos) == '[')
	            // recognize opening bracket for temporal expression
	        {
	            symbols.addElement(new Symbol(SymbolType.ST_OPENTEMP,0,pos));
	            tempMode = true;
	            ++pos;
	        }
	        else if (currentInput.charAt(pos) == ']')
	            // recognize opening bracket for temporal expression
	        {
	            symbols.addElement(new Symbol(SymbolType.ST_CLOSETEMP,0,pos));
	            tempMode = false;
	            ++pos;
	        }
	        else if (currentInput.charAt(pos) == '-' ||
	        			currentInput.charAt(pos) == '+')
	            // recognize number (for temporal expression)
	        {
	        	if(currentInput.charAt(pos) == '-')
	        		symbols.addElement(new Symbol(SymbolType.ST_MATH,0,pos));
	        	else
	        		symbols.addElement(new Symbol(SymbolType.ST_MATH,1,pos));
	            ++pos;
	        }
	        else if (tempMode && Character.isLetter(currentInput.charAt(pos)))
	        {
	            symbols.addElement(new Symbol(SymbolType.ST_VAR,currentInput.charAt(pos),pos));
	            ++pos;
	        }
	        else
	            // no one-character symbol
	        {
	            // find next whitespace or delimiter
	            int nextPos = pos + 1;
	            while (nextPos < currentInput.length() && currentInput.charAt(nextPos) != ' '
	                   && currentInput.charAt(nextPos) != ')' && currentInput.charAt(nextPos) != '('
	                   && currentInput.charAt(nextPos) != '[' && currentInput.charAt(nextPos) != ']')
	                ++nextPos;
	            
	            // extract symbol and convert it to upper case
	            String nextSym = currentInput.substring(pos,nextPos);
	            nextSym = nextSym.toUpperCase();
	            
	            if (nextSym.equals("ANY"))
	                //recognice ANY (represented as OR)
	            {
	                symbols.addElement(new Symbol(SymbolType.ST_OPERATOR,OperatorType.OP_OR.no,pos));
	            }
	            else if (nextSym.equals("ALL"))
	                //recognice ALL (represented as AND)
	            {
	                symbols.addElement(new Symbol(SymbolType.ST_OPERATOR,OperatorType.OP_AND.no,pos));
	            }
	            else if (nextSym.equals("AND"))
	                // recognize alternative AND
	            {
	                symbols.addElement(new Symbol(SymbolType.ST_OPERATOR,OperatorType.OP_AND.no,pos));
	            }
	            else if (nextSym.equals("OR"))
	                // recognize alternative OR
	            {
	                symbols.addElement(new Symbol(SymbolType.ST_OPERATOR,OperatorType.OP_OR.no,pos));
	            }
	            else if (nextSym.equals("XOR"))
	                // recognize XOR
	            {
	                symbols.addElement(new Symbol(SymbolType.ST_OPERATOR,OperatorType.OP_XOR.no,pos));
	            }
	            else if (nextSym.equals("NOT"))
	                // recognize alternative NOT
	            {
	                symbols.addElement(new Symbol(SymbolType.ST_NEGATION,0,pos));
	            }
	            else if (nextSym.equals("TRUE"))
	                // recognize alternative TRUE
	            {
	                symbols.addElement(new Symbol(SymbolType.ST_CONSTANT,-1,pos));
	            }
	            else if (nextSym.equals("FALSE"))
	                // recognize alternative FALSE
	            {
	                symbols.addElement(new Symbol(SymbolType.ST_CONSTANT,-2,pos));
	            }
	            else
	                // this must be a variable name
	            {
	                int litIdx;
	                Integer[] indices = varNames.keySet().toArray(new Integer[0]);
	                // lookup symbol in variable name list
	                for (litIdx = 0; litIdx < indices.length; ++litIdx)
	                {
	                    String geneName = varNames.get(indices[litIdx]);
	                    geneName = geneName.toUpperCase();
	                    
	                    if (geneName.equals(nextSym))
	                        break;
	                    
	                }
	                
	                if (litIdx == varNames.size()){
	                	
	                	// variable was not found
	                    throw new ParseException("Unknown symbol " + nextSym + "!",currentInput,pos);
	                }
	                else
	                    // add variable index as a symbol
	                    symbols.addElement(new Symbol(SymbolType.ST_CONSTANT,indices[litIdx],pos));
	                
	                
	            }
	            
	            pos = nextPos;
	        }
	        
	        
		}
	}
	
	public BooleanOperator op(boolean negated, Vector<Character> envVar) throws ParseException
	{
		// start with current symbol
		Symbol current = popSym();
		boolean childNegated = false;
	    BooleanOperator res = new BooleanOperator(OperatorType.OP_AND,null,negated);
	    
		
		BooleanFormula child;
	    
		int opType = -1;
	    Symbol next = previewSym();
		do
		{
			switch (current.type)
			{
	            case ST_CLOSETEMP:
	                throw new ParseException("Temporal information expected after literal",
	                                     currentInput,next.start);
	                
	            case ST_MATH:
	                throw new ParseException("Temporal information expected after literal",
	                                     currentInput,next.start);
	                
	            case ST_OPENTEMP:
	                //operation includes temporal information -> any/all instead of or/and
	                next = previewSym();
	                if(next.type != SymbolType.ST_VAR)
	                    throw new ParseException("Variable expected in temporal operator",
											 currentInput,next.start);
	                
	                break;
	            case ST_NUMBER:
	                next = previewSym();
					if (next.type != SymbolType.ST_OPERATOR && next.type != SymbolType.ST_CLOSEBRACKET && next.type != SymbolType.ST_OPENTEMP)
						throw new ParseException("Operator or \")\" expected!",
											 currentInput,next.start);
	                
	                //no temporal information added
	                if(next.type != SymbolType.ST_OPENTEMP)
	                    child = literal(childNegated, envVar);
	                else
	                    //with temporal information
	                    child = literalWithTemp(childNegated, envVar);
	                
					res.addNewOperand(child);
					child.setParent(res);
					childNegated = false;
					break;
	                
				case ST_OPENBRACKET:
					// a new operator starts here
					child = op(childNegated, envVar);
					if(((BooleanOperator)child).operatorType == OperatorType.OP_COMMA)
					{
						res.operatorType = OperatorType.OP_COMMA;
						for(BooleanFormula n : ((BooleanOperator)child).operands)
						{
							res.addNewOperand(n);
							n.setParent(res);
						}
					}
					else
					{
						res.addNewOperand(child);
						child.setParent(res);
					}
					childNegated = false;
					break;
				case ST_CONSTANT:
					// this is a literal
					// check for an operator or closing bracket
					next = previewSym();
					if (next.type != SymbolType.ST_OPERATOR && next.type != SymbolType.ST_CLOSEBRACKET && next.type != SymbolType.ST_OPENTEMP)
						throw new ParseException("Operator or \")\" expected!",
											 currentInput,next.start);
	                
	                //no temporal information added
	                if(next.type != SymbolType.ST_OPENTEMP)
	                    child = literal(childNegated, envVar);
	                else
	                    //with temporal information
	                    child = literalWithTemp(childNegated, envVar);
	                
					res.addNewOperand(child);
					child.setParent(res);
					childNegated = false;
					break;
				case ST_NEGATION:
					// this is the negation of an operand
					childNegated = !childNegated;
					// check for operand
					next = previewSym();
					if (next.type != SymbolType.ST_CONSTANT && next.type != SymbolType.ST_NEGATION
						&& next.type != SymbolType.ST_OPENBRACKET)
						throw new ParseException("Operand expected after negation!",currentInput,next.start);
					break;
				case ST_OPERATOR:
					// check whether the operand fits with other operands in the same bracket
					if (opType != -1 && opType != current.additionalInfo)
						throw new ParseException("Multiple different operators in an expression without proper bracketing!",
	                                         currentInput,current.start);
	                
					// check for operand
					next = previewSym();
					if (next.type != SymbolType.ST_CONSTANT && next.type != SymbolType.ST_NEGATION
						&& next.type != SymbolType.ST_OPENBRACKET && next.type != SymbolType.ST_OPENTEMP)
						throw new ParseException("Operand expected after operator!",currentInput,next.start);
					opType = current.additionalInfo;
	                
	                //read temporal information for operand
	                if (next.type == SymbolType.ST_OPENTEMP)
	                {
	                    opWithTemp(res, envVar);
	                    //envVar.remove(envVar.size() - 1);
	                }
	                
	                
	                
					break;
	            case ST_COMMA:
				//add another operand to operator
	                opWithComma(res, envVar);
	                
	                break;
	            default:
					throw new ParseException("Unexpected symbol!",currentInput,current.start);
			}
			// proceed to next symbol
			current = popSym();
		}
		while (current.type != SymbolType.ST_CLOSEBRACKET);
	    
	    if (opType != -1)
			res.operatorType = OperatorType.values()[opType];
	    
		return res;
	}
	
	public Literal literalWithTemp(boolean negated, Vector<Character> envVar) throws ParseException
	{
	    //pop literal
	    Symbol current = currentSym();
	    Literal res;
	    boolean minus = false;
	    boolean blockNumbers = false;
	    int numCount = -1;
	    Vector<Integer> numbers = new Vector<Integer>();
	    //pop bracket
	    Symbol temp = popSym();
	    
	    //copy envVar vector
	    Vector<Character> envVarCheck = new Vector<Character>();
	    
	    for(Character c : envVar)
	    {
	    	envVarCheck.add(c);
	    }
	    
	    
	    if(temp.type != SymbolType.ST_OPENTEMP)
	        throw new ParseException("Temporal information expected", currentInput, temp.start);
	    
	    while (temp.type != SymbolType.ST_CLOSETEMP)
	    {
	        //pop next
	        temp = popSym();
	        
	        //check environmend variables
	        if(envVar.size() != 0)
	        {
	            if (temp.type == SymbolType.ST_VAR)
	            {
	                int it = envVarCheck.indexOf((char)temp.additionalInfo);
	            	
	                if(it != -1)
	                	envVarCheck.remove(it);
	                
	                
	                if(numbers.size() > 0)
	                	blockNumbers = true;
	            }
	        }
	        
	        //number is negative
//	        if(temp.type == SymbolType.ST_MATH && temp.additionalInfo == 0) //minus
//	        {
//	        	if(numbers.size() > 0)
//                	blockNumbers = true;
//	        	
//	            minus = !minus;
//	        }
	        if(temp.type == SymbolType.ST_MATH)
	        {
	        	Symbol nxt = previewSym();
	        	
	        	if(nxt.type == SymbolType.ST_VAR && temp.additionalInfo != 1)
	        		throw new ParseException("Only additive concatenation allowed for variables", currentInput, temp.start);
	             		
	        	if(temp.additionalInfo == 0)
	        		minus = !minus;
	        	
	        	if(numbers.size() > 0)
	        		blockNumbers = true;
	        }
	        
	        //store number in vector
	        if(temp.type == SymbolType.ST_NUMBER)
	        {
	        	if(blockNumbers)
	        		throw new ParseException("Only one integer in statement allowed", currentInput, temp.start);
	        	
	            numbers.add(temp.additionalInfo);
	            ++numCount;
	        }
	        
	        if(temp.type != SymbolType.ST_MATH && temp.type != SymbolType.ST_NUMBER && temp.type != SymbolType.ST_CLOSETEMP && temp.type != SymbolType.ST_VAR)
	            throw new ParseException("Temporal information includes incorrect type", currentInput, temp.start);
	        
	    }
	    
	    if (!envVarCheck.isEmpty())
	        //if not all variables are used in this literal throw error
	        throw new ParseException("Variables declared in operator are not used in this literal", currentInput, temp.start);
	    
	    //sum number
	    int value = 0;
	    for (int i = 0; i <= numCount; i++){
	        value += numbers.lastElement() * Math.pow((double)10, i);
	        numbers.remove(numbers.size() - 1); //pop last element
	    }
	    if(minus)
	        value = value * (-1);
	    
	    
	    if(envVar.isEmpty() && value > -1)
	        throw new ParseException("Time value of literal must be <= -1", currentInput, temp.start);
	    
	    //no temporal information . timeStep is set to -1
	    if (current.type == SymbolType.ST_NUMBER && current.additionalInfo == 1)
	        // create a TRUE constant, if following symbol is a number with value 1
	    {
	        res = new Literal(-1, null, negated, value);
	    }
	    else
	        if (current.type == SymbolType.ST_NUMBER && current.additionalInfo == 0)
	            // create a FALSE constant, if following symbol is a number with value 0
	        {
	            res = new Literal(-1, null, !negated, value);
	        }
	        else
	            //with temporal information
	        {
	            res = new Literal(current.additionalInfo, null, negated, value);
	        }
	    
	    return res;
	    
	}
	
	public Literal literal(boolean negated, Vector<Character> envVar) throws ParseException
	{
		Symbol current = currentSym();
		Literal res;
	    
	    //no temporal information . timeStep is set to -1
	    if (current.type == SymbolType.ST_NUMBER && current.additionalInfo == 1)
	        // create a TRUE constant, if following symbol is a number with value 1
	    {
	        res = new Literal(-1, null, negated);
	    }
	    else
	    {
	        if (!envVar.isEmpty())
	            //if not all variables are used in this literal throw error
	            throw new ParseException("Variables declared in operator are not used in this literal", currentInput, current.start);
	    
	        if (current.type == SymbolType.ST_NUMBER && current.additionalInfo == 0)
	            // create a FALSE constant, if following symbol is a number with value 0
	        {
	            res = new Literal(-1, null, !negated);
	        }
	        else
	            // create a variable literal
	        {
	            res = new Literal(current.additionalInfo, null, negated);
	        }
	    
	    }
	    
		return res;
	}
	
	
	public void opWithTemp(BooleanOperator op, Vector<Character> envVar) throws ParseException
	{
	    
	    //Symbol * current = currentSym();
	    
	    //pop bracket
	    Symbol temp = popSym();
	    Vector<Integer> numbers = new Vector<Integer>();
	    int numCount = -1;
	    boolean lowerSet = false;
	    if (temp.type != SymbolType.ST_OPENTEMP)
	        throw new ParseException("Temporal information expected", currentInput, temp.start);

	    //pop variable
	    temp = popSym();
	    
	    if (temp.type != SymbolType.ST_VAR)
	        throw new ParseException("Variable expected", currentInput, temp.start);
	    
	    op.envVar = (char)temp.additionalInfo;
	    
	    //check if variable is already declared . throw arrow
	    for (int i = 0; i < envVar.size(); i++)
	    {
	        if (op.envVar == envVar.elementAt(i))
	            throw new ParseException("Variable is declared more than once", currentInput, temp.start);
	    }
	    
	    //add var to list
	    envVar.addElement((char)temp.additionalInfo);
	    
	    
	    
	    boolean minus = false;
	    while (temp.type != SymbolType.ST_CLOSETEMP)
	    {
	        temp = popSym();
	        
	        if (temp.type == SymbolType.ST_MATH)
	            minus = !minus;
	    
	        //store number in vector
	        if(temp.type == SymbolType.ST_NUMBER)
	        {
	            numbers.addElement(temp.additionalInfo);
	            ++numCount;
	        }
	        
	        
	        if(temp.type == SymbolType.ST_DASH)
	        {
	            
	             //lower limit already set, skip other dashes
	            if (lowerSet)
	                continue;
	            
	            //sum number
	            int value = 0;
	            for (int i = numCount; i >= 0; i--){
	                value += numbers.lastElement() * Math.pow((double)10, (int)numCount);
	                numbers.remove(numbers.size() - 1);
	            }
	            if(minus)
	                value = value * (-1);
	            
	            op.timeLowerLimit = value;
	            
	            numCount = -1;
	            numbers.clear();
	            minus = false;
	            lowerSet = true;
	            
	        }
	        
	        if(temp.type != SymbolType.ST_MATH && temp.type != SymbolType.ST_NUMBER && temp.type != SymbolType.ST_CLOSETEMP && temp.type != SymbolType.ST_DASH)
	            throw new ParseException("Temporal information includes incorrect type", currentInput, temp.start);
	            
	        
	    }
	    
	    
	    //sum number
	    int value = 0;
	    for (int i = numCount; i >= 0; i--){
	        value += numbers.lastElement() * Math.pow((double)10, (int)numCount);
	        numbers.remove(numbers.size() - 1);
	    }
	    if(minus)
	        value = value * (-1);
	    
	    op.timeUpperLimit = value;
	    
	    //switch values if upper limit is smaller than lower
	    if(value < op.timeLowerLimit)
	    {
	        op.timeUpperLimit = op.timeLowerLimit;
	        op.timeLowerLimit = value;
	    }
	    
	    
	    
	    

	    
	}
	
	
	public void opWithComma(BooleanOperator operand, Vector<Character> envVar) throws ParseException
	{

		boolean childNegated = false;
	    Symbol next = currentSym();
	    
	    //set operator to type comma
	    operand.operatorType = OperatorType.OP_COMMA;
	    
		BooleanFormula child;
	    if  (operand.operands.size() == 0)
	        //if comma comes before first operand . error
	        throw new ParseException("Comma must follow another operand!", currentInput, next.start);

	    Symbol current = popSym();
	    next = previewSym();
	    
	    if (current.type != SymbolType.ST_CONSTANT && current.type != SymbolType.ST_NEGATION
	        && current.type != SymbolType.ST_OPENBRACKET && current.type != SymbolType.ST_NUMBER)
	        throw new ParseException("Operand expected after comma!",currentInput,next.start);
	    
	    next = previewSym();
	    
	    if (next.type != SymbolType.ST_COMMA && next.type != SymbolType.ST_NEGATION
	        && next.type != SymbolType.ST_OPENBRACKET && next.type != SymbolType.ST_CLOSEBRACKET && next.type != SymbolType.ST_OPENTEMP && next.type != SymbolType.ST_CONSTANT && next.type != SymbolType.ST_NUMBER)
	        throw new ParseException("Wrong symbol following operand!",currentInput,next.start);
	  
	    while (current.type == SymbolType.ST_NEGATION)
	        // this is the negation of an operand
	    {
	        current = popSym();
	        next = previewSym();
	        childNegated = !childNegated;
	        
	    }
	    
	    if (next.type == SymbolType.ST_CLOSEBRACKET || next.type == SymbolType.ST_COMMA)
	        //must be literal wO temporal information
	    {
//	        child = literal(childNegated);
	        //literal in temporal operator must have temporal information too
	        throw new ParseException("Literal in temporal operator must include temporal variable!",currentInput,next.start);
	    }
	    else if ((current.type == SymbolType.ST_CONSTANT || current.type == SymbolType.ST_NUMBER) && next.type == SymbolType.ST_OPENTEMP)
	        //literal w temporal information
	    {
	        child = literalWithTemp(childNegated, envVar);
	    }
	    else if (current.type == SymbolType.ST_OPENBRACKET)
	        //new operator
	    {
	        child = op(childNegated, envVar);
	    }
	    else
	        throw new ParseException("Wrong input after comma!", currentInput, next.start);
	    
	    operand.addNewOperand(child);
	    child.setParent(operand);
	    childNegated = false;
	}
	
	public BooleanFormula parse(String input) throws ParseException
	{
		// remove leading and trailing blanks and comments
		int commIndex = input.indexOf("#");
		if(commIndex == -1)
			commIndex = input.length();
		
		input = input.substring(0, commIndex);
		
		if(commIndex == 0)
			input = "";
		
		input = input.trim();
		this.currentInput = input;
	    
		if (input.length() == 0)
			return null;
	    
		// convert to upper case
		String tmp = input.toUpperCase();
		
	    
		if (tmp == "UNKNOWN" || tmp == "(UNKNOWN)")
	        // special case: unknown formula
			return new Literal(-2,null,false);
	    
		// check whether the expression is surrounded by brackets
		boolean brackets = (input.charAt(0) == '(' && input.charAt(input.length()-1) == ')');
		if (brackets && input.length() >= 4)
		{
			int bracketCount = 0;
			for (int i = 1; i < input.length() - 1; ++i)
			{
				if (input.charAt(i) == '(')
					++bracketCount;
				else
	                if (input.charAt(i) == ')' && bracketCount > 0)
	                    --bracketCount;
			}
			if (bracketCount != 0)
				brackets = false;
		}
		// add brackets if necessary
		if (!brackets)
			this.currentInput = '(' + this.currentInput + ')';
	    
		// scan symbols
		scan();
		if (symbols.size() == 0)
			return null;
	    
	    Vector<Character> envVars = new Vector<Character>();
		Symbol current = currentSym();
		boolean negated = false;
		BooleanFormula  res = null;
		Symbol next = previewSym();
		do
		{
			switch (current.type)
			{
	            case ST_OPENTEMP:
	                throw new ParseException("Temporal information expected after literal",
	                                     currentInput,next.start);
	                
	            case ST_CLOSETEMP:
	                throw new ParseException("Temporal information expected after literal",
	                                     currentInput,next.start);
	                
	            case ST_MINUS:
	                throw new ParseException("Temporal information expected after literal",
	                                     currentInput,next.start);
	                
	            case ST_NUMBER:
	                next = previewSym();
					if (next.type != SymbolType.ST_OPERATOR && next.type != SymbolType.ST_CLOSEBRACKET && next.type != SymbolType.ST_OPENTEMP)
						throw new ParseException("Operator or \")\" expected!",
											 currentInput,next.start);
	                
	                //no temporal information added
	                if (next.type != SymbolType.ST_OPENTEMP)
	                    res = literal(negated, envVars);
	                else
	                    //with temporal information
	                    res = literalWithTemp(negated, envVars);
	                
					negated = false;
					break;
	            case ST_OPENBRACKET:
					// this is the beginning of an operator
					res = op(negated, envVars);
					negated = false;
					break;
				case ST_CONSTANT:
					// this is the beginning of a literal
	                next = previewSym();
	                if (next.type != SymbolType.ST_OPENTEMP)
	                    res = literal(negated, envVars);
	                else
	                    res = literalWithTemp(negated, envVars);
	                
					negated = false;
					break;
				case ST_NEGATION:
					// this is the beginning of a negated operand
					negated = !negated;
					next = previewSym();
					if (next.type != SymbolType.ST_CONSTANT && next.type != SymbolType.ST_NEGATION
						&& next.type != SymbolType.ST_OPENBRACKET)
						throw new ParseException("Operand expected after negation!",currentInput,next.start);
					break;
				default:
					throw new ParseException("Unexpected symbol!",currentInput,current.start);
			}
			if (!eof())
				current = popSym();
		}
		while (res == null);
	    
	    
	    
		if (!eof())
	        // the expression is longer than expected
			throw new ParseException("Unexpected symbol!",currentInput,previewSym().start);
	    
	    if (!checkTree(res))
	        //time shifts in boolean formula is wrong
	        throw new ParseException("Timepoint > -1 must not exist",currentInput,0);
	        
		if (res.getClass().getName().equals("model.tree.BooleanOperator"))
		{
			BooleanOperator temp = (BooleanOperator) res;
			if (temp.operands.size() == 1 && temp.timeLowerLimit == temp.timeUpperLimit ) //&& (temp.operands.elementAt(0).getClass().getName() == "BooleanOperator")
	            // if operator only consists of one element and is not temporal, replace it by operand
			{
				res = temp.operands.elementAt(0).copy(null);
				
			}
			
		}

		return res;
	}
	
	/**
	 * Checks if temporal offset is valid for formula (<-1)
	 * @param root
	 * @return
	 */
	public static boolean checkTree(BooleanFormula root)
	{
	    BooleanTree temp = new BooleanTree(root,1);
	    int noElements = temp.getNumberOfElements();
	    
	    //iterate over elements in formula
	    for (int e = 0; e < noElements; e++)
	    {
	        
	        TreePosition current = temp.getElementByIndex(e);

	        
	        if (current.element.getClass().getName() == "model.tree.Literal")
	        //search for literals

	        	//check if literals with timesteps = 0 have parents that shift them by at least -1
	        {
	        	int sumShift = 0;
	        	BooleanFormula par = current.parent;

	        	while (par != null)
	        	{
	        		sumShift += ((BooleanOperator)par).timeUpperLimit;
	        		par = ((BooleanOperator)par).parent;
	        	}

	        	sumShift += ((Literal)current.element).timeSteps;
	        	if (sumShift > -1)
	        		//tree is not valid
	        		return false;
	        }
	        
//	        if (current.element.getClass().getName() == "model.tree.BooleanOperator")
//	        {    //search for literals
//	        	double max = Math.max(((BooleanOperator)current.element).timeUpperLimit,((BooleanOperator)current.element).timeLowerLimit);
//		            if (max >= 0)
//		                //check if literals with timesteps = 0 have parents that shift them by at least -1
//		            {
//		                int sumShift = 0;
//		                BooleanFormula par = current.parent;
//		                
//		                while (par != null)
//		                {
//		                    sumShift += ((BooleanOperator)par).timeUpperLimit;
//		                    par = ((BooleanOperator)par).parent;
//		                }
//		                
//		                sumShift += max;
//		                if (sumShift > 0)
//		                    //tree is not valid
//		                    return false;
//		            }
//	        }
	    }
	    
	    return true;
	}

}
