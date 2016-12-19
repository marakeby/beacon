package edu.vt.beacon.editor.dialog.canvas;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import edu.vt.beacon.editor.dialog.AbstractDialog;
import edu.vt.beacon.editor.dialog.BColorChooser;
import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.swing.EditingTextField;

public class CanvasDialog extends AbstractDialog {
	
	// Label Constants
	private static final String LABEL_MAJOR_GRID_SPACING = "Major Grid Spacing: ";
	private static final String LABEL_MINOR_GRID_SPACING = "Minor Grid Spacing: ";
	private static final String LABEL_BOUND_SIZE = "Bound Size: ";
	private static final String LABEL_PORT_SIZE = "Port Size: ";
	private static final String LABEL_MAJOR_GRID_COLOR = "Major Grid Color: ";
	private static final String LABEL_MINOR_GRID_COLOR = "Minor Grid Color: ";
	private static final String LABEL_BACKGROUND_COLOR = "Background Color: ";
	private static final String LABEL_SELECTION_COLOR = "Selection Color:";
	
	// Action Command Constants
	private static final String AC_TEXTFIELD_MAJOR_GRID_SPACING = "MAJOR_GRID_SPACING";
	private static final String AC_TEXTFIELD_MINOR_GRID_SPACING = "MINOR_GRID_SPACING";
	private static final String AC_COMBOBOX_BOUND_SIZE = "BOUND_SIZE";
	private static final String AC_COMBOBOX_PORT_SIZE = "PORT_SIZE";

	// Combobox Constants
	private static final String[] CHOICES_BOUND_SIZE = {"small", "medium", "large"};
	private static final String[] CHOICES_PORT_SIZE = {"small", "medium", "large"};
	
	private JPanel majorGridColorButtonPanel_;
	private JPanel minorGridColorButtonPanel_;
	private JPanel bkgColorButtonPanel_;
	private JPanel selectionColorButtonPanel_;
	private CanvasDialogState canvasDialogState_;
	
	
	public CanvasDialog(Document document) throws HeadlessException {		
		
	    super (document, document.getFrame());
	    setModalityType(ModalityType.DOCUMENT_MODAL);
	    this.document_ = document;
	    canvasDialogState_ = new CanvasDialogState(document_);
	    add(createContentPanel());		
		pack();
		setLocationRelativeTo(getOwner());		
		setVisible(true);
	}
	
	

	private JPanel createContentPanel() {
		JPanel basePanel = createBasePanel();
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new GridBagLayout());
		contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		GridBagConstraints c = new GridBagConstraints();
		
