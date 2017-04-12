package edu.vt.beacon.simulation.model;

import java.util.Collections;
import java.util.Vector;

public class InitialValues {

	Vector<SimpleConditions> initialConditions;
    
    public InitialValues()
    {
    	initialConditions = new Vector<SimpleConditions>();
    }
    /**
	 * Generates a state matching the conditions in the object,
	 * and write it to <result>. Here, <size> is the number of genes
	 * in <result>.
	 */
	public boolean[] generateMatchingState(int size, Vector<Integer> temporalMemory,
			int[] position, int sumTemporalMemory)
	{
		int maxDiff = Collections.max(temporalMemory);
		Vector<Integer> indices = new Vector<Integer>(initialConditions.elementAt(0).geneValues.keySet());
		boolean[]  result = new boolean[sumTemporalMemory];
		for (int diff = 1; diff <= maxDiff; diff++)
		{

			for (int j = 0; j < size; ++j)
			{

				if(diff <= temporalMemory.elementAt(j))
					//if another generation has to be memorized for this gene
				{
					if(diff <= initialConditions.size())
						//if there are enough generations are given in initalValues -> extract value from there
					{
						if (initialConditions.elementAt(diff - 1).geneValues.containsKey(indices.elementAt(j)))
							// this gene is fixed => use the corresponding value
							result[position[j + 1] - diff] = initialConditions.elementAt(diff - 1).geneValues.get(indices.elementAt(j));
						else
							// this gene is not specified => generate it
							result[position[j + 1] - diff] = (Math.random() > 0.5);
					}
					else
						//if there are not enough generations given in initialValues -> take value from "oldest" generation that is given
					{
						if (initialConditions.lastElement().geneValues.containsKey(indices.elementAt(j)))
							// this gene is fixed => use the corresponding value
							result[position[j + 1] - diff] = initialConditions.lastElement().geneValues.get(indices.elementAt(j));
						else
							// this gene is not specified => generate it
							result[position[j + 1] - diff] = (Math.random() > 0.5);
					}
				}
			}
		}
		
		return result;
	}

	/**
	 * Generates the <number>-th state matching the conditions in the object,
	 * and write it to <result>. Here, <size> is the number of genes
	 * in <result>. This means filling the binary representation of <number>
	 * into the "free" (unspecified) genes.
	 */
	public boolean[] generateStateNo(int size, int number, Vector<Integer> temporalMemory,
			int[] position, int sumTemporalMemory)
	{
		int maxDiff = Collections.max(temporalMemory);
		Vector<Integer> indices = new Vector<Integer>(initialConditions.elementAt(0).geneValues.keySet());
		boolean[]  result = new boolean[sumTemporalMemory];
		int bit = 0;
		
//		System.out.println("Initial Conditions:");
//		for (int diff = 0; diff < initialConditions.size(); diff++)
//		{
//			for (int j = 0; j < size; ++j)
//			{
//				System.out.print(initialConditions.elementAt(diff).geneValues.values().toArray()[j] + ", ");
//			}
//			System.out.println();
//		}
		

		
		for (int diff = 1; diff <= maxDiff; diff++)
		{
			
			for (int j = 0; j < size; ++j)
			{

				if(diff <= temporalMemory.elementAt(j))
					//if another generation has to be memorized for this gene
				{
					if(diff <= initialConditions.size())
						//if there are enough generations are given in initalValues -> extract value from there
					{
						if (initialConditions.elementAt(diff - 1).geneValues.containsKey(indices.elementAt(j)))
							// this gene is fixed => use the corresponding value
							result[position[j + 1] - diff] = initialConditions.elementAt(diff - 1).geneValues.get(indices.elementAt(j));
						else
						{
							// this gene is not specified => generate it
							result[position[j + 1] - diff] = ((number & (1 << bit)) > 0);
							bit++;
						}
					}
					else
						//if there are not enough generations given in initialValues -> take value from "oldest" generation that is given
					{
						if (initialConditions.lastElement().geneValues.containsKey(indices.elementAt(j)))
							// this gene is fixed => use the corresponding value
							result[position[j + 1] - diff] = initialConditions.lastElement().geneValues.get(indices.elementAt(j));
						else
							// this gene is not specified => generate it
						{
							result[position[j + 1] - diff] = ((number & (1 << bit)) > 0);
							bit++;
						}
					}
				}
//				for(int t = 0; t < result.length ; t++)
//				{
//					System.out.print(result[t] +",");
//				}
//				System.out.println();
			}
			
		}
		
		return result;
	}

    public int getNumConditions(int noGenes, Vector<Integer> temporalMemory)
    {
    	int cond = 0;
    	Vector<Integer> indices = new Vector<Integer>(initialConditions.elementAt(0).geneValues.keySet());
    	
        for(int i = 0; i < noGenes; i++)
        {
            for (int j = 0; j < temporalMemory.elementAt(i); j++)
            {
                if (j < initialConditions.size())
                    //is initial state of this time given
                {
                    if (initialConditions.elementAt(j).geneValues.containsKey(indices.elementAt(i)))
                        //check if gene is specified -> increase number
                        cond ++;
                }
                else
                    //generation not given in initial values -> increase number anyway
                    cond++;
                    
            }
        }
        return cond;
    }
    
    
    public Vector<SimpleConditions> getConditions()
    {
    	return initialConditions;
    }
    
    
    public InitialValues copy()
    {
    	//Vector<SimpleConditions> ret = new Vector<SimpleConditions>();
    	InitialValues ret = new InitialValues();
    	
    	for(SimpleConditions cond : this.initialConditions)
    	{
    		ret.initialConditions.addElement(cond.copy());
    	}
    	
    	return ret;
    }
    
    /**
     * function to create startstate matrix corresponding to the set initial conditions 
     * this function is used creating the matrix of startstates, if the user wants to transfer them to R
     * @return startstates of network as set up in initial conditions. converted to a integer matrix (rows = conditions, cols = gene)
     */
    public int[][] returnStartStates()
    {
    	//no conditions set -> return null
    	if(initialConditions == null)
    		return null;
    	else if(initialConditions.size() == 0)
    		return null;
    	
    	
    	int[][] ret = new int[initialConditions.size()][initialConditions.elementAt(0).geneValues.size()];
    	//fill matrix 
    	for(int i = 0; i < initialConditions.size(); i++)
    	{
    		Vector<Integer> indices = new Vector<Integer>(initialConditions.elementAt(i).geneValues.keySet());
    		
    		for(int j = 0; j < initialConditions.elementAt(i).geneValues.size(); j++)
    		{
    			ret[i][j] = initialConditions.elementAt(i).geneValues.get(indices.elementAt(j)) ? 1 : 0;
    		}
    	}
    	
    	return ret;
    }
}
