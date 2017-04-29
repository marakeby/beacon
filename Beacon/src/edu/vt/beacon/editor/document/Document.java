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
import edu.vt.beacon.editor.simulation.SimulationPanel;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

public class Document extends HashMap<String, String> {
    private static final long serialVersionUID = 1L;

    private boolean isChanged_;
    private boolean isSavedAtLeastOnce_;

    private ContextManager contextManager_;

    private StateManager stateManager_;

    private LegendManager legendManager_;

    private DocumentState state_;

    private File file_;

    private HashMap<ActionType, Action> actionMap_;

    private HashMap<String, Component> componentMap_;

    private HashMap<String, Boolean> componentInit_;

    private Pathway pathway_;

    private static BrowserMenuPanel browser_;
    private static Frame frame_;
    private static PalettePanel palette_;
    private CanvasPanel canvas_;
    private static LayersMenuPanel layers_;
    private static MenuBar menu_;
    private static JTabbedPane tabs_;
    private static DocumentViewer document_viewer_;

    private static SimulationPanel simulation_;

//    private static BrowserMenuPanel palette_;

    // FIXME complete constructor
    public Document(File file) {
        contextManager_ = new ContextManager(this);
//        stateManager_ = StateManager.getInstance(this);
        stateManager_ = new StateManager(this);
        legendManager_ = new LegendManager(this);
        componentMap_ = new HashMap<String, Component>();
        file_ = file;
        pathway_ = new Pathway("untitled");


//        initializeCanvas();
        componentInit_ = new HashMap<String, Boolean> ();
        stateManager_.insert(new Command(CommandType.CREATING_PATHWAY, pathway_.copy(), getCanvas().getZoomFactor(),
                new Date().getTime()));

        String[] comps= {"browser", "frame","palette","canvas","layers", "menuBar", "documentViewer", "simulation"};

        for(String comp : comps)
        {
            componentInit_.put(comp, false);
        }


        initializeActionMap();
        initializeProperties();

//        initializeComponents();
    }

//    private void initComp(String compName){
//        Boolean compInit = componentInit_.get(compName);
//
//        if (compInit)
//        {
//            comp = componentMap_.get(compName);
//            comp
//        }
//            canvas_ =  new CanvasPanel(this);
//        else
//            canvas_.setDocument_(this);
//
//        componentMap_.put(compName, canvas_);
//    }

    private void initializeCanvas(){
        if (canvas_ == null)
            canvas_ =  new CanvasPanel(this);

        if (componentInit_.get("canvas") ==null || ! componentInit_.get("canvas"))
        {
            componentInit_.put("canvas", true);
            canvas_.setDocument(this);
        }

        componentMap_.put("canvas", canvas_);

    }

    private void initializeTabs(){
        if (tabs_ ==null) {
            tabs_ = new JTabbedPane();
            tabs_.addTab(this.getFile().getName(), null, this.getCanvas().getScrollPane(), this.getFile().getName());
        }
        else
        if (componentInit_.get("tabs") ==null || ! componentInit_.get("tabs"))
        {
            componentInit_.put("tabs", true);
            tabs_.addTab(this.getFile().getName(), null, this.getCanvas().getScrollPane(), this.getFile().getName());
        }

        componentMap_.put("tabs", tabs_);

    }

    private void initializeViewer(){
        if (document_viewer_ ==null) {
            document_viewer_ = new DocumentViewer();
//            tabs_.addTab(this.getFile().getName(), null, this.getCanvas().getScrollPane(), this.getFile().getName());
//            document_viewer_.addProject(this);
        }
//        else
        if (componentInit_.get("documentViewer") ==null || ! componentInit_.get("documentViewer"))
        {
            componentInit_.put("documentViewer", true);
            document_viewer_.addProject(this);
        }

        componentMap_.put("documentViewer", document_viewer_);

    }

    private void initializePalette(){
        if (palette_ ==null)
            palette_ =  new PalettePanel(this);

        componentMap_.put("palette", palette_);

    }

    private void initializeMenuBar(){
        if (menu_ ==null)
            menu_ =  new MenuBar(this);
        else
        if (! componentInit_.get("menuBar"))
        {
            componentInit_.put("menuBar", true);
            menu_.setDocument(this);
        }

        componentMap_.put("menuBar", menu_);

    }
    private void initializeFrame(){
        if (frame_ ==null)
            frame_ =  new Frame(this);

        if (! componentInit_.get("frame")) {
            componentInit_.put("frame", true);
            frame_.setDocument(this);
        }

        componentMap_.put("frame", frame_);

    }

    private void initializeBrowser(){
        if (browser_ ==null)
            browser_ =  new BrowserMenuPanel(this);

        if (! componentInit_.get("browser")) {
            componentInit_.put("browser", true);
            browser_.setDocument(this);
        }

        componentMap_.put("browser_", browser_);

    }

    private void initializeLayers(){
        if (layers_ ==null)
            layers_ =  new LayersMenuPanel(this);

        if (! componentInit_.get("layers"))
        {
            componentInit_.put("layers", true);
            layers_.setDocument(this);
        }

        componentMap_.put("layers", layers_);

    }

