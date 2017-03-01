package edu.vt.beacon.editor.dialog.properties;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.vt.beacon.editor.dialog.AbstractDialog;
import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.properties.Contributor;
import edu.vt.beacon.editor.resources.icons.IconType;
import edu.vt.beacon.editor.swing.EditingTextField;
import edu.vt.beacon.pathway.Pathway;

public class PropertiesDialog extends AbstractDialog implements FocusListener, ListSelectionListener, ItemListener{
	
	// GUI label constants	
	private static final String LABEL_TEXTFIELD_PATHWAY_NAME = "Pathway Name: ";
	private static final String LABEL_TEXTFIELD_ORGANISM = "Organism: ";
	private static final String LABEL_TEXTFIELD_CONTRIBUTOR_NAME = "Name:";
	private static final String LABEL_TEXTFIELD_CONTRIBUTOR_INSTITUION = "Institution/Organization: ";
	private static final String LABEL_TEXTFIELD_CONTRIBUTOR_EMAIL = "Email: ";
	private static final String LABEL_CHECKBOX_CONTRIBUTOR_CORRESPONDING = "Corresponding Contributor";
	private static final String LABEL_JLIST_CONTRIBUTORS = "Contributors: ";
	
	// GUI combobox constants
	private static final String[] defaultOrganisms = {"Arabidopsis thaliana", "Medicago truncatula", "Populus", "Rice", "Zea mays"};
	private static final String fileSep =  System.getProperty("file.separator");
	private static final String organismFileName = System.getProperty("user.home") + fileSep+ ".beacon" +fileSep + "organism.properties";


	// GUI action command constants
	private static final String AC_TEXTFIELD_PATHWAY_NAME = "TEXTFIELD_PATHWAY_NAME";
	private static final String AC_COMBOBOX_ORGANISM = "COMBOBOX_ORGANISM";
	private static final String AC_BUTTON_CONTRIBUTOR_ADD = "CONTRIBUTOR_ADD";
	private static final String AC_BUTTON_CONTRIBUTOR_DELETE = "CONTRIBUTOR_DELETE";
	private static final String AC_TEXTFIELD_CONTRIBUTOR_NAME = "TEXTFIELD_NAME";
	private static final String AC_TEXTFIELD_CONTRIBUTOR_INSTITUTION = "TEXTFIELD_INSTITUTION";
	private static final String AC_TEXTFIELD_CONTRIBUTOR_EMAIL = "TEXTFIELD_EMAIL";
	private static final String AC_Edit_ORGANISM = "EDIT_ORGANISM";
	
	//GUI default value constants
	private static final String DEFAULT_CONTRIBUTOR_NAME = "<new name>";
	private static final String DEFAULT_CONTRIBUTOR_INSTITUTION = "<new institution>";
	private static final String DEFAULT_CONTRIBUTOR_EMAIL = "<new email>";
	
	// Global components
	private Document document_;
	private Pathway pathway_;
	private List<Contributor> contributorList_;
	
	// GUI components
	private JList contributorsList;
	private DefaultListModel contributorsListModel;
	private EditingTextField emailTextField;
	private EditingTextField institutionTextField;
	private EditingTextField nameTextField;
	private JCheckBox correspondingContributorCheckBox;
	private JButton addButton;
	private JButton deleteButton;
	private EditingTextField pathwayNameTextField;
	private JButton addOrganism;
	private JComboBox organismComboBox;

	public PropertiesDialog(Document document) throws HeadlessException {		
		
	    super (document, document.getFrame());
	    setModalityType( ModalityType.DOCUMENT_MODAL);
	    this.document_ = document;
	    this.pathway_ = document_.getPathway();
		
	    add(createContentPanel());		
		pack();
		setLocationRelativeTo(getOwner());		
		setVisible(true);
	}

