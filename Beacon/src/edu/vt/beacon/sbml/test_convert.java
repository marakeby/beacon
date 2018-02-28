//package edu.vt.beacon.sbml;
//
//import javax.xml.stream.XMLStreamException;
//import java.io.IOException;
//import java.util.ArrayList;
//
//import edu.vt.beacon.graph.glyph.AbstractGlyph;
//import edu.vt.beacon.graph.glyph.arc.AbstractArc;
//import edu.vt.beacon.graph.glyph.node.activity.BiologicalActivity;
//import edu.vt.beacon.graph.glyph.node.activity.Phenotype;
//import edu.vt.beacon.io.FileManager;
//import edu.vt.beacon.pathway.Pathway;
//import org.sbml.jsbml.*;
//import org.sbml.jsbml.ext.qual.*;
//import edu.vt.beacon.map.Map;
//import edu.vt.beacon.layer.Layer;
///**
// * Created by marakeby on 1/17/17.
// */
//public class test_convert {
//
//    public static void test_memory() throws IOException, XMLStreamException {
//        String output_fileName = "examples/test_memory1.sbml";
//        SBMLDocument doc = new SBMLDocument(3, 1);
//        Model model = doc.createModel("test_model");
//
//        QualModelPlugin qualModel = new QualModelPlugin(model);
//        model.addExtension(QualConstants.namespaceURI, qualModel);
//        Compartment compartment = model.createCompartment("default");
//        compartment.setSize(1d);
//
//        QualitativeSpecies specisA = qualModel.createQualitativeSpecies("A", compartment);
//        QualitativeSpecies specisB = qualModel.createQualitativeSpecies("B", compartment);
//        QualitativeSpecies specisC = qualModel.createQualitativeSpecies("C", compartment);
//
//        Transition tr = qualModel.createTransition("tr_B");
//        Input in= new Input("theta_B_A", specisA, InputTransitionEffect.none );
//        Output o = new Output(specisB, OutputTransitionEffect.assignmentLevel );
//
//        tr.addInput(in);
//        tr.addOutput(o);
//
//        FunctionTerm fterm = new FunctionTerm();
//        fterm.setDefaultTerm(true);
//        fterm.setResultLevel(1);
//        tr.addFunctionTerm(fterm);
//
//        SBMLWriter w = new SBMLWriter();
//
//        w.writeSBMLToFile(doc,output_fileName );
//
//    }
//
//    public static void test_memory_with_logic() throws IOException, XMLStreamException {
//        String output_fileName = "examples/test_memory1.sbml";
//        SBMLDocument doc = new SBMLDocument(3, 1);
//        Model model = doc.createModel("test_model");
//
//        QualModelPlugin qualModel = new QualModelPlugin(model);
//        model.addExtension(QualConstants.namespaceURI, qualModel);
//        Compartment compartment = model.createCompartment("default");
//        compartment.setSize(1d);
//
//        QualitativeSpecies specisA = qualModel.createQualitativeSpecies("A", compartment);
//        QualitativeSpecies specisB = qualModel.createQualitativeSpecies("B", compartment);
//        QualitativeSpecies specisC = qualModel.createQualitativeSpecies("C", compartment);
//
//        Transition tr = qualModel.createTransition("tr_B");
//        Input in= new Input("theta_B_A", specisA, InputTransitionEffect.none );
//        Output o = new Output(specisB, OutputTransitionEffect.assignmentLevel );
//
//        tr.addInput(in);
//        tr.addOutput(o);
//
//        FunctionTerm fterm = new FunctionTerm();
//        fterm.setDefaultTerm(true);
//        fterm.setResultLevel(1);
//        tr.addFunctionTerm(fterm);
//
//
//        SBMLWriter w = new SBMLWriter();
//
//        w.writeSBMLToFile(doc,output_fileName );
//
//    }
//
//
//    public static void test_converter(String path) throws IOException, XMLStreamException{
//        String output_fileName = path +".sbml";
//        path = path +".sbgn";
//        Pathway pathway = FileManager.load(path);
//
//        SbgnToSbmlConverterDebug conveter = new SbgnToSbmlConverterDebug(pathway, IDOption.Id);
//
//        SBMLDocument doc = conveter.getSbml();
//
//        SBMLWriter w = new SBMLWriter();
//        try {
////            w.write(doc, System.out);
//            w.writeSBMLToFile(doc,output_fileName );
//        }
//        catch (Exception e) {
//            for(StackTraceElement el:e.getStackTrace())
//                System.out.println(el);
//        }
//
//    }
//
//    public static void main(String[] args) throws IOException, XMLStreamException {
////        test_memory();
////        test_converter("examples/simple_logic");
////        test_converter("examples/simple_logic2");
////        test_converter("examples/ex1_drought_stress");
////        test_converter("examples/Gillaspy");
//            test_converter("examples/Gillaspy");
////        test_converter("examples/negative");
//
//
//
//    }
//}
