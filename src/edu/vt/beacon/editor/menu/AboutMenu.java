package edu.vt.beacon.editor.menu;

import edu.vt.beacon.editor.action.ActionType;
import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.swing.platform.PlatformMenuItem;

import java.awt.event.KeyEvent;

public class AboutMenu extends AbstractMenu {

    PlatformMenuItem aboutItem_;
    PlatformMenuItem blankItem_;

    public AboutMenu(Document document) {
        super(document, "About", KeyEvent.VK_A);
    }

    @Override
    protected void buildMenu() {
        aboutItem_ = new PlatformMenuItem();

        aboutItem_.setAction(document_.getAction(ActionType.ABOUT_ABOUT));

        add(aboutItem_);

    }

}
