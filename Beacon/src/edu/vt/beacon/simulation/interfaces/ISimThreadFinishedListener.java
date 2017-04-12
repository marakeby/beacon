package edu.vt.beacon.simulation.interfaces;

/**
 * If the simulation/attractor search is finshed, the GUI is notified by this function
 * The results of the attractor search are send as Vector<Vector<boolean[]>> to the GUI
 * In the GUI an attractor plot is created.
 */
import java.util.Vector;

public interface ISimThreadFinishedListener {
	
	
	public void visualizeSimulationResults(Vector<Vector<boolean[]>> attractors);

}
