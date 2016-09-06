package edu.vt.beacon.editor.menu;

import edu.vt.beacon.editor.action.ActionType;
import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.swing.platform.PlatformMenuItem;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class ViewMenu extends AbstractMenu {
    private static final long serialVersionUID = 1L;

    private ButtonGroup zoomGroup;

    // FIXME complete constructor
    public ViewMenu(Document document) {
        super(document, "View", KeyEvent.VK_V);
    }

    public void clearSelection() {
        if (zoomGroup != null)
            zoomGroup.clearSelection();
    }

    // FIXME complete method
    @Override
    protected void buildMenu() {
        JCheckBoxMenuItem gridLinesItem = new JCheckBoxMenuItem();
        PlatformMenuItem zoomInItem = new PlatformMenuItem();
        PlatformMenuItem zoomOutItem = new PlatformMenuItem();
        JMenu zoomItem = new JMenu("Zoom...");

        gridLinesItem.setAction(document_.getAction(ActionType.VIEW_GRID_LINES));
        zoomInItem.setAction(document_.getAction(ActionType.VIEW_ZOOM_IN));
        zoomOutItem.setAction(document_.getAction(ActionType.VIEW_ZOOM_OUT));

        populateZoomMenuItem(zoomItem);

        add(gridLinesItem);
        add(zoomInItem);
        add(zoomOutItem);
        add(zoomItem);
    }

    private void populateZoomMenuItem(JMenu zoomMenuItem) {

        zoomGroup = new ButtonGroup();

        for (int zoomFactor = 25; zoomFactor <= 200; zoomFactor += 25) {
            for (ActionType actionType : ActionType.values())
                if (actionType.toString().equals("VIEW_ZOOM_" + zoomFactor)) {

                    JRadioButtonMenuItem item = new JRadioButtonMenuItem();
                    item.setAction(document_.getAction(actionType));
                    zoomMenuItem.add(item);
                    zoomGroup.add(item);
                }
        }

    }
}
