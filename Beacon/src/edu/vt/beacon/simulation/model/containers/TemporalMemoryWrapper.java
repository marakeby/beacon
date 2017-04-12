package edu.vt.beacon.simulation.model.containers;

import java.util.Vector;

public class TemporalMemoryWrapper {

	
	private Vector<Integer> maxTemp;
	private int additionalMemory;
	private Vector<Character> envVars;
	
	public TemporalMemoryWrapper(Vector<Integer> maxTemp, int addTemp, Vector<Character> envVars) 
	{
		this.maxTemp = maxTemp;
		this.additionalMemory = addTemp;
		this.envVars = envVars;
	}
	
	public Vector<Integer> getMaxTemp() 
	{
		return maxTemp;
	}
	
	public int getAdditionalMemory() 
	{
		return additionalMemory;
	}

	public Vector<Character> getEnvVars() {
		return envVars;
	}
	
	
	
	
	
	
}
