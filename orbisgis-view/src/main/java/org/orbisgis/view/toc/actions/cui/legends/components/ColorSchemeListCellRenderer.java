/*
 * The Unified Mapping Platform (JUMP) is an extensible, interactive GUI 
 * for visualizing and manipulating spatial features with geometry and attributes.
 *
 * Copyright (C) 2003 Vivid Solutions
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * For more information, contact:
 *
 * Vivid Solutions
 * Suite #1A
 * 2328 Government Street
 * Victoria BC  V8T 5G5
 * Canada
 *
 * (250)385-6040
 * www.vividsolutions.com
 */
package org.orbisgis.view.toc.actions.cui.legends.components;

import org.orbisgis.view.components.renderers.ListLaFRenderer;

import javax.swing.*;
import java.awt.*;

/**
 * This dedicated ListCellRenderer intends to ease the embedding of ColorSchemes in list and combo box.</p>
 * <p>This file was originally integrated in the openJUMP software.</p>
 * @author OpenJump
 * @author Alexis Guéganno
 */
public class ColorSchemeListCellRenderer
        extends ListLaFRenderer {

    /**
     * Builds a new renderer.
     */
    public ColorSchemeListCellRenderer(JList list) {
        super(list);
    }

    @Override
    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        String name = (String) value;
        Component cellRenderer = lookAndFeelRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (cellRenderer instanceof JLabel) {
            JLabel lab = (JLabel) cellRenderer;
            ColorSchemeListCell pan;
            if(isSelected){
                pan = new ColorSchemeListCell(name, list.getSelectionBackground());
            } else {
                pan = new ColorSchemeListCell(name, list.getBackground());
            }
            lab.setIcon(pan.getIcon());
            // Put the palette's source in the tooltip.
            if (name.contains("(") && name.contains(")")) {
                String[] s = name.split("\\(");
                String shortName = s[0].trim();
                String paletteSource = s[1].split("\\)")[0].trim();
                lab.setText(shortName);
                lab.setToolTipText(paletteSource);
            }
        }
        return cellRenderer;
    }

}
