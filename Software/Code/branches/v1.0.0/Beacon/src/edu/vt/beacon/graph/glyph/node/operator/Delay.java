package edu.vt.beacon.graph.glyph.node.operator;

import edu.vt.beacon.graph.glyph.GlyphType;
import edu.vt.beacon.graph.glyph.node.auxiliary.Label;

public class Delay extends AbstractOperator
{
    /*
     * document constructor
     */
    public Delay()
    {
        super (GlyphType.DELAY);
        
        update();
    }
    
    /*
     * document method
     */
    @Override
    protected void initializeLabel()
    {
        label_ = new Label(this, "\u03C4");
    }
}