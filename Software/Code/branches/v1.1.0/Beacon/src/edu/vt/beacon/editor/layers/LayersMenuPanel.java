package edu.vt.beacon.editor.layers;

import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.swing.laf.Skinnable;
import edu.vt.beacon.editor.swing.platform.PlatformScrollPane;
import edu.vt.beacon.layer.Layer;
import edu.vt.beacon.map.Map;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LayersMenuPanel extends JPanel implements Skinnable {
    private static final long serialVersionUID = 1L;

    private Document document_;

    private JTable layersTable_;

    private Map selectedMap_;

    // FIXME complete constructor
    public LayersMenuPanel(Document document) {
//        System.out.println("LayersMenuPanel" +document);
        setLayout(new BorderLayout());

        document_ = document;
        selectedMap_ = document_.getPathway().getMap();

        buildHeaderPanel();
        buildMenuPanel();

        refresh();
    }

    // TODO document method
    private void buildHeaderPanel() {
//        System.out.println("buildHeaderPanel" );
        JLabel headerLabel = new JLabel("Map Layers");
        headerLabel.setFont(FONT_MEDIUM.deriveFont(Font.BOLD));
        headerLabel.setForeground(COLOR_FOREGROUND);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(COLOR_BACKGROUND);
        headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        headerPanel.add(headerLabel);

        add(headerPanel, BorderLayout.NORTH);
    }

    // TODO document method
    private void buildLayersPanel(JPanel menuPanel) {
//        System.out.println("buildLayersPanel" );
        layersTable_ = new JTable(new LayerTableModel(selectedMap_.getLayers()));
        layersTable_.setFillsViewportHeight(true);
        layersTable_.setTableHeader(null);
        layersTable_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        PlatformScrollPane scrollPane = new PlatformScrollPane(layersTable_);

        layersTable_.setDefaultEditor(Integer.class,
                new DefaultCellEditor(new JTextField()));

        layersTable_.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                if (e.getModifiers() == InputEvent.BUTTON1_MASK) {
                    selectedMap_.setSelection(layersTable_.getSelectedRow());
                    return;
                }

                if (e.getModifiers() != InputEvent.BUTTON3_MASK)
                    return;

                if (e.getComponent() instanceof JTable) {
                    JPopupMenu popup = new LayerPopupMenu(document_, layersTable_.getSelectedRows(), LayersMenuPanel.this);
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        menuPanel.add(scrollPane);
    }

    // TODO document method
    private void buildMenuPanel() {
//        System.out.println("buildMenuPanel" );
        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(Color.white);
        menuPanel.setBorder(BorderFactory.createEtchedBorder());
        menuPanel.setLayout(new BorderLayout());

        buildLayersPanel(menuPanel);

        add(menuPanel);
    }

    // TODO document method
    public Layer getSelectedLayer() {
//        System.out.println("getSelectedLayer" );
        if (selectedMap_ != null)
        for (int i = selectedMap_.getLayerCount() - 1; i >= 0; i--)
            if (selectedMap_.getLayerAt(i).isSelected())

                return selectedMap_.getLayerAt(i);

        return null;
    }

    public int getSelectedLayerIndex() {
//        System.out.println("getSelectedLayerIndex" );
        if (selectedMap_ != null)
            for (int i = selectedMap_.getLayerCount() - 1; i >= 0; i--)
                if (selectedMap_.getLayerAt(i).isSelected())

                    return i;

        return 0;
    }



    public void refresh() {
//        System.out.println("refresh" );
        selectedMap_ = document_.getBrowserMenu().getSelectedMap();
        layersTable_.setModel(new LayerTableModel(selectedMap_.getLayers()));
        int selected_index = getSelectedLayerIndex();
//        System.out.println("selected layer # " +selected_index);
        layersTable_.setRowSelectionInterval(selected_index,selected_index);
        repaint();
    }

    public JTable getTable() {
//        System.out.println("getTable" );
        return layersTable_;
    }

    public void setDocument(Document document) {
//        System.out.println("setDocument" );
        document_ = document;
        refresh();
    }

}