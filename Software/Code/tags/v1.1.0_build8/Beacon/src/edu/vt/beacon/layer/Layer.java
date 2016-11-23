package edu.vt.beacon.layer;

import edu.vt.beacon.graph.glyph.AbstractGlyph;
import edu.vt.beacon.graph.glyph.node.submap.Submap;
import edu.vt.beacon.graph.glyph.node.submap.Terminal;
import edu.vt.beacon.map.Map;
import edu.vt.beacon.util.IdGenerator;

import java.util.ArrayList;

public class Layer {
    private ArrayList<AbstractGlyph> glyphs_;

    private boolean isActive_;

    private boolean isSelected_;

    private Map map_;

    private String name_;

    private String id;

    // TODO document constructor
    public Layer(String name, Map map) {
        glyphs_ = new ArrayList<AbstractGlyph>();
        isActive_ = true;
        name_ = name;
        map_ = map;
        id = IdGenerator.generate();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // TODO document method
    public void add(AbstractGlyph glyph) {
        glyph.setLayer(this);
        glyphs_.add(glyph);

        if (glyph instanceof Submap)
            map_.getSubmaps().add((Submap) glyph);
    }

    // TODO document method
    public Layer copy() {
        Layer layer = new Layer(name_, map_);
        layer.isActive_ = isActive_;
        layer.isSelected_ = isSelected_;

        return layer;
    }

    // TODO document method
    public AbstractGlyph getGlyphAt(int index) {
        return glyphs_.get(index);
    }

    public ArrayList<AbstractGlyph> getGlyphs() {
        return glyphs_;
    }

    // TODO document method
    public int getGlyphCount() {
        return glyphs_.size();
    }

    // TODO document method
    public Map getMap() {
        return map_;
    }

    // TODO document method
    public String getName() {
        return name_;
    }

    // TODO document method
    public boolean isActive() {
        return isActive_;
    }

    // TODO document method
    public boolean isSelected() {
        return isSelected_;
    }

    // TODO document method
    public void remove(AbstractGlyph glyph) {
        glyph.setLayer(null);
        glyphs_.remove(glyph);
    }

    public ArrayList<AbstractGlyph> getSelectedGlyphs() {

        ArrayList<AbstractGlyph> selectedGlyphs = new ArrayList<AbstractGlyph>();

        for (AbstractGlyph g : glyphs_) {
            if (g.isSelected())
                selectedGlyphs.add(g);

            if (g instanceof Submap && ((Submap) g).getTerminals() != null)
                for (Terminal t : ((Submap) g).getTerminals())
                    if (t.isSelected())
                        selectedGlyphs.add(t);
        }

        return selectedGlyphs;
    }

    public void removeGlyphs(ArrayList<AbstractGlyph> glyphs) {

        if (glyphs != null && !glyphs.isEmpty())
            glyphs_.removeAll(glyphs);

    }

    // TODO document method
    public void setActive(boolean isActive) {
        isActive_ = isActive;
    }

    // TODO document method
    public void setMap(Map map) {
        map_ = map;
    }

    // TODO document method
    public void setName(String name) {
        name_ = name;
    }

    // TODO document method
    public void setSelected(boolean isSelected) {
        isSelected_ = isSelected;
    }

    @Override
    public String toString() {
        return (name_ == null) ? "" : name_;
    }
}