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

import net.miginfocom.swing.MigLayout;
import org.orbisgis.sif.common.ContainerItem;

import javax.swing.*;
import java.awt.*;

/**
 * Customize the rendering of a JList containing ContainerItem. The content of the container item is rendered as a
 * JPanel. If the content of the ContainerItem is a :
 *  - JPanel, the panel is rendered.
 *  - JComponent, the component is add to a JPanel and the panel is rendered
 *  - Any other object, the toString() method result is stored into a JLabel, added to a JPanel which is rendered.
 *
 * @author Sylvain PALOMINOS
 */
public class JPanelListRenderer extends JPanel implements ListCellRenderer<ContainerItem<Object>>{

    @Override
    public Component getListCellRendererComponent(JList<? extends ContainerItem<Object>> panelList,
                                                  ContainerItem<Object> container,
                                                  int index, boolean isSelected, boolean cellHasFocus){
        //If the container is null, render an empty JPanel
        if(container == null){
            return new JPanel();
        }
        JComponent panel;
        //If the container contains a JPanel, the panel is rendered.
        if(container.getKey() instanceof JPanel) {
            panel = (JPanel)container.getKey();
        }
        //If the container contains a JComponent, add it to a JPanel and render the panel.
        else if(container.getKey() instanceof JComponent){
            panel = new JPanel(new MigLayout("ins 0, gap 0"));
            panel.add((JComponent)container.getKey());
        }
        //Else, create a JLabel with the toString() method result and add it to a JPanel before rendering it.
        else{
            panel = new JPanel();
            panel.add(new JLabel(container.getKey().toString()));
        }

        //Sets the backGround and foreground colors according to the selection state.
        if (isSelected) {
            panel.setBackground(panelList.getSelectionBackground());
            panel.setForeground(panelList.getSelectionForeground());
        } else {
            panel.setBackground(panelList.getBackground());
            panel.setForeground(panelList.getForeground());
        }

        return panel;
    }
}
