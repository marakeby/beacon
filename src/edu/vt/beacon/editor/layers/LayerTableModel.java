package edu.vt.beacon.editor.layers;

import edu.vt.beacon.layer.Layer;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * Created by ppws on 1/14/16.
 */
public class LayerTableModel extends AbstractTableModel {

    private List<Layer> layers;

    public LayerTableModel(List<Layer> layers) {
        this.layers = layers;
    }

    @Override
    public int getRowCount() {
        return (layers == null) ? 0 : layers.size();
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        if (layers == null || layers.size() <= rowIndex || columnIndex > 0)
            return "";

        return layers.get(rowIndex).toString();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public void setValueAt(Object newValue, int rowIndex, int columnIndex) {
        if (layers == null || layers.size() <= rowIndex || columnIndex > 0)
            return;

        layers.get(rowIndex).setName((String) newValue);
    }
}
