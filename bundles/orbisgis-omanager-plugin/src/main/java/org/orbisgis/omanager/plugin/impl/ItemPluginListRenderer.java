/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
package org.orbisgis.omanager.plugin.impl;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * Decorate a Jlist with a custom icon to identify plugins
 * @author Erwan Bocher
 */
public class ItemPluginListRenderer implements ListCellRenderer<ItemPlugin> {

    private ListCellRenderer<? super ItemPlugin> lookAndFeelRenderer;

    public ItemPluginListRenderer(JList list) {
        lookAndFeelRenderer = list.getCellRenderer();
    }   
    
    
    @Override
    public Component getListCellRendererComponent(JList<? extends ItemPlugin> list, ItemPlugin value, int index, boolean isSelected, boolean cellHasFocus) {
        Component lafComp = lookAndFeelRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (lafComp instanceof JLabel && value != null) {
            JLabel label = (JLabel) lafComp;
            label.setIcon(value.getIcon());
        }
        return lafComp;
    }

}
