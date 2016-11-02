package edu.vt.beacon.io;

import edu.vt.beacon.graph.AbstractEntity;
import edu.vt.beacon.graph.OrientationType;
import edu.vt.beacon.graph.glyph.AbstractGlyph;
import edu.vt.beacon.graph.glyph.GlyphType;
import edu.vt.beacon.graph.glyph.arc.AbstractArc;
import edu.vt.beacon.graph.glyph.node.AbstractNode;
import edu.vt.beacon.graph.glyph.node.activity.AbstractActivity;
import edu.vt.beacon.graph.glyph.node.activity.BiologicalActivity;
import edu.vt.beacon.graph.glyph.node.auxiliary.*;
import edu.vt.beacon.graph.glyph.node.auxiliary.Label;
import edu.vt.beacon.graph.glyph.node.auxiliary.Port;
import edu.vt.beacon.graph.glyph.node.container.Compartment;
import edu.vt.beacon.graph.glyph.node.submap.AbstractTagTerminal;
import edu.vt.beacon.graph.glyph.node.submap.Submap;
import edu.vt.beacon.graph.glyph.node.submap.Tag;
import edu.vt.beacon.graph.glyph.node.submap.Terminal;
import edu.vt.beacon.layer.Layer;
import edu.vt.beacon.map.Map;
import edu.vt.beacon.pathway.Pathway;
import org.sbgn.bindings.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by ppws on 2/17/16.
 */
public class Converter {

    private static HashMap<String, Object> libSbgnMapIdToObjects = new HashMap<String, Object>();
    private static HashMap<String, Object> beaconMapIdToObjects = new HashMap<String, Object>();
    private static HashMap<String, Layer> beaconMapIdToLayers = new HashMap<String, Layer>();
    private static HashMap<Submap, ArrayList<Terminal>> beaconMapSubmapToTerminals = new HashMap<Submap, ArrayList<Terminal>>();
    private static HashMap<Submap, String> beaconMapSubmapToMapId = new HashMap<Submap, String>();
    private static HashMap<Terminal, String> beaconMapTerminalToTagId = new HashMap<Terminal, String>();
    private static HashMap<Terminal, Port> beaconMapTerminalToPort = new HashMap<Terminal, Port>();
    private static HashMap<String, String> beaconMapTerminalIdToPortId = new HashMap<String, String>();

    private static final String NAMESPACE = "https://bioinformatics.cs.vt.edu";
    private static final String PREFIX = "beacon";

    private static final String PATHWAY_ELEMENT = "pathway";
    private static final String PATHWAY_NAME = "name";
    private static final String PATHWAY_ORGANISM = "organism";

    private static final String MAP_ELEMENT = "map";
    private static final String MAP_NAME = "name";

    private static final String LAYER_ELEMENT = "layer";
    private static final String LAYER_ID = "id";
    private static final String LAYER_NAME = "name";

    private static final String FONT_ELEMENT = "font";
    private static final String FONT_NAME = "name";
    private static final String FONT_SIZE = "size";
    private static final String FONT_STYLE = "style";
    private static final String FONT_COLOR = "color";
    private static final String FONT_PLAIN = "Plain";
    private static final String FONT_BOLD = "Bold";
    private static final String FONT_ITALIC = "Italic";
    private static final String FONT_BOLD_ITALIC = "Bold-italic";

    private static final String SHAPE_ELEMENT = "shape";
    private static final String SHAPE_PADDING = "padding";
    private static final String SHAPE_FILL_COLOR = "fillColor";
    private static final String SHAPE_LINE_WIDTH = "lineWidth";
    private static final String SHAPE_LINE_COLOR = "lineColor";

    private static final String PORT_TYPE_ELEMENT = "portType";
    private static final String PORT_TYPE = "type";
    private static final String PORT_LINE_INDEX = "lineIndex";
    private static final String PORT_FRACTION = "fraction";
    private static final String PORT_TERMINAL_ID = "terminalId";

    private static final String ARC_SOURCE_PORT_ELEMENT = "sourcePort";
    private static final String ARC_TARGET_PORT_ELEMENT = "targetPort";
    private static final String ARC_PORT_ID = "id";

    private static final String LABEL_STYLE_ELEMENT = "style";
    private static final String LABEL_CENTERED = "centered";

