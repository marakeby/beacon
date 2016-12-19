package edu.vt.beacon.editor.palette;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.vt.beacon.editor.document.Document;
import edu.vt.beacon.editor.resources.icons.IconType;
import edu.vt.beacon.editor.swing.IconButton;
import edu.vt.beacon.graph.glyph.GlyphType;

public class PaletteButton extends IconButton
    implements ActionListener
{
    private static final long serialVersionUID = 1L;
    
//    private Document document_;

    private PalettePanel panel_ ;
    private GlyphType glyphType_;
    
    // TODO document constructor
    protected PaletteButton(PalettePanel panel, GlyphType glyphType)
    {
        super (IconType.valueOf(glyphType.name()).getIcon());
        
        setAlignmentX(Component.CENTER_ALIGNMENT);
        setToolTipText(glyphType.toString());
        
//        document_ = document;
        glyphType_ = glyphType;
        panel_ = panel;
        
        addActionListener(this);
    }
    
    // TODO document method
    @Override
    public void actionPerformed(ActionEvent event)
    {
        setSelected(!isSelected());
    }
    
    // TODO document method
    public GlyphType getGlyphType()
    {
        return glyphType_;
    }
    
    // FIXME complete method
    @Override
    public void setSelected(boolean isSelected)
    {
        if (isSelected) {
            
            PaletteButton selectedButton =
                    panel_.getSelectedButton();
            
            if (selectedButton != null)
                selectedButton.setSelected(false);
        }
        
        setIcon(isSelected ? getDisabledIcon()
                           : IconType.valueOf(glyphType_.name()).getIcon());
        
        super.setSelected(isSelected);
    }
}