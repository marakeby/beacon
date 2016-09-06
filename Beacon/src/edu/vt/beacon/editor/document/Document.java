package edu.vt.beacon.editor.document;

import edu.vt.beacon.editor.EditorApplication;
import edu.vt.beacon.editor.action.Action;
import edu.vt.beacon.editor.action.ActionType;
import edu.vt.beacon.editor.browser.BrowserMenuPanel;
import edu.vt.beacon.editor.canvas.CanvasPanel;
import edu.vt.beacon.editor.command.Command;
import edu.vt.beacon.editor.command.CommandType;
import edu.vt.beacon.editor.context.ContextManager;
import edu.vt.beacon.editor.frame.Frame;
import edu.vt.beacon.editor.layers.LayersMenuPanel;
import edu.vt.beacon.editor.menubar.MenuBar;
import edu.vt.beacon.editor.palette.PalettePanel;
import edu.vt.beacon.editor.util.LegendManager;
import edu.vt.beacon.editor.util.PlatformManager;
import edu.vt.beacon.editor.util.StateManager;
import edu.vt.beacon.graph.OrientationType;
import edu.vt.beacon.pathway.Pathway;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

public class Document extends HashMap<String, String> {
    private static final long serialVersionUID = 1L;

    private boolean isChanged_;

    private ContextManager contextManager_;

    private StateManager stateManager_;

    private LegendManager legendManager_;

    private DocumentState state_;

    private File file_;

    private HashMap<ActionType, Action> actionMap_;

    private HashMap<String, Component> componentMap_;

    private Pathway pathway_;


    // FIXME complete constructor
    public Document(File file) {
        contextManager_ = new ContextManager(this);
        stateManager_ = StateManager.getInstance(this);
        legendManager_ = new LegendManager(this);
        componentMap_ = new HashMap<String, Component>();
        file_ = file;
        pathway_ = new Pathway("untitled");

        stateManager_.insert(new Command(CommandType.CREATING_PATHWAY, pathway_.copy(), getCanvas().getZoomFactor(),
                new Date().getTime()));

        initializeActionMap();
        initializeProperties();
    }

    // TODO document method
    public Action getAction(ActionType type) {
        return actionMap_.get(type);
    }

    // TODO document method
    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    // TODO document method
    public BrowserMenuPanel getBrowserMenu() {
        if (componentMap_.get("browser") == null)
            componentMap_.put("browser", new BrowserMenuPanel(this));

        return (BrowserMenuPanel) componentMap_.get("browser");
    }

    // TODO document method
    public CanvasPanel getCanvas() {
        if (componentMap_.get("canvas") == null)
            componentMap_.put("canvas", new CanvasPanel(this));

        return (CanvasPanel) componentMap_.get("canvas");
    }

    // TODO document method
    public Color getColor(String key) {
        return new Color(getInteger(key));
    }

    // TODO document method
    public ContextManager getContextManager() {
        return contextManager_;
    }

    public StateManager getStateManager() {
        return stateManager_;
    }

    public LegendManager getLegendManager() {
        return legendManager_;
    }

    public void setLegendManager(LegendManager legendManager_) {
        this.legendManager_ = legendManager_;
    }

    // TODO document method
    public double getDouble(String key) {
        return Double.parseDouble(get(key));
    }

    // TODO document method
    public float getFloat(String key) {
        return Float.parseFloat(get(key));
    }

    // TODO document method
    public Frame getFrame() {
        if (componentMap_.get("frame") == null)
            componentMap_.put("frame", new Frame(this));

        return (Frame) componentMap_.get("frame");
    }

    // TODO document method
    public int getInteger(String key) {
        return Integer.parseInt(get(key));
    }

    // TODO document method
    public LayersMenuPanel getLayersMenu() {
        if (componentMap_.get("layers") == null)
            componentMap_.put("layers", new LayersMenuPanel(this));

        return (LayersMenuPanel) componentMap_.get("layers");
    }

    // TODO document method
    public MenuBar getMenuBar() {
        if (componentMap_.get("menuBar") == null)
            componentMap_.put("menuBar", new MenuBar(this));

        return (MenuBar) componentMap_.get("menuBar");
    }

    // TODO document method
    public PalettePanel getPalette() {
        if (componentMap_.get("palette") == null)
            componentMap_.put("palette", new PalettePanel(this));

        return (PalettePanel) componentMap_.get("palette");
    }

    // TODO document method
    public File getFile() {
        return file_;
    }

    // TODO document method
    public Pathway getPathway() {
        return pathway_;
    }

    // TODO document method
    public DocumentState getState() {
        return state_;
    }

    // TODO document method
    private void initializeActionMap() {
        actionMap_ = new HashMap<ActionType, Action>();

        for (ActionType type : ActionType.values())
            actionMap_.put(type, new Action(this, type));
    }

