package edu.vt.beacon.editor.context;

import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.graph.glyph.AbstractGlyph;

public abstract class AbstractContext
{
    protected Document document_;
    
    // TODO document constructor
    protected AbstractContext(Document document)
    {
        document_ = document;
    }
    
    // TODO document method
    public abstract void process(AbstractGlyph glyph, float zoomFactor);
    
    // TODO document method
    public abstract void reset();
}