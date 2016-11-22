package edu.vt.beacon.io;

import edu.vt.beacon.editor.action.handler.ExportType;
import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.io.backwardcompatibility.OldVersionConverter;
import edu.vt.beacon.io.backwardcompatibility.OldVersionParser;
import edu.vt.beacon.pathway.Pathway;
import org.sbgn.SbgnUtil;
import org.sbgn.bindings.Sbgn;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by ppws on 2/21/16.
 */
public class FileManager {

    public static boolean save(Pathway pathway, String filename) {
        if (pathway == null || filename == null || filename.trim().isEmpty())
            return false;

        Sbgn sbgn = Converter.convert(pathway);

        File file = new File(filename);

        try {

            SbgnUtil.writeToFile(sbgn, file);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Pathway load(String filename) {
        if (filename == null || filename.trim().isEmpty())
            return null;

        File file = new File(filename);

        try {

            return Converter.convert(SbgnUtil.readFromFile(file));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Pathway backwardCompatibilityImport(String filename) {
        if (filename == null || filename.trim().isEmpty())
            return null;

        try {

            OldVersionParser parser = new OldVersionParser(filename);
            return new OldVersionConverter().convert(parser);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean export(Document document, String filename, ExportType exportType) {
        if (document == null || document.getPathway() == null || filename == null || filename.trim().isEmpty()
                || exportType == null)
            return false;

        File file = new File(filename + "." + exportType.name());
        float zoomFactor = document.getCanvas().getZoomFactor();

        // create the buffered image
        BufferedImage bi = new BufferedImage(
                (int) Math.ceil(document.getContextManager().getBoundaryContext().getActiveBoundary().getWidth() * zoomFactor) + 100,
                (int) Math.ceil(document.getContextManager().getBoundaryContext().getActiveBoundary().getHeight() * zoomFactor) + 100,
                BufferedImage.TYPE_INT_RGB);

        // paint the canvas graphics onto the buffered image
        Graphics g = bi.createGraphics();
        document.getCanvas().paint(g);
        g.dispose();

        try {

            // write the buffered image
            ImageIO.write(bi, exportType.name(), file);
            return true;

        } catch (Exception ex) {

            ex.printStackTrace();
            return false;

        }

    }

}
