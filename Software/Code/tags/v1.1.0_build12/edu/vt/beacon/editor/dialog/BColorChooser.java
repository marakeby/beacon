package edu.vt.beacon.editor.dialog;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.vt.beacon.editor.dialog.BColorChooser;
//import edu.vt.beacon.editor.dialog.BColorChooser.NodePanel;

public class BColorChooser extends JDialog {
	
    // size of entity bound boxes, node port boxes, and edge point boxes
    public static int BOX_SIZE = 8;
    
    // size of the margin between edge end caps and target nodes
    public static int CAP_MARGIN = 10;
    
    // size of edge end caps
    public static int CAP_SIZE = 9;
    
    // size of the margin between node label text and the node boundary
    public static int LABEL_MARGIN = 10;
    
    // size of the margin between pathways and the canvas boundary
    public static int PATHWAY_MARGIN = 20;
    
    // size of the default stroke used in graphics rendering
    public static int STROKE_SIZE = 3;
    
    // size of the margin before, between, and after unit decorations
    public static int UNIT_MARGIN = 10;

	// change listener responding to color selections
	private static ChangeListener listener = null;
	
	private Color bkgColor,   // node background color
				  edgeColor,  // edge color
                  frgColor,   // node foreground color
                  lblColor;   // node label color
    
    private int size;  // font size
    
    // color chooser to select current color
    private static JColorChooser chooser = new JColorChooser();
    
    private JPanel preview,  // preview selected color for node/edge
                   result;   // store final color selection
    
    // serialized object ID
    private static final long serialVersionUID = 5865649447241984980L;

    public static final String BACKGROUND = "BKG";

    public static final String EDGE = "EDGE";

    public static final String FOREGROUND = "FRG";

    public static final String LABEL = "LBL";
    
    private String font,  // node label font
                   type;  // color chooser dialog type
    
    private BColorChooser(Container parent, Color arcColor, JPanel result) {
    	
        // set the default arc color
        this.edgeColor = arcColor;
        
        // create the preview panel
        preview = new EdgePanel();
        
        // set the result panel
        this.result = result;
        
        // set the dialog type
        type = EDGE;
        
        // build the dialog
        buildDialog(parent);
    }
    
    private BColorChooser(Container parent, Color bkg, Color frg,
            Color lbl, String font, int size, JPanel result, String type) {
        
        // set the default node colors
        bkgColor = bkg;
        frgColor = frg;
        lblColor = lbl;
        
        // set the font size
        this.size = size;
        
        // set the node label font
        this.font = font;
        
        // create the preview panel
        preview = new NodePanel();
        
        // set the result panel
        this.result = result;
        
        // set the dialog type
        this.type = type;
        
        // build the dialog
        buildDialog(parent);
    }
    
    private BColorChooser(Container parent, Color lbl, String font, int size, JPanel result, String type) {
        
        // set the default node colors
        bkgColor = lbl;
        frgColor = lbl;
        lblColor = lbl;
        
        // set the font size
        this.size = size;
        
        // set the node label font
        this.font = font;
        
        // create the preview panel
        preview = new NodePanel();
        
        // set the result panel
        this.result = result;
        
        // set the dialog type
        this.type = type;
        
        // build the dialog
        buildDialog(parent);
    }
    
    private BColorChooser(Color lbl, String font, int size, JPanel result, String type) {
        
        // set the default node colors
        bkgColor = lbl;
        frgColor = lbl;
        lblColor = lbl;
        
        // set the font size
        this.size = size;
        
        // set the node label font
        this.font = font;
        
        // create the preview panel
        preview = new NodePanel();
        
        // set the result panel
        this.result = result;
        
        // set the dialog type
        this.type = type;
        
        // build the dialog
        buildDialog();
    }
    
    private BColorChooser(JPanel result) {
        
        // set the default node colors
        bkgColor = Color.WHITE;
        frgColor = Color.BLACK;
        lblColor = Color.RED;
        
        // set the font size
        this.size = 12;
        
        // set the node label font
        this.font = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames(null)[0];
        
        // create the preview panel
        preview = new NodePanel();
        
        // set the result panel
        this.result = result;
        
        // set the dialog type
        this.type = "PLAIN";
        
        // build the dialog
        //buildDialog(parent);
        buildDialog();
    }
    
