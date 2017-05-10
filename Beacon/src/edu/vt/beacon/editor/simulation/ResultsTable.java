package edu.vt.beacon.editor.simulation;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.HashMap;


/**
 * Created by marakeby on 4/26/17.
 */
public class ResultsTable extends JTable {

    private ResultsTableModel dm;
    private SimulationPanel parent_;
    public ResultsTable(ResultsTableModel dm, SimulationPanel parent){
        super(dm);
        this.dm = dm;
        parent_ = parent;
//        this.addMouseListener(new ResultsMouseHandler());
        this.getTableHeader().addMouseListener(new ResultsMouseHandler());
    }
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int rowIndex,
                                     int columnIndex) {
        JComponent component = (JComponent) super.prepareRenderer(renderer, rowIndex, columnIndex);

        if(getValueAt(rowIndex, columnIndex).equals( false)) {
            component.setBackground(Color.RED);
        } else if(getValueAt(rowIndex, columnIndex).equals( true) ){
            component.setBackground(Color.GREEN);
        }
        else
            component.setBackground(Color.white);

        return component;
    }
    public SimulationPanel getSimulationPanel(){
        return parent_;
    }

}

class ResultsMouseHandler extends java.awt.event.MouseAdapter{
//    @Override
//    public void mouseClicked(java.awt.event.MouseEvent evt) {
//        JTable t = (JTable)evt.getSource();
//        IntermediateTableModel m = (IntermediateTableModel)t.getModel();
//        int row = t.rowAtPoint(evt.getPoint());
//        int col = t.columnAtPoint(evt.getPoint());
//        if (row >= 0 && col == 1) {
//            Object v = m.getValueAt(row,col);
//            System.out.println(v);
//            if (v.equals(true))
//                m.setValueAt(false, row, col);
//            if (v.equals(false))
//                m.setValueAt('x', row, col);
//            if (v.equals('x'))
//                m.setValueAt(true, row, col);
//            m.fireTableDataChanged();
//        }
//    }
    @Override
    public void mouseClicked(java.awt.event.MouseEvent e) {
        JTableHeader header = (JTableHeader)e.getSource();
        ResultsTable table = (ResultsTable)header.getTable();
        int col = table.columnAtPoint(e.getPoint());
        String name = table.getColumnName(col);
        System.out.println("Column index selected " + col + " " + name);

        HashMap<String, Boolean> results = new HashMap<String, Boolean>();
        ResultsTableModel model = (ResultsTableModel) table.getModel();

        HashMap<Integer, String> ids = model.getNodeIds();

        for(int i =1 ; i < ids.size(); i++){
            String id = ids.get(i);
            Boolean value = (Boolean)model.getValueAt(i, col);
            results.put(id, value);
        }

        Coloring.setBackColors(table.getSimulationPanel().getDocument(), results);

    }
}