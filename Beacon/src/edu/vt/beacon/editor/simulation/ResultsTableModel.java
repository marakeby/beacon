package edu.vt.beacon.editor.simulation;

import javax.swing.table.AbstractTableModel;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by marakeby on 4/25/17.
 */
public class ResultsTableModel extends AbstractTableModel {

    private String[] columnNames;
    private HashMap<Integer, String> nodeNames;
    private Vector<boolean[]> states;

    public ResultsTableModel(Vector<boolean[]> states,HashMap<Integer, String> nodeNames , String[] columnNames) {
        this.states = states;
        this.nodeNames = nodeNames;
        this.columnNames = columnNames;
    }

    public int getColumnCount() {
        if (states==null)
            return 2;
        return states.size()+1;
    }

    public int getRowCount() {
        if (states==null)
            return 0;
        return states.get(0).length;
    }

    public String getColumnName(int col) {
        return columnNames[1];
    }

    public Object getValueAt(int row, int col) {
        if (col==0)
            return nodeNames.get(row);

        return states.get(col-1)[row];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

//    public void setValueAt(Object value, int row, int col) {
//         data[row][col] = value;
//    }


}
