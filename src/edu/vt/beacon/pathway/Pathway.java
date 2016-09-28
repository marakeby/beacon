package edu.vt.beacon.pathway;

import edu.vt.beacon.editor.properties.Contributor;
import edu.vt.beacon.graph.glyph.AbstractGlyph;
import edu.vt.beacon.map.Map;

import java.util.ArrayList;
import java.util.List;

public class Pathway {
    private Map map_;

    private String name_;

    private String organism_;

    private List<Contributor> contributorList_;

    // FIXME complete constructor
    public Pathway(String name) {
        map_ = new Map("Main Pathway");
        name_ = name;
    }

    // FIXME complete method
    public Pathway copy() {
        Pathway pathway = new Pathway(name_);
        pathway.map_ = map_.copy();

        return pathway;
    }


    public List<Contributor> getContributorList() {
        return contributorList_;
    }

    public void setContributorList(List<Contributor> contributorList) {
        this.contributorList_ = contributorList;
    }

    // TODO document method
    public Map getMap() {
        return map_;
    }

    public void setMap(Map map) {
        map_ = map;
    }

    // TODO document method
    public String getName() {
        return name_;
    }

    // TODO document method
    public void setName(String name) {
        name_ = name;
    }

    public String getOrganism() {
        return organism_;
    }

    public void setOrganism_(String organism) {
        this.organism_ = organism;
    }

    public void removeGlyphs(ArrayList<AbstractGlyph> glyphs, Map map) {

        if (map != null && glyphs != null && !glyphs.isEmpty())
            map.removeGlyphs(glyphs);

    }


}