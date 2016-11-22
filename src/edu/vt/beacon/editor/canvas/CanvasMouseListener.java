package edu.vt.beacon.editor.canvas;

import edu.vt.beacon.editor.action.handler.EditHandler;
import edu.vt.beacon.editor.command.Command;
import edu.vt.beacon.editor.command.CommandType;
import edu.vt.beacon.editor.dialog.label.LabelDialog;
import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.document.DocumentState;
import edu.vt.beacon.editor.palette.PaletteButton;
import edu.vt.beacon.graph.AbstractEntity;
import edu.vt.beacon.graph.Orientable;
import edu.vt.beacon.graph.OrientationType;
import edu.vt.beacon.graph.glyph.AbstractGlyph;
import edu.vt.beacon.graph.glyph.arc.AbstractArc;
import edu.vt.beacon.graph.glyph.auxiliary.Bound;
import edu.vt.beacon.graph.glyph.auxiliary.BoundType;
import edu.vt.beacon.graph.glyph.node.AbstractNode;
import edu.vt.beacon.graph.glyph.node.activity.AbstractActivity;
import edu.vt.beacon.graph.glyph.node.annotation.Annotation;
import edu.vt.beacon.graph.glyph.node.auxiliary.Label;
import edu.vt.beacon.graph.glyph.node.auxiliary.Port;
import edu.vt.beacon.graph.glyph.node.container.Compartment;
import edu.vt.beacon.graph.glyph.node.operator.AbstractOperator;
import edu.vt.beacon.graph.glyph.node.submap.Submap;
import edu.vt.beacon.graph.glyph.node.submap.Tag;
import edu.vt.beacon.graph.glyph.node.submap.Terminal;
import edu.vt.beacon.graph.legend.Legend;
import edu.vt.beacon.layer.Layer;
import edu.vt.beacon.map.Map;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

public class CanvasMouseListener extends MouseAdapter {
    private static final int LOCK_HORIZONTAL = 0;

    private static final int LOCK_OFF = 1;

    private static final int LOCK_VERTICAL = 2;

    private AbstractEntity glyph_;

    private boolean isDragEnabled_;

    private boolean shouldRepaint_;

    private Bound bound_;

    private Document document_;

    private int shiftLock_;

    private Point2D.Float currentPoint_;

    private Point2D.Float dragStartPoint_;

    private Point2D.Float dragStopPoint_;

    private Port port_;

    // TODO document constructor
    protected CanvasMouseListener(Document document) {
        document_ = document;
        shiftLock_ = LOCK_OFF;
        currentPoint_ = new Point2D.Float();
        dragStartPoint_ = new Point2D.Float();
        dragStopPoint_ = new Point2D.Float();
    }

    // TODO document method
    protected AbstractEntity getActiveGlyph() {
        return glyph_;
    }

    // TODO document method
    private Bound getBoundAt(AbstractGlyph glyph, Point2D.Float point) {
        if (glyph == null)
            return null;

        for (int i = glyph.getBoundCount() - 1; i >= 0; i--)
            if (glyph.getBoundAt(i).contains(point))

                return glyph.getBoundAt(i);

        return null;
    }

    // TODO document method
    private AbstractEntity getGlyphAt(Point2D.Float point, MouseEvent event) {
        Layer layer;
        Map map = document_.getBrowserMenu().getSelectedMap();

        if (map.getLegend() != null && map.getLegend().contains(point))
            return map.getLegend();

        for (int i = map.getLayerCount() - 1; i >= 0; i--) {

            layer = map.getLayerAt(i);

            if (layer.isActive())
                for (int j = layer.getGlyphCount() - 1; j >= 0; j--)
                    if (layer.getGlyphAt(j) instanceof Annotation &&
                            ((Annotation) layer.getGlyphAt(j)).getCalloutPoint() != null &&
                            ((Annotation) layer.getGlyphAt(j)).getCalloutPoint().contains(point))

                        return ((Annotation) layer.getGlyphAt(j)).getCalloutPoint();
        }


        for (int i = map.getLayerCount() - 1; i >= 0; i--) {

            layer = map.getLayerAt(i);

            if (layer.isActive()) {

                for (int j = layer.getGlyphCount() - 1; j >= 0; j--)
                    if (layer.getGlyphAt(j).contains(point)) {

                        AbstractGlyph glyph = layer.getGlyphAt(j);

                        if (glyph instanceof Compartment && ((Compartment) glyph).getLabel().contains(point)
                                && event.isAltDown())
                            return ((Compartment) glyph).getLabel();

                        if (glyph instanceof Submap && ((Submap) glyph).getTerminals() != null) {

                            Set<Terminal> terminals = ((Submap) glyph).getTerminals();
                            for (Terminal terminal : terminals)
                                if (terminal.contains(point))
                                    return terminal;

                        }

                        return layer.getGlyphAt(j);
                    }
            }
        }

        return null;
    }

