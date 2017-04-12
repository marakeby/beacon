package edu.vt.beacon.simulation;

import edu.vt.beacon.simulation.interfaces.ISimThreadFinishedListener;
import edu.vt.beacon.simulation.model.InitialValues;
import edu.vt.beacon.simulation.model.MyBooleanArray;
import edu.vt.beacon.simulation.model.containers.NetworkContainer;

import java.util.*;

/**
 * Thread that runs the attractor search process as background process. Extenx Service class.
 * 
 * @author julian_schwab
 *
 */
public class SimulationThread {
	
	NetworkContainer network;
	InitialValues initConditions;
	int maxStarts;
	int maxTransitions;
	HashMap<Integer,Boolean> fixedGenes;
	boolean isAsynchronous;
	
	Vector<ISimThreadFinishedListener> listener;
	
	/**
	 * Constructor for SimulationThread.
	 * @param net network model
	 * @param initCond start states
	 * @param maxS unused so far
	 * @param maxTrans maximum number of transition to be used for attractor search.
	 */
	public SimulationThread(NetworkContainer net, InitialValues initCond, int maxS, int maxTrans, HashMap<Integer,Boolean> fixedGenes, boolean isAsynchronous)
	{
		super();
		network = net;
		initConditions = initCond.copy();
		Collections.reverse(initConditions.getConditions());
		maxTransitions = maxTrans;
		maxStarts = maxS;
		this.fixedGenes = fixedGenes;
		listener = new Vector<ISimThreadFinishedListener>();
		this.isAsynchronous = isAsynchronous;
	}
	


