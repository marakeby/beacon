package edu.vt.beacon.sbml;

import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.graph.glyph.AbstractGlyph;
import edu.vt.beacon.graph.glyph.GlyphType;
import edu.vt.beacon.graph.glyph.arc.*;
import edu.vt.beacon.graph.glyph.node.AbstractNode;
import edu.vt.beacon.graph.glyph.node.activity.AbstractActivity;
import edu.vt.beacon.graph.glyph.node.activity.BiologicalActivity;
import edu.vt.beacon.graph.glyph.node.activity.Phenotype;
import edu.vt.beacon.graph.glyph.node.auxiliary.Port;
import edu.vt.beacon.graph.glyph.node.operator.AbstractOperator;
import edu.vt.beacon.graph.glyph.node.submap.Submap;
import edu.vt.beacon.layer.Layer;
import edu.vt.beacon.map.Map;
import edu.vt.beacon.pathway.Pathway;
import org.sbml.jsbml.ASTNode;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.ext.qual.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by marakeby on 1/18/17.
 */

public class SbgnToSbmlConverterDebug {
    private Pathway pathway;
    private Document document;
    private SBMLDocument sbmlDoc;
    private static HashMap<String, Object> allSpecies = new HashMap<String, Object>();
    IDOption id;
    private HashMap<String, Integer> unique_ids =new HashMap<String, Integer>();


    public SbgnToSbmlConverterDebug(Document document, IDOption option){
        this.document = document;
        this.pathway = document.getPathway();
        sbmlDoc = getInitialSbmlDoc( pathway.getName());
        this.id = option;
    }

    private String get_id(AbstractNode node){
        String id="";
        if (this.id == IDOption.Id) {
            id = node.getId();
        }
        else {
            id = node.getLabel().getText();
            id = nomalizeName(id);

        }
        System.out.println("obtained name:" +id);

//        Integer count = unique_ids.get(id);
//        if (count ==null)
//            count = 0;
//        else
//            count +=1;
//
//        unique_ids.put(id, count);
//        if (count>1)
//            id = id + "_" + count;
        return id;
    }

    public SBMLDocument getSbml(){

        ArrayList<AbstractGlyph> allActivites = getAllActivites(pathway);
//        System.out.println(allActivites.size() + " activities found");
        addSpecies(allActivites);

        ArrayList<AbstractArc> allArcs = getAllArcs(pathway);
//        System.out.println(allArcs.size()+ " arcs found");
        addTansitions(allArcs);
        return sbmlDoc;
    }

    private ASTNode getMathTree(AbstractNode node, ASTNode math){
        ASTNode logicalNode= new ASTNode() ;
        if (node.getType() == GlyphType.AND)
            logicalNode = new ASTNode(ASTNode.Type.LOGICAL_AND);
        else if (node.getType() == GlyphType.OR)
            logicalNode = new ASTNode(ASTNode.Type.LOGICAL_OR);

        //get all input arcs for this node
        ArrayList<AbstractArc> arcs = node.getInputArcs();
        // loop over the arcs get their inputs, and  them to the math tree
        for (AbstractArc a: arcs){
            if (a.getSource() instanceof AbstractActivity) {
                String source_name = a.getSource().getLabel().getText();
                source_name= nomalizeName(source_name);

                ASTNode equal = new ASTNode(ASTNode.Type.RELATIONAL_EQ);
                equal.addChild(new ASTNode(source_name));
                equal.addChild(new ASTNode(1));
                logicalNode.addChild(equal);
            }
            else {

//                ASTNode equal = new ASTNode(ASTNode.Type.RELATIONAL_EQ);
                ASTNode equal = null;
                equal = getMathTree(a.getSource(), equal);
                equal.addChild(new ASTNode(1));
                logicalNode.addChild(equal);
            }

        }
        if (math ==null)
            math = logicalNode;
        else
            math.addChild(logicalNode);
        return math;

    }

    private void addInputs(AbstractNode node, Transition tr, QualModelPlugin qualModel, Sign sign){
//        if (node instanceof AbstractActivity){
            String source_name = node.getLabel().getText();
            source_name= nomalizeName(source_name);
            Input in= new Input("tr_" + source_name , qualModel.getQualitativeSpecies(source_name), InputTransitionEffect.none );
            in.setThresholdLevel(1);
            in.setSign(sign);
            tr.addInput(in);
//            return;
//        }
//        ArrayList<AbstractArc> arcs = node.getInputArcs();
//        for (AbstractArc a: arcs)
//            addInputs( a.getSource(), tr, qualModel, sign);

    }

//    private void getInputs(AbstractArc arc, QualModelPlugin qualModel ){
//        AbstractNode source = arc.getSource();
//        Transition tr = qualModel.getTransition(arc.getId());
//        // use only one function term by transition, the zero index is reserved for the default function term
//        FunctionTerm ft= tr.getListOfFunctionTerms().get(1);
//        ASTNode math = ft.getMath();
//        //the arc has no inputs
//        if (source==null)
//            return;
//
//        // the arc is connected to a logical node, assuming no loops in the model
//        if(source instanceof AbstractOperator){
//
//            getMathTree(source, math);
//
//        }
//        addInputs(source, tr, qualModel, sign);
//
//
//    }

