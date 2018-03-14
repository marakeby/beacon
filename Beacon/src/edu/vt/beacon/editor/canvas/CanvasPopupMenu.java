package edu.vt.beacon.editor.canvas;

import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.document.DocumentState;
import edu.vt.beacon.graph.Orientable;
import edu.vt.beacon.graph.OrientationType;
import edu.vt.beacon.graph.glyph.AbstractGlyph;
import edu.vt.beacon.graph.glyph.GlyphType;
import edu.vt.beacon.graph.glyph.arc.AbstractArc;
import edu.vt.beacon.graph.glyph.node.AbstractNode;
import edu.vt.beacon.graph.glyph.node.activity.AbstractActivity;
import edu.vt.beacon.graph.glyph.node.activity.BiologicalActivity;
import edu.vt.beacon.graph.glyph.node.activity.Phenotype;
import edu.vt.beacon.graph.glyph.node.annotation.Annotation;
import edu.vt.beacon.graph.glyph.node.auxiliary.*;
import edu.vt.beacon.graph.glyph.node.container.Compartment;
import edu.vt.beacon.graph.glyph.node.submap.AbstractTagTerminal;
import edu.vt.beacon.graph.glyph.node.submap.Submap;
import edu.vt.beacon.graph.glyph.node.submap.Terminal;
import edu.vt.beacon.layer.Layer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Created by ppws on 2/10/16.
 */
public class CanvasPopupMenu extends JPopupMenu {

    private Document document;

    private Point2D.Float currentLocation;

    public CanvasPopupMenu(final Document document, Point2D.Float currentLocation) {

        this.document = document;
        this.currentLocation = currentLocation;

        populateCreatePortMenuItem();
        populateRemovingPortMenuItem();
        populateAddBendingPointMenuItem();
        populateRemoveBendingPointMenuItem();
        populateTransparentMenuItem();
        populateCreatingEditingUnitofInformationMenuItem();
        populateDeletingMenuItem();
        populateAnnotationMenuItem();
        populateTerminalMenuItem();
        populateRotate();
    }