    // TODO document method
    private AbstractGlyph getGlyphAt(Point2D.Float point,
                                     AbstractEntity ignoredGlyph) {
        Layer layer;
        Map map = document_.getBrowserMenu().getSelectedMap();

        for (int i = map.getLayerCount() - 1; i >= 0; i--) {

            layer = map.getLayerAt(i);

            if (layer.isActive()) {

                for (int j = layer.getGlyphCount() - 1; j >= 0; j--)
                    if (layer.getGlyphAt(j).contains(point) &&
                            !layer.getGlyphAt(j).equals(ignoredGlyph))

                        return layer.getGlyphAt(j);
            }
        }

        return null;
    }

    private AbstractGlyph getNodeAt(Point2D.Float point,
                                     AbstractEntity ignoredGlyph) {
        Layer layer;
        Map map = document_.getBrowserMenu().getSelectedMap();

        for (int i = map.getLayerCount() - 1; i >= 0; i--) {

            layer = map.getLayerAt(i);

            if (layer.isActive()) {

                for (int j = layer.getGlyphCount() - 1; j >= 0; j--)
                    if (layer.getGlyphAt(j).contains(point) &&
                            !layer.getGlyphAt(j).equals(ignoredGlyph) && layer.getGlyphAt(j) instanceof AbstractNode )

                        return layer.getGlyphAt(j);
            }
        }

        return null;
    }

    private Compartment getCompartmenteAt(Point2D.Float point,
                                    AbstractEntity ignoredGlyph) {

        Layer layer;
        Map map = document_.getBrowserMenu().getSelectedMap();

        for (int i = map.getLayerCount() - 1; i >= 0; i--) {

            layer = map.getLayerAt(i);

            if (layer.isActive()) {

                for (int j = layer.getGlyphCount() - 1; j >= 0; j--)
                    if (layer.getGlyphAt(j).contains(point) &&
                            !layer.getGlyphAt(j).equals(ignoredGlyph) && layer.getGlyphAt(j) instanceof Compartment )

                        return (Compartment) layer.getGlyphAt(j);
            }
        }

        return null;
    }

    // TODO document method
    public Port getPortAt(AbstractGlyph glyph, Point2D.Float point) {
        if (glyph == null || !(glyph instanceof AbstractNode))

            return null;

        AbstractNode node = (AbstractNode) glyph;

        for (int i = node.getPortCount() - 1; i >= 0; i--)
            if (node.getPortAt(i).contains(point))

                return node.getPortAt(i);

        return null;
    }

    // FIXME complete method
    private void initializeGlyph(AbstractGlyph glyph) {
        glyph.setBackgroundColor(document_.getColor("glyph.background"));
        glyph.setForegroundColor(document_.getColor("glyph.foreground"));
        glyph.setHypothetical(document_.getBoolean("glyph.hypothetical"));
        glyph.setLineWidth(document_.getFloat("glyph.lineWidth"));

        if (glyph instanceof Tag)
            glyph.setPadding(0);
        else
            glyph.setPadding(document_.getFloat("glyph.padding"));


        if (!(glyph instanceof AbstractNode))
            return;

        AbstractNode node = (AbstractNode) glyph;
        node.setFontColor(document_.getColor("font.color"));
        node.setFont(new Font(document_.get("font.name"),
                document_.getInteger("font.style"), 12).deriveFont(
                document_.getFloat("font.size")));

        if (node instanceof Orientable)
            node.setOrientation(OrientationType.values()
                    [document_.getInteger("glyph.orientation")]);
    }

