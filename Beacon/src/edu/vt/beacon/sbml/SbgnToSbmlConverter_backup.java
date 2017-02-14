package edu.vt.beacon.sbml;

import edu.vt.beacon.graph.glyph.AbstractGlyph;
import edu.vt.beacon.graph.glyph.arc.*;
import edu.vt.beacon.graph.glyph.node.AbstractNode;
import edu.vt.beacon.graph.glyph.node.activity.BiologicalActivity;
import edu.vt.beacon.graph.glyph.node.activity.Phenotype;
import edu.vt.beacon.graph.glyph.node.auxiliary.Port;
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
public class SbgnToSbmlConverter_backup {
    private Pathway pathway;
    private SBMLDocument sbmlDoc;
    private static HashMap<String, Object> allSpecies = new HashMap<String, Object>();
//    public SbgnToSbmlConverter() {
//    }

    public SbgnToSbmlConverter_backup(Pathway pathway){
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
    private void addTansitions(ArrayList<AbstractArc> allArcs){
        QualModelPlugin qualModel = (QualModelPlugin)sbmlDoc.getModel().getExtension(QualConstants.namespaceURI);

        for (AbstractArc arc : allArcs){

            if (arc instanceof LogicArc)
                continue;

            Transition tr = qualModel.createTransition(arc.getId());
            String source_id = arc.getSource().getId();
            String source_name = arc.getSource().getLabel().getText();

            source_name= nomalizeName(source_name);

//          create input in the SBML model
            Input in= new Input("tr_" + source_name , qualModel.getQualitativeSpecies(source_name), InputTransitionEffect.none );
            in.setThresholdLevel(1);

//            String target_id = arc.getTarget().getId();

//          create output in the SBML model
            String target_id = arc.getTarget().getLabel().getText();
            target_id= nomalizeName(target_id);
            Output o = new Output( qualModel.getQualitativeSpecies(target_id), OutputTransitionEffect.assignmentLevel );

            tr.addInput(in);
            tr.addOutput(o);

            //default function term
            FunctionTerm defterm = new FunctionTerm();
            defterm.setDefaultTerm(true);
            defterm.setResultLevel(0);
            tr.addFunctionTerm(defterm);

            // add a function term to the transition (SBML) based on the arc type in SBGN
            FunctionTerm fterm = new FunctionTerm();
            ASTNode math = new ASTNode(ASTNode.Type.RELATIONAL_EQ);

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

            fterm.setMath(math);
            tr.addFunctionTerm(fterm);


        }

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
                if(g instanceof BiologicalActivity || g instanceof Phenotype)
                    allGlyphs.add(g);
            }

        return allGlyphs;
    }
}
