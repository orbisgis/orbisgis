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
package org.orbisgis.view.toc.actions.cui.legend.components;


import org.orbisgis.sif.components.renderers.ListLaFRenderer;

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
        Component cellRenderer = lookAndFeelRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (cellRenderer instanceof JLabel && value != null) {
            JLabel lab = (JLabel) cellRenderer;
            ColorSchemeListCell pan;
            String name = (String) value;
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
