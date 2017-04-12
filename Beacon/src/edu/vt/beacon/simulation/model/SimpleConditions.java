package edu.vt.beacon.simulation.model;

import javafx.beans.property.SimpleBooleanProperty;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Vector;

public class SimpleConditions {

	// the index of the condition in the parent list
	int index;

	// the alternative the condition belongs to
	int alternative;
	
	public HashMap<Integer, Boolean > geneValues;
	
	public SimpleConditions(Vector<SimpleBooleanProperty> condition, HashMap<Integer,Integer> indices, HashMap<Integer,String> varNames)
	{
		this.index = 0;
		this.alternative = 0;
		this.geneValues = new HashMap<Integer, Boolean>();
		Vector<Integer> idX = new Vector<Integer>(varNames.keySet());
		
		//if new col -> init with false
		if(condition == null)
		{
			for(int i = 0; i < varNames.size(); i++)
			{
				geneValues.put(idX.elementAt(i), false);
			}
		}
		else
		{
			for(int i = 0; i < varNames.size(); i++)
			{
				geneValues.put(idX.elementAt(i), condition.elementAt(indices.get(idX.elementAt(i))).getValue());
			}
		}
		
		
		
	}
	/**
	 * Copy constructor
	 * @param geneValues
	 * @param index
	 * @param alternative
	 */
	private SimpleConditions(HashMap<Integer, Boolean> geneValues, int index, int alternative)
	{
		this.index = index;
		this.alternative = alternative;
		this.geneValues = new HashMap<Integer, Boolean>();
		for (Entry<Integer, Boolean>  e : geneValues.entrySet())
		{
			this.geneValues.put(e.getKey(), e.getValue());
		}
	}
	public SimpleConditions(String condition, HashMap<Integer,String> varNames, int index, int alternative)
	{
		this.index = index;
		this.alternative = alternative;
		this.geneValues = new HashMap<Integer, Boolean>();
		int lastSep = 0,nextSep = 0;
		
		while(true)
		{
			if(nextSep == condition.length())
				break;
			
			nextSep = condition.indexOf(' ', lastSep);
			
			if(nextSep == -1)
				nextSep = condition.length();
			
			String val = condition.substring(lastSep, nextSep);
			val = val.trim();
			
			boolean neg = false;
			
			if(val.charAt(0) == '!')
			{
				neg = true;
				val = val.substring(1);
			}
			
			//check if gene exists 
			if(!varNames.containsValue(val))
			{
				System.err.println("Gene " + val + " in initial conditions not found!");
				return;
			}
		
			//set value in genelist
			
			for(Entry<Integer,String> e : varNames.entrySet())
			{
				if(e.getValue().equals(val))
				{
					geneValues.put(e.getKey(), !neg);
					break;
				}
			}
			
			lastSep = nextSep + 1;
			
			
			
		}
		
	}
	
	public boolean[] generateMatchingState(int size, Vector<Integer> temporalMemory)
	{
		boolean[] result = new boolean[size];
	    int pos = 0;
	    Vector<Integer> indices = new Vector<Integer>(geneValues.keySet());
	    
		for (int j = 0; j < size; ++j)
		{
			if (geneValues.containsKey(indices.elementAt(j)))
	            // this gene is fixed => use the corresponding value
			{
	            for (int i = 0; i < temporalMemory.elementAt(j); i++)
	            {
	                result[pos++] = geneValues.get(indices.elementAt(j));
	            }
				
			}
			else
	            // this gene is not specified => generate it randomly
			{
	            for (int i = 0; i < temporalMemory.elementAt(j); i++)
	            {
	                result[pos++] = (Math.random() > 0.5);
	            }
	            
			}
		}
		
		return result;
	}
	
	public boolean[] generateMatchingStateNo(int size, int number, Vector<Integer> temporalMemory)
	{
		int bit = 0;
	    int pos = 0;
	    boolean[] result = new boolean[size];
	    Vector<Integer> indices = new Vector<Integer>(geneValues.keySet());
	    
		for (int j = 0; j < size; ++j)
		{
			if (geneValues.containsKey(indices.elementAt(j)))
	            // this gene is fixed => use the corresponding value
			{
	            for (int i = 0; i < temporalMemory.elementAt(j); i++)
	            {
	                result[pos++] = geneValues.get(indices.elementAt(j));
	            }
			}
			else
	            // this gene is not specified => use the value for the next bit in <number>
			{
	            for (int i = 0; i < temporalMemory.elementAt(j); i++)
	            {
	                result[pos++] = ((number & (1 << bit)) > 0);
	            }
	            
				++bit;
			}
		}
		
		return result;
	}
	
	public SimpleConditions copy()
	{
		return new SimpleConditions(geneValues, index, alternative);
	}
	
	/**
	 * toString creates String representation of an initial conditions as known in the cantata specification of attractor files 
	 * @param names
	 * @return
	 */
	public String toString(HashMap<Integer,String> names)
	{
		String res = "";
		
		for(Entry<Integer,Boolean> e : geneValues.entrySet())
		{
			if(e.getValue())
				res += names.get(e.getKey()) + " ";
			else
				res += "!" + names.get(e.getKey()) + " ";
		}
		
		return res.trim();
		
	}
	
}
