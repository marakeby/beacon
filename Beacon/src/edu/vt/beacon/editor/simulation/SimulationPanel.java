package edu.vt.beacon.editor.simulation;

import edu.vt.beacon.editor.action.handler.ExportType;
import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.document.DocumentState;
import edu.vt.beacon.editor.swing.laf.Skinnable;
import edu.vt.beacon.graph.glyph.AbstractGlyph;
import edu.vt.beacon.graph.glyph.node.AbstractNode;
import edu.vt.beacon.graph.glyph.node.annotation.Annotation;
import edu.vt.beacon.io.FileManager;
import edu.vt.beacon.layer.Layer;
import edu.vt.beacon.map.Map;
import edu.vt.beacon.simulation.SimulationThread;
import edu.vt.beacon.simulation.Simulator;
import edu.vt.beacon.simulation.model.InitialValues;
import edu.vt.beacon.simulation.model.SimpleConditions;
import edu.vt.beacon.simulation.model.containers.NetworkContainer;
import org.sbml.jsbml.ListOf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;
import java.util.List;


public class SimulationPanel extends JPanel implements Skinnable, ActionListener {
    private static final long serialVersionUID = 1L;

    private static final String AC_SIMULATE = "SIMULATE";

    private Document document_;

    private InputsTable inputsTable_;
    private IntermediateTable intermediateTable_;
//    private ResultsTable resultsTable_;
    private ResultsPanel resultsPanel_;
    private Map selectedMap_;

    JButton addButton_;

    // FIXME complete constructor
    public SimulationPanel(Document document) {
        System.out.println("Simulation" + document);
        setLayout(new BorderLayout());

        document_ = document;
        selectedMap_ = document_.getPathway().getMap();

        buildHeaderPanel();
        buildMenuPanel();
    }


    // TODO document method
    private void buildHeaderPanel() {
        JLabel headerLabel = new JLabel("Simulation");
        headerLabel.setFont(FONT_MEDIUM.deriveFont(Font.BOLD));
        headerLabel.setForeground(COLOR_FOREGROUND);
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(COLOR_BACKGROUND);
        headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        headerPanel.add(headerLabel);
        add(headerPanel, BorderLayout.NORTH);
    }

    private void buildMenuPanel() {

        GridBagConstraints c = new GridBagConstraints();

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());
//        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputsPanel = new JPanel();
        inputsPanel.setBackground(Color.white);
        inputsPanel.setBorder(BorderFactory.createEtchedBorder());
        inputsPanel.setLayout(new BorderLayout());

        JLabel headerLabel = new JLabel("Input Nodes");
        inputsPanel.add(headerLabel, BorderLayout.NORTH);
        buildInputsTable(inputsPanel);

        JPanel intermediatePanel = new JPanel();
        intermediatePanel.setBackground(Color.white);
        intermediatePanel.setBorder(BorderFactory.createEtchedBorder());
        intermediatePanel.setLayout(new BorderLayout());
        headerLabel = new JLabel("Intermediate Nodes");
        intermediatePanel.add(headerLabel, BorderLayout.NORTH);

        buildIntermediateTable(intermediatePanel);

        c.ipady = 100;
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.gridwidth = 3;
        c.weightx = 0.0;
        c.gridwidth = 5;
        c.gridheight = 5;
//        c.fill= GridBagConstraints.HORIZONTAL;
        c.fill= GridBagConstraints.VERTICAL;

        contentPanel.add(inputsPanel, c);

        c.gridx = 6;
        c.gridy = 0;
        c.anchor = GridBagConstraints.PAGE_START;
        c.gridwidth = 3;
        c.weightx = 0.0;
        c.gridwidth = 5;
        c.gridheight = 5;
        c.fill= GridBagConstraints.HORIZONTAL;
        c.fill= GridBagConstraints.VERTICAL;

        contentPanel.add(intermediatePanel, c);

        c.gridx = 11;
        c.gridy = 0;
        c.anchor = GridBagConstraints.PAGE_START;
        c.gridwidth = 3;
        c.weightx = 0.0;
        c.gridwidth = 5;
        c.gridheight = 10;
        c.fill= GridBagConstraints.VERTICAL;

