package edu.vt.beacon.simulation.model.tree;

import edu.vt.beacon.simulation.model.containers.TemporalMemoryWrapper;

import java.util.*;

public class BooleanTree {
	
	// the root element of the container
	public BooleanFormula root;

	// has the formula been modified,
	// such that the literal map must be recalculated?
	public boolean modified;

	// A cached map of the literals in the tree,
	// which is only recalculated if necessary
	public Map<Literal,Integer> cachedLiterals;

	// the uncertainty of the formula
	// (0 = no uncertainty, 1 = high uncertainty)
	double uncertainty;

	// vector that stores how many generations of each gene have to be stored
	// item at i stands for number of generations for gene with index i
	Vector<Integer> temporalMemory;
	
	//vector that stores environment variables for temporal operators that are in use
    Vector<Character> environmentVars;
    
    //for GUI usage
    List<ILiteralDecisionListener> listener;
    
    public void addListener(ILiteralDecisionListener listener) {
		this.listener.add(listener);
	}

	public BooleanTree(BooleanFormula root, double uncertainty)
    {
    	this.root = root;
    	this.modified = true;
    	this.uncertainty = uncertainty;
    	this.cachedLiterals = new HashMap<Literal, Integer>();
    	this.listener = new ArrayList<ILiteralDecisionListener>();
    	this.environmentVars = new Vector<Character>();
  
    }
    
    public BooleanTree(BooleanFormula root, Vector<Integer> temporalMemory, Vector<Character> environmentVars, double uncertainty)
    {
        this.root = root;
    	this.modified = true;
    	this.uncertainty = uncertainty;
    	this.temporalMemory = temporalMemory;
    	this.environmentVars = environmentVars;
    	this.cachedLiterals = new HashMap<Literal, Integer>();
    	this.listener = new ArrayList<ILiteralDecisionListener>();
    	this.environmentVars = new Vector<Character>();
    }
    
    /**
	 * Returns the root element of the container
	 * @return root of tree
	 */
	public BooleanFormula getRoot()
	{
		return root;
	}
    
	/**
	 * Retrieves the uncertainty of the tree,
	 * which determines how frequently it is mutated
	 * @return uncertainty value of tree (at this point always 1)
	 */
	public double getUncertainty()
	{
		return uncertainty;
	}
    
    
	/**
	 * Sets the uncertainty of the tree,
	 * which determines how frequently it is mutated,
	 * to a new value uncertainty
	 * @param uncertainty : (0 = no uncertainty, 1 = high uncertainty)
	 */
	public void setUncertainty(double uncertainty)
	{
		this.uncertainty = uncertainty;
	}
    
    /**
     * Return number of generations that has to be stored for gene at index
     * @param index of literal
     * @return no of generations used for specified literal 
     */
    public int getNoGenerationsOf(int index)
    {
        return temporalMemory.elementAt(index);
    }
    
    
	/**
	 * UNUSED : use getElementByBSIndex instead
	 * @param index element of the expression by traversing
	 * the tree in a (depth) breadth-first search.
	 * @return the element as a TreePosition object.
	 */
	public TreePosition getElementByIndex(int index)
	{
		if (index == 0)
        // the root element is requested
		return new TreePosition(null,0,root);
	else
	{
		int runningIndex = 0;
		if (root.getClass().getName() == "model.tree.BooleanOperator")
            // look up element in subtree
			return ((BooleanOperator)root).getElementByIndex(index-1, runningIndex);
		else
            // the index was too large
			return new TreePosition(null,0,null);
	}
		
	}
	
	/**
	 * @param index element of the expression by traversing
	 * the tree in a (depth) breadth-first search.
	 * @return the element as a TreePosition object.
	 */
	public TreePosition getElementByBSIndex(int index)
	{
		LinkedList<BooleanFormula> fringe = new LinkedList<BooleanFormula>();
		int label = 0;
		
		if(index == 0)
			return new TreePosition(null, 0, root);
		
		if(root.getClass().getName() == "model.tree.BooleanOperator")
		{
			for(BooleanFormula child : ((BooleanOperator)root).operands)
			{
				fringe.add(child);
			}
		}
		
		while(!fringe.isEmpty())
		{
			BooleanFormula n = fringe.pollFirst();
			
			if(++label == index)
				return new TreePosition((BooleanOperator)n.parent, index, n);
			
			
			if(n.getClass().getName() == "model.tree.BooleanOperator")
			{
				for(BooleanFormula child : ((BooleanOperator)n).operands)
				{
					fringe.add(child);
				}
			}
			
		}
		
		//index to large
		return new TreePosition(null,0,null);
		
	}
    
	
	/**
	 * @param position Replaces the element specified by a new expression 
	 * @param newElement new expression
	 * @param freeOld is true, the old element is deleted.
	 */
	public void replaceElement(TreePosition position, BooleanFormula newElement, boolean freeOld)
	{
		if (position.parent == null)
	        // exchange the root of the tree
		{
			root = newElement;
		}
		else
	        // exchange an element at a lower level
		{
			// recalculate number of elements in the subtree
			position.parent.updateNumberOfElements(-((int)position.element.getNumberOfSubtreeElements())
													+ ((int)newElement.getNumberOfSubtreeElements()));
			// replace element
			position.parent.operands.setElementAt(newElement,position.index);
			newElement.setParent(position.parent);
		}
		touch();
		
	}
    