    // TODO document method
    @Override
    public void mouseDragged(MouseEvent event) {
        if (!isDragEnabled_)

            return;

        setPointCoordinates(event, dragStopPoint_);
        document_.getCanvas().scrollRectToVisible(
                new Rectangle(event.getPoint().x, event.getPoint().y, 1, 1));

        if (glyph_ == null || !glyph_.isSelected()) {

            setSelectionBoxCoordinates();

            document_.getCanvas().setState(CanvasStateType.SELECTION_DRAWING);
            document_.getCanvas().repaint();

            return;
        }

        processShiftLock(event);

        if (bound_ != null) {

            if (bound_.getType() == BoundType.POINT) {

                processPointMovement();

                document_.getCanvas().repaint();
            } else {

                processResize();

                document_.getCanvas().setState(
                        CanvasStateType.SELECTION_SIZING);
            }
        } else {

            processMovement();

            document_.getCanvas().setState(CanvasStateType.SELECTION_MOVING);
            document_.getCanvas().repaint();
        }

        currentPoint_.setLocation(dragStopPoint_);
    }

    // FIXME complete method
    @Override
    public void mousePressed(MouseEvent event) {
        document_.getCanvas().getScrollPane().requestFocus();

        shouldRepaint_ = false;
        setPointCoordinates(event, currentPoint_);

        processGlyph(event);

        glyph_ = getGlyphAt(currentPoint_, event);
        if (glyph_ instanceof AbstractGlyph)
            bound_ = getBoundAt((AbstractGlyph) glyph_, currentPoint_);
        else
            bound_ = null;

        boolean isLeftClick = (event.getButton() == MouseEvent.BUTTON1);

        if (isLeftClick && !event.isPopupTrigger()) {

            isDragEnabled_ = true;
            dragStartPoint_.setLocation(currentPoint_);

            processSelection(event);
            processCursor();
        } else if (event.isPopupTrigger()) {

            processPopupMenu(event);
        }

        if (shouldRepaint_)
            document_.getCanvas().repaint();
    }

    // FIXME complete method
    @Override
    public void mouseReleased(MouseEvent event) {
        shouldRepaint_ = false;
        setPointCoordinates(event, currentPoint_);

        //double click event
        if (event.getClickCount() == 2 && !event.isConsumed()) {

            ArrayList<AbstractGlyph> selectedGlyphs = document_.getBrowserMenu().getSelectedMap().getSelectedGlyphs();

            if (selectedGlyphs != null && selectedGlyphs.size() == 1 && selectedGlyphs.get(0) instanceof AbstractNode) {
                event.consume();
                LabelDialog.createDialog(document_);
                return;
            }
        }

        boolean isLeftRelease = (event.getButton() == MouseEvent.BUTTON1);

        if (isLeftRelease) {

            if (document_.getCanvas().isPointMoving()) {

                if (!dragStopPoint_.equals(dragStartPoint_)) {

                    document_.setChanged(true);
                    new DocumentState(document_, "Point Movement", false);
                }
            } else if (document_.getCanvas().isPortChanging()) {

                shouldRepaint_ = true;
                processPortChange();
            } else if (document_.getCanvas().isSelectionDrawing()) {

                shouldRepaint_ = true;
                selectGlyphs();
            } else if (document_.getCanvas().isSelectionMoving()) {

                if (!dragStopPoint_.equals(dragStartPoint_)) {

                    document_.setChanged(true);
                    new DocumentState(document_, "Glyph Movement", false);
                }
            } else if (document_.getCanvas().isSelectionSizing()) {

                if (!dragStopPoint_.equals(dragStartPoint_)) {

                    document_.setChanged(true);
                    new DocumentState(document_, "Glyph Sizing", false);
                }
            }

            isDragEnabled_ = false;
            document_.getCanvas().setState(CanvasStateType.NORMAL);
            document_.getCanvas().setCursor(Cursor.getDefaultCursor());
        } else if (event.isPopupTrigger()) {

            // reset active glyphs

            processPopupMenu(event);
        }

        if (shouldRepaint_)
            document_.getCanvas().repaint();
    }

