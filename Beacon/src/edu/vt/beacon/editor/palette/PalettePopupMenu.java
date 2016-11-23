package edu.vt.beacon.editor.palette;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import edu.vt.beacon.editor.swing.IconButton;
import edu.vt.beacon.editor.swing.laf.Skinnable;

public class PalettePopupMenu extends JPopupMenu
    implements Skinnable
{
    private static final long serialVersionUID = 1L;
    
    private JPanel buttonPanel_;
    
    private PalettePanel palettePanel_;
    
    // TODO document constructor
    protected PalettePopupMenu(PalettePanel palettePanel)
    {
        setBorder(BorderFactory.createEmptyBorder());
        
        palettePanel_ = palettePanel;
        
        buildButtonPanel();
    }
    
    // TODO document method
    private void buildButtonPanel()
    {
        buttonPanel_ = new JPanel();
        buttonPanel_.setBackground(COLOR_BACKGROUND);
        buttonPanel_.setBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel_.setLayout(new BoxLayout(buttonPanel_, BoxLayout.Y_AXIS));
        
        add(buttonPanel_);
    }
    
    // TODO document method
    protected void refresh()
    {
        buttonPanel_.removeAll();
        
        Dimension spacingSize = new Dimension(0, 20);
        ArrayList<PaletteButton> buttons = palettePanel_.getHiddenButtons();
        
        for (int i = 0; i < buttons.size(); i++) {
            
            buttonPanel_.add(new PopupMenuButton(buttons.get(i)));
            
            if (i < buttons.size() - 1)
                buttonPanel_.add(Box.createRigidArea(spacingSize));
        }
    }
    
    private class PopupMenuButton extends IconButton
        implements ActionListener
    {
        private static final long serialVersionUID = 1L;
        
        private PaletteButton button_;
        
        // TODO document constructor
        private PopupMenuButton(PaletteButton button)
        {
            super (button.getIcon());
            
            setAlignmentX(Component.CENTER_ALIGNMENT);
            setToolTipText(button.getToolTipText());
            
            button_ = button;
            
            addActionListener(this);
        }
        
        // TODO document method
        @Override
        public void actionPerformed(ActionEvent event)
        {
            button_.setSelected(!button_.isSelected());
            PalettePopupMenu.this.setVisible(false);
        }
    }
}