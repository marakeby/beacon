package edu.vt.beacon.editor.util;

import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.graph.glyph.AbstractGlyph;
import edu.vt.beacon.graph.glyph.node.AbstractNode;
import edu.vt.beacon.graph.legend.Legend;
import edu.vt.beacon.graph.legend.LegendEntry;
import edu.vt.beacon.layer.Layer;
import edu.vt.beacon.map.Map;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ppws on 3/25/16.
 */
public class LegendManager {

    private HashMap<Color, String> colorMapping;
    private ArrayList<Color> colorOrdering;
    private Document document;

    public LegendManager(Document document) {
        colorMapping = new HashMap<Color, String>();
        colorOrdering = new ArrayList<Color>();
        this.document = document;
    }

    public ArrayList<Color> getColorOrdering() {
        return colorOrdering;
    }

    public void setColorOrdering(ArrayList<Color> colorOrdering) {
        this.colorOrdering = colorOrdering;
    }

    public String getColorDescription(Color color) {
        if (color == null || colorMapping == null)
            return null;

        return colorMapping.get(color);
    }

    public void setColorDescription(Color color, String description) {
        if (color == null || colorMapping == null || !colorMapping.keySet().contains(color))
            return;

        colorMapping.put(color, description);
    }

    public void populateMapping() {

        if (document == null || document.getPathway() == null || document.getPathway().getMap() == null)
            return;

        Map map = document.getPathway().getMap();

        HashSet<Color> existingColors = new HashSet<Color>();

        for (Layer layer : map.getLayers())
            for (AbstractGlyph glyph : layer.getGlyphs()) {

                existingColors.add(glyph.getBackgroundColor());
                existingColors.add(glyph.getForegroundColor());

                if (glyph instanceof AbstractNode)
                    existingColors.add(((AbstractNode) glyph).getFontColor());
            }

        existingColors.remove(Color.white);

        for (Color color : existingColors)
            if (!colorMapping.containsKey(color))
                colorMapping.put(color, "");

        Set<Color> removedColors = new HashSet<Color>(colorMapping.keySet());
        removedColors.removeAll(existingColors);

        for (Color removedColor : removedColors)
            colorMapping.remove(removedColor);

        colorOrdering.removeAll(removedColors);
        for (Color color : existingColors)
            if (!colorOrdering.contains(color))
                colorOrdering.add(color);
    }

    public void removeLegend() {
        if (document == null || document.getPathway() == null || document.getPathway().getMap() == null)
            return;

        document.getPathway().getMap().setLegend(null);
        document.refresh();
    }

    public void createLegend() {

        Legend previousLegend = document.getPathway().getMap().getLegend();
        Legend legend;

        if (previousLegend == null)
            legend = new Legend();

        else {

            legend = previousLegend.copy();
            if (legend.getEntries() != null)
                legend.getEntries().clear();

        }


        for (Color color : colorOrdering)
            if (!getColorDescription(color).trim().equals(""))
                legend.addEntry(new LegendEntry(legend, color, getColorDescription(color)));

        document.getPathway().getMap().setLegend(legend);
        legend.update();
        legend.move(10, 10);
        document.refresh();
    }

}
