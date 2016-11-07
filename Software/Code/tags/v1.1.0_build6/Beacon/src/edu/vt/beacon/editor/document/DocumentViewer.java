package edu.vt.beacon.editor.document;

/**
 * Created by marakeby on 10/24/16.
 */


import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;

public class DocumentViewer
        extends JPanel
{
    private ArrayList<Document> projects;
    private JTabbedPane projectTabs;

    public DocumentViewer()
    {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(
                Color.getHSBColor(1.0F, 1.0F, 0.35F), 2));
        setLayout(new GridLayout(0, 1));

        this.projects = new ArrayList();

        this.projectTabs = new JTabbedPane();
        this.projectTabs.addChangeListener(new TabListener(this));
        this.projectTabs.setFocusable(false);
        this.projectTabs.setTabLayoutPolicy(1);

        add(this.projectTabs);
    }

    public void addProject(Document p)
    {
        System.out.println("number of projects " + this.projects.size());
        addProject(this.projects.size(), p);
    }

    public void addProject(int index, Document p)
    {
        this.projects.add(index, p);

        this.projectTabs.insertTab("", null, p.getCanvas().getScrollPane(), null, index);
        this.projectTabs.setTabComponentAt(index, new DocumentTab(p.getFile().getName(), p));
        this.projectTabs.setSelectedIndex(index);
    }

    public ArrayList<Document> getProjects()
    {
        return this.projects;
    }

    public JTabbedPane getProjectTabs()
    {
        return this.projectTabs;
    }

    public void removeProject(Document p)
    {
        if (p.isChanged())
        {
            int result = JOptionPane.showConfirmDialog(
                    p.getCanvas(), "The selected project has not been saved. Are you sure you want to close the project?",

                    "Confirm Close",
                    0, 2);
            if (result != 0) {
                return;
            }
        }

        this.projects.remove(p);

        this.projectTabs.remove(p.getCanvas().getScrollPane());
    }

    public void setSelectedProject(Document p)
    {
        int index= this.projects.indexOf(p);
        System.out.println("selecting index " + index + " " + p.getFile().toString());
        this.projectTabs.setSelectedIndex(index);
    }

    private class TabListener
            implements ChangeListener
    {
        private DocumentViewer doc;
        private TabListener(DocumentViewer doc) {this.doc = doc ;}

        public void stateChanged(ChangeEvent ce)
        {
            JTabbedPane source = (JTabbedPane) ce.getSource();
            int index= source.getSelectedIndex();

            if ( index >=0 ) {
                DocumentTab tab = (DocumentTab)source.getTabComponentAt(index);
                if (tab !=null) {
                    Document doc = tab.getDocument();
                    if (doc !=null) {
                        System.out.println("tab change, index= " + index + ", file = " + doc.getFile().getAbsolutePath() + "empty? " + doc.isEmpty() + "change? " + doc.isChanged());
                        doc.refresh();
                    }
                }
            }


        }
    }
}