	/**
	 * @return the number of elements in the expression tree
	 */
	public int getNumberOfElements()
	{
		if (root == null)
			return 0;
		else
			return root.getNumberOfSubtreeElements() + 1;
	}
    
	/**
	 * @return a map of literals and their frequencies in the expression.
	 * Here, the keys denote the literals, and the values denote their frequencies
	 */
	public Map<Literal,Integer> getLiterals()
	{
		modified = true;
		if (modified)
	        // recalculate the cache
		{
			if (root != null)
			{
				cachedLiterals.clear();
				if (root.getClass().getName() == "model.tree.BooleanOperator")
					((BooleanOperator)root).getLiterals(cachedLiterals);
				else
				{
					Literal lit = (Literal)root;
					cachedLiterals.put(lit, 1);
				}
			}
			modified = false;
		}
	    
		// return the cache
		return cachedLiterals;
	}
    
	/**
	 * @return a string representation of the formula based on the variable names
	 * in variableNames.
	 * @param variableNames of literals in tree as map (key = index, value = name)
	 */
	public String toString(HashMap<Integer,String> variableNames)
	{
		Vector<Character> envVars = new Vector<Character>();
		if (root != null)
			return root.toString(variableNames, envVars);
		else
			return "UNKNOWN";
		
	}
    
	/**
	 * Evaluates the expression on a given set of variable values
	 * variableSet.
	 * @param variableSet as current state
	 * @param temporalMemory vector that hold references which part of variable set is used for which literal
	 * @param timeShift is constant shift of time from external operators
	 * @param position array that shows positions of literals 
	 * @param indiceMapper maps indices of literal index and current position in lists 
	 * @return the value of the expression.
	 */
	public boolean evaluate(boolean[] variableSet, Vector<Integer> temporalMemory, int[] position, int timeShift, HashMap<Integer, Integer> indiceMapper)
	{
		if (root == null)
	        // empty tree
			return false;
		else
			return root.evaluate(variableSet, temporalMemory, position, timeShift, indiceMapper);

	}
	
		/**
	 * @return an exact copy of the container and its expression.
	 */
	public BooleanTree copy()
	{
		if (root == null)
			return new BooleanTree(null, this.uncertainty);
		else
			return new BooleanTree(root.copy(null), this.temporalMemory, this.environmentVars, this.uncertainty);	
	}
    
	/**
	 * Replaces the expression in the container by a simplified copy
	 * by eliminating duplicates, resolving tautologies, etc.
	 */
	public void simplify()
	{
		if (root != null && root.getClass().getName() == "model.tree.BooleanOperator")
		{
			BooleanOperator r = ((BooleanOperator)root);

			// calculate a simplified copy of the root operator
			r = (BooleanOperator)r.getSimplifiedCopy();


			// replace the root
			root = r;


			if (r.operands.size() == 1 && r.timeLowerLimit == r.timeUpperLimit)
				// if the root operator consists of only one element, which is not all or any,
				// replace it with this element
			{
				root = r.operands.elementAt(0).copy(null);

				if (root.getClass().getName() == "model.tree.BooleanOperator" && r.timeLowerLimit != 0)
				{
					((BooleanOperator)root).timeLowerLimit += r.timeLowerLimit;
					((BooleanOperator)root).timeUpperLimit += r.timeUpperLimit;
					((BooleanOperator)root).envVar = r.envVar;
				}

				if (root.getClass().getName() == "model.tree.Literal" && r.timeLowerLimit != 0)
					((Literal)root).timeSteps += r.timeLowerLimit;
				
				root.negated = (!r.negated && root.negated) || (r.negated && !root.negated);
				root.setParent(null);
				
			}

			else if (r.timeUpperLimit == r.timeLowerLimit)
				//if range of timesteps is the same, pass it to children and set it to zero afterwards. Environment variable is also deleted
			{

				for (int i = 0 ; i < r.operands.size(); i++)
				{

					if(r.operands.elementAt(i).getClass().getName() == "model.tree.BooleanOperator" && r.timeLowerLimit != 0)
					{
						((BooleanOperator)r.operands.elementAt(i)).timeLowerLimit += r.timeLowerLimit;
						((BooleanOperator)r.operands.elementAt(i)).timeUpperLimit += r.timeUpperLimit;
						if (((BooleanOperator)r.operands.elementAt(i)).envVar == 0)
							//if no other temporal operator before, pass variable to child
							{
								((BooleanOperator)r.operands.elementAt(i)).envVar = r.envVar;
							}

					}
					if(r.operands.elementAt(i).getClass().getName() == "model.tree.Literal" && r.timeLowerLimit != 0)
						//pass timeshift to literal
					{
						((Literal)r.operands.elementAt(i)).timeSteps += r.timeLowerLimit;
					}
				}

				r.timeUpperLimit = 0;
				r.timeLowerLimit = 0;
				r.envVar = 0;

			}
		}

		touch();
	}
    
