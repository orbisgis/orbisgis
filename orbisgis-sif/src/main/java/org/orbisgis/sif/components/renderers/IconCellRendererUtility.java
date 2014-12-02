package org.orbisgis.sif.components.renderers;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * This utility class intends to merge icons and components and put them
 * in a final icon. The main goal is to be able to bypass a swing bug that
 * prevents us to use complex components in some cell renderers.
 * @author Nicolas Fortin
 * @author Alexis Guéganno
 */
public class IconCellRendererUtility {

    private IconCellRendererUtility(){}

    /**
     * Apply the opacity, background color, foreground color and border properties of {@code source} to
     * {@code destination}.
     * @param source The source component.
     * @param destination The destination component.
     */
    public static void copyComponentStyle(JComponent source, JComponent destination) {
        destination.setOpaque(source.isOpaque());
        destination.setBackground(source.getBackground());
        destination.setForeground(source.getForeground());
        destination.setBorder(source.getBorder());
    }

    /**
     * Merge the two given icons in one, drawing {@code bottom} under {@code top}.
     * @param bottom The Icon drawn at the bottom
     * @param top The Icon drawn at the top
     * @return THe merged Icon.
     */
    public static ImageIcon mergeIcons(ImageIcon bottom,ImageIcon top) {
        if(bottom==null) {
            return top;
        }
        if(top==null) {
            return bottom;
        }
        BufferedImage image = new BufferedImage(Math.max(bottom.getIconWidth(),top.getIconWidth()),
                Math.max(bottom.getIconHeight(),top.getIconHeight()), BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        g.drawImage(bottom.getImage(),0,0,null);
        g.drawImage(top.getImage(), 0, 0, null);
        return new ImageIcon(image);
    }
    /**
     * Draw the component at the left of the provided icon
     * @param component The component to be merged.
     * @param icon The Icon to be merged.
     * @return The resulting Icon.
     */
    public static Icon mergeComponentAndIcon(JComponent component,Icon icon) {
        Dimension compSize = component.getPreferredSize();
        component.setSize(compSize);
        int compWidth = compSize.width;
        int compHeight =  compSize.height;
        int iconY = 0;
        if(icon!=null) {
            compWidth += icon.getIconWidth();
            //Set vertical icon alignment to center
            if(compHeight>icon.getIconHeight()) {
                iconY = (int)Math.ceil((compHeight - icon.getIconHeight()) / 2);
            } else {
                compHeight = icon.getIconHeight();
            }
        }
        //Create an icon that is the compound of the checkbox with the row icon
        if(compWidth<=0 || compHeight<=0) {
            return null;
        }
        BufferedImage image = new BufferedImage(compWidth, compHeight, BufferedImage.TYPE_INT_ARGB);
        CellRendererPane pane = new CellRendererPane();
        pane.add(component);
        pane.paintComponent(image.createGraphics(), component, pane, component.getBounds());
        if(icon!=null) {
            icon.paintIcon(pane, image.getGraphics(), component.getPreferredSize().width, iconY);
        }
        return new ImageIcon(image);
    }
}
