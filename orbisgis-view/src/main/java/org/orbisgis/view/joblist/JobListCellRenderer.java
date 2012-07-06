/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *
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
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */
package org.orbisgis.view.joblist;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import javax.swing.*;
import org.orbisgis.view.icons.OrbisGISIcon;

/**
 * @brief Renderer of the JobList
 * 
 * A Job row contain a Label,an image (progression) and a button(cancel)
 */

public class JobListCellRenderer implements ListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList jlist, Object o, int i, boolean bln, boolean bln1) {
                JobListItem item = (JobListItem) o;
                //The panel show the background of the DataSource Item
                JPanel jobPanel = new JPanel();
                FlowLayout fl = new FlowLayout(FlowLayout.LEADING);
                fl.setHgap(5);
                fl.setVgap(0);
                jobPanel.setLayout(fl);
                //Add a JButton to the right to cancel the Job
                JButton jobCancel = new JButton();
                //jobCancel.addActionListener(EventHandler.create(ActionListener.class,item,"onCancel"));
                jobCancel.setIcon(OrbisGISIcon.getIcon("cancel"));
                //The label show the text of the DataSource Item
                JLabel jobLabel = new JLabel();
                jobLabel.setText(item.getLabel());
                jobPanel.add(jobCancel);
                //Add the label into the Panel
                jobPanel.add(jobLabel);
                return jobPanel;
        }
        
}