    private void initializeSimulation(){
        System.out.println("initialize simulation");
        if (simulation_ ==null)
            simulation_ =  new SimulationPanel(this);

        if (! componentInit_.get("simulation"))
        {
            componentInit_.put("simulation", true);
            simulation_.setDocument(this);
        }

        componentMap_.put("simulation", simulation_);

    }

//    private void initializeComponents(){
//        if (palette_ ==null)
//            palette_ = new PalettePanel(this);
//
//        componentMap_.put("palette", palette_);
//
//
//        if (menu_==null)
//            menu_ = new MenuBar(this);
//        else
//            menu_.setDocument(this);
//
//        componentMap_.put("menuBar", menu_);
//
//        if (frame_ ==null)
//            frame_ = new Frame(this);
//        else
//            frame_.setDocument(this);
//
//        componentMap_.put("frame", frame_);
//
//        if (browser_ == null)
//            browser_ = new BrowserMenuPanel(this);
//        else
//            browser_.setDocument(this);
//
//        componentMap_.put("browser", browser_);
//
//        if (layers_ ==null)
//            layers_ = new LayersMenuPanel(this);
//        else
//            layers_.setDocument(this);
//
////        if (componentMap_.get("layers") == null)
//        componentMap_.put("layers", new LayersMenuPanel(this));
//
//
//    }
//    public Document copy(){
//        Document doc = new Document(this.getFile());
//        doc.setState(this.getState());
//        doc.setPathway(this.getPathway().copy());
//        return doc;
//    }

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
//        System.out.println("getBrowserMenu");

//        if (browser_ == null)
//            browser_ = new BrowserMenuPanel(this);
//        else
//            browser_.setDocument(this);
//
//        componentMap_.put("browser", browser_);
        initializeBrowser();
        return (BrowserMenuPanel) componentMap_.get("browser_");
    }

    // TODO document method
    public CanvasPanel getCanvas() {
//        if (canvas_ ==null)
//            canvas_ =  new CanvasPanel(this);
//        else
//            canvas_.setDocument_(this);
//
//        componentMap_.put("canvas", canvas_);
        initializeCanvas();
//        getTabs();
        return (CanvasPanel) componentMap_.get("canvas");
    }

    public JTabbedPane getTabs() {
//        if (canvas_ ==null)
//            canvas_ =  new CanvasPanel(this);
//        else
//            canvas_.setDocument_(this);
//
//        componentMap_.put("canvas", canvas_);
        initializeTabs();
        return (JTabbedPane) componentMap_.get("tabs");
    }

    public DocumentViewer getViewer() {
//        if (canvas_ ==null)
//            canvas_ =  new CanvasPanel(this);
//        else
//            canvas_.setDocument_(this);
//
//        componentMap_.put("canvas", canvas_);
        initializeViewer();
        return (DocumentViewer) componentMap_.get("documentViewer");
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
//        if (frame_ ==null)
//            frame_ = new Frame(this);
//        else
//            frame_.setDocument(this);
//
////        if (componentMap_.get("frame") == null)
//        componentMap_.put("frame", frame_);

        initializeFrame();
        return (Frame) componentMap_.get("frame");
    }

    // TODO document method
    public int getInteger(String key) {
        return Integer.parseInt(get(key));
    }

    // TODO document method
    public LayersMenuPanel getLayersMenu() {
//        if (layers_ ==null)
//            layers_ = new LayersMenuPanel(this);
//        else
//            layers_.setDocument(this);
//
////        if (componentMap_.get("layers") == null)
//        componentMap_.put("layers", new LayersMenuPanel(this));
        initializeLayers();
        return (LayersMenuPanel) componentMap_.get("layers");
    }

    public SimulationPanel getSimulationPanel() {

        initializeSimulation();
        return (SimulationPanel) componentMap_.get("simulation");
    }
    // TODO document method
    public MenuBar getMenuBar() {
//        if (menu_==null)
//            menu_ = new MenuBar(this);
//        else
//            menu_.setDocument(this);
//
//
////        if (componentMap_.get("menuBar") == null)
//        componentMap_.put("menuBar", menu_);
        initializeMenuBar();

        return (MenuBar) componentMap_.get("menuBar");
    }

    // TODO document method
    public PalettePanel getPalette() {
//        if (palette_ ==null)
//            palette_ = new PalettePanel(this);
////        else
////            palette_.setDocument(this);
//
//        componentMap_.put("palette", palette_);

        initializePalette();
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

    public boolean isSavedAtLeastOnce(){
        return isSavedAtLeastOnce_;
    }

    public void setSavedAtLeastOnce(boolean state){
        isSavedAtLeastOnce_ = state;
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
//        System.out.println("setChanged " + isChanged);
        String title = file_.getAbsolutePath();

        if (isChanged)
            title += " - Edited";

        getFrame().setTitle(title);
        getFrame().getRootPane().putClientProperty(
                "Window.documentModified", isChanged);

        isChanged_ = isChanged;
        getViewer().highlightProject(this, isChanged_);
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

    public void registerComponents()
    {
//        menu_.setDocument(this);
        getBrowserMenu().setDocument(this);
//        layers_.setDocument(this);
        getLayersMenu().setDocument(this);
        getSimulationPanel().setDocument(this);
//        browser_.setDocument(this);
//        getBrowserMenu().setDocument(this);
//        canvas_.setDocument(this);
        getCanvas().setDocument(this);
        getFrame().setDocument(this);
        getMenuBar().setDocument(this);
        initializeProperties();
    }
    public void refresh() {
        getMenuBar();
        getBrowserMenu().refresh();
        getCanvas().repaint();
//        getLayersMenu().setDocument(this);
//        getBrowserMenu().setDocument(this);
        registerComponents();


    }

    public void undo(){
        if (getState().getPrevious() != null)
            getState().getPrevious().apply(this);
    }
    public void redo(){
        if (getState().getNext() != null)
            getState().getNext().apply(this);
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return ((Document) obj).getPathway().equals(getPathway());
    }
}