package edu.vt.beacon.editor.document;
import edu.vt.beacon.editor.resources.icons.IconType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/**
 * Created by marakeby on 10/24/16.
 */

public class DocumentTab
        extends JPanel
{
    private JLabel titleLabel;
    private Document doc;
    public DocumentTab(String title, Document doc)
    {
        this.doc = doc;
        setLayout(new FlowLayout(1, 5, 3));
        setOpaque(false);

        this.titleLabel = new JLabel(title);
        this.titleLabel.setIcon(IconType.PROJECT.getIcon());
        this.titleLabel.setIconTextGap(5);

        add(this.titleLabel);
        add(Box.createHorizontalStrut(10));

        JButton closeButton = new JButton();
        closeButton.addActionListener(new CloseListener(this.doc));
        closeButton.setBorder(BorderFactory.createEmptyBorder());
        closeButton.setContentAreaFilled(false);
        closeButton.setFocusable(false);
        closeButton.setIcon(IconType.CLOSE.getIcon());
        closeButton.setPressedIcon(IconType.CLOSE_PRESS.getIcon());
        closeButton.setRolloverIcon(IconType.CLOSE_ROLL.getIcon());

        add(closeButton);
    }
    public Document getDocument(){
        return doc;
    }

    public void refresh(){
        this.titleLabel.setText(doc.getFile().getName());
        setHighlightTab(doc.isChanged());
    }
    public void setHighlightTab(boolean state){
        if (state)
            this.titleLabel.setForeground(Color.red);
        else
            this.titleLabel.setForeground(Color.black);


    }

    public void setFontStyle(int style)
    {
        this.titleLabel.setFont(this.titleLabel.getFont().deriveFont(style));
    }

    private class CloseListener
            implements ActionListener
    {
        private Document document;
        private CloseListener(Document doc) {this.document = doc;}

        public void actionPerformed(ActionEvent ae)
        {
            System.out.println("removing project " + this.document.getFile());
            this.document.getViewer().removeProject(this.document);
        }
    }
}
