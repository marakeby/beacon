package edu.vt.beacon.editor.mvc;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class AbstractModel
{
    private ArrayList<ModelChangeListener> listeners_;
    
    private HashMap<String, Object> propertyMap_;
    
    /*
     * XXX: validate constructor
     */
    protected AbstractModel()
    {
        listeners_ = new ArrayList<ModelChangeListener>();
        propertyMap_ = new HashMap<String, Object>();
    }
    
    /*
     * XXX: validate method
     */
    public void addModelChangeListener(ModelChangeListener listener)
    {
        listeners_.add(listener);
    }
    
    /*
     * XXX: validate method
     */
    public Object get(String key)
    {
        return propertyMap_.get(key);
    }
    
    /*
     * XXX: validate method
     */
    public void put(String key, Object value)
    {
        Object oldValue = propertyMap_.put(key, value);
        
        if (!value.equals(oldValue))
        {
            ModelChangeEvent event = new ModelChangeEvent(
                this, key, oldValue, value);
            
            for (ModelChangeListener listener : listeners_)
                listener.modelChange(event);
        }
    }
    
    /*
     * XXX: validate method
     */
    public void removeModelChangeListener(ModelChangeListener listener)
    {
        listeners_.remove(listener);
    }
}