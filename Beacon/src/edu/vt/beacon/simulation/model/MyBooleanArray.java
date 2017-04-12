package edu.vt.beacon.simulation.model;

import java.util.Arrays;

public class MyBooleanArray {

	public boolean[] array;
	
	public MyBooleanArray(boolean[] a)
	{
		array = a;
	}
	
	@Override
	public boolean equals(Object b)
	{
		// If the object is compared with itself then return true  
        if (b == this) {
            return true;
        }
 
        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(b instanceof MyBooleanArray)) {
            return false;
        }
        
        MyBooleanArray tmp = (MyBooleanArray)b;
        
        return (Arrays.equals(array, tmp.array));
	}
	
	@Override
	public int hashCode()
	{
		int h = 0;
		for (int i = 0; i < array.length; ++i)
			h |= array[i] ? 1 : 0 << i;
		return h;
	}
}
