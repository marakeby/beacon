package edu.vt.beacon.io.backwardcompatibility;

import edu.vt.beacon.graph.AbstractEntity;
import edu.vt.beacon.graph.OrientationType;
import edu.vt.beacon.graph.glyph.AbstractGlyph;
import edu.vt.beacon.graph.glyph.GlyphType;
import edu.vt.beacon.graph.glyph.arc.AbstractArc;
import edu.vt.beacon.graph.glyph.node.AbstractNode;
import edu.vt.beacon.graph.glyph.node.activity.BiologicalActivity;
import edu.vt.beacon.graph.glyph.node.activity.Phenotype;
import edu.vt.beacon.graph.glyph.node.auxiliary.*;
import edu.vt.beacon.graph.glyph.node.container.Compartment;
import edu.vt.beacon.graph.glyph.node.submap.Submap;
import edu.vt.beacon.graph.glyph.node.submap.Tag;
import edu.vt.beacon.graph.glyph.node.submap.Terminal;
import edu.vt.beacon.layer.Layer;
import edu.vt.beacon.map.Map;
import edu.vt.beacon.pathway.Pathway;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by ppws on 2/17/16.
 */
public class OldVersionConverter {

    private HashMap<String, Object> beaconMapIdToObjects = new HashMap<String, Object>();
    private HashMap<String, Layer> beaconMapIdToLayers = new HashMap<String, Layer>();
    private OldVersionParser parser = null;

    public Pathway convert(OldVersionParser parser) {
        beaconMapIdToObjects.clear();
        beaconMapIdToLayers.clear();
        this.parser = parser;

        Pathway pathway = new Pathway("");

        setPathwayInfo(parser, pathway);

        setLayers(pathway.getMap());

        convert(parser, pathway.getMap());

        return pathway;
    }

    private boolean setPathwayInfo(OldVersionParser parser, Pathway pathway) {
        if (parser == null || pathway == null)
            return false;

        return true;
    }

    private boolean setLayers(Map map) {
        Layer layer = new Layer("New Layer", map);
        map.add(layer);
        beaconMapIdToLayers.put(layer.getId(), layer);
        return true;
    }

    private Map convert(OldVersionParser parser, Map map) {
        if (parser == null) {
            return null;
        }

        for (HashMap<String, Object> entity : parser.getAllEntities())
            if (isCompartment(entity)) {
                AbstractNode node = convertGlyph(entity);
                beaconMapIdToObjects.put(node.getId(), node);
            }

        for (HashMap<String, Object> entity : parser.getAllEntities())
            if (isSubmap(entity)) {
                AbstractNode node = convertGlyph(entity);

                String path = parser.filename.substring(0, parser.filename.length() - 11) + parser.SUBMAPS_DIR;
                OldVersionParser submapParser = new OldVersionParser(path + node.getId() + ".bpw");
                Pathway submapPathway = new OldVersionConverter().convert(submapParser);

                submapPathway.getMap().setName(node.getText());
                ((Submap) node).setMap(submapPathway.getMap());

                setupSubmapTerminals(submapPathway.getMap(), ((Submap) node));

                beaconMapIdToObjects.put(node.getId(), node);
            }

        for (HashMap<String, Object> entity : parser.getAllEntities())
            if (!isSubmap(entity) && !isCompartment(entity) && !isArc(entity)) {
                AbstractNode node = convertGlyph(entity);
                beaconMapIdToObjects.put(node.getId(), node);
            }

        for (HashMap<String, Object> entity : parser.getAllEntities())
            if (isArc(entity)) {
                AbstractArc beaconArc = convertArc(entity);
                beaconMapIdToObjects.put(beaconArc.getId(), beaconArc);
            }

        return map;
    }

