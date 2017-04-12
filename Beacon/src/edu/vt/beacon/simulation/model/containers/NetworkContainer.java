package edu.vt.beacon.simulation.model.containers;

import edu.vt.beacon.simulation.interfaces.IModelChanged;
import edu.vt.beacon.simulation.interfaces.NetworkListener;
import edu.vt.beacon.simulation.model.tree.BooleanTree;
import edu.vt.beacon.simulation.model.tree.Literal;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class NetworkContainer implements NetworkListener{
	
	public HashMap<Integer, BooleanTree> network;
	public HashMap<Integer, String> varNames;
	public HashMap<Integer, String> varText;
	public int sumTemporalMemory;
	public int[] temporalMemoryPos;
	public Vector<Integer> maxTemporalMemory;
	
	private Vector<IModelChanged> modelChangedListener;
	
	
	public NetworkContainer(Vector<BooleanTree> net, Vector<String> vars, Vector<String> texts )
	{
		network = new HashMap<Integer, BooleanTree>();
		varNames = new HashMap<Integer, String>();
		varText = new HashMap<Integer, String>();
		sumTemporalMemory = 0;
		temporalMemoryPos = new int[vars.size() + 1];
		maxTemporalMemory = new Vector<Integer>();
		modelChangedListener = new Vector<IModelChanged>();
		
		for(int i = 0; i < vars.size(); i++)
		{
			varNames.put(i, vars.elementAt(i));
			varText.put(i, texts.elementAt(i));
			network.put(i, net.elementAt(i));
		}
		
		
	}
	
	
	public void applyLiteralDecision(int decision)
    {
		
    	network.forEach((indice, tree) -> {if(tree.root == null)
    										{ switch(decision)
    										  {
    										    case 0 : tree.root = new Literal(indice, null, false); break;
    										    case 1 : tree.root = new Literal(-1, null, false); break;
    										    case 2 : tree.root = new Literal(-1, null, true); break;
    										  }
    										} 
    										});
    }

	public boolean[] stateTransition(boolean[] state, HashMap<Integer, Boolean> fixedGenes)
	{
		boolean[] oldState = new boolean[sumTemporalMemory];
		System.arraycopy(state, 0, oldState, 0, sumTemporalMemory);
	    int geneCount = 0;
	   
	    
	    //rearrange array holding states of each time step of the genes (x1[t-1],[t-2]...,x2[t-3],...,xN[t-1],..)

	    for (int i = 1; i < sumTemporalMemory + 1; i++)
	    {
	    	//oldest gene is not moved
	        if(i == temporalMemoryPos[geneCount + 1])
	        {
	            geneCount++;
	            continue;
	        }
	        //not oldest gene => move one step backwards
	        if(i > temporalMemoryPos[geneCount])
	            state[i-1] = state[i];
	    }
	    
	    
	    //compute new state and write states in corresponding field of array (at array temporalMemoryPos at index of gene)
	    Vector<Integer> indices = new Vector<Integer>(varNames.keySet());

	    HashMap<Integer,Integer> indiceMapper = new HashMap<Integer,Integer>();

	    for(int i = 0; i < varNames.size(); i++)
	    {
	    	indiceMapper.put(indices.elementAt(i), i);
	    }

	    for (int i = 0; i < varNames.size(); ++i)
		{
	        if (fixedGenes != null && fixedGenes.containsKey(i))
	            state[temporalMemoryPos[i + 1] - 1] = fixedGenes.get(indices.elementAt(i));
	        else
	            state[temporalMemoryPos[i + 1] - 1] = network.get(indices.elementAt(i)).evaluate(oldState,
	                                                                           maxTemporalMemory,
	                                                                           temporalMemoryPos,
	                                                                           0,
	                                                                           indiceMapper);
	        
		}
	    
//	    for(int i = 0; i < indiceMapper.size(); i++)
//	    {
//	    	System.out.println("index : " + i + ", temporalMemoryPos : " + temporalMemoryPos[i] + ", maxTemp: " + maxTemporalMemory.elementAt(i));
//	    }
//	    
//	    for(int i = 0; i < state.length; i++)
//	    {
//	    	System.out.print(state[i] ? '1' : '0');
//	    }
//	    System.out.println();
		return state;
	}
	
	
	public boolean[] stateTransitionAsynchronous(boolean[] state, HashMap<Integer, Boolean> fixedGenes, int geneIndex)
	{
		boolean[] oldState = new boolean[sumTemporalMemory];
		System.arraycopy(state, 0, oldState, 0, sumTemporalMemory);
	    	    
	    //rearrange array holding states of each time step of the genes (x1[t-1],[t-2]...,x2[t-3],...,xN[t-1],..)
//	    for (int i = temporalMemoryPos[geneIndex]; i < temporalMemoryPos[geneIndex + 1]; i++)
//	    {
//	    	    state[i-1] = state[i];
//	    }
		
	    //ALL OR ONLY CHOSEN ONE??
		int geneCount = 0;
	    for (int i = 1; i < sumTemporalMemory + 1; i++)
	    {
	    	//oldest gene is not moved
	        if(i == temporalMemoryPos[geneCount + 1])
	        {
	            geneCount++;
	            continue;
	        }
	        //not oldest gene => move one step backwards
	        if(i > temporalMemoryPos[geneCount])
	            state[i-1] = state[i];
	    }
	    
	    //compute new state and write states in corresponding field of array (at array temporalMemoryPos at index of gene)
	    Vector<Integer> indices = new Vector<Integer>(varNames.keySet());

	    HashMap<Integer,Integer> indiceMapper = new HashMap<Integer,Integer>();

	    for(int i = 0; i < varNames.size(); i++)
	    {
	    	indiceMapper.put(indices.elementAt(i), i);
	    }
	    
	    //perform state transition for gene at geneIndex
	    if (fixedGenes != null && fixedGenes.containsKey(geneIndex))
	    	state[temporalMemoryPos[geneIndex + 1] - 1] = fixedGenes.get(indices.elementAt(geneIndex));
	    else
	    	state[temporalMemoryPos[geneIndex + 1] - 1] = network.get(indices.elementAt(geneIndex)).evaluate(oldState,
	    			maxTemporalMemory,
	    			temporalMemoryPos,
	    			0,
	    			indiceMapper);

		return state;
	}
	
	public void setMaxTemporalMemory()
	{
	    //resize and reset vector
	   maxTemporalMemory.clear();
	   
	   for(int i = 0; i < varNames.size(); i++)
	   {
		   maxTemporalMemory.addElement(0);
	   }
	   
	   Vector<BooleanTree> networks = new Vector<BooleanTree>(network.values());
	   Vector<Integer> indices = new Vector<Integer>(network.keySet());
	   HashMap<Integer,Integer> indiceMapper = new HashMap<Integer,Integer>();
	   
	   for(int i = 0; i < networks.size(); i++)
	   {
		   indiceMapper.put(indices.elementAt(i), i);
		   //System.out.println(indices.elementAt(i) + "->" + i	);
	   }
	   
	    //get maximum of generations for each gene in each rule of the network
	    for (int i = 0; i < network.size(); i++)
	    {
	    	
	      networks.elementAt(i).computeTemporalMemory(varNames.size(), indiceMapper);
	        
	        for (int j = 0; j < maxTemporalMemory.size(); j++)
	        {
	            if (networks.elementAt(i).getNoGenerationsOf(j) > maxTemporalMemory.elementAt(j))
	                maxTemporalMemory.setElementAt(networks.elementAt(i).getNoGenerationsOf(j), j);
	        }
	    }
	    
	    sumTemporalMemory = 0;
	    //sum number of generations that have to be remembered for all genes
	    for (int i = 0; i < maxTemporalMemory.size(); i++)
	    {
	        sumTemporalMemory += maxTemporalMemory.elementAt(i);
	    }
	    temporalMemoryPos = new int[varNames.size() + 1]; //init with zeros
	    int posSum = 0;
	    for (int i = 1; i < maxTemporalMemory.size() + 1; i++)
	    {
	        posSum += maxTemporalMemory.elementAt(i - 1);
	        temporalMemoryPos[i] = posSum;
	    }
	    

	    notifyModelChangedListener();

	    
	}
	
	public String toString()
	{
		String out = "targets, factors";
		out += System.getProperty("line.separator");
		out += System.getProperty("line.separator");
		ArrayList<Integer> keys = new ArrayList<Integer>(network.keySet());
		
		for (int i = 0; i < network.size(); ++i)
		{
			out += varNames.get(keys.get(i)) + ", ";
			out += network.get(keys.get(i)).toString(varNames);
			out += System.getProperty("line.separator");
		}
		out += System.getProperty("line.separator");
		return out;
	}
	
    public boolean[] extractCurrentState(boolean[] state)
    {
    	boolean[] res = new boolean[varNames.size()];

    	//extract latest value of each gene
    	for (int i = 0; i < varNames.size(); i++)
    	{
    		res[i] = state[temporalMemoryPos[i + 1] - 1];
    	}


    	return res;
    }
	
	public void addListener(IModelChanged listener)
	{
		modelChangedListener.addElement(listener);
	}
	
	public void removeListener(IModelChanged listener)
	{
		modelChangedListener.remove(listener);
	}

	public void notifyModelChangedListener()
	{
		for (IModelChanged l : modelChangedListener)
		{
			l.checkSimulation(this);
		}
	}
}
