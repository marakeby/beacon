package edu.vt.beacon.editor.dialog.properties;

import javax.swing.table.AbstractTableModel;
import java.util.List;


public class OrganismTableModel extends AbstractTableModel {

    private String[] columnNames = {"organisms"};
    private List<String> data ;

    public OrganismTableModel(List<String> data) {
        super();
        this.data = data;
    }


    @Override
    public int getColumnCount() {
        return columnNames.length;
//        return 1;
    }

    @Override
    public int getRowCount() {
        return data.size();
//        return 5;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        return data.get(row);
    }

    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    @Override
    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        if (col < 2) {
            return false;
        } else {
            return true;
        }
    }

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    @Override
    public void setValueAt(Object value, int row, int col) {
//        data[row][col] = value;
        data.set(row, (String)value);
        fireTableCellUpdated(row, col);
    }

    public List<String> getData() {
        return data;
    }
}
