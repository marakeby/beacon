package edu.vt.beacon.editor.gene;

public class Gene {

    private String id_;
    private String name_;
    private String pubMed_;
    private String description_;

    public Gene() {
    }

    public Gene(String id, String name, String pubMed, String description) {
        this.id_ = id;
        this.name_ = name;
        this.pubMed_ = pubMed;
        this.description_ = description;
    }

    public Gene(Gene gene) {
        super();
        this.id_ = gene.id_;
        this.name_ = gene.name_;
        this.description_ = gene.description_;
    }

    public String getId() {
        return id_;
    }

    public void setId(String id) {
        this.id_ = id;
    }

    public String getName() {
        return name_;
    }

    public void setName(String name) {
        this.name_ = name;
    }

    public String getDescription() {
        return description_;
    }

    public void setDescription(String description) {
        this.description_ = description;
    }

    public String toString() {
        return id_;
    }

    public String getPubMed() {
        return pubMed_;
    }

    public void setPubMed(String pubMed) {
        this.pubMed_ = pubMed;
    }
}
