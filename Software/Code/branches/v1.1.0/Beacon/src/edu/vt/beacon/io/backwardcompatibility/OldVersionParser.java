package edu.vt.beacon.io.backwardcompatibility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by ppws on 3/29/16.
 */
public class OldVersionParser {

    public final static String ENTITY = "entity";
    public final static String ID = "id";
    public final static String CLASS = "class";
    public final static String PARENT_COMPARTMENT = "compartment";
    public final static String BOUNDARY = "boundary";
    public final static String X = "x";
    public final static String Y = "y";
    public final static String WIDTH = "width";
    public final static String HEIGHT = "height";
    public final static String LABEL = "label";
    public final static String TEXT = "text";
    public final static String FONT = "font";
    public final static String NAME = "name";
    public final static String SIZE = "size";
    public final static String COLOR = "color";
    public final static String BACKGROUND_COLOR = "bkg";
    public final static String FOREGROUND_COLOR = "frg";
    public final static String LABEL_COLOR = "lbl";
    public final static String UNIT_OF_INFO = "unit";
    public final static String PORTS = "ports";
    public final static String SOURCE = "source";
    public final static String TARGET = "target";
    public final static String INDEX = "index";
    public final static String SOURCE_INDEX = "source index";
    public final static String TARGET_INDEX = "target index";
    public final static String POINTS = "points";
    public final static String ORIENTATION = "orientation";

    public final static String AND = "and";
    public final static String ANNOTATION = "annotation";
    public final static String BIOLOGICAL_ACTIVITY = "biological_activity";
    public final static String COMPARTMENT = "compartment";
    public final static String COMPARTMENT_UNIT = "";
    public final static String COMPLEX_UNIT = "complex";
    public final static String DELAY = "delay";
    public final static String EQUIVALENCE_ARC = "equivalence_arc";
    public final static String LOGIC_ARC = "logic_arc";
    public final static String MACROMOLECULE_UNIT = "macromolecule";
    public final static String NECESSARY_STIMULATION = "necessary_stimulation";
    public final static String NEGATIVE_INFLUENCE = "negative_influence";
    public final static String NUCLEIC_ACID_FEATURE_UNIT = "nucleic_acid_feature";
    public final static String NOT = "not";
    public final static String OR = "or";
    public final static String PERTURBATION_UNIT = "perturbation";
    public final static String PHENOTYPE = "phenotype";
    public final static String POSITIVE_INFLUENCE = "positive_influence";
    public final static String SIMPLE_CHEMICAL_UNIT = "simple_chemical";
    public final static String SUBMAP = "submap";
    public final static String TAG = "tag";
    public final static String UNKNOWN_INFLUENCE = "unknown_influence";
    public final static String UNSPECIFIED_ENTITY_UNIT = "unspecified_entity";

    public final static String SUBMAPS_DIR = "submaps/";


    private Set<HashMap<String, Object>> loadedEntities;
    private HashMap<String, HashMap<String, Object>> idToEntityMapping;
    private HashMap<String, Set<HashMap<String, Object>>> classToEntityMapping;

    public String filename;

    public OldVersionParser(String filename) {
        loadedEntities = new HashSet<HashMap<String, Object>>();
        idToEntityMapping = new HashMap<String, HashMap<String, Object>>();
        classToEntityMapping = new HashMap<String, Set<HashMap<String, Object>>>();

        this.filename = filename;
        parse();
    }

    public HashMap<String, Object> getEntity(String id) {
        return idToEntityMapping.get(id);
    }

    public Set<HashMap<String, Object>> getEntitiesByClass(String clas) {
        return classToEntityMapping.get(clas);
    }

    public Set<HashMap<String, Object>> getAllEntities() {
        return loadedEntities;
    }

