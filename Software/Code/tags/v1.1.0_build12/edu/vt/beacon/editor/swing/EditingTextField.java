package edu.vt.beacon.editor.swing;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JDialog;
import javax.swing.JTextField;

public class EditingTextField extends JTextField {
	
	public EditingTextField(int len) {
		super(len);
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent evt) {
				int key = evt.getKeyCode();
				if (key == KeyEvent.VK_ENTER)
					transferFocus();
			}
		});
	}
}