    // FIXME complete method
    private void initializeProperties() {

        try {

            readPropertiesFromConfigFile();

        } catch (Exception e) {

            setDefaults();

        }

    }

    private void setDefaults() {

        put("canvas.color", Color.white);
        put("canvas.zoom", 100);

        put("color.grid.major", Color.gray);
        put("color.grid.minor", Color.lightGray);

        put("font.color", Color.black);
        put("font.name", new JLabel().getFont().getName());
        put("font.size", 12.0F);
        put("font.style", Font.PLAIN);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        put("frame.height", screenSize.height * 3 / 4);
        put("frame.hSplit", 0.8);
        put("frame.vSplit", 0.5);
        put("frame.width", screenSize.width * 3 / 4);
        put("frame.x", screenSize.width / 8);
        put("frame.y", screenSize.height / 8);

        if (PlatformManager.isMacPlatform())
            put("frame.y", screenSize.height / 12);

        put("grid.major", 100.0F);
        put("grid.minor", 20.0F);

        put("glyph.background", Color.white);
        put("glyph.foreground", Color.black);
        put("glyph.hypothetical", false);
        put("glyph.lineWidth", 3.0F);
        put("glyph.orientation", OrientationType.RIGHT.ordinal());
        put("glyph.padding", 20.0F);
        put("glyph.selection", Color.blue);

        put("version", EditorApplication.getVersion());

    }

    public void readPropertiesFromConfigFile() throws IOException {

        Properties props = new Properties();
        String propFileName = System.getProperty("user.home") + "/.beacon.config.properties";

        InputStream inputStream = new FileInputStream(propFileName);
        props.load(inputStream);
        inputStream.close();

        put("canvas.color", Color.decode(props.getProperty("canvas.color")));
        put("canvas.zoom", 100);

        put("color.grid.major", Color.decode(props.getProperty("color.grid.major")));
        put("color.grid.minor", Color.decode(props.getProperty("color.grid.minor")));

        put("font.color", Color.decode(props.getProperty("font.color")));
        put("font.name", props.getProperty("font.name"));
        put("font.size", Float.parseFloat(props.getProperty("font.size")));
        put("font.style", Integer.parseInt(props.getProperty("font.style")));

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        put("frame.height", screenSize.height * 3 / 4);
        put("frame.hSplit", 0.8);
        put("frame.vSplit", 0.5);
        put("frame.width", screenSize.width * 3 / 4);
        put("frame.x", screenSize.width / 8);
        put("frame.y", screenSize.height / 8);

        if (PlatformManager.isMacPlatform())
            put("frame.y", screenSize.height / 12);

        put("grid.major", 100.0F);
        put("grid.minor", 20.0F);

        put("glyph.background", Color.decode(props.getProperty("glyph.background")));
        put("glyph.foreground", Color.decode(props.getProperty("glyph.foreground")));
        put("glyph.hypothetical", false);
        put("glyph.lineWidth", Float.parseFloat(props.getProperty("glyph.lineWidth")));
        put("glyph.orientation", OrientationType.RIGHT.ordinal());
        put("glyph.padding", Float.parseFloat(props.getProperty("glyph.padding")));
        put("glyph.selection", Color.decode(props.getProperty("glyph.selection")));

        put("version", EditorApplication.getVersion());

    }

    // TODO document method
    public boolean isChanged() {
        return isChanged_;
    }

    // TODO document method
    public void put(String key, boolean value) {
        put(key, Boolean.toString(value));
    }

    // TODO document method
    public void put(String key, Color value) {
        put(key, value.getRGB());
    }

    // TODO document method
    public void put(String key, double value) {
        put(key, Double.toString(value));
    }

    // TODO document method
    public void put(String key, float value) {
        put(key, Float.toString(value));
    }

    // TODO document method
    public void put(String key, int value) {
        put(key, Integer.toString(value));
    }

    // TODO document method
    public void setChanged(boolean isChanged) {
        String title = file_.getName();

        if (isChanged)
            title += " - Edited";

        getFrame().setTitle(title);
        getFrame().getRootPane().putClientProperty(
                "Window.documentModified", isChanged);

        isChanged_ = isChanged;
    }

    // TODO document method
    public void setFile(File file) {
        file_ = file;
    }

    // TODO document method
    public void setPathway(Pathway pathway) {
        pathway_ = pathway;
    }

    // TODO document method
    public void setState(DocumentState state) {
        state_ = state;
    }
    
    public void refresh() {
        getBrowserMenu().refresh();
        getCanvas().repaint();
        getLayersMenu().setDocument(this);
        getBrowserMenu().setDocument(this);
    }
}