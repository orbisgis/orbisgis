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
 * Copyright (C) 2015-2016 CNRS (Lab-STICC UMR CNRS 6285)
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

package org.orbisgis.wpsclient.view.utils.sif;

import net.miginfocom.swing.MigLayout;
import org.orbisgis.sif.common.ContainerItem;

import javax.swing.*;
import java.awt.*;

/**
 * @author Sylvain PALOMINOS
 */
public class JPanelListRenderer extends JPanel implements ListCellRenderer<ContainerItem<Object>>{

    @Override
    public Component getListCellRendererComponent(JList<? extends ContainerItem<Object>> panelList,
                                                  ContainerItem<Object> container,
                                                  int index, boolean isSelected, boolean cellHasFocus){
        if(container == null){
            return new JPanel();
        }
        JComponent panel;
        if(container.getKey() instanceof JPanel) {
            panel = (JPanel)container.getKey();
        }
        else if(container.getKey() instanceof JComponent){
            panel = new JPanel(new MigLayout("ins 0, gap 0"));
            panel.add((JComponent)container.getKey());
        }
        else{
            panel = new JPanel();
            panel.add(new JLabel(container.getKey().toString()));
        }

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