        resultsPanel_ = new ResultsPanel(this);
        contentPanel.add(resultsPanel_, c);



        addButton_ = new JButton("Simulate");
        addButton_.setActionCommand(AC_SIMULATE);
        addButton_.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(addButton_);

        c.gridx = 2;
        c.gridy = 5;
//        c.anchor = GridBagConstraints.LINE_END;
        c.anchor =  GridBagConstraints.PAGE_END; //bottom of space;
//        c.insets = new Insets(10,0,0,0);  //top padding
//        c.gridwidth = 5;   //2 columns wide
        contentPanel.add(buttonPanel, c);

        add(contentPanel, BorderLayout.CENTER);


    }

    private InputsTableModel getInputTableModel(){
        ArrayList<AbstractNode> inputNodes = document_.getPathway().getMap().getInputNodes();

        ArrayList<String> inputsID = new ArrayList<String>();
        ArrayList<String> inputsText = new ArrayList<String>();
        for (AbstractNode n : inputNodes) {
            inputsText.add(n.getLabel().getText());
            inputsID.add(n.getId());
            System.out.println(n.getLabel().getText());
        }
        HashMap<String,Boolean> preExistingSettings = new HashMap<>();

        if(inputsTable_ != null && inputsTable_.getModel() != null && inputsTable_.getModel() instanceof InputsTableModel)
        {
            Object[][] oldData = ((InputsTableModel) inputsTable_.getModel()).getData();
            // create a hash map of pre existing ids to boolean values

            int n = oldData.length;
            for(int i = 0; i < n; i++)
            {
                preExistingSettings.put((String)oldData[i][0],(Boolean) oldData[i][2]); //this is super hacky
            }
        }

        int n = inputsText.size();
        Object[][] data = new Object[n][3];
        for (int i=0; i<n ;i++) {
            data[i][0] = inputsID.get(i);
            data[i][1] = inputsText.get(i);
            if(preExistingSettings.containsKey(data[i][0]))
                data[i][2] = preExistingSettings.get(data[i][0]);
            else
                data[i][2] = true; // this initializes
        }
        String[] colNames =  { "Id", "Input", "Active" };
        InputsTableModel dm = new InputsTableModel(data, colNames);
        return dm;
    }

    private IntermediateTableModel getIntermediateTableModel(){
        ArrayList<AbstractNode> inputNodes = document_.getPathway().getMap().getInputNodes();
        ArrayList<AbstractNode> allNodes = document_.getPathway().getMap().getAllNodes();

        ArrayList<String> inputsID = new ArrayList<String>();
        ArrayList<String> inputsText = new ArrayList<String>();
        for (AbstractNode n : allNodes) {
            if(n instanceof Annotation) // annotations shouldn't be loaded as an intermediate state
                continue;
            if (inputNodes.indexOf(n)>=0)
                continue;
            inputsText.add(n.getLabel().getText());
            inputsID.add(n.getId());
            System.out.println(n.getLabel().getText());
        }

        HashMap<String,Boolean> preExistingSettings = new HashMap<>();

        if(intermediateTable_ != null && intermediateTable_.getModel() != null && intermediateTable_.getModel() instanceof IntermediateTableModel)
        {
            Object[][] oldData = ((IntermediateTableModel) intermediateTable_.getModel()).getData();
            // create a hash map of pre existing ids to boolean values

            int n = oldData.length;
            for(int i = 0; i < n; i++)
            {
                if (!oldData[i][2].equals('x')) {
                    preExistingSettings.put((String)oldData[i][0],(Boolean) oldData[i][2]); //this is super hacky
                }
            }
        }

        int n = inputsText.size();
        Object[][] data = new Object[n][3];
        for (int i=0; i<n ;i++) {
            data[i][0] = inputsID.get(i);
            data[i][1] = inputsText.get(i);
            if(preExistingSettings.containsKey(data[i][0]))
                data[i][2] = preExistingSettings.get(data[i][0]);
            else
                data[i][2] = 'x'; // this initializes
        }
        String[] colNames =  { "Id", "Input", "Active" };
        IntermediateTableModel dm = new IntermediateTableModel(data, colNames);
        return dm;
    }

    private void buildInputsTable(JPanel menuPanel) {

        InputsTableModel dm = getInputTableModel();
        inputsTable_ = new InputsTable(dm);
        inputsTable_.setRowSelectionAllowed(false);
        inputsTable_.setSelectionMode(0);
        inputsTable_.getColumnModel().getColumn(1).setPreferredWidth(50);
        inputsTable_.getColumnModel().getColumn(0).setPreferredWidth(100);
        JScrollPane scroll = new JScrollPane(inputsTable_);
        menuPanel.add(scroll);
        menuPanel.setMinimumSize(new Dimension(300,100));
//        setSize(400, 100);
        setVisible(true);

    }

    private void buildIntermediateTable(JPanel menuPanel) {

        IntermediateTableModel dm = getIntermediateTableModel();
//        String[] colNames =  { "Id", "Input", "Active" };
//        InputsTableModel dm = new InputsTableModel(null, colNames);
        intermediateTable_ = new IntermediateTable(dm);
        intermediateTable_.setRowSelectionAllowed(false);
        intermediateTable_.setSelectionMode(0);
        intermediateTable_.getColumnModel().getColumn(1).setPreferredWidth(50);
        intermediateTable_.getColumnModel().getColumn(0).setPreferredWidth(100);
        JScrollPane scroll = new JScrollPane(intermediateTable_);
        menuPanel.add(scroll);
        menuPanel.setMinimumSize(new Dimension(300,100));
//        setSize(400, 100);
        setVisible(true);

    }

