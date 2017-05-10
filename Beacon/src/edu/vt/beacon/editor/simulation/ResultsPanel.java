package edu.vt.beacon.editor.simulation;

import edu.vt.beacon.graph.glyph.node.AbstractNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by marakeby on 5/4/17.
 */
public class ResultsPanel extends JPanel implements ActionListener {

    private static final String AC_NEXT= "NEXT";
    private static final String AC_PREV= "PREV";

    private ResultsTable resultsTable_;
    private SimulationPanel simulationPanel_;
    private int index;
    private Vector<Vector<boolean[]>> results_;
    private HashMap<Integer, String> geneIDs_;
    private HashMap<Integer, String> geneNames_;

    private JButton next;
    private JButton prev;

    String[] colNames =  { "Node", "State" };

    public ResultsPanel(SimulationPanel sp){
        super();
        setBackground(Color.white);
        setLayout(new BorderLayout());
        JLabel headerLabel = new JLabel("Results");
        add(headerLabel, BorderLayout.NORTH);
//        add( new JPanel().add(headerLabel), BorderLayout.NORTH);
        JPanel btnPanel = new JPanel();
        next = new JButton("<");
        prev = new JButton(">");
        next.setActionCommand("NEXT");
        prev.setActionCommand("PREV");
        next.addActionListener(this);
        prev.addActionListener(this);
        btnPanel.add(next);
        btnPanel.add(prev);

        setLayout(new BorderLayout());

        simulationPanel_ = sp;
        buildResultsTable();
        add(btnPanel, BorderLayout.PAGE_END);

    }

    private void buildResultsTable() {

        ArrayList<AbstractNode> inputNodes = simulationPanel_.getDocument().getPathway().getMap().getAllNodes();
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

        ResultsTableModel dm = new ResultsTableModel(null, null, null, colNames);
        resultsTable_ = new ResultsTable(dm, simulationPanel_);
        resultsTable_.setRowSelectionAllowed(false);
        resultsTable_.setSelectionMode(0);

        JScrollPane scroll = new JScrollPane(resultsTable_);
        add(scroll, BorderLayout.CENTER);
        setMinimumSize(new Dimension(400,100));

    }

    public void setResults(Vector<Vector<boolean[]>> results, HashMap<Integer, String> geneIDs, HashMap<Integer, String> geneNames ){
        index= 0;
        results_ = results;
        geneIDs_ = geneIDs;
        geneNames_= geneNames;
        System.out.println("number of attractors" + results.size());
        if (results.size()<=1)
            next.setEnabled(false);
            prev.setEnabled(false);

        ResultsTableModel resultsTableMode  = new ResultsTableModel(results.get(index), geneIDs, geneNames, colNames);
        resultsTable_.setModel(resultsTableMode);
        resultsTableMode.fireTableDataChanged();
    }

    public void actionPerformed(ActionEvent e) {
        // Actions independent of glyphs selected
        String actionCommand = e.getActionCommand();
        System.out.println("command " + actionCommand);
        if (actionCommand.equals(AC_NEXT))
            index++;

        else  if (actionCommand.equals(AC_PREV))
        {
            index--;
        }
        ResultsTableModel resultsTableMode  = new ResultsTableModel(results_.get(index), geneIDs_, geneNames_, colNames);
        resultsTable_.setModel(resultsTableMode);
        resultsTableMode.fireTableDataChanged();


    }

}
