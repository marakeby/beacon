package edu.vt.beacon.simulation.interfaces;

import edu.vt.beacon.simulation.model.containers.NetworkContainer;

/**
 * Interface to send signal from model to the simulation. 
 * If there are any changes in the model, this function will be called and the simulation is updated 
 */


public interface IModelChanged {

	public void checkSimulation(NetworkContainer network);
}
