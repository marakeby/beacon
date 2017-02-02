package edu.vt.beacon.sbml;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.ArrayList;

import edu.vt.beacon.graph.glyph.AbstractGlyph;
import edu.vt.beacon.graph.glyph.arc.AbstractArc;
import edu.vt.beacon.graph.glyph.node.activity.BiologicalActivity;
import edu.vt.beacon.graph.glyph.node.activity.Phenotype;
import edu.vt.beacon.io.FileManager;
import edu.vt.beacon.pathway.Pathway;
import org.sbml.jsbml.*;
import org.sbml.jsbml.ext.qual.*;
import edu.vt.beacon.map.Map;
import edu.vt.beacon.layer.Layer;
/**
 * Created by marakeby on 1/17/17.
 */
public class test_convert {

    public static void test_memory() throws IOException, XMLStreamException {
        String output_fileName = "examples/test_memory1.sbml";
        SBMLDocument doc = new SBMLDocument(3, 1);
        Model model = doc.createModel("test_model");

        QualModelPlugin qualModel = new QualModelPlugin(model);
        model.addExtension(QualConstants.namespaceURI, qualModel);
        Compartment compartment = model.createCompartment("default");
        compartment.setSize(1d);

        QualitativeSpecies specisA = qualModel.createQualitativeSpecies("A", compartment);
        QualitativeSpecies specisB = qualModel.createQualitativeSpecies("B", compartment);
        QualitativeSpecies specisC = qualModel.createQualitativeSpecies("C", compartment);

        Transition tr = qualModel.createTransition("tr_B");
        Input in= new Input("theta_B_A", specisA, InputTransitionEffect.none );
        Output o = new Output(specisB, OutputTransitionEffect.assignmentLevel );

        tr.addInput(in);
        tr.addOutput(o);

        FunctionTerm fterm = new FunctionTerm();
        fterm.setDefaultTerm(true);
        fterm.setResultLevel(1);
        tr.addFunctionTerm(fterm);

        SBMLWriter w = new SBMLWriter();

        w.writeSBMLToFile(doc,output_fileName );

    }

    public static void test_memory_with_logic() throws IOException, XMLStreamException {
        String output_fileName = "examples/test_memory1.sbml";
        SBMLDocument doc = new SBMLDocument(3, 1);
        Model model = doc.createModel("test_model");

        QualModelPlugin qualModel = new QualModelPlugin(model);
        model.addExtension(QualConstants.namespaceURI, qualModel);
        Compartment compartment = model.createCompartment("default");
        compartment.setSize(1d);

        QualitativeSpecies specisA = qualModel.createQualitativeSpecies("A", compartment);
        QualitativeSpecies specisB = qualModel.createQualitativeSpecies("B", compartment);
        QualitativeSpecies specisC = qualModel.createQualitativeSpecies("C", compartment);

        Transition tr = qualModel.createTransition("tr_B");
        Input in= new Input("theta_B_A", specisA, InputTransitionEffect.none );
        Output o = new Output(specisB, OutputTransitionEffect.assignmentLevel );

        tr.addInput(in);
        tr.addOutput(o);

        FunctionTerm fterm = new FunctionTerm();
        fterm.setDefaultTerm(true);
        fterm.setResultLevel(1);
        tr.addFunctionTerm(fterm);


        SBMLWriter w = new SBMLWriter();

        w.writeSBMLToFile(doc,output_fileName );

    }


    public static void test_converter() throws IOException, XMLStreamException {
        String path = "examples/ex1_drought_stress.sbgn";
        String output_fileName = "examples/ex1_drought_stress2.sbml";
        Pathway pathway = FileManager.load(path);

        SbgnToSbmlConverter conveter = new SbgnToSbmlConverter(pathway);

        SBMLDocument doc = conveter.getSbml();

        SBMLWriter w = new SBMLWriter();

        w.writeSBMLToFile(doc,output_fileName );

    }



    public static void test_converter_withlogic() throws IOException, XMLStreamException {
        String path = "examples/simple_logic.sbgn";
        String output_fileName = "examples/simple_logic.sbml";
        Pathway pathway = FileManager.load(path);

        SbgnToSbmlConverterDebug conveter = new SbgnToSbmlConverterDebug(pathway);

        SBMLDocument doc = conveter.getSbml();

        SBMLWriter w = new SBMLWriter();

        w.writeSBMLToFile(doc,output_fileName );

    }
    public static void test_converter_withlogic2() throws IOException, XMLStreamException {
        String path = "examples/simple_logic2.sbgn";
        String output_fileName = "examples/simple_logic2.sbml";
        Pathway pathway = FileManager.load(path);

        SbgnToSbmlConverterDebug conveter = new SbgnToSbmlConverterDebug(pathway);

        SBMLDocument doc = conveter.getSbml();

        SBMLWriter w = new SBMLWriter();

        w.writeSBMLToFile(doc,output_fileName );

    }

    public static void main(String[] args) throws IOException, XMLStreamException {
//        test_memory();
//        test_converter();
        test_converter_withlogic();
          test_converter_withlogic2();

//        String a = "a";
//        String b = "b";
//        FunctionTerm fterm = new FunctionTerm();
//        fterm.setResultLevel(0);
//
//        ASTNode andNode = new ASTNode(ASTNode.Type.LOGICAL_AND);
//        andNode.addChild(new ASTNode(a));
//        andNode.addChild(new ASTNode(b));
//
//        ASTNode math = new ASTNode(ASTNode.Type.RELATIONAL_EQ);
//        math.addChild(andNode);
//        math.addChild(new ASTNode(1));
//
//        fterm.setMath(math);
//        System.out.println(fterm.getMathMLString());


    }
}
