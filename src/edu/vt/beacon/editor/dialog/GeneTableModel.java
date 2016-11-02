package edu.vt.beacon.editor.dialog;

import edu.vt.beacon.editor.gene.Gene;
import edu.vt.beacon.editor.resources.icons.IconType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

/**
 * Created by ppws on 5/15/16.
 */
public class GeneTableModel extends DefaultTableModel {

    private List<Gene> data;
    private String[] columnsName = {"ID", "", "Name", "pubMed ID", "", "Description"};


    public GeneTableModel(List<Gene> data) {
        super();
        this.data = data;
    }

    @Override
    public int getRowCount() {
        if (data == null)
            return 0;

        return data.size();
    }

    @Override
    public int getColumnCount() {
        return 6;
    }

    @Override
    public String getColumnName(int column) {

        if (column < 0 || column > 5)
            return "";

        return columnsName[column];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex == 1 || columnIndex == 4)
            return false;

        return true;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex != 1 && columnIndex != 4)
            return super.getColumnClass(columnIndex);

        return ImageIcon.class;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        if (data == null || data.size() <= rowIndex || columnIndex > 5)
            return "";

        switch (columnIndex) {
            case 0:
                if (rowIndex < 0)
                    return null;

                return data.get(rowIndex).getId();

            case 1:

                if (rowIndex < 0)
                    return null;

                String id = data.get(rowIndex).getId();
                if (id != null && !id.isEmpty()) {
                    ImageIcon link = (ImageIcon) IconType.SEARCH.getIcon();
                    return link;
                }

                return null;

            case 2:
                if (rowIndex < 0)
                    return null;

                return data.get(rowIndex).getName();

            case 3:
                if (rowIndex < 0)
                    return null;

                return data.get(rowIndex).getPubMed();

            case 4:

                if (rowIndex < 0)
                    return null;

                String pubmed = data.get(rowIndex).getPubMed();
                if (pubmed != null && !pubmed.isEmpty()) {
                    ImageIcon link = (ImageIcon) IconType.SEARCH.getIcon();
                    return link;
                }

                return null;

            case 5:
                if (rowIndex < 0)
                    return null;

                return data.get(rowIndex).getDescription();

        }

        return "";

    }

    @Override
    public void setValueAt(Object newValue, int rowIndex, int columnIndex) {
        if (data == null || data.size() <= rowIndex || columnIndex > 5)
            return;

        switch (columnIndex) {
            case 0:
                data.get(rowIndex).setId((String) newValue);
                break;

            case 2:
                data.get(rowIndex).setName((String) newValue);
                break;

            case 3:
                data.get(rowIndex).setPubMed((String) newValue);
                break;

            case 5:
                data.get(rowIndex).setDescription((String) newValue);
                break;

        }
    }

    public List<Gene> getData() {
        return data;
    }

}
