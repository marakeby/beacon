package edu.vt.beacon.editor.canvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by mostafa on 2/10/16.
 */
public class AnnotationAuxiliaryUnitDialog {

    private JDialog dialog;
    private String textValue;
    private String selectionValue;

    private JFrame parent;
    private String title;
    private String textFieldLabel;
    private String comboBoxLabel;
    private String[] comboBoxValues;

    private final int width = 400;
    private final int height = 300;

    public AnnotationAuxiliaryUnitDialog(JFrame parent, String title, String textFieldLabel, String comboBoxLabel,
                                         String[] comboBoxValues) {

        textValue = null;
        selectionValue = null;

        this.parent = parent;
        this.title = title;
        this.textFieldLabel = textFieldLabel;
        this.comboBoxLabel = comboBoxLabel;
        this.comboBoxValues = comboBoxValues;
    }

    public void display() {

        dialog = new JDialog(parent, title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(width, height);
        dialog.setResizable(false);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setContentPane(createContent());

        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    private JPanel createContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));

        JComboBox<String> combo = null;

        if (comboBoxValues != null && comboBoxValues.length > 0) {

            JLabel selectionLabel = new JLabel(comboBoxLabel + ":");
            combo = new JComboBox<String>();
            for (String item : comboBoxValues)
                combo.addItem(item);

            if (selectionValue != null) {
                combo.setSelectedItem(selectionValue);
            }

            panel.add(selectionLabel);
            panel.add(combo);
            panel.add(Box.createRigidArea(new Dimension(20, 20)));
        }

        JLabel textAreaLabel = new JLabel(textFieldLabel + ":");
        final JTextArea textArea = new JTextArea();
        textArea.setEditable(true);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        if (textValue != null) {
            textArea.setText(textValue);
        }

        JScrollPane jsp = new JScrollPane(textArea);
        jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        jsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jsp.setPreferredSize(new Dimension(180, 120));

        panel.add(textAreaLabel);
        panel.add(jsp);
        panel.add(Box.createRigidArea(new Dimension(150,0)));

        JButton btnok = new JButton("OK");
        panel.add(btnok);

        JButton btncancel = new JButton("Cancel");
        panel.add(btncancel);

        final JComboBox<String> finalCombo = combo;
        btnok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent a) {
                textValue = textArea.getText();

                if (finalCombo != null) {
                    selectionValue = (String) finalCombo.getSelectedItem();
                }

                dialog.dispose();
            }
        });

        btncancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent a) {
                dialog.dispose();
            }
        });

        return panel;
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    public String getSelectionValue() {
        return selectionValue;
    }

    public void setSelectionValue(String selectionValue) {
        this.selectionValue = selectionValue;
    }

}
