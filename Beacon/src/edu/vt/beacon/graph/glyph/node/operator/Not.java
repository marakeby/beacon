package edu.vt.beacon.graph.glyph.node.operator;

import edu.vt.beacon.graph.glyph.GlyphType;
import edu.vt.beacon.graph.glyph.node.auxiliary.Label;

public class Not extends AbstractOperator
{
    /*
     * document constructor
     */
    public Not()
    {
        super (GlyphType.NOT);
        
        update();
    }
    
    /*
     * document method
     */
    @Override
    protected void initializeLabel()
    {
        label_ = new Label(this, "NOT");
    }
}