package edu.vt.beacon.editor.dialog.legend;

import edu.vt.beacon.editor.util.LegendManager;

import javax.swing.table.AbstractTableModel;

/**
 * Created by ppws on 3/25/16.
 */
public class LegendTableModel extends AbstractTableModel {

    private LegendManager legendManager;

    public LegendTableModel(LegendManager legendManager) {
        this.legendManager = legendManager;
    }

    public int getColumnCount() {
        return 2;
    }

    public int getRowCount() {
        if (legendManager == null || legendManager.getColorOrdering() == null)
            return 0;

        return legendManager.getColorOrdering().size();
    }

    public String getColumnName(int col) {
        if (col == 0)
            return "Color";

        if (col == 1)
            return "Description";

        return "Unknown";
    }

    public Object getValueAt(int row, int col) {
        if (col == 0)
            return legendManager.getColorOrdering().get(row);

        if (col == 1)
            return legendManager.getColorDescription(legendManager.getColorOrdering().get(row));

        return null;
    }

    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    public boolean isCellEditable(int row, int col) {
        if (col < 1)
            return false;

        return true;
    }

    public void setValueAt(Object value, int row, int col) {
        if (col == 0)
            return;

        legendManager.setColorDescription(legendManager.getColorOrdering().get(row), (String) value);
        fireTableCellUpdated(row, col);
    }

}