    private void setupSubmapTerminals(Map submapMap, Submap submapGlyph) {

        if (submapMap == null || submapGlyph == null)
            return;

        HashMap<OrientationType, ArrayList<Tag>> orientationTagMapping = new HashMap<OrientationType, ArrayList<Tag>>(4);
        orientationTagMapping.put(OrientationType.DOWN, new ArrayList<Tag>());
        orientationTagMapping.put(OrientationType.UP, new ArrayList<Tag>());
        orientationTagMapping.put(OrientationType.RIGHT, new ArrayList<Tag>());
        orientationTagMapping.put(OrientationType.LEFT, new ArrayList<Tag>());

        if (submapMap.getLayers() == null || submapMap.getLayerCount() == 0)
            return;

        for (AbstractGlyph glyph : submapMap.getLayerAt(0).getGlyphs())
            if (glyph instanceof Tag)
                orientationTagMapping.get(((Tag) glyph).getOrientation()).add((Tag) glyph);

        for (Tag tag : orientationTagMapping.get(OrientationType.RIGHT))
            createTerminal(tag, submapGlyph, OrientationType.RIGHT, orientationTagMapping.get(OrientationType.RIGHT).size(),
                    orientationTagMapping.get(OrientationType.RIGHT).indexOf(tag));


        for (Tag tag : orientationTagMapping.get(OrientationType.DOWN))
            createTerminal(tag, submapGlyph, OrientationType.DOWN, orientationTagMapping.get(OrientationType.DOWN).size(),
                    orientationTagMapping.get(OrientationType.DOWN).indexOf(tag));

        for (Tag tag : orientationTagMapping.get(OrientationType.LEFT))
            createTerminal(tag, submapGlyph, OrientationType.LEFT, orientationTagMapping.get(OrientationType.LEFT).size(),
                    orientationTagMapping.get(OrientationType.LEFT).indexOf(tag));


        for (Tag tag : orientationTagMapping.get(OrientationType.UP))
            createTerminal(tag, submapGlyph, OrientationType.UP, orientationTagMapping.get(OrientationType.UP).size(),
                    orientationTagMapping.get(OrientationType.UP).indexOf(tag));

    }

    private void createTerminal(Tag tag, Submap submap, OrientationType orientation, int total, int index) {

        Terminal terminal = new Terminal(submap);
        Port port = new Port(submap, PortType.ARBITRARY);
        submap.getPorts().add(port);
        submap.getTerminalToPortMapping().put(terminal, port);
        initializeTerminal(terminal, tag);
        terminal.setOrientation(orientation);

        switch (orientation) {
            case RIGHT:
                terminal.moveIntoSubmap(submap.getMinX(), submap.getMinY() +
                        (index + 1) * submap.getHeight() / (total + 1) - terminal.getHeight() / 2);
                port.setCenter(terminal.getMinX(), terminal.getCenterY());
                break;

            case LEFT:
                terminal.moveIntoSubmap(submap.getMaxX() - terminal.getWidth(), submap.getMinY() +
                        (index + 1) * submap.getHeight() / (total + 1) - terminal.getHeight() / 2);
                port.setCenter(terminal.getMaxX(), terminal.getCenterY());
                break;

            case DOWN:
                terminal.moveIntoSubmap(submap.getMinX() +
                        (index + 1) * submap.getWidth() / (total + 1) - terminal.getWidth() / 2, submap.getMinY());
                port.setCenter(terminal.getCenterX(), terminal.getMinY());
                break;

            case UP:
                terminal.moveIntoSubmap(submap.getMinX() +
                        (index + 1) * submap.getWidth() / (total + 1) - terminal.getWidth() / 2, submap.getMaxY() - terminal.getHeight());
                port.setCenter(terminal.getCenterX(), terminal.getMaxY());
                break;

        }

        setupTerminalDependencies(terminal, tag, submap);

    }

    private void setupTerminalDependencies(Terminal terminal, Tag tag, Submap submap) {

        if (terminal == null || tag == null || submap == null)
            return;

        submap.getTerminals().add(terminal);
        submap.getTags().add(tag);
        submap.getTerminalToTagMapping().put(terminal, tag);
        submap.getTagToTerminalMapping().put(tag, terminal);

    }

    private void initializeTerminal(Terminal terminal, Tag tag) {
        if (terminal == null || tag == null)
            return;

        terminal.setBackgroundColor(tag.getBackgroundColor());
        terminal.setForegroundColor(tag.getForegroundColor());
        terminal.setFontColor(tag.getFontColor());
        terminal.setPadding(0);
        terminal.setText(tag.getText());
        terminal.setHeight(tag.getHeight());
        terminal.setWidth(tag.getWidth());
    }

