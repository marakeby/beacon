package edu.vt.beacon.editor.dialog.preferences;

import edu.vt.beacon.editor.dialog.font.FontCellRenderer;
import edu.vt.beacon.editor.document.Document;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Date;
import java.util.Properties;

/**
 * Created by ppws on 5/18/16.
 */
public class PreferencesDialog extends Dialog {

    private Button saveButton;
    private Button discardButton;

    private Document document;

    private JPanel glyphForegroundColorComponent;
    private JPanel glyphBackgroundColorComponent;
    private TextField glyphLineWidthTextField;
    private TextField glyphPaddingTextField;
    private JPanel glyphSelectionColorComponent;

    private JComboBox fontNameComboBox;
    private JComboBox fontSizeComboBox;
    private JComboBox fontStyleComboBox;
    private JPanel fontColorComponent;

    private JPanel canvasBackgroundColorComponent;
    private JPanel canvasMajorColorComponent;
    private JPanel canvasMinorColorComponent;

    private String[] fontSizes = {"5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "18", "20", "22", "24",
            "26", "28", "30", "32", "34", "36", "38", "40", "42", "44", "46", "48", "50", "52", "54", "56", "58", "60", "65", "70", "75", "80"};
    private String[] fontTypes = {"Plain", "Bold", "Italic", "Bold-italic"};
    private String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames(null);

    public PreferencesDialog(JPanel parent, Document document) {
        super((JFrame) null);

        buildComponents();

        setPreferredSize(new Dimension(485, 450));
        setSize(new Dimension(485, 450));
//        setMaximumSize(new Dimension(475, 450));
//        setMinimumSize(new Dimension(475, 450));
        setLocationRelativeTo(parent);
        setResizable(false);

        this.document = document;
        initComponents();

        pack();

        setVisible(true);
    }

    private void initComponents() {

        glyphForegroundColorComponent.setBackground(document.getColor("glyph.foreground"));
        glyphBackgroundColorComponent.setBackground(document.getColor("glyph.background"));
        glyphLineWidthTextField.setText(document.get("glyph.lineWidth"));
        glyphPaddingTextField.setText(document.get("glyph.padding"));
        glyphSelectionColorComponent.setBackground(document.getColor("glyph.selection"));

        fontNameComboBox.setSelectedItem(document.get("font.name"));
        fontColorComponent.setBackground(document.getColor("font.color"));

        fontStyleComboBox.setSelectedItem(fontTypes[Integer.parseInt(document.get("font.style"))]);
        for (int i = 0; i < fontSizes.length; i++)
            if (Math.abs(Float.parseFloat(fontSizes[i]) - Float.parseFloat(document.get("font.size"))) < 0.1) {
                fontSizeComboBox.setSelectedItem(fontSizes[i]);
                break;
            }

        canvasBackgroundColorComponent.setBackground(document.getColor("canvas.color"));
        canvasMajorColorComponent.setBackground(document.getColor("color.grid.major"));
        canvasMinorColorComponent.setBackground(document.getColor("color.grid.minor"));

    }

    private void buildComponents() {

        setLayout(new GridBagLayout());
        int y = 0;
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = y++;
        c.insets = new Insets(30, 0, 20, 0);
        add(createGlyphSection(), c);

        c.gridx = 0;
        c.gridy = y++;
        c.insets = new Insets(0, 0, 20, 0);
        add(createFontSection(), c);

        c.gridx = 0;
        c.gridy = y++;
        c.insets = new Insets(0, 0, 0, 0);
        add(createCanvasSection(), c);

        c.gridx = 0;
        c.gridy = y++;
        c.insets = new Insets(20, 20, 20, 20);
        add(createButtonPanel(), c);

    }

