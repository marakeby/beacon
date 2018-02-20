package edu.vt.beacon.editor.frame;

import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.gene.Gene;
import edu.vt.beacon.editor.swing.ClearSplitPane;
import edu.vt.beacon.graph.glyph.AbstractGlyph;
import edu.vt.beacon.graph.glyph.node.AbstractNode;
import edu.vt.beacon.graph.glyph.node.activity.AbstractActivity;
import edu.vt.beacon.layer.Layer;
import edu.vt.beacon.map.Map;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class Frame extends JFrame {
    private static final long serialVersionUID = 1L;

    private ClearSplitPane hSplitPane_;

    private ClearSplitPane vSplitPane_;
    private ClearSplitPane vSplitPane2_;

    private Document document_;

    /*
     * document constructor
     */
    public Frame(Document document) {
        this(document, document.getFile().getName());
    }

    public void setDocument(Document doc)
    {
        document_ = doc;
        this.setTitle(doc.getFile().getAbsolutePath());
    }
    // FIXME complete constructor
    public Frame(Document document, String title)  {

        super(title);

        setJMenuBar(document.getMenuBar());
        setLocation(document.getInteger("frame.x"),
                document.getInteger("frame.y"));
        setSize(document.getInteger("frame.width"),
                document.getInteger("frame.height"));
        setDocument( document);
        buildContentPane();

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setVisible(true);

        hSplitPane_.setDividerLocation(document.getDouble("frame.hSplit"));
        vSplitPane_.setDividerLocation(document.getDouble("frame.vSplit"));
//        vSplitPane2_.setDividerLocation(document.getDouble("frame.vSplit"));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Frame frame= (Frame)e.getSource();
                if (frame !=null){
                    frame.quit();
                }

            }
        });
    }

    public void quit(){
        if (!this.document_.getViewer().isAllProjectsSaved()) {
            int result = JOptionPane.showConfirmDialog(
                    this.document_.getCanvas(), "Some projects have not been saved. Are you sure you want to close the Beacon Editor?",
                    "Confirm Close",
                    0, 2);
            if (result != 0)
                return;
        }
        System.exit(0);

    }
    // FIXME complete method
    private void buildContentPane() {

        vSplitPane_ = new ClearSplitPane(JSplitPane.VERTICAL_SPLIT);
        vSplitPane_.setBottomComponent(document_.getBrowserMenu());
        vSplitPane_.setResizeWeight(0.5);
        vSplitPane_.setTopComponent(document_.getLayersMenu());

        hSplitPane_ = new ClearSplitPane();
        hSplitPane_.setLeftComponent(document_.getViewer());
        hSplitPane_.setResizeWeight(1.0);
        hSplitPane_.setRightComponent(vSplitPane_);
        hSplitPane_.setOneTouchExpandable(true);
        add(hSplitPane_);
        add(document_.getPalette(), BorderLayout.WEST);


    }
    public void showSimulation(){
        System.out.println("show simulation");


        vSplitPane2_ = new ClearSplitPane(JSplitPane.VERTICAL_SPLIT);
        vSplitPane2_.setTopComponent(document_.getViewer());
        vSplitPane2_.setResizeWeight(0.7);
        vSplitPane2_.setBottomComponent(document_.getSimulationPanel());
        vSplitPane2_.setOneTouchExpandable(true);

        hSplitPane_.setLeftComponent(vSplitPane2_);
        hSplitPane_.setRightComponent(vSplitPane_);
        hSplitPane_.setDividerLocation(0.8);
        hSplitPane_.setResizeWeight(1.0);

        add(hSplitPane_);
        add(document_.getPalette(), BorderLayout.WEST);
    }

    public void hideSimulation(){

        hSplitPane_.setLeftComponent(document_.getViewer());
        hSplitPane_.setRightComponent(vSplitPane_);
        hSplitPane_.setDividerLocation(0.8);
        hSplitPane_.setOneTouchExpandable(true);
        add(hSplitPane_);
        add(document_.getPalette(), BorderLayout.WEST);
    }

    public void find() {
        String searchQuery = JOptionPane.showInputDialog(null, "Find:");
        boolean foundOne = false;
        if (searchQuery != null) {
            ArrayList<AbstractNode> nodeList = getActiveNodes();
            for (AbstractNode node : nodeList) {
                if (searchQuery.equals(node.getText())) {
                    foundOne = true;
                    node.setSelected(true);
                }
                else if (node.isSelected()) {
                    foundOne = true;
                    node.setSelected(false);
                }
                else {
                    List<Gene> geneList = ((AbstractActivity) node).getGenes();
                    for (Gene gene : geneList) {
                        if (gene.getId().equals(searchQuery) || gene.getName().equals(searchQuery)) {
                            foundOne = true;
                            node.setSelected(true);
                        }
                    }
                }
            }
        }
        if (foundOne) {
            document_.getCanvas().repaint();
        }
    }

    public ArrayList<AbstractNode> getActiveNodes(){

        ArrayList<AbstractNode> allGlyphs = new ArrayList<AbstractNode>();
        Layer layer;
        Map map = document_.getBrowserMenu().getSelectedMap();

        for (int i = map.getLayerCount() - 1; i >= 0; i--) {

            layer = map.getLayerAt(i);

            if (layer.isActive()) {

                for (int j = layer.getGlyphCount() - 1; j >= 0; j--) {
                    AbstractGlyph glyph = layer.getGlyphAt(j);
                    if (glyph instanceof AbstractNode){
                        allGlyphs.add((AbstractNode)glyph);
                    }

                }
            }
        }
        return allGlyphs;

    }
}