	private void populateOrganismCombobox() {

		List<String> organismsList = new ArrayList<String>();
		try {

			InputStream is = new FileInputStream(new File(organismFileName));
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line;
			while ((line = reader.readLine()) != null) {
				organismsList.add(line);
			}
		} catch(Exception e){
//			System.out.println(e);
		}

		if (organismsList.size()==0){
			organismsList.addAll(Arrays.asList(defaultOrganisms));
		}

		String[] organismArray = new String[organismsList.size()];
		organismArray = organismsList.toArray(organismArray);

		if (organismComboBox==null)
			organismComboBox = new JComboBox(organismArray);
		else
			organismComboBox.setModel(new DefaultComboBoxModel (organismArray));
		organismComboBox.setSelectedItem(pathway_.getOrganism());
		organismComboBox.setActionCommand(AC_COMBOBOX_ORGANISM);
		organismComboBox.addActionListener(this);
		organismComboBox.addFocusListener(this);


	}
	public String getAPropertiesDirectoryPath() {
		return PropertiesDialog.class.getPackage().getName().replace(".",
				System.getProperty("file.separator")) +
				System.getProperty("file.separator");
	}

	private Component createContentPanel() {
		JPanel basePanel = createBasePanel();
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JLabel pathwayNameLabel = new JLabel(LABEL_TEXTFIELD_PATHWAY_NAME);
		JLabel organismLabel = new JLabel(LABEL_TEXTFIELD_ORGANISM);					
		
		pathwayNameTextField = new EditingTextField(20);
		pathwayNameTextField.setText(document_.getPathway().getName());
		pathwayNameTextField.setActionCommand(AC_TEXTFIELD_PATHWAY_NAME);
		pathwayNameTextField.addActionListener(this);
		pathwayNameTextField.addFocusListener(this);

		populateOrganismCombobox();


		addOrganism = new JButton("Edit organisms");
		addOrganism.setActionCommand(AC_Edit_ORGANISM);
		addOrganism.addActionListener(this);
		addOrganism.addFocusListener(this);

		panel.add(getLabelledPanel(pathwayNameLabel, pathwayNameTextField));
		JPanel panel2 = new JPanel();

		panel2.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx=0;
		c.gridy=0;
		c.gridwidth=2;
		panel2.add(organismComboBox, c);
		c.gridx=3;
		c.gridy=0;
		c.gridwidth=1;
		panel2.add(addOrganism, c);
		panel.add(getLabelledPanel(organismLabel, panel2));

		panel.add(createContributorListPanel());
		panel.add(createContributorEditPanel());
		basePanel.add(panel, BorderLayout.CENTER);
		return basePanel;
	}
	

	private Component createContributorListPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		JLabel contributorsLabel = new JLabel(LABEL_JLIST_CONTRIBUTORS);
		
		addButton = new JButton(IconType.valueOf("ADD").getIcon());
		deleteButton = new JButton(IconType.valueOf("DELETE").getIcon());
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		
		addButton.setBorder(BorderFactory.createEmptyBorder());
		addButton.setContentAreaFilled(false);
		addButton.setActionCommand(AC_BUTTON_CONTRIBUTOR_ADD);
		addButton.addActionListener(this);
		
		deleteButton.setBorder(BorderFactory.createEmptyBorder());
		deleteButton.setContentAreaFilled(false);
		deleteButton.setActionCommand(AC_BUTTON_CONTRIBUTOR_DELETE);
		deleteButton.addActionListener(this);
		
		buttonPanel.add(addButton);
		buttonPanel.add(deleteButton);
		
		contributorsListModel = new DefaultListModel();
		
		// If contributors exist, load up model
		contributorList_ = document_.getPathway().getContributorList();
		if (contributorList_ != null && contributorList_.size() > 0) {
			Contributor contributor;
			for (int i = 0; i < contributorList_.size(); i++) {
				contributor = contributorList_.get(i);
				contributorsListModel.add(i, contributor.toString());
			}
		} else {
			contributorList_ = new ArrayList<Contributor>();
			document_.getPathway().setContributorList(contributorList_);
		}
		
		contributorsList = new JList(contributorsListModel);
		contributorsList.setLayoutOrientation(JList.VERTICAL);
		contributorsList.setVisibleRowCount(-1);
		contributorsList.setCellRenderer(new PropertiesCellRenderer());
		contributorsList.addListSelectionListener(this);
		contributorsList.addFocusListener(this);

		JScrollPane listScrollPane = new JScrollPane(contributorsList);
		listScrollPane.setPreferredSize(new Dimension(250, 80));
		
