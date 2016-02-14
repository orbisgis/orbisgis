/**
 * OrbisGIS is a GIS application dedicated to scientific spatial analysis.
 * This cross-platform GIS is developed at the Lab-STICC laboratory by the DECIDE
 * team located in University of South Brittany, Vannes.
 *
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
 * Copyright (C) 2015-2016 CNRS (UMR CNRS 6285)
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
public class JPanelComboBoxRenderer extends JPanel implements ListCellRenderer<ContainerItem<Object>>{

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
