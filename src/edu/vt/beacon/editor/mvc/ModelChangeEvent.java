package edu.vt.beacon.editor.mvc;

import java.beans.PropertyChangeEvent;

public class ModelChangeEvent extends PropertyChangeEvent
{
    private static final long serialVersionUID = 1L;
    
    /*
     * XXX: validate constructor
     */
    public ModelChangeEvent(AbstractModel model, String propertyName,
                            Object oldValue, Object newValue)
    {
        super (model, propertyName, oldValue, newValue);
    }
}