package edu.vt.beacon.graph.glyph.node.activity;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import edu.vt.beacon.editor.gene.Gene;
import edu.vt.beacon.graph.glyph.GlyphType;
import edu.vt.beacon.graph.glyph.node.AbstractNode;
import edu.vt.beacon.graph.glyph.node.auxiliary.Port;
import edu.vt.beacon.graph.glyph.node.auxiliary.PortType;

public abstract class AbstractActivity extends AbstractNode {
    private ArrayList<Integer> correspondingLinesForArbitraryPorts;
    private ArrayList<Float> correspondingFractionsForArbitraryPorts;

    // List of Genes
    private List<Gene> genes = new ArrayList<Gene>();

    // FIXME complete constructor
    protected AbstractActivity(GlyphType type) {
        super(type);

        correspondingLinesForArbitraryPorts = new ArrayList<Integer>();
        correspondingFractionsForArbitraryPorts = new ArrayList<Float>();
    }

    // TODO document method
    @Override
    protected void initializePorts() {
        ports_ = new ArrayList<Port>();
        ports_.add(new Port(this, PortType.TOP));
        ports_.add(new Port(this, PortType.LEFT));
        ports_.add(new Port(this, PortType.BOTTOM));
        ports_.add(new Port(this, PortType.RIGHT));
    }

    // TODO document method
    @Override
    protected void setPortCoordinates() {
        ports_.get(0).setCenter(getCenterX(), getMinY());
        ports_.get(1).setCenter(getMinX(), getCenterY());
        ports_.get(2).setCenter(getCenterX(), getMaxY());
        ports_.get(3).setCenter(getMaxX(), getCenterY());
    }

    public List<Gene> getGenes() {
        return genes;
    }

    public void setGenes(List<Gene> genes) {
        this.genes = genes;
    }

    public ArrayList<Float> getCorrespondingFractionsForArbitraryPorts() {
        return correspondingFractionsForArbitraryPorts;
    }

    public ArrayList<Integer> getCorrespondingLinesForArbitraryPorts() {
        return correspondingLinesForArbitraryPorts;
    }

    public int getGenesCount() {
        if (genes.isEmpty())
            return 0;
        else
            return genes.size();
    }

    public Gene getGeneAt(int a) {
        return genes.get(a);
    }

    public void addGene(Gene gene) {
        genes.add(gene);
        return;
    }

    public void addGene(String id, String name, String pubMedId, String annotation) {
        Gene gene = new Gene(id, name, pubMedId, annotation);
        genes.add(gene);
        return;
    }

    public void removeGene(int a) {
        genes.remove(a);
        return;
    }

    public void clear() {
        genes.clear();
        return;
    }

    public String getGeneIdAt(int a) {
        return genes.get(a).getId();
    }

    public String getGeneNameAt(int a) {
        return genes.get(a).getName();
    }

    public String getGeneDescriptionAt(int a) {
        return genes.get(a).getDescription();
    }

    public boolean addPort(Point2D.Float location) {
        if (location == null || !isOnBoundary(location))
            return false;

        Point2D.Float portLocation = findBestLocation(location);
        Port port = new Port(this, PortType.ARBITRARY);
        port.setCenter(portLocation.x, portLocation.y);

        ports_.add(port);
        update();
        return true;
    }

    public boolean removePort(Point2D.Float location) {
        if (location == null || !isOnBoundary(location))
            return false;

        Port port = findPort(location);
        if (port == null)
            return false;

        int portIndex = ports_.indexOf(port);

        ports_.remove(portIndex);
        getCorrespondingLinesForArbitraryPorts().remove(portIndex - 4);
        getCorrespondingFractionsForArbitraryPorts().remove(portIndex - 4);
        update();
        return true;
    }

    public Port findPort(Point2D.Float location) {
        if (location == null || ports_ == null || getPortCount() <= 4)
            return null;

        for (int i = 4; i < getPortCount(); i++)
            if (location.distance(getPortAt(i).getCenterX(), getPortAt(i).getCenterY()) <= getLineWidth())
                return getPortAt(i);

        return null;
    }

    protected abstract ArrayList<Line2D.Float> createLinesBoundary();

    protected void updatePorts() {
        if (ports_ == null || ports_.size() <= 4)
            return;

        ArrayList<Line2D.Float> lines = createLinesBoundary();
        for (int i = 4; i < ports_.size(); i++) {
            Port port = ports_.get(i);
            Line2D.Float line = lines.get(getCorrespondingLinesForArbitraryPorts().get(i - 4));
            float fraction = getCorrespondingFractionsForArbitraryPorts().get(i - 4);
            port.setCenter(fraction * (line.x2 - line.x1) + line.x1, fraction * (line.y2 - line.y1) + line.y1);
        }
    }

    protected Point2D.Float findBestLocation(Point2D.Float location) {
        if (location == null)
            return null;

        ArrayList<Line2D.Float> lines = createLinesBoundary();
        Integer lineIndex = getNearestLine(location, lines);

        if (lineIndex != null) {
            getCorrespondingLinesForArbitraryPorts().add(lineIndex);
            return projectPointToLine(location, lines.get(lineIndex));
        }

        return null;
    }

    private Point2D.Float projectPointToLine(Point2D.Float location, Line2D.Float line) {
        if (location == null || line == null)
            return null;

        Point2D.Float b = new Point2D.Float(line.x2 - line.x1, line.y2 - line.y1);
        Point2D.Float a = new Point2D.Float(location.x - line.x1, location.y - line.y1);

        float m = (a.x * b.x + a.y * b.y) / (b.x * b.x + b.y * b.y);
        Point2D.Float projectedPoint = new Point2D.Float(m * b.x, m * b.y);
        projectedPoint.x += line.x1;
        projectedPoint.y += line.y1;

        float lineLength = (float) Math.sqrt((b.x * b.x + b.y * b.y));
        float pointDistToStart = (float) Math.sqrt((a.x * a.x + a.y * a.y));
        getCorrespondingFractionsForArbitraryPorts().add(pointDistToStart / lineLength);


        return projectedPoint;
    }

    protected boolean isOnBoundary(Point2D.Float point) {
        if (point == null)
            return false;

        ArrayList<Line2D.Float> lines = createLinesBoundary();
        Integer lineIndex = getNearestLine(point, lines);

        if (lineIndex == null)
            return false;

        return lines.get(lineIndex).ptLineDist(point) <= lineWidth_;
    }

    private Integer getNearestLine(Point2D.Float point, ArrayList<Line2D.Float> lines) {
        if (point == null || lines == null || lines.isEmpty())
            return null;

        double minDist = Double.MAX_VALUE;
        Integer lineIndex = null;

        for (int i = 0; i < lines.size(); i++)
            if (lines.get(i).ptLineDist(point) < minDist) {
                minDist = lines.get(i).ptLineDist(point);
                lineIndex = i;
            }

        return lineIndex;
    }
}