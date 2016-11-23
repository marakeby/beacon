package edu.vt.beacon.editor.frame;

import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.resources.icons.IconType;
import edu.vt.beacon.editor.swing.ClearSplitPane;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

public class Frame extends JFrame {
    private static final long serialVersionUID = 1L;

    private ClearSplitPane hSplitPane_;

    private ClearSplitPane vSplitPane_;

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

        setVisible(true);

        hSplitPane_.setDividerLocation(document.getDouble("frame.hSplit"));
        vSplitPane_.setDividerLocation(document.getDouble("frame.vSplit"));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
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

        add(hSplitPane_);
        add(document_.getPalette(), BorderLayout.WEST);
    }
}