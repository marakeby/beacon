package edu.vt.beacon.editor.util;

import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.graph.AbstractEntity;
import edu.vt.beacon.graph.glyph.AbstractGlyph;
import edu.vt.beacon.graph.glyph.arc.AbstractArc;
import edu.vt.beacon.graph.glyph.auxiliary.Bound;
import edu.vt.beacon.graph.glyph.node.AbstractNode;
import edu.vt.beacon.graph.glyph.node.activity.AbstractActivity;
import edu.vt.beacon.graph.glyph.node.activity.BiologicalActivity;
import edu.vt.beacon.graph.glyph.node.annotation.Annotation;
import edu.vt.beacon.graph.glyph.node.auxiliary.AuxiliaryUnit;
import edu.vt.beacon.graph.glyph.node.auxiliary.Label;
import edu.vt.beacon.graph.glyph.node.auxiliary.Port;
import edu.vt.beacon.graph.glyph.node.container.Compartment;
import edu.vt.beacon.graph.glyph.node.operator.AbstractOperator;
import edu.vt.beacon.graph.glyph.node.submap.AbstractTagTerminal;
import edu.vt.beacon.graph.glyph.node.submap.Submap;
import edu.vt.beacon.graph.glyph.node.submap.Tag;
import edu.vt.beacon.graph.glyph.node.submap.Terminal;
import edu.vt.beacon.graph.legend.Legend;
import edu.vt.beacon.graph.legend.LegendEntry;
import edu.vt.beacon.layer.Layer;
import edu.vt.beacon.map.Map;
import edu.vt.beacon.util.ArcManager;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class RenderingManager {
    private static Document document_;

    private static Graphics2D graphics_;

    private static Map map_;

    // FIXME complete method
    private static void initializeGraphics(Graphics g) {
        graphics_ = (Graphics2D) g;
        graphics_.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        float zoomFactor = document_.getCanvas().getZoomFactor();

        // scale the graphics based on the current zoom level
        graphics_.scale(zoomFactor, zoomFactor);
    }

    // FIXME complete method
    public static void render(Document document, Graphics g) {
        document_ = document;
        map_ = document.getBrowserMenu().getSelectedMap();

        initializeGraphics(g);

        document.getContextManager().reset();

        paintCanvasGrid(g);

        renderLegend(map_.getLegend());

        for (int i = 0; i < map_.getLayerCount(); i++)
            renderLayer(map_.getLayerAt(i));

        if (document_.getCanvas().isSelectionDrawing())
            renderSelectionBox();

        map_.validate();
        ActionManager.updateActions(document);
    }

    private static void paintCanvasGrid(Graphics g) {

        if (!CanvasGridManager.isEnabled())
            return;

        int increment = Math.round(CanvasGridManager.getGridSize());

        int canvasWidth = document_.getCanvas().getWidth();
        int canvasHeight = document_.getCanvas().getHeight();

        // for each horizontal grid line
        for (int i = 0; i < canvasHeight; i += increment) {

            // if the current line is a major grid line
            if (i % (increment * CanvasGridManager.getChunk()) == 0)

                // set a darker grid line color
                g.setColor(Color.GRAY);

                // if the current line is not a major grid line
            else

                // set a lighter grid line color
                g.setColor(Color.LIGHT_GRAY);

            // draw the horizontal grid line
            g.drawLine(0, i, canvasWidth, i);
        }

        // for each vertical grid line
        for (int i = 0; i < canvasWidth; i += increment) {

            // if the current line is a major grid line
            if (i % (increment * CanvasGridManager.getChunk()) == 0)

                // set a darker grid line color
                g.setColor(Color.GRAY);

                // if the current line is not a major grid line
            else

                // set a lighter grid line color
                g.setColor(Color.LIGHT_GRAY);

            // draw the vertical grid line
            g.drawLine(i, 0, i, canvasHeight);
        }

    }

    private static void renderLegend(Legend legend) {
        if (legend == null || legend.isEmpty())
            return;

        graphics_.setColor(legend.getBackgroundColor());
        graphics_.fill(legend.getShape());
        graphics_.setColor(legend.getForegroundColor());

        Stroke originalStroke = graphics_.getStroke();
        graphics_.setStroke(new BasicStroke(legend.getLineWidth(),
                BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0F));
        graphics_.draw(legend.getShape());
        graphics_.setStroke(originalStroke);

        for (LegendEntry entry : legend.getEntries())
            renderLegendEntry(entry);

        if (legend.isSelected()) {

            Composite originalComposite = graphics_.getComposite();
            graphics_.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, 1.0F));

            originalStroke = graphics_.getStroke();
            graphics_.setStroke(new BasicStroke(1.0F));

            graphics_.setColor(document_.getColor("glyph.selection"));
            graphics_.draw(legend.getShape());

            graphics_.setComposite(originalComposite);
            graphics_.setStroke(originalStroke);

            renderBounds(legend);
        }

    }

    private static void renderLegendEntry(LegendEntry entry) {
        if (entry == null || entry.getParent() == null)
            return;

        graphics_.setColor(entry.getColor());
        graphics_.fill(entry.getColorBar());

        Font font = entry.getParent().getFont();
        Color fontColor = entry.getParent().getFontColor();

        graphics_.setColor(fontColor);
        graphics_.setFont(font);

        FontMetrics metrics = graphics_.getFontMetrics();

        float xStart;
        float yStart = entry.getMinY() + metrics.getAscent();

        String line;

        for (int i = 0; i < entry.getLineCount(); i++) {

            line = entry.getLineAt(i);

            xStart = entry.getMinX() + entry.getParent().getColorBarWidth() + entry.getParent().getColorBarTextDistance();

            graphics_.drawString(line, xStart, yStart);

            yStart += metrics.getHeight();
        }

    }

    // FIXME complete method
    private static void renderActivity(AbstractActivity activity) {
        graphics_.setColor(activity.getBackgroundColor());
        graphics_.fill(activity.getShape());
        graphics_.setColor(activity.getForegroundColor());

        if (activity instanceof BiologicalActivity) {

            BiologicalActivity biologicalActivity = (BiologicalActivity) activity;
            if (biologicalActivity.getAuxiliaryUnit() != null)
                biologicalActivity.updateShapeCoordinates(biologicalActivity.getAuxiliaryUnit().getLabel());
        }

        graphics_.draw(activity.getShape());

        renderLabel(activity);
        renderAuxiliaryUnit(activity);

        if (activity.isSelected()) {

            Composite originalComposite = graphics_.getComposite();
            graphics_.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, 1.0F));

            Stroke originalStroke = graphics_.getStroke();
            graphics_.setStroke(new BasicStroke(1.0F));

            graphics_.setColor(document_.getColor("glyph.selection"));
            graphics_.draw(activity.getShape());

            graphics_.setComposite(originalComposite);
            graphics_.setStroke(originalStroke);
        }
    }

    // FIXME complete method
    private static void renderArc(AbstractArc arc) {
        graphics_.setColor(arc.getForegroundColor());
        graphics_.draw(arc.getShape());

        AffineTransform originalTransform = graphics_.getTransform();
        AffineTransform rotatedTransform = graphics_.getTransform();

        Point2D.Float sourcePoint = arc.getPointAt(arc.getPointCount() - 2);
        Point2D.Float targetPoint = arc.getPointAt(arc.getPointCount() - 1);
        ArcManager.setPointCoordinates(sourcePoint, targetPoint);
        rotatedTransform.rotate(ArcManager.getRotation(), targetPoint.x,
                targetPoint.y);

        graphics_.setColor(arc.getBackgroundColor());
        graphics_.setTransform(rotatedTransform);
        graphics_.fill(arc.getEndCap());
        graphics_.setColor(arc.getForegroundColor());
        graphics_.draw(arc.getEndCap());

        if (arc.isSelected()) {

            Composite originalComposite = graphics_.getComposite();
            graphics_.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, 1.0F));

            Stroke originalStroke = graphics_.getStroke();
            graphics_.setStroke(new BasicStroke(1.0F));

            graphics_.setColor(document_.getColor("glyph.selection"));
            graphics_.draw(arc.getEndCap());

            graphics_.setTransform(originalTransform);
            graphics_.draw(arc.getShape());

            graphics_.setComposite(originalComposite);
            graphics_.setStroke(originalStroke);
        } else {

            graphics_.setTransform(originalTransform);
        }
    }

    // TODO document method
    private static void renderBounds(AbstractEntity entity) {
        if (!(entity instanceof AbstractGlyph || entity instanceof Legend))
            return;

        Composite originalComposite = graphics_.getComposite();
        graphics_.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, 1.0F));

        graphics_.setColor(document_.getColor("glyph.selection"));

        Bound bound = null;
        Point2D.Float centerPoint = new Point2D.Float();

        int boundCount = 0;
        if (entity instanceof AbstractGlyph)
            boundCount = ((AbstractGlyph) entity).getBoundCount();
        else if (entity instanceof Legend)
            boundCount = ((Legend) entity).getBoundCount();

        for (int i = 0; i < boundCount; i++) {

            if (entity instanceof AbstractGlyph)
                bound = ((AbstractGlyph) entity).getBoundAt(i);
            else if (entity instanceof Legend)
                bound = ((Legend) entity).getBoundAt(i);

            centerPoint.setLocation(bound.getCenterX(), bound.getCenterY());

            bound.update();
            bound.setCenter(centerPoint);

            graphics_.fill(bound.getShape());
        }

        graphics_.setComposite(originalComposite);
    }

    // FIXME complete method
    private static void renderGlyph(AbstractGlyph glyph) {
        if (document_.getCanvas().isPortChanging())
            if (!glyph.equals(document_.getCanvas().getActiveGlyph()))
                glyph.setSelected(false);

        Stroke originalStroke = graphics_.getStroke();

        if (glyph.isHypothetical())
            graphics_.setStroke(new BasicStroke(glyph.getLineWidth(),
                    BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0F,
                    new float[]{5.0F, 2.0F}, 0.0F));

        else
            graphics_.setStroke(new BasicStroke(glyph.getLineWidth(),
                    BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0F));

        if (glyph instanceof AbstractArc)
            renderArc((AbstractArc) glyph);

        else if (glyph instanceof AbstractNode)
            renderNode((AbstractNode) glyph);

        graphics_.setStroke(originalStroke);

        if (glyph.isSelected())
            renderBounds(glyph);
    }

    // TODO document method
    private static void renderLabel(AbstractGlyph glyph) {
        if (!(glyph instanceof AbstractNode) && !(glyph instanceof AuxiliaryUnit) && !(glyph instanceof Annotation))
            return;

        Font font;
        Label label;
        Color fontColor;

        if (glyph instanceof AbstractNode) {

            AbstractNode node = (AbstractNode) glyph;
            font = node.getFont();
            label = node.getLabel();
            fontColor = node.getFontColor();

        } else {

            AuxiliaryUnit aux = (AuxiliaryUnit) glyph;
            font = aux.getFont();
            label = aux.getLabel();
            fontColor = aux.getFontColor();

        }

        graphics_.setColor(fontColor);
        graphics_.setFont(font);

        FontMetrics metrics = graphics_.getFontMetrics();

        float xStart;
        float yStart = label.getMinY() + metrics.getAscent();

        String line;

        for (int i = 0; i < label.getLineCount(); i++) {

            line = label.getLineAt(i);
            xStart = label.getCenterX() - metrics.stringWidth(line) / 2.0F;

            graphics_.drawString(line, xStart, yStart);

            yStart += metrics.getHeight();
        }

        if (label.isSelected())
            renderSelectedLabel(label);
    }

    private static void renderSelectedLabel(Label label) {
        Composite originalComposite = graphics_.getComposite();
        graphics_.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, 1.0F));

        graphics_.setColor(document_.getColor("glyph.selection"));

        float size = 7.0F;
        graphics_.fill(new Rectangle2D.Float(label.getMinX() - size / 2.0f, label.getMinY() - size / 2.0f, size, size));
        graphics_.fill(new Rectangle2D.Float(label.getMaxX() - size / 2.0f, label.getMinY() - size / 2.0f, size, size));
        graphics_.fill(new Rectangle2D.Float(label.getMaxX() - size / 2.0f, label.getMaxY() - size / 2.0f, size, size));
        graphics_.fill(new Rectangle2D.Float(label.getMinX() - size / 2.0f, label.getMaxY() - size / 2.0f, size, size));

        graphics_.draw(label.getShape());

        graphics_.setComposite(originalComposite);
    }

    private static void renderAuxiliaryUnit(AbstractNode node) {
        if (!(node instanceof BiologicalActivity) && !(node instanceof Compartment))
            return;

        AuxiliaryUnit unitOfInfo = null;

        if (node instanceof BiologicalActivity)
            unitOfInfo = ((BiologicalActivity) node).getAuxiliaryUnit();

        else if (node instanceof Compartment)
            unitOfInfo = ((Compartment) node).getCompartmentUnit();


        if (unitOfInfo == null)
            return;

        unitOfInfo.updateShapeCoordinates(node);
        unitOfInfo.getLabel().updateShapeCoordinates(node);

        graphics_.setColor(unitOfInfo.getBackgroundColor());
        graphics_.fill(unitOfInfo.getShape());

        Stroke originalStroke = graphics_.getStroke();
        graphics_.setStroke(new BasicStroke(unitOfInfo.getLineWidth(),
                BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0F));

        graphics_.setColor(unitOfInfo.getForegroundColor());
        graphics_.draw(unitOfInfo.getShape());

        graphics_.setStroke(originalStroke);

        renderLabel(unitOfInfo);

        if (node.isSelected()) {

            Composite originalComposite = graphics_.getComposite();
            graphics_.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, 1.0F));

            originalStroke = graphics_.getStroke();
            graphics_.setStroke(new BasicStroke(1.0F));

            graphics_.setColor(document_.getColor("glyph.selection"));
            graphics_.draw(unitOfInfo.getShape());

            graphics_.setComposite(originalComposite);
            graphics_.setStroke(originalStroke);
        }
    }

    private static void renderAnnotation(Annotation annotation) {
        if (annotation == null)
            return;

        graphics_.setColor(annotation.getForegroundColor());
        graphics_.draw(annotation.getShape());
        graphics_.setColor(annotation.getBackgroundColor());
        graphics_.fill(annotation.getShape());
        graphics_.setColor(annotation.getForegroundColor());
        graphics_.fill(annotation.getFoldedCorner());

        renderLabel(annotation);

        if (annotation.isSelected()) {

            Composite originalComposite = graphics_.getComposite();
            graphics_.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, 1.0F));

            Stroke originalStroke = graphics_.getStroke();
            graphics_.setStroke(new BasicStroke(1.0F));

            graphics_.setColor(document_.getColor("glyph.selection"));
            graphics_.draw(annotation.getShape());

            graphics_.setComposite(originalComposite);
            graphics_.setStroke(originalStroke);
        }

        if (annotation.getCalloutPoint() != null && annotation.getCalloutPoint().isSelected()) {

            Composite originalComposite = graphics_.getComposite();
            graphics_.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, 1.0F));

            Stroke originalStroke = graphics_.getStroke();
            graphics_.setStroke(new BasicStroke(1.0F));

            graphics_.setColor(document_.getColor("glyph.selection"));
            graphics_.draw(annotation.getCalloutPoint().getShape());

            graphics_.setComposite(originalComposite);
            graphics_.setStroke(originalStroke);
        }

    }

    // FIXME complete method
    private static void renderLayer(Layer layer) {
        AbstractGlyph glyph;

        for (int i = 0; i < layer.getGlyphCount(); i++) {

            glyph = layer.getGlyphAt(i);

            if (!map_.isValid()){
                // Do NOT move nodes inside compartments
                // Do NOT move arcs that connects nodes inside compartments
                float x  = map_.getHorizontalValidationShift();
                float y = map_.getVerticalValidationShift();
//                if ( glyph instanceof  AbstractNode && ((AbstractNode) glyph).getParentCompartment() != null){}
//                if(glyph instanceof  AbstractArc && ((AbstractArc) glyph).getSource()!=null && ((AbstractArc) glyph).getSource().getParentCompartment() != null) {}
//                else if(glyph instanceof  AbstractArc && ((AbstractArc) glyph).getTarget()!=null && ((AbstractArc) glyph).getTarget().getParentCompartment() != null) {}

                if(glyph instanceof AbstractArc ){((AbstractArc)glyph).move_without_dependents(x,y);}
                else if(glyph instanceof Compartment ){((Compartment)glyph).move_without_dependents(x,y);}
                else
                    glyph.move(x,y);
            }
            if (layer.isActive())
                renderGlyph(glyph);

            document_.getContextManager().process(glyph, document_.getCanvas().getZoomFactor());
        }
    }

    // FIXME complete method
    private static void renderNode(AbstractNode node) {
        if (node instanceof AbstractActivity)
            renderActivity((AbstractActivity) node);

        else if (node instanceof AbstractOperator)
            renderOperator((AbstractOperator) node);

        else if (node instanceof Submap)
            renderSubmap((Submap) node);

        else if (node instanceof Compartment)
            renderCompartment((Compartment) node);

        else if (node instanceof Annotation)
            renderAnnotation((Annotation) node);

        else if (node instanceof Tag)
            renderTagTerminal((Tag) node);

        node.setPortRendering(document_.getCanvas().isPortChanging() || document_.getCanvas().isPortShowing());

        if (node.isPortRendering())
            renderPorts(node);
    }

    // FIXME complete method
    private static void renderOperator(AbstractOperator operator) {
        graphics_.setColor(operator.getBackgroundColor());
        graphics_.fill(operator.getShape());
        graphics_.setColor(operator.getForegroundColor());
        graphics_.draw(operator.getPortLine());
        graphics_.draw(operator.getShape());

        renderLabel(operator);

        if (operator.isSelected()) {

            Composite originalComposite = graphics_.getComposite();
            graphics_.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, 1.0F));

            Stroke originalStroke = graphics_.getStroke();
            graphics_.setStroke(new BasicStroke(1.0F));

            graphics_.setColor(document_.getColor("glyph.selection"));
            graphics_.draw(operator.getPortLine());
            graphics_.draw(operator.getShape());

            graphics_.setComposite(originalComposite);
            graphics_.setStroke(originalStroke);
        }
    }

    // FIXME complete method
    private static void renderPorts(AbstractNode node) {
        Stroke originalStroke = graphics_.getStroke();
        BasicStroke fillStroke = new BasicStroke(3.0F);
        BasicStroke lineStroke = new BasicStroke(1.0F);

        AbstractArc activeArc = null;

        if (document_.getCanvas().isTargetChanging() || document_.getCanvas().isSourceChanging())
            activeArc = (AbstractArc) document_.getCanvas().getActiveGlyph();

        Port port;
        Point2D.Float centerPoint = new Point2D.Float();

        for (int i = 0; i < node.getPortCount(); i++) {

            port = node.getPortAt(i);
            centerPoint.setLocation(port.getCenterX(), port.getCenterY());

            port.update();
            port.setCenter(centerPoint);

            graphics_.setColor(Color.black);
            graphics_.setStroke(fillStroke);
            graphics_.draw(port.getShape());

            if (document_.getCanvas().isSourceChanging()) {

                graphics_.setColor(activeArc.isValidSource(port)
                        ? Color.green : Color.red);

            } else if (document_.getCanvas().isTargetChanging()) {

                graphics_.setColor(activeArc.isValidTarget(port)
                        ? Color.green : Color.red);

            } else if (document_.getCanvas().isPortShowing()) {

                graphics_.setColor(Color.yellow);
            }

            graphics_.setStroke(lineStroke);
            graphics_.draw(port.getShape());
        }

        graphics_.setStroke(originalStroke);
    }

    // TODO document method
    private static void renderSelectionBox() {
        Composite originalComposite = graphics_.getComposite();
        graphics_.setComposite(AlphaComposite.getInstance(
                AlphaComposite.SRC_OVER, 0.25F));

        graphics_.setColor(document_.getColor("glyph.selection"));
        graphics_.fill(document_.getCanvas().getSelectionBox());

        AlphaComposite composite = (AlphaComposite) graphics_.getComposite();
        graphics_.setComposite(composite.derive(1.0F));
        graphics_.draw(document_.getCanvas().getSelectionBox());

        graphics_.setComposite(originalComposite);
    }

    // FIXME complete method
    private static void renderSubmap(Submap submap) {
        graphics_.setColor(submap.getBackgroundColor());
        graphics_.fill(submap.getShape());
        graphics_.setColor(submap.getForegroundColor());
        graphics_.draw(submap.getShape());

        renderLabel(submap);

        if (submap.getTerminals() != null && !submap.getTerminals().isEmpty())
            for (Terminal terminal : submap.getTerminals())
                renderTagTerminal(terminal);


        if (submap.isSelected()) {

            Composite originalComposite = graphics_.getComposite();
            graphics_.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, 1.0F));

            Stroke originalStroke = graphics_.getStroke();
            graphics_.setStroke(new BasicStroke(1.0F));

            graphics_.setColor(document_.getColor("glyph.selection"));
            graphics_.draw(submap.getShape());

            graphics_.setComposite(originalComposite);
            graphics_.setStroke(originalStroke);
        }
    }

    private static void renderTagTerminal(AbstractTagTerminal tagTerminal) {
        graphics_.setColor(tagTerminal.getBackgroundColor());
        graphics_.fill(tagTerminal.getShape());
        graphics_.setColor(tagTerminal.getForegroundColor());
        graphics_.draw(tagTerminal.getShape());

        renderLabel(tagTerminal);


        if (tagTerminal.isSelected()) {

            Composite originalComposite = graphics_.getComposite();
            graphics_.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, 1.0F));

            Stroke originalStroke = graphics_.getStroke();
            graphics_.setStroke(new BasicStroke(1.0F));

            graphics_.setColor(document_.getColor("glyph.selection"));
            graphics_.draw(tagTerminal.getShape());

            graphics_.setComposite(originalComposite);
            graphics_.setStroke(originalStroke);

            renderBounds(tagTerminal);
        }
    }

    private static void renderCompartment(Compartment compartment) {
        graphics_.setColor(compartment.getBackgroundColor());

        if (compartment.isTransparent()) {
            Composite originalComposite = graphics_.getComposite();
            graphics_.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0f));
            graphics_.fill(compartment.getShape());
            graphics_.setComposite(originalComposite);
        } else {
            graphics_.fill(compartment.getShape());
        }

        graphics_.setColor(compartment.getForegroundColor());

        if (compartment.getCompartmentUnit() != null)
            compartment.updateShapeCoordinates(compartment.getCompartmentUnit().getLabel());

        graphics_.draw(compartment.getShape());

        renderLabel(compartment);
        renderAuxiliaryUnit(compartment);

        if (compartment.isSelected()) {

            Composite originalComposite = graphics_.getComposite();
            graphics_.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, 1.0F));

            Stroke originalStroke = graphics_.getStroke();
            graphics_.setStroke(new BasicStroke(1.0F));

            graphics_.setColor(document_.getColor("glyph.selection"));
            graphics_.draw(compartment.getShape());

            graphics_.setComposite(originalComposite);
            graphics_.setStroke(originalStroke);
        }
    }
}