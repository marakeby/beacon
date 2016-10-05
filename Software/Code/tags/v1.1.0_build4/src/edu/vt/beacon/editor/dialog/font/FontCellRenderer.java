package edu.vt.beacon.editor.dialog.font;

import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

public class FontCellRenderer extends DefaultListCellRenderer {


	private static final long serialVersionUID = 1L;

	public Component getListCellRendererComponent(
        JList list,
        Object value,
        int index,
        boolean isSelected,
        boolean cellHasFocus) {
        JLabel label = (JLabel)super.getListCellRendererComponent(
            list,value,index,isSelected,cellHasFocus);
        
        Font font = new Font((String) value, label.getFont().getStyle(),
                             label.getFont().getSize());
        
        label.setFont(font);
        return label;
    }
}