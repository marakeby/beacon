package edu.vt.beacon.editor.simulation;

import edu.vt.beacon.editor.action.handler.ExportType;
import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.document.DocumentState;
import edu.vt.beacon.editor.swing.laf.Skinnable;
import edu.vt.beacon.graph.glyph.AbstractGlyph;
import edu.vt.beacon.graph.glyph.node.AbstractNode;
import edu.vt.beacon.layer.Layer;
import edu.vt.beacon.map.Map;
import edu.vt.beacon.simulation.SimulationThread;
import edu.vt.beacon.simulation.Simulator;
import edu.vt.beacon.simulation.model.InitialValues;
import edu.vt.beacon.simulation.model.SimpleConditions;
import edu.vt.beacon.simulation.model.containers.NetworkContainer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;


public class SimulationPanel extends JPanel implements Skinnable, ActionListener {
    private static final long serialVersionUID = 1L;

    private static final String AC_SIMULATE = "SIMULATE";

    private Document document_;

    private InputsTable inputsTable_;
    private ResultsTable resultsTable_;

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
//        System.out.println("Simulation" );
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
        buildInputsTable(inputsPanel);

        JPanel resultsPanel = new JPanel();
        resultsPanel.setBackground(Color.white);
//        resultsPanel.setBorder(BorderFactory.createEtchedBorder());
        resultsPanel.setLayout(new BorderLayout());
        buildResultsTable(resultsPanel);


        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.gridwidth = 3;
        c.weightx = 0.0;
        c.gridwidth = 5;
        c.gridheight = 5;
        c.fill= GridBagConstraints.HORIZONTAL;

        contentPanel.add(inputsPanel, c);

        c.gridx = 6;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.gridwidth = 3;
        c.weightx = 0.0;
        c.gridwidth = 5;
        c.gridheight = 5;
        c.fill= GridBagConstraints.HORIZONTAL;

        contentPanel.add(resultsPanel, c);



        addButton_ = new JButton("Simulate");
        addButton_.setActionCommand(AC_SIMULATE);
        addButton_.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(addButton_);

        c.gridx = 2;
        c.gridy = 6;
//        c.anchor = GridBagConstraints.LINE_END;
        c.anchor =  GridBagConstraints.PAGE_END; //bottom of space;
        c.insets = new Insets(10,0,0,0);  //top padding
        c.gridwidth = 5;   //2 columns wide
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

        int n = inputsText.size();
        Object[][] data = new Object[n][3];
        for (int i=0; i<n ;i++) {
            data[i][0] = inputsID.get(i);
            data[i][1] = inputsText.get(i);
            data[i][2] = true;
        }
        String[] colNames =  { "Id", "Input", "Active" };
        InputsTableModel dm = new InputsTableModel(data, colNames);
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
        menuPanel.setMinimumSize(new Dimension(500,100));
//        setSize(400, 100);
        setVisible(true);

    }

    private void buildResultsTable(JPanel resultsPanel) {

        ArrayList<AbstractNode> inputNodes = document_.getPathway().getMap().getAllNodes();
        HashMap<Integer, String> nodeNames = new HashMap<Integer, String> ();

        ArrayList<String> inputs = new ArrayList<String>();
        int i =0;
        int s = inputNodes.size();
        boolean[] state= new boolean[s];
        for (AbstractNode n : inputNodes) {
            inputs.add(n.getLabel().getText());
            nodeNames.put(i, n.getLabel().getText());
            state[i] = true;
            i++;
        }

        Vector<boolean[]> states = new Vector<boolean[]> ();
        states.add(state);

        String[] colNames =  { "Node", "State" };

        ResultsTableModel dm = new ResultsTableModel(null, null, colNames);
        resultsTable_ = new ResultsTable(dm);
        resultsTable_.setRowSelectionAllowed(false);
        resultsTable_.setSelectionMode(0);

        JScrollPane scroll = new JScrollPane(resultsTable_);
        resultsPanel.add(scroll);
        resultsPanel.setMinimumSize(new Dimension(500,100));

    }

    public void refresh() {
        System.out.println("refresh..........");
        selectedMap_ = document_.getBrowserMenu().getSelectedMap();

        String[] colNames =  { "Node", "State" };
        ResultsTableModel resultsTableMode = new ResultsTableModel(null, null, colNames);
        InputsTableModel inputsTableMode = getInputTableModel();
        inputsTable_.setModel(inputsTableMode);
        resultsTable_.setModel(resultsTableMode);
        resultsTableMode.fireTableDataChanged();
        inputsTableMode.fireTableDataChanged();
        repaint();
    }

    public void setDocument(Document document) {
        if (document_!=document) {
            document_ = document;
            selectedMap_ = document_.getPathway().getMap();
            refresh();
        }
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

            String full_filename = document_.getFile().getPath() + '.' + ExportType.sbml.name();
            String filename = document_.getFile().getName() + '.' + ExportType.sbml.name();
            System.out.println(full_filename);
            File f = new File(full_filename);
            NetworkContainer net = Simulator.readNetworkFromSBML(f);


            HashMap<Integer, Integer> geneToColIndices = new HashMap<Integer, Integer>();
            HashMap<Integer, Integer> colToGeneIndices = new HashMap<Integer, Integer>();
            HashMap<Integer, String> geneNames = net.varNames;
            HashMap<Integer, String> geneText = net.varText;

            HashMap<String, Boolean> results = new HashMap<String, Boolean>();

            Vector<Integer> indices = new Vector<Integer>(geneNames.keySet());
            for(int i = 0; i < indices.size(); i++) {
                geneToColIndices.put(indices.elementAt(i), i);
                colToGeneIndices.put(i, indices.elementAt(i));
            }

            InitialValues initConditions = getInitialCondition(geneToColIndices, geneNames);

            HashMap<Integer,Boolean> fixedGenes = getFixedGenes(geneNames);
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
                    for (int i =0; i< rr.length; i++) {
                        System.out.print("" + geneNames.get(i) + " " + rr[i] + "\n");
                        results.put(geneNames.get(i), rr[i]);
                    }
                }
            }
            setBackColors(document_, results);
            populateResultsTable(results_vector.get(0),geneText );
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
//            System.out.println(id);//id
//            System.out.println(value);//value
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
    private void populateResultsTable(Vector<boolean[]> results, HashMap<Integer, String> geneNames ){

        String[] colNames =  { "Node", "State" };
        ResultsTableModel resultsTableMode  = new ResultsTableModel(results, geneNames, colNames);
        resultsTable_.setModel(resultsTableMode);
        resultsTableMode.fireTableDataChanged();
//        resultsTable_.updateUI();
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


    }

}
