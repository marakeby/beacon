package edu.vt.beacon.simulation.model.tree;

public class TreePosition {

	// the parent expression
	public BooleanOperator parent;

	// the index of the element in the parent operator
	public int index;

	// the element itself
	public BooleanFormula element;

	/**
	 * Creates a new TreePosition with the following parameters:
	 * <parent>: the parent expression
	 * <index>: the index of the element in the parent operator
	 * <element>: the element itself
	 */
	TreePosition(BooleanOperator parent, int index,	BooleanFormula element)
	{
		this.parent = parent;
		this.index = index;
		this.element = element;
	}
	
	
}
