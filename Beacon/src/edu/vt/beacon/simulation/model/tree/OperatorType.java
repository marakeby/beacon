package edu.vt.beacon.simulation.model.tree;

public enum OperatorType {
	
	OP_AND(0,"AND", "ALL"),
	OP_OR(1,"OR", "ANY"),
	OP_XOR(2,"XOR", "NO"),
    OP_COMMA(3,",", "NO");
    
    
    public final int no;
    public final String name;
    public final String temporalName;
	private OperatorType(int no, String name, String temporalName)
    {
		this.no = no;
		this.name = name;
		this.temporalName = temporalName;
    }
	
	public String toString()
	{
		return name;
	}
	
	public String toTemporalString()
	{
		return temporalName;
	}

}
