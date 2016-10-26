package edu.vt.beacon.editor.resources.icons;

import javax.swing.*;

public enum IconType {
    AND("and.png"),
    ARROW_EXPAND("arrow_expand.png"),
    BIOLOGICAL_ACTIVITY("biological_activity.png"),
    COMPARTMENT("compartment.png"),
    DELAY("delay.png"),
    EQUIVALENCE_ARC("equivalence_arc.png"),
    LOGIC_ARC("logic_arc.png"),
    NECESSARY_STIMULATION("necessary_stimulation.png"),
    NEGATIVE_INFLUENCE("negative_influence.png"),
    NOT("not.png"),
    OR("or.png"),
    PERTURBATION("perturbation.png"),
    PHENOTYPE("phenotype.png"),
    POSITIVE_INFLUENCE("positive_influence.png"),
    SUBMAP("submap.png"),
    TEXT("text.png"),
    UNKNOWN_INFLUENCE("unknown_influence.png"),
    ADD("add.png"),
    DELETE("delete.png"),
    PENCIL("pencil.png"),
    COLOR("color_wheel.png"),
    HELP("help.png"),
    TAG("tag_right.png"),
    NO_IMAGE("no-available-image.png"),
    VIEW("view.png"),
    SEARCH("search.png"),
    CLOSE("close.png"),
    CLOSE_PRESS("close_pressed.png"),
    CLOSE_ROLL("close_rollover.png"),
    PROJECT("project.png"),
    LOGO("logo.png");


    private ImageIcon imageIcon_;

    // TODO document constructor
    private IconType(String fileName) {
        System.out.println(
        Thread.currentThread().getContextClassLoader().getResource(
                getIconDirectoryPath()));
        imageIcon_ = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(
                        getIconDirectoryPath() + fileName));
    }

    // TODO document method
    public Icon getIcon() {
        return imageIcon_;
    }

    // TODO document method
    public String getIconDirectoryPath() {
        return IconType.class.getPackage().getName().replace(".",
                System.getProperty("file.separator")) +
                System.getProperty("file.separator");
    }
}