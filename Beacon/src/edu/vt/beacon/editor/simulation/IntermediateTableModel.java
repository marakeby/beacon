package edu.vt.beacon.editor.simulation;

import javax.swing.table.AbstractTableModel;

/**
 * Created by marakeby on 4/25/17.
 */
public class IntermediateTableModel extends AbstractTableModel {

    private String[] columnNames;
    private Object[][] data; //3 columns, 0 id, 1 name, 2 booleanvalue

    public IntermediateTableModel(Object[][] data, String[] columnNames) {
        this.data = data;
        this.columnNames = columnNames;
    }

    public int getColumnCount() {
        return columnNames.length-1; //dont show the id
    }

    public int getRowCount() {
        return data.length;
    }

    public String getColumnName(int col) {
        return columnNames[col+1];
    }

    public Object getValueAt(int row, int col) {
        return data[row][col+1];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void setValueAt(Object value, int row, int col) {
         data[row][col+1] = value;
    }
    public Object[][] getData(){
        return data;
    }


}
