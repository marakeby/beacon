
package edu.vt.beacon.editor.action.handler;

import edu.vt.beacon.editor.action.Action;
import edu.vt.beacon.editor.command.Command;
import edu.vt.beacon.editor.command.CommandType;
import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.menu.ViewMenu;
import edu.vt.beacon.editor.util.CanvasGridManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Date;

public class ViewHandler
        implements ActionHandler {
    private static final float ZOOM_FACTOR = 1.1f;
    private static final ViewHandler instance_ = new ViewHandler();

    // TODO document method
    public static ViewHandler getInstance() {
        return instance_;
    }

    // FIXME complete method
    @Override
    public void handle(Action action, ActionEvent event) {
        Document document = action.getDocument();

        switch (action.getType()) {
            case VIEW_GRID_LINES:
                CanvasGridManager.setEnabled(((JCheckBoxMenuItem) event.getSource()).isSelected());
                document.getCanvas().repaint();
                break;
            case VIEW_ZOOM:
                break;
            case VIEW_ZOOM_IN:
                document.getCanvas().setZoomFactor(document.getCanvas().getZoomFactor() * ZOOM_FACTOR);
                ((ViewMenu) document.getMenuBar().getMenu("View")).clearSelection();
                document.getStateManager().insert(new Command(CommandType.ZOOMING, document.getPathway().copy(),
                        document.getCanvas().getZoomFactor(), new Date().getTime()));
                break;
            case VIEW_ZOOM_OUT:
                document.getCanvas().setZoomFactor(document.getCanvas().getZoomFactor() / ZOOM_FACTOR);
                ((ViewMenu) document.getMenuBar().getMenu("View")).clearSelection();
                document.getStateManager().insert(new Command(CommandType.ZOOMING, document.getPathway().copy(),
                        document.getCanvas().getZoomFactor(), new Date().getTime()));
                break;
            default:
                float zoom = Integer.parseInt(action.getType().toString().replace("VIEW_ZOOM_", "")) / 100f;
                document.getCanvas().setZoomFactor(zoom);
                document.getStateManager().insert(new Command(CommandType.ZOOMING, document.getPathway().copy(),
                        document.getCanvas().getZoomFactor(), new Date().getTime()));
        }
    }

}