	/**
	 * @return a normalized version of the expression by transforming it
	 * into the negation normal form.
	 */
	public BooleanTree getNormalizedCopy()
	{
		if (root == null)
			return new BooleanTree(null, this.uncertainty);
		if (root.getClass().getName() == "BooleanOperator")
		{
			// first, simplify the tree
			BooleanOperator res = (BooleanOperator)(root.getSimplifiedCopy());

			// now apply a normalization to the simplified tree
			res = normalizeOperator(res);


			return new BooleanTree(res, this.uncertainty);
		}
		else
			return new BooleanTree(root.copy(null), this.temporalMemory, this.environmentVars, this.uncertainty);
	}
    
	/**
	 * Marks this object as modified, which means that the cached literal
	 * map has to be recalculated.
	 */
	public void touch()
	{
		this.modified = true;
	}
    
    /**
     * Compute list with information about how many generations of each gene have to be stored.
     * This information corresponds to the temporal information of each literal
     * @param noGenes number of factors in model
     * @param indices mapper list between current index in list and literalIndex
     */
    public void computeTemporalMemory(int noGenes,HashMap<Integer,Integer> indices)
    {
    	temporalMemory = new Vector<Integer>();
    	
    	for(int i = 0; i < noGenes; i++)
    	{
    		temporalMemory.add(1);
    	}
    	
        environmentVars = new Vector<Character>();
        //additional steps summed over any/all iterations
        int additionalMemory = 0;

        if(root != null)
        {
        	TemporalMemoryWrapper res = root.computeTemporalMemory(temporalMemory, additionalMemory, environmentVars, indices);
        	temporalMemory = res.getMaxTemp();
        	environmentVars = res.getEnvVars();
        }
    }
    
    /**
     * Calculate and return total delay of all Literals, including delay of operators, in this tree
     * @return total time delay in this tree
     */
    public int getTotalTimeDelay()
    {
    	return root.computeTotalTimeDelay();
    }
    
    
    /**
     * Return maximum delay of literal with index
     * @param index of literal of interest
     * @return maximum delay for literal of index index 
     */
    public int getMaxDelayOfLiteral(int index)
    {
    	return root.computeMaxDelayOfLiteral(index);
    }
    
    
	/**
	 * Normalize operator
	 * @param op operator to normalize
	 * @return normalized operator
	 */
	
