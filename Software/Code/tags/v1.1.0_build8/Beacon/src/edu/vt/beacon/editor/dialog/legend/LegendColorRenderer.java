package edu.vt.beacon.editor.dialog.legend;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Created by ppws on 3/25/16.
 */
public class LegendColorRenderer extends JLabel implements TableCellRenderer {

    Border unselectedBorder = null;
    Border selectedBorder = null;
    boolean isBordered = true;

    public LegendColorRenderer(boolean isBordered) {
        this.isBordered = isBordered;
        setOpaque(true); //MUST do this for background to show up.
    }

    public Component getTableCellRendererComponent(
            JTable table, Object color,
            boolean isSelected, boolean hasFocus,
            int row, int column) {
        Color newColor = (Color) color;
        setBackground(newColor);
        if (isBordered) {
            if (isSelected) {
                if (selectedBorder == null) {
                    selectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5,
                            table.getSelectionBackground());
                }
                setBorder(selectedBorder);
            } else {
                if (unselectedBorder == null) {
                    unselectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5,
                            table.getBackground());
                }
                setBorder(unselectedBorder);
            }
        }

        return this;
    }
}
