package edu.vt.beacon.editor.dialog.font;


import edu.vt.beacon.editor.command.Command;
import edu.vt.beacon.editor.command.CommandType;
import edu.vt.beacon.editor.dialog.BColorChooser;
import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.document.DocumentState;
import edu.vt.beacon.editor.util.PlatformManager;
import edu.vt.beacon.graph.AbstractEntity;
import edu.vt.beacon.graph.glyph.AbstractGlyph;
import edu.vt.beacon.graph.glyph.node.AbstractNode;
import edu.vt.beacon.graph.legend.Legend;
import edu.vt.beacon.layer.Layer;
import edu.vt.beacon.map.Map;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Date;


public final class FontDialog extends JDialog
        implements ActionListener, ChangeListener {
    private static final long serialVersionUID = 1L;

    private Document document_;

    private JDialog FormatFont_;
    private JPanel formatPanel_;

    private JButton cancelButton;
    private JButton approveButton;

    private JPanel bkgValue,  // select node background color
            lblValue;  // select node label color
    protected Color bkgColor, frgColor, lblColor;  // original node label color

    private JPanel fontPanel_;
    private JComboBox fontChooserNames_;
    private JComboBox fontChooserSizes_;
    private JComboBox fontChooserTypes_;


    protected ArrayList<JLabel> labels;

    public FontDialog(Document document) {
        super(document.getFrame(), ModalityType.DOCUMENT_MODAL);

        setResizable(false);

        document_ = document;

        formatPanel_ = new JPanel(new BorderLayout());
        formatPanel_.setBorder(BorderFactory.createEtchedBorder());
        formatPanel_.setLayout(new BorderLayout());

        buildButtonPanel();
        buildFontPanel();
        setInitialValuesInDialog();

        add(formatPanel_);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                document_.getState().apply(document_);
                super.windowClosing(e);
            }
        });

        pack();
        setLocationRelativeTo(getOwner());
        setVisible(true);
    }

    private void setInitialValuesInDialog() {
        Map map = document_.getBrowserMenu().getSelectedMap();

        if (map.getLegend() != null && map.getLegend().isSelected()) {

            Legend legend = map.getLegend();
            setDialogValues(legend.getFont().getName(), legend.getFont().getSize(), legend.getFontStyle(), legend.getFontColor());

        } else {

            if (map.getSelectedGlyphs() != null && map.getSelectedGlyphs().size() == 1 &&
                    map.getSelectedGlyphs().get(0) instanceof AbstractNode &&
                    !((AbstractNode) map.getSelectedGlyphs().get(0)).getFont().getName().equalsIgnoreCase("Default")) {

                AbstractNode selectedNode = (AbstractNode) map.getSelectedGlyphs().get(0);
                setDialogValues(selectedNode.getFont().getName(), selectedNode.getFont().getSize(), selectedNode.getFont().getStyle(),
                        selectedNode.getFontColor());

            }

        }
    }

    private void setDialogValues(String name, int size, int style, Color color) {

        fontChooserNames_.setSelectedItem(name);
        fontChooserSizes_.setSelectedItem(size + "");

        if (style == 0)
            fontChooserTypes_.setSelectedItem("Plain");
        else if (style == 1)
            fontChooserTypes_.setSelectedItem("Bold");
        else if (style == 2)
            fontChooserTypes_.setSelectedItem("Italic");
        else if (style == 3)
            fontChooserTypes_.setSelectedItem("Bold-italic");

        lblValue.setBackground(color);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Layer layer;
        AbstractGlyph glyph;
        Map map = document_.getBrowserMenu().getSelectedMap();

        if (event.getSource().equals(approveButton)) {
            new DocumentState(document_, "Font", false);
            this.dispose();
        } else if (event.getSource().equals(cancelButton)) {
            document_.getState().apply(document_);
            this.dispose();
        }

        if (map.getLegend() != null && map.getLegend().isSelected()) {

            if (event.getSource().equals(fontChooserNames_))
                applyFontName(map.getLegend());

            else if (event.getSource().equals(fontChooserSizes_))
                // call method for size change with glyph
                applyFontSize(map.getLegend());

            else if (event.getSource().equals(fontChooserTypes_))
                // call method for type change
                applyFontType(map.getLegend());

        } else {
            for (int i = 0; i < map.getLayerCount(); i++) {

                layer = map.getLayerAt(i);

                if (layer.isActive()) {

                    for (int j = 0; j < layer.getGlyphCount(); j++) {

                        glyph = layer.getGlyphAt(j);

                        if (glyph.isSelected()) {

                            if (event.getSource().equals(fontChooserNames_))
                                applyFontName(glyph);

                            else if (event.getSource().equals(fontChooserSizes_))
                                // call method for size change with glyph
                                applyFontSize(glyph);

                            else if (event.getSource().equals(fontChooserTypes_))
                                // call method for type change
                                applyFontType(glyph);

                        }
                    }
                }
            }
        }

        document_.getCanvas().repaint();
        document_.getStateManager().insert(new Command(CommandType.CHANGING_GLYPH_STYLE, document_.getPathway().copy(),
                document_.getCanvas().getZoomFactor(), new Date().getTime()));

    }

    private void applyFontName(AbstractEntity entity) {
        if (!(entity instanceof AbstractNode || entity instanceof Legend))
            return;

        if (entity instanceof AbstractNode) {

            AbstractNode node = (AbstractNode) entity;

            Font font = node.getFont();
            String fontName = (String) fontChooserNames_.getSelectedItem();

            node.setFont(new Font(fontName, font.getStyle(), font.getSize()));

        } else {

            Legend legend = (Legend) entity;

            Font font = legend.getFont();
            String fontName = (String) fontChooserNames_.getSelectedItem();

            legend.setFont(new Font(fontName, font.getStyle(), font.getSize()));

        }
    }

    private void applyFontSize(AbstractEntity entity) {
        if (!(entity instanceof AbstractNode || entity instanceof Legend))
            return;

        if (entity instanceof AbstractNode) {

            AbstractNode node = (AbstractNode) entity;

            Font font = node.getFont();

            int fontSize = Integer.parseInt((String) fontChooserSizes_.getSelectedItem());

            node.setFont(new Font(font.getFontName(), font.getStyle(), fontSize));

        } else {

            Legend legend = (Legend) entity;

            Font font = legend.getFont();

            int fontSize = Integer.parseInt((String) fontChooserSizes_.getSelectedItem());

            legend.setFont(new Font(font.getFontName(), font.getStyle(), fontSize));

        }
    }

    private void applyFontType(AbstractEntity entity) {
        if (!(entity instanceof AbstractNode || entity instanceof Legend))
            return;

        if (entity instanceof AbstractNode) {

            AbstractNode node = (AbstractNode) entity;

            Font font = node.getFont();
            String fontType = (String) fontChooserTypes_.getSelectedItem();

            int style = 0;

            if (fontType.equalsIgnoreCase("Plain"))
                style = 0;
            else if (fontType.equalsIgnoreCase("Bold"))
                style = 1;
            else if (fontType.equalsIgnoreCase("Italic"))
                style = 2;
            else if (fontType.equalsIgnoreCase("Bold-italic"))
                style = 3;
            else
                style = 0;

            node.setFont(new Font(font.getFontName(), style, font.getSize()));

        } else {

            Legend legend = (Legend) entity;

            Font font = legend.getFont();
            String fontType = (String) fontChooserTypes_.getSelectedItem();

            int style = 0;

            if (fontType.equalsIgnoreCase("Plain"))
                style = 0;
            else if (fontType.equalsIgnoreCase("Bold"))
                style = 1;
            else if (fontType.equalsIgnoreCase("Italic"))
                style = 2;
            else if (fontType.equalsIgnoreCase("Bold-italic"))
                style = 3;
            else
                style = 0;

            legend.setFont(new Font(font.getFontName(), style, font.getSize()));

        }

        document_.getCanvas().repaint();
    }

    private void buildFontPanel() {

        fontPanel_ = new JPanel();
        fontPanel_.setBorder(BorderFactory.createRaisedBevelBorder());
        fontPanel_.setLayout(new FlowLayout(BoxLayout.LINE_AXIS));

        fontPanel_.setBounds(new Rectangle(100, 350));


        String[] fontSizes = {"5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "18", "20", "22", "24", "26", "28", "30", "32", "34", "36", "38", "40", "42", "44", "46", "48", "50", "52", "54", "56", "58", "60", "65", "70", "75", "80"};
        String[] fontTypes = {"Plain", "Bold", "Italic", "Bold-italic"};


        String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames(null);
        fontChooserNames_ = new JComboBox(fontNames);
        fontChooserNames_.setRenderer(new FontCellRenderer());

        fontChooserSizes_ = new JComboBox(fontSizes);
        fontChooserSizes_.setRenderer(new FontCellRenderer());

        fontChooserTypes_ = new JComboBox(fontTypes);
        fontChooserTypes_.setRenderer(new FontCellRenderer());

        fontPanel_.add(fontChooserNames_);

        fontPanel_.add(fontChooserSizes_);

        fontPanel_.add(fontChooserTypes_);


        ////////////////////////////////////////////////////
        //font color chooser
        ///////////////////////////////////////////////////
        // create the label color panel and set its properties

        JPanel lblPanel = new JPanel();
        lblPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // create the array of property labels
        labels = new ArrayList<JLabel>();


        // add the appropriate property label to the label color panel
        labels.add(new JLabel("Font Color: "));
        lblPanel.add(labels.get(labels.size() - 1));


        // create the label value panel and set its properties
        lblValue = new JPanel();
        lblValue.addMouseListener(new ColorListener(BColorChooser.LABEL));

        lblValue.setBackground(lblColor);
        lblValue.setBorder(BorderFactory.createLoweredBevelBorder());
        lblValue.setPreferredSize(new Dimension(25, 25));

        // add the label value panel to the label color panel
        lblPanel.add(lblValue);

        fontPanel_.add(lblPanel);

        /////////////////////////////////////////////////////////////

        formatPanel_.add(fontPanel_, BorderLayout.EAST);

        fontChooserNames_.addActionListener(this);
        fontChooserSizes_.addActionListener(this);
        fontChooserTypes_.addActionListener(this);

        fontPanel_.setVisible(true);
    }

    private void buildButtonPanel() {

        // create the button panel and set its properties
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        // create each dialog button

        cancelButton = new JButton("Cancel");
        approveButton = new JButton("OK");


        // add each dialog button to the button panel
        if (PlatformManager.isMacPlatform()) {
            buttonPanel.add(cancelButton);
            buttonPanel.add(approveButton);
        } else {
            buttonPanel.add(approveButton);
            buttonPanel.add(cancelButton);
        }

        approveButton.addActionListener(this);
        cancelButton.addActionListener(this);

        // add the button panel to the content pane
        formatPanel_.add(buttonPanel, BorderLayout.SOUTH);
    }

    @Override
    public void stateChanged(ChangeEvent event) {

    }


    private class ColorListener implements MouseListener {

        private String type;  // color type

        public ColorListener(String type) {

            // set the color type
            this.type = type;
        }

        @Override
        public void mouseClicked(MouseEvent me) {
        }

        @Override
        public void mouseEntered(MouseEvent me) {
        }

        @Override
        public void mouseExited(MouseEvent me) {
        }

        @Override
        public void mousePressed(MouseEvent me) {

            // show a new color chooser for the selected color panel
            BColorChooser.showNodeDialog(FormatFont_, bkgColor, frgColor, lblValue.getBackground(),
                    GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames(null)[0],
                    24, (JPanel) me.getSource(), type);


            if (me.getSource().equals(lblValue)) {

                Layer layer;
                AbstractGlyph glyph;
                AbstractNode node;

                Map map = document_.getBrowserMenu().getSelectedMap();

                if (map.getLegend() != null && map.getLegend().isSelected()) {

                    map.getLegend().setFontColor(lblValue.getBackground());

                } else {

                    for (int i = 0; i < map.getLayerCount(); i++) {

                        layer = map.getLayerAt(i);

                        if (layer.isActive()) {

                            for (int j = 0; j < layer.getGlyphCount(); j++) {

                                glyph = layer.getGlyphAt(j);

                                if (glyph instanceof AbstractNode && glyph.isSelected()) {

                                    node = (AbstractNode) glyph;
                                    node.setFontColor(lblValue.getBackground());

                                }
                            }
                        }
                    }
                }

                document_.getCanvas().repaint();

            }


        }

        @Override
        public void mouseReleased(MouseEvent me) {
        }
    }

    private class ButtonListener implements MouseListener {

        public ButtonListener() {
        }

        @Override
        public void mouseClicked(MouseEvent me) {
        }

        @Override
        public void mouseEntered(MouseEvent me) {
        }

        @Override
        public void mouseExited(MouseEvent me) {
        }

        @Override
        public void mousePressed(MouseEvent me) {
//            System.out.println("out-ok");
            if (me.getSource().equals(approveButton)) {
//                System.out.println("ok");
                FormatFont_.dispose();
            }
        }

        @Override
        public void mouseReleased(MouseEvent arg0) {

        }
    }

}