    public BooleanOperator normalizeOperator(BooleanOperator op)
    {
    	BooleanOperator opToNormalize = op;
    	if (opToNormalize.operatorType == OperatorType.OP_XOR)
            // convert to AND/OR
    	{
            
    		BooleanOperator opOr = new BooleanOperator(OperatorType.OP_OR,null,false);
    		BooleanFormula op1 = opToNormalize.operands.elementAt(0).copy(null);
    		BooleanFormula op2 = opToNormalize.operands.elementAt(1).copy(null);
            
    		for (int i = 1; i < opToNormalize.operands.size(); ++i)
    		{
    			BooleanOperator opAnd1 = new BooleanOperator(OperatorType.OP_AND,opOr,false);
    			BooleanOperator opAnd2 = new BooleanOperator(OperatorType.OP_AND,opOr,false);
                
    			opAnd1.addNewOperand(op1);
    			op1.setParent(opAnd1);
    			opAnd1.addNewOperand(op2);
    			op2.setParent(opAnd1);
                
    			BooleanFormula op12 = op1.copy(opAnd2);
    			op2.copy(opAnd2);
                
    			op2.negated = !op2.negated;
    			op12.negated = !op12.negated;
    			if (i < opToNormalize.operands.size() -1)
    			{
    				op1 = opOr;
    				op2 = opToNormalize.operands.elementAt(i+1).copy(null);
    				opOr = new BooleanOperator(OperatorType.OP_OR,null,false);
    			}
    		}
    		
    		op = opOr;
    		opToNormalize = opOr;
    	}
    	if (opToNormalize.negated)
            // apply De Morgan's rule
    	{
    		opToNormalize.negated = false;
    		if (opToNormalize.operatorType == OperatorType.OP_AND)
    			opToNormalize.operatorType = OperatorType.OP_OR;
    		else
    			opToNormalize.operatorType = OperatorType.OP_AND;
            
    		for (BooleanFormula it : opToNormalize.operands)
    		{
    			it.negated = !it.negated;
    		}
    	}
        
    	// recursively normalize subtrees
    	for (BooleanFormula it : opToNormalize.operands)
    	{
            if (it.getClass().getName() == "model.tree.BooleanOperator")
            {
                BooleanOperator operand = (BooleanOperator)it;
                operand = normalizeOperator(operand);
                it = operand;
            }
    	}
        
    	// obtain a unique order by sorting the subtrees
    	opToNormalize.operands.sort(null);
    	
    	return opToNormalize;
    }
    
    //1 is for activatory, 0 for inhibitory, -1 for both
    public Map<Literal,Integer> isLiteralSameState()
    {
    	
    	Map<Literal, Integer> literals = new HashMap<Literal, Integer>();
    	
    	if(root == null)
    		return literals;
    	
    	if(root.getClass().getName() == "model.tree.Literal")
    	{
    		if(root.negated) 
    			literals.put((Literal)root, 0);
    		else
    			literals.put((Literal)root, 1);
    	}
   	  	else
   	  	//root is operator
    	{
   	  	  literals = ((BooleanOperator)root).isLiteralSameState(literals);	
    	}
    	
    	return literals;
    }
    
    
    public boolean deleteAllLiterals(int literalIndex)
    {
    	boolean emptyRule = false;
    	if(root.getClass().getName() == "model.tree.Literal")
    	{
    		if(((Literal)root).literalIndex == literalIndex)
    		{
    			emptyRule = true;
    			root = null;
    			notifyLiteralDecisionListener();
    		}	
    	}
    	else
    	{
    		root.deleteAllLiterals(literalIndex,0);


    		if(root.getClass().getName() == "model.tree.BooleanOperator")
    		{
    			//make constant FALSE if nothing is left
    			if(((BooleanOperator)root).operands.size() == 0)
    			{
    				emptyRule = true;
    				root = null;
    				notifyLiteralDecisionListener();
    			}
    			else if(((BooleanOperator)root).operands.size() == 1)
    			{
    				root = ((BooleanOperator)root).operands.elementAt(0); 
    			}
    		}
    	}
    	return emptyRule;
    }
	
    public boolean deleteByPosition(int position)
    {
    	if(position == 0)
    	{
    		root = null;
    		notifyLiteralDecisionListener();
    		return false;
    	}
    	
    	//remove element to delete from parent operator
    	TreePosition toDelete = this.getElementByBSIndex(position);
    	if(toDelete.parent != null)
    	{
    		toDelete.parent.operands.remove(toDelete.element);
    		this.simplify();
    	}
    	
    	return true;
    }
    /**
     * Adds a new element to the boolean tree. The position is computed by breadth-first traversing the tree
     * @param position is the place were the new formula is inserted
     * @param toAdd new Operator/Literal that is going to be inserted
     */
    public void addToPosition(int position, BooleanFormula toAdd)
    {
    	TreePosition parentNode = this.getElementByIndex(position);
    	if(parentNode.element != null && parentNode.element.getClass().getName() == "model.tree.BooleanOperator")
    	{
    		((BooleanOperator)parentNode.element).addNewOperand(toAdd);
    		//this.simplify();
    	}
    }
    
    /**
     * Return next free variable name for temporal operator
     * @return environment variable as char
     */
    public char getNewEnvironmentVar()
    {
    	char var = 97;
    	
    	while(environmentVars.contains(var++));
    	
    	return var;
    }
    
    /**
     * Add new environment variable for temporal operator to vector
     * @param var environment variable as char 
     */
    public void addNewEnvironmentVar(char var)
    {
    	environmentVars.addElement(var);
    }
    
    /**
     * Notify listeners about literal decision for empty rule
     */
    public void notifyLiteralDecisionListener()
    {
    	for (ILiteralDecisionListener l : listener)
    	{
    		l.manualLiteralDecision();
    	}
    }
    
    
}
