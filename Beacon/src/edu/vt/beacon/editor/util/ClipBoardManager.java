package edu.vt.beacon.editor.util;

import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.graph.glyph.AbstractGlyph;
import edu.vt.beacon.graph.glyph.GlyphType;
import edu.vt.beacon.graph.glyph.arc.AbstractArc;
import edu.vt.beacon.graph.glyph.node.container.Compartment;
import edu.vt.beacon.graph.glyph.node.submap.Submap;
import edu.vt.beacon.io.Converter;
import edu.vt.beacon.layer.Layer;
import edu.vt.beacon.map.Map;
import edu.vt.beacon.util.IdGenerator;
import org.sbgn.bindings.Sbgn;
import edu.vt.beacon.pathway.Pathway;
import java.util.ArrayList;
import java.util.Objects;

public class ClipBoardManager {

    private static final float PASTE_OFFSET = 50;
    private static int numberOfPaste = 0;
    private static Sbgn serializedPathway_;
    private static String mapId_;
    private static ArrayList<String> selectedGlyphIds_ = new ArrayList<String>();


    private static Map getCopyOfSelectedMap(Pathway pathway){

        if (Objects.equals(mapId_, pathway.getMap().getId()))
            return pathway.getMap();

        for (Submap submap : pathway.getMap().getSubmaps())
            if (mapId_.equals(submap.getMap().getId()))
                return submap.getMap();

        return null;
    }

    private static void setSelectedGlyphs(Map map){


        for (Layer l : map.getLayers())
            for (AbstractGlyph glyph : l.getGlyphs() )
                if (selectedGlyphIds_.contains(glyph.getId() ))
                    glyph.setSelected(true);

    }
    public static void copy(Map map) {


        if (map == null)
            return;

        Pathway pathway2 = new Pathway("");
        pathway2.setMap(map);
        numberOfPaste = 0;
        selectedGlyphIds_.clear();
        mapId_ = map.getId();
//        System.out.println("copy "+ mapId_);
        ArrayList<AbstractGlyph> selectedGlyphs = map.getSelectedGlyphs();
        if (selectedGlyphs == null || selectedGlyphs.isEmpty())
            return;



        serializedPathway_ = Converter.convert(pathway2);

        selectedGlyphIds_ = new ArrayList<>();
        for (AbstractGlyph glyph : selectedGlyphs)
            selectedGlyphIds_.add(glyph.getId());


    }

    public static ArrayList<AbstractGlyph> getGlyphs() {
//        System.out.println("paste");
        numberOfPaste++;
        Pathway copiedPathway = Converter.convert(serializedPathway_);
        Map copiedMap = getCopyOfSelectedMap(copiedPathway);
        ArrayList<AbstractGlyph> results = null;

        setSelectedGlyphs(copiedMap);
        if (copiedMap != null)
            results = copiedMap.getSelectedGlyphs();

        float x = PASTE_OFFSET * numberOfPaste;

        if (results != null) {
            for (AbstractGlyph glyph : results) {
                glyph.setId(IdGenerator.generate());
                if(glyph.getType() == GlyphType.EQUIVALENCE_ARC || glyph.
                        getType() == GlyphType.LOGIC_ARC || glyph.getType() == GlyphType.POSITIVE_INFLUENCE|| glyph.getType() == GlyphType.NEGATIVE_INFLUENCE ||glyph.getType() == GlyphType.NECESSARY_STIMULATION ){
                    ((AbstractArc)glyph).move_without_dependents(x,x);}
                else if(glyph.getType() ==  GlyphType.COMPARTMENT){
                    ((Compartment)glyph).move_without_dependents(x,x);}
                else
                    glyph.move(x,x);
            }
        }


        return results;

    }

}