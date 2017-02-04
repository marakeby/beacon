package edu.vt.beacon.editor.context;

import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.graph.glyph.AbstractGlyph;

public class ContextManager extends AbstractContext
{
    private BoundaryContext boundaryContext_;
    
    private StatisticsContext statisticsContext_;
    
    // FIXME complete constructor
    public ContextManager(Document document)
    {
        super (document);
        
        boundaryContext_ = new BoundaryContext(document);
        statisticsContext_ = new StatisticsContext(document);
    }
    
    // TODO document method
    public BoundaryContext getBoundaryContext()
    {
        return boundaryContext_;
    }
    
    // TODO document method
    public StatisticsContext getStatisticsContext()
    {
        return statisticsContext_;
    }
    
    // FIXME complete method
    @Override
    public void process(AbstractGlyph glyph, float zoomFactor)
    {
        boundaryContext_.process(glyph, zoomFactor);
        statisticsContext_.process(glyph, zoomFactor);
    }
    
    // FIXME complete method
    @Override
    public void reset()
    {
        boundaryContext_.reset();
        statisticsContext_.reset();
    }
}