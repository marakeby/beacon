package edu.vt.beacon.pathway;

import edu.vt.beacon.editor.properties.Contributor;
import edu.vt.beacon.graph.glyph.AbstractGlyph;
import edu.vt.beacon.io.Converter;
import edu.vt.beacon.map.Map;
import org.sbgn.bindings.Sbgn;

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
//    public Pathway copy() {
//        Pathway pathway = new Pathway(name_);
//        pathway.map_ = map_.copy();
//
//        return pathway;
//    }



    public Pathway copy() {
//        Pathway pathway = new Pathway(name_);
//        pathway.map_ = map_.copy();
        Sbgn serializedPathway = Converter.convert(this);
        Pathway copiedPathway = Converter.convert(serializedPathway);

        if (getMap().getLegend() != null)
            copiedPathway.getMap().setLegend(this.getMap().getLegend().copy());

        return copiedPathway;
//        return pathway;
    }


    public List<Contributor> getContributorList() {
        return contributorList_;
    }


    /*
    we need this method between we store the contributors info in a String when we save the pathway in a file
    so this method convert the info into a stirng, the syntax is
    name1, institution1, email1 (corresponding contibutor);name2, institution2, email2 ..................
     */
    public String getContributorListText() {
        String str = "";
        if (contributorList_ !=  null){
            for (Contributor ctb: contributorList_){
                str = str + ctb.toString() + ";";
            }
            return str;
        }
        return "";
    }

    /*
    we parse a specific string and get the contributors' infomation, and set the contributor list
     */
    public void setContributorList(String str) {
        String ctbs[] = str.split(";");
        if (contributorList_ == null){
            contributorList_ = new ArrayList<Contributor>();
        }
        for (String ctb: ctbs){
            String attribute[] = ctb.split(", ");
            if (attribute != null && attribute.length == 3){
                if (attribute[2].contains(" (corresponding contibutor)")){
                    attribute[2] = attribute[2].replace(" (corresponding contibutor)", "");
                    contributorList_.add(new Contributor(attribute[0],attribute[1],attribute[2],true));
                }
                else{
                    contributorList_.add(new Contributor(attribute[0],attribute[1],attribute[2],false));
                }
            }
        }
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