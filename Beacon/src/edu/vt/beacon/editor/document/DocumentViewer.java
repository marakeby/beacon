package edu.vt.beacon.editor.document;

/**
 * Created by marakeby on 10/24/16.
 */


import edu.vt.beacon.editor.EditorApplication;
import edu.vt.beacon.layer.Layer;
import edu.vt.beacon.map.Map;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.io.File;
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
//        System.out.println("number of projects " + this.projects.size());
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

    public boolean removeProject(Document p)
    {
        if (p.isChanged())
        {
            int result = JOptionPane.showConfirmDialog(
                    p.getCanvas(), "The selected project has not been saved. Are you sure you want to close the project?",

                    "Confirm Close",
                    0, 2);
            if (result != 0) {
                return false;
            }
        }
        this.projects.remove(p);
        this.projectTabs.remove(p.getCanvas().getScrollPane());
        return true;
    }


    public boolean isAllProjectsSaved(){

        int count =  this.getProjectTabs().getTabCount();

        for(int i =0; i< count; i++)
        {
            DocumentTab tab = (DocumentTab)getProjectTabs().getTabComponentAt(i);
            Document currentDoc = tab.getDocument();
            if(currentDoc != null){
                if (currentDoc.isChanged())
                    return false ;
            }

        }
        return true;
    }

    public void refresh(){
        int count =  this.getProjectTabs().getTabCount();

        for(int i =0; i< count; i++)
        {
            DocumentTab tab = (DocumentTab)getProjectTabs().getTabComponentAt(i);
            tab.refresh();
        }
    }
    public void highlightProject(Document p, boolean state){
        int count =  this.getProjectTabs().getTabCount();

        for(int i =0; i< count; i++)
        {
            DocumentTab tab = (DocumentTab)getProjectTabs().getTabComponentAt(i);
            if (tab.getDocument().getFile().getAbsoluteFile().equals(p.getFile().getAbsoluteFile()))
                tab.setHighlightTab(state);
        }
    }
    public Boolean setSelectedProject(String path)
    {

        for (Document doc : this.projects)
        {
            String pp= doc.getFile().getAbsolutePath();
//            System.out.println("we have project opend "+pp);
            if (pp.equals(path))
            {
                setSelectedProject(doc);
                return true;
            }
        }

       return false;
    }

    public void setSelectedProject(Document p)
    {
        for (int i =0; i<projects.size(); i++){
            DocumentTab tab = (DocumentTab)this.projectTabs.getTabComponentAt(i);
            String file1 = tab.getDocument().getFile().getAbsolutePath();
            String file2 = p.getFile().getAbsolutePath();
            if (file1.equals(file2))
                projectTabs.setSelectedIndex(i);
        }
//        int index= this.projects.indexOf(p);
//        System.out.println("selecting index " + index + " " + p.getFile().toString());
//        this.projectTabs.setSelectedIndex(index);


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
//                        System.out.println("tab change, index= " + index + ", file = " + doc.getFile().getAbsolutePath() + "empty? " + doc.isEmpty() + "change? " + doc.isChanged());
                        doc.refresh();
                    }
                }
            }
            else {
                String fileName = "untitled 1";

                fileName += " ";

                Document newDocument = new Document(new File(fileName));

                Map map = newDocument.getPathway().getMap();
                Layer layer = new Layer("New Layer", map);
                layer.setSelected(true);
                map.add(layer);
                map.setSelected(true);

                new DocumentState(newDocument);
                newDocument.getFrame();
                EditorApplication.viewer = newDocument.getViewer();
                newDocument.setSavedAtLeastOnce(false);
                newDocument.refresh();
            }


        }
    }
}