    private boolean parse() {

        BufferedReader br = null;

        try {

            br = new BufferedReader(new FileReader(new File(filename)));
            while (readEntity(br)) ;
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


        return true;
    }

    private boolean readEntity(BufferedReader reader) throws IOException {
        String line;
        HashMap<String, Object> entity = null;

        while ((line = reader.readLine()) != null && !(line = line.trim()).equals("</entity>")) {

            if (startWith(line, ENTITY, "<", " ")) {

                entity = createEntity(line);
                loadedEntities.add(entity);
                idToEntityMapping.put((String) entity.get(ID), entity);

                if (!classToEntityMapping.containsKey(entity.get(CLASS)))
                    classToEntityMapping.put((String) entity.get(CLASS), new HashSet<HashMap<String, Object>>());

                classToEntityMapping.get(entity.get(CLASS)).add(entity);

            } else if (startWith(line, COMPARTMENT, "<", "=")) {

                line = line.trim();
                extract(COMPARTMENT, line.substring(1, line.length()), entity);

            } else if (startWith(line, BOUNDARY, "<", " ")) {

                entity.put(BOUNDARY, extractBoundary(line));

            } else if (startWith(line, LABEL, "<", " ")) {

                entity.put(LABEL, extractLabel(line));

            } else if (startWith(line, FONT, "<", " ")) {

                entity.put(FONT, extractFont(line));

            } else if (startWith(line, COLOR, "<", " ")) { // for compartments, biological activities, and logical operators

                entity.put(COLOR, extractColor(line));

            } else if (startWith(line, COLOR, "<", "=")) { // for arcs

                extractColorArcs(line, entity);

            } else if (startWith(line, ORIENTATION, "<", "=")) {

                extractOrientation(line, entity);

            } else if (startWith(line, UNIT_OF_INFO, "<", " ")) {

                entity.put(UNIT_OF_INFO, extractUnitOfInfo(line, reader));

            } else if (startWith(line, PORTS, "<", " ")) {

                entity.put(PORTS, extractPorts(line));

            } else if (startWith(line, POINTS, "<", "")) {

                if (startWith(line, POINTS, "<", " "))
                    entity.put(POINTS, extractPoints(line));

            } else {

                System.err.println("Unexpected line: " + line);
                return false;

            }
        }

        if (line == null)
            return false;

        return true;
    }

    private boolean extractOrientation(String line, HashMap<String, Object> entity) {
        if (line == null || entity == null)
            return false;

        line = line.trim();
        extract(ORIENTATION, line.substring(1, line.length() - 1), entity);

        return true;
    }

    private boolean extractColorArcs(String line, HashMap<String, Object> entity) {
        if (line == null || entity == null)
            return false;

        line = line.trim();
        extract(COLOR, line.substring(1, line.length() - 1), entity);

        return true;
    }

    private List<String> extractPoints(String line) {
        if (line == null || line.trim().isEmpty() || line.trim().equalsIgnoreCase("<points=null>"))
            return new ArrayList<String>();

        List<String> coordinates = new ArrayList<String>();
        String[] subStrings = line.trim().split(" ");

        if (subStrings.length == 0)
            return null;

        for (int i = 1; i < subStrings.length - 1; i++) {

            subStrings[i] = subStrings[i].trim();
            coordinates.add(subStrings[i].substring(2));

        }

        String lastSubString = subStrings[subStrings.length - 1].trim();
        coordinates.add(lastSubString.substring(2, lastSubString.length() - 1));

        return coordinates;
    }

    private HashMap<String, Object> extractPorts(String line) {
        if (line == null)
            return null;

        String[] subStrings = line.trim().split(" ");

        if (subStrings.length == 0)
            return null;

        HashMap<String, Object> ports = new HashMap<String, Object>();

        extract(SOURCE, subStrings[1], ports);
        extract(TARGET, subStrings[3], ports);

        String sourceIndex = subStrings[2].trim().substring(INDEX.length() + 1);
        if (sourceIndex != null && !sourceIndex.isEmpty())
            ports.put(SOURCE_INDEX, sourceIndex);

        subStrings[4] = subStrings[4].trim();
        String targetIndex = subStrings[4].substring(INDEX.length() + 1, subStrings[4].length() - 1);
        if (targetIndex != null && !targetIndex.isEmpty())
            ports.put(TARGET_INDEX, targetIndex);

        return ports;
    }

    private HashMap<String, Object> extractUnitOfInfo(String line, BufferedReader reader) throws IOException {
        if (line == null)
            return null;

        String[] subStrings = line.trim().split(" ");

        if (subStrings.length == 0)
            return null;

        HashMap<String, Object> unitOfInfo = new HashMap<String, Object>();

        extract(CLASS, subStrings[1].substring(0, subStrings[1].length() - 1), unitOfInfo);

        while ((line = reader.readLine()) != null && !(line = line.trim()).equals("</unit>")) {

            if (startWith(line, LABEL, "<", " ")) {

                subStrings = line.trim().split(" ");
                extractLabel(TEXT, subStrings[1].substring(0, subStrings[1].length() - 1), unitOfInfo);

            } else if (startWith(line, FONT, "<", " ")) {

                unitOfInfo.put(FONT, extractFont(line));

            } else if (startWith(line, COLOR, "<", " ")) {

                unitOfInfo.put(COLOR, extractColor(line));

            }

        }

        return unitOfInfo;
    }

    private HashMap<String, Object> extractColor(String line) {
        if (line == null)
            return null;

        String[] subStrings = line.trim().split(" ");

        if (subStrings.length == 0)
            return null;

        HashMap<String, Object> color = new HashMap<String, Object>();

        extract(BACKGROUND_COLOR, subStrings[1], color);
        extract(FOREGROUND_COLOR, subStrings[2], color);
        extract(LABEL_COLOR, subStrings[3].substring(0, subStrings[3].length() - 1), color);

        return color;
    }

    private HashMap<String, Object> extractFont(String line) {
        if (line == null)
            return null;

        String[] subStrings = line.trim().split(" ");

        if (subStrings.length == 0)
            return null;

        HashMap<String, Object> font = new HashMap<String, Object>();

        extract(NAME, subStrings[1], font);
        extract(SIZE, subStrings[2].substring(0, subStrings[2].length() - 1), font);

        return font;
    }

    private HashMap<String, Object> extractLabel(String line) {
        if (line == null)
            return null;

        String[] subStrings = line.trim().split(" ");

        if (subStrings.length == 0)
            return null;

        HashMap<String, Object> label = new HashMap<String, Object>();

        extractLabel(TEXT, subStrings[1], label);
        extract(X, subStrings[2], label);
        extract(Y, subStrings[3], label);
        extract(WIDTH, subStrings[4], label);
        extract(HEIGHT, subStrings[5].substring(0, subStrings[5].length() - 1), label);

        return label;
    }

    private HashMap<String, Object> extractBoundary(String line) {
        if (line == null)
            return null;

        String[] subStrings = line.trim().split(" ");

        if (subStrings.length == 0)
            return null;

        HashMap<String, Object> boundary = new HashMap<String, Object>();

        extract(X, subStrings[1], boundary);
        extract(Y, subStrings[2], boundary);
        extract(WIDTH, subStrings[3], boundary);
        extract(HEIGHT, subStrings[4].substring(0, subStrings[4].length() - 1), boundary);

        return boundary;
    }

    private HashMap<String, Object> createEntity(String line) {
        if (line == null)
            return null;

        HashMap<String, Object> entity = new HashMap<String, Object>();
        String[] subStrings = line.trim().split(" ");

        if (subStrings.length == 0)
            return entity;

        extract(ID, subStrings[1], entity);
        extract(CLASS, subStrings[2].substring(0, subStrings[2].length() - 1), entity);

        return entity;
    }

    private boolean extract(String attribute, String str, HashMap<String, Object> container) {
        if (attribute == null || str == null || !startWith(str, attribute, "", "="))
            return false;

        String attributeValue = str.trim().substring(attribute.length() + 1);

        if (attributeValue != null && attributeValue.length() > 0 && attributeValue.charAt(attributeValue.length()-1)=='>') {
            attributeValue = attributeValue.substring(0, attributeValue.length()-1);
        }
        if (attribute != null && !attribute.isEmpty()) {
            container.put(attribute, attributeValue);
            return true;
        }

        return false;
    }

    private boolean extractLabel(String attribute, String str, HashMap<String, Object> container) {
        if (attribute == null || str == null || !startWith(str, attribute, "", "="))
            return false;

        String attributeValue = str.trim().substring(attribute.length() + 1);

        attributeValue = attributeValue.replaceAll("&#11", " ");
        attributeValue = attributeValue.replaceAll("&#10", "\n");

        if (attribute != null && !attribute.isEmpty()) {
            container.put(attribute, attributeValue);
            return true;
        }

        return false;
    }

    private boolean startWith(String str, String subStr, String prefix, String postfix) {
        if (str == null)
            return false;

        return str.trim().startsWith(prefix + subStr + postfix);
    }

}