		panel.add(contributorsLabel, BorderLayout.LINE_START);
		panel.add(buttonPanel, BorderLayout.LINE_END);
		panel.add(listScrollPane, BorderLayout.PAGE_END);
		panel.setBorder(BorderFactory.createEtchedBorder());
		return panel;
	}
	
	private Component createContributorEditPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		
		JLabel nameLabel = new JLabel(LABEL_TEXTFIELD_CONTRIBUTOR_NAME);
		JLabel institutionLabel = new JLabel(LABEL_TEXTFIELD_CONTRIBUTOR_INSTITUION);
		JLabel emailLabel = new JLabel(LABEL_TEXTFIELD_CONTRIBUTOR_EMAIL);
		JLabel corespondingContributorLabel = new JLabel(LABEL_CHECKBOX_CONTRIBUTOR_CORRESPONDING);
		
		nameTextField = new EditingTextField(20);
		nameTextField.setEnabled(false);
		nameTextField.setActionCommand(AC_TEXTFIELD_CONTRIBUTOR_NAME);
		nameTextField.addActionListener(this);
		nameTextField.addFocusListener(this);
		
		institutionTextField = new EditingTextField(20);
		institutionTextField.setEnabled(false);
		institutionTextField.setActionCommand(AC_TEXTFIELD_CONTRIBUTOR_INSTITUTION);
		institutionTextField.setFocusable(true);
		institutionTextField.addActionListener(this);
		institutionTextField.addFocusListener(this);
		
		emailTextField = new EditingTextField(20);
		emailTextField.setEnabled(false);
		emailTextField.setActionCommand(AC_TEXTFIELD_CONTRIBUTOR_EMAIL);
		emailTextField.addActionListener(this);
		emailTextField.addFocusListener(this);
		
		correspondingContributorCheckBox = new JCheckBox();
		correspondingContributorCheckBox.setEnabled(false);
		correspondingContributorCheckBox.addItemListener(this);
		correspondingContributorCheckBox.addFocusListener(this);
		
		
		JPanel correspondingContributorPanel = new JPanel();
		correspondingContributorPanel.add(correspondingContributorCheckBox);
		correspondingContributorPanel.add(corespondingContributorLabel);
				
		panel.add(getLabelledPanel(nameLabel, nameTextField));
		panel.add(getLabelledPanel(institutionLabel, institutionTextField));
		panel.add(getLabelledPanel(emailLabel, emailTextField));
		panel.add(correspondingContributorPanel);
				
		return panel;
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		
	    String actionCommand = e.getActionCommand();