    private JPanel createGlyphSection() {

        JPanel glyphSection = new JPanel();
        glyphSection.setBorder(new TitledBorder("Glyph"));
        glyphSection.setPreferredSize(new Dimension(465, 120));
        glyphSection.setMinimumSize(new Dimension(465, 120));
        glyphSection.setLayout(new GridBagLayout());


        int x = 0;
        int y = 0;
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = x++;
        c.gridy = y;
        c.insets = new Insets(0, 0, 10, 22);
        c.anchor = GridBagConstraints.LINE_START;
        JLabel foregroundColorLabel = new JLabel("Foreground Color:");
        glyphSection.add(foregroundColorLabel, c);

        c.gridx = x++;
        c.gridy = y;
        c.insets = new Insets(0, 10, 10, 0);
        glyphForegroundColorComponent = getColorPanel(Color.BLACK);
        glyphSection.add(glyphForegroundColorComponent, c);

        c.gridx = x++;
        c.gridy = y;
        c.insets = new Insets(0, 50, 10, 22);
        JLabel backgroundColorLabel = new JLabel("Background Color:");
        glyphSection.add(backgroundColorLabel, c);

        c.gridx = x++;
        c.gridy = y++;
        c.insets = new Insets(0, 10, 10, 0);
        glyphBackgroundColorComponent = getColorPanel(Color.BLACK);
        glyphSection.add(glyphBackgroundColorComponent, c);

        x = 0;

        c.gridx = x++;
        c.gridy = y;
        c.insets = new Insets(0, 0, 10, 22);
        JLabel lineWidthLabel = new JLabel("Line Width:");
        glyphSection.add(lineWidthLabel, c);

        c.gridx = x++;
        c.gridy = y;
        c.insets = new Insets(0, 10, 10, 0);
        glyphLineWidthTextField = new TextField();
        glyphLineWidthTextField.setPreferredSize(new Dimension(40, 20));
        glyphSection.add(glyphLineWidthTextField, c);

        c.gridx = x++;
        c.gridy = y;
        c.insets = new Insets(0, 50, 10, 22);
        JLabel paddingLabel = new JLabel("Padding:");
        glyphSection.add(paddingLabel, c);

        c.gridx = x++;
        c.gridy = y++;
        c.insets = new Insets(0, 10, 10, 0);
        glyphPaddingTextField = new TextField();
        glyphPaddingTextField.setPreferredSize(new Dimension(40, 20));
        glyphSection.add(glyphPaddingTextField, c);

        x = 0;

        c.gridx = x++;
        c.gridy = y;
        c.insets = new Insets(0, 0, 10, 22);
        JLabel selectionColorLabel = new JLabel("Selection Color:");
        glyphSection.add(selectionColorLabel, c);

        c.gridx = x++;
        c.gridy = y++;
        c.insets = new Insets(0, 10, 10, 0);
        glyphSelectionColorComponent = getColorPanel(Color.BLACK);
        glyphSection.add(glyphSelectionColorComponent, c);

        return glyphSection;

    }

    private JPanel createFontSection() {

        JPanel fontSection = new JPanel();
        fontSection.setBorder(new TitledBorder("Font"));
        fontSection.setPreferredSize(new Dimension(465, 100));
        fontSection.setMinimumSize(new Dimension(465, 100));
        fontSection.setLayout(new GridBagLayout());

        int x = 0;
        int y = 0;
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = x++;
        c.gridy = y;
        c.insets = new Insets(0, 0, 10, 0);
        c.anchor = GridBagConstraints.LINE_START;
        JLabel nameLabel = new JLabel("Name:");
        fontSection.add(nameLabel, c);

        c.gridx = x++;
        c.gridy = y;
        c.insets = new Insets(0, 10, 10, 0);
        fontNameComboBox = new JComboBox(fontNames);
        fontNameComboBox.setRenderer(new FontCellRenderer());
        fontSection.add(fontNameComboBox, c);

        c.gridx = x++;
        c.gridy = y;
        c.insets = new Insets(0, 10, 10, 0);
        JLabel sizeLabel = new JLabel("Size:");
        fontSection.add(sizeLabel, c);

        c.gridx = x++;
        c.gridy = y++;
        c.insets = new Insets(0, 10, 10, 0);
        fontSizeComboBox = new JComboBox(fontSizes);
        fontNameComboBox.setRenderer(new FontCellRenderer());
        fontSection.add(fontSizeComboBox, c);

        x = 0;

        c.gridx = x++;
        c.gridy = y;
        c.insets = new Insets(0, 0, 10, 0);
        JLabel styleLabel = new JLabel("Style:");
        fontSection.add(styleLabel, c);

        c.gridx = x++;
        c.gridy = y;
        c.insets = new Insets(0, 10, 10, 0);
        fontStyleComboBox = new JComboBox(fontTypes);
        fontStyleComboBox.setRenderer(new FontCellRenderer());
        fontSection.add(fontStyleComboBox, c);

        c.gridx = x++;
        c.gridy = y;
        c.insets = new Insets(0, 10, 10, 0);
        JLabel colorLabel = new JLabel("Color:");
        fontSection.add(colorLabel, c);

        c.gridx = x++;
        c.gridy = y++;
        c.insets = new Insets(0, 10, 10, 0);
        fontColorComponent = getColorPanel(Color.BLACK);
        fontSection.add(fontColorComponent, c);

        return fontSection;

    }