    private boolean isArc(HashMap<String, Object> entity) {
        if (entity == null)
            return false;

        return entity.get(OldVersionParser.CLASS).equals(OldVersionParser.EQUIVALENCE_ARC) ||
                entity.get(OldVersionParser.CLASS).equals(OldVersionParser.LOGIC_ARC) ||
                entity.get(OldVersionParser.CLASS).equals(OldVersionParser.NECESSARY_STIMULATION) ||
                entity.get(OldVersionParser.CLASS).equals(OldVersionParser.NEGATIVE_INFLUENCE) ||
                entity.get(OldVersionParser.CLASS).equals(OldVersionParser.POSITIVE_INFLUENCE) ||
                entity.get(OldVersionParser.CLASS).equals(OldVersionParser.UNKNOWN_INFLUENCE);
    }

    private boolean isSubmap(HashMap<String, Object> entity) {
        if (entity == null)
            return false;

        return entity.get(OldVersionParser.CLASS).equals(OldVersionParser.SUBMAP);
    }

    private boolean isCompartment(HashMap<String, Object> entity) {
        if (entity == null)
            return false;

        return entity.get(OldVersionParser.CLASS).equals(OldVersionParser.COMPARTMENT);
    }

    private AbstractNode convertGlyph(HashMap<String, Object> glyph) {
        if (glyph == null)
            return null;

        AbstractNode node = null;

        if ("perturbation".equalsIgnoreCase((String) glyph.get(OldVersionParser.CLASS))) {

            node = new BiologicalActivity();
            node.setLineWidth(3);
            ((BiologicalActivity) node).setAuxiliaryUnit(new PerturbationUnit());

        } else {

            for (GlyphType type : GlyphType.values())
                if (type.toString().replace(' ', '_').equalsIgnoreCase((String) glyph.get(OldVersionParser.CLASS))) {
                    node = (AbstractNode) type.newGlyph();
                    break;
                }

        }

        node.setId((String) glyph.get(OldVersionParser.ID));

        if (node instanceof Compartment)
            node.setLineWidth(5);
        else
            node.setLineWidth(3);

        node.setPadding(0);

        setBoundingBox((HashMap<String, Object>) glyph.get(OldVersionParser.BOUNDARY), node);

        setFont((HashMap<String, Object>) glyph.get(OldVersionParser.FONT), node);
        setShapeStyle((HashMap<String, Object>) glyph.get(OldVersionParser.COLOR), node);
        node.setOrientation(getOrientation((String) glyph.get(OldVersionParser.ORIENTATION)));

        setAuxiliaryUnit((HashMap<String, Object>) glyph.get(OldVersionParser.UNIT_OF_INFO), node);

        String xx= (String) glyph.get(OldVersionParser.COMPARTMENT);
        Compartment container = getParentCompartment(xx);
        node.setParentCompartment(container);
        setLabel((HashMap<String, Object>) glyph.get(OldVersionParser.LABEL), node);

        setLayer(node);

        if ("perturbation".equalsIgnoreCase((String) glyph.get(OldVersionParser.CLASS))) {

            AuxiliaryUnit perturbationUnit = ((BiologicalActivity) node).getAuxiliaryUnit();
            perturbationUnit.setBackgroundColor(node.getBackgroundColor());

            if (!node.getBackgroundColor().equals(node.getForegroundColor()))
                perturbationUnit.setForegroundColor(node.getForegroundColor());
            else
                perturbationUnit.setForegroundColor(node.getFontColor());

        }

        node.update();

        return node;
    }

    private boolean setShapeStyle(HashMap<String, Object> color, AbstractGlyph node) {
        if (color == null || node == null)
            return false;

        node.setBackgroundColor(Color.decode((String) color.get(OldVersionParser.BACKGROUND_COLOR)));
        node.setForegroundColor(Color.decode((String) color.get(OldVersionParser.FOREGROUND_COLOR)));

        if (node instanceof AbstractNode)
            ((AbstractNode) node).setFontColor(Color.decode((String) color.get(OldVersionParser.LABEL_COLOR)));

        return true;
    }

    private boolean setFont(HashMap<String, Object> font, AbstractNode node) {
        if (font == null || node == null)
            return false;

        String fontName = (String) font.get(OldVersionParser.NAME);
        int fontSize = Integer.parseInt((String) font.get(OldVersionParser.SIZE));

        if (node instanceof Compartment)
            node.setFont(new Font(fontName, 1, fontSize));
        else
            node.setFont(new Font(fontName, 0, fontSize));

        return true;
    }

    private boolean setFont(HashMap<String, Object> font, AuxiliaryUnit aux) {
        if (font == null || aux == null)
            return false;

        String fontName = (String) font.get(OldVersionParser.NAME);
        int fontSize = Integer.parseInt((String) font.get(OldVersionParser.SIZE));

        aux.setFont(new Font(fontName, 0, fontSize));

        return true;
    }

    private boolean setAuxiliaryUnit(HashMap<String, Object> unitOfInfo, AbstractNode node) {
        if (node == null || unitOfInfo == null || unitOfInfo.get(OldVersionParser.CLASS) == null ||
                ((String) unitOfInfo.get(OldVersionParser.CLASS)).trim().isEmpty())
            return false;

        if (((String) unitOfInfo.get(OldVersionParser.CLASS)).trim().equalsIgnoreCase("null"))
            return true;

        if (!(node instanceof Compartment || node instanceof BiologicalActivity))
            return true;

        String unitOfInfoClass = (String) unitOfInfo.get(OldVersionParser.CLASS);
        AuxiliaryUnit aux = null;

        if (node instanceof Compartment) {

            aux = new CompartmentUnit();

        } else {

            for (GlyphType type : GlyphType.values())
                if (type.toString().replace(' ', '_').equalsIgnoreCase(unitOfInfoClass)) {
                    aux = (AuxiliaryUnit) type.newGlyph();
                    break;
                }

        }

        if (aux == null || aux.getType() == null)
            return true;

        aux.setText((String) unitOfInfo.get(OldVersionParser.TEXT));
        setFont((HashMap<String, Object>) unitOfInfo.get(OldVersionParser.FONT), aux);
        setShapeStyle((HashMap<String, Object>) unitOfInfo.get(OldVersionParser.COLOR), aux);

        if (node instanceof Compartment)
            ((Compartment) node).setCompartmentUnit((CompartmentUnit) aux);
        else
            ((BiologicalActivity) node).setAuxiliaryUnit(aux);

        aux.update();

        return true;
    }

    private boolean setLabel(HashMap<String, Object> label, AbstractNode node) {
        if (label == null)
            return true;

        if (node == null)
            return false;

        node.setText((String) label.get(OldVersionParser.TEXT));

        if (node instanceof Compartment)
            node.setLabelLocation(Float.parseFloat((String) label.get(OldVersionParser.X)),
                    Float.parseFloat((String) label.get(OldVersionParser.Y)),
                    Float.parseFloat((String) label.get(OldVersionParser.WIDTH)),
                    Float.parseFloat((String) label.get(OldVersionParser.HEIGHT)));
        else
            node.setCenteredLabel(true);

        node.getLabel().update();

        return true;
    }

    private OrientationType getOrientation(String orientation) {
        if (orientation == null || orientation.trim().isEmpty())
            return null;

        if (orientation.equalsIgnoreCase("vertical"))
            return OrientationType.DOWN;

        for (OrientationType type : OrientationType.values())
            if (type.toString().equalsIgnoreCase(orientation))
                return type;

        return null;
    }

    private AbstractArc convertArc(HashMap<String, Object> arc) {
        if (arc == null)
            return null;

        AbstractArc result = null;

        for (GlyphType type : GlyphType.values())
            if (type.toString().replace(' ', '_').equalsIgnoreCase((String) arc.get(OldVersionParser.CLASS))) {
                result = (AbstractArc) type.newGlyph();
                break;
            }

        if (result == null)
            return null;

        result.setId((String) arc.get(OldVersionParser.ID));

        result.setForegroundColor(Color.decode((String) arc.get(OldVersionParser.COLOR)));

        String source = (String) ((HashMap<String, Object>) arc.get(OldVersionParser.PORTS)).get(OldVersionParser.SOURCE);
        String sourceIndex = (String) ((HashMap<String, Object>) arc.get(OldVersionParser.PORTS)).get(OldVersionParser.SOURCE_INDEX);
        String target = (String) ((HashMap<String, Object>) arc.get(OldVersionParser.PORTS)).get(OldVersionParser.TARGET);
        String targetIndex = (String) ((HashMap<String, Object>) arc.get(OldVersionParser.PORTS)).get(OldVersionParser.TARGET_INDEX);

        result.setSourcePort(getPort(source, sourceIndex, target));
        result.setTargetPort(getPort(target, targetIndex, source));

        setPoints((List<String>) arc.get(OldVersionParser.POINTS), result);

        setLayer(result);
        result.setPadding(20);
        result.setLineWidth(3);
        result.update();

        return result;
    }

