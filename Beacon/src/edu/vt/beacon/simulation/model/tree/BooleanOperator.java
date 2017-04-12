package edu.vt.beacon.simulation.model.tree;


import edu.vt.beacon.simulation.model.containers.TemporalMemoryWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class BooleanOperator extends BooleanFormula {
	
	//number of elements of this node
	private int numberOfSubtreeElements;
	
	// The parent expression
	//public BooleanOperator  parent;

	// The type of the operator, i.e. AND/OR/XOR
	public OperatorType operatorType;

	// Time range for operator
	public int timeLowerLimit;
	public int timeUpperLimit;
	
	// Variable of this environment (for temporal operators)
	public char envVar;

	// The operands of the operator (usually >=2)
	public Vector<BooleanFormula > operands;
	
	/**
	 * Creates a new BooleanOperator with parameters
	 * <operatorType>: The type of the operator, i.e. AND/OR/XOR
	 * <parent>: The parent expression
	 * <negated>: Is the operator negated or not?
	 */
	public BooleanOperator(OperatorType operatorType, BooleanOperator parent, boolean negated)
	{
		this.envVar = 0;
		this.operatorType = operatorType;
		this.parent = parent;
		this.negated = negated;
		this.numberOfSubtreeElements = 0;
		this.timeLowerLimit = 0;
		this.timeUpperLimit = 0;
		this.operands = new Vector<BooleanFormula>();
		if(parent != null)
			parent.addNewOperand(this);
	}
	
    public BooleanOperator(OperatorType operatorType, BooleanOperator parent, boolean negated, int lower, int upper, char var)
    {
    	this.operatorType = operatorType;
		this.parent = parent;
		this.negated = negated;
		this.timeLowerLimit = lower;
		this.timeUpperLimit = upper;
		this.envVar = var;
		this.numberOfSubtreeElements = 0;
		this.operands = new Vector<BooleanFormula>();
		if(parent != null)
			parent.addNewOperand(this);
    }

	@Override
	public boolean evaluate(boolean[] variableSet, Vector<Integer> temporalMemory, int[] position, int timeShift, HashMap<Integer, Integer> indiceMapper) 
	{
		boolean res;
		switch (operatorType)
		{
			case OP_AND:
				res = true;
				for (BooleanFormula it : operands)
				{
	                for (int i = timeLowerLimit ; i <= timeUpperLimit; i++)
	                    //evaluate operand over given range of time
	                {
	                    // evaluate operand
	                    res &= it.evaluate(variableSet, temporalMemory, position, timeShift + i, indiceMapper);
	                }
					
	                if (!res)
	                    // break if any operand is false
						return (negated);
				}
				return (!negated);
			case OP_OR:
				res = false;
				for (BooleanFormula it : operands)
				{
					for (int i = timeLowerLimit ; i <= timeUpperLimit; i++)
	                    //evaluate operand over given range of time
	                {
	                    // evaluate operand
	                    res |= it.evaluate(variableSet, temporalMemory, position, timeShift + i, indiceMapper);
	                }
					if (res)
						// break if any operand is true
						return (!negated);
				}
				return (negated);
			case OP_XOR:
				if (operands.size() == 0)
					return false;
				res = operands.elementAt(0).evaluate(variableSet, temporalMemory, position, timeShift, indiceMapper);
				for (int i = 1; i < operands.size(); ++i)
				{
					boolean next = operands.elementAt(i).evaluate(variableSet, temporalMemory, position, timeShift, indiceMapper);
					res = (res && !next) || (!res && next);
				}
				if (negated)
					return !res;
				else
					return res;
	        default :
	            break;
		}
		return false;
	
	}

	@Override
	public String toString(HashMap<Integer,String> variableNames, Vector<Character> environmentVariables) 
	{
		if (operands.size() == 0)
			return "UNKNOWN";
	    
		String res = "";
	    
		if (negated)
			res += "!";
	    
		res += "(";
	    
	    if (envVar != 0)
	    //temporal operator
	    {
	        switch (operatorType) {
	            case OP_AND:
	                res += "all" + "[" + envVar + " = " + timeLowerLimit + ".." + timeUpperLimit + "]";
	                break;
	            case OP_OR:
	                res += "any" + "[" + envVar + "=" + timeLowerLimit + ".." + timeUpperLimit + "]";
	                break;
	            case OP_XOR:
	                break;
	            default: //comma operator does not exist here anyway
	                break;
	        }
	        
	        if (operands.size() > 1 || (operands.size() == 1 && operands.elementAt(0).getClass().getName() == "model.tree.Literal"))
	            res += "(" ;
	        
	        for (int i = 0; i < operands.size(); i++)
	        {
	            //search for environment variables in parents
	            environmentVariables.clear();
	            BooleanFormula parRef = parent;
	            while (parRef != null)
	            {
	                if  (((BooleanOperator)parRef).envVar != 0)
	                    environmentVariables.add(((BooleanOperator)parRef).envVar);
	                
	                parRef = parRef.parent;
	            }
	            //push own variable
	            environmentVariables.add(envVar);
	            
	            //last one -> no comma
	            if (i == operands.size() - 1)
	            {
	                res += operands.elementAt(i).toString(variableNames, environmentVariables);
	                if (operands.size() > 1 || (operands.size() == 1 && operands.elementAt(i).getClass().getName() == "model.tree.Literal"))
	                    res += ")" ;
	            }
	            else
	                res += operands.elementAt(i).toString(variableNames, environmentVariables) + ",";
	        }
	        
	    }
	    else
		//"normal operator"
	    {
	        for (int i = 0; i < operands.size(); ++i)
	        {
	            if (i != 0)
	            {
	                switch (operatorType)
	                {
	                    case OP_AND:
	                        res += " & ";
	                        break;
	                    case OP_OR:
	                        res += " | ";
	                        break;
	                    case OP_XOR:
	                        res += " XOR ";
	                        break;
	                    default: //comma operator does not exist here anyway
	                        break;
	                }
	            }
	            //search for environment variables in parents
	            environmentVariables.clear();
	            BooleanFormula parRef = parent;
	            while (parRef != null)
	            {
	                if  (((BooleanOperator)parRef).envVar != 0)
	                    environmentVariables.add(((BooleanOperator)parRef).envVar);
	                
	                parRef = parRef.parent;
	            }
	            res += operands.elementAt(i).toString(variableNames, environmentVariables);
	        }
	    }
	    environmentVariables.clear();
		res += ")";
		return res;	
	}

	@Override
	public BooleanFormula copy(BooleanOperator parent) {
		
		// create empty operator
		BooleanOperator res = new BooleanOperator(this.operatorType, parent, negated);
	    
	    res.envVar = this.envVar;
	    res.timeLowerLimit = this.timeLowerLimit;
	    res.timeUpperLimit = this.timeUpperLimit;
	    
		//copy operands
		for (BooleanFormula it : this.operands)
		{
			it.copy(res);
		}
		return res;
	
	}

	@Override
	public void setParent(BooleanOperator parent)
	{
		
		this.parent = parent;
		
	}

	@Override
	public int getNumberOfSubtreeElements() {
		// TODO Auto-generated method stub
		return numberOfSubtreeElements;
	}

	@Override
	public BooleanFormula getSimplifiedCopy() {
		// temporary operand list
		Vector<BooleanFormula> resList = new Vector<BooleanFormula>();
		// specifies whether the operand is included in the result
		Vector<Boolean> useList = new Vector<Boolean>();
		int useCount = 0;
	    
		// obtain simplified versions of the operands
		for (BooleanFormula it1 : operands)
		{
			resList.add(it1.getSimplifiedCopy());
			useList.add(true);
			++useCount;
		}
	    
		// determine which operands can be replaced or deleted
		for (int i = 0; i < resList.size(); ++i)
		{
			if (!useList.elementAt(i))
				// this operand has already been excluded
				continue;

			if (resList.elementAt(i).getClass().getName() == "model.tree.BooleanOperator")
			{
				BooleanOperator obj = (BooleanOperator)(resList.elementAt(i));


				if (obj.operands.size() == 1 && obj.timeLowerLimit == obj.timeUpperLimit)
					// "unlist" operators with only one element and no temporal information
				{

					resList.setElementAt(obj.operands.elementAt(0).copy(null),i);

					//if in operand is only one other operand -> symplify and pass limits to children
					if(obj.operands.elementAt(0).getClass().getName() == "model.tree.BooleanOperator")
					{
						((BooleanOperator)resList.elementAt(i)).timeLowerLimit += obj.timeLowerLimit;
						((BooleanOperator)resList.elementAt(i)).timeUpperLimit += obj.timeUpperLimit;
						if (((BooleanOperator)resList.elementAt(i)).envVar == 0)
							//if no other temporal operator before, pass variable to child
						{
							((BooleanOperator)resList.elementAt(i)).envVar = obj.envVar;
						}

					}
					if(obj.operands.elementAt(0).getClass().getName() == "model.tree.Literal")
						//pass timeshift to literal
					{
						((Literal)resList.elementAt(i)).timeSteps += obj.timeLowerLimit;
					}

					resList.elementAt(i).negated = (!resList.elementAt(i).negated && obj.negated) || (resList.elementAt(i).negated && !obj.negated);
					continue;
				}
				else
					if ((obj.operatorType == this.operatorType && obj.negated == false && obj.timeLowerLimit == obj.timeUpperLimit) || obj.operatorType == OperatorType.OP_COMMA)
						// "unlist" operators with the same type, if child operator is no temporal one
					{
						useList.set(i, false);
						--useCount;

						for (BooleanFormula it2 : obj.operands)
						{
							resList.add(it2.copy(null));
							useList.add(true);
							++useCount;
						}

						//add eventual timeoffset to parent
						this.timeLowerLimit += obj.timeLowerLimit;
						this.timeUpperLimit += obj.timeUpperLimit;
					}
					else
						if (obj.timeLowerLimit == obj.timeUpperLimit && obj.timeUpperLimit != 0 && obj.envVar == 0)
							//if operator is not temporal anymore, as limits are equal, pass eventual timeshift to children
						{

							for (BooleanFormula it2 : obj.operands)
							{
								if (it2.getClass().getName() == "model.tree.BooleanOperator")
								{
									((BooleanOperator)it2).timeUpperLimit += obj.timeUpperLimit;
									((BooleanOperator)it2).timeLowerLimit += obj.timeLowerLimit;

									if(((BooleanOperator)it2).envVar == 0)
										((BooleanOperator)it2).envVar = obj.envVar;
								}

								if (it2.getClass().getName() == "model.tree.Literal")
									((Literal)it2).timeSteps += obj.timeUpperLimit;

							}
							obj.envVar = 0;
							obj.timeUpperLimit = 0;
							obj.timeLowerLimit = 0;
						}
			}
			else
			{
				Literal lit1 = (Literal)(resList.elementAt(i));
				for (int j = 0; j < resList.size(); ++j)
				{
					if (useList.elementAt(j) && i != j && resList.elementAt(j).getClass().getName() == "model.tree.Literal")
					{
						Literal lit2 = (Literal)(resList.elementAt(j));
						if (lit1.literalIndex == lit2.literalIndex && lit1.timeSteps == lit2.timeSteps)
						{
							if (lit1.negated == lit2.negated)
								// remove duplicate literals
							{
								useList.setElementAt(false, j);
								--useCount;
							}
							else
								// found "Literal &/| !Literal)
							{
								if (this.operatorType == OperatorType.OP_AND)
									// replace "Literal & !Literal" by 0
								{
									lit1.literalIndex = -1;
									lit1.negated = true;
								}
								else if (this.operatorType == OperatorType.OP_OR)
										// replace "Literal | !Literal" by 1
								{
									lit1.literalIndex = -1;
									lit1.negated = false;
								}
								if (this.operatorType == OperatorType.OP_AND || this.operatorType == OperatorType.OP_OR)
									// remove all other occurrences of the literal
								{
									for (int k = 0; k < resList.size(); ++k)
									{
										if (i != k)
										{
											useList.setElementAt(false, k);
											--useCount;
										}
									}
								}
							}
						}
					}
				}
			}
		}
	    
		// remove duplicate 0 and 1 from the simplified list
		for (int i = 0; i < resList.size(); ++i)
		{
			if (useList.elementAt(i) && resList.elementAt(i).getClass().getName() == "model.tree.Literal")
			{
				Literal lit1 = (Literal)(resList.elementAt(i));
				if (lit1.literalIndex == -1)
				{
					if (((lit1.negated && this.operatorType == OperatorType.OP_OR) ||
	                     (!lit1.negated && this.operatorType == OperatorType.OP_AND))
						&& useCount > 1)
					{
						useList.setElementAt(false,i);
						--useCount;
					}
					else
	                    if ((lit1.negated && this.operatorType == OperatorType.OP_AND) ||
	                        (!lit1.negated && this.operatorType == OperatorType.OP_OR))
	                    {
	                        for (int j = 0; j < resList.size(); ++j)
	                        {
	                            if (i != j)
	                            {
	                                useList.setElementAt(false, j);
	                                --useCount;
	                            }
	                        }
	                        break;
	                    }
				}
			}
		}
	    
		// create result object
		BooleanOperator res = new BooleanOperator(this.operatorType, null, this.negated, this.timeLowerLimit, this.timeUpperLimit, this.envVar);
	    
		for (int i = 0; i < resList.size(); ++i)
		{
			if (useList.elementAt(i))
			{
				res.addNewOperand(resList.elementAt(i));
				resList.elementAt(i).setParent(res);
			}			
		}
		
		res.recalculateNumberOfElements();
		
		return res;
	}

	@Override
	public int computeTotalTimeDelay() {
		int sumDelay = 0;
	    
	    //recursive sum of delay of children including the offset of this operator
	    for (int i = 0; i < this.operands.size(); i++)
	    {
	        sumDelay += Math.abs(this.timeLowerLimit) + this.operands.elementAt(i).computeTotalTimeDelay();
	    }
	    
	    return sumDelay;
	}

	@Override
	public int computeMaxDelayOfLiteral(int index) 
	{
		int maxDelay = 0;
	    for (int i = 0; i < operands.size(); i++)
	    {
	        if(maxDelay <= (Math.abs(timeLowerLimit) + operands.elementAt(i).computeMaxDelayOfLiteral(index)))
	           maxDelay = (Math.abs(timeLowerLimit) + operands.elementAt(i).computeMaxDelayOfLiteral(index));
	    }
	    
	    if (maxDelay <= Math.abs(timeLowerLimit)) //TODO???
	        return 0;
	    
	    
	    return maxDelay;
	}

	@Override
	public TemporalMemoryWrapper computeTemporalMemory(Vector<Integer> tempMax,
			int additionalMemory, Vector<Character> envVars,HashMap<Integer,Integer> indices) 
	{

		//store used variable names
	    if (envVar != 0)
	        envVars.add(envVar);
	    //if constant -> skip
	    additionalMemory += Math.abs(timeLowerLimit);

	    TemporalMemoryWrapper res = new TemporalMemoryWrapper(tempMax, additionalMemory, envVars);
	    for (int i = 0; i < operands.size(); i++)
	    {
	        res = operands.elementAt(i).computeTemporalMemory(res.getMaxTemp(),  res.getAdditionalMemory(), res.getEnvVars(), indices);
	    }
	    
	    return res;
	}
	
	public void addNewOperand(BooleanFormula operand)
	{
		updateNumberOfElements(operand.getNumberOfSubtreeElements() + 1);
		operands.addElement(operand);
	}
	
	public void deleteOperand(int index)
	{
		updateNumberOfElements(-((int)operands.elementAt(index).getNumberOfSubtreeElements()) - 1);
		operands.removeElementAt(index);
	}

	public void updateNumberOfElements(int additionalElements)
	{
		numberOfSubtreeElements += additionalElements;
		if (parent != null)
			((BooleanOperator)parent).updateNumberOfElements(additionalElements);
	}
	
	/**
	 * Retrieves the <index>th element of the expression by traversing
	 * the tree in a (depth) breadth-first search.
	 * <runningIndex> is an internal counter that is increased during the recursion
	 * to determine the position.
	 * Returns the element as a TreePosition object.
	 */
	public TreePosition getElementByIndex(int index, int runningIndex)
	{
		for (int i = 0; i < operands.size(); ++i)
		{
			if (runningIndex == index)
	            // the current element is the desired one
				return new TreePosition(this,i,operands.elementAt(i));
			
			++runningIndex;
	        
			if (operands.elementAt(i).getClass().getName() == "model.tree.BooleanOperator")
			{
				BooleanOperator obj = (BooleanOperator)(operands.elementAt(i));
				
				if (runningIndex + obj.getNumberOfSubtreeElements() - 1 >= index)
	                // the element is in the current subtree
				{
					return obj.getElementByIndex(index,runningIndex);
				}
				else
	                // the element is not in the current subtree
					runningIndex += obj.getNumberOfSubtreeElements();
			}
		}
		// the index was too large
		return new TreePosition(null ,0,null);
	}
    
	
	/**
	 * Retrieves a map of all literals in the expression and their frequencies.
	 * Here, the keys of the map denote the literals, and the values denote the frequencies.
	 */
	public void getLiterals(Map<Literal,Integer> literals){
		for (BooleanFormula it : operands)
		{
			if (it.getClass().getName() == "model.tree.BooleanOperator")
			{
				// recursive call in a subtree
				((BooleanOperator)it).getLiterals(literals);
			}
			else
			{
				Literal lit = (Literal)it;
				if (!literals.containsKey(lit))
					// initialize previously unseen literal
					literals.put(lit, 1);
				else
					// increment counter of a literal
					literals.put(lit, literals.get(lit) + 1);
			}
		}
	}
    
	/**
	 * Recalculates the internal counter of sub-elements in the tree.
	 */
	public int recalculateNumberOfElements()
	{
		numberOfSubtreeElements = 0;
		for (BooleanFormula it : operands)
		{
			// recursive calculation of element count
			if (it.getClass().getName() == "model.tree.BooleanOperator")
			{
				numberOfSubtreeElements += ((BooleanOperator)it).recalculateNumberOfElements() + 1;
			}
			else
				++numberOfSubtreeElements;
		}
		return numberOfSubtreeElements;
	}

	public Map<Literal, Integer> isLiteralSameState(Map<Literal, Integer> literals) 
	{
		for( int i = 0; i < operands.size(); i++)
		{
			if(operands.elementAt(i).getClass().getName() == "model.tree.Literal")
			{
				if(literals.containsKey((Literal)operands.elementAt(i)))
					//already in list. Check if has the same negation state
				{
					if(literals.get((Literal)operands.elementAt(i)).intValue() != (((Literal)operands.elementAt(i)).negated? 0 : 1))
						literals.replace((Literal)operands.elementAt(i), -1);
				}
				else
					//add to list. 0 if negated, if not 1
				{
					if(((Literal)operands.elementAt(i)).negated) 
		    			literals.put((Literal)operands.elementAt(i), 0);
		    		else
		    			literals.put((Literal)operands.elementAt(i), 1);
				}
					
			}
			else
				//recursively go over other operator nodes
			{
				literals = ((BooleanOperator)operands.elementAt(i)).isLiteralSameState(literals);
			}
		}
		
		return literals;
		
	}

	@Override
	public void deleteAllLiterals(int literalIndex, int myIndexInParent)
	{
		operands.removeIf(x -> x.getClass().getName() == "model.tree.Literal" && ((Literal)x).literalIndex == literalIndex);
		
		//if no literal left -> delete node
		if(operands.size() == 0)
			((BooleanOperator)parent).operands.remove(myIndexInParent);
		//if only one element left -> set this element to position of current node in parent
		
		//else go recursivly over other elements
		else
		{
			for(int i = 0; i < operands.size(); i++)
			{
				if(operands.elementAt(i).getClass().getName() == "model.tree.BooleanOperator")
					((BooleanOperator)operands.elementAt(i)).deleteAllLiterals(literalIndex, i);
			}
			
			if(operands.size() == 1)
			{
				if(parent!=null)
				{
					
					((BooleanOperator)parent).operands.setElementAt(operands.elementAt(0), myIndexInParent);
				}
			}
		}
	}

	@Override
	public String toMathML(HashMap<Integer, String> variableNames, int indentLvl) 
	{
		final String newLine = System.getProperty("line.separator").toString();
		String ret = "";
		//is negated?
		if(negated)
		{
			ret += indent("<apply>",indentLvl) + newLine;
			ret += indent("<not/>", indentLvl + 1) + newLine;
			ret += indent("<apply>",indentLvl + 1) + newLine;
			if(operatorType == OperatorType.OP_AND)
				ret += indent("<and/>", indentLvl + 2) + newLine;
			else if(operatorType == OperatorType.OP_OR)
				ret += indent("<or/>", indentLvl + 2) + newLine;

			//include operands recursively
			for(BooleanFormula op : operands)
				ret += op.toMathML(variableNames, indentLvl + 2);
			ret += indent("</apply>", indentLvl + 1) + newLine;
			ret += indent("</apply>", indentLvl) + newLine;
		}
		else{
			ret += indent("<apply>",indentLvl) + newLine;
			if(operatorType == OperatorType.OP_AND)
				ret += indent("<and/>", indentLvl + 1) + newLine;
			else if(operatorType == OperatorType.OP_OR)
				ret += indent("<or/>", indentLvl + 1) + newLine;

			//include operands recursively
			for(BooleanFormula op : operands)
				ret += op.toMathML(variableNames, indentLvl + 1);
			ret += indent("</apply>", indentLvl) + newLine;
		}
		

		
	
		return ret;
	}


}

