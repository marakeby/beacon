package edu.vt.beacon.simulation.model.tree;


import edu.vt.beacon.simulation.model.containers.TemporalMemoryWrapper;

import java.util.HashMap;
import java.util.Vector;

public abstract class BooleanFormula implements Comparable<BooleanFormula> {

	/**
	 * Is the expression negated?
	 */
	public boolean negated;
    
    public BooleanFormula parent;
    
    
    public abstract TemporalMemoryWrapper computeTemporalMemory(Vector<Integer> tempMax, int additionalMemory, Vector<Character> envVars, HashMap<Integer,Integer> indices);
	/**
	 * Evaluates the expression on a given set of variable values
	 * <variableSet>.
	 * Returns the value of the expression.
	 */
	public abstract boolean evaluate(boolean[] variableSet, Vector<Integer> temporalMemory,
                          int[] position, int timeShift,HashMap<Integer,Integer> indices);
    
	/**
	 * Returns a string representation of the formula based on the variable names
	 * in <variableNames>.
	 */
	public abstract String toString(HashMap<Integer, String> variableNames, Vector<Character> environmentVariables);
    
	
	/** 
	 * Returns a string of the formula based on the variable names in MathML format
	 * 
	 */
	public abstract String toMathML(HashMap<Integer, String> variableNames, int indentLvl);
	/**
	 * Creates an exact copy of this object, and adds it to <parent> if supplied.
	 */
	
	public abstract BooleanFormula copy(BooleanOperator parent);
    
	/**
	 * Changes the parent expression of the object to <parent>
	 */
	public abstract void setParent(BooleanOperator parent);
    
	/**
	 * Returns the number of elements in the subtree denoted by the expression.
	 * Here, each operator and each literal is counted as one element.
	 */
	public abstract int getNumberOfSubtreeElements();
    
	/**
	 * Simplifies an expression by removing duplicates, resolving tautologies, etc.,
	 * and returns the simplified version as a copy.
	 */
	public BooleanFormula getSimplifiedCopy()
	{
		return this.copy(null);
	}
    
    /**
     * Calculate and return total time delay in this formula
     */
    public abstract int computeTotalTimeDelay();
    
    /**
     * Search for the maximum delay of literal with index <index>
     */
    public abstract int computeMaxDelayOfLiteral(int index);
    
    @Override 
    /**
     * compares to formulae based on the literal indices and operator subtrees
     */
    public int compareTo(BooleanFormula f2)
    {
	   if (this.getClass().getName() == "Literal" && f2.getClass().getName() == "Literal")
           // compare literals by literal index and negation
		{
			Literal l1 = ((Literal)this);
			Literal l2 = ((Literal)f2);
			if(l1.literalIndex * 2 + (l1.negated?1:0) < l2.literalIndex * 2 + (l2.negated?1:0))
				return -1;
			else 
				return 1;
		}
		else
		{
           // literals are "smaller" than operators
           if (this.getClass().getName() == "Literal")
           {
               return -1;
           }
           else if (f2.getClass().getName() == "Literal")
           {
        	   return 1;
           }
           else
        	   // compare two operator subtrees
           {
        	   BooleanOperator o1 = ((BooleanOperator)this);
        	   BooleanOperator o2 = ((BooleanOperator)f2);
        	   if (o1.operands.size() == 0)
        		   return -1;
        	   if (o2.operands.size() == 0)
        		   return 1;

        	   int i;
        	   for (i = 0; i < o1.operands.size() && i < o2.operands.size(); ++i)
        	   {
        		   if (!(o1.operands.elementAt(i) == o2.operands.elementAt(i)))
        			   break;
        	   }
        	   if (i == o1.operands.size() || i == o2.operands.size())
        		   // all operands are equal
        		   return 1;

        	   // recursive comparison of subtrees
        	   return o1.operands.elementAt(i).compareTo(o2.operands.elementAt(i));
           }
    }
    }
	public abstract void deleteAllLiterals(int literalIndex, int myIndexInParent);
	
	static String indent(String str, int indentLvl)
	{
		String ret = "";
		for(int i = 0; i < indentLvl; i++)
		{
			ret += "\t";
		}
		
		return(ret + str);
		
	}
			
	
};

