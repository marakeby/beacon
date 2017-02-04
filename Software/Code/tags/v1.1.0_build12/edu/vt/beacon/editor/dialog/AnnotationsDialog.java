package edu.vt.beacon.editor.dialog;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.document.DocumentState;
import edu.vt.beacon.editor.gene.Gene;
import edu.vt.beacon.editor.resources.icons.IconType;
import edu.vt.beacon.editor.swing.EditingTextField;
import edu.vt.beacon.graph.glyph.AbstractGlyph;
import edu.vt.beacon.graph.glyph.node.AbstractNode;
import edu.vt.beacon.graph.glyph.node.activity.AbstractActivity;
import edu.vt.beacon.layer.Layer;
import edu.vt.beacon.map.Map;

public class AnnotationsDialog extends AbstractDialog {
	
	// Annotations parameters publics
	public String pubMed_ID = "PubMed ID";
	public String annotation = "Annotation";
	public String notes = "Notes";
	public String gene_ID = "Gene ID";
	public String gene_NAME = "Gene Name";
	
	// Annotations parameters publics
	private String init_PUBMED_ID ;
	private String init_ANNOTATION ;
	private String init_NOTES ;
	private String init_GENE_ID ;
	private String init_GENE_NAME ;
	

	// Label Constants
	private static final String LABEL_PUBMED_ID = "PubMed ID: ";
	private static final String LABEL_ANNOTATION = "Annotation: ";
	private static final String LABEL_NOTES = "Notes: ";
	private static final String LABEL_GENE_ID = "Gene ID: ";
	private static final String LABEL_GENE_NAME = "Gene Name: ";
	
	private static final String LABEL_SELECTABLE_GENES = "Selectable Genes: ";
	private static final String LABEL_SELECTED_GENES = "Selected Genes: ";
	
	// Action Command Constants
	private static final String AC_PUBMED_ID = "PUBMED_ID";
	private static final String AC_ANNOTATION = "ANNOTATION";
	private static final String AC_NOTES = "NOTES";
	private static final String AC_GENE_ID = "AC_GENE_ID";
	private static final String AC_GENE_NAME = "AC_GENE_NAME";
	private static final String AC_BUTTON_GENE_ADD = "AC_BUTTON_GENE_ADD"; 
	private static final String AC_BUTTON_GENE_DELETE = "AC_BUTTON_GENE_DELETE";
	
	// Text Areas
	private EditingTextField geneIDTextBox;
	private EditingTextField annotationTextBox;
	private EditingTextField geneNameTextBox;
	private TextArea notesTextArea;
	private EditingTextField pubMedIDTextBox;
	
	// Genes Lists / table
	private JList selectableGenesList_;
	private JList selectedGenesList_;
	private JTable selectableGenestable;
	private JTable selectedGenestable;
	
    ListSelectionModel selectableListSelectionModel;
    ListSelectionModel selectedListSelectionModel;
	
	// list of genes
	private List<Gene> allGenes_ = new ArrayList<Gene>();
	private List<Gene> selectedGenes_ = new ArrayList<Gene>();
	private List<Gene> selectableGenes_ = new ArrayList<Gene>();
	
	// 2D array for tables
	String [][] selectableGenesTableData;
	String [][] selectedGenesTableData;
	
	// If any of selected glyphs is not an AbtractActivity
	private boolean notAbstractAvtivity = false;
		
		
	private static final long serialVersionUID = 1L;

	public AnnotationsDialog(Document document) {
		super(document, document.getFrame());
		// TODO Auto-generated constructor stub
		init_PUBMED_ID = pubMed_ID ;
		init_ANNOTATION = annotation ;
		init_NOTES = notes ;
		init_GENE_ID = gene_ID ;
		
		//super (document, document.getFrame());
	    setModalityType(ModalityType.DOCUMENT_MODAL);
	    this.document_ = document;
	    
	    fillTestGenesList();
	    
	    add(createContentPanel());
	    
	    initializeGenesLists();
	    
	    
	    
	    //canvasDialogState_ = new CanvasDialogState(document_);
	    		
		pack();
		setLocationRelativeTo(getOwner());		
		
		setVisible(true);
		
	}
	 
	
 
