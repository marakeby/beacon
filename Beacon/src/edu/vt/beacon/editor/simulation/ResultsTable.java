package edu.vt.beacon.editor.simulation;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Created by marakeby on 4/26/17.
 */
public class ResultsTable extends JTable {

    private ResultsTableModel dm;
    public ResultsTable(ResultsTableModel dm){
        super(dm);
        this.dm = dm;
//        this.addMouseListener(new MouseHandler());
    }
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int rowIndex,
                                     int columnIndex) {
        JComponent component = (JComponent) super.prepareRenderer(renderer, rowIndex, columnIndex);

        if(getValueAt(rowIndex, 1).equals( false)) {
            component.setBackground(Color.RED);
        } else if(getValueAt(rowIndex, 1).equals( true) ){
            component.setBackground(Color.GREEN);
        }
        else
            component.setBackground(Color.white);

        return component;
    }

}
