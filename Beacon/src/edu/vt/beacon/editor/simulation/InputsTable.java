package edu.vt.beacon.editor.simulation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Created by marakeby on 4/25/17.
 */
public class InputsTable extends JTable {

    private InputsTableModel dm;
    public InputsTable(InputsTableModel dm){
        super(dm);
        this.dm = dm;
        this.addMouseListener(new MouseHandler());
    }
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int rowIndex,
                                     int columnIndex) {
        JComponent component = (JComponent) super.prepareRenderer(renderer, rowIndex, columnIndex);

        if(getValueAt(rowIndex, 1).equals( false) && columnIndex == 1) {
            component.setBackground(Color.RED);
            setToolTipText("Click to activate");
        } else if(getValueAt(rowIndex, 1).equals( true) && columnIndex == 1){
            component.setBackground(Color.GREEN);
            setToolTipText("Click to deactivate");
        }
        else
            component.setBackground(Color.white);

        return component;
    }

}

class MouseHandler extends java.awt.event.MouseAdapter{
    @Override
    public void mouseClicked(java.awt.event.MouseEvent evt) {
        JTable t = (JTable)evt.getSource();
        InputsTableModel m = (InputsTableModel)t.getModel();
        int row = t.rowAtPoint(evt.getPoint());
        int col = t.columnAtPoint(evt.getPoint());
        if (row >= 0 && col == 1) {
            boolean v = (boolean)m.getValueAt(row,col);
            System.out.println(v);
            m.setValueAt(!v, row, col);
            v = (boolean)m.getValueAt(row,col);
            System.out.println(v);
            m.fireTableDataChanged();
        }
    }
}