	private void fillTestGenesList() {
		// TODO Auto-generated method stub
		
		/*
		for (int k=0; k<5; k++){
			
			Gene gene = new Gene("Glyma"+ Integer.toString(k) ,"GeneName"+ Integer.toString(k), "Annotation"+ Integer.toString(k));
			allGenes_.add(gene);
			//allGenes_.add(new Gene("Glyma"+ Integer.toString(k) ,"GeneName"+ Integer.toString(k), "Annotation"+ Integer.toString(k)));
			selectedGenes_.add(gene);
		}
		
		for (int k=15; k<20; k++){
			
			Gene gene = new Gene("Glyma"+ Integer.toString(k) ,"GeneName"+ Integer.toString(k), "Annotation"+ Integer.toString(k));
			allGenes_.add(gene);
			//allGenes_.add(new Gene("Glyma"+ Integer.toString(k) ,"GeneName"+ Integer.toString(k), "Annotation"+ Integer.toString(k)));
			selectableGenes_.add(gene);
		}
		*/
		/*for (int k=0; k<7; k++){
			
			Gene gene = new Gene("" , "" , "");
			selectedGenes_.add(gene);
			selectableGenes_.add(gene);
		}*/
	}

	private JPanel createContentPanel() {
		
		JPanel basePanel = createBasePanel();
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new GridBagLayout());
		contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		GridBagConstraints c = new GridBagConstraints();
		

		///////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////

