package edu.vt.beacon.editor.simulation;

import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.document.DocumentState;
import edu.vt.beacon.graph.glyph.AbstractGlyph;
import edu.vt.beacon.graph.glyph.node.AbstractNode;
import edu.vt.beacon.layer.Layer;
import edu.vt.beacon.map.Map;

import java.awt.*;
import java.util.HashMap;

/**
 * Created by marakeby on 5/4/17.
 */
public class Coloring {

    public static void setBackColors(Document doc, HashMap<String, Boolean> states){
        if (states ==null)
            return;
        Map map = doc.getPathway().getMap();


        for (Layer l : map.getLayers())
            for (AbstractGlyph g : l.getGlyphs()) {
                if (g instanceof AbstractNode && states.get(g.getId()) !=null ) {
                    if (states.get(g.getId()) )
                        g.setBackgroundColor(new Color(209, 255, 215));
                    else
                        g.setBackgroundColor(new Color(255, 209, 209));
                }

            }
        doc.refresh();
        new DocumentState(doc, "Simulation Coloring", false);


    }
}
