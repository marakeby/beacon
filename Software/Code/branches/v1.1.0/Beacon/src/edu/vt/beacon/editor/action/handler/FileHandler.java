package edu.vt.beacon.editor.action.handler;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;

import javax.swing.*;

import edu.vt.beacon.editor.EditorApplication;
import edu.vt.beacon.editor.action.Action;
import edu.vt.beacon.editor.dialog.preferences.PreferencesDialog;
import edu.vt.beacon.editor.dialog.properties.PropertiesDialog;
import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.document.DocumentState;
import edu.vt.beacon.editor.menu.FileTypeFilter;
import edu.vt.beacon.io.FileManager;
import edu.vt.beacon.layer.Layer;
import edu.vt.beacon.map.Map;
import edu.vt.beacon.pathway.Pathway;

public class FileHandler
        implements ActionHandler {
    private static final FileHandler instance_ = new FileHandler();

    private HashMap<String, Document> opendDocs;
    private static int fileNo_ = 1;

    private static JTabbedPane tabbedPane ;

    // TODO document method
    public static FileHandler getInstance() {
        return instance_;
    }

    // FIXME complete method
    @Override
    public void handle(Action action, ActionEvent event) {
        switch (action.getType()) {
            case FILE_PROPERTIES:
                propertiesDialog(action.getDocument());
                break;
            case FILE_PREFERENCES:
                showPreferencesDialog(action);
                break;
            case FILE_OPEN:
                processOpenFile(action);
                break;
            case FILE_NEW:
                newFile(action.getDocument());
                break;
            case FILE_SAVE:
                processSaveFile(action);
                break;
            case FILE_SAVE_AS:
                processSaveAsFile(action);
                break;
            case FILE_EXPORT:
                processExportFile(action);
                break;
            case FILE_BACK_COMP_IMPORT:
                processBackwardCompatibilityImport(action);
                break;
            default:
                throw new IllegalStateException("missing action type case");
        }
    }

    private void showPreferencesDialog(Action action) {

        PreferencesDialog dialog = new PreferencesDialog(action.getDocument().getCanvas(), action.getDocument());

    }

    private void save(Document doc){
        System.out.println("saving to " + doc.getFile().getAbsolutePath());
        FileManager.save(doc.getPathway(), doc.getFile().getAbsolutePath());
        doc.setChanged(false);
    }

    private void save_As(Document doc) {

        FileDialog fileDialog = new FileDialog(new Frame(), "Save", FileDialog.SAVE);
        fileDialog.setFilenameFilter(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".sbgn");
            }
        });

        fileDialog.setFile("Untitled.sbgn");
        fileDialog.setVisible(true);

        if (fileDialog.getFile() != null && !fileDialog.getFile().isEmpty() && fileDialog.getDirectory() != null
                && !fileDialog.getDirectory().isEmpty()) {
            String fileName = fileDialog.getDirectory() + fileDialog.getFile();
            FileManager.save(doc.getPathway(), fileName);
            doc.setFile(new File(fileName));
            doc.setChanged(false);
            doc.getViewer().refresh();
        }
    }

    private void processSaveAsFile(Action action) {

        save_As(action.getDocument());

//            action.getDocument().setFile(new File(fileDialog.getDirectory() + fileDialog.getFile()));
//            action.getDocument().setChanged(false);
    }

    private void processSaveFile(Action action) {

        Document doc = action.getDocument();
        if(doc.isSavedAtLeastOnce())
            save(doc);
        else
            save_As(doc);

//            action.getDocument().setFile(new File(fileDialog.getDirectory() + fileDialog.getFile()));
//            action.getDocument().setChanged(false);
    }


    private void processOpenFile(Action action) {
        FileDialog fileDialog = new FileDialog(new Frame(), "Open", FileDialog.LOAD);
        fileDialog.setFilenameFilter(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".sbgn");
            }
        });

        fileDialog.setVisible(true);

        if (fileDialog.getFile() != null && !fileDialog.getFile().isEmpty() && fileDialog.getDirectory() != null
                && !fileDialog.getDirectory().isEmpty()) {
            Pathway pathway = FileManager.load(fileDialog.getDirectory() + fileDialog.getFile());

//            action.getDocument().setFile(new File(fileDialog.getDirectory() + fileDialog.getFile()));
//            action.getDocument().setPathway(pathway);
//            action.getDocument().refresh();
//            action.getDocument().getFrame()
            String path = fileDialog.getDirectory() + fileDialog.getFile();
            System.out.print("trying to open file " + path);
            if (action.getDocument().getViewer().setSelectedProject(path))
                return;

            Document doc = new Document(new File(fileDialog.getDirectory() + fileDialog.getFile()));
//            action.getDocument().setFile(new File(fileDialog.getDirectory() + fileDialog.getFile()));

            doc.setPathway(pathway);
            new DocumentState(doc);
            doc.setSavedAtLeastOnce(true);
            doc.getViewer();
            doc.refresh();

        }
    }

    private void processBackwardCompatibilityImport(Action action) {
        FileDialog fileDialog = new FileDialog(new Frame(), "Import", FileDialog.LOAD);
        fileDialog.setFilenameFilter(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".bpw");
            }
        });

        fileDialog.setVisible(true);

        if (fileDialog.getFile() != null && !fileDialog.getFile().isEmpty() && fileDialog.getDirectory() != null
                && !fileDialog.getDirectory().isEmpty()) {
            Pathway pathway = FileManager.backwardCompatibilityImport(fileDialog.getDirectory() + fileDialog.getFile());
//            action.getDocument().setFile(new File(fileDialog.getDirectory() + fileDialog.getFile()));
//            action.getDocument().setPathway(pathway);
//            action.getDocument().refresh();
            String path = fileDialog.getDirectory() + fileDialog.getFile();
            System.out.print("trying to open file " + path);
            if (action.getDocument().getViewer().setSelectedProject(path))
                return;

            Document doc = new Document(new File(path));
//            action.getDocument().setFile(new File(fileDialog.getDirectory() + fileDialog.getFile()));
            doc.setPathway(pathway);
            new DocumentState(doc);
            doc.setSavedAtLeastOnce(false);
            doc.getViewer();
            doc.refresh();
        }
    }

    private void processExportFile(Action action) {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileTypeFilter(ExportType.bmp.name(), ExportType.bmp.toString()));
        fileChooser.addChoosableFileFilter(new FileTypeFilter(ExportType.gif.name(), ExportType.gif.toString()));
        fileChooser.addChoosableFileFilter(new FileTypeFilter(ExportType.jpeg.name(), ExportType.jpeg.toString()));
        fileChooser.addChoosableFileFilter(new FileTypeFilter(ExportType.png.name(), ExportType.png.toString()));

        int returnVal = fileChooser.showSaveDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION)
            if (fileChooser.getSelectedFile() != null)
                for (ExportType exportType : ExportType.values())
                    if (exportType.toString().equals(fileChooser.getFileFilter().getDescription())) {
                        FileManager.export(action.getDocument(), fileChooser.getSelectedFile().toString(), exportType);
                        break;
                    }

    }

    // FIXME complete method
    public void newFile(Document document) {
//        if (tabbedPane ==null)
//            tabbedPane = new JTabbedPane();

        String fileName = "untitled";

//        if (fileNo_ > 1)
            fileName += " " + fileNo_;

            fileNo_++;

//        if (document ==null) {
            System.out.println("creating file "+fileName);
            Document newDocument = new Document(new File(fileName));

            Map map = newDocument.getPathway().getMap();
            Layer layer = new Layer("New Layer", map);
            layer.setSelected(true);
            map.add(layer);
            map.setSelected(true);

            new DocumentState(newDocument);
            newDocument.getFrame();
//            newDocument.getTabs();
            EditorApplication.viewer = newDocument.getViewer();
            newDocument.setSavedAtLeastOnce(false);
            newDocument.refresh();

//        }
//        else
//        {
//
//            Pathway pathway = new Pathway("pathway");
//            document.setPathway(pathway);
//            Map map = document.getPathway().getMap();
//            Layer layer = new Layer("New Layer", map);
//            layer.setSelected(true);
//            map.add(layer);
//            map.setSelected(true);
//            document.setFile(new File(fileName));
//            document.getFrame();
//            document.refresh();
//        }
    }

    // CMK
    public void propertiesDialog(Document document) {
        JDialog propertiesDialog = new PropertiesDialog(document);
    }

}