		JLabel selectableGenesLabel = new JLabel(LABEL_SELECTABLE_GENES);
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 5, 0);
		c.anchor = GridBagConstraints.LINE_START;
		contentPanel.add(selectableGenesLabel, c);
		
		
        selectableGenestable = new JTable(new GeneTableModel(GeneTableModel.SELECTABLE));
        selectableListSelectionModel = selectableGenestable.getSelectionModel();
        selectableGenestable.setSelectionModel(selectableListSelectionModel);
        JScrollPane selectableTablePane = new JScrollPane(selectableGenestable);
	
        c.gridx = 1;
		c.gridy = 0;
		c.insets = new Insets(0, 5, 5, 0);
		c.anchor = GridBagConstraints.LINE_START;
		selectableTablePane.setPreferredSize(new Dimension(420, 130));
		c.fill = GridBagConstraints.HORIZONTAL;
		
		contentPanel.add(selectableTablePane, c);
		
		///////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////

		JLabel selectedGenesLabel = new JLabel(LABEL_SELECTED_GENES);
		c.gridx = 0;
		c.gridy = 2;
		c.insets = new Insets(0, 0, 5, 0);
		c.anchor = GridBagConstraints.LINE_START;
		contentPanel.add(selectedGenesLabel, c);
		
        selectedGenestable = new JTable(new GeneTableModel(GeneTableModel.SELECTED));
        selectedGenestable.setModel(new GeneTableModel(GeneTableModel.SELECTED));
        selectedListSelectionModel = selectedGenestable.getSelectionModel();
        
        selectedGenestable.setSelectionModel(selectedListSelectionModel);
        JScrollPane selectedTablePane = new JScrollPane(selectedGenestable);
	
        c.gridx = 1;
		c.gridy = 2;
		c.insets = new Insets(0, 5, 5, 0);
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		
		//TableColumn column = selectedTablePane.getColumnModel().getColumn(2);
		//selectedTablePane.getColumnModel().getColumn(2).setPreferredWidth(100);
		//TableColumn column = selectedTablePane.getColumnModel().getColumn(i);
		
		selectedTablePane.setPreferredSize(new Dimension(420, 130));
		
		contentPanel.add(selectedTablePane, c);
		
		///////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////
		
		///////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////

		JPanel buttonPanel = new JPanel();
		
    	JButton addButton = new JButton(IconType.valueOf("ADD").getIcon());
    	addButton.setBorder(BorderFactory.createEmptyBorder());
		addButton.setContentAreaFilled(false);
		addButton.setActionCommand(AC_BUTTON_GENE_ADD);
		addButton.addActionListener(this);
		
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(0, 5, 5, 0);
		c.anchor = GridBagConstraints.LINE_START;
		//contentPanel.add(addButton, c);
		
		buttonPanel.add(addButton);
		
		JButton deleteButton = new JButton(IconType.valueOf("DELETE").getIcon());
		deleteButton.setBorder(BorderFactory.createEmptyBorder());
		deleteButton.setContentAreaFilled(false);
		deleteButton.setActionCommand(AC_BUTTON_GENE_DELETE);
		deleteButton.addActionListener(this);	
		
		c.gridx = 1;
		c.gridy = 1;
		c.insets = new Insets(0, 5, 5, 0);
		c.anchor = GridBagConstraints.CENTER;
		//contentPanel.add(deleteButton, c);
		
		buttonPanel.add(deleteButton);
		
		contentPanel.add(buttonPanel, c);
		
		///////////////////////////////////////////////////////////////////////////
		///////////////////////////////////////////////////////////////////////////
		
		JLabel pubMedIDLabel = new JLabel(LABEL_PUBMED_ID);
		c.gridx = 0;
		c.gridy = 3;
		c.insets = new Insets(0, 5, 5, 0);
		//c.anchor = GridBagConstraints.LINE_START;
		c.anchor = GridBagConstraints.LINE_START;
		contentPanel.add(pubMedIDLabel, c);
		
		pubMedIDTextBox = getTextField(40, AC_PUBMED_ID);
		pubMedIDTextBox.setText(pubMed_ID);
		c.gridx = 1;
		c.gridy = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		pubMedIDTextBox.setEditable(false);
		contentPanel.add(pubMedIDTextBox, c);
		
		///////////////////////////////////////////////////////////////////////////
		JLabel notesLabel = new JLabel(LABEL_NOTES);
		c.gridx = 0;
		c.gridy = 4;
		c.insets = new Insets(0, 5, 5, 0);
		c.anchor = GridBagConstraints.LINE_START;
		contentPanel.add(notesLabel, c);
		
		notesTextArea = new TextArea(7, 55);
		notesTextArea.setText(notes);
		c.gridx = 1;
		c.gridy = 4;
		c.fill = GridBagConstraints.HORIZONTAL;
		notesTextArea.setEditable(false);
		contentPanel.add(notesTextArea, c);

		
		///////////////////////////////////////////////////////////////////////////

		
		////////////////////////////////////////////////////////////////////////////
		basePanel.add(contentPanel);

		return basePanel;
	}



	private void initializeGenesLists() {
		
		// TODO Auto-generated method stub
		
		/*selectableGenesTableData = new String[selectableGenes_.size()][3] ;
		for (int i=0; i<selectableGenes_.size(); i++){
			selectableGenesTableData[i][0] = selectableGenes_.get(i).getId();
			selectableGenesTableData[i][1] = selectableGenes_.get(i).getName();
			selectableGenesTableData[i][2] = selectableGenes_.get(i).getAnnotation();
		}*/
//		if (document_.getGenes() != null )
//			for (int i=0; i<document_.getGenes().size(); i++){
//				Gene gene = new Gene(document_.getGenes().get(i).getId(),document_.getGenes().get(i).getName(), document_.getGenes().get(i).getAnnotation());
//
//				selectableGenes_.add(gene);
//
//				//selectableGenesTableData[i][0] = document_.getGenes().get(i).getId();
//				//selectableGenesTableData[i][1] = document_.getGenes().get(i).getName();
//				//selectableGenesTableData[i][2] = document_.getGenes().get(i).getAnnotation();
//			}
		
		///////////////////////////////////////////////////////////////////////////
		
		
        Layer layer;
        AbstractGlyph glyph;
        Map map = document_.getBrowserMenu().getSelectedMap();
        boolean firstGlyph = true;
        
        for (int i = 0; i < map.getLayerCount(); i++) {
            
            layer = map.getLayerAt(i);
            
            if (layer.isActive()) {
                
                for (int j = 0; j < layer.getGlyphCount(); j++) {
                    
                    glyph = layer.getGlyphAt(j);
                    
                    if (glyph.isSelected()) {
                    	if (!(glyph instanceof AbstractNode))
                            return;
                    	
                    	if (!(glyph instanceof AbstractActivity))
                            notAbstractAvtivity = true;
                    	
                    	if (glyph instanceof AbstractActivity){
                    		
                    		AbstractActivity node = (AbstractActivity) glyph;
                    		
                    		if (firstGlyph){
                    			
                    			pubMed_ID = node.getPubMedID();
                        		notes = node.getNotes();
                    			
                    			for (int k=0; k<node.getGenesCount(); k++){
                    				Gene gene = new Gene(node.getGeneIdAt(k),node.getGeneNameAt(k), "", node.getGeneDescriptionAt(k));
                    				allGenes_.add(gene);
                    				selectedGenes_.add(gene);
                    			}

                    			firstGlyph = false;
                    		}
                    		else{
                    			if (pubMed_ID != node.getPubMedID())
                    				pubMed_ID = "";
                    			if (notes != node.getNotes())
                    				notes = "";
                    			
                    			
                    			int size = selectedGenes_.size()-1;
                    			for (int l=size; l>=0; l--){
                    				Gene gene = new Gene(selectedGenes_.get(l));
                    				boolean commonGene = false;
                    				
                    				for (int k=0; k<node.getGenesCount(); k++){
                    					if (gene.getId() == node.getGeneIdAt(k))
                    						commonGene = true;
                    				}
                    				
                    				if (! commonGene){
                    					selectedGenes_.remove(l);
                    				}
                    			}
                    			
                    			
                    		
                    		}
                    		
                    	}
                    		
                    		
                    		
                    }
                    	
                    	
                        
                   	
                       
                   	//if (allGenes_.isEmpty())
                   	//	return;
                    	
                   	//AbstractNode node = (AbstractNode) glyph;
                        
                    //Font font = node.getFont();
                    //String fontName = (String) fontChooserNames_.getSelectedItem();
                        
                    //node.setFont(new Font(fontName, font.getStyle(), font.getSize()));
                            
                    
                }
            }
        } 
        
        
        
        int selectableSize = selectableGenes_.size()-1;
        
        for (int j=selectableSize; j>=0; j--){
        	Gene selGene = new Gene(selectableGenes_.get(j));
        	boolean exist = false;
        	for (int k=0; k<selectedGenes_.size(); k++){
        		if (selGene.getId() == selectedGenes_.get(k).getId()){
        			exist = true;
        		}
        	}
        	
        	if (exist){
        		selectableGenes_.remove(j);
        	}
        }        
        
        
		selectableGenesTableData = new String[selectableGenes_.size()][3] ;
		for (int i=0; i<selectableGenes_.size(); i++){
			selectableGenesTableData[i][0] = selectableGenes_.get(i).getId();
			selectableGenesTableData[i][1] = selectableGenes_.get(i).getName();
//			selectableGenesTableData[i][2] = selectableGenes_.get(i).getAnnotation();
		}
		
		selectedGenesTableData = new String[selectedGenes_.size()][3] ;
		for (int i=0; i<selectedGenes_.size(); i++){
			selectedGenesTableData[i][0] = selectedGenes_.get(i).getId();
			selectedGenesTableData[i][1] = selectedGenes_.get(i).getName();
//			selectedGenesTableData[i][2] = selectedGenes_.get(i).getAnnotation();
		}
        
		/*selectedGenesTableData = new String[selectedGenes_.size()][3] ;
		for (int i=0; i<selectedGenes_.size(); i++){
			selectedGenesTableData[i][0] = selectedGenes_.get(i).getId();
			selectedGenesTableData[i][1] = selectedGenes_.get(i).getName();
			selectedGenesTableData[i][2] = selectedGenes_.get(i).getAnnotation();
		}*/
        
        document_.getCanvas().repaint();

	}





	@Override
	public void actionPerformed(ActionEvent e) {
		
		JComboBox cb;
		EditingTextField etf;
		String actionCommand = e.getActionCommand();
		
	    if (actionCommand.equals(AC_BUTTON_GENE_ADD))
	    {
			if ((selectableGenestable.getSelectedRow() >= 0) && (!selectableGenes_.isEmpty()))
			{
				int selectedIndex = selectableGenestable.getSelectedRow();
				
				String id = selectableGenes_.get(selectedIndex).getId();
				String name = selectableGenes_.get(selectedIndex).getName();
//				String annotation = selectableGenes_.get(selectedIndex).getAnnotation();
				
				Gene gene = new Gene(id, name, "", annotation);
				
				selectedGenes_.add(gene);
				selectedGenesTableData = new String[selectedGenes_.size()][3] ;
				for (int i=0; i<selectedGenes_.size(); i++){
					selectedGenesTableData[i][0] = selectedGenes_.get(i).getId();
					selectedGenesTableData[i][1] = selectedGenes_.get(i).getName();
//					selectedGenesTableData[i][2] = selectedGenes_.get(i).getAnnotation();
				}
				selectedGenestable.revalidate();
				selectedGenestable.repaint();
				
				selectableGenes_.remove(selectedIndex);
				selectableGenesTableData = new String[selectableGenes_.size()][3] ;
				for (int i=0; i<selectableGenes_.size(); i++){
					selectableGenesTableData[i][0] = selectableGenes_.get(i).getId();
					selectableGenesTableData[i][1] = selectableGenes_.get(i).getName();
//					selectableGenesTableData[i][2] = selectableGenes_.get(i).getAnnotation();
				}
				selectableGenestable.revalidate();
				selectableGenestable.repaint();
					
			}
	    }
	    else if (actionCommand.equals(AC_BUTTON_GENE_DELETE))
	    {	
			if ((selectedGenestable.getSelectedRow() >= 0) && (!selectedGenes_.isEmpty()))
			{
				int selectedIndex = selectedGenestable.getSelectedRow();
				
				String id = selectedGenes_.get(selectedIndex).getId();
				String name = selectedGenes_.get(selectedIndex).getName();
//				String annotation = selectedGenes_.get(selectedIndex).getAnnotation();
				
				Gene gene = new Gene(id, name, "", annotation);
				
				selectableGenes_.add(gene);
				selectableGenesTableData = new String[selectableGenes_.size()][3] ;
				for (int i=0; i<selectableGenes_.size(); i++){
					selectableGenesTableData[i][0] = selectableGenes_.get(i).getId();
					selectableGenesTableData[i][1] = selectableGenes_.get(i).getName();
//					selectableGenesTableData[i][2] = selectableGenes_.get(i).getAnnotation();
				}
				selectableGenestable.revalidate();
				selectableGenestable.repaint();
				
				selectedGenes_.remove(selectedIndex);
				selectedGenesTableData = new String[selectedGenes_.size()][3] ;
				for (int i=0; i<selectedGenes_.size(); i++){
					selectedGenesTableData[i][0] = selectedGenes_.get(i).getId();
					selectedGenesTableData[i][1] = selectedGenes_.get(i).getName();
//					selectedGenesTableData[i][2] = selectedGenes_.get(i).getAnnotation();
				}
				selectedGenestable.revalidate();
				selectedGenestable.repaint();
			}
	    }
	    else if (actionCommand.equals(AC_CANCEL_BUTTON))
	    {
			//cancel();
			/*pubMed_ID = init_PUBMED_ID ;
			annotation = init_ANNOTATION ;
			notes = init_NOTES ;
			gene_ID = init_GENE_ID ;
			
			document_.getState().apply(document_);*/
        	this.dispose();
	    }
	    else if (actionCommand.equals(AC_OK_BUTTON))
	    {	
	        Layer layer;
	        AbstractGlyph glyph;
	        Map map = document_.getBrowserMenu().getSelectedMap();
	        	        
	        for (int i = 0; i < map.getLayerCount(); i++) {
	            
	            layer = map.getLayerAt(i);
	            
	            if (layer.isActive()) {
	                
	                for (int j = 0; j < layer.getGlyphCount(); j++) {
	                    
	                    glyph = layer.getGlyphAt(j);
	                    
	                    if (glyph.isSelected()) {
	                    	if (!(glyph instanceof AbstractNode))
	                            return;
	                    	
	                    	if (!(glyph instanceof AbstractActivity))
	                            notAbstractAvtivity = true;
	                    	
	                    	if (glyph instanceof AbstractActivity){
	                    		
	                    		AbstractActivity node = (AbstractActivity) glyph;
	                    		
	                    		node.clear();
	                    		//for (int nodeGNum=0; nodeGNum<node.getGenesCount(); nodeGNum++){
                    			//	node.removeGene(nodeGNum);
                    			//}
                		
	                    		for (int seleGNum = 0; seleGNum<selectedGenes_.size(); seleGNum++){
	                    			Gene seltdGene = new Gene(selectedGenes_.get(seleGNum));
	                    			node.addGene(seltdGene);
	                    		}
	                    		
	                    	}
	                    		
	                    		
	                    		
	                    }
	                    
	                }
	            }
	        } 
	        
			
			new DocumentState(document_, "Font", false);
        	this.dispose();
	    }
		
	}
	/*public void valueChanged(ListSelectionEvent event)
	{
		
		if (event.getSource().equals(selectableGenesList_)){
			String selection_str = (String) selectableGenesList_.getSelectedValue();
			for (int l=0; l<selectableGenes_.size(); l++){
				if (selectableGenes_.get(l).getId().equals(selection_str)){
					geneIDTextBox.setText(selectableGenes_.get(l).getId());
					annotationTextBox.setText(selectableGenes_.get(l).getAnnotation());
					geneNameTextBox.setText(selectableGenes_.get(l).getName());
				}
			}
		}
		else if (event.getSource().equals(selectedGenesList_)){
			String selection_str = (String) selectedGenesList_.getSelectedValue();
			for (int l=0; l<selectedGenes_.size(); l++){
				if (selectedGenes_.get(l).getId().equals(selection_str)){
					
					geneIDTextBox.setText(selectedGenes_.get(l).getId());
					annotationTextBox.setText(selectedGenes_.get(l).getAnnotation());
					geneNameTextBox.setText(selectedGenes_.get(l).getName());
				}
			}
		}
		else if (event.getSource().equals(selectableListSelectionModel)){

			int selectedIndex = 0;
			int minIndex = selectableListSelectionModel.getMinSelectionIndex();
	        int maxIndex = selectableListSelectionModel.getMaxSelectionIndex();
	        for (int i = minIndex; i <= maxIndex; i++) {
	        	if (selectableListSelectionModel.isSelectedIndex(i)) {
	        		//output.append(" " + i);
	                selectedIndex = i;
	            }
	        }
					
					
		}

		//String selection_str = (String) list_.getSelectedValue();
		//int selection = (int) list_.getSelectedIndex();
		
        
	}
*/	
	
	private class GeneTableModel extends AbstractTableModel
	{
		private static final int SELECTABLE = 0;
		
		private static final int SELECTED = 1;
		
		private String[] columnNames_ = {"Gene ID", "Gene Name", "Annotation"};
		
		private int table_;
		
		private GeneTableModel(int table)
		{
			table_ = table;
		}
		
		@Override
		public int getRowCount()
		{
			if (table_ == SELECTABLE)
				return selectableGenesTableData.length;
			
			return selectedGenesTableData.length;
		}

		@Override
		public int getColumnCount()
		{
			int column = 0;
			/*if (table_ == SELECTABLE)
				column = selectableGenesTableData[0].length;
			else 
				column = selectedGenesTableData[0].length;
			*/
			
			if (column == 0)
				return 3;
			else 
				return column;
		}
		
		@Override
		public String getColumnName(int columnIndex)
		{
			return columnNames_[columnIndex];
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			if (table_ == SELECTABLE) {
				
				return selectableGenesTableData[rowIndex][columnIndex];
			} else {
				
				return selectedGenesTableData[rowIndex][columnIndex];
			}
			
			
		}
	}
}
