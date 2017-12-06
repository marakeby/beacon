package edu.vt.beacon.editor.action.handler;

import edu.vt.beacon.editor.about.AboutDialog;
import edu.vt.beacon.editor.action.Action;
import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.document.DocumentState;
import edu.vt.beacon.editor.swing.platform.PlatformMenuItem;
import edu.vt.beacon.graph.glyph.AbstractGlyph;
import edu.vt.beacon.graph.glyph.arc.AbstractArc;
import edu.vt.beacon.graph.glyph.node.AbstractNode;
import edu.vt.beacon.io.FileManager;
import edu.vt.beacon.layer.Layer;
import edu.vt.beacon.map.Map;
import edu.vt.beacon.simulation.model.InitialValues;
import edu.vt.beacon.simulation.model.SimpleConditions;
import edu.vt.beacon.simulation.model.containers.NetworkContainer;
import edu.vt.beacon.simulation.*;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;
import javax.swing.JOptionPane;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

public class SimulateHandler implements ActionHandler {

    private static final SimulateHandler instance_ = new SimulateHandler();

    public static SimulateHandler getInstance() {
        return instance_;
    }

    public static void setBackColors(Document doc, HashMap<String, Boolean>  states){
        Map map = doc.getPathway().getMap();


        for (Layer l : map.getLayers())
            for (AbstractGlyph g : l.getGlyphs()) {
                if (g instanceof AbstractNode && states.get(g.getId()) !=null ) {
                    if (states.get(g.getId()) )
                        g.setBackgroundColor(new Color(209, 255, 215));
                    else
                        g.setBackgroundColor(new Color(255, 209, 209));
                }

            }
        doc.refresh();
        new DocumentState(doc, "Simulation Coloring", false);
//        try {
////            TimeUnit.SECONDS.sleep(2);
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        doc.undo();
//        doc.refresh();


    }
    @Override
    public void handle(Action action, ActionEvent event) {
        switch (action.getType()) {

            case SIMULATE_SHOW_HIDE:

                PlatformMenuItem menuItem = (PlatformMenuItem)event.getSource();
                if (menuItem.getText().equals("Show Simulation"))
                {
                    menuItem.setText("Hide Simulation");
                    action.getDocument().getFrame().showSimulation();
                }
                else
                {
                    menuItem.setText("Show Simulation");
                    action.getDocument().getFrame().hideSimulation();
                }


                break;
            case SIMULATE_EXPORT:
                try{
                FileManager.export_sbml(action.getDocument(), action.getDocument().getFile().getPath(), ExportType.sbml);}
                catch (Exception e){
                    JOptionPane.showMessageDialog(null,  " cannot load the SBML file ", "InfoBox: " , JOptionPane.ERROR_MESSAGE);

                    System.out.println(e);
                    for(StackTraceElement el:e.getStackTrace())
                        System.out.println(el);
                }
                break;
            case SIMULATE_LOAD_SBML:

                try {
//                    SBMLReader reader = new SBMLReader();
                    String full_filename = action.getDocument().getFile().getPath() + '.' + ExportType.sbml.name();
                    String filename = action.getDocument().getFile().getName() + '.' + ExportType.sbml.name();
//                    System.out.println(full_filename);
//                    SBMLDocument doc =  reader.readSBML(full_filename);
                    File f = new File(full_filename);
                    NetworkContainer net = Simulator.readNetworkFromSBML(f);
                    JOptionPane.showMessageDialog(null, filename+ " is loaded", "InfoBox: " , JOptionPane.INFORMATION_MESSAGE);
                }
                catch (Exception e) {
                    JOptionPane.showMessageDialog(null,  " cannot load the SBML file ", "InfoBox: " , JOptionPane.ERROR_MESSAGE);
                    System.out.println(e);
                    for(StackTraceElement el:e.getStackTrace())
                        System.out.println(el);
                }

                break;

            case SIMULATE_SIMULATE:
                try {


                    int maxStarts = 1;
                    int maxTransitions = 10;

                    Document doc = action.getDocument();
                    String full_filename = action.getDocument().getFile().getPath() + '.' + ExportType.sbml.name();
                    String filename = action.getDocument().getFile().getName() + '.' + ExportType.sbml.name();
                    System.out.println(full_filename);
                    File f = new File(full_filename);
                    NetworkContainer net = Simulator.readNetworkFromSBML(f);
                    InitialValues initConditions = new InitialValues();

                    HashMap<Integer, Integer> geneToColIndices = new HashMap<Integer, Integer>();
                    HashMap<Integer, Integer> colToGeneIndices = new HashMap<Integer, Integer>();
                    HashMap<Integer, String> geneNames = net.varNames;
                    HashMap<String, Boolean> results = new HashMap<String, Boolean>();

                    Vector<Integer> indices = new Vector<Integer>(geneNames.keySet());
                    for(int i = 0; i < indices.size(); i++) {
                        geneToColIndices.put(indices.elementAt(i), i);
                        colToGeneIndices.put(i, indices.elementAt(i));
                    }
                    SimpleConditions cond = new SimpleConditions(null, geneToColIndices, geneNames);

                    for(int i = 0; i < geneNames.size(); i++)
                        cond.geneValues.put(i, true);

                    initConditions.getConditions().add(cond);
                    SimulationThread simThread = new SimulationThread(net, initConditions,
                            maxStarts,
                            maxTransitions,
                            null,
                            false);

                    Vector<Vector<boolean[]>>  results_vector = simThread.call();
                    for (Vector<boolean[]> rrr: results_vector){

                        System.out.println("-----");
                        for (boolean[] rr :rrr ) {
                            System.out.println("*");
                            for (int i =0; i< rr.length; i++) {
                                System.out.print("" + geneNames.get(i) + " " + rr[i] + "\n");
                                results.put(geneNames.get(i), rr[i]);
                            }
                        }
                    }
                    setBackColors(doc, results);
                }catch (Exception e) {
                    JOptionPane.showMessageDialog(null,  " cannot load the SBML file ", "InfoBox: " , JOptionPane.ERROR_MESSAGE);

                    System.out.println(e.getMessage());
                    for(StackTraceElement el:e.getStackTrace())
                        System.out.println(el);
                }

                break;

            default:
                throw new IllegalStateException("missing action type case");
        }

    }

}

class SimulationSucceededEvent implements EventHandler<WorkerStateEvent> {

//    @SuppressWarnings("unchecked")
//    @Override
    public void handle(WorkerStateEvent event) {
        JOptionPane.showMessageDialog(null,  " Simulation done", "InfoBox: " , JOptionPane.INFORMATION_MESSAGE);

    }
}