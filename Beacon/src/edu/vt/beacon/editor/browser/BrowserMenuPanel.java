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
import java.util.LinkedHashMap;

public class BrowserMenuPanel extends JPanel
        implements Skinnable, TreeSelectionListener {
    private static final long serialVersionUID = 1L;

    private Document document_;
    private LinkedHashMap<String, Document>  allDocuments_;

    private JTree mapTree_;

    private MapNode selectedNode_;

    private boolean isEditable = false;

    // FIXME complete constructor
    public BrowserMenuPanel(Document document) {
        setLayout(new BorderLayout());
        allDocuments_ = new LinkedHashMap<String, Document>();

        document_ = document;

        addDocument(document_);

        buildHeaderPanel();
        buildTreePanel();

        refresh();
        mapTree_.addTreeSelectionListener(this);
    }

    /*
     * document method
     */

    private void addDocument(Document doc){
//        System.out.println("adding document with ID "+ doc.getFile().getAbsolutePath());
        allDocuments_.put(doc.getFile().getAbsolutePath(), (Document) doc.clone() );
    }
    private Boolean hasDocument(Document doc){
        return allDocuments_.containsKey(doc.getFile().getAbsolutePath());
    }
    private void buildHeaderPanel() {
//        System.out.println("buildHeaderPanel ");
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
//        System.out.println("buildTreePanel ");
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

    private MapNode getDefaultMap(){
        DefaultMutableTreeNode rootNode= (DefaultMutableTreeNode) mapTree_.getModel().getRoot();
        DefaultMutableTreeNode firstProject= (DefaultMutableTreeNode) mapTree_.getModel().getChild(rootNode, 0);
        MapNode defaultNode = (MapNode) firstProject.getChildAt(0);
        return defaultNode ;
    }
    /*
     * document method
     */
    public Map getSelectedMap() {
//        System.out.println("getSelectedMap ");

        MapNode defaultNode = getDefaultMap();

        if (selectedNode_ == null)
            selectedNode_ = defaultNode;

        if (selectedNode_ == mapTree_.getModel().getRoot())
            document_.getPalette().enableTagButton(false);
        else
            document_.getPalette().enableTagButton(true);

        return selectedNode_.getMap();
    }

    private DefaultMutableTreeNode getDocumentTree(Document doc){

        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(doc.getFile().getName());
        MapNode mainMapNode = new MapNode(doc.getPathway().getMap());

        ArrayList<MapNode> tempNodes = new ArrayList<MapNode>();
        tempNodes.add(mainMapNode);

        ArrayList<MapNode> childNodes = new ArrayList<MapNode>();
        while (!tempNodes.isEmpty()) {

            setChildNodes(childNodes, tempNodes.get(0));

            for (MapNode childNode : childNodes) {

                tempNodes.add(childNode);
                tempNodes.get(0).add(childNode);
            }

            tempNodes.remove(0);
        }
        rootNode.add(mainMapNode);
        return rootNode;
    }
    // FIXME complete method
    public void refresh() {
//        System.out.println("refresh ");
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Projects");

        DefaultMutableTreeNode docTree =null ;
        for (Document doc : this.allDocuments_.values()) {
//        for (int i = 0; i < allDocuments_.size(); i++) {
//                doc = allDocuments_.get()
//                System.out.println(doc.getFile().getAbsolutePath());
                docTree = getDocumentTree(doc);
                rootNode.add(docTree);
            }


        mapTree_.setModel(new DefaultTreeModel(rootNode));

        //expand all level 1 nodes
        DefaultMutableTreeNode currentNode = rootNode.getNextNode();
        do {
            if (currentNode.getLevel()==1)
                mapTree_.expandPath(new TreePath(currentNode.getPath()));
            currentNode = currentNode.getNextNode();
        }
        while (currentNode != null);

        //select the last added document
        mapTree_.setRootVisible(false);
        mapTree_.setSelectionPath(new TreePath(docTree.getNextNode().getPath()));
        revalidate();
        repaint();
    }

//    public void refresh() {
//        System.out.println("refresh ");
//        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Projects");
//
//
//        ArrayList<MapNode> docNodes = new ArrayList<MapNode>();
//        for (Document doc : this.allDocuments_.values()) {
//            MapNode docNode = new MapNode(doc.getPathway().getMap());
//            rootNode.add(docNode);
//            docNodes.add(docNode);
//        }
////        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Projects");
//
//
//        ArrayList<MapNode> childNodes = new ArrayList<MapNode>();
//        while (!docNodes.isEmpty()) {
//
//            setChildNodes(childNodes, docNodes.get(0));
//
//            for (MapNode childNode : childNodes) {
//
//                docNodes.add(childNode);
//                docNodes.get(0).add(childNode);
//            }
//
//            docNodes.remove(0);
//        }
//
//        mapTree_.setModel(new DefaultTreeModel(rootNode));
////        mapTree_.setModel(new DefaultTreeModel(rootNode));
////        MapNode selected= (MapNode) mapTree_.getModel().getChild(mapTree_.getModel().getRoot(), 0);
////        mapTree_.setSelectionPath(new TreePath(selected.getPath()));
//        revalidate();
//        repaint();
//    }

    // FIXME complete method
    private void setChildNodes(ArrayList<MapNode> childNodes,
                               MapNode rootNode) {
//        System.out.println("setChildNodes ");
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
//        System.out.println("addSubmap" + submap +" " + parentMap);
        if (parentMap == null)
            return;

        MapNode parentNode = findMapNode(parentMap);
        MapNode childNode= new MapNode(submap.getMap());
        parentNode.add(childNode);
        mapTree_.expandPath(new TreePath(parentNode.getPath()));
        mapTree_.scrollPathToVisible(new TreePath(childNode.getPath()));
        mapTree_.updateUI();
    }

    private MapNode findMapNode(Map map)
    {
        MapNode submapNode = null;
        for (Enumeration e = ((DefaultMutableTreeNode) mapTree_.getModel().getRoot()).depthFirstEnumeration(); e.hasMoreElements(); ) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();

            if (node.getUserObject()  instanceof Map && node.getUserObject() == map) {
                submapNode = (MapNode) node;
                break;
            }
        }
        return submapNode;
    }
    public void removeSubmap(Submap submap) {
//        System.out.println("removeSubmap");
        if (submap == null)
            return;

        MapNode submapNode = findMapNode(submap.getMap());

        if (submapNode != null) {
            submapNode.removeFromParent();
            mapTree_.updateUI();
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {

//        System.out.println("valueChanged ");

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) mapTree_.getLastSelectedPathComponent();

        //nothing is selected, select the first map node
        if (node == null) {
            selectedNode_ = getDefaultMap();
//            System.out.println("selected node " + selectedNode_);
        }
        //this is a map node
        else if (node.getUserObject() instanceof Map)
            selectedNode_ = (MapNode) node;
        else
        {

            DefaultMutableTreeNode currentNode = node;
            while (! (currentNode.getUserObject() instanceof Map))
            {
                currentNode = currentNode.getNextNode();
            }
//            mapTree_.setSelectionPath(new TreePath(currentNode.getPath()));
            selectedNode_ = (MapNode) currentNode;


        }

            document_.getCanvas().repaint();
            document_.getLayersMenu().refresh();


    }

    public void setDocument(Document document) {
//        System.out.println("setDocument " + document);
//        document_ = document;
        if (!hasDocument(document))
        {
//            System.out.println("adding new doc " +document.getFile().toString());
            addDocument(document);
            refresh();
        }

    }

    public Submap getParentOfSelectedMap() {
//        System.out.println("getParentOfSelectedMap ");
        if (selectedNode_ == null || selectedNode_.getParent() == null)
            return null;

        Map parentMap = ((MapNode) selectedNode_.getParent()).getMap();

        for (Submap submap : parentMap.getSubmaps())
            if (submap.getMap() == selectedNode_.getMap())
                return submap;

        return null;
    }
}