    public static Sbgn convert(Pathway pathway) {
        libSbgnMapIdToObjects.clear();
        beaconMapIdToObjects.clear();
        beaconMapIdToLayers.clear();
        beaconMapSubmapToMapId.clear();
        beaconMapSubmapToTerminals.clear();
        beaconMapTerminalToTagId.clear();
        beaconMapTerminalToPort.clear();
        beaconMapTerminalIdToPortId.clear();

        Sbgn sbgn = new Sbgn();

        try {
            SBGNBase.Extension extension = new SBGNBase.Extension();

            Element pathwayElement = createElement(PATHWAY_ELEMENT);
            pathwayElement.setAttribute(PATHWAY_NAME, pathway.getName());
            pathwayElement.setAttribute(PATHWAY_ORGANISM, pathway.getOrganism());

            extension.getAny().add(pathwayElement);
            sbgn.setExtension(extension);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        sbgn.getMap().add(convert(pathway.getMap(), sbgn));

        return sbgn;
    }

    private static org.sbgn.bindings.Map convert(Map map, Sbgn sbgn) {
        if (map == null) {
            map = new Map("");
        }

        org.sbgn.bindings.Map sbgnMap = new org.sbgn.bindings.Map();
        sbgnMap.setId(map.getId());
        sbgnMap.setLanguage("activity flow");

        if (createLayers(map.getLayers(), sbgnMap)) {

            try {

                Element mapNameElement = createElement(MAP_ELEMENT);
                mapNameElement.setAttribute(MAP_NAME, map.getName());
                sbgnMap.getExtension().getAny().add(mapNameElement);

            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }

            //First we process the compartments
            for (Layer layer : map.getLayers())
                for (AbstractGlyph glyph : layer.getGlyphs())
                    if (glyph instanceof Compartment) {
                        Glyph sbgnGlyph = convert((AbstractNode) glyph);
                        libSbgnMapIdToObjects.put(sbgnGlyph.getId(), sbgnGlyph);
                        sbgnMap.getGlyph().add(sbgnGlyph);
                    }

            //Second we process the submaps 
            for (Layer layer : map.getLayers())
                for (AbstractGlyph glyph : layer.getGlyphs())
                    if (glyph instanceof Submap) {

                        //First the map associated with the submap
                        org.sbgn.bindings.Map sbgnSubmapMap = convert(((Submap) glyph).getMap(), sbgn);
                        libSbgnMapIdToObjects.put(sbgnSubmapMap.getId(), sbgnSubmapMap);
                        sbgn.getMap().add(sbgnSubmapMap);

                        //Second the submap glyph
                        Glyph sbgnSubmapGlyph = convert((AbstractNode) glyph);
                        sbgnSubmapGlyph.setMapRef(sbgnSubmapMap);
                        libSbgnMapIdToObjects.put(sbgnSubmapGlyph.getId(), sbgnSubmapGlyph);
                        sbgnMap.getGlyph().add(sbgnSubmapGlyph);

                    }

            //Third we process the activity nodes and logical operators
            for (Layer layer : map.getLayers())
                for (AbstractGlyph glyph : layer.getGlyphs())
                    if (glyph instanceof AbstractNode && !(glyph instanceof Compartment || glyph instanceof Submap)) {
                        Glyph sbgnGlyph = convert((AbstractNode) glyph);
                        libSbgnMapIdToObjects.put(sbgnGlyph.getId(), sbgnGlyph);
                        sbgnMap.getGlyph().add(sbgnGlyph);
                    }


            //Fourth process all arcs 
            for (Layer layer : map.getLayers())
                for (AbstractGlyph glyph : layer.getGlyphs())
                    if (glyph instanceof AbstractArc) {
                        Arc sbgnArc = convert((AbstractArc) glyph);
                        libSbgnMapIdToObjects.put(sbgnArc.getId(), sbgnArc);
                        sbgnMap.getArc().add(sbgnArc);
                    }
        }
        return sbgnMap;
    }

    private static boolean createLayers(ArrayList<Layer> layers, org.sbgn.bindings.Map sbgnMap) {
        if (layers == null || layers.isEmpty() || sbgnMap == null)
            return false;

        SBGNBase.Extension sbgnMapExtension = new SBGNBase.Extension();

        try {

            for (Layer layer : layers) {
                Element layerElement = createElement(LAYER_ELEMENT);
                layerElement.setAttribute(LAYER_ID, layer.getId());
                layerElement.setAttribute(LAYER_NAME, layer.getName());
                sbgnMapExtension.getAny().add(layerElement);

            }

            sbgnMap.setExtension(sbgnMapExtension);

            return true;

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static Glyph convert(AbstractNode node) {
        if (node == null)
            return null;

        Glyph glyph = new Glyph();


        glyph.setId(node.getId());

        if (node.getType() != null)
            glyph.setClazz(node.getType().toString());

        glyph.setBbox(getBoundingBox(node));
        glyph.setCallout(getAnnotation(node));

        if (node instanceof AbstractNode && node.getOrientation() != null)
            glyph.setOrientation(node.getOrientation().toString());

        setAuxiliaryUnit(node, glyph);
        glyph.getPort().addAll(getPorts(node.getPorts(), node));

        glyph.setCompartmentRef(getCompartmentRef(node.getParentCompartment()));

        if (node instanceof Submap) {
            glyph.setMapRef(libSbgnMapIdToObjects.get(node.getId()));
            setTerminals((Submap) node, glyph);
        }

        if (node instanceof Terminal)
            glyph.setTagRef(libSbgnMapIdToObjects.get(((Terminal) node).getParent().getTerminalToTagMapping().get(node).getId()));

        if (node instanceof Compartment)
            glyph.setCompartmentOrder(((float) ((Compartment) node).getRenderingOrder()));

        glyph.setLabel(getLabel(node.getLabel(), node));

        glyph.setExtension(getExtension(node));
        return glyph;
    }

    private static void setTerminals(Submap submap, Glyph glyph) {

        Set<Terminal> terminals = submap.getTerminals();

        if (terminals != null && !terminals.isEmpty()) {
            for (Terminal t : terminals) {

                Glyph sbgnTerminal = convert(t);
                glyph.getGlyph().add(sbgnTerminal);
                libSbgnMapIdToObjects.put(sbgnTerminal.getId(), sbgnTerminal);

            }
        }

    }

    private static Glyph getCompartmentRef(Compartment parentCompartment) {
        if (parentCompartment == null)
            return null;

        return (Glyph) libSbgnMapIdToObjects.get(parentCompartment.getId());
    }

    private static Compartment getParentCompartment(Glyph compartment) {
        if (compartment == null || compartment.getId() == null || compartment.getId().trim().isEmpty())
            return null;

        return (Compartment) beaconMapIdToObjects.get(compartment.getId());
    }

    private static Arc convert(AbstractArc arc) {
        if (arc == null)
            return null;

        Arc result = new Arc();

        result.setId(arc.getId());

        if (arc.getSource() != null)
            result.setSource(libSbgnMapIdToObjects.get(arc.getSource().getId()));

        if (arc.getTarget() != null)
            result.setTarget(libSbgnMapIdToObjects.get(arc.getTarget().getId()));

        if (arc.getType() != null)
            result.setClazz(arc.getType().toString());

        result.setStart(getStartPoint(arc));
        result.setEnd(getEndPoint(arc));
        result.getNext().addAll(getPoints(arc.getPoints()));

        result.setExtension(getExtension(arc));
        setArcPortIds(result.getExtension(), arc);

        return result;
    }

    private static boolean setArcPortIds(SBGNBase.Extension extension, AbstractArc arc) {
        if (extension == null || arc == null)
            return false;

        try {

            Element sourcePort = createElement(ARC_SOURCE_PORT_ELEMENT);
            Element targetPort = createElement(ARC_TARGET_PORT_ELEMENT);

            if (arc.getSourcePort() != null)
                sourcePort.setAttribute(ARC_PORT_ID, arc.getSourcePort().getId());
            if (arc.getTargetPort() != null)
                targetPort.setAttribute(ARC_PORT_ID, arc.getTargetPort().getId());

            extension.getAny().add(sourcePort);
            extension.getAny().add(targetPort);

            return true;

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static ArrayList<Arc.Next> getPoints(ArrayList<Point2D.Float> points) {
        if (points == null || points.size() <= 2)
            return new ArrayList<Arc.Next>();

        ArrayList<Arc.Next> results = new ArrayList<Arc.Next>(points.size());

        for (int i = 1; i < (points.size() - 1); i++) {
            Point2D.Float p = points.get(i);
            Arc.Next next = new Arc.Next();
            next.setX(p.x);
            next.setY(p.y);
            results.add(next);
        }

        return results;
    }

    private static Arc.End getEndPoint(AbstractArc arc) {
        if (arc == null )
            return null;

        float x = 0;
        float y = 0;
        if (arc.getTargetPort() == null){
            int end_index= arc.getPointCount()-1;
            x= arc.getPointAt(end_index).x;
            y= arc.getPointAt(end_index).y;
        }
        else{
            x= arc.getTargetPort().getCenterX();
            y= arc.getTargetPort().getCenterY();
        }

        Arc.End end = new Arc.End();

        end.setX(x);
        end.setY(y);

        return end;
    }

    private static Arc.Start getStartPoint(AbstractArc arc) {
        if (arc == null )
            return null;

        float x= 0;
        float y = 0;
        if (arc.getSourcePort() == null){
            x= arc.getPointAt(0).x;
            y= arc.getPointAt(0).y;
        }
        else{
            x= arc.getSourcePort().getCenterX();
            y= arc.getSourcePort().getCenterY();
        }
        Arc.Start start = new Arc.Start();

        start.setX(x);
        start.setY(y);

        return start;
    }

    private static org.sbgn.bindings.Label getLabel(Label label, AbstractNode node) {
        if (label == null || label.getText() == null || label.getText().isEmpty())
            return null;

        org.sbgn.bindings.Label result = new org.sbgn.bindings.Label();

        result.setBbox(getBoundingBox(label));
        result.setText(label.getText());

        if (node != null) {
            try {

                Element style = createElement(LABEL_STYLE_ELEMENT);
                SBGNBase.Extension extension = new SBGNBase.Extension();
                style.setAttribute(LABEL_CENTERED, node.isCenteredLabel() + "");
                extension.getAny().add(style);
                result.setExtension(extension);

            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    private static ArrayList<org.sbgn.bindings.Port> getPorts(ArrayList<Port> ports, AbstractNode node) {
        if (ports == null || ports.size() == 0)
            return new ArrayList<org.sbgn.bindings.Port>();

        ArrayList<org.sbgn.bindings.Port> results = new ArrayList<org.sbgn.bindings.Port>(ports.size());
        for (Port p : ports) {
            org.sbgn.bindings.Port r = new org.sbgn.bindings.Port();
            r.setId(p.getId());
            r.setX(p.getCenterX());
            r.setY(p.getCenterY());
            r.setExtension(getPortType(p.getType(), p, node));
            results.add(r);
        }

        return results;
    }

    private static SBGNBase.Extension getPortType(PortType type, Port port, AbstractNode node) {
        SBGNBase.Extension extension = new SBGNBase.Extension();

        Element portType = null;

        try {

            portType = createElement(PORT_TYPE_ELEMENT);
            portType.setAttribute(PORT_TYPE, "" + type);

            if (type == PortType.ARBITRARY && node != null && node instanceof AbstractActivity) {

                AbstractActivity activity = (AbstractActivity) node;
                int portIndex = activity.getPorts().indexOf(port);
                portType.setAttribute(PORT_LINE_INDEX, "" +
                        activity.getCorrespondingLinesForArbitraryPorts().get(portIndex - 4));

                portType.setAttribute(PORT_FRACTION, "" +
                        activity.getCorrespondingFractionsForArbitraryPorts().get(portIndex - 4));

            }

            if (node instanceof Submap) {

                for (java.util.Map.Entry<Terminal, Port> entry : ((Submap) node).getTerminalToPortMapping().entrySet())
                    if (entry.getValue() == port) {
                        portType.setAttribute(PORT_TERMINAL_ID, entry.getKey().getId());
                        break;
                    }

            }


        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        if (portType == null)
            return null;

        extension.getAny().add(portType);

        return extension;
    }

    private static PortType getPortType(SBGNBase.Extension extension) {
        if (extension == null || extension.getAny().isEmpty() || extension.getAny().get(0).getAttribute(PORT_TYPE) == null)
            return null;

        String type = extension.getAny().get(0).getAttribute(PORT_TYPE);

        for (PortType p : PortType.values())
            if (p.toString().equalsIgnoreCase(type))
                return p;

        return null;
    }

    private static String getTerminalId(SBGNBase.Extension extension) {
        if (extension == null || extension.getAny().isEmpty() || extension.getAny().get(0).getAttribute(PORT_TYPE) == null)
            return null;

        return extension.getAny().get(0).getAttribute(PORT_TERMINAL_ID);
    }

    private static boolean setAuxiliaryUnit(AbstractNode node, Glyph glyph) {
        if (node == null || glyph == null)
            return false;

        if (!(node instanceof Compartment || node instanceof BiologicalActivity))
            return true;

        AuxiliaryUnit aux = null;

        if (node instanceof Compartment)
            aux = ((Compartment) node).getCompartmentUnit();
        else if (node instanceof BiologicalActivity)
            aux = ((BiologicalActivity) node).getAuxiliaryUnit();

        if (aux == null || aux.getType() == null)
            return true;

        Glyph unitOfInformation = new Glyph();
        unitOfInformation.setId(aux.getId());
        unitOfInformation.setClazz("unit of information");
        unitOfInformation.setLabel(getLabel(aux.getLabel(), node));

        if (node instanceof BiologicalActivity) {
            Glyph.Entity entity = new Glyph.Entity();
            entity.setName(aux.getType().toString());
            unitOfInformation.setEntity(entity);
        }

        unitOfInformation.setBbox(getBoundingBox(aux));

        glyph.getGlyph().add(unitOfInformation);

        return true;
    }

    private static Bbox getBoundingBox(AbstractEntity entity) {
        Bbox bbox = new Bbox();

        bbox.setX(entity.getMinX());
        bbox.setY(entity.getMinY());
        bbox.setW(entity.getWidth());
        bbox.setH(entity.getHeight());

        return bbox;
    }

    private static boolean setBoundingBox(Bbox bbox, AbstractEntity entity) {
        if (bbox == null)
            return true;

        if (entity == null || entity.getBoundary() == null)
            return false;

        entity.getBoundary().setRect(bbox.getX(), bbox.getY(), bbox.getW(), bbox.getH());
        entity.update();

        return true;
    }

    private static SBGNBase.Extension getExtension(AbstractGlyph glyph) {
        if (glyph == null)
            return null;

        SBGNBase.Extension extension = new SBGNBase.Extension();

        Element layerInfo = null;
        Element shapeInfo = null;
        Element fontInfo = null;

        try {

            layerInfo = createElement(LAYER_ELEMENT);
            layerInfo.setAttribute(LAYER_ID, glyph.getLayer().getId());

            shapeInfo = createElement(SHAPE_ELEMENT);
            shapeInfo.setAttribute(SHAPE_PADDING, "" + glyph.getPadding());
            shapeInfo.setAttribute(SHAPE_FILL_COLOR, "" + glyph.getBackgroundColor().getRGB());
            shapeInfo.setAttribute(SHAPE_LINE_WIDTH, "" + glyph.getLineWidth());
            shapeInfo.setAttribute(SHAPE_LINE_COLOR, "" + glyph.getForegroundColor().getRGB());

            if (glyph instanceof AbstractNode) {
                Font font = ((AbstractNode) glyph).getFont();

                if (font != null) {
                    fontInfo = createElement(FONT_ELEMENT);
                    fontInfo.setAttribute(FONT_NAME, font.getName());
                    fontInfo.setAttribute(FONT_SIZE, "" + font.getSize());

                    if (((AbstractNode) glyph).getFontColor() != null)
                        fontInfo.setAttribute(FONT_COLOR, ((AbstractNode) glyph).getFontColor().getRGB() + "");

                    String style = null;
                    if (font.getStyle() == 0)
                        style = FONT_PLAIN;
                    else if (font.getStyle() == 1)
                        style = FONT_BOLD;
                    else if (font.getStyle() == 2)
                        style = FONT_ITALIC;
                    else if (font.getStyle() == 3)
                        style = FONT_BOLD_ITALIC;

                    fontInfo.setAttribute(FONT_STYLE, style);
                }
            }

            if (layerInfo != null)
                extension.getAny().add(layerInfo);

            if (shapeInfo != null)
                extension.getAny().add(shapeInfo);

            if (fontInfo != null)
                extension.getAny().add(fontInfo);

            return extension;

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Element createElement(String elementName) throws ParserConfigurationException {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();

        Element elt = doc.createElementNS(NAMESPACE, elementName);
        elt.setPrefix(PREFIX);

        return elt;
    }

    private static Glyph.Callout getAnnotation(AbstractNode node) {
        return null;
    }

    private static boolean setAnnotation(Glyph glyph, AbstractNode node) {
        return false;
    }

    public static Pathway convert(Sbgn sbgn) {
        libSbgnMapIdToObjects.clear();
        beaconMapIdToObjects.clear();
        beaconMapIdToLayers.clear();
        beaconMapSubmapToMapId.clear();
        beaconMapSubmapToTerminals.clear();
        beaconMapTerminalToTagId.clear();
        beaconMapTerminalToPort.clear();
        beaconMapTerminalIdToPortId.clear();

        Pathway pathway = new Pathway("");

        setPathwayInfo(sbgn, pathway);

        for (org.sbgn.bindings.Map sbgnMap : sbgn.getMap()) {

            Map beaconMap = new Map("");
            setLayers(sbgnMap, beaconMap);
            convert(sbgnMap, beaconMap);
            beaconMapIdToObjects.put(beaconMap.getId(), beaconMap);

        }

        setupSubmapsDependencies();

        pathway.setMap(findMainMap(sbgn.getMap()));

        return pathway;
    }

    private static Map findMainMap(List<org.sbgn.bindings.Map> sbgnMaps) {

        for (org.sbgn.bindings.Map map : sbgnMaps)
            if (!beaconMapSubmapToMapId.values().contains(map.getId()))
                return (Map) beaconMapIdToObjects.get(map.getId());

        return null;

    }

    private static void setupSubmapsDependencies() {

        for (Submap submap : beaconMapSubmapToMapId.keySet()) {

            submap.setMap((Map) beaconMapIdToObjects.get(beaconMapSubmapToMapId.get(submap)));

            for (Terminal terminal : beaconMapSubmapToTerminals.get(submap)) {

                Tag tag = (Tag) beaconMapIdToObjects.get(beaconMapTerminalToTagId.get(terminal));
                tag.setParent(submap);
                submap.getTags().add(tag);
                submap.getTagToTerminalMapping().put(tag, terminal);
                submap.getTerminalToTagMapping().put(terminal, tag);
                submap.getTerminalToPortMapping().put(terminal, beaconMapTerminalToPort.get(terminal));
                tag.update();

            }

            submap.update();
        }

    }

    private static boolean setPathwayInfo(Sbgn sbgn, Pathway pathway) {
        if (sbgn == null || pathway == null)
            return false;

        SBGNBase.Extension extension = sbgn.getExtension();

        if (extension == null)
            return true;

        for (Element element : extension.getAny())
            if (element.getTagName().equalsIgnoreCase(getTagName(PATHWAY_ELEMENT))) {
                pathway.setName(element.getAttribute(PATHWAY_NAME));
                pathway.setOrganism_(element.getAttribute(PATHWAY_ORGANISM));
                return true;
            }

        return true;
    }

    private static boolean setLayers(org.sbgn.bindings.Map sbgnMap, Map map) {
        if (sbgnMap == null)
            return true;

        map.getLayers().clear();

        SBGNBase.Extension extensions = sbgnMap.getExtension();
        if (extensions == null || extensions.getAny().isEmpty()) {
            Layer layer = new Layer("New Layer", map);
            map.add(layer);
            beaconMapIdToLayers.put(layer.getId(), layer);
            return true;
        }

        boolean hasLayers = false;
        for (Element element : extensions.getAny()) {

            if (element.getTagName() != null && element.getTagName().equalsIgnoreCase(getTagName(LAYER_ELEMENT))) {

                Layer layer = new Layer(element.getAttribute(LAYER_NAME), map);
                layer.setId(element.getAttribute(LAYER_ID));
                map.add(layer);
                beaconMapIdToLayers.put(layer.getId(), layer);

                hasLayers = true;
            }
        }

        if (!hasLayers) {
            Layer layer = new Layer("New Layer", map);
            map.add(layer);
            beaconMapIdToLayers.put(layer.getId(), layer);
        }
        map.getLayers().get(0).setSelected(true);

        return true;
    }

    private static Map convert(org.sbgn.bindings.Map sbgnMap, Map map) {
        if (sbgnMap == null) {
            return null;
        }

        map.setId(sbgnMap.getId());
        setName(sbgnMap, map);

        for (Glyph glyph : sbgnMap.getGlyph()) {
            AbstractNode node = convert(glyph);
            beaconMapIdToObjects.put(node.getId(), node);
        }

        for (Arc arc : sbgnMap.getArc()) {
            AbstractArc beaconArc = convert(arc);
            beaconMapIdToObjects.put(beaconArc.getId(), beaconArc);
        }

        return map;
    }

    private static boolean setName(org.sbgn.bindings.Map sbgnMap, Map map) {
        if (sbgnMap == null || map == null)
            return false;

        SBGNBase.Extension extension = sbgnMap.getExtension();

        if (extension == null)
            return true;

        for (Element element : extension.getAny())
            if (element.getTagName().equalsIgnoreCase(getTagName(MAP_ELEMENT))) {
                map.setName(element.getAttribute(MAP_NAME));
                return true;
            }

        return true;
    }

    private static AbstractNode convert(Glyph glyph) {
        if (glyph == null)
            return null;

        AbstractNode node = null;

        for (GlyphType type : GlyphType.values())
            if (type.toString().equalsIgnoreCase(glyph.getClazz())) {
                node = (AbstractNode) type.newGlyph();
                break;
            }

        node.setId(glyph.getId());

        setBoundingBox(glyph.getBbox(), node);
        setAnnotation(glyph, node);

        setFont(glyph, node);
        setShapeStyle(glyph, node);
        node.setOrientation(getOrientation(glyph.getOrientation()));

        setAuxiliaryUnit(glyph, node);
        setPorts(glyph.getPort(), node);

        node.setParentCompartment(getParentCompartment((Glyph) glyph.getCompartmentRef()));
        setLabel(glyph.getLabel(), node);

        if (glyph.getClazz().equalsIgnoreCase(GlyphType.COMPARTMENT.toString()) && glyph.getCompartmentOrder() != null)
            ((Compartment) node).setRenderingOrder(glyph.getCompartmentOrder().intValue());

        setLayer(glyph, node);
        node.update();

        if (glyph.getClazz().equalsIgnoreCase(GlyphType.SUBMAP.toString()))
            processSubmap(glyph, (Submap) node);


        if (glyph.getClazz().equalsIgnoreCase(GlyphType.TERMINAL.toString()) && glyph.getTagRef() != null)
            beaconMapTerminalToTagId.put((Terminal) node, ((Glyph) glyph.getTagRef()).getId());

        return node;
    }

    private static void processSubmap(Glyph sbgnSubmap, Submap submap) {

        if (sbgnSubmap.getMapRef() != null)
            beaconMapSubmapToMapId.put(submap, ((org.sbgn.bindings.Map) sbgnSubmap.getMapRef()).getId());

        beaconMapSubmapToTerminals.put(submap, new ArrayList<Terminal>());

        if (sbgnSubmap.getGlyph() != null)
            for (Glyph sbgnTerminal : sbgnSubmap.getGlyph()) {

                Terminal terminal = (Terminal) convert(sbgnTerminal);
                terminal.setParent(submap);
                submap.getTerminals().add(terminal);
                beaconMapSubmapToTerminals.get(submap).add(terminal);
                beaconMapTerminalToPort.put(terminal, (Port) beaconMapIdToObjects.get(beaconMapTerminalIdToPortId.get(terminal.getId())));
                terminal.update();

            }

    }

    private static boolean setShapeStyle(SBGNBase base, AbstractGlyph node) {
        if (base == null || node == null)
            return false;

        Element shapeElement = getElementByTagName(base.getExtension(), SHAPE_ELEMENT);
        if (shapeElement == null)
            return true;

        node.setPadding(Float.parseFloat(shapeElement.getAttribute(SHAPE_PADDING)));
        node.setLineWidth(Float.parseFloat(shapeElement.getAttribute(SHAPE_LINE_WIDTH)));
        node.setBackgroundColor(Color.decode(shapeElement.getAttribute(SHAPE_FILL_COLOR)));
        node.setForegroundColor(Color.decode(shapeElement.getAttribute(SHAPE_LINE_COLOR)));

        return true;
    }

    private static boolean setFont(Glyph glyph, AbstractNode node) {
        if (glyph == null || node == null)
            return false;

        Element fontElement = getElementByTagName(glyph.getExtension(), FONT_ELEMENT);
        if (fontElement == null)
            return true;

        String fontName = fontElement.getAttribute(FONT_NAME);
        int fontSize = Integer.parseInt(fontElement.getAttribute(FONT_SIZE));

        int styleCode = 0;
        String style = fontElement.getAttribute(FONT_STYLE);
        if (style.equalsIgnoreCase(FONT_PLAIN))
            styleCode = 0;
        else if (style.equalsIgnoreCase(FONT_BOLD))
            styleCode = 1;
        else if (style.equalsIgnoreCase(FONT_ITALIC))
            styleCode = 2;
        else if (style.equalsIgnoreCase(FONT_BOLD_ITALIC))
            styleCode = 3;

        node.setFont(new Font(fontName, styleCode, fontSize));
        node.setFontColor(Color.decode(fontElement.getAttribute(FONT_COLOR)));

        return true;
    }

    private static boolean setAuxiliaryUnit(Glyph glyph, AbstractNode node) {
        if (node == null || glyph == null || glyph.getClazz() == null || glyph.getClazz().isEmpty())
            return false;

        if (!(node instanceof Compartment || node instanceof BiologicalActivity))
            return true;

        if (!glyph.getClazz().equalsIgnoreCase(GlyphType.BIOLOGICAL_ACTIVITY.toString()) &&
                !glyph.getClazz().equalsIgnoreCase(GlyphType.COMPARTMENT.toString()))
            return true;

        Glyph sbgnAux = null;

        for (Glyph childGlypgh : glyph.getGlyph())
            if (childGlypgh.getClazz().equalsIgnoreCase("unit of information")) {
                sbgnAux = childGlypgh;
                break;
            }

        if (sbgnAux == null)
            return true;


        AuxiliaryUnit aux = null;

        if (glyph.getClazz().equalsIgnoreCase(GlyphType.COMPARTMENT.toString())) {

            aux = new CompartmentUnit();

        } else {

            if (sbgnAux.getEntity() == null || sbgnAux.getEntity().getName() == null
                    || sbgnAux.getEntity().getName().isEmpty())
                return false;

            for (GlyphType type : GlyphType.values())
                if (type.toString().equalsIgnoreCase(sbgnAux.getEntity().getName())) {
                    aux = (AuxiliaryUnit) type.newGlyph();
                    break;
                }
        }

        if (aux == null || aux.getType() == null)
            return true;

        aux.setId(sbgnAux.getId());
        setLabel(sbgnAux.getLabel(), aux);
        setFont(glyph, node);
        setShapeStyle(glyph, node);

        setBoundingBox(sbgnAux.getBbox(), aux);
        aux.update();

        if (node instanceof Compartment)
            ((Compartment) node).setCompartmentUnit((CompartmentUnit) aux);

        else //if (node instanceof BiologicalActivity)
            ((BiologicalActivity) node).setAuxiliaryUnit(aux);

        return true;
    }

    private static boolean setLabel(org.sbgn.bindings.Label label, AuxiliaryUnit auxiliaryUnit) {
        if (label == null)
            return true;

        if (auxiliaryUnit == null)
            return false;

        auxiliaryUnit.setText(label.getText());
        setBoundingBox(label.getBbox(), auxiliaryUnit);

        return true;
    }

    private static boolean setLabel(org.sbgn.bindings.Label label, AbstractNode node) {
        if (label == null)
            return true;

        if (node == null)
            return false;

        node.setText(label.getText());

        Element styleElement = getElementByTagName(label.getExtension(), LABEL_STYLE_ELEMENT);

        if (styleElement == null || (styleElement != null &&
                !Boolean.parseBoolean(styleElement.getAttribute(LABEL_CENTERED))))
            node.setLabelLocation(label.getBbox().getX(), label.getBbox().getY(),
                    label.getBbox().getW(), label.getBbox().getH());

        node.getLabel().update();

        return true;
    }

    private static OrientationType getOrientation(String orientation) {
        if (orientation == null || orientation.trim().isEmpty())
            return null;

        for (OrientationType type : OrientationType.values())
            if (type.toString().equalsIgnoreCase(orientation))
                return type;

        return null;
    }

    private static AbstractArc convert(Arc arc) {
        if (arc == null)
            return null;

        AbstractArc result = null;

        for (GlyphType type : GlyphType.values())
            if (type.toString().equalsIgnoreCase(arc.getClazz())) {
                result = (AbstractArc) type.newGlyph();
                break;
            }

        if (result == null)
            return null;

        setShapeStyle(arc, result);
        result.setId(arc.getId());

        result.setSourcePort(getPort(arc, ARC_SOURCE_PORT_ELEMENT));
        result.setTargetPort(getPort(arc, ARC_TARGET_PORT_ELEMENT));
        setPoints(arc, result);

        setLayer(arc, result);
        result.update();

        return result;
    }

    private static boolean setPoints(Arc sbgn_arc, AbstractArc arc) {

        if (arc == null)
            return false;
        List<Arc.Next> nexts = sbgn_arc.getNext();
        float x, y;

            x = sbgn_arc.getStart().getX();
            y = sbgn_arc.getStart().getY();
            arc.getPoints().set(0,  new Point2D.Float(x,y));
            x = sbgn_arc.getEnd().getX();
            y = sbgn_arc.getEnd().getY();
            arc.getPoints().set(1, new Point2D.Float(x,y));



        int index = 1;
        for (Arc.Next next : nexts) {
            x = next.getX();
            y = next.getY();
            arc.getPoints().add(index, new Point2D.Float(x,y));
            arc.incrementBounds();
            index++;
        }

        return true;
    }

    private static Port getPort(Arc arc, String portTagName) {
        String portId;
        Element portElement = getElementByTagName(arc.getExtension(), portTagName);
        if (portElement != null)
            portId = portElement.getAttribute(ARC_PORT_ID);
        else
            return null;

        return (Port) beaconMapIdToObjects.get(portId);
    }

    private static Element getElementByTagName(SBGNBase.Extension extension, String tagName) {
        if (extension == null || tagName == null || tagName.isEmpty())
            return null;

        for (Element element : extension.getAny())
            if (element.getTagName().equalsIgnoreCase(getTagName(tagName)))
                return element;

        return null;
    }

    private static boolean setPorts(java.util.List<org.sbgn.bindings.Port> ports, AbstractNode node) {
        if (node == null)
            return false;

        if (ports == null || ports.size() == 0)
            return true;

        node.getPorts().clear();

        for (org.sbgn.bindings.Port p : ports) {

            if (node instanceof AbstractActivity) {

                PortType type = getPortType(p.getExtension());
                Port r = null;

                if (type == PortType.ARBITRARY) {

                    r = new Port(node, type);
                    r.setCenter(p.getX(), p.getY());

                    ((AbstractActivity) node).getCorrespondingLinesForArbitraryPorts().
                            add(Integer.parseInt(p.getExtension().getAny().get(0).getAttribute(PORT_LINE_INDEX)));

                    ((AbstractActivity) node).getCorrespondingFractionsForArbitraryPorts().
                            add(Float.parseFloat(p.getExtension().getAny().get(0).getAttribute(PORT_FRACTION)));

                    node.getPorts().add(r);

                } else {
                    r = new Port(node, type);
                    node.getPorts().add(r);
                }

                r.setId(p.getId());
                beaconMapIdToObjects.put(r.getId(), r);

            } else {

                PortType type = getPortType(p.getExtension());
                Port r = new Port(node, type);
                r.setId(p.getId());
                node.getPorts().add(r);
                beaconMapIdToObjects.put(r.getId(), r);

                if (node instanceof Submap) {

                    String terminalId = getTerminalId(p.getExtension());
                    if (terminalId != null)
                        beaconMapTerminalIdToPortId.put(terminalId, r.getId());

                }

                r.setCenter(p.getX(), p.getY());

            }

        }

        return true;
    }

    public static boolean setLayer(SBGNBase sbgnBase, AbstractGlyph glyph) {
        if (sbgnBase == null)
            return false;

        Element layerInfo = getElementByTagName(sbgnBase.getExtension(), LAYER_ELEMENT);

        if (layerInfo != null)
            beaconMapIdToLayers.get(layerInfo.getAttribute(LAYER_ID)).add(glyph);

        else if (beaconMapIdToLayers.size() == 1)
            beaconMapIdToLayers.values().iterator().next().add(glyph);

        else
            return false;

        return true;
    }

    private static String getTagName(String localName) {
        return PREFIX + ":" + localName;
    }
}