    // TODO document method
    private void processCursor() {
        int cursorType = Cursor.DEFAULT_CURSOR;

        if (bound_ != null)
            cursorType = bound_.getType().getCursorType();

        else if (glyph_ != null)
            cursorType = Cursor.MOVE_CURSOR;

        document_.getCanvas().setCursor(
                Cursor.getPredefinedCursor(cursorType));
    }

    // TODO document method
    private void processGlyph(MouseEvent event) {
        Layer layer = document_.getLayersMenu().getSelectedLayer();

        if (layer == null)
            return;

        boolean isLeftClick = (event.getButton() == MouseEvent.BUTTON1);
        PaletteButton button = document_.getPalette().getSelectedButton();

        if (!isLeftClick || button == null || !layer.isActive())
            return;

        AbstractGlyph glyph = button.getGlyphType().newGlyph();
        initializeGlyph(glyph);
        layer.add(glyph);
        glyph.setCenter(currentPoint_);

        if (!event.isShiftDown())
            button.setSelected(false);

        if (glyph instanceof Submap) {

            document_.getBrowserMenu().addSubmap((Submap) glyph, document_.getBrowserMenu().getSelectedMap());

        }

        if (glyph instanceof Tag) {

            Tag tag = (Tag) glyph;
            tag.setParent(document_.getBrowserMenu().getParentOfSelectedMap());

            if (tag.getParent() != null)
                tag.getParent().createTerminal((Tag) glyph);

        }

        shouldRepaint_ = true;
        document_.setChanged(true);

        document_.getStateManager().insert(new Command(CommandType.CREATING_GLYPH, document_.getPathway().copy(),
                document_.getCanvas().getZoomFactor(), new Date().getTime()));

        new DocumentState(document_, glyph.getType().toString(), false);
    }

    private void removeGlyphfromAllCompartments(AbstractGlyph glyph, AbstractEntity ignoredGlyph){
        Layer layer;
        Map map = document_.getBrowserMenu().getSelectedMap();

        for (int i = map.getLayerCount() - 1; i >= 0; i--) {

            layer = map.getLayerAt(i);

            if (layer.isActive()) {

                for (int j = layer.getGlyphCount() - 1; j >= 0; j--)
                    if (!layer.getGlyphAt(j).equals(ignoredGlyph) && layer.getGlyphAt(j) instanceof Compartment )
                        ((Compartment) layer.getGlyphAt(j)).removeNode(glyph);
            }
        }
    }
    private void processCompartmentMembership(AbstractGlyph glyph){
        // check if it is dragged to a compartment
        if (glyph instanceof AbstractNode){
            Compartment container = getCompartmenteAt(dragStopPoint_, glyph);
            if (container !=null) {
                if (container.addNode(glyph)) // try to add the node to the compartment, the compartment will check if it is already adde or not, if it is already inside it or not.
                {
                    removeGlyphfromAllCompartments(glyph, container);
                    ((AbstractNode) glyph).setParentCompartment(container);
                }
            }
            //remove glyph from the compartment
            if (container ==null) {
                if (((AbstractNode) glyph).getParentCompartment() != null) {
                    ((AbstractNode) glyph).getParentCompartment().removeNode(glyph);

                }
            }

        }
    }