    private void buildButtonPanel() {
        
        // create the button panel and set its properties
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        
        // create the approve button and set its properties
        JButton approveButton = new JButton("OK");
        approveButton.addActionListener(new ApproveListener());
        
        // create the reset button and set its properties
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(new ResetListener());
        
        // create the cancel button and set its properties
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new CancelListener());
        
        // add each dialog button to the button panel
        buttonPanel.add(cancelButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(approveButton);
        
        // add the button panel to the dialog
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void buildColorPanel(JPanel contentPanel) {
        
        // create the color panel and set its properties
        JPanel colorPanel = new JPanel();
        colorPanel.setBorder(BorderFactory.createEmptyBorder(
                10, 10, 10, 10));
        
        // set the color chooser preview
        chooser.setPreviewPanel(new JPanel());
        
        // if a current change listener exists
        if(listener != null)
        	
        	// remove the current change listener
        	chooser.getSelectionModel().removeChangeListener(listener);
        
        // if a node background dialog is shown
        if(type.equals(BACKGROUND)) {
            
            // set the appropriate color
            chooser.setColor(bkgColor);
            
            // set the appropriate change listener
            listener = new BkgListener();
            chooser.getSelectionModel().addChangeListener(listener);
            
        // if a node foreground dialog is shown
        } else if(type.equals(FOREGROUND)) {
            
            // set the appropriate color
            chooser.setColor(frgColor);
            
            // set the appropriate change listener
            listener = new FrgListener();
            chooser.getSelectionModel().addChangeListener(listener);
            
        // if a node label dialog is shown
        } else if(type.equals(LABEL)) {
            
            // set the appropriate color
            chooser.setColor(lblColor);
            
            // set the appropriate change listener
            listener = new LblListener();
            chooser.getSelectionModel().addChangeListener(listener);
            
        // if an edge dialog is shown
        } else {
            
            // set the appropriate color
            chooser.setColor(edgeColor);
            
            // set the appropriate change listener
            listener = new EdgeListener();
            chooser.getSelectionModel().addChangeListener(listener);
        }
        
        // add the color chooser to the color panel
        colorPanel.add(chooser);
        
        // add the color panel to the content panel
        contentPanel.add(colorPanel);
    }
    
    private void buildContentPane() {
        
        // set the layout of the content pane
        getContentPane().setLayout(new BorderLayout());
        
        // build the button panel
        buildButtonPanel();
        
        // create the content panel and set its properties
        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(BorderFactory.createEtchedBorder());
        contentPanel.setLayout(new BoxLayout(
                contentPanel, BoxLayout.Y_AXIS));
        
        // build the color panel
        buildColorPanel(contentPanel);
        
        // build the preview panel
        buildPreviewPanel(contentPanel);
        
        // add the content panel to the dialog
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void buildDialog(Container parent) {
        
        // set the dialog properties
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setModal(true);
        setResizable(false);
        setTitle("Color Chooser");
        
        // build the content pane
        buildContentPane();
        
        // pack the dialog size
        pack();
        
        // set the location of the dialog
        setLocationRelativeTo(parent);
        
        // display the dialog
        setVisible(true);
    }
    private void buildDialog() {
        
        // set the dialog properties
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setModal(true);
        setResizable(false);
        setTitle("Color Chooser");
        
        // build the content pane
        buildContentPane();
        
        // pack the dialog size
        pack();
        
        // set the location of the dialog
        //setLocationRelativeTo(parent);
        
        // display the dialog
        setVisible(true);
    }
    
    private void buildPreviewPanel(JPanel contentPanel) {
        
        // create the container panel and set its properties
        JPanel containerPanel = new JPanel();
        containerPanel.setBorder(BorderFactory.createEmptyBorder(
                0, 15, 10, 15));
        containerPanel.setLayout(new GridLayout(0, 1));
        
        // create the preview panel and set its properties
        JPanel prevPanel = new JPanel();
        prevPanel.setBackground(Color.WHITE);
        prevPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Preview"));
        
        // add the preview to the preview panel
        prevPanel.add(preview);
        
        // add the preview panel to the container panel
        containerPanel.add(prevPanel);
        
        // add the container panel to the content panel
        contentPanel.add(containerPanel);
    }
    
    public static void showEdgeDialog(Container parent, Color edgeColor,
            JPanel result) {
        
        // create a new edge dialog
        new BColorChooser(parent, edgeColor, result);
    }
    
    public static void showNodeDialog(Container parent, Color lbl, String font, int size, JPanel result,
            String type) {
        
        // create a new node dialog
        new BColorChooser(parent, lbl,
                font, size, result, type);
    }
    
    public static void showNodeDialog(Container parent, Color bkg,
            Color frg, Color lbl, String font, int size, JPanel result,
            String type) {
        
        // create a new node dialog
        new BColorChooser(parent, bkg, frg, lbl,
                font, size, result, type);
    }
    
    
   
    public static void showNodeDialog( JPanel result ) {
        
        // create a new node dialog
        new BColorChooser( result );
    }
    
    private class ApproveListener implements ActionListener {
        
        @Override
        public void actionPerformed(ActionEvent ae) {
            
            // update the color of the results panel
            result.setBackground(chooser.getColor());
            
            // close the dialog
            dispose();
        }
    }
    
    private class BkgListener implements ChangeListener {
        
        @Override
        public void stateChanged(ChangeEvent ce) {
            
            // update the node preview background
            ((NodePanel) preview).setBackground(chooser.getColor());
        }
    }
    
    private class CancelListener implements ActionListener {
        
        @Override
        public void actionPerformed(ActionEvent ae) {
            
            // close the dialog
            dispose();
        }
    }
    
    private class EdgeListener implements ChangeListener {
        
        @Override
        public void stateChanged(ChangeEvent ce) {
            
            // update the edge preview foreground
            ((EdgePanel) preview).setForeground(chooser.getColor());
        }
    }
    
    private class EdgePanel extends JPanel {
        
        /**
		 * 
		 */
		private static final long serialVersionUID = -8527457890090376017L;

		public EdgePanel() {
            
            // set the panel properties
            setBackground(Color.WHITE);
            setForeground(edgeColor);
            setLayout(new GridLayout(0, 1));
            setPreferredSize(new Dimension(150, 75));
            
            // create the label text and set its properties
            JLabel label = new JLabel("Sample Arc");
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
            
            // add the label text to the panel
            add(label);
        }
        
        @Override
        public void paintComponent(Graphics g) {
            
            // call the superclass method
            super.paintComponent(g);
            
            // cast the graphics as a 2D graphics object
            Graphics2D graph2D = (Graphics2D) g;
            
            // set the rendering color
            graph2D.setColor(getForeground());
            
            // set the rendering stroke
            graph2D.setStroke(new BasicStroke(3));
            
            // draw the sample arc (negative influence)
            graph2D.drawLine(5, 50, 145, 50);
            graph2D.drawLine(145, 40, 145, 60);
        }
    }
    
    private class FrgListener implements ChangeListener {
        
        @Override
        public void stateChanged(ChangeEvent ce) {
            
            // update the node preview foreground
            ((NodePanel) preview).setBorder(
                    BorderFactory.createLineBorder(
                    chooser.getColor(), 3));
        }
    }
    
    private class LblListener implements ChangeListener {
        
        @Override
        public void stateChanged(ChangeEvent ce) {
            
            // update the node preview label
            ((NodePanel) preview).setLabelColor(chooser.getColor());
        }
    }
    
    private class NodePanel extends JPanel {
        
        /**
		 * 
		 */
		private static final long serialVersionUID = 2637619059543923901L;
		private JLabel nodeLabel;  // sample glyph label
        
        public NodePanel() {
            
            // set the panel properties
            setBackground(bkgColor);
            setBorder(BorderFactory.createLineBorder(frgColor, 3));
            setLayout(new GridLayout(0, 1));
            
            // create the node label and set its properties
            nodeLabel = new JLabel("Sample Node");
            nodeLabel.setFont(new Font(font, Font.PLAIN, size));
            nodeLabel.setForeground(lblColor);
            nodeLabel.setHorizontalAlignment(SwingConstants.CENTER);
            nodeLabel.setVerticalAlignment(SwingConstants.CENTER);
            
            // set the preferred size of the panel
            setPreferredSize(new Dimension(
                    nodeLabel.getPreferredSize().width +
                    LABEL_MARGIN * 2,
                    nodeLabel.getPreferredSize().height +
                    LABEL_MARGIN * 2));
            
            // add the node label to the panel
            add(nodeLabel);
        }
        
        public void setLabelColor(Color c) {
            
            // set the label text of the node label
            nodeLabel.setForeground(c);
        }
    }
    
    private class ResetListener implements ActionListener {
        
        @Override
        public void actionPerformed(ActionEvent ae) {
            
            // restore the original color of the results panel
            chooser.setColor(result.getBackground());
        }
    }
}