package edu.vt.beacon.io;

import edu.vt.beacon.editor.action.handler.ExportType;
import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.io.backwardcompatibility.OldVersionConverter;
import edu.vt.beacon.io.backwardcompatibility.OldVersionParser;
import edu.vt.beacon.pathway.Pathway;
import edu.vt.beacon.sbml.IDOption;
import edu.vt.beacon.sbml.SbgnToSbmlConverterDebug;
import org.sbgn.SbgnUtil;
import org.sbgn.bindings.Sbgn;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.xml.stream.XMLStreamException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Properties;
import org.freehep.graphics2d.VectorGraphics;
import org.freehep.graphicsio.pdf.PDFGraphics2D;
import org.freehep.graphicsio.ps.PSGraphics2D;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLWriter;

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

    public static boolean export_vector(Document document, String filename, ExportType exportType) {
        if (document == null || document.getPathway() == null || filename == null || filename.trim().isEmpty()
                || exportType == null)
            return false;
        File file = new File(filename + "." + exportType.name());
        float zoomFactor = document.getCanvas().getZoomFactor();
        System.out.println(zoomFactor);


//
//
//        int h = (int) Math.ceil(document.getContextManager().getBoundaryContext().getActiveBoundary().getWidth() ) + 100;
//        int w = (int) Math.ceil(document.getContextManager().getBoundaryContext().getActiveBoundary().getHeight()) + 100;


//        File out = new File("YourPanel.pdf");
        VectorGraphics graphics = null;
        try {

            if(exportType == ExportType.pdf) {
                document.getCanvas().setZoomFactor(1f);
//                int w = (int) Math.ceil(document.getContextManager().getBoundaryContext().getActiveBoundary().getWidth() ) + 100;
//                int h = (int) Math.ceil(document.getContextManager().getBoundaryContext().getActiveBoundary().getHeight()) + 100;


                Properties p = new Properties();
                p.setProperty("PageSize", "A4");
//                graphics = new PDFGraphics2D(file, new Dimension(w, h));
                graphics = new PDFGraphics2D(file,  document.getCanvas());
                graphics.setProperties(p);

                graphics.startExport();
                document.getCanvas().print(graphics);
                graphics.endExport();
                document.getCanvas().setZoomFactor(zoomFactor);

            }
            else if(exportType == ExportType.eps)
            {
//                int h = (int) Math.ceil(document.getContextManager().getBoundaryContext().getActiveBoundary().getWidth() ) + 100;
//                int w = (int) Math.ceil(document.getContextManager().getBoundaryContext().getActiveBoundary().getHeight() ) + 100;


                Properties p = new Properties();
                p.setProperty("PageSize","A4");
//                graphics = new PSGraphics2D(file, new Dimension(w,h));
                graphics = new PSGraphics2D(file, document.getCanvas());
                graphics.setProperties(p);
                graphics.startExport();
                document.getCanvas().print(graphics);
                graphics.endExport();

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }




        return true;

    }

    public static boolean export_sbml(Document document, String filename, ExportType exportType){
        int result = JOptionPane.showConfirmDialog(
                document.getCanvas(), "Are you sure you want to export as SBML?",
                "Confirm Export",
                0, 2);
        if (result != 0) {
            return false;
        }

        if(exportType == ExportType.sbml)
        {
            SbgnToSbmlConverterDebug conveter = new SbgnToSbmlConverterDebug(document, IDOption.Id);

            SBMLDocument doc = conveter.getSbml();

            SBMLWriter w = new SBMLWriter();

            try {
                w.writeSBMLToFile(doc,filename + "." + exportType.name() );
            } catch (FileNotFoundException | XMLStreamException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(document.getCanvas(), "Export Fail");
                return false;
            }

        }
        JOptionPane.showMessageDialog(document.getCanvas(), "Export Successful");
        return true;
    }

    public static boolean export(Document document, String filename, ExportType exportType) {
        if (document == null || document.getPathway() == null || filename == null || filename.trim().isEmpty()
                || exportType == null)
            return false;

        if (exportType == ExportType.pdf || exportType == ExportType.eps)
            return export_vector(document, filename, exportType);

        if (exportType == ExportType.sbml) {
            return export_sbml(document, filename, exportType);
        }

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
