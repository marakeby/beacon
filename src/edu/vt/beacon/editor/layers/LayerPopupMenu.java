package edu.vt.beacon.editor.layers;

import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.layer.Layer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

/**
 * Created by ppws on 1/14/16.
 */
public class LayerPopupMenu extends JPopupMenu {

    private Document document;
    private LayersMenuPanel layersMenuPanel;
    private int[] selectedRows;

    public LayerPopupMenu(final Document document, final int[] selectedRows, final LayersMenuPanel layersMenuPanel) {

        this.document = document;
        this.layersMenuPanel = layersMenuPanel;
        this.selectedRows = selectedRows;

        populateCreatingMenuItem();
        populateDeletingMenuItem();
        populateShowingHidingMenuItem(true, false);
        populateShowingHidingMenuItem(false, false);
        populateShowingHidingMenuItem(true, true);
        populateShowingHidingMenuItem(false, true);

    }

    private void populateCreatingMenuItem() {

        JMenuItem createMenuItem = new JMenuItem("Create");
        add(createMenuItem);

        createMenuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (document.getBrowserMenu().getSelectedMap().getLayers() == null)
                    document.getBrowserMenu().getSelectedMap().setLayers(new ArrayList<Layer>());

                ArrayList<Layer> layers = document.getBrowserMenu().getSelectedMap().getLayers();
                layers.add(new Layer("New Layer " + (layers.size() + 1), document.getBrowserMenu().getSelectedMap()));
                layersMenuPanel.refresh();
            }
        });

    }

    private void populateDeletingMenuItem() {

        JMenuItem deleteMenuItem = new JMenuItem("Delete");
        deleteMenuItem.setEnabled(selectedRows != null && selectedRows.length > 0);

        add(deleteMenuItem);


        deleteMenuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (selectedRows == null || selectedRows.length == 0)
                    return;

                for (int i = selectedRows.length - 1; i >= 0; i--)
                    document.getBrowserMenu().getSelectedMap().remove(selectedRows[i]);

                layersMenuPanel.getTable().clearSelection();
            }
        });

    }

    private void populateShowingHidingMenuItem(final boolean show, final boolean all) {

        JMenuItem menuItem = new JMenuItem();

        if (show) {
            if (all)
                menuItem.setText("Show All");
            else
                menuItem.setText("Show");
        } else {
            if (all)
                menuItem.setText("Hide All");
            else
                menuItem.setText("Hide");
        }

        if (!all)
            menuItem.setEnabled(selectedRows != null && selectedRows.length > 0);

        add(menuItem);


        menuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (!all && (selectedRows == null || selectedRows.length == 0))
                    return;

                if (all) {

                    for (int i = document.getBrowserMenu().getSelectedMap().getLayerCount() - 1; i >= 0; i--)
                        document.getBrowserMenu().getSelectedMap().getLayerAt(i).setActive(show);

                } else {

                    for (int i = selectedRows.length - 1; i >= 0; i--)
                        document.getBrowserMenu().getSelectedMap().getLayerAt(selectedRows[i]).setActive(show);
                }

                document.getCanvas().repaint();
            }
        });

    }

}