		JLabel majorGridSpacingLabel = new JLabel(LABEL_MAJOR_GRID_SPACING);
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 5, 0);
		c.anchor = GridBagConstraints.LINE_START;
		contentPanel.add(majorGridSpacingLabel, c);

		
		EditingTextField majorGridTextBox = getTextField(5, AC_TEXTFIELD_MAJOR_GRID_SPACING);
		majorGridTextBox.setText(Float.toString(canvasDialogState_.getMajorGridSpacing()));
		c.gridx = 1;
		c.gridy = 0;
		contentPanel.add(majorGridTextBox, c);
		
		JLabel majorGridColorLabel = new JLabel(LABEL_MAJOR_GRID_COLOR);
		c.gridx = 0;
		c.gridy = 1;
		contentPanel.add(majorGridColorLabel, c);
		
		majorGridColorButtonPanel_ = getColorPanel(canvasDialogState_.getMajorGridColor());
		majorGridColorButtonPanel_.addMouseListener(new CanvasColorListener(BColorChooser.LABEL, 
				document_, majorGridColorButtonPanel_, this, 
				CanvasColorListener.ACTION_MAJOR_GRID_COLOR));		
		c.gridx = 1;
		c.gridy = 1;
		contentPanel.add(majorGridColorButtonPanel_, c);
		
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		contentPanel.add(new JSeparator(JSeparator.HORIZONTAL), c);

		
		JLabel minorGridSpacingLabel = new JLabel(LABEL_MINOR_GRID_SPACING);
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 1;
		contentPanel.add(minorGridSpacingLabel, c);
		

		EditingTextField minorGridTextBox = getTextField(5, AC_TEXTFIELD_MINOR_GRID_SPACING);
		minorGridTextBox.setText(Float.toString(canvasDialogState_.getMinorGridSpacing()));
		c.gridx = 1;
		c.gridy = 3;
		contentPanel.add(minorGridTextBox, c);
		
		JLabel minorGridColorLabel = new JLabel(LABEL_MINOR_GRID_COLOR);
		c.gridx = 0;
		c.gridy = 4;
		contentPanel.add(minorGridColorLabel, c);
		
		minorGridColorButtonPanel_ = getColorPanel(canvasDialogState_.getMinorGridColor());
		minorGridColorButtonPanel_.addMouseListener(new CanvasColorListener(BColorChooser.LABEL, 
				document_, minorGridColorButtonPanel_, this, 
				CanvasColorListener.ACTION_MINOR_GRID_COLOR));		
		c.gridx = 1;
		c.gridy = 4;
		contentPanel.add(minorGridColorButtonPanel_, c);
		
		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 2;
		contentPanel.add(new JSeparator(JSeparator.HORIZONTAL), c);
		
		JLabel boundSizeLabel = new JLabel(LABEL_BOUND_SIZE);
		c.gridx = 0;
		c.gridy = 6;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.LINE_START;
		contentPanel.add(boundSizeLabel, c);
		
		
		JComboBox boundSizeComboBox = getComboBox(CHOICES_BOUND_SIZE, AC_COMBOBOX_BOUND_SIZE);
		c.gridx = 1;
		c.gridy = 6;
		c.anchor = GridBagConstraints.CENTER;
		contentPanel.add(boundSizeComboBox, c);
		
		JLabel portSizeLabel = new JLabel(LABEL_PORT_SIZE);
		c.gridx = 0;
		c.gridy = 7;
		c.anchor = GridBagConstraints.LINE_START;
		contentPanel.add(portSizeLabel, c);
		
		
		JComboBox portSizeComboBox = getComboBox(CHOICES_PORT_SIZE, AC_COMBOBOX_PORT_SIZE);
		c.gridx = 1;
		c.gridy = 7;
		c.anchor = GridBagConstraints.CENTER;
		contentPanel.add(portSizeComboBox, c);
		
		c.gridx = 0;
		c.gridy = 8;
		c.gridwidth = 2;
		contentPanel.add(new JSeparator(JSeparator.HORIZONTAL), c);
		
		JLabel canvasColorLabel = new JLabel(LABEL_BACKGROUND_COLOR);
		c.gridx = 0;
		c.gridy = 9;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.LINE_START;
		contentPanel.add(canvasColorLabel, c);
		
		
		bkgColorButtonPanel_ = getColorPanel(canvasDialogState_.getBkgColor());
		bkgColorButtonPanel_.addMouseListener(new CanvasColorListener(BColorChooser.LABEL, 
				document_, bkgColorButtonPanel_, this, 
				CanvasColorListener.ACTION_BACKGROUND_COLOR));
		c.gridx = 1;
		c.gridy = 9;
		contentPanel.add(bkgColorButtonPanel_, c);
		
		
		JLabel selectionColorLabel = new JLabel(LABEL_SELECTION_COLOR);
		c.gridx = 0;
		c.gridy = 10;
		contentPanel.add(selectionColorLabel, c);
		
		selectionColorButtonPanel_ = getColorPanel(canvasDialogState_.getSelectionColor());
		selectionColorButtonPanel_.addMouseListener(new CanvasColorListener(BColorChooser.LABEL, 
				document_, selectionColorButtonPanel_, this, 
				CanvasColorListener.ACTION_SELECTED_COLOR));
		c.gridx = 1;
		c.gridy = 10;
		contentPanel.add(selectionColorButtonPanel_, c);
		
		
		basePanel.add(contentPanel);
		return basePanel;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		JComboBox cb;
		EditingTextField etf;
		String actionCommand = e.getActionCommand();
		
		if (actionCommand.equals(AC_TEXTFIELD_MAJOR_GRID_SPACING))
		{
			etf = (EditingTextField)e.getSource();
			canvasDialogState_.setMajorGridSpacing(Float.valueOf(etf.getText().trim()));
			document_.put("grid.major", canvasDialogState_.getMajorGridSpacing());
		}
		else if (actionCommand.equals(AC_TEXTFIELD_MINOR_GRID_SPACING))
		{
			etf = (EditingTextField)e.getSource();
			canvasDialogState_.setMinorGridSpacing(Float.valueOf(etf.getText().trim()));
			document_.put("grid.minor", canvasDialogState_.getMinorGridSpacing());
		}
		else if (actionCommand.equals(AC_COMBOBOX_BOUND_SIZE))
		{
			cb = (JComboBox)e.getSource();
		}
		else if (actionCommand.equals(AC_COMBOBOX_PORT_SIZE))
		{
			cb = (JComboBox)e.getSource();
		}
		else if (actionCommand.equals(AC_CANCEL_BUTTON))
		{
			cancel();
		}
		else if (actionCommand.equals(AC_OK_BUTTON))
		{
			ok();
		}
	}

	private void ok() {
		this.dispose();
		
	}

	private void cancel() {
		canvasDialogState_.rollback();
		this.dispose();
		
	}

	public CanvasDialogState getCanvasDialogState() {
		return canvasDialogState_;
	}	

}