    /**
     * Change the orientation of the glyph.
     */
    private void populateRotate() {
        ArrayList<AbstractGlyph> selectedGlyphs = document.getBrowserMenu().getSelectedMap().getSelectedGlyphs();
        if (selectedGlyphs == null || selectedGlyphs.isEmpty() || selectedGlyphs.size() > 1) {
            return;
        }
        AbstractGlyph selectedGlyph = selectedGlyphs.get(0);
        if (selectedGlyph.getType() != GlyphType.AND && selectedGlyph.getType() != GlyphType.NOT &&
                selectedGlyph.getType() != GlyphType.OR && selectedGlyph.getType() != GlyphType.DELAY) {
            return;
        }
        JMenuItem rotateMenuItem = new JMenuItem("Rotate");
        add(rotateMenuItem);
        rotateMenuItem.addActionListener(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                AbstractNode node = (AbstractNode) selectedGlyph;
                if (node.getOrientation().isHorizontal()) {
                    node.setOrientation(OrientationType.VERTICAL);
                }
                else {
                    node.setOrientation(OrientationType.HORIZONTAL);
                }
                document.getCanvas().repaint();
            }
        });
    }

    private void populateTransparentMenuItem() {
        ArrayList<AbstractGlyph> selectedGlyphs = document.getBrowserMenu().getSelectedMap().getSelectedGlyphs();

        if (selectedGlyphs == null || selectedGlyphs.isEmpty() || selectedGlyphs.size() > 1)
            return;

        AbstractGlyph selectedGlyph = selectedGlyphs.get(0);
        if (!(selectedGlyph instanceof Compartment))
            return;

        final Compartment selectedCompartment = (Compartment) selectedGlyph;

        JMenuItem transparentMenuItem = new JMenuItem();
        if (selectedCompartment.isTransparent())
            transparentMenuItem.setText("Opaque");
        else
            transparentMenuItem.setText("Transparent");

        add(transparentMenuItem);

        transparentMenuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                selectedCompartment.setTransparent(!selectedCompartment.isTransparent());

                document.getCanvas().updateUI();
            }
        });
    }

    private void populateCreatingEditingUnitofInformationMenuItem() {

        final ArrayList<AbstractGlyph> selectedGlyphs = document.getBrowserMenu().getSelectedMap().getSelectedGlyphs();

        boolean enabled = true;
        if (selectedGlyphs == null || selectedGlyphs.isEmpty())
            return;
        if (selectedGlyphs.size() == 1 && selectedGlyphs.get(0) instanceof AbstractArc)
            return;
        if (selectedGlyphs.size() > 1)
            enabled = false;
        else if (!(selectedGlyphs.get(0) instanceof BiologicalActivity || selectedGlyphs.get(0) instanceof Compartment))
            enabled = false;

        JMenuItem createMenuItem = new JMenuItem("Create/Edit Unit of Information");
        createMenuItem.setEnabled(enabled);

        add(createMenuItem);

        createMenuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (selectedGlyphs.get(0) instanceof BiologicalActivity) {

                    BiologicalActivity activity = (BiologicalActivity) selectedGlyphs.get(0);

                    AnnotationAuxiliaryUnitDialog dialog = new AnnotationAuxiliaryUnitDialog(document.getFrame(),
                            "Create/Edit Unit of Information", "Label", "Type", new String[]{
                            GlyphType.MACROMOLECULE_UNIT.toString(), GlyphType.NUCLEIC_ACID_FEATURE_UNIT.toString(),
                            GlyphType.SIMPLE_CHEMICAL_UNIT.toString(), GlyphType.UNSPECIFIED_ENTITY_UNIT.toString(),
                            GlyphType.COMPLEX_UNIT.toString(), GlyphType.PERTURBATION_UNIT.toString()});

                    if (activity.getAuxiliaryUnit() != null) {
                        dialog.setSelectionValue(activity.getAuxiliaryUnit().getType().toString());
                        dialog.setTextValue(activity.getAuxiliaryUnit().getText());
                    }

                    dialog.display();

                    activity.setAuxiliaryUnit(createAuxiliaryUnit(dialog.getTextValue(), dialog.getSelectionValue()));

                } else if (selectedGlyphs.get(0) instanceof Compartment) {

                    Compartment compartment = (Compartment) selectedGlyphs.get(0);

                    AnnotationAuxiliaryUnitDialog dialog = new AnnotationAuxiliaryUnitDialog(document.getFrame(),
                            "Create/Edit Unit of Information", "Label", null, null);

                    if (compartment.getCompartmentUnit() != null) {
                        dialog.setTextValue(compartment.getCompartmentUnit().getText());
                    }

                    dialog.display();

                    CompartmentUnit compartmentUnit = new CompartmentUnit();
                    compartmentUnit.setText(dialog.getTextValue());
                    compartment.setCompartmentUnit(compartmentUnit);
                }

                document.getCanvas().updateUI();
            }
        });

    }

    private AuxiliaryUnit createAuxiliaryUnit(String label, String type) {
        if (type == null || type.isEmpty())
            return null;

        AuxiliaryUnit auxUnit = null;

        if (type.equalsIgnoreCase(GlyphType.MACROMOLECULE_UNIT.toString()))
            auxUnit = new MacromoleculeUnit();
        else if (type.equalsIgnoreCase(GlyphType.NUCLEIC_ACID_FEATURE_UNIT.toString()))
            auxUnit = new NucleicAcidFeatureUnit();
        else if (type.equalsIgnoreCase(GlyphType.SIMPLE_CHEMICAL_UNIT.toString()))
            auxUnit = new SimpleChemicalUnit();
        else if (type.equalsIgnoreCase(GlyphType.UNSPECIFIED_ENTITY_UNIT.toString()))
            auxUnit = new UnspecifiedEntityUnit();
        else if (type.equalsIgnoreCase(GlyphType.COMPLEX_UNIT.toString()))
            auxUnit = new ComplexUnit();
        else if (type.equalsIgnoreCase(GlyphType.PERTURBATION_UNIT.toString()))
            auxUnit = new PerturbationUnit();

        auxUnit.setText(label);
        return auxUnit;
    }

    private boolean auxiliaryUnitsExist(ArrayList<AbstractGlyph> selectedGlyphs) {
        if (selectedGlyphs == null || selectedGlyphs.isEmpty())
            return false;

        for (AbstractGlyph glyph : selectedGlyphs) {

            if (glyph instanceof BiologicalActivity) {
                if (((BiologicalActivity) glyph).getAuxiliaryUnit() != null)
                    return true;

            } else if (glyph instanceof Compartment) {
                if (((Compartment) glyph).getCompartmentUnit() != null)
                    return true;
            }

        }

        return false;
    }

    private void populateDeletingMenuItem() {

        JMenuItem deleteMenuItem = new JMenuItem("Delete");
        deleteMenuItem.setEnabled(auxiliaryUnitsExist(document.getBrowserMenu().getSelectedMap().getSelectedGlyphs()));

        add(deleteMenuItem);

        deleteMenuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                for (AbstractGlyph glyph : document.getBrowserMenu().getSelectedMap().getSelectedGlyphs()) {

                    if (glyph instanceof BiologicalActivity) {

                        BiologicalActivity activity = (BiologicalActivity) glyph;
                        if (activity.getAuxiliaryUnit() != null)
                            activity.setAuxiliaryUnit(null);

                    } else if (glyph instanceof Compartment) {

                        Compartment compartment = (Compartment) glyph;
                        if (compartment.getCompartmentUnit() != null)
                            compartment.setCompartmentUnit(null);
                    }

                }

                document.getCanvas().updateUI();
            }
        });

    }

    private void populateAddBendingPointMenuItem() {

        final ArrayList<AbstractGlyph> selectedGlyphs = document.getBrowserMenu().getSelectedMap().getSelectedGlyphs();

        boolean enabled = true;
        if (selectedGlyphs == null || selectedGlyphs.isEmpty())
            return;
        if (selectedGlyphs.size() == 1 && !(selectedGlyphs.get(0) instanceof AbstractArc))
            return;
        if (selectedGlyphs.size() > 1)
            enabled = false;

        JMenuItem addPointMenuItem = new JMenuItem("Add Point");
        addPointMenuItem.setEnabled(enabled);

        add(addPointMenuItem);

        addPointMenuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                AbstractArc selectedArc = (AbstractArc) selectedGlyphs.get(0);
                selectedArc.addPoint(new Point2D.Float(currentLocation.x, currentLocation.y));
                document.getCanvas().repaint();

            }
        });

    }

    private void populateRemoveBendingPointMenuItem() {

        final ArrayList<AbstractGlyph> selectedGlyphs = document.getBrowserMenu().getSelectedMap().getSelectedGlyphs();

        boolean enabled = true;
        if (selectedGlyphs == null || selectedGlyphs.isEmpty())
            return;
        if (selectedGlyphs.size() == 1 && !(selectedGlyphs.get(0) instanceof AbstractArc))
            return;
        if (selectedGlyphs.size() > 1)
            enabled = false;

        final Point2D.Float selectedPoint = getSelectedPoint((AbstractArc) selectedGlyphs.get(0));
        if (selectedPoint == null)
            return;

        JMenuItem addPointMenuItem = new JMenuItem("Remove Point");
        addPointMenuItem.setEnabled(enabled);

        add(addPointMenuItem);

        addPointMenuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                AbstractArc selectedArc = (AbstractArc) selectedGlyphs.get(0);
                selectedArc.removePoint(selectedPoint);
                document.getCanvas().repaint();

            }
        });

    }

    private Point2D.Float getSelectedPoint(AbstractArc selectedArc) {
        if (selectedArc == null || !selectedArc.isSelected())
            return null;

        for (int i = 1; i < (selectedArc.getPoints().size()); i++) {
            Point2D.Float point = selectedArc.getPoints().get(i);
            if (point.distance(currentLocation) <= selectedArc.getLineWidth())
                return point;
        }

        return null;
    }

    private void populateTerminalMenuItem() {

        final ArrayList<AbstractGlyph> selectedGlyphs = document.getBrowserMenu().getSelectedMap().getSelectedGlyphs();

        if (selectedGlyphs == null || selectedGlyphs.isEmpty())
            return;
        if (selectedGlyphs.size() == 1 && !(selectedGlyphs.get(0) instanceof Submap))
            return;
        if (selectedGlyphs.size() > 1)
            return;
        if (!isOnBoundaries(currentLocation, selectedGlyphs))
            return;

        JMenuItem createTerminalMenuItem = new JMenuItem("Create Terminal");

        add(createTerminalMenuItem);

        createTerminalMenuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                createTerminal((Submap) selectedGlyphs.get(0));

            }
        });

    }

    private void createTerminal(Submap submap) {
        if (submap == null)
            return;

        Terminal terminal = new Terminal(submap);
        initializeGlyph(terminal);

//        System.out.println("currentLocation.x " + currentLocation.x + "currentLocation.y "+ currentLocation.y);
        if (Math.abs(currentLocation.x - submap.getMinX()) < submap.getLineWidth()) {

            terminal.setOrientation(OrientationType.RIGHT);
            terminal.moveIntoSubmap(submap.getMinX(), currentLocation.y - terminal.getHeight() / 2);

        } else if (Math.abs(currentLocation.y - submap.getMinY()) < submap.getLineWidth()) {

            terminal.setOrientation(OrientationType.DOWN);
            terminal.moveIntoSubmap(currentLocation.x - terminal.getWidth() / 2, submap.getMinY());

        } else if (Math.abs(currentLocation.x - submap.getMaxX()) < submap.getLineWidth()) {

            terminal.setOrientation(OrientationType.LEFT);
            terminal.moveIntoSubmap(submap.getMaxX() - terminal.getWidth(), currentLocation.y - terminal.getHeight() / 2);

        } else if (Math.abs(currentLocation.y - submap.getMaxY()) < submap.getLineWidth()) {

            terminal.setOrientation(OrientationType.UP);
            float x = currentLocation.x - terminal.getWidth() / 2;
            float y = submap.getMaxY() - terminal.getHeight();
//            System.out.println("x = "+ x + "y= "+ y);
            terminal.moveIntoSubmap(x,y );


        }
        else{

            terminal.setOrientation(OrientationType.UP);
            float x = submap.getMaxX() - terminal.getWidth() ;
            float y = submap.getMaxY() - terminal.getHeight();
//            System.out.println("else, x = "+ x + "y= "+ y);
            terminal.moveIntoSubmap(x,y );
        }

        submap.createTag(terminal);
        document.getCanvas().repaint();
    }

    private void populateCreatePortMenuItem() {

        final ArrayList<AbstractGlyph> selectedGlyphs = document.getBrowserMenu().getSelectedMap().getSelectedGlyphs();

        if (selectedGlyphs == null || selectedGlyphs.isEmpty())
            return;
        if (selectedGlyphs.size() == 1 && !(selectedGlyphs.get(0) instanceof BiologicalActivity ||
                selectedGlyphs.get(0) instanceof Phenotype))
            return;
        if (selectedGlyphs.size() > 1)
            return;
        if (!isOnBoundaries(currentLocation, selectedGlyphs))
            return;

        JMenuItem createPortMenuItem = new JMenuItem("Create Port");

        add(createPortMenuItem);

        createPortMenuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                AbstractActivity selectedActivity = (AbstractActivity) selectedGlyphs.get(0);
                selectedActivity.addPort(currentLocation);
                document.getCanvas().repaint();

            }
        });

    }

    private void populateRemovingPortMenuItem() {

        final ArrayList<AbstractGlyph> selectedGlyphs = document.getBrowserMenu().getSelectedMap().getSelectedGlyphs();

        if (selectedGlyphs == null || selectedGlyphs.isEmpty())
            return;
        if (selectedGlyphs.size() == 1 && !(selectedGlyphs.get(0) instanceof BiologicalActivity ||
                selectedGlyphs.get(0) instanceof Phenotype))
            return;
        if (selectedGlyphs.size() > 1)
            return;

        if (((AbstractNode) selectedGlyphs.get(0)).getPortCount() <= 4)
            return;

        if (!isOnPorts(currentLocation, selectedGlyphs))
            return;

        JMenuItem removePortMenuItem = new JMenuItem("Remove Port");

        add(removePortMenuItem);

        removePortMenuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                AbstractActivity selectedActivity = (AbstractActivity) selectedGlyphs.get(0);
                selectedActivity.removePort(currentLocation);
                document.getCanvas().repaint();

            }
        });

    }

    private boolean isOnBoundaries(Point2D.Float point, ArrayList<AbstractGlyph> glyphs) {
        if (point == null || glyphs == null || glyphs.isEmpty())
            return false;

        for (AbstractGlyph glyph : glyphs) {

            float lineWidth = glyph.getLineWidth();

            if (glyph.getShape().contains(point.x, point.y)) {

                for (int xStep = -1; xStep <= 1; xStep++)
                    for (int yStep = -1; yStep <= 1; yStep++)
                        if (!glyph.getShape().contains(point.x + xStep * lineWidth, point.y + yStep * lineWidth))
                            return true;

                return false;

            } else {

                for (int xStep = -1; xStep <= 1; xStep++)
                    for (int yStep = -1; yStep <= 1; yStep++)
                        if (glyph.getShape().contains(point.x + xStep * lineWidth, point.y + yStep * lineWidth))
                            return true;

                return false;
            }
        }

        return false;
    }

    private boolean isOnPorts(Point2D.Float point, ArrayList<AbstractGlyph> glyphs) {
        if (point == null || glyphs == null || glyphs.isEmpty())
            return false;

        for (AbstractGlyph glyph : glyphs) {

            if (!(glyph instanceof AbstractNode))
                continue;

            AbstractNode node = (AbstractNode) glyph;
            if (node.getPorts() == null || node.getPorts().isEmpty())
                continue;

            for (int i = 4; i < node.getPortCount(); i++)
                if (point.distance(node.getPortAt(i).getCenterX(), node.getPortAt(i).getCenterY()) <= node.getLineWidth())
                    return true;
        }

        return false;
    }

    private void populateAnnotationMenuItem() {

        JMenuItem annotationMenuItem = new JMenuItem("Create/Edit Annotation");
        final ArrayList<AbstractGlyph> selectedGlyphs = document.getBrowserMenu().getSelectedMap().getSelectedGlyphs();
        annotationMenuItem.setEnabled(selectedGlyphs == null || selectedGlyphs.size() <= 1);

        add(annotationMenuItem);

        annotationMenuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String defaultText = "";

                if (selectedGlyphs != null && selectedGlyphs.size() == 1) {
                    AbstractGlyph selectedGlyph = selectedGlyphs.get(0);
                    if (selectedGlyph.getAnnotation() != null)
                        defaultText = selectedGlyph.getAnnotation().getText();
                }

                String annotationText = JOptionPane.showInputDialog(null, "Enter annotation:", defaultText);

                if (annotationText != null) {

                    Annotation annotation = null;

                    if (selectedGlyphs != null && selectedGlyphs.size() == 1 && selectedGlyphs.get(0).getAnnotation() != null)
                        annotation = selectedGlyphs.get(0).getAnnotation();
                    else
                        annotation = new Annotation();

                    initializeGlyph(annotation);
                    annotation.setText(annotationText);

                    if (selectedGlyphs != null && selectedGlyphs.size() == 1) {

                        annotation.setTarget(selectedGlyphs.get(0));
                        selectedGlyphs.get(0).getLayer().add(annotation);
                        Point2D.Float center = new Point2D.Float(currentLocation.x - annotation.offset,
                                currentLocation.y - annotation.offset);
                        annotation.setCenter(center);

                    } else {

                        annotation.setTarget(null);
                        getActiveLayer().add(annotation);
                        annotation.setCenter(currentLocation);

                    }

                    annotation.update();
                    new DocumentState(document, "Annotation", false);

                    document.getCanvas().updateUI();
                }

            }
        });

    }

    private void initializeGlyph(AbstractGlyph glyph) {
        glyph.setBackgroundColor(document.getColor("glyph.background"));
        glyph.setForegroundColor(document.getColor("glyph.foreground"));
        glyph.setHypothetical(document.getBoolean("glyph.hypothetical"));
        glyph.setLineWidth(document.getFloat("glyph.lineWidth"));

        if (glyph instanceof AbstractTagTerminal)
            glyph.setPadding(0);
        else
            glyph.setPadding(document.getFloat("glyph.padding"));


        if (!(glyph instanceof AbstractNode))
            return;

        AbstractNode node = (AbstractNode) glyph;
        node.setFontColor(document.getColor("font.color"));
        node.setFont(new Font(document.get("font.name"),
                document.getInteger("font.style"), 12).deriveFont(
                document.getFloat("font.size")));

        if (node instanceof Orientable)
            node.setOrientation(OrientationType.values()
                    [document.getInteger("glyph.orientation")]);
    }

    private Layer getActiveLayer() {
        for (Layer layer : document.getPathway().getMap().getLayers())
            if (layer.isSelected())
                return layer;

        return document.getPathway().getMap().getLayerAt(0);
    }

}