    // TODO document method
    private void processMovement() {
        Layer layer;
        AbstractGlyph glyph;
        Map map = document_.getBrowserMenu().getSelectedMap();

        if (map.getLegend() != null && map.getLegend().isSelected()) {
            map.getLegend().move(dragStopPoint_.x - currentPoint_.x,
                    dragStopPoint_.y - currentPoint_.y);
            return;
        }

        for (int i = 0; i < map.getLayerCount(); i++) {

            layer = map.getLayerAt(i);

            if (layer.isActive()) {

                for (int j = 0; j < layer.getGlyphCount(); j++) {

                    glyph = layer.getGlyphAt(j);

                    if (glyph.isSelected()) {

                        if (glyph instanceof AbstractNode) processCompartmentMembership(glyph);

                        glyph.move(dragStopPoint_.x - currentPoint_.x,
                                dragStopPoint_.y - currentPoint_.y);

                    } else if (glyph instanceof Compartment && ((Compartment) glyph).getLabel().isSelected()) {
                        ((Compartment) glyph).getLabel().move(dragStopPoint_.x - currentPoint_.x,
                                dragStopPoint_.y - currentPoint_.y);
                        ((Compartment) glyph).setCenteredLabel(false);

                    } else if (glyph instanceof Annotation && ((Annotation) glyph).getCalloutPoint() != null &&
                            ((Annotation) glyph).getCalloutPoint().isSelected()) {

                        ((Annotation) glyph).getCalloutPoint().move(dragStopPoint_.x - currentPoint_.x,
                                dragStopPoint_.y - currentPoint_.y);

                    } else if (glyph instanceof Submap && ((Submap) glyph).getTerminals() != null) {

                        for (Terminal t : ((Submap) glyph).getTerminals())
                            if (t.isSelected())
                                t.move(dragStopPoint_.x - currentPoint_.x, dragStopPoint_.y - currentPoint_.y);

                    }

                }
            }
        }

        document_.getStateManager().insert(new Command(CommandType.DRAGGING, document_.getPathway().copy(),
                document_.getCanvas().getZoomFactor(), new Date().getTime()));
    }

    // TODO document method
    private void processPointMovement() {
        int pointIndex = -1;

        if (!(glyph_ instanceof AbstractGlyph))
            return;

        for (int i = 0; i < ((AbstractGlyph) glyph_).getBoundCount(); i++)
            if (((AbstractGlyph) glyph_).getBoundAt(i).equals(bound_))
                pointIndex = i;

        AbstractArc arc = (AbstractArc) glyph_;

        if (pointIndex == 0) {

            if (arc.getSourcePort() != null)
                port_ = arc.getSourcePort();

            arc.setSourcePort(null);
            document_.getCanvas().setState(CanvasStateType.SOURCE_CHANGING);
        } else if (pointIndex == arc.getPointCount() - 1) {

            if (arc.getTargetPort() != null)
                port_ = arc.getTargetPort();

            arc.setTargetPort(null);
            document_.getCanvas().setState(CanvasStateType.TARGET_CHANGING);
        } else {

            document_.getCanvas().setState(CanvasStateType.POINT_MOVING);
        }

        arc.getPointAt(pointIndex).x += dragStopPoint_.x - currentPoint_.x;
        arc.getPointAt(pointIndex).y += dragStopPoint_.y - currentPoint_.y;
        arc.update();
    }

    // FIXME complete method
    private void processPopupMenu(MouseEvent e) {
        CanvasPopupMenu popup = new CanvasPopupMenu(document_, currentPoint_);
        popup.show(e.getComponent(), e.getX(), e.getY());
    }

    // TODO document method
    private void processPortChange() {
//        AbstractGlyph glyph = getGlyphAt(currentPoint_, glyph_);
        AbstractGlyph glyph = getNodeAt(currentPoint_, glyph_);
        Port port = getPortAt(glyph, currentPoint_);

        AbstractArc arc = (AbstractArc) glyph_;

        if (document_.getCanvas().isSourceChanging()) {

            if (port == null || arc.isValidSource(port))
                arc.setSourcePort(port);
        } else {

            if (port == null || arc.isValidTarget(port))
                arc.setTargetPort(port);
        }

        arc.update();

        if (port_ == null && port == null) {

            if (!dragStopPoint_.equals(dragStartPoint_)) {

                document_.setChanged(true);
                new DocumentState(document_, "Point Movement", false);
            }
        } else {

            if ((port_ != null && port == null) ||
                    (port_ == null && port != null) ||
                    (!port_.equals(port))) {

                document_.setChanged(true);
                new DocumentState(document_, "Port Change", false);
            }
        }

        port_ = null;
    }

