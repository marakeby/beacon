package edu.vt.beacon.editor.menu;

import edu.vt.beacon.editor.action.ActionType;
import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.swing.platform.PlatformMenuItem;

import java.awt.event.KeyEvent;

public class FileMenu extends AbstractMenu {
    private static final long serialVersionUID = 1L;

    PlatformMenuItem propertiesItem;
    PlatformMenuItem preferencesItem;
    PlatformMenuItem newItem;
    PlatformMenuItem saveItem;
    PlatformMenuItem openItem;
    PlatformMenuItem exportItem;
    PlatformMenuItem compImportItem;


    // FIXME complete constructor
    public FileMenu(Document document) {
        super(document, "File", KeyEvent.VK_F);
    }


    // FIXME complete method
    @Override
    protected void buildMenu() {
        propertiesItem = new PlatformMenuItem();
        newItem = new PlatformMenuItem();
        preferencesItem = new PlatformMenuItem();
        saveItem = new PlatformMenuItem();
        openItem = new PlatformMenuItem();
        exportItem = new PlatformMenuItem();
        compImportItem = new PlatformMenuItem();

        add(newItem);
        add(openItem);
        add(saveItem);
        add(exportItem);
        add(compImportItem);
        add(propertiesItem);
        add(preferencesItem);

    }
    public void registerActions(Document doc)
    {
        System.out.println("register doc "+ doc.getFile().getAbsolutePath());
        propertiesItem.setAction(document_.getAction(
                ActionType.FILE_PROPERTIES));

        preferencesItem.setAction(document_.getAction(
                ActionType.FILE_PREFERENCES));

        newItem.setAction(document_.getAction(
                ActionType.FILE_NEW));

        saveItem.setAction(document_.getAction(
                ActionType.FILE_SAVE));

        openItem.setAction(document_.getAction(
                ActionType.FILE_OPEN));

        exportItem.setAction(document_.getAction(
                ActionType.FILE_EXPORT));

        compImportItem.setAction(document_.getAction(
                ActionType.FILE_BACK_COMP_IMPORT));
    }
}