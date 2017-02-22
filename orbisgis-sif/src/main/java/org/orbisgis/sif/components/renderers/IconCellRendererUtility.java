/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
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
