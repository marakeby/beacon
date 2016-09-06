package edu.vt.beacon.editor.util;

import edu.vt.beacon.graph.glyph.AbstractGlyph;
import edu.vt.beacon.util.IdGenerator;

import java.util.ArrayList;

public class ClipBoardManager {

    private static ArrayList<AbstractGlyph> copiedGlyphs = new ArrayList<AbstractGlyph>();
    private static final float PASTE_OFFSET = 50;
    private static int numberOfPaste = 0;

    public static void copy(ArrayList<AbstractGlyph> selectedGlyphs) {

        if (selectedGlyphs == null || selectedGlyphs.isEmpty())
            return;

        numberOfPaste = 0;
        copiedGlyphs.clear();

        for (AbstractGlyph glyph : selectedGlyphs)
            copiedGlyphs.add(glyph.copy());

    }


    public static ArrayList<AbstractGlyph> getGlyphs() {

        numberOfPaste++;
        ArrayList<AbstractGlyph> results = new ArrayList<AbstractGlyph>(copiedGlyphs.size());

        for (AbstractGlyph glyph : copiedGlyphs) {

            AbstractGlyph pastedGlyph = glyph.copy();
            pastedGlyph.setId(IdGenerator.generate());
            pastedGlyph.move(PASTE_OFFSET * numberOfPaste, PASTE_OFFSET * numberOfPaste);
            results.add(pastedGlyph);

        }

        return results;

    }

}