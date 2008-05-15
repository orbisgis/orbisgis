package org.orbisgis.geoview.cui.gui.widgets;

import java.awt.Component;
import java.awt.image.BufferedImage;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;


public class ImageRenderer extends DefaultListCellRenderer
{

    public Component getListCellRendererComponent(JList list,
                                                  Object value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus)
    {
        // for default cell renderer behavior
        Component c = super.getListCellRendererComponent(list, value,
                                       index, isSelected, cellHasFocus);
        // set icon for cell image
        ((JLabel)c).setIcon(new ImageIcon((BufferedImage)value));
        ((JLabel)c).setText("");
        return c;
    }
}