//    private void buildResultsTable(JPanel resultsPanel) {
//
//        ArrayList<AbstractNode> inputNodes = document_.getPathway().getMap().getAllNodes();
//        HashMap<Integer, String> nodeNames = new HashMap<Integer, String> ();
//
//        ArrayList<String> inputs = new ArrayList<String>();
//        int i =0;
//        int s = inputNodes.size();
//        boolean[] state= new boolean[s];
//        for (AbstractNode n : inputNodes) {
//            inputs.add(n.getLabel().getText());
//            nodeNames.put(i, n.getLabel().getText());
//            state[i] = true;
//            i++;
//        }
//
//        Vector<boolean[]> states = new Vector<boolean[]> ();
//        states.add(state);
//
//        String[] colNames =  { "Node", "State" };
//
//        ResultsTableModel dm = new ResultsTableModel(null, null, null, colNames);
//        resultsTable_ = new ResultsTable(dm, this);
//        resultsTable_.setRowSelectionAllowed(false);
//        resultsTable_.setSelectionMode(0);
//
//        JScrollPane scroll = new JScrollPane(resultsTable_);
//        resultsPanel.add(scroll);
//        resultsPanel.setMinimumSize(new Dimension(300,100));
//
//    }

    public void refresh() {
        System.out.println("refresh..........");
        selectedMap_ = document_.getBrowserMenu().getSelectedMap();

        String[] colNames =  { "Node", "State" };
        ResultsTableModel resultsTableMode = new ResultsTableModel(null, null,null,  colNames);
        InputsTableModel inputsTableMode = getInputTableModel();
        IntermediateTableModel intermediateTableMode = getIntermediateTableModel();
        inputsTable_.setModel(inputsTableMode);
//        resultsTable_.setModel(resultsTableMode);
        intermediateTable_.setModel(intermediateTableMode);
        resultsTableMode.fireTableDataChanged();
        inputsTableMode.fireTableDataChanged();
        intermediateTableMode.fireTableDataChanged();
        repaint();
    }

    public void setDocument(Document document) {
        if (document_!=document) {
            document_ = document;
            selectedMap_ = document_.getPathway().getMap();
            refresh();
        }
    }

    public Document getDocument(){
        return document_;
    }



    public void actionPerformed(ActionEvent e) {
        // Actions independent of glyphs selected
        String actionCommand = e.getActionCommand();
        System.out.println("command " + actionCommand);
        if (actionCommand.equals(AC_SIMULATE))
            simulate();


    }
    private void simulate(){
        try {


            int maxStarts = 1;
            int maxTransitions = 100;

            String[] ogName = document_.getFile().getPath().split("[.]");
            String full_filename = ogName[0] + '.' + ExportType.sbml.name();
            FileManager.export_sbml(document_, document_.getFile().getPath(), ExportType.sbml);
            System.out.println(full_filename);
            File f = new File(full_filename);
            refresh();
            NetworkContainer net = Simulator.readNetworkFromSBML(f);


            HashMap<Integer, Integer> geneToColIndices = new HashMap<Integer, Integer>();
            HashMap<Integer, Integer> colToGeneIndices = new HashMap<Integer, Integer>();
            HashMap<Integer, String> geneIDs = net.varNames;
            HashMap<Integer, String> geneText = net.varText;

            //geneIDs.values().removeIf(val -> "not".equalsIgnoreCase(val) || "or".equalsIgnoreCase(val) || "and".equalsIgnoreCase(val)); // dont allow logical operators in the set
           // geneText.values().removeIf(val -> "not".equalsIgnoreCase(val) || "or".equalsIgnoreCase(val) || "and".equalsIgnoreCase(val)); // dont allow logical operators in the set

            //I'm assuming the gene id and gene text have the same indexes mappings
            HashSet<Integer> invalidIndexes = new HashSet<Integer>();
            for(int i  = 0; i < geneText.size();i++){
                if(geneText.get(i).equalsIgnoreCase("not") || geneText.get(i).equalsIgnoreCase("or") || geneText.get(i).equalsIgnoreCase("and")){
                    invalidIndexes.add(i);
                }

            }


            HashMap<String, Boolean> results = new HashMap<String, Boolean>();

            Vector<Integer> indices = new Vector<Integer>(geneIDs.keySet());
            for(int i = 0; i < indices.size(); i++) {
                geneToColIndices.put(indices.elementAt(i), i);
                colToGeneIndices.put(i, indices.elementAt(i));
            }

            InitialValues initConditions = getInitialCondition(geneToColIndices, geneIDs);

            HashMap<Integer,Boolean> fixedGenes = getFixedGenes(geneIDs);
            SimulationThread simThread = new SimulationThread(net, initConditions,
                    maxStarts,
                    maxTransitions,
                    fixedGenes,
                    false);

            Vector<Vector<boolean[]>>  results_vector = simThread.call();
            for (Vector<boolean[]> rrr: results_vector){

                System.out.println("-----");
                for (boolean[] rr :rrr ) {
                    System.out.println("*");
                    for (int i =0; i< rr.length; i++) { // this inner for loop makes me think there is only one set of booleans
                        System.out.print("" + geneText.get(i) + " " + rr[i] + "\n");
                        results.put(geneIDs.get(i), rr[i]);
                    }
                }
            }

            for(Integer i : invalidIndexes){
                geneIDs.remove(i);
                geneText.remove(i);
            }

            ArrayList<Boolean> booleans = new ArrayList<Boolean>();

            int totalIndex = 0;
            for (Vector<boolean[]> rrr: results_vector){
                for (boolean[] rr :rrr ) {

                    for (int i =0; i< rr.length; i++) { // this inner for loop makes me think there is only one set of booleans
                        if(invalidIndexes.contains(totalIndex) == false)
                            booleans.add(rr[i]);
                        totalIndex++;
                    }
                }
            }

            boolean[] actualBooleans = new boolean[booleans.size()];
            for(int i = 0; i < booleans.size();i++){
                actualBooleans[i] = booleans.get(i);
            }

            results_vector.firstElement().remove(0);
            results_vector.firstElement().add(actualBooleans);

            Coloring.setBackColors(document_, results); // set graph node colors
//            populateResultsTable(results_vector.get(0),geneIDs, geneText );
//            resultsPanel_.setResults(results_vector.get(0),geneIDs, geneText );
            resultsPanel_.setResults(results_vector,geneIDs, geneText );
        }catch (Exception e) {
            JOptionPane.showMessageDialog(null,  " cannot load the SBML file ", "InfoBox: " , JOptionPane.ERROR_MESSAGE);

            System.out.println(e.getMessage());
            for(StackTraceElement el:e.getStackTrace())
                System.out.println(el);
        }

    }

    private HashMap<Integer,Boolean> getFixedGenes(HashMap<Integer, String> geneNames){
        HashMap<Integer,Boolean> fixedGenes = new HashMap<Integer,Boolean>();
        InputsTableModel model = (InputsTableModel)inputsTable_.getModel();
        Object[][] data = model.getData();
        String id;
        Boolean value;
        for (int i =0; i < data.length; i++)
        {
            id = data[i][0].toString();
            value = (Boolean)data[i][2];
//            System.out.println(id);//id
//            System.out.println(value);//value
            //get the key given the id
            int index= -1;
            for (java.util.Map.Entry<Integer, String> entry : geneNames.entrySet()) {
                if (entry.getValue().equals(id)) {
                    index =(int)entry.getKey();
                }
            }
            fixedGenes.put(index, value);

        }

        IntermediateTableModel model2 = (IntermediateTableModel)intermediateTable_.getModel();
        data = model2.getData();

        Object value2;
        for (int i =0; i < data.length; i++)
        {
            id = data[i][0].toString();
            value2 = data[i][2];
            if (value2.equals(true)|| value2.equals(false)) {
//            System.out.println(id);//id
//            System.out.println(value);//value
                //get the key given the id
                int index = -1;
                for (java.util.Map.Entry<Integer, String> entry : geneNames.entrySet()) {
                    if (entry.getValue().equals(id)) {
                        index = (int) entry.getKey();
                    }
                }
                fixedGenes.put(index, (Boolean)value2);
            }

        }

        return fixedGenes;
    }
    private InitialValues getInitialCondition(HashMap<Integer, Integer> geneToColIndices, HashMap<Integer, String> geneNames){
        SimpleConditions cond = new SimpleConditions(null, geneToColIndices, geneNames);

        for(int i = 0; i < geneNames.size(); i++)
            cond.geneValues.put(i, true);

        InputsTableModel model = (InputsTableModel)inputsTable_.getModel();
        Object[][] data = model.getData();
        String id;
        Boolean value;
        for (int i =0; i < data.length; i++)
        {
            id = data[i][0].toString();
            value = (Boolean)data[i][2];
            //get the key given the id
            int index= -1;
            for (java.util.Map.Entry<Integer, String> entry : geneNames.entrySet()) {
                if (entry.getValue().equals(id)) {
                    index =(int)entry.getKey();
                }
            }
            cond.geneValues.put(index, value);

        }

        InitialValues initConditions = new InitialValues();
        initConditions.getConditions().add(cond);
        return initConditions;
    }
//    private void populateResultsTable(Vector<boolean[]> results, HashMap<Integer, String> geneIDs, HashMap<Integer, String> geneNames ){
//
//        String[] colNames =  { "Node", "State" };
//        ResultsTableModel resultsTableMode  = new ResultsTableModel(results, geneIDs, geneNames, colNames);
//        resultsTable_.setModel(resultsTableMode);
//        resultsTableMode.fireTableDataChanged();
//    }
//    public static void setBackColors(Document doc, HashMap<String, Boolean>  states){
//        if (states ==null)
//            return;
//        Map map = doc.getPathway().getMap();
//
//
//        for (Layer l : map.getLayers())
//            for (AbstractGlyph g : l.getGlyphs()) {
//                if (g instanceof AbstractNode && states.get(g.getId()) !=null ) {
//                    if (states.get(g.getId()) )
//                        g.setBackgroundColor(new Color(209, 255, 215));
//                    else
//                        g.setBackgroundColor(new Color(255, 209, 209));
//                }
//
//            }
//        doc.refresh();
//        new DocumentState(doc, "Simulation Coloring", false);
//
//
//    }

}