//		System.out.println("AC_COMBOBOX_ORGANISM");
	    
		if (actionCommand.equals(AC_TEXTFIELD_PATHWAY_NAME))
			updatePathwayName(e);
		
		else if (actionCommand.equals(AC_BUTTON_CONTRIBUTOR_ADD))
			performButtonAdd(e);
		
		else if (actionCommand.equals(AC_BUTTON_CONTRIBUTOR_DELETE))
			performButtonDelete(e);
		
		else if (actionCommand.equals(AC_OK_BUTTON))
			performButtonOk(e);
		
		else if (actionCommand.equals(AC_CANCEL_BUTTON))
			performButtonCancel(e);
		
		else if (actionCommand.equals(AC_TEXTFIELD_CONTRIBUTOR_NAME))
			updateList(e);
		
		else if (actionCommand.equals(AC_TEXTFIELD_CONTRIBUTOR_INSTITUTION))
			updateList(e);
		
		else if (actionCommand.equals(AC_TEXTFIELD_CONTRIBUTOR_EMAIL))
			updateList(e);
		
		else if (actionCommand.equals(AC_COMBOBOX_ORGANISM))
		{
			JComboBox cb = (JComboBox)e.getSource();
			performOrganismComboBox(cb);
		}

		else if (actionCommand.equals(AC_Edit_ORGANISM)){
			OrganismDialog dlg =new OrganismDialog(this.document_, this);
			populateOrganismCombobox();
			this.repaint();
//			System.out.println("repaint");

		}
		
	}

	private void performOrganismComboBox(JComboBox cb) {

		pathway_.setOrganism_((String)cb.getSelectedItem());
		
	}

	private void updatePathwayName(ActionEvent e) {
		pathway_.setName(pathwayNameTextField.getText().trim());
		
	}

	private void performButtonCancel(ActionEvent e) {
		this.setVisible(false);
		
	}

	private void performButtonOk(ActionEvent e) {
		this.setVisible(false);
		
	}

	private void updateList(ActionEvent e) {
		int index = contributorsList.getSelectedIndex();
		Contributor contributor = new Contributor(nameTextField.getText().trim(), 
				institutionTextField.getText().trim(), emailTextField.getText().trim(), 
				correspondingContributorCheckBox.isSelected());
		contributorList_.set(index, contributor);
		contributorsListModel.setElementAt(contributor.toString(), index);
		updatePathway();
	}

	private void performButtonDelete(ActionEvent e) {
		int index = contributorsList.getSelectedIndex();
		contributorList_.remove(index);
		contributorsListModel.remove(index);
		
		int size = contributorsListModel.getSize();

	    if (size == 0) { 
	        deleteButton.setEnabled(false);
	        nameTextField.setText("");
	        institutionTextField.setText("");
	        emailTextField.setText("");
	        correspondingContributorCheckBox.setSelected(false);
	        
	    } else { 
	        if (index == contributorsListModel.getSize()) {
	            index--;
	        }

	        contributorsList.setSelectedIndex(index);
	        contributorsList.ensureIndexIsVisible(index);
	    }
	    updatePathway();
		
	}

	private void performButtonAdd(ActionEvent e) {
		enableEditorFields();
		nameTextField.setText(DEFAULT_CONTRIBUTOR_NAME);
		institutionTextField.setText(DEFAULT_CONTRIBUTOR_INSTITUTION);
		emailTextField.setText(DEFAULT_CONTRIBUTOR_EMAIL);
		Contributor contributor = new Contributor(DEFAULT_CONTRIBUTOR_NAME, 
				DEFAULT_CONTRIBUTOR_INSTITUTION, DEFAULT_CONTRIBUTOR_EMAIL, false);
		contributorList_.add(contributor);
		int size = contributorsListModel.getSize();
		contributorsListModel.addElement(contributor.toString());
		contributorsList.setSelectedIndex(size);
		deleteButton.setEnabled(true);
		nameTextField.requestFocus();
		updatePathway();
	}

	private void updatePathway() {
		pathway_.setContributorList(contributorList_);
		
	}

	private void enableEditorFields() {
		nameTextField.setEnabled(true);
		institutionTextField.setEnabled(true);
		emailTextField.setEnabled(true);
		correspondingContributorCheckBox.setEnabled(true);
	}

	@Override
	public void focusGained(FocusEvent e) {
		updateAll();
	}

	@Override
	public void focusLost(FocusEvent e) {
		updateAll();
	}
	
	private void updateAll() {
		if (!contributorsList.isSelectionEmpty()) {
			int index = contributorsList.getSelectedIndex();
			Contributor contributor = new Contributor(nameTextField.getText().trim(), 
					institutionTextField.getText().trim(), emailTextField.getText().trim(), 
					correspondingContributorCheckBox.isSelected());
			contributorList_.set(index, contributor);
			contributorsListModel.setElementAt(contributor.toString(), index);
		}
		updatePathway();
		pathway_.setName(pathwayNameTextField.getText().trim());
		pathway_.setOrganism_((String)organismComboBox.getSelectedItem());
	}

	@Override
	/*
	 * Detects when a new Jlist item is selected and updates fields.
	 */
	public void valueChanged(ListSelectionEvent e) {
		int index = contributorsList.getSelectedIndex();
		if (index != -1) {
			Contributor contributor = contributorList_.get(index);
			nameTextField.setText(contributor.getName());
			institutionTextField.setText(contributor.getInstitution());
			emailTextField.setText(contributor.getEmail());
			correspondingContributorCheckBox.setSelected(contributor.isCorespondingContributor());
			enableEditorFields();
		}
		
	}

	@Override
	/*
	 * Detects the change to the correspondingContributorCheckBox and performs an update on contributor.
	 */
	public void itemStateChanged(ItemEvent e) {
		int index = contributorsList.getSelectedIndex();
		Contributor contributor = new Contributor(nameTextField.getText().trim(), 
				institutionTextField.getText().trim(), emailTextField.getText().trim(), 
				correspondingContributorCheckBox.isSelected());
		contributorList_.set(index, contributor);
		contributorsListModel.setElementAt(contributor.toString(), index);
		updatePathway();
		
	}

}