    private boolean setPoints(List<String> coordinates, AbstractArc arc) {
        if (arc == null)
            return false;

        if (coordinates == null || coordinates.isEmpty())
            return true;

        int index = 1;
        for (int i = 0; i < coordinates.size(); i += 2) {
            arc.getPoints().add(index, new Point2D.Float(Float.parseFloat(coordinates.get(i)),
                    Float.parseFloat(coordinates.get(i + 1))));
            arc.incrementBounds();
            index++;
        }

        return true;
    }

    private Port getPort(String nodeId, String portIndex, String otherSideId) {
        if (nodeId == null || nodeId.trim().isEmpty() || nodeId.trim().equalsIgnoreCase("null"))
            return null;

        AbstractNode node = (AbstractNode) beaconMapIdToObjects.get(nodeId);

        if (node == null) {

            if (parser == null)
                return null;

            Set<HashMap<String, Object>> submaps = parser.getEntitiesByClass(OldVersionParser.SUBMAP);
            if (submaps == null || submaps.isEmpty())
                return null;

            String submapId = (String) submaps.iterator().next().get(OldVersionParser.ID);
            Submap submap = (Submap) beaconMapIdToObjects.get(submapId);

            AbstractNode otherSideNode = (AbstractNode) beaconMapIdToObjects.get(otherSideId);

//            if (submap.getPortCount() > 0) {
//
//                if (otherSideNode.getMinY() > submap.getMaxY())
//                    return submap.getPortAt(2);
//
//                if (otherSideNode.getMaxY() < submap.getMinY())
//                    return submap.getPortAt(0);
//
//                if (otherSideNode.getMaxX() < submap.getMinX())
//                    return submap.getPortAt(1);
//
//                return submap.getPortAt(3);
//
//            }

            if (submap.getPortCount() > 0) {

                    return submap.getPortAt(0);

            }

            return null;
        }

        int index = 0;

        if (node instanceof BiologicalActivity || node instanceof Phenotype || node instanceof Tag) {

            index = Integer.parseInt(portIndex);
            switch (index) {
                case 0:
                    index = 1;
                    break;
                case 1:
                    index = 0;
                    break;
                case 2:
                    index = 3;
                    break;
                case 3:
                    index = 2;
                    break;
            }

        } else
            index = Integer.parseInt(portIndex);

        if (index >= 0 && index < node.getPortCount())
            return node.getPortAt(index);

        return node.getPortAt(0);
    }

    public boolean setLayer(AbstractGlyph glyph) {
        if (glyph == null)
            return false;

        beaconMapIdToLayers.values().iterator().next().add(glyph);

        return true;
    }

    private boolean setBoundingBox(HashMap<String, Object> boundary, AbstractEntity entity) {
        if (boundary == null)
            return true;

        if (entity == null || entity.getBoundary() == null)
            return false;

        entity.getBoundary().setRect(Float.parseFloat((String) boundary.get(OldVersionParser.X)),
                Float.parseFloat((String) boundary.get(OldVersionParser.Y)),
                Float.parseFloat((String) boundary.get(OldVersionParser.WIDTH)),
                Float.parseFloat((String) boundary.get(OldVersionParser.HEIGHT)));

        entity.update();
        return true;
    }

    private Compartment getParentCompartment(String compartment) {
        if (compartment == null || compartment.trim().isEmpty() || compartment.trim().equalsIgnoreCase("null"))
            return null;
        return (Compartment) beaconMapIdToObjects.get(compartment);
    }

}
