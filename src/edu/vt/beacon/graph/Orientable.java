package edu.vt.beacon.graph;

public interface Orientable
{
    /*
     * document method
     */
    public abstract OrientationType getOrientation();
    
    /*
     * document method
     */
    public abstract void setOrientation(OrientationType orientation);
}