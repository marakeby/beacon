package edu.vt.beacon.simulation.model.tree;


import edu.vt.beacon.simulation.model.containers.TemporalMemoryWrapper;

import java.util.HashMap;
import java.util.Vector;

public class Literal extends BooleanFormula {

	public int literalIndex;
	public int timeSteps;
	//public HashMap<Integer,Integer> formulaMapper;
	
	/*
	 * Creates a new Literal with the following parameters:
	 * @param <literalIndex>: the index of the literal, use -1 for constant TRUE/FALSE depending on <negated>
	 * @param <parent>: the expression in which the literal is included
	 * @param <negated>: is this literal negated?
	 * @param <timeSteps>: temporal predicate for literal
	 */
	public Literal(int literalIndex, BooleanOperator parent, boolean negated, int timeSteps)
	{ 
		this.literalIndex = literalIndex;
		this.parent = parent;
		this.negated = negated;
		this.timeSteps = timeSteps;
		
		if (parent != null)
		{
			parent.addNewOperand(this);
		}
	}
	
	/*
	 * Creates a new Literal with the following parameters:
	 * @param <literalIndex>: the index of the literal, use -1 for constant TRUE/FALSE depending on <negated>
	 * @param <parent>: the expression in which the literal is included
	 * @param <negated>: is this literal negated?
	 */
	public Literal(int literalIndex, BooleanOperator parent, boolean negated)
	{
		this.literalIndex = literalIndex;
		this.parent = parent;
		this.negated = negated;
		this.timeSteps = -1;
		
		if (parent != null)
		{
			parent.addNewOperand(this);
		}
	}
	
	@Override
	public TemporalMemoryWrapper computeTemporalMemory(Vector<Integer> tempMax, int additionalMemory, Vector<Character> envVars, HashMap<Integer,Integer> indices)
	{
		//if constant -> skip
	    if(literalIndex == -1)
	        return new TemporalMemoryWrapper(tempMax, additionalMemory, envVars);
	    
	    
	    if((Math.abs(timeSteps) + additionalMemory) > tempMax.elementAt(indices.get(literalIndex)))
	    {
	        tempMax.setElementAt(Math.abs(timeSteps) + additionalMemory, indices.get(literalIndex));
	    }
	    
	    return new TemporalMemoryWrapper(tempMax, additionalMemory, envVars);
		
	}

	@Override
	public boolean evaluate(boolean[] variableSet, Vector<Integer> temporalMemory, int[] position, int timeShift, HashMap<Integer, Integer> indiceMapper) 
	{
		if (literalIndex < 0)
	        return !negated;

	    //return value of literal at literal index in the position of timeSteps
	    if (negated)
	        return !variableSet[position[indiceMapper.get(literalIndex) + 1] + timeSteps + timeShift];
	    else
	    	return variableSet[position[indiceMapper.get(literalIndex) + 1] + timeSteps + timeShift];
		
	}

	@Override
	public String toString(HashMap<Integer,String> variableNames, Vector<Character> environmentVariables) 
	{
		//return constants
	    if (literalIndex == -1)
		{
			if (negated)
				return "0";
			else
				return "1";
		}
	    
		if (literalIndex == -2)
			return "UNKNOWN";

	    //return genes
	    String s = "";
	    //print environment variables
	    for (int i = 0; i < environmentVariables.size(); i++)
	    {
	        if  (i == environmentVariables.size() - 1)
	            s += environmentVariables.elementAt(i);
	        else
	            s += environmentVariables.elementAt(i) +  "+";
	    }
	    //print timesteps of literal
	    if  (timeSteps != 0)
	    {
	        if (timeSteps < 0)
	            s += timeSteps;
	        else
	            s += "+" + timeSteps;
	    }
		   
		if (negated)
			return "!" + variableNames.get(literalIndex) + "[" + s + "]";
		else
			return variableNames.get(literalIndex) + "[" + s + "]";			
	}

	public String toString()
	{
		return "" + literalIndex + "[" + timeSteps +"]";
	}
	@Override
	public BooleanFormula copy(BooleanOperator parent) {
		return new Literal(literalIndex, parent, negated, timeSteps);
	}

	@Override
	public void setParent(BooleanOperator parent) 
	{
		this.parent = parent; 
	}

	@Override
	public int getNumberOfSubtreeElements() 
	{
		return 0;
	}

	@Override
	public int computeTotalTimeDelay() {
		 return Math.abs(this.timeSteps);
	}

	@Override
	public int computeMaxDelayOfLiteral(int index) {
		if(literalIndex == index)
	        return Math.abs(timeSteps);
	    else
	        return 0;
	}


	@Override 
	public int hashCode()
	{
		return this.literalIndex;
	}

	@Override 
	public boolean equals( Object obj )
	{
		if ( this == obj )
			return true;
		
		if (obj.getClass().getName() != "model.tree.Literal")
			return false;
		else
		{
			if(((Literal)obj).literalIndex == this.literalIndex)
				return true;
			else
				return false;
		}
	}

	@Override
	public void deleteAllLiterals(int literalIndex, int myIndexInParent) {
		// do nothing for literals
		
	}

	@Override
	public String toMathML(HashMap<Integer, String> variableNames, int indentLvl) 
	{
		final String newLine = System.getProperty("line.separator").toString();
		String ret = "";
		
		//literal is constant
		if(literalIndex == -1)
			if(negated)
				ret += indent("<cn type=\"integer\">0</cn>",indentLvl) + newLine;
			else
				ret += indent("<cn type=\"integer\">1</cn>",indentLvl) + newLine;
		else
		{
			ret += indent("<apply>", indentLvl) + newLine;
			ret += indent("<eq/>", indentLvl + 1) + newLine;
			ret += indent("<ci>" + variableNames.get(literalIndex) + "</ci>", indentLvl + 1) + newLine;
			ret += indent("<cn type=\"integer\">" + (negated ? "0" : "1") + "</cn>", indentLvl + 1) + newLine;
			ret += indent("</apply>",indentLvl) + newLine;
		}
		
		return ret;
	}
	
	

}
