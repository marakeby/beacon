package edu.vt.beacon.graph.glyph.node.submap;

import edu.vt.beacon.graph.OrientationType;
import edu.vt.beacon.graph.glyph.GlyphType;
import edu.vt.beacon.graph.glyph.node.AbstractNode;
import edu.vt.beacon.graph.glyph.node.auxiliary.Label;
import edu.vt.beacon.graph.glyph.node.auxiliary.Port;
import edu.vt.beacon.graph.glyph.node.auxiliary.PortType;
import edu.vt.beacon.layer.Layer;
import edu.vt.beacon.map.Map;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Submap extends AbstractNode {
    private Map map_;
    private Set<Terminal> terminals;
    private Set<Tag> tags;
    private HashMap<Tag, Terminal> tagToTerminalMapping;
    private HashMap<Terminal, Tag> terminalToTagMapping;
    private HashMap<Terminal, Port> terminalToPortMapping;

    // FIXME complete constructor
    public Submap() {
        super(GlyphType.SUBMAP);

        terminals = new HashSet<Terminal>();
        tags = new HashSet<Tag>();
        tagToTerminalMapping = new HashMap<Tag, Terminal>();
        terminalToTagMapping = new HashMap<Terminal, Tag>();
        terminalToPortMapping = new HashMap<Terminal, Port>();

        map_ = new Map("submap");
        map_.add(new Layer("New Layer", map_));

        update();
    }

//    @Override
//    public AbstractNode copy() {
//        Submap submap = (Submap) super.copy();
//        submap.setMap(map_.copy());
//
//        HashMap<Tag, Tag> tagTagMapping = new HashMap<Tag, Tag>(tags.size());
//        HashMap<Terminal, Terminal> terminalTerminalMapping = new HashMap<Terminal, Terminal>(terminals.size());
//
//        for (Tag tag : tags) {
//            Tag copiedTag = (Tag) tag.copy();
//            copiedTag.setParent(submap);
//            submap.getTags().add(copiedTag);
//            tagTagMapping.put(tag, copiedTag);
//        }
//
//        submap.getPorts().clear();
//
//        for (Terminal terminal : terminals) {
//            Terminal copiedTerminal = (Terminal) terminal.copy();
//            copiedTerminal.setParent(submap);
//
//            Port port = terminalToPortMapping.get(terminal);
//            Port copiedPort = new Port(submap, port.getType());
//
//            submap.getTerminals().add(copiedTerminal);
//            submap.getTerminalToPortMapping().put(copiedTerminal, copiedPort);
//
//            terminalTerminalMapping.put(terminal, copiedTerminal);
//        }
//
//        for (Tag tag : tags) {
//
//            Tag copiedTag = tagTagMapping.get(tag);
//            Terminal copiedTerminal = terminalTerminalMapping.get(tagToTerminalMapping.get(tag));
//
//            submap.getTagToTerminalMapping().put(copiedTag, copiedTerminal);
//            submap.getTerminalToTagMapping().put(copiedTerminal, copiedTag);
//
//        }
//
//        return submap;
//    }

    /*
         * document
         */
    public Map getMap() {
        return map_;
    }

    public void setMap(Map map) {
        this.map_ = map;
    }

    public Set<Terminal> getTerminals() {
        return terminals;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public HashMap<Tag, Terminal> getTagToTerminalMapping() {
        return tagToTerminalMapping;
    }

    public HashMap<Terminal, Tag> getTerminalToTagMapping() {
        return terminalToTagMapping;
    }

    public HashMap<Terminal, Port> getTerminalToPortMapping() {
        return terminalToPortMapping;
    }

    /*
                 * document
                 */
    @Override
    protected void initializeLabel() {
        label_ = new Label(this, "Submap");
    }

    /*
     * document
     */
    @Override
    protected void initializePorts() {
        ports_ = new ArrayList<Port>();
    }

    /*
     * document
     */
    @Override
    protected void initializeShape() {
        shape_ = new Rectangle2D.Float();
    }

    /*
     * document
     */
    @Override
    protected void setPortCoordinates() {
    }

    /*
     * document
     */
    @Override
    protected void setShapeCoordinates() {
        Rectangle2D.Float rectangle = (Rectangle2D.Float) shape_;
        rectangle.setFrame(getMinX(), getMinY(), getWidth(), getHeight());
    }

    @Override
    public void move(float deltaX, float deltaY) {
        super.move(deltaX, deltaY);

        if (terminals != null && !terminals.isEmpty())
            for (Terminal terminal : terminals)
                terminal.move(deltaX, deltaY);
    }

    public boolean createTerminal(Tag tag) {

        if (tag == null)
            return false;

        Terminal terminal = new Terminal(this);
        terminal.setOrientation(tag.getOrientation());
        terminal.setLayer(getLayer());
        terminal.setWidth(25);
        terminal.setHeight(15);
        terminal.setText(tag.getText());
        terminal.moveIntoSubmap(getMinX() + terminal.getLineWidth() / 2, getMaxY() - 10 - terminal.getHeight());
        terminal.update();
        tags.add(tag);
        addTerminal(terminal);

        tagToTerminalMapping.put(tag, terminal);
        terminalToTagMapping.put(terminal, tag);

        return true;
    }

    public boolean createTag(Terminal terminal) {

        if (terminal == null)
            return false;

        Tag tag = new Tag(this);
        tag.setOrientation(terminal.getOrientation());

        if (terminal.getParent().getMap().getLayers().isEmpty())
            terminal.getParent().getMap().getLayers().add(new Layer("New Layer", terminal.getParent().getMap()));

        terminal.getParent().getMap().getLayerAt(0).add(tag);
        tag.setWidth(25);
        tag.setHeight(15);
        tag.setLineWidth(terminal.getLineWidth());
        tag.setBackgroundColor(terminal.getBackgroundColor());
        tag.setForegroundColor(terminal.getForegroundColor());
        tag.setFontColor(terminal.getFontColor());
        tag.setText(terminal.getText());
        tag.move(terminal.getMinX(), terminal.getMinY());
        tag.update();
        tags.add(tag);
        addTerminal(terminal);

        terminalToTagMapping.put(terminal, tag);
        tagToTerminalMapping.put(tag, terminal);

        return true;
    }

    private void addTerminal(Terminal terminal) {
        if (terminal == null || terminals.contains(terminal))
            return;

        terminals.add(terminal);
        terminal.setLayer(getLayer());
        Port port = new Port(this, PortType.ARBITRARY);
        ports_.add(port);
        updatePortLocation(port, terminal);
        terminalToPortMapping.put(terminal, port);
    }

    private void updatePortLocation(Port port, Terminal terminal) {

        switch (terminal.getOrientation()) {

            case DOWN:
                port.setCenter(terminal.getCenterX(), terminal.getMinY());
                break;

            case UP:
                port.setCenter(terminal.getCenterX(), terminal.getMaxY());
                break;

            case LEFT:
                port.setCenter(terminal.getMaxX(), terminal.getCenterY());
                break;

            case RIGHT:
                port.setCenter(terminal.getMinX(), terminal.getCenterY());
                break;
        }

        port.update();
    }


    public void updatePortsLocation() {

        for (Terminal t : terminals) {
            Port port = terminalToPortMapping.get(t);
            updatePortLocation(port, t);
        }

    }

    @Override
    public float getMinHeight() {
        if (terminals == null || terminals.isEmpty())
            return super.getMinHeight();

        float minHeight = super.getMinHeight();
        for (Terminal t : terminals)
            minHeight = Math.max(minHeight, t.getHeight());

        return minHeight;
    }

    @Override
    public float getMinWidth() {
        if (terminals == null || terminals.isEmpty())
            return super.getMinWidth();

        float minWidth = super.getMinWidth();
        for (Terminal t : terminals)
            minWidth = Math.max(minWidth, t.getWidth());

        return minWidth;
    }

    @Override
    public void setHeight(float height) {
        if (height < getMinHeight())
            return;

        super.setHeight(height);

        if (terminals != null)
            for (Terminal t : terminals) {

                if (t.getMinY() < getMinY())
                    t.move(0, getMinY() - t.getMinY());

                else if (t.getMaxY() > getMaxY())
                    t.move(0, getMaxY() - t.getMaxY());

                if (t.getOrientation() == OrientationType.UP && t.getMaxY() < getMaxY())
                    t.move(0, getMaxY() - t.getMaxY());
            }

    }

    @Override
    public void setWidth(float width) {
        if (width < getMinWidth())
            return;

        super.setWidth(width);

        if (terminals != null)
            for (Terminal t : terminals) {

                if (t.getMinX() < getMinX())
                    t.move(getMinX() - t.getMinX(), 0);

                else if (t.getMaxX() > getMaxX())
                    t.move(getMaxX() - t.getMaxX(), 0);

                if (t.getOrientation() == OrientationType.LEFT && t.getMaxX() < getMaxX())
                    t.move(getMaxX() - t.getMaxX(), 0);

            }

    }
}