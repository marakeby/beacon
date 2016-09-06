package edu.vt.beacon.editor.dialog;

import edu.vt.beacon.editor.resources.icons.IconType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by ppws on 5/16/16.
 */
public class GeneLinkCellEditor extends DefaultCellEditor {

    protected JButton button;

    public GeneLinkCellEditor(JCheckBox checkBox) {
        super(checkBox);
        button = new JButton(IconType.SEARCH.getIcon());
        button.setOpaque(true);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        button.setForeground(table.getSelectionForeground());
        button.setBackground(table.getSelectionBackground());

        return button;
    }

    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }
}
