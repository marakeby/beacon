package edu.vt.beacon.editor.simulation;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Created by marakeby on 4/25/17.
 */
public class IntermediateTable extends JTable {

    private IntermediateTableModel dm;
    public IntermediateTable(IntermediateTableModel dm){
        super(dm);
        this.dm = dm;
        this.addMouseListener(new IntermediateMouseHandler());
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
        else if(getValueAt(rowIndex, 1).equals( 'x') && columnIndex == 1) {
            component.setBackground(Color.GRAY);
            setToolTipText("Click to activate");
        }
        else
            component.setBackground(Color.white);

        return component;
    }

}

class IntermediateMouseHandler extends java.awt.event.MouseAdapter{
    @Override
    public void mouseClicked(java.awt.event.MouseEvent evt) {
        JTable t = (JTable)evt.getSource();
        IntermediateTableModel m = (IntermediateTableModel)t.getModel();
        int row = t.rowAtPoint(evt.getPoint());
        int col = t.columnAtPoint(evt.getPoint());
        if (row >= 0 && col == 1) {
            Object v = m.getValueAt(row,col);
            System.out.println(v);
            if (v.equals(true))
                m.setValueAt(false, row, col);
            if (v.equals(false))
                m.setValueAt('x', row, col);
            if (v.equals('x'))
                m.setValueAt(true, row, col);
            m.fireTableDataChanged();
        }
    }
}