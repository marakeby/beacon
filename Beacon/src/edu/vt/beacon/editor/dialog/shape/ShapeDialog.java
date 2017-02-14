package edu.vt.beacon.editor.dialog.shape;

import edu.vt.beacon.editor.command.Command;
import edu.vt.beacon.editor.command.CommandType;
import edu.vt.beacon.editor.dialog.AbstractDialog;
import edu.vt.beacon.editor.dialog.BColorChooser;
import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.document.DocumentState;
import edu.vt.beacon.editor.swing.EditingTextField;
import edu.vt.beacon.graph.AbstractEntity;
import edu.vt.beacon.graph.Orientable;
import edu.vt.beacon.graph.OrientationType;
import edu.vt.beacon.graph.glyph.AbstractGlyph;
import edu.vt.beacon.graph.glyph.GlyphType;
import edu.vt.beacon.graph.glyph.node.activity.BiologicalActivity;
import edu.vt.beacon.graph.glyph.node.auxiliary.AuxiliaryUnit;
import edu.vt.beacon.graph.glyph.node.container.Compartment;
import edu.vt.beacon.layer.Layer;
import edu.vt.beacon.map.Map;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;

public class ShapeDialog extends AbstractDialog implements ActionListener,
        ItemListener, FocusListener {

    // GUI tab label constants
    private static final String TAB_SHAPE = "Shape";
    private static final String TAB_UOI = "Unit of Information";

    // GUI label constants
    private static final String LABEL_COMBOBOX_UOI = "Unit of Information: ";
    private static final String LABEL_LINE_WIDTH = "Line Width: ";
    private static final String LABEL_PADDING = "Padding: ";
    private static final String LABEL_LINE_COLOR = "Line Color: ";
    private static final String LABEL_FILL_COLOR = "Fill Color: ";
    private static final String LABEL_HYPO = "Hypothetical";
    private static final String LABEL_ORIENT = "Orientation: ";

    //Legend Label constants
    private static final String LEGEND_LABEL_COLOR_BAR_WIDTH = "Color Bar Width: ";
    private static final String LEGEND_LABEL_COLOR_BAR_HEIGHT = "Color Bar Height: ";
    private static final String LEGEND_LABEL_INTER_ENTRY_DISTANCE = "Distance Between Entries: ";
    private static final String LEGEND_LABEL_COLOR_BAR_TEXT_DISTNCE = "Distance Between Color Bars and Descriptions: ";


    // GUI combobox constants
    private static final String[] CHOICES_UOI = {"macromolecule",
            "nucleic acid feature", "simple chemical", "unspecified entity", "complex"};
    private static final String[] CHOICES_ORIENTATION = {"Up", "Down", "Left", "Right"};
    private static final String CHOICE_UP = "Up";
    private static final String CHOICE_DOWN = "Down";
    private static final String CHOICE_LEFT = "Left";
    private static final String CHOICE_RIGHT = "Right";

    // GUI action command constants
    private static final String AC_COMBOBOX_UOI = "COMBOBOX_UOI";
    private static final String AC_COMBOBOX_ORIENTATION = "COMBOBOX_ORIENT";
    private static final String AC_CHECKBOX_HYPO = "CHECKBOX_HYPO";
    private static final String AC_TEXTFIELD_LINE_WIDTH = "LINE_WIDTH";
    private static final String AC_TEXTFIELD_PADDING = "PADDING";

    // GUI component names (for validation purposes)
    private static final String NAME_LINE_WIDTH = "Line width";
    private static final String NAME_PADDING = "Padding";

    private JTabbedPane tabbedPane_;
    private JPanel fillColorButtonPanel_;
    private static JPanel lineColorButtonPanel_;
    private JComboBox orientationComboBox_;

    private float lineWidth_;
    private float padding_;
    private Color lineColor_;
    private Color fillColor_;
    boolean hypothetical_;
    boolean isOnlyOrientable_;
    private boolean isOnlyBiologicalActivities_;

    private float colorBarWidth_;
    private float colorBarHeight_;
    private float interEntryDistance_;
    private float colorBarTextDistance_;

    private ShapeListner sl = new ShapeListner();

    public ShapeDialog(Document document) throws HeadlessException {
        super(document, document.getFrame());
        setResizable(false);
        setModalityType(ModalityType.DOCUMENT_MODAL);

        if (isDefaultOrNoGlyphSelected()) {
            loadDefaultValues();
        } else {
            generateConsensusSettings();
        }

        add(createTabbedPane());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                document_.getState().apply(document_);
                super.windowClosing(e);
            }
        });

        if (!isOnlyBiologicalActivities_) {
            tabbedPane_.setEnabledAt(1, false);
            // html hack to make tabbed title appear disabled (see if better way)
            tabbedPane_.setTitleAt(1, "<html><font color=#BDBDBD>"
                    + tabbedPane_.getTitleAt(1) + "</font></html>");
        }

        pack();
        setLocationRelativeTo(getOwner());

        setVisible(true);

    }


    private void loadDefaultValues() {
        lineWidth_ = document_.getFloat("glyph.lineWidth");
        padding_ = document_.getFloat("glyph.padding");
        lineColor_ = document_.getColor("glyph.foreground");
        fillColor_ = document_.getColor("glyph.background");
        hypothetical_ = document_.getBoolean("glyph.hypothetical");
    }


    private Component createTabbedPane() {
        tabbedPane_ = new JTabbedPane();

        JComponent shapePanel = createContentPanel(false);
        tabbedPane_.addTab(TAB_SHAPE, shapePanel);
        tabbedPane_.setMnemonicAt(0, KeyEvent.VK_1);

        JComponent unitOfInfoPanel = createContentPanel(true);
        tabbedPane_.addTab(TAB_UOI, unitOfInfoPanel);
        tabbedPane_.setMnemonicAt(1, KeyEvent.VK_2);

        return tabbedPane_;
    }

    private JComponent createContentPanel(boolean isUnitOfInfoTab) {
        JComponent panel = createBasePanel();
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints c = new GridBagConstraints();

        edu.vt.beacon.graph.legend.Legend legend = document_.getPathway().getMap().getLegend();

        int x = 0;
        int y = 0;

        // Add Unit of Information combobox selection on Unit of Info Tab
//        if (isUnitOfInfoTab) {
//            JLabel unitOfInfoComboBoxLabel = new JLabel(LABEL_COMBOBOX_UOI);
//            c.gridx = x;
//            c.gridy = y;
//            c.gridwidth = 2;
//            contentPanel.add(unitOfInfoComboBoxLabel, c);
//
//
//            JComboBox unitOfInfoCatComboBox = new JComboBox(CHOICES_UOI);
//            c.gridx = x + 2;
//            c.gridy = y;
//            contentPanel.add(unitOfInfoCatComboBox, c);
//            x = 0;
//            y = 1;
//            c.gridwidth = 1;
//        }

        JLabel lineWidthLabel = new JLabel(LABEL_LINE_WIDTH);
        c.gridx = x++;
        c.gridy = y;
        c.anchor = GridBagConstraints.LINE_START;
        contentPanel.add(lineWidthLabel, c);

        EditingTextField lineWidthTextBox = getTextField(5, AC_TEXTFIELD_LINE_WIDTH);
        lineWidthTextBox.setBorder(BorderFactory.createMatteBorder(
                1, 1, 1, 1, Color.BLACK));
        ShapeInputVerifier lineWidthInputVerifier = new ShapeInputVerifier();
        JLabel errorLabel = new JLabel(" ");
        lineWidthInputVerifier.setErrorLabel_(errorLabel);
        lineWidthInputVerifier.setParent_(this);
        lineWidthTextBox.setInputVerifier(lineWidthInputVerifier);

        lineWidthTextBox.setName(NAME_LINE_WIDTH);
        lineWidthTextBox.addFocusListener(this);
        lineWidthTextBox.addFocusListener(sl);
        if (lineWidth_ != -1) {
            lineWidthTextBox.setText("" + lineWidth_);
        }
        c.gridx = x++;
        c.gridy = y;
        contentPanel.add(lineWidthTextBox, c);

        JLabel lineColorLabel = new JLabel(LABEL_LINE_COLOR);
        c.gridx = x++;
        c.gridy = y;
        contentPanel.add(lineColorLabel, c);


        lineColorButtonPanel_ = getColorPanel(lineColor_);
        lineColorButtonPanel_.addMouseListener(new ShapeColorListener(BColorChooser.LABEL,
                isUnitOfInfoTab, document_, lineColorButtonPanel_, this));
        c.gridx = x;
        c.gridy = y++;
        contentPanel.add(lineColorButtonPanel_, c);
        x = 0; // New Row


        JLabel paddingLabel = new JLabel(LABEL_PADDING);
        c.gridx = x++;
        c.gridy = y;
        contentPanel.add(paddingLabel, c);


        EditingTextField paddingTextBox = getTextField(5, AC_TEXTFIELD_PADDING);
        paddingTextBox.setBorder(BorderFactory.createMatteBorder(
                1, 1, 1, 1, Color.BLACK));
        ShapeInputVerifier paddingInputVerifier = new ShapeInputVerifier();
        paddingInputVerifier.setErrorLabel_(errorLabel);
        paddingInputVerifier.setParent_(this);
        paddingTextBox.setInputVerifier(paddingInputVerifier);
        paddingTextBox.setName(NAME_PADDING);
        paddingTextBox.addFocusListener(this);
        paddingTextBox.addFocusListener(sl);
        if (padding_ != -1) {
            paddingTextBox.setText("" + padding_);
        }
        c.gridx = x++;
        c.gridy = y;
        contentPanel.add(paddingTextBox, c);

        JLabel fillColorLabel = new JLabel(LABEL_FILL_COLOR);
        c.gridx = x++;
        c.gridy = y;
        contentPanel.add(fillColorLabel, c);


        fillColorButtonPanel_ = getColorPanel(fillColor_);
        fillColorButtonPanel_.addMouseListener(new ShapeColorListener(BColorChooser.LABEL,
                isUnitOfInfoTab, document_, fillColorButtonPanel_, this, true));
        c.gridx = x;
        c.gridy = y++;
        contentPanel.add(fillColorButtonPanel_, c);
        x = 0; // New Row

        c.gridx = x;
        c.gridy = y++;
        contentPanel.add(new JSeparator(JSeparator.HORIZONTAL), c);

        // Adding Legend Properties
        if (legend != null && legend.isSelected()) {

            JLabel colorBarWidthLabel = new JLabel(LEGEND_LABEL_COLOR_BAR_WIDTH);
            c.gridx = x++;
            c.gridy = y;
            contentPanel.add(colorBarWidthLabel, c);


            EditingTextField colorBarWidthTextBox = getTextField(5, LEGEND_LABEL_COLOR_BAR_WIDTH);
            colorBarWidthTextBox.setBorder(BorderFactory.createMatteBorder(
                    1, 1, 1, 1, Color.BLACK));
            ShapeInputVerifier colorBarWidthVerifier = new ShapeInputVerifier();
            JLabel colorBarWidthErrorLabel = new JLabel(" ");
            colorBarWidthVerifier.setErrorLabel_(colorBarWidthErrorLabel);
            colorBarWidthVerifier.setParent_(this);
            colorBarWidthTextBox.setInputVerifier(colorBarWidthVerifier);

            colorBarWidthTextBox.setName(LEGEND_LABEL_COLOR_BAR_WIDTH);
            colorBarWidthTextBox.addFocusListener(this);
            colorBarWidthTextBox.addFocusListener(sl);
            if (colorBarWidth_ != -1) {
                colorBarWidthTextBox.setText("" + colorBarWidth_);
            }
            c.gridx = x++;
            c.gridy = y;
            contentPanel.add(colorBarWidthTextBox, c);
            x = 0; // New Row
            y++;

            JLabel colorBarHeightLabel = new JLabel(LEGEND_LABEL_COLOR_BAR_HEIGHT);
            c.gridx = x;
            c.gridy = y;
            contentPanel.add(colorBarHeightLabel, c);


            EditingTextField colorBarHeightTextBox = getTextField(5, LEGEND_LABEL_COLOR_BAR_HEIGHT);
            colorBarHeightTextBox.setBorder(BorderFactory.createMatteBorder(
                    1, 1, 1, 1, Color.BLACK));
            ShapeInputVerifier colorBarHeightVerifier = new ShapeInputVerifier();
            JLabel colorBarHeightErrorLabel = new JLabel(" ");
            colorBarHeightVerifier.setErrorLabel_(colorBarHeightErrorLabel);
            colorBarHeightVerifier.setParent_(this);
            colorBarHeightTextBox.setInputVerifier(colorBarHeightVerifier);

            colorBarHeightTextBox.setName(LEGEND_LABEL_COLOR_BAR_HEIGHT);
            colorBarHeightTextBox.addFocusListener(this);
            colorBarHeightTextBox.addFocusListener(sl);
            if (colorBarHeight_ != -1) {
                colorBarHeightTextBox.setText("" + colorBarHeight_);
            }
            c.gridx = ++x;
            c.gridy = y++;
            contentPanel.add(colorBarHeightTextBox, c);
            x = 0; // New Row
            y++;

            JLabel interEntryDistanceLabel = new JLabel(LEGEND_LABEL_INTER_ENTRY_DISTANCE);
            c.gridx = x;
            c.gridy = y;
            contentPanel.add(interEntryDistanceLabel, c);


            EditingTextField interEntryDistanceTextBox = getTextField(5, LEGEND_LABEL_INTER_ENTRY_DISTANCE);
            interEntryDistanceTextBox.setBorder(BorderFactory.createMatteBorder(
                    1, 1, 1, 1, Color.BLACK));
            ShapeInputVerifier interEntryDistanceVerifier = new ShapeInputVerifier();
            JLabel interEntryDistanceErrorLabel = new JLabel(" ");
            interEntryDistanceVerifier.setErrorLabel_(interEntryDistanceErrorLabel);
            interEntryDistanceVerifier.setParent_(this);
            interEntryDistanceTextBox.setInputVerifier(interEntryDistanceVerifier);

            interEntryDistanceTextBox.setName(LEGEND_LABEL_INTER_ENTRY_DISTANCE);
            interEntryDistanceTextBox.addFocusListener(this);
            interEntryDistanceTextBox.addFocusListener(sl);
            if (interEntryDistance_ != -1) {
                interEntryDistanceTextBox.setText("" + interEntryDistance_);
            }
            c.gridx = ++x;
            c.gridy = y++;
            contentPanel.add(interEntryDistanceTextBox, c);
            x = 0; // New Row
            y++;

            JLabel colorBarTextDistanceLabel = new JLabel(LEGEND_LABEL_COLOR_BAR_TEXT_DISTNCE);
            c.gridx = x;
            c.gridy = y;
            contentPanel.add(colorBarTextDistanceLabel, c);


            EditingTextField colorBarTextDistanceTextBox = getTextField(5, LEGEND_LABEL_COLOR_BAR_TEXT_DISTNCE);
            colorBarTextDistanceTextBox.setBorder(BorderFactory.createMatteBorder(
                    1, 1, 1, 1, Color.BLACK));
            ShapeInputVerifier colorBarTextDistanceVerifier = new ShapeInputVerifier();
            JLabel colorBarTextDistanceErrorLabel = new JLabel(" ");
            colorBarTextDistanceVerifier.setErrorLabel_(colorBarTextDistanceErrorLabel);
            colorBarTextDistanceVerifier.setParent_(this);
            colorBarTextDistanceTextBox.setInputVerifier(colorBarTextDistanceVerifier);

            colorBarTextDistanceTextBox.setName(LEGEND_LABEL_COLOR_BAR_TEXT_DISTNCE);
            colorBarTextDistanceTextBox.addFocusListener(this);
            colorBarTextDistanceTextBox.addFocusListener(sl);
            if (colorBarTextDistance_ != -1) {
                colorBarTextDistanceTextBox.setText("" + colorBarTextDistance_);
            }
            c.gridx = ++x;
            c.gridy = y++;
            contentPanel.add(colorBarTextDistanceTextBox, c);
            x = 0; // New Row
            y++;


            c.gridx = x;
            c.gridy = y++;
            contentPanel.add(new JSeparator(JSeparator.HORIZONTAL), c);
        }

        if (!isUnitOfInfoTab) {
            JLabel orientationLabel = new JLabel(LABEL_ORIENT);
            c.gridx = x++;
            c.gridy = y;
            c.anchor = GridBagConstraints.LINE_START;
            contentPanel.add(orientationLabel, c);


            orientationComboBox_ = getComboBox(CHOICES_ORIENTATION,
                    AC_COMBOBOX_ORIENTATION);
            orientationComboBox_.setEditable(false);

            if (!isOnlyOrientable_ && !isDefaultOrNoGlyphSelected()) {
                orientationComboBox_.setEnabled(false);
            }
            c.gridx = x;
            c.gridy = y++;
            contentPanel.add(orientationComboBox_, c);
            x = 0; // New Row


            c.gridx = x;
            c.gridy = y++;
            contentPanel.add(new JSeparator(JSeparator.HORIZONTAL), c);
        }

        if (!isUnitOfInfoTab && (legend == null || !legend.isSelected())) {

            JCheckBox hypoCheckBox = getCheckBox(AC_CHECKBOX_HYPO);
            hypoCheckBox.addItemListener(this);
            hypoCheckBox.addItemListener(sl);
            if (hypothetical_) {
                hypoCheckBox.setSelected(true);
            }
            c.gridx = x++;
            c.gridy = y;
            c.anchor = GridBagConstraints.LINE_END;
            contentPanel.add(hypoCheckBox, c);


            JLabel hypoLabel = new JLabel(LABEL_HYPO);
            c.gridx = x++;
            c.gridy = y;
            c.anchor = GridBagConstraints.LINE_START;
            contentPanel.add(hypoLabel, c);
        }

        x = 0; //new Row
        c.gridx = x;
        c.gridy = ++y;
        c.anchor = GridBagConstraints.CENTER;
        c.gridwidth = 4;
        contentPanel.add(errorLabel, c);


        panel.add(contentPanel);
        return panel;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        boolean shapeActive = true;
        if (tabbedPane_.isEnabledAt(0)) {
            shapeActive = true;
        } else {
            shapeActive = false;
        }

        // Actions independent of glyphs selected
        String actionCommand = e.getActionCommand();

        if (actionCommand.equals(AC_OK_BUTTON))
            ok();

        else if (actionCommand.equals(AC_CANCEL_BUTTON))
            cancel();

        // Actions dependent on default
        if (isDefaultOrNoGlyphSelected()) {

            if (actionCommand.equals(AC_TEXTFIELD_LINE_WIDTH)) {
                document_.put("glyph.lineWidth", ((EditingTextField) e.getSource()).getText().trim());
                lineWidth_ = document_.getFloat("glyph.lineWidth");
            } else if (actionCommand.equals(AC_TEXTFIELD_PADDING)) {
                document_.put("glyph.padding",
                        ((EditingTextField) e.getSource()).getText().trim());
                padding_ = document_.getFloat("glyph.padding");
            } else if (actionCommand.equals(AC_COMBOBOX_ORIENTATION)) {
                document_.put("glyph.orientation", getOrientationType(e.getSource()).ordinal());
            }
        }

        // Actions dependent on glyphs selected
        if (!isDefaultOrNoGlyphSelected()) {

            if (actionCommand.equals(AC_TEXTFIELD_LINE_WIDTH))
                modifyGlyphs(new GlyphLineWidthChange(), e);

            else if (actionCommand.equals(AC_TEXTFIELD_PADDING))
                modifyGlyphs(new GlyphPaddingChange(), e);

            else if (actionCommand.equals(AC_COMBOBOX_ORIENTATION))
                modifyGlyphs(new GlyphOrientationChange(), e);

            else if (actionCommand.equals(LEGEND_LABEL_COLOR_BAR_WIDTH))
                modifyGlyphs(new LegendColorBarWidthChange(), e);

            else if (actionCommand.equals(LEGEND_LABEL_COLOR_BAR_HEIGHT))
                modifyGlyphs(new LegendColorBarHeightChange(), e);

            else if (actionCommand.equals(LEGEND_LABEL_COLOR_BAR_TEXT_DISTNCE))
                modifyGlyphs(new LegendColorBarTextDistanceChange(), e);

            else if (actionCommand.equals(LEGEND_LABEL_INTER_ENTRY_DISTANCE))
                modifyGlyphs(new LegendInterEntryDistanceChange(), e);
        }

        document_.getCanvas().repaint();
        document_.getStateManager().insert(new Command(CommandType.CHANGING_GLYPH_STYLE, document_.getPathway().copy(),
                document_.getCanvas().getZoomFactor(), new Date().getTime()));
    }

    private void cancel() {
        document_.getState().apply(document_);
        this.dispose();

    }

    public void ok() {
        if (isChanged()) {
            new DocumentState(document_, "Shape", false);
        }
        this.dispose();
    }

    private boolean isChanged() {
        // TODO Auto-generated method stub
        return true;
    }


    @Override
    public void itemStateChanged(ItemEvent e) {
        boolean hypo = ((JCheckBox) e.getSource()).isSelected() ? true : false;

        if (isDefaultOrNoGlyphSelected()) {
            document_.put("glyph.hypothetical", hypo);
            hypothetical_ = hypo;
        } else {
            modifyGlyphs(new GlyphHypoChange(), e);
        }

        document_.getCanvas().repaint();
    }

    public void generateConsensusSettings() {

        Float initialLineWidth = new Float(-1);
        Float initialPadding = new Float(-1);
        Float negativeResult = new Float(-1);
        Float initialColorBarWidth = new Float(-1);
        Float initialColorBarHeight = new Float(-1);
        Float initialInterEntryDistance = new Float(-1);
        Float initialColorBarTextDistance = new Float(-1);
        Color initialFillColor = Color.WHITE;
        Color initialLineColor = Color.WHITE;
        boolean initialHypothetical = true;
        boolean initialOrientable = true;

        Map map = document_.getBrowserMenu().getSelectedMap();

        if (map.getLegend() != null && map.getLegend().isSelected()) {

            edu.vt.beacon.graph.legend.Legend legend = map.getLegend();
            initialLineWidth = new Float(legend.getLineWidth());
            initialPadding = new Float(legend.getMargin());
            initialFillColor = legend.getBackgroundColor();
            initialLineColor = legend.getForegroundColor();
            initialColorBarWidth = legend.getColorBarWidth();
            initialColorBarHeight = legend.getColorBarHeight();
            initialInterEntryDistance = legend.getInterEntryDistance();
            initialColorBarTextDistance = legend.getColorBarTextDistance();

        } else {

            boolean isFirst = true;
            Layer layer;
            AbstractGlyph glyph;
            isOnlyBiologicalActivities_ = true;
            for (int i = 0; i < map.getLayerCount(); i++) {

                layer = map.getLayerAt(i);

                if (layer.isActive()) {

                    for (int j = 0; j < layer.getGlyphCount(); j++) {

                        glyph = layer.getGlyphAt(j);

                        if (glyph.isSelected()) {

                            if (isFirst) {
                                initialLineWidth = new Float(glyph.getLineWidth());
                                initialPadding = new Float(glyph.getPadding());
                                initialFillColor = glyph.getBackgroundColor();
                                initialLineColor = glyph.getForegroundColor();
                                initialHypothetical = glyph.isHypothetical();
                                isFirst = false;
                                if (glyph.getType() != GlyphType.BIOLOGICAL_ACTIVITY) {
                                    isOnlyBiologicalActivities_ = false;
                                }
                                if (glyph instanceof Orientable) {
                                } else {
                                    initialOrientable = false;
                                }

                            } else {
                                if (initialLineWidth.floatValue() != glyph.getLineWidth() && initialLineWidth != negativeResult) {
                                    initialLineWidth = negativeResult;
                                }
                                if (initialPadding.floatValue() != glyph.getLineWidth() && initialPadding != negativeResult) {
                                    initialPadding = negativeResult;
                                }
                                if (initialFillColor.getRGB() != glyph.getBackgroundColor().getRGB()
                                        && initialFillColor != null) {
                                    initialFillColor = null;
                                }
                                if (initialLineColor.getRGB() != glyph.getForegroundColor().getRGB()
                                        && initialLineColor != null) {
                                    initialLineColor = null;
                                }
                                if (glyph.getType() != GlyphType.BIOLOGICAL_ACTIVITY) {
                                    isOnlyBiologicalActivities_ = false;
                                }
                                if (initialHypothetical != glyph.isHypothetical() && initialLineColor != null) {
                                    initialHypothetical = false;
                                }
                                if (glyph instanceof Orientable) {
                                } else {
                                    initialOrientable = false;
                                }

                            }

                        }
                    }
                }
            }

        }

        lineWidth_ = initialLineWidth.floatValue();
        padding_ = initialPadding.floatValue();
        colorBarWidth_ = initialColorBarWidth;
        colorBarHeight_ = initialColorBarHeight;
        interEntryDistance_ = initialInterEntryDistance;
        colorBarTextDistance_ = initialColorBarTextDistance;
        lineColor_ = initialLineColor;
        fillColor_ = initialFillColor;
        hypothetical_ = initialHypothetical;
        isOnlyOrientable_ = initialOrientable;
    }


    @Override
    public void focusGained(FocusEvent e) {
        // TODO Auto-generated method stub

    }


    @Override
    public void focusLost(FocusEvent e) {
        if (e.getSource() instanceof EditingTextField) {
//            System.out.println(((EditingTextField) e.getSource()).getName());
            String name = ((EditingTextField) e.getSource()).getName();

            if (name.equals(NAME_LINE_WIDTH)) {

                if (isDefaultOrNoGlyphSelected()) {
                    document_.put("glyph.lineWidth", ((EditingTextField) e.getSource()).getText().trim());
                    lineWidth_ = document_.getFloat("glyph.lineWidth");
                } else {
                    modifyGlyphs(new GlyphLineWidthChange(), e);
                }

            } else if (name.equals(NAME_PADDING)) {
                if (isDefaultOrNoGlyphSelected()) {
                    document_.put("glyph.padding",
                            ((EditingTextField) e.getSource()).getText().trim());
                    padding_ = document_.getFloat("glyph.padding");
                } else {

                    modifyGlyphs(new GlyphPaddingChange(), e);
                }

            } else if (name.equals(LEGEND_LABEL_COLOR_BAR_WIDTH))
                modifyGlyphs(new LegendColorBarWidthChange(), e);

            else if (name.equals(LEGEND_LABEL_COLOR_BAR_HEIGHT))
                modifyGlyphs(new LegendColorBarHeightChange(), e);

            else if (name.equals(LEGEND_LABEL_COLOR_BAR_TEXT_DISTNCE))
                modifyGlyphs(new LegendColorBarTextDistanceChange(), e);

            else if (name.equals(LEGEND_LABEL_INTER_ENTRY_DISTANCE))
                modifyGlyphs(new LegendInterEntryDistanceChange(), e);

            document_.getCanvas().repaint();
        }

    }

    public OrientationType getOrientationType(Object source) {
        JComboBox cb = (JComboBox) source;
        String selection = (String) cb.getSelectedItem();

        if (selection.equals(CHOICE_UP))
            return OrientationType.UP;

        if (selection.equals(CHOICE_DOWN))
            return OrientationType.DOWN;

        if (selection.equals(CHOICE_LEFT))
            return OrientationType.LEFT;

        if (selection.equals(CHOICE_RIGHT))
            return OrientationType.RIGHT;

        return null;

    }

    protected class GlyphPaddingChange implements ShapeGlyphRunnable {

        @Override
        public void modifyGlyph(Object source, AbstractEntity entity, Document document_) {

            if (isShapeInfo()) {

                if (entity instanceof AbstractGlyph)
                    ((AbstractGlyph) entity).setPadding(Float.parseFloat(
                            ((EditingTextField) source).getText().trim()));

                else if (entity instanceof edu.vt.beacon.graph.legend.Legend)
                    ((edu.vt.beacon.graph.legend.Legend) entity).setMargin(Float.parseFloat(
                            ((EditingTextField) source).getText().trim()));

            } else { //Unit of Information

                AuxiliaryUnit aux = null;

                if (entity instanceof BiologicalActivity)
                    aux = ((BiologicalActivity) entity).getAuxiliaryUnit();

                else if (entity instanceof Compartment)
                    aux = ((Compartment) entity).getCompartmentUnit();

                if (aux == null)
                    return;

                aux.setPadding(Float.parseFloat(((EditingTextField) source).getText().trim()));

            }

        }

    }

    protected class GlyphLineWidthChange implements ShapeGlyphRunnable {

        @Override
        public void modifyGlyph(Object source, AbstractEntity entity, Document document_) {

            if (isShapeInfo()) {

                if (entity instanceof AbstractGlyph)
                    ((AbstractGlyph) entity).setLineWidth(Float.parseFloat(
                            ((EditingTextField) source).getText().trim()));

                else if (entity instanceof edu.vt.beacon.graph.legend.Legend)
                    ((edu.vt.beacon.graph.legend.Legend) entity).setLineWidth(Float.parseFloat(
                            ((EditingTextField) source).getText().trim()));

            } else { //Unit of Information

                AuxiliaryUnit aux = null;

                if (entity instanceof BiologicalActivity)
                    aux = ((BiologicalActivity) entity).getAuxiliaryUnit();

                else if (entity instanceof Compartment)
                    aux = ((Compartment) entity).getCompartmentUnit();

                if (aux == null)
                    return;

                aux.setLineWidth(Float.parseFloat(((EditingTextField) source).getText().trim()));

            }
        }

    }

    protected class GlyphHypoChange implements ShapeGlyphRunnable {

        @Override
        public void modifyGlyph(Object source, AbstractEntity entity, Document document_) {

            if (isShapeInfo()) {

                boolean hypo;
                if (((JCheckBox) source).isSelected()) {
                    hypo = true;
                } else {
                    hypo = false;
                }

                if (entity instanceof AbstractGlyph)
                    ((AbstractGlyph) entity).setHypothetical(hypo);

            }

        }

    }

    protected class GlyphOrientationChange implements ShapeGlyphRunnable {

        @Override
        public void modifyGlyph(Object source, AbstractEntity entity, Document document_) {
            if (entity instanceof AbstractGlyph || entity instanceof edu.vt.beacon.graph.legend.Legend)
                ((Orientable) entity).setOrientation(getOrientationType(source));

        }

    }

    protected class LegendColorBarWidthChange implements ShapeGlyphRunnable {

        @Override
        public void modifyGlyph(Object source, AbstractEntity entity, Document document_) {
            if (entity instanceof edu.vt.beacon.graph.legend.Legend)
                ((edu.vt.beacon.graph.legend.Legend) entity).setColorBarWidth(Float.parseFloat(
                        ((EditingTextField) source).getText().trim()));

        }

    }

    protected class LegendColorBarHeightChange implements ShapeGlyphRunnable {

        @Override
        public void modifyGlyph(Object source, AbstractEntity entity, Document document_) {
            if (entity instanceof edu.vt.beacon.graph.legend.Legend)
                ((edu.vt.beacon.graph.legend.Legend) entity).setColorBarHeight(Float.parseFloat(
                        ((EditingTextField) source).getText().trim()));

        }

    }

    protected class LegendInterEntryDistanceChange implements ShapeGlyphRunnable {

        @Override
        public void modifyGlyph(Object source, AbstractEntity entity, Document document_) {
            if (entity instanceof edu.vt.beacon.graph.legend.Legend)
                ((edu.vt.beacon.graph.legend.Legend) entity).setInterEntryDistance(Float.parseFloat(
                        ((EditingTextField) source).getText().trim()));

        }

    }

    protected class LegendColorBarTextDistanceChange implements ShapeGlyphRunnable {

        @Override
        public void modifyGlyph(Object source, AbstractEntity entity, Document document_) {
            if (entity instanceof edu.vt.beacon.graph.legend.Legend)
                ((edu.vt.beacon.graph.legend.Legend) entity).setColorBarTextDistance(Float.parseFloat(
                        ((EditingTextField) source).getText().trim()));

        }

    }

    private boolean isShapeInfo() {
        return tabbedPane_.getSelectedIndex() == 0;
    }
}


