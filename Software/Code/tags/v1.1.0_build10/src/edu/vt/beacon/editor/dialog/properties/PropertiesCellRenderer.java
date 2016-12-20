package edu.vt.beacon.editor.dialog.properties;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class PropertiesCellRenderer extends JLabel implements ListCellRenderer {

		private DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

		@Override
		public Component getListCellRendererComponent(
				JList list, Object value, int index,
				boolean isSelected, boolean cellHasFocus) {
			JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index,
			        isSelected, cellHasFocus);
			
			// Add zebra striping to list
			if (!isSelected && index % 2 != 0) {
				renderer.setBackground(new Color(237,237,237));
			}
			
			return renderer;
		}


}
