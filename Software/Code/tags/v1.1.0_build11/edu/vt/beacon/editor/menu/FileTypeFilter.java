package edu.vt.beacon.editor.menu;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * Created by mostafa on 3/8/16.
 */
public class FileTypeFilter extends FileFilter {

    String description = "";
    String fileExt = "";

    public FileTypeFilter(String extension) {
        fileExt = extension;
    }

    public FileTypeFilter(String extension, String typeDescription) {
        fileExt = extension;
        this.description = typeDescription;
    }

    @Override
    public boolean accept(File f) {
        if (f.isDirectory())
            return true;
        return (f.getName().toLowerCase().endsWith(fileExt));
    }

    @Override
    public String getDescription() {
        return description;
    }
}