    private JPanel createCanvasSection() {

        JPanel canvasSection = new JPanel();
        canvasSection.setBorder(new TitledBorder("Canvas"));
        canvasSection.setPreferredSize(new Dimension(465, 90));
        canvasSection.setMinimumSize(new Dimension(465, 90));
        canvasSection.setLayout(new GridBagLayout());

        int x = 0;
        int y = 0;
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = x++;
        c.gridy = y;
        c.insets = new Insets(0, 0, 10, 0);
        c.anchor = GridBagConstraints.LINE_START;
        JLabel backgroundColorLabel = new JLabel("Background Color:");
        canvasSection.add(backgroundColorLabel, c);

        c.gridx = x++;
        c.gridy = y;
        c.insets = new Insets(0, 10, 10, 0);
        canvasBackgroundColorComponent = getColorPanel(Color.BLACK);
        canvasSection.add(canvasBackgroundColorComponent, c);

        c.gridx = x++;
        c.gridy = y;
        c.insets = new Insets(0, 50, 10, 0);
        JLabel majorColorLabel = new JLabel("Grid Major Line Color:");
        canvasSection.add(majorColorLabel, c);

        c.gridx = x++;
        c.gridy = y++;
        c.insets = new Insets(0, 10, 10, 0);
        canvasMajorColorComponent = getColorPanel(Color.BLACK);
        canvasSection.add(canvasMajorColorComponent, c);

        x = 0;

        c.gridx = x++;
        c.gridy = y;
        c.insets = new Insets(0, 0, 10, 0);
        JLabel minorColorLabel = new JLabel("Grid Minor Line Color:");
        canvasSection.add(minorColorLabel, c);

        c.gridx = x++;
        c.gridy = y++;
        c.insets = new Insets(0, 10, 10, 0);
        canvasMinorColorComponent = getColorPanel(Color.BLACK);
        canvasSection.add(canvasMinorColorComponent, c);

        return canvasSection;

    }

    private JPanel createButtonPanel() {

        saveButton = new Button("Save");
        discardButton = new Button("Discard");

        setupActions();

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(discardButton);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(saveButton);

        return buttonPane;
    }

    private void setupActions() {

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        discardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savePreferences();
                dispose();
            }
        });
    }

    private JPanel getColorPanel(final Color color) {

        final JPanel panel = new JPanel();
        final Dimension colorPanelSize = new Dimension(40, 20);

        if (color != null) {
            panel.setBackground(color);
        } else {
            panel.setBackground(Color.WHITE);
        }
        panel.setBorder(BorderFactory.createLoweredBevelBorder());
        panel.setPreferredSize(colorPanelSize);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                Color newColor = JColorChooser.showDialog(panel, "Select a color", panel.getBackground());

                if (newColor != null && !newColor.equals(panel.getBackground()))
                    panel.setBackground(newColor);

            }
        });

        return panel;

    }

    private void savePreferences() {

        Properties props = new Properties();
        String propFileName = System.getProperty("user.home") + "/.beacon.config.properties";

        try {

            InputStream inputStream = new FileInputStream(propFileName);
            props.load(inputStream);
            inputStream.close();

        } catch (Exception e) {
        }

        try {

            props.setProperty("glyph.foreground", glyphForegroundColorComponent.getBackground().getRGB() + "");
            props.setProperty("glyph.background", glyphBackgroundColorComponent.getBackground().getRGB() + "");
            props.setProperty("glyph.selection", glyphSelectionColorComponent.getBackground().getRGB() + "");

            try {

                Float.parseFloat(glyphLineWidthTextField.getText());
                props.setProperty("glyph.lineWidth", glyphLineWidthTextField.getText());

            } catch (Exception e) {
            }

            try {

                Float.parseFloat(glyphPaddingTextField.getText());
                props.setProperty("glyph.padding", glyphPaddingTextField.getText());

            } catch (Exception e) {
            }


            props.setProperty("font.name", fontNameComboBox.getSelectedItem() + "");
            props.setProperty("font.color", fontColorComponent.getBackground().getRGB() + "");

            for (int i = 0; i < 4; i++)
                if (fontStyleComboBox.getSelectedItem().toString().equalsIgnoreCase(fontTypes[i]))
                    props.setProperty("font.style", i + "");

            try {

                Float.parseFloat(fontSizeComboBox.getSelectedItem() + "");
                props.setProperty("font.size", fontSizeComboBox.getSelectedItem() + "");

            } catch (Exception e) {
            }

            props.setProperty("canvas.color", canvasBackgroundColorComponent.getBackground().getRGB() + "");
            props.setProperty("color.grid.major", canvasMajorColorComponent.getBackground().getRGB() + "");
            props.setProperty("color.grid.minor", canvasMinorColorComponent.getBackground().getRGB() + "");


            File f = new File(propFileName);
            OutputStream out = new FileOutputStream(f);
            props.store(out, "Updated in " + new Date());
            out.close();

            Thread.sleep(1000);
            document.readPropertiesFromConfigFile();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
