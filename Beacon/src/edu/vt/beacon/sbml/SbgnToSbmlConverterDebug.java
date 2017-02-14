package edu.vt.beacon.sbml;

import edu.vt.beacon.graph.glyph.AbstractGlyph;
import edu.vt.beacon.graph.glyph.GlyphType;
import edu.vt.beacon.graph.glyph.arc.*;
import edu.vt.beacon.graph.glyph.node.AbstractNode;
import edu.vt.beacon.graph.glyph.node.activity.AbstractActivity;
import edu.vt.beacon.graph.glyph.node.activity.BiologicalActivity;
import edu.vt.beacon.graph.glyph.node.activity.Phenotype;
import edu.vt.beacon.graph.glyph.node.auxiliary.Port;
import edu.vt.beacon.graph.glyph.node.operator.AbstractOperator;
import edu.vt.beacon.layer.Layer;
import edu.vt.beacon.map.Map;
import edu.vt.beacon.pathway.Pathway;
import org.sbml.jsbml.ASTNode;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.ext.qual.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by marakeby on 1/18/17.
 */
public class SbgnToSbmlConverterDebug {
    private Pathway pathway;
    private SBMLDocument sbmlDoc;
    private static HashMap<String, Object> allSpecies = new HashMap<String, Object>();
//    public SbgnToSbmlConverter() {
//    }

    public SbgnToSbmlConverterDebug(Pathway pathway){
        this.pathway = pathway;
        sbmlDoc = getInitialSbmlDoc( pathway.getName());
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
        Transition tr = qualModel.createTransition(arc.getId());
        addDefaultFunctionTerm(tr);


        // add a function term to the transition (SBML) based on the arc type in SBGN
        FunctionTerm fterm = new FunctionTerm();

        //get the target
        String target_id = arc.getTarget().getLabel().getText();
        target_id= nomalizeName(target_id);
//        System.out.println(target_id);
        Output o = new Output( qualModel.getQualitativeSpecies(target_id), OutputTransitionEffect.assignmentLevel );
        tr.addOutput(o);

        Sign sign = Sign.positive;
        ASTNode math = new ASTNode(ASTNode.Type.RELATIONAL_EQ);

        String source_name = arc.getSource().getLabel().getText();
        source_name= nomalizeName(source_name);

        Input in= new Input("tr_" + source_name +"something" , qualModel.getQualitativeSpecies(source_name), InputTransitionEffect.none );
        in.setThresholdLevel(1);

        if (arc instanceof NegativeInfluence) {
            in.setSign(Sign.negative);
            fterm.setResultLevel(0);
            math.addChild(new ASTNode(source_name));
            math.addChild(new ASTNode(1));

        }

        if (arc instanceof PositiveInfluence) {
            in.setSign(Sign.positive);
            fterm.setResultLevel(1);
            math.addChild(new ASTNode(source_name));
            math.addChild(new ASTNode(1));

        }

        if (arc instanceof UnknownInfluence) {
            in.setSign(Sign.unknown);
            //TODO:change this to reflect unknown relationship
            fterm.setResultLevel(1);
            math.addChild(new ASTNode(source_name));
            math.addChild(new ASTNode(1));

        }

        in.setThresholdLevel(1);
        in.setSign(sign);
        tr.addInput(in);
        fterm.setMath(math);
        tr.addFunctionTerm(fterm);


    }

    private void addTansitions(ArrayList<AbstractArc> allArcs){
        QualModelPlugin qualModel = (QualModelPlugin)sbmlDoc.getModel().getExtension(QualConstants.namespaceURI);

        for (AbstractArc arc : allArcs){

            if (arc instanceof LogicArc)
                continue;

            getTransition(arc, qualModel);

        }

    }
    private void logicalGateToTransition(AbstractOperator node, QualModelPlugin qualModel){

        Transition tr = qualModel.createTransition("tr_"+node.getLabel().getText());
        addDefaultFunctionTerm(tr);
        String target_id = node.getLabel().getText();
        target_id = nomalizeName(target_id);
        Output o = new Output( qualModel.getQualitativeSpecies(target_id), OutputTransitionEffect.assignmentLevel );
        tr.addOutput(o);

        // add a function term to the transition (SBML) based on the arc type in SBGN
        FunctionTerm fterm = new FunctionTerm();
        fterm.setResultLevel(1);
        ASTNode logicalNode= new ASTNode() ;
        if (node.getType() == GlyphType.AND)
            logicalNode = new ASTNode(ASTNode.Type.LOGICAL_AND);
        else if (node.getType() == GlyphType.OR)
            logicalNode = new ASTNode(ASTNode.Type.LOGICAL_OR);

        ArrayList<AbstractArc> arcs = node.getInputArcs();
        for (AbstractArc a: arcs) {

            String source_name = a.getSource().getLabel().getText();
            source_name = nomalizeName(source_name);

            Input in= new Input("tr_" + source_name +"_input" , qualModel.getQualitativeSpecies(source_name), InputTransitionEffect.none );
            in.setThresholdLevel(1);
            in.setSign(Sign.positive);
            tr.addInput(in);

            ASTNode equal = new ASTNode(ASTNode.Type.RELATIONAL_EQ);
            equal.addChild(new ASTNode(source_name));
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

            String id = g.getId();
            String name= ((AbstractNode)g).getLabel().getText();
            name= nomalizeName(name);
            QualitativeSpecies specisA = qualModel.createQualitativeSpecies(name, compartment);
            specisA.setName(name);
            specisA.setMaxLevel(1);

            //if this node is input, set it as constant in SBML
            if(isInputNode((AbstractNode)g))
                specisA.setConstant(true);
            else
                specisA.setConstant(false);

            if (g instanceof AbstractOperator){
                logicalGateToTransition((AbstractOperator) g, qualModel);
            }

        }
    }

    private String nomalizeName(String name){
        return name.replace(" ", "_");
    }

    private Boolean isInputNode(AbstractNode node){
        Boolean output = true;
        for(Port p: node.getPorts())
        {
            for(AbstractArc arc : p.getArcs())
                if (arc.getTarget().equals(node))
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
