package edu.vt.beacon.editor.context;

import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.graph.glyph.AbstractGlyph;
import edu.vt.beacon.graph.glyph.node.annotation.Annotation;
import edu.vt.beacon.graph.glyph.node.submap.Submap;
import edu.vt.beacon.graph.glyph.node.submap.Terminal;

public class StatisticsContext extends AbstractContext {
    private int nActiveGlyphs_;

    private int nGlyphs_;

    private int nSelectedGlyphs_;

    // TODO document constructor
    public StatisticsContext(Document document) {
        super(document);
    }

    // TODO document method
    public int getActiveGlyphCount() {
        return nActiveGlyphs_;
    }

    // TODO document method
    public int getGlyphCount() {
        return nGlyphs_;
    }

    // TODO document method
    public int getSelectedGlyphCount() {
        return nSelectedGlyphs_;
    }

    // FIXME complete method
    @Override
    public void process(AbstractGlyph glyph, float zoomFactor) {
        nGlyphs_++;

        if (glyph.getLayer().isActive()) {

            nActiveGlyphs_++;

            if (glyph.isSelected())
                nSelectedGlyphs_++;

            if (glyph instanceof Annotation) {

                Annotation annotation = (Annotation) glyph;
                if (annotation.getCalloutPoint() != null && annotation.getCalloutPoint().isSelected())
                    nSelectedGlyphs_++;

            } else if (glyph instanceof Submap && ((Submap) glyph).getTerminals() != null) {

                for (Terminal terminal : ((Submap) glyph).getTerminals())
                    if (terminal.isSelected())
                        nSelectedGlyphs_++;

            }

        }
    }

    // FIXME complete method
    @Override
    public void reset() {
        nActiveGlyphs_ = 0;
        nGlyphs_ = 0;
        nSelectedGlyphs_ = 0;
    }
}