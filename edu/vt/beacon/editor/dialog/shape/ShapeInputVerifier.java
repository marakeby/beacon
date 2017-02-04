package edu.vt.beacon.editor.dialog.shape;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.Border;

public class ShapeInputVerifier extends InputVerifier {

    // GUI component names (for validation purposes)
    private static final String NAME_LINE_WIDTH = "Line width";
    private static final String NAME_PADDING = "Padding";
    private static final String LEGEND_LABEL_COLOR_BAR_WIDTH = "Color Bar Width: ";
    private static final String LEGEND_LABEL_COLOR_BAR_HEIGHT = "Color Bar Height: ";
    private static final String LEGEND_LABEL_INTER_ENTRY_DISTANCE = "Distance Between Entries: ";
    private static final String LEGEND_LABEL_COLOR_BAR_TEXT_DISTNCE = "Distance Between Color Bars and Descriptions: ";
    private JLabel errorLabel_;
    private JDialog parent_;

    public JDialog getParent_() {
        return parent_;
    }

    public void setParent_(JDialog parent_) {
        this.parent_ = parent_;
    }

    public JLabel getErrorLabel_() {
        return errorLabel_;
    }

    public void setErrorLabel_(JLabel errorLabel_) {
        this.errorLabel_ = errorLabel_;
    }

    @Override
    public boolean verify(JComponent input) {
        String text = null;

        if (input instanceof JTextField) {
            text = ((JTextField) input).getText().trim();
        } else if (input instanceof JComboBox) {
            text = ((JComboBox) input).getSelectedItem().toString();
        }

        try {
            Float.parseFloat(text);
            errorLabel_.setText("");
            input.setBorder(BorderFactory.createMatteBorder(
                    1, 1, 1, 1, Color.BLACK));
            parent_.pack();

        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean shouldYieldFocus(JComponent input) {
        boolean valid = verify(input);
        if (!valid) {

            if (input.getName().compareTo(NAME_LINE_WIDTH) == 0)
                errorLabel_.setText("Line width must be a number.");

            else if (input.getName().compareTo(NAME_PADDING) == 0)
                errorLabel_.setText("Padding must be a number.");

            else if (input.getName().compareTo(LEGEND_LABEL_COLOR_BAR_HEIGHT) == 0)
                errorLabel_.setText("Height must be a number.");

            else if (input.getName().compareTo(LEGEND_LABEL_COLOR_BAR_WIDTH) == 0)
                errorLabel_.setText("Width must be a number.");

            else if (input.getName().compareTo(LEGEND_LABEL_COLOR_BAR_TEXT_DISTNCE) == 0)
                errorLabel_.setText("Distance must be a number.");

            else if (input.getName().compareTo(LEGEND_LABEL_INTER_ENTRY_DISTANCE) == 0)
                errorLabel_.setText("Distance must be a number.");


            errorLabel_.setForeground(Color.RED);
            input.setBorder(BorderFactory.createMatteBorder(
                    1, 1, 1, 1, Color.red));

        }

        return valid;
    }
}
