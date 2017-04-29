package edu.vt.beacon.editor.menu;

import edu.vt.beacon.editor.action.ActionType;
import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.swing.platform.PlatformMenuItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class SimulateMenu extends AbstractMenu {

    PlatformMenuItem simulateItem_;
    PlatformMenuItem exportItem_;
    PlatformMenuItem loadItem_;
    PlatformMenuItem showHide_;

    public SimulateMenu(Document document) {
        super(document, "Simulate", KeyEvent.VK_S);
    }

    @Override
    protected void buildMenu() {
        simulateItem_ = new PlatformMenuItem();
        exportItem_ = new PlatformMenuItem();
        loadItem_ = new PlatformMenuItem();
        showHide_ = new PlatformMenuItem();

        add(exportItem_);
        add(loadItem_);
        add(simulateItem_);
        add(showHide_);

    }
    public void registerActions(Document doc)
    {
        simulateItem_.setAction(document_.getAction(ActionType.SIMULATE_SIMULATE));
        exportItem_.setAction(document_.getAction(ActionType.SIMULATE_EXPORT));
        loadItem_.setAction(document_.getAction(ActionType.SIMULATE_LOAD_SBML));
        showHide_.setAction(document_.getAction(ActionType.SIMULATE_SHOW_HIDE));
    }

}
