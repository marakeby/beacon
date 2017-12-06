package edu.vt.beacon.simulation;

import com.sun.javafx.application.PlatformImpl;
import edu.vt.beacon.simulation.model.containers.NetworkContainer;
import edu.vt.beacon.simulation.model.tree.*;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;

/**
 * Created by marakeby on 4/4/17.
 */
public class Simulator {

    public static NetworkContainer readNetworkFromSBML(File f)
    {
        HashMap<Integer,String> genePos = new HashMap<Integer,String>();
        HashMap<Integer,String> TextPos = new HashMap<Integer,String>();
        HashMap<String,Integer> geneNames = new HashMap<String,Integer>();
        HashMap<String, BooleanTree> trees = new HashMap<String, BooleanTree>();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();

            Document doc = dBuilder.parse(f);

            NodeList proof = doc.getElementsByTagName("sbml");


            if(proof.getLength() == 0)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ViSiBooL");
                alert.setHeaderText("Could not read SBML File!");
                alert.setContentText("XML seems not to be an SBML-file");
                alert.showAndWait();
                Logger.log(Level.SEVERE, "Wrong file format");
                return null;
            }

            if(!((Element)proof.item(0)).hasAttribute("level") || !((Element)proof.item(0)).getAttribute("level").equals("3"))
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ViSiBooL");
                alert.setHeaderText("Could not read SBML File!");
                alert.setContentText("SBML seems not to be an SBML-qual-file");
                alert.showAndWait();
                Logger.log(Level.SEVERE, "Wrong file format");
                return null;
            }
            //doc.getDocumentElement().normalize();

            //read names of regulatory factors
            NodeList regulatoryFactors = doc.getElementsByTagName("qual:qualitativeSpecies");

            for(int i = 0; i < regulatoryFactors.getLength(); i++)
            {
                Element rf = (Element)regulatoryFactors.item(i);


                //if maxLevel is too large, loading the network fails
                if(rf.hasAttribute("qual:maxLevel") && Integer.parseInt(rf.getAttribute("qual:maxLevel")) > 1)
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("ViSiBooL");
                    alert.setHeaderText("Could not read SBML File!");
                    alert.setContentText("MaxLevel > 1. Only binary values supported.");
                    alert.showAndWait();
                    Logger.log(Level.SEVERE, "Opening file failed with error of MaxLevel > 1");
                    return null;
                }


                if(rf.hasAttribute("qual:id"))
                {
                    String modGeneName = rf.getAttribute("qual:id");
                    String geneName = rf.getAttribute("qual:id");
                    String geneText = rf.getAttribute("qual:name");


                    int id = 1;

                    //remove duplicate genes
                    while(geneNames.keySet().contains(modGeneName))
                    {
                        modGeneName = geneName + "_" + id;
                        id++;
                    }

                    if(!modGeneName.equals(geneName))
                    {

                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("ViSiBooL");
                        alert.setHeaderText("Duplicate gene name found.");
                        alert.show();

                        Logger.log(Level.WARNING,"Duplicate gene " + geneName + "changed to " + modGeneName);
                        return null;
                    }

                    geneNames.put(modGeneName,i);
                    genePos.put(i, modGeneName);
                    TextPos.put(i, geneText);

                    //check if is constant
                    if(rf.hasAttribute("qual:constant") && rf.getAttribute("qual:constant").equals("true"))
                    {
                        if(rf.hasAttribute("qual:initialLevel"))
                            trees.put(modGeneName, new BooleanTree(new Literal(-1, null, rf.getAttribute("qual:initialLevel").equals("1") ? true : false),1));
                        else
                            //automatically set to one
                            trees.put(modGeneName, new BooleanTree(new Literal(-1, null,false),1));
                    }
                }
            }

            //read transitions
            //NodeList transitions = doc.getElementsByTagName("qual:listOfTransitions");
            NodeList transitions = doc.getElementsByTagName("qual:transition");
            if(transitions.getLength() < 1)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ViSiBooL");
                alert.setHeaderText("List of transitions error");
                alert.setContentText("List of transitions has wrong format");
                alert.show();
                Logger.log(Level.SEVERE,"List of transitions has wrong format");
                return null;
            }
            //transitions = transitions.item(0).getChildNodes();

            for(int t = 0; t < transitions.getLength(); t++)
            {
                Element trans = ((Element)transitions.item(t));
                //Extract name of output -> gene name
                String name = ((Element)trans.getElementsByTagName("qual:output").item(0)).getAttribute("qual:qualitativeSpecies");

                if(!geneNames.keySet().contains(name))
                {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("ViSiBooL");
                    alert.setHeaderText("Factor " + name + "not found");
                    alert.setContentText(name + " not found in list of species.");
                    alert.showAndWait();
                    Logger.log(Level.WARNING," gene " + name + " in transitions but not in species list");
                    return null;
                }

                //Extract function term
                Element function = ((Element)trans.getElementsByTagName("apply").item(0));

                if(trees.containsKey(name)) {
                    System.out.println("Duplicate transition target, or Input transitions together");

                    BooleanTree existingFormula = trees.get(name);
                    //construct a new boolean tree based on the previous tree and oring it with the additional tree

                    BooleanOperator orThisArc = new BooleanOperator(OperatorType.OP_OR,null,false);
                    orThisArc.addNewOperand(parseMLTree(function,orThisArc,false,geneNames)); // new tree is here
                    orThisArc.addNewOperand(existingFormula.getRoot());

                    trees.remove(name);
                    trees.put(name, new BooleanTree(orThisArc,0));

                    //trees.get(name).addToPosition(trees.get(name).getNumberOfElements(), orThisArc);

                }
                else {
                    trees.put(name, new BooleanTree(parseMLTree(function, null, false, geneNames), 0));
                }

            }

            if(trees.size() != geneNames.size())
            {

                PlatformImpl.startup(()->{}); // JavaFx and Swing do not like to play with each other especially  on Mac
                //We probably should remove all refs to JavaFx or as much as possible
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ViSiBooL");
                alert.setHeaderText("SBML read error");
                alert.setContentText("no of transitions and no of factors are not equal");
                alert.showAndWait();
                Logger.log(Level.SEVERE,"Wrong SBML format. unequal functions and factors.");
                return null;
            }

            //resort data in correct order
            Vector<String> geneSet = new Vector<String>();
            Vector<String> TextSet = new Vector<String>();
            Vector<BooleanTree> treeSet = new Vector<BooleanTree>();

            for(int i = 0; i < trees.size(); i++)
            {
                geneSet.add(i, genePos.get(i));
                TextSet.add(i, TextPos.get(i));
                treeSet.add(i, trees.get(genePos.get(i)));
            }
            return new NetworkContainer(treeSet, geneSet, TextSet);

        } catch (ParserConfigurationException | SAXException | IOException e) {
            PlatformImpl.startup(()->{}); // JavaFx and Swing do not like to play with each other especially  on Mac
            //We probably should remove all refs to JavaFx or as much as possible
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ViSiBooL");
            alert.setHeaderText("Could not read sbml-file");
            alert.setContentText("Check if file is in correct sbml-qual format.");
            alert.show();
            return null;
        }



    }

    public static BooleanFormula parseMLTree(Element function, BooleanOperator parent, boolean negated, HashMap<String,Integer> genes)
    {

        Element apply = function;

        function = (Element)function.getChildNodes().item(1);

        if(function.getTagName().equals("or"))
        {
            NodeList children = apply.getChildNodes();
            BooleanOperator op = new BooleanOperator(OperatorType.OP_OR, parent, negated);

//			   System.out.println(children.getLength());
//
//			   for(int i = 0; i < children.getLength(); i++) //skip function node (child 1)
//			   {
//				   System.out.println("Val :" + children.item(i).getNodeName());
//
//				   if(children.item(i).getNodeName().equals("apply"))
//					   System.out.println("Child : " + children.item(i).getChildNodes().item(1).getNodeName());
//			   }

            for(int i = 2; i < children.getLength(); i++) //skip function node (child 1)
            {
                if(Node.ELEMENT_NODE == children.item(i).getNodeType())
                    parseMLTree(((Element)children.item(i)), op, false, genes);
            }

            return op;
        }
        if(function.getTagName().equals("and"))
        {
            NodeList children = apply.getChildNodes();
            BooleanOperator op = new BooleanOperator(OperatorType.OP_AND, parent, negated);


            for(int i = 2; i < children.getLength(); i++) //skip function node (child 1)
            {
                //System.out.println("Val :" + children.item(i).getNodeName());
                if(Node.ELEMENT_NODE == children.item(i).getNodeType())
                    parseMLTree(((Element)children.item(i)), op, false, genes);
            }

            return op;
        }
        if(function.getTagName().equals("not"))
        {
            NodeList children = apply.getChildNodes();

            return parseMLTree(((Element)children.item(3)), parent, true, genes);

        }

        if(function.getTagName().equals("eq"))
        {
            String name = function.getNextSibling().getNextSibling().getFirstChild().getNodeValue();
            int idx = genes.get(name.trim()).intValue();
            String neg = (function.getNextSibling().getNextSibling().getNextSibling().getNextSibling().getFirstChild().getNodeValue()).trim();
            return new Literal(idx,parent,neg.equals("1") ? false : true);
        }

        return null;

    }


}
