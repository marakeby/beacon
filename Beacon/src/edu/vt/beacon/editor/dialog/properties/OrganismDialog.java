package edu.vt.beacon.editor.dialog.properties;

import edu.vt.beacon.editor.dialog.AbstractDialog;
import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.resources.icons.IconType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class OrganismDialog extends AbstractDialog {

    private static final String AC_BUTTON_GENE_ADD = "GENE_ADD";
    private static final String AC_BUTTON_GENE_DELETE = "GENE_DELETE";
    private static final String AC_BUTTON_GENE_EDIT = "GENE_EDIT";

    private static final String AC_OK_BUTTON = "OK";
    private static final String AC_CANCEL_BUTTON = "CANCEL";

    private boolean isChanged =false;
    private static final String[] defaultOrganisms = {"Arabidopsis thaliana", "Medicago truncatula", "Populus", "Rice", "Zea mays"};
    private static final String fileSep =  System.getProperty("file.separator");
    private static final String organismFileName = System.getProperty("user.home") + fileSep+ ".beacon" +fileSep + "organism.properties";

    private JButton addButton_;
    private JButton editButton_;
    private JButton deleteButton_;
    private JButton okButton_;
    private JButton cancelButton_;
    private OrganismTableModel organismListModel_;
    private JTable organismList_;

    public OrganismDialog(Document document, Component owner) throws HeadlessException {

//        super(document, document.getFrame());
        super(document, (JFrame) null);
        setTitle("Organisms");

        if (document == null)
            return;

        setModalityType(ModalityType.DOCUMENT_MODAL);
        this.document_ = document;

        add(createContentPanel());
//        setSize(600, 350);
//        setPreferredSize(new Dimension(600, 350));
//        setMaximumSize(new Dimension(600, 350));
//        setMinimumSize(new Dimension(600, 350));
//        setResizable(false);
//        pack();
//        setLocationRelativeTo(getOwner());
//        setVisible(true);

        isChanged =false;
        setLocationRelativeTo(document.getCanvas());
        setResizable(false);
        pack();
        setVisible(true);
    }

    public String getAPropertiesDirectoryPath() {
        return PropertiesDialog.class.getPackage().getName().replace(".",
                System.getProperty("file.separator")) +
                System.getProperty("file.separator");
    }

    private void populateOrganismTable() {

        Vector<String> organismsList = new Vector<String>();
        try {

//            System.out.println("loading prop file for organism " + organismFileName);

            InputStream is = new FileInputStream(new File(organismFileName));

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                organismsList.add(line);
            }
        } catch(Exception e){
//            System.out.println(e);
        }

        if (organismsList.size()==0){
            organismsList.addAll(Arrays.asList(defaultOrganisms));
        }

        organismListModel_= new OrganismTableModel(organismsList);
        organismList_ = new JTable(organismListModel_);

        organismList_.setFillsViewportHeight(true);
        organismList_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

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

        populateOrganismTable();

        JScrollPane geneListScrollPane = new JScrollPane(organismList_);
        geneListScrollPane.setPreferredSize(new Dimension(580, 200));
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.gridwidth = 5;

        contentPanel.add(geneListScrollPane, c);

        JPanel p = new JPanel();
        okButton_ = new JButton("save");
        okButton_.setActionCommand(AC_OK_BUTTON);
        okButton_.addActionListener(this);
        cancelButton_ = new JButton("cancel");
        cancelButton_.setActionCommand(AC_CANCEL_BUTTON);
        cancelButton_.addActionListener(this);

        p.add(okButton_);
        p.add(cancelButton_);

        panel.add(contentPanel, BorderLayout.PAGE_START);
        panel.add(p, BorderLayout.AFTER_LAST_LINE);
        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
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

        String whatTheUserEntered = JOptionPane.showInputDialog("Insert new organism");
        if (whatTheUserEntered != null) {

//            System.out.println(whatTheUserEntered);
            organismListModel_.getData().add(whatTheUserEntered);
            organismListModel_.fireTableDataChanged();
            isChanged=true;
        }

    }

    private void performButtonEdit() {

        int selectedRow = organismList_.getSelectedRow();
        if (selectedRow >= 0) {
            String org = (String)organismList_.getModel().getValueAt(selectedRow, 0);
            String whatTheUserEntered = JOptionPane.showInputDialog(null, "Edit and organism", org);
            if (whatTheUserEntered!=null) {
                organismList_.getModel().setValueAt(whatTheUserEntered, selectedRow, 0);
                isChanged=true;
            }

        }

    }

    private void performButtonDelete() {

        int selectedRow = organismList_.getSelectedRow();
//        System.out.println(selectedRow);
        if (selectedRow >= 0) {
            organismListModel_.getData().remove(selectedRow);
            if (selectedRow>0)
                organismList_.setRowSelectionInterval(selectedRow-1, selectedRow-1);
            organismList_.repaint();
            isChanged=true;
        }

    }

    private void cancel() {
//        System.out.println("cancel ...");
        this.dispose();
    }

    public void ok() {
//        System.out.println("ok ...");
        if (isChanged) {
//            System.out.println("saving ...");

            List<String> organismsList = organismListModel_.getData();
            try {

                File directory = new File(String.valueOf(System.getProperty("user.home") + fileSep+ ".beacon"));
                if (! directory.exists())
                    directory.mkdir();


                PrintWriter writer =
                        new PrintWriter(
                                new File(organismFileName));
                for(String str: organismsList) {
                    writer.write(str+"\n");
                }
                writer.close();

            } catch(Exception e){
                System.out.println(e);
            }

        }
        this.dispose();
    }



}