    // TODO document method
    private void processResize() {
        shouldRepaint_ = false;

        Layer layer;
        AbstractGlyph glyph;
        Map map = document_.getBrowserMenu().getSelectedMap();

        for (int i = 0; i < map.getLayerCount(); i++) {

            layer = map.getLayerAt(i);

            if (layer.isActive()) {

                for (int j = 0; j < layer.getGlyphCount(); j++) {

                    glyph = layer.getGlyphAt(j);

                    if (glyph.isSelected()) {

                        if (glyph instanceof AbstractNode)
                            processResize((AbstractNode) glyph);

                    }

                    if (glyph instanceof Submap && ((Submap) glyph).getTerminals() != null) {

                        Set<Terminal> terminals = ((Submap) glyph).getTerminals();
                        for (Terminal terminal : terminals)
                            if (terminal.isSelected())
                                processResize(terminal);

                    }

                }
            }
        }

        if (shouldRepaint_)
            document_.getCanvas().repaint();
    }

    // TODO document method
    private void processResize(AbstractNode node) {
        BoundType type = bound_.getType();

        float width = node.getWidth();

        if (type == BoundType.NORTHEAST || type == BoundType.SOUTHEAST)
            width += dragStopPoint_.x - currentPoint_.x;

        else
            width += currentPoint_.x - dragStopPoint_.x;


        float height = node.getHeight();

        if (type == BoundType.SOUTHEAST || type == BoundType.SOUTHWEST)
            height += dragStopPoint_.y - currentPoint_.y;

        else
            height += currentPoint_.y - dragStopPoint_.y;

        //The logical operators should be remained circle after resizing, so we set the width and height the same.
        if (node instanceof AbstractOperator) {

            width = height = Math.max(node.getMinHeight(), (width + height) / 2);

        }

        if (node instanceof Terminal) {

            Terminal terminal = (Terminal) node;

            if (terminal.getOrientation() == OrientationType.RIGHT || terminal.getOrientation() == OrientationType.DOWN) {

                width = Math.min(width, terminal.getParent().getWidth() - terminal.getMinX() + terminal.getParent().getMinX() - 10);
                height = Math.min(height, terminal.getParent().getHeight() - terminal.getMinY() + terminal.getParent().getMinY() - 10);

            } else if (terminal.getOrientation() == OrientationType.LEFT) {

                width = Math.min(width, terminal.getParent().getWidth() - 10);
                height = Math.min(height, terminal.getParent().getHeight() - terminal.getMinY() + terminal.getParent().getMinY() - 10);

            } else if (terminal.getOrientation() == OrientationType.UP) {

                width = Math.min(width, terminal.getParent().getWidth() - terminal.getMinX() + terminal.getParent().getMinX() - 10);
                height = Math.min(height, terminal.getParent().getHeight() - 10);

            }

        }

        if (width != node.getWidth() && width >= node.getMinWidth()) {

            if (type != BoundType.NORTHEAST && type != BoundType.SOUTHEAST)
                node.move(dragStopPoint_.x - currentPoint_.x, 0);

            node.setWidth(width);
            shouldRepaint_ = true;
        }

        if (height != node.getHeight() && height >= node.getMinHeight()) {

            if (type != BoundType.SOUTHEAST && type != BoundType.SOUTHWEST)
                node.move(0, dragStopPoint_.y - currentPoint_.y);

            node.setHeight(height);
            shouldRepaint_ = true;
        }
    }

