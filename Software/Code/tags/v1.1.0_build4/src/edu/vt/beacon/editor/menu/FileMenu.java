package edu.vt.beacon.editor.menu;

import edu.vt.beacon.editor.action.ActionType;
import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.swing.platform.PlatformMenuItem;

import java.awt.event.KeyEvent;

public class FileMenu extends AbstractMenu {
    private static final long serialVersionUID = 1L;

    // FIXME complete constructor
    public FileMenu(Document document) {
        super(document, "File", KeyEvent.VK_F);
    }

    // FIXME complete method
    @Override
    protected void buildMenu() {
        PlatformMenuItem propertiesItem = new PlatformMenuItem();

        propertiesItem.setAction(document_.getAction(
                ActionType.FILE_PROPERTIES));

        PlatformMenuItem preferencesItem = new PlatformMenuItem();

        preferencesItem.setAction(document_.getAction(
                ActionType.FILE_PREFERENCES));

        PlatformMenuItem newItem = new PlatformMenuItem();

        newItem.setAction(document_.getAction(
                ActionType.FILE_NEW));

        PlatformMenuItem saveItem = new PlatformMenuItem();

        saveItem.setAction(document_.getAction(
                ActionType.FILE_SAVE));

        PlatformMenuItem openItem = new PlatformMenuItem();

        openItem.setAction(document_.getAction(
                ActionType.FILE_OPEN));

        PlatformMenuItem exportItem = new PlatformMenuItem();

        exportItem.setAction(document_.getAction(
                ActionType.FILE_EXPORT));

        PlatformMenuItem compImportItem = new PlatformMenuItem();

        compImportItem.setAction(document_.getAction(
                ActionType.FILE_BACK_COMP_IMPORT));

        add(newItem);
        add(openItem);
        add(saveItem);
        add(exportItem);
        add(compImportItem);
        add(propertiesItem);
        add(preferencesItem);

    }
}