package edu.vt.beacon.editor.dialog.legend;

import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.document.DocumentState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class LegendDialog extends JDialog {

    public LegendDialog(final Document document) {

        setTitle("Legend Dialog");
        setResizable(false);
        setLocation(document.getFrame().getX() + document.getFrame().getWidth() / 4,
                document.getFrame().getY() + document.getFrame().getHeight() / 4);
        Container contentPane = getContentPane();

        document.getLegendManager().populateMapping();
        JTable table = new JTable(new LegendTableModel(document.getLegendManager()));
        table.setPreferredScrollableViewportSize(new Dimension(500, 150));
        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        //Create the scroll pane and add the table to it.
        JScrollPane tablePane = new JScrollPane(table);

        //Set up renderer and editor for the Favorite Color column.
        table.setDefaultRenderer(Color.class,
                new LegendColorRenderer(true));

        table.getColumnModel().getColumn(0).setMinWidth(50);
        table.getColumnModel().getColumn(0).setMaxWidth(50);

        table.setRowHeight(20);

        contentPane.add(tablePane, BorderLayout.PAGE_START);

        final JButton createButton = new JButton("Create");
        getRootPane().setDefaultButton(createButton);
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                document.getLegendManager().createLegend();
                new DocumentState(document, "Create legend", false);
                dispose();
            }
        });


        final JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                document.getLegendManager().removeLegend();
                new DocumentState(document, "Remove legend", false);
                dispose();
            }
        });


        JButton cancelButton = new JButton("Cancel");
        getRootPane().setDefaultButton(cancelButton);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(removeButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(createButton);

        contentPane.add(buttonPanel, BorderLayout.PAGE_END);

        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        rootPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                setVisible(false);
            }
        }, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

        pack();
        setVisible(true);
    }
}
