package edu.vt.beacon.editor.browser;

import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.swing.laf.Skinnable;
import edu.vt.beacon.editor.swing.platform.PlatformScrollPane;
import edu.vt.beacon.graph.glyph.AbstractGlyph;
import edu.vt.beacon.graph.glyph.node.submap.Submap;
import edu.vt.beacon.layer.Layer;
import edu.vt.beacon.map.Map;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.EventObject;

public class BrowserMenuPanel extends JPanel
        implements Skinnable, TreeSelectionListener {
    private static final long serialVersionUID = 1L;

    private Document document_;

    private JTree mapTree_;

    private MapNode selectedNode_;

    private boolean isEditable = false;

    // FIXME complete constructor
    public BrowserMenuPanel(Document document) {
        setLayout(new BorderLayout());

        document_ = document;

        buildHeaderPanel();
        buildTreePanel();

        refresh();
        mapTree_.addTreeSelectionListener(this);
    }

    /*
     * document method
     */
    private void buildHeaderPanel() {
        JLabel headerLabel = new JLabel("Map Browser");
        headerLabel.setFont(FONT_MEDIUM.deriveFont(Font.BOLD));
        headerLabel.setForeground(COLOR_FOREGROUND);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(COLOR_BACKGROUND);
        headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        headerPanel.add(headerLabel);

        add(headerPanel, BorderLayout.NORTH);
    }

    /*
     * FIXME complete method
     */
    private void buildTreePanel() {
        mapTree_ = new JTree();
        mapTree_.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);
        mapTree_.setFont(FONT_MEDIUM);
        mapTree_.setOpaque(false);
        mapTree_.setEditable(true);

        mapTree_.setCellEditor(new DefaultTreeCellEditor(mapTree_, (DefaultTreeCellRenderer) mapTree_.getCellRenderer()) {
            @Override
            public boolean isCellEditable(EventObject e) {
                return isEditable;
            }
        });

        mapTree_.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int selRow = mapTree_.getRowForLocation(e.getX(), e.getY());
                if (selRow != -1) {
                    if (e.getClickCount() == 1) {
                        isEditable = false;
                    } else if (e.getClickCount() == 2) {
                        isEditable = true;
                    }
                }
            }
        });

        PlatformScrollPane treeScroll = new PlatformScrollPane(mapTree_);
        treeScroll.getViewport().setOpaque(false);
        treeScroll.setBorder(BorderFactory.createEmptyBorder());
        treeScroll.setOpaque(false);
        treeScroll.setViewportBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel treePanel = new JPanel();
        treePanel.setBackground(Color.white);
        treePanel.setBorder(BorderFactory.createEtchedBorder());
        treePanel.setLayout(new BorderLayout());

        treePanel.add(treeScroll);

        add(treePanel);
    }

    /*
     * document method
     */
    public Map getSelectedMap() {
        if (selectedNode_ == null)
            selectedNode_ = (MapNode) mapTree_.getModel().getRoot();

        if (selectedNode_ == mapTree_.getModel().getRoot())
            document_.getPalette().enableTagButton(false);
        else
            document_.getPalette().enableTagButton(true);

        return selectedNode_.getMap();
    }

    // FIXME complete method
    public void refresh() {

//        MapNode selecetd = (MapNode) mapTree_.getLastSelectedPathComponent();
        int[] selected_rows = mapTree_.getSelectionRows();
        int selected = 0;
        if (selected_rows !=null && selected_rows.length >0)
            selected = selected_rows[0];

        MapNode rootNode = new MapNode(document_.getPathway().getMap());

        ArrayList<MapNode> rootNodes = new ArrayList<MapNode>();
        rootNodes.add(rootNode);

        ArrayList<MapNode> childNodes = new ArrayList<MapNode>();
        while (!rootNodes.isEmpty()) {

            setChildNodes(childNodes, rootNodes.get(0));

            for (MapNode childNode : childNodes) {

                rootNodes.add(childNode);
                rootNodes.get(0).add(childNode);
            }

            rootNodes.remove(0);
        }

        mapTree_.setModel(new DefaultTreeModel(rootNode));
        if (selected >0)
            mapTree_.setSelectionInterval(selected, selected);
        else
             mapTree_.setSelectionPath(new TreePath(rootNode.getPath()));

        revalidate();
        repaint();
    }

    // FIXME complete method
    private void setChildNodes(ArrayList<MapNode> childNodes,
                               MapNode rootNode) {
        childNodes.clear();

        Layer layer;
        AbstractGlyph glyph;
        Map map = rootNode.getMap();

        for (int i = 0; i < map.getLayerCount(); i++) {

            layer = map.getLayerAt(i);

            if (layer.isActive()) {

                for (int j = 0; j < layer.getGlyphCount(); j++) {

                    glyph = layer.getGlyphAt(j);

                    if (glyph instanceof Submap)
                        childNodes.add(new MapNode(
                                ((Submap) glyph).getMap()));
                }
            }
        }
    }

    // FIXME complete method
    public void addSubmap(Submap submap, Map parentMap) {
        if (parentMap == null)
            return;

        MapNode parentNode = null;
        for (Enumeration e = ((MapNode) mapTree_.getModel().getRoot()).depthFirstEnumeration(); e.hasMoreElements(); ) {
            MapNode node = (MapNode) e.nextElement();

            if (node.getUserObject() == parentMap) {
                parentNode = node;
                break;
            }
        }

        parentNode.add(new MapNode(submap.getMap()));
        mapTree_.expandPath(new TreePath(parentNode.getPath()));
        mapTree_.updateUI();
    }

    public void removeSubmap(Submap submap) {
        if (submap == null)
            return;

        MapNode submapNode = null;
        for (Enumeration e = ((MapNode) mapTree_.getModel().getRoot()).depthFirstEnumeration(); e.hasMoreElements(); ) {
            MapNode node = (MapNode) e.nextElement();

            if (node.getUserObject() == submap.getMap()) {
                submapNode = node;
                break;
            }
        }

        if (submapNode != null) {
            submapNode.removeFromParent();
            mapTree_.updateUI();
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {

        selectedNode_ = (MapNode) mapTree_.getLastSelectedPathComponent();

        if (selectedNode_ == null)
            selectedNode_ = (MapNode) mapTree_.getModel().getRoot();

        document_.getCanvas().repaint();
        document_.getLayersMenu().refresh();
    }

    public void setDocument(Document document) {
        document_ = document;
        refresh();
    }

    public Submap getParentOfSelectedMap() {
        if (selectedNode_ == null || selectedNode_.getParent() == null)
            return null;

        Map parentMap = ((MapNode) selectedNode_.getParent()).getMap();

        for (Submap submap : parentMap.getSubmaps())
            if (submap.getMap() == selectedNode_.getMap())
                return submap;

        return null;
    }
}