    protected boolean isCancelled(){
        return false;
    }
    public Vector<Vector<boolean[]>> call() throws Exception {

        if (isAsynchronous) {
            return computeAsynchronousAttractor();
        } else {

            Vector<Vector<boolean[]>> attractors = new Vector<Vector<boolean[]>>();
            //fixedGenes = new HashMap<Integer, Boolean>(); //empty so far -> we don't use this
            boolean random = true;
            int maxStartStates = maxStarts;
            int calculatedTransitions = maxTransitions;
            network.setMaxTemporalMemory(); //recalculate temporalmemory setup
            int possibleStates = network.sumTemporalMemory - initConditions.getNumConditions(network.varNames.size(), network.maxTemporalMemory);
            if (possibleStates > 31)
                possibleStates = Integer.MAX_VALUE;
            else
                possibleStates = (1 << possibleStates);

            if (maxStartStates >= possibleStates) {
                random = false;
                maxStartStates = possibleStates;
            }


            Vector<MyBooleanArray> foundAttractors = new Vector<MyBooleanArray>();

            boolean[] state;
            for (int i = 0; i < maxStartStates; ) {
                if (isCancelled())
                    break;

                // generate a start state
                if (random)
                    state = initConditions.generateMatchingState(network.varNames.size(), network.maxTemporalMemory, network.temporalMemoryPos, network.sumTemporalMemory);
                else
                    state = initConditions.generateStateNo(network.varNames.size(), i, network.maxTemporalMemory, network.temporalMemoryPos, network.sumTemporalMemory);

                // save sequence of states or attractor in a hash table
                HashMap<MyBooleanArray, MyBooleanArray> states = new HashMap<MyBooleanArray, MyBooleanArray>();
                Vector<boolean[]> stateList = new Vector<boolean[]>();

                int k;
                for (k = 0; k < calculatedTransitions; ++k) {
                    if (isCancelled())
                        break;

                    if (!states.containsKey(new MyBooleanArray(state)))
                    // this state has not been reached previously
                    {
                        boolean[] startState = new boolean[network.sumTemporalMemory];
                        System.arraycopy(state, 0, startState, 0, state.length);
                        state = network.stateTransition(state, fixedGenes);

                        boolean[] nextState = new boolean[network.sumTemporalMemory];
                        System.arraycopy(state, 0, nextState, 0, state.length);


                        states.put(new MyBooleanArray(startState), new MyBooleanArray(nextState));
                    } else {
                        foundAttractors.add(new MyBooleanArray(state));
                        break;
                    }
                }

                if (k == calculatedTransitions)
                // The number of transitions was not sufficient to reach the attractor
                {
                    System.err.println("max transitions not sufficient");
    //							if(random)
    //								continue;
    //							else
    //							{
    //								++i;
    //								continue;
    //							}
                    return null;
                }

                if (foundAttractors.contains(state))
                    // attractor has already been found -> go on
                    continue;


                // determine the attractor
                boolean[] startState = new boolean[network.sumTemporalMemory];
                System.arraycopy(state, 0, startState, 0, state.length);
                boolean[] curState = state;
                MyBooleanArray cur = new MyBooleanArray(curState);
                do {
                    if (isCancelled())
                        break;


                    stateList.add(network.extractCurrentState(curState));


                    curState = states.get(cur).array;

                    cur = new MyBooleanArray(curState);
                }
                while (!cur.equals(new MyBooleanArray(startState)));

                attractors.addElement(stateList);

                ++i;

            }

            return attractors;
        }
    }

	
	
	
	private Vector<Vector<boolean[]>> computeAsynchronousAttractor()
	{
		Vector<Vector<boolean[]>> attractors = new Vector<Vector<boolean[]>>();
		//boolean random = true;
		int maxStartStates = maxStarts;
		//int calculatedTransitions = maxTransitions;
		network.setMaxTemporalMemory(); //recalculate temporalmemory setup
		int possibleStates = network.sumTemporalMemory - initConditions.getNumConditions(network.varNames.size(), network.maxTemporalMemory);
		if (possibleStates > 31)
			possibleStates = Integer.MAX_VALUE;
		else
			possibleStates = (1 << possibleStates);

		if (maxStartStates >= possibleStates)
		{
			//random = false;
			maxStartStates = possibleStates;
		}

		boolean[] state = initConditions.generateStateNo(network.varNames.size(), 0, network.maxTemporalMemory, network.temporalMemoryPos, network.sumTemporalMemory);
		Random rand = new Random();
		
		for(int i = 0; i < maxTransitions; i++)
		{
			int rnd = Math.abs(rand.nextInt() % network.varNames.size());
			
			boolean[] currentState = new boolean[state.length];
			System.arraycopy(state, 0, currentState, 0, state.length);
			
			state = network.stateTransitionAsynchronous(currentState, fixedGenes, rnd);
		}
		
		Vector<boolean[]> attractor = computeForwardSet(state);
		
		
		if(attractor == null || !validateAsyncAttractor(attractor))
			attractor = null;

		
		attractors.addElement(attractor);
		
		
		return attractors;
	}
	
	
	private Vector<boolean[]> computeForwardSet(boolean[] state)
	{
		Vector<MyBooleanArray> resultSet = new Vector<MyBooleanArray>();
		Stack<boolean[]> stack = new Stack<boolean[]>();
		
		
		resultSet.add(new MyBooleanArray(state));
		stack.add(state);
		
		boolean[] currentState;
		MyBooleanArray nextState;
		
		
		
		do 
		{
			currentState = stack.pop();
			boolean[] workingState = new boolean[state.length];
			System.arraycopy(currentState, 0, workingState, 0, currentState.length);
			
			for(int i = 0; i < network.varNames.size(); i++)
			{
				nextState = new MyBooleanArray(network.stateTransitionAsynchronous(workingState, fixedGenes, i));
				
				if(!resultSet.contains(nextState))
				{
					resultSet.add(nextState);
					stack.push(nextState.array);
				}
				
				if(resultSet.size() > 1)
					return null;
			}
			
		} while(!stack.isEmpty());
		
		Vector<boolean[]> res = new Vector<boolean[]>();
		resultSet.forEach(arr -> res.add(arr.array));
		
		return res;
		
	}
	
	private boolean validateAsyncAttractor(Vector<boolean[]> states)
	{
		Vector<Integer> intStates = new Vector<Integer>();
		
		states.forEach(s -> intStates.addElement(booleansToInt(s)));
		
		Collections.sort(intStates);
		
		for(boolean[] s : states)
		{
			Vector<boolean[]> resList = computeForwardSet(s);
			Vector<Integer> resToInt = new Vector<Integer>();
			
			resList.forEach(res -> resToInt.add(booleansToInt(res)));
			Collections.sort(resToInt);
						
			if(intStates.size() == resToInt.size())
			{
				for(int i = 0; i < resList.size(); i++)
				{
					if(intStates.get(i).intValue() != resToInt.get(i).intValue())
					{
						return false;
					}
				}
			}
			else
			{
				return false;
			}
		}
		
		return true;
	}
	
	private static int booleansToInt(boolean[] arr){
	    int n = 0;
	    for (boolean b : arr)
	        n = (n << 1) | (b ? 1 : 0);
	    return n;
	}
}