    // TODO document method
    private void processSelection(MouseEvent event) {
        int nSelectedGlyphs = document_.getContextManager()
                .getStatisticsContext().getSelectedGlyphCount();

        if (glyph_ == null && !event.isShiftDown()) {

            shouldRepaint_ = true;
            EditHandler.getInstance().selectNone(document_, false);

            Legend legend = document_.getBrowserMenu().getSelectedMap().getLegend();
            if (legend != null && legend.isSelected())
                legend.setSelected(false);

        } else if (glyph_ != null && !glyph_.isSelected()) {

            if (!event.isShiftDown() && nSelectedGlyphs > 0)
                EditHandler.getInstance().selectNone(document_, false);

            Legend legend = document_.getBrowserMenu().getSelectedMap().getLegend();
            if (legend != null && legend.isSelected())
                legend.setSelected(false);

            shouldRepaint_ = true;

            if (!(glyph_ instanceof Label)) {
                deselectAll(true);
                glyph_.setSelected(true);

            } else if (glyph_ instanceof Label && event.isAltDown()) {
                deselectAll(false);
                glyph_.setSelected(true);
            }


        } else if (glyph_ != null && event.isShiftDown()) {

            shouldRepaint_ = true;
            glyph_.setSelected(!glyph_.isSelected());
        }
    }

    private void deselectAll(boolean onlyLabels) {
        for (Layer layer : document_.getBrowserMenu().getSelectedMap().getLayers())
            for (AbstractGlyph glyph : layer.getGlyphs()) {

                if (!onlyLabels)
                    glyph.setSelected(false);

                if (glyph instanceof AbstractActivity)
                    ((AbstractActivity) glyph).getLabel().setSelected(false);

                else if (glyph instanceof Compartment)
                    ((Compartment) glyph).getLabel().setSelected(false);
            }
    }

    // TODO document method
    private void processShiftLock(MouseEvent event) {
        if (!event.isShiftDown()) {

            shiftLock_ = LOCK_OFF;
        } else if (shiftLock_ == LOCK_HORIZONTAL) {

            dragStopPoint_.x = dragStartPoint_.x;
        } else if (shiftLock_ == LOCK_VERTICAL) {

            dragStopPoint_.y = dragStartPoint_.y;
        } else {

            if (dragStopPoint_.x != currentPoint_.x) {

                shiftLock_ = LOCK_VERTICAL;
                dragStopPoint_.y = dragStartPoint_.y;
            } else {

                shiftLock_ = LOCK_HORIZONTAL;
                dragStopPoint_.x = dragStartPoint_.x;
            }
        }
    }

    // TODO document method
    private void selectGlyphs() {
        Layer layer;
        AbstractGlyph glyph;
        Map map = document_.getBrowserMenu().getSelectedMap();

        Rectangle2D.Float selectionBox =
                document_.getCanvas().getSelectionBox();

        if (document_.getBrowserMenu().getSelectedMap().getLegend() != null &&
                selectionBox.contains(document_.getBrowserMenu().getSelectedMap().getLegend().getBoundary()))
            document_.getBrowserMenu().getSelectedMap().getLegend().setSelected(true);

        for (int i = 0; i < map.getLayerCount(); i++) {

            layer = map.getLayerAt(i);

            if (layer.isActive()) {

                for (int j = 0; j < layer.getGlyphCount(); j++) {

                    glyph = layer.getGlyphAt(j);

                    if (selectionBox.contains(glyph.getBoundary()))
                        glyph.setSelected(true);
                }
            }
        }
    }

    // FIXME complete method
    private void setPointCoordinates(MouseEvent event, Point2D.Float point) {
        Point eventPoint = event.getPoint();
        point.x = (float) eventPoint.getX();
        point.y = (float) eventPoint.getY();

        float zoomFactor = document_.getCanvas().getZoomFactor();
        point.x /= zoomFactor;
        point.y /= zoomFactor;
    }

    // TODO document method
    private void setSelectionBoxCoordinates() {
        Rectangle2D.Float selectionBox =
                document_.getCanvas().getSelectionBox();

        selectionBox.height = Math.abs(dragStopPoint_.y - dragStartPoint_.y);
        selectionBox.width = Math.abs(dragStopPoint_.x - dragStartPoint_.x);
        selectionBox.x = Math.min(dragStartPoint_.x, dragStopPoint_.x);
        selectionBox.y = Math.min(dragStartPoint_.y, dragStopPoint_.y);
    }
}