    private void addDefaultFunctionTerm(Transition tr){
        //default function term
        FunctionTerm defterm = new FunctionTerm();
        defterm.setDefaultTerm(true);
        defterm.setResultLevel(0);
        tr.addFunctionTerm(defterm);
    }
    private void getTransition(AbstractArc arc, QualModelPlugin qualModel ){
        System.out.println("getTransition");
        String target_id = get_id(arc.getTarget());
        String source_name = get_id(arc.getSource());
        QualitativeSpecies source = qualModel.getQualitativeSpecies(source_name);
        QualitativeSpecies target = qualModel.getQualitativeSpecies(target_id);
        if (target ==null || source ==null)
        {
            System.out.println("null"+ target_id + source_name);
            return;
        }

        Transition tr = qualModel.createTransition(arc.getId());
        addDefaultFunctionTerm(tr);


        // add a function term to the transition (SBML) based on the arc type in SBGN
        FunctionTerm fterm = new FunctionTerm();

        //get the target
        Output o = new Output(target , OutputTransitionEffect.assignmentLevel );
        tr.addOutput(o);
        ASTNode math = new ASTNode(ASTNode.Type.RELATIONAL_EQ);


        Input in= new Input("tr_" + source_name + target_id , source , InputTransitionEffect.none );
        in.setThresholdLevel(1);

            if (arc instanceof NegativeInfluence) {
                System.out.println("negative arc");
                in.setSign(Sign.negative);
                fterm.setResultLevel(1);
                math.addChild(new ASTNode(source_name));
                math.addChild(new ASTNode(0));

            }

            else if (arc instanceof PositiveInfluence) {
                System.out.println("positive arc");
            in.setSign(Sign.positive);
            fterm.setResultLevel(1);
            math.addChild(new ASTNode(source_name));
            math.addChild(new ASTNode(1));

        }

//        else if (arc instanceof UnknownInfluence) {
        else {
            System.out.println("unknown arc");
            in.setSign(Sign.unknown);
            //TODO:change this to reflect unknown relationship
            fterm.setResultLevel(1);
            math.addChild(new ASTNode(source_name));
            math.addChild(new ASTNode(1));

        }

//        in.setThresholdLevel(1);
//        in.setSign(sign);
        tr.addInput(in);
        fterm.setMath(math);
        tr.addFunctionTerm(fterm);


    }

    private void addTansitions(ArrayList<AbstractArc> allArcs){
        QualModelPlugin qualModel = (QualModelPlugin)sbmlDoc.getModel().getExtension(QualConstants.namespaceURI);

        for (AbstractArc arc : allArcs){

            if (arc instanceof LogicArc || arc.getTarget() instanceof Submap || arc.getSource() instanceof Submap)
                continue;

            getTransition(arc, qualModel);

        }

    }

    private void set_transition_inputs(Transition tr, QualModelPlugin qualModel, AbstractOperator node){
        ArrayList<AbstractArc> arcs = node.getInputArcs();
        for (AbstractArc a: arcs) {
            if (a.getSource() == null) {
                JOptionPane.showMessageDialog(null,  " An arc is misaligned ", "InfoBox: " , JOptionPane.ERROR_MESSAGE);
                a.setSelected(true);
                document.getCanvas().repaint();
                break;
            }
            String source_name = get_id(a.getSource());
            QualitativeSpecies source = qualModel.getQualitativeSpecies(source_name);
            if (source==null) {
                System.out.println("no source found" + source_name);
                continue;
            }
            Input in= new Input("tr_" + source_name + "_to_" + node.getId()  +"_input"  , source, InputTransitionEffect.none );
            in.setThresholdLevel(1);
            in.setSign(Sign.positive);
            tr.addInput(in);

        }
    }

    private void logicalGateToTransition(AbstractOperator node, QualModelPlugin qualModel){

        Transition tr = qualModel.createTransition("tr_"+ get_id(node));
        addDefaultFunctionTerm(tr);
        String target_id = get_id(node);
        if (target_id==null) {

            System.out.println("missing" + target_id);
            return;
        }
        QualitativeSpecies target = qualModel.getQualitativeSpecies(target_id);

        Output o = new Output( target, OutputTransitionEffect.assignmentLevel);
        tr.addOutput(o);
        set_transition_inputs(tr, qualModel, node);

        ArrayList<AbstractArc> arcs = node.getInputArcs();

        // add a function term to the transition (SBML) based on the arc type in SBGN
        FunctionTerm fterm = new FunctionTerm();
        fterm.setResultLevel(1);
        ASTNode logicalNode= new ASTNode() ;
        if (node.getType() == GlyphType.AND)
            logicalNode = new ASTNode(ASTNode.Type.LOGICAL_AND);
        else if (node.getType() == GlyphType.OR)
            logicalNode = new ASTNode(ASTNode.Type.LOGICAL_OR);
        else if (node.getType() == GlyphType.NOT) {
            logicalNode = new ASTNode(ASTNode.Type.LOGICAL_OR);
        }


        for (AbstractArc a: arcs) {
            String source_name = get_id(a.getSource());
            ASTNode equal = new ASTNode(ASTNode.Type.RELATIONAL_EQ);
            equal.addChild(new ASTNode(source_name));
            if( node.getType() == GlyphType.NOT)
                equal.addChild(new ASTNode(0));
            else
                equal.addChild(new ASTNode(1));
            logicalNode.addChild(equal);
        }

        fterm.setMath(logicalNode);
        tr.addFunctionTerm(fterm);
    }


    private void addSpecies(ArrayList<AbstractGlyph> allActivites){
        QualModelPlugin qualModel = (QualModelPlugin)sbmlDoc.getModel().getExtension(QualConstants.namespaceURI);
        Compartment compartment = sbmlDoc.getModel().getCompartment(0);


        for (AbstractGlyph g : allActivites){

            if (g == null)
                continue;
          //  String id = g.getId();

            String name = get_id((AbstractNode)g);
            System.out.println("about to add qualitative Species:" + name);
            QualitativeSpecies specisA = qualModel.createQualitativeSpecies(name, compartment);
            name = ((AbstractNode)g).getLabel().getText();
            name= nomalizeName(name);
            specisA.setName(name); // update the name so it looks nicer to the user
            specisA.setMaxLevel(1);
            System.out.println("added a new species: " + specisA.getId());
            //if this node is input, set it as constant in SBML
            if(isInputNode((AbstractNode)g))
                specisA.setConstant(true);
            else
                specisA.setConstant(false);
        }

        for (AbstractGlyph g : allActivites)
            if (g instanceof AbstractOperator) {
                System.out.println("about to add logic Gate :"+g.getId());
                logicalGateToTransition((AbstractOperator) g, qualModel);
            }
    }

    private String nomalizeName(String name){
//        name = name.replaceAll(" ", "_");
//        name = name.replaceAll("\r", "_");
//        name = name.replaceAll("\n", "_");
//        name = name.replaceAll("\r\n", "_");
//        name = name.replaceAll("\\n\\r", "_");
//        name = name.replaceAll("\\r\\n", "");
        return name.replaceAll("[^A-Za-z0-9 ]", "_");
//        return name;
    }

    private Boolean isInputNode(AbstractNode node){
        Boolean output = true;
        for(Port p: node.getPorts())
        {
            for(AbstractArc arc : p.getArcs())
                if (arc.getTarget() == null || arc.getTarget().equals(node))
                    output=false;
        }
        return output;
    }

    private SBMLDocument getInitialSbmlDoc(String id){

        SBMLDocument doc = new SBMLDocument(3, 1);
        Model model = doc.createModel(id);

        QualModelPlugin qualModel = new QualModelPlugin(model);
        model.addExtension(QualConstants.namespaceURI, qualModel);
        Compartment compartment = model.createCompartment("default");
        compartment.setSize(1d);
        return doc;

    }

    private ArrayList<AbstractArc> getAllArcs(Pathway pathway) {

        Map map = pathway.getMap();
        ArrayList<AbstractArc> allGlyphs = new ArrayList<>();

        for (Layer l : map.getLayers())
            for (AbstractGlyph g : l.getGlyphs()) {
                if (g instanceof AbstractArc)
                    allGlyphs.add((AbstractArc)g);
            }
        return allGlyphs;
    }

    private ArrayList<AbstractGlyph> getAllActivites(Pathway pathway) {

        Map map = pathway.getMap();
        ArrayList<AbstractGlyph> allGlyphs = new ArrayList<AbstractGlyph>();

        for (Layer l : map.getLayers())
            for(AbstractGlyph g :l.getGlyphs() ) {
                if(g instanceof BiologicalActivity || g instanceof Phenotype || g instanceof AbstractOperator)
                    allGlyphs.add(g);
            }

        return allGlyphs;
    }
}
