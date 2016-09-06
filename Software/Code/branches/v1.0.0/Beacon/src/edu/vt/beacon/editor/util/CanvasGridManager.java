package edu.vt.beacon.editor.util;

/**
 * Created by ppws on 4/19/16.
 */
public class CanvasGridManager {

    private static boolean enabled = false;
    private static float gridSize = 20;
    private static int chunk = 5;

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean enabled) {
        CanvasGridManager.enabled = enabled;
    }

    public static float getGridSize() {
        return gridSize;
    }

    public static void setGridSize(float gridSize) {
        CanvasGridManager.gridSize = gridSize;
    }

    public static int getChunk() {
        return chunk;
    }

    public static void setChunk(int chunk) {
        CanvasGridManager.chunk = chunk;
    }

}
