package edu.vt.beacon.editor.dialog;

import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.document.DocumentState;
import edu.vt.beacon.editor.gene.Gene;
import edu.vt.beacon.editor.resources.icons.IconType;
import edu.vt.beacon.graph.glyph.AbstractGlyph;
import edu.vt.beacon.graph.glyph.node.activity.AbstractActivity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class GeneDialog extends AbstractDialog {

    private static final String AC_BUTTON_GENE_ADD = "GENE_ADD";
    private static final String AC_BUTTON_GENE_DELETE = "GENE_DELETE";
    private static final String AC_BUTTON_GENE_EDIT = "GENE_EDIT";

    JButton addButton_;
    JButton editButton_;
    JButton deleteButton_;
    GeneTableModel geneListModel_;
    JTable geneList_;
    AbstractActivity selectedActivity;

    public GeneDialog(Document document) throws HeadlessException {

        super(document, document.getFrame());
        setTitle("Genes");

        if (document == null)
            return;

        ArrayList<AbstractGlyph> selectedGlyphs = document.getBrowserMenu().getSelectedMap().getSelectedGlyphs();
        if (selectedGlyphs == null || selectedGlyphs.size() != 1 || !(selectedGlyphs.get(0) instanceof AbstractActivity))
            return;

        selectedActivity = (AbstractActivity) selectedGlyphs.get(0);

        setModalityType(ModalityType.DOCUMENT_MODAL);
        this.document_ = document;
        add(createContentPanel());
        setSize(600, 180);
        setPreferredSize(new Dimension(600, 180));
        setMaximumSize(new Dimension(600, 180));
        setMinimumSize(new Dimension(600, 180));
        setLocationRelativeTo(getOwner());
        setVisible(true);
    }

    private Component createContentPanel() {
        JComponent panel = createBasePanel();
        panel.setLayout(new BorderLayout());
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints c = new GridBagConstraints();

        addButton_ = new JButton(IconType.ADD.getIcon());
        editButton_ = new JButton(IconType.PENCIL.getIcon());
        deleteButton_ = new JButton(IconType.DELETE.getIcon());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        addButton_.setBorder(BorderFactory.createEmptyBorder());
        addButton_.setContentAreaFilled(false);
        addButton_.setActionCommand(AC_BUTTON_GENE_ADD);
        addButton_.addActionListener(this);
        editButton_.setBorder(BorderFactory.createEmptyBorder());
        editButton_.setContentAreaFilled(false);
        editButton_.setActionCommand(AC_BUTTON_GENE_EDIT);
        editButton_.addActionListener(this);
        deleteButton_.setBorder(BorderFactory.createEmptyBorder());
        deleteButton_.setContentAreaFilled(false);
        deleteButton_.setActionCommand(AC_BUTTON_GENE_DELETE);
        deleteButton_.addActionListener(this);
        buttonPanel.add(addButton_);
        buttonPanel.add(editButton_);
        buttonPanel.add(deleteButton_);
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_END;
        contentPanel.add(buttonPanel, c);

        geneListModel_ = new GeneTableModel(selectedActivity.getGenes());
        geneList_ = new JTable(geneListModel_);
        geneList_.setFillsViewportHeight(true);
        geneList_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        geneList_.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                int columnIndex = geneList_.columnAtPoint(e.getPoint());
                int rowIndex = geneList_.rowAtPoint(e.getPoint());

                if (rowIndex >= 0 && columnIndex == 1 || columnIndex == 4) {

                    String value = (String) geneListModel_.getValueAt(rowIndex, columnIndex - 1);
                    if (value != null && !value.isEmpty())
                        geneList_.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                } else
                    geneList_.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

            }
        });

        geneList_.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                int row = geneList_.rowAtPoint(e.getPoint());
                int col = geneList_.columnAtPoint(e.getPoint());

                if (row >= 0 && (col == 1 || col == 4) && geneListModel_.getValueAt(row, col) != null) {

                    URI link;
                    String searchTerm = (String) geneListModel_.getValueAt(row, col - 1);

                    try {

                        if (col == 1)
                            link = new URI("http://www.ncbi.nlm.nih.gov/gene/?term=" + searchTerm);
                        else
                            link = new URI("http://www.ncbi.nlm.nih.gov/pubmed/?term=" + searchTerm);

                        Desktop.getDesktop().browse(link);

                    } catch (URISyntaxException e1) {
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                }
            }
        });

        JScrollPane geneListScrollPane = new JScrollPane(geneList_);
        geneListScrollPane.setPreferredSize(new Dimension(580, 100));
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.gridwidth = 5;
        contentPanel.add(geneListScrollPane, c);

        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 5;
        contentPanel.add(new JSeparator(JSeparator.HORIZONTAL), c);

        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Actions independent of glyphs selected
        String actionCommand = e.getActionCommand();

        if (actionCommand.equals(AC_OK_BUTTON))
            ok();

        else if (actionCommand.equals(AC_CANCEL_BUTTON))
            cancel();

        else if (actionCommand.equals(AC_BUTTON_GENE_ADD))
            performButtonAdd();

        else if (actionCommand.equals(AC_BUTTON_GENE_DELETE))
            performButtonDelete();

        else if (actionCommand.equals(AC_BUTTON_GENE_EDIT))
            performButtonEdit();

    }

    private void performButtonAdd() {

        Gene resultValue = new Gene();
        new GeneDialogInput(document_, resultValue);
        if (resultValue.getId() != null && !resultValue.getId().trim().isEmpty()) {
            geneListModel_.getData().add(resultValue);
        }

    }

    private void performButtonEdit() {

        int selectedRow = geneList_.getSelectedRow();
        if (selectedRow >= 0) {

            Gene resultValue = selectedActivity.getGeneAt(selectedRow);
            new GeneDialogInput(document_, resultValue);
            if (resultValue.getId() != null && !resultValue.getId().trim().isEmpty()) {
                geneList_.repaint();
                geneList_.updateUI();
            }

        }

    }

    private void performButtonDelete() {

        int selectedRow = geneList_.getSelectedRow();
        if (selectedRow >= 0) {
            geneListModel_.getData().remove(selectedRow);
            geneList_.repaint();
        }

    }

    private void cancel() {
        document_.getState().apply(document_);
        this.dispose();

    }

    public void ok() {
        if (isChanged()) {
            new DocumentState(document_, "GENE", false);
        }
        this.dispose();
    }

    private boolean isChanged() {
        // TODO Auto-generated method stub
        return false;
    }

}
