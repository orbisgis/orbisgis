package org.orbisgis.view.toc.actions.cui.legends.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Iterator;

/**
 * Builds an Icon with a subset of a given palette.
 * @author Alexis Guéganno
 */
public class ColorSchemeListCell {
    private BufferedImage result;
    private String label;

    /**
     * Builds a new ColorSchemeListCell that will retrieve colours from the palette name
     * {@code name}. {@code bg} will be used as the background color of the generated
     * Icon.
     * @param name The palette's name
     * @param bg The background color
     */
    public ColorSchemeListCell(String name, Color bg) {
        try {
            Collection<Color> colors = colorScheme(name).getColors();
            result = new BufferedImage(80,16,BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = result.createGraphics();
            g2.setBackground(bg);
            g2.setPaint(bg);
            g2.fillRect(0,0,80,16);
            Iterator i = colorScheme(name).getSubset(5).iterator();
            label = "(" + colors.size() + ") " +  name;
            drawAt(0,(Color) i.next(), g2);
            drawAt(1,(Color) i.next(), g2);
            drawAt(2,(Color) i.next(), g2);
            drawAt(3,(Color) i.next(), g2);
            drawAt(4,(Color) i.next(), g2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Draws a black bordered rectangle of color c with g2 at "ith" position
     * in the inner image.
     * @param i The index
     * @param c The color of the fill
     * @param g2 The graphics of the image
     */
    private void drawAt(int i, Color c, Graphics2D g2){
        g2.setPaint(c);
        g2.setBackground(c);
        g2.fillRect(i*16,0,16,16);
        g2.setPaint(Color.BLACK);
        g2.drawRect(i*16,0, i<4 ? 16 : 15,15);
    }

    /**
     * Gets the generated ImageIcon
     * @return The icon
     */
    public ImageIcon getIcon(){
        return new ImageIcon(result);
    }

    /**
     * Gets the ColorScheme whose name is {@code name}
     * @param name The name of the ColorScheme.
     * @return The ColorScheme
     */
    protected ColorScheme colorScheme(String name) {
        return ColorScheme.create(name);
    }

    /**
     * Gets the label that should be used with the palette.
     * @return The label.
     */
    public String getLabel(){
        return label;
    }

}

