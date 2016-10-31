package edu.vt.beacon.editor.action.handler;

/**
 * Created by ppws on 3/9/16.
 */
public enum ExportType {

    bmp          ("BMP  (*.bmp)"),
    gif          ("GIF  (*.gif)"),
    jpeg         ("JPEG (*.jpeg)"),
    png          ("PNG  (*.png)");

    private String text;

    // FIXME complete constructor
    private ExportType(String text)
    {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }
}
