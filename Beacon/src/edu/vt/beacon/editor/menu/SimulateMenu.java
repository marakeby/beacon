package edu.vt.beacon.editor.menu;

import edu.vt.beacon.editor.action.ActionType;
import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.swing.platform.PlatformMenuItem;

import java.awt.event.KeyEvent;

public class SimulateMenu extends AbstractMenu {

    PlatformMenuItem simulateItem_;
    PlatformMenuItem blankItem_;

    public SimulateMenu(Document document) {
        super(document, "Simulate", KeyEvent.VK_S);
    }

    @Override
    protected void buildMenu() {
        simulateItem_ = new PlatformMenuItem();

        add(simulateItem_);

    }
    public void registerActions(Document doc)
    {
        simulateItem_.setAction(document_.getAction(ActionType.SIMULATE_SIMULATE));
    }

}
