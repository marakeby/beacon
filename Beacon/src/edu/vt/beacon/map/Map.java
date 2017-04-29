package edu.vt.beacon.map;

import edu.vt.beacon.graph.glyph.AbstractGlyph;
import edu.vt.beacon.graph.glyph.node.AbstractNode;
import edu.vt.beacon.graph.glyph.node.submap.Submap;
import edu.vt.beacon.graph.legend.Legend;
import edu.vt.beacon.layer.Layer;
import edu.vt.beacon.util.IdGenerator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Map {

    private Legend legend;

    private ArrayList<Layer> layers_;

    private Set<Submap> submaps;

    private boolean isSelected_;

    private boolean isValid_;

    private float hValidationShift_;

    private float vValidationShift_;

    private String name_;

    private String id;

    // TODO document constructor
    public Map(String name) {
        layers_ = new ArrayList<Layer>();
        submaps = new HashSet<Submap>();
        name_ = name;
        setId(IdGenerator.generate());

        validate();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Legend getLegend() {
        return legend;
    }

    public void setLegend(Legend legend) {
        this.legend = legend;
    }

    // TODO document method
    public void add(Layer layer) {
        add(layers_.size(), layer);
    }

    // TODO document method
    public void add(int index, Layer layer) {
        layer.setMap(this);
        layers_.add(layer);
    }

    // TODO document method
    public Map copy() {
        Map map = new Map(name_);
        map.isSelected_ = isSelected_;
        map.isValid_ = isValid_;
        map.hValidationShift_ = hValidationShift_;
        map.vValidationShift_ = vValidationShift_;
        if (legend != null)
            map.legend = legend.copy();

        Layer layerCopy;
        AbstractGlyph glyphCopy;

        ArrayList<AbstractGlyph> originalGlyphs =
                new ArrayList<AbstractGlyph>();
        ArrayList<AbstractGlyph> glyphCopies =
                new ArrayList<AbstractGlyph>();

        for (Layer layer : layers_) {

            layerCopy = layer.copy();
            map.add(layerCopy);

            for (int i = 0; i < layer.getGlyphCount(); i++) {

                glyphCopy = layer.getGlyphAt(i).copy();
                layerCopy.add(glyphCopy);

                originalGlyphs.add(layer.getGlyphAt(i));
            }
        }

        return map;
    }

    // TODO document method
    public float getHorizontalValidationShift() {
        return hValidationShift_;
    }

    // TODO document method
    public ArrayList<Layer> getLayers() {
        return layers_;
    }

    public Set<Submap> getSubmaps() {
        return submaps;
    }

    public void setLayers(ArrayList<Layer> layers) {
        this.layers_ = layers;
    }

    public void setSelection(int layerIndex) {
        if (layers_ == null || layers_.size() == 0)
            return;

        for (int i = 0; i < layers_.size(); i++) {
            if (layerIndex != i)
                layers_.get(i).setSelected(false);
            else
                layers_.get(i).setSelected(true);
        }
    }

    // TODO document method
    public Layer getLayerAt(int index) {
        return layers_.get(index);
    }

    // TODO document method
    public int getLayerCount() {
        return layers_.size();
    }

    // TODO document method
    public String getName() {
        return name_;
    }

    // TODO document method
    public float getVerticalValidationShift() {
        return vValidationShift_;
    }

    // TODO document method
    public void invalidate(AbstractGlyph glyph) {

        if (glyph.getAbsMinX() < 0) {

            hValidationShift_ =
                    Math.max(hValidationShift_, Math.abs(glyph.getAbsMinX()));
        }

        if (glyph.getAbsMinY() < 0) {

            vValidationShift_ =
                    Math.max(vValidationShift_, Math.abs(glyph.getAbsMinY()));
        }

        isValid_ = false;
    }

    // TODO document method
    public boolean isSelected() {
        return isSelected_;
    }

    // TODO document method
    public boolean isValid() {
        return isValid_;
    }

    // TODO document method
    public void remove(int layerIndex) {
        if (layers_ == null || layerIndex < 0 || layers_.size() <= layerIndex)
            return;

        layers_.get(layerIndex).setMap(null);
        layers_.remove(layerIndex);
    }

    // TODO document method
    public void remove(Layer layer) {
        layer.setMap(null);
        layers_.remove(layer);
    }

    public ArrayList<AbstractGlyph> getSelectedGlyphs() {

        ArrayList<AbstractGlyph> selectedGlyphs = new ArrayList<AbstractGlyph>();

        for (Layer l : layers_)
            selectedGlyphs.addAll(l.getSelectedGlyphs());

        return selectedGlyphs;
    }

    public ArrayList<AbstractNode> getInputNodes() {

        ArrayList<AbstractNode> selectedGlyphs = new ArrayList<AbstractNode>();

        for (Layer l : layers_)
            for (AbstractGlyph g: l.getGlyphs())
                if (g instanceof AbstractNode && ((AbstractNode) g).isInputNode())
                    selectedGlyphs.add((AbstractNode)g);

        return selectedGlyphs;
    }

    public ArrayList<AbstractNode> getAllNodes() {

        ArrayList<AbstractNode> selectedGlyphs = new ArrayList<AbstractNode>();

        for (Layer l : layers_)
            for (AbstractGlyph g: l.getGlyphs())
                if (g instanceof AbstractNode )
                    selectedGlyphs.add((AbstractNode)g);

        return selectedGlyphs;
    }


    public void removeGlyphs(ArrayList<AbstractGlyph> glyphs) {

        if (glyphs != null && !glyphs.isEmpty())
            for (Layer l : layers_)
                l.removeGlyphs(glyphs);


    }

    // TODO document method
    public void setName(String name) {
        name_ = name;
    }

    // TODO document method
    public void setSelected(boolean isSelected) {
        isSelected_ = isSelected;
    }

    /*
     * document
     */
    @Override
    public String toString() {
        return name_;
    }

    // TODO document method
    public void validate() {
        isValid_ = true;
        hValidationShift_ = 0.0F;
        vValidationShift_ = 0.0F;
    }

    public Layer getLayer(String layerId) {
        if (layerId == null || layers_ == null || layers_.isEmpty())
            return null;

        for (Layer layer : layers_)
            if (layer.getId().equals(layerId))
                return layer;

        return null;
    }
}