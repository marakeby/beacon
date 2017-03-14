package edu.vt.beacon.editor.dialog;

import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.gene.Gene;
import edu.vt.beacon.editor.resources.icons.IconType;
import edu.vt.beacon.editor.swing.EditingTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class GeneDialogInput extends AbstractDialog {

    private static final String LABEL_ID = "ID:";
    private static final String LABEL_NAME = "Name:";
    private static final String LABEL_PUBMED = "PubMed:";
    private static final String LABEL_DESCRIPTION = "Description:";

    private static final String AC_BUTTON_GENE_ADD = "GENE_ADD";
    private static final String AC_BUTTON_GENE_DELETE = "GENE_DELETE";
    private static final String AC_TEXTFIELD_ID = "TEXTFIELD_ID";
    private static final String AC_TEXTFIELD_NAME = "TEXTFIELD_NAME";
    private static final String AC_TEXTFIELD_PUBMED = "TEXTFIELD_PUBMED";
    private static final String AC_TEXTAREA_DESCRIPTION = "TEXTAREA_DESCRIPTION";

    private EditingTextField idTextField_;
    private EditingTextField nameTextField_;
    private EditingTextField pubMedTextField_;
    private JTextArea descriptionTextArea_;
    private Gene gene_;

    public GeneDialogInput(Document document, Gene gene) throws HeadlessException {

        super(document, document.getFrame());
        setModalityType(ModalityType.DOCUMENT_MODAL);
        this.document_ = document;
        this.gene_ = gene;
        add(createBasePanel(), BorderLayout.SOUTH);
        add(createContentPanel(), BorderLayout.CENTER);
        initForm();
//        setSize(700, 300);
//        setPreferredSize(new Dimension(700, 300));
//        setMaximumSize(new Dimension(700, 300));
//        setMinimumSize(new Dimension(700, 300));
        this.setUndecorated(false);
        setLocationRelativeTo(getOwner());
        pack();
        setVisible(true);
    }

    private void initForm() {

        if (gene_ != null) {

            idTextField_.setText(gene_.getId() != null ? gene_.getId() : "");
            nameTextField_.setText(gene_.getName() != null ? gene_.getName() : "");
            pubMedTextField_.setText(gene_.getPubMed() != null ? gene_.getPubMed() : "");
            descriptionTextArea_.setText(gene_.getDescription() != null ? gene_.getDescription() : "");

        }

    }

    private Component createContentPanel() {
        JComponent panel = createBasePanel();
        panel.setLayout(new BorderLayout());
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.LINE_START;

        JLabel idLabel = new JLabel(LABEL_ID);
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        contentPanel.add(idLabel, c);

        idTextField_ = getTextField(40, AC_TEXTFIELD_ID);
        idTextField_.addActionListener(this);
        c.gridx = 1;
        c.gridy = 0;
        contentPanel.add(idTextField_, c);

        JButton idHelpButton = new JButton(IconType.valueOf("HELP").getIcon());
        idHelpButton.setBorder(BorderFactory.createEmptyBorder());
        idHelpButton.setContentAreaFilled(false);
        idHelpButton.setToolTipText("test");
        c.gridx = 2;
        c.gridy = 0;
        c.insets = new Insets(0, 20, 0, 0);
        contentPanel.add(idHelpButton, c);

        JLabel nameLabel = new JLabel(LABEL_NAME);
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(0, 0, 0, 0);
        contentPanel.add(nameLabel, c);

        nameTextField_ = getTextField(40, AC_TEXTFIELD_NAME);
        nameTextField_.addActionListener(this);
        c.gridx = 1;
        c.gridy = 1;
        contentPanel.add(nameTextField_, c);

        JLabel pubMedLabel = new JLabel(LABEL_PUBMED);
        c.gridx = 0;
        c.gridy = 2;
        c.insets = new Insets(0, 0, 0, 0);
        contentPanel.add(pubMedLabel, c);

        pubMedTextField_ = getTextField(40, AC_TEXTFIELD_PUBMED);
        pubMedTextField_.addActionListener(this);
        c.gridx = 1;
        c.gridy = 2;
        contentPanel.add(pubMedTextField_, c);

        JButton pubMedHelpButton = new JButton(IconType.valueOf("HELP").getIcon());
        pubMedHelpButton.setBorder(BorderFactory.createEmptyBorder());
        pubMedHelpButton.setContentAreaFilled(false);
        pubMedHelpButton.setToolTipText("test");
        c.gridx = 2;
        c.gridy = 2;
        c.insets = new Insets(0, 20, 0, 0);
        contentPanel.add(pubMedHelpButton, c);

        JLabel descriptionLabel = new JLabel(LABEL_DESCRIPTION);
        c.gridx = 0;
        c.gridy = 3;
        c.insets = new Insets(5, 0, 0, 0);
        contentPanel.add(descriptionLabel, c);

        descriptionTextArea_ = new JTextArea(5, 45);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionTextArea_);
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(5, 0, 0, 0);
        contentPanel.add(descriptionScrollPane, c);

        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Actions independent of glyphs selected
        String actionCommand = e.getActionCommand();

        if (actionCommand.equals(AC_OK_BUTTON)) {

            gene_.setId(idTextField_.getText());
            gene_.setName(nameTextField_.getText());
            gene_.setPubMed(pubMedTextField_.getText());
            gene_.setDescription(descriptionTextArea_.getText());

        }

        dispose();

    }

}
