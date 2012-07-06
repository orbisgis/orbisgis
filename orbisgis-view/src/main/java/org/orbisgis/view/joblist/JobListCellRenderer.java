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

import java.awt.*;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import org.orbisgis.view.icons.OrbisGISIcon;

/**
 * @brief Renderer of the JobList
 * 
 * A Job row contain a Label,an image (progression) and a button(cancel)
 */

public class JobListCellRenderer implements ListCellRenderer {
        private JLabel jobCancelLabel;
        
        private JPanel makeRow(JobListItem item) {
                //The panel show the background of the DataSource Item
                JPanel jobPanel = new JPanel();
                FlowLayout fl = new FlowLayout(FlowLayout.LEADING);
                fl.setHgap(5);
                fl.setVgap(0);
                jobPanel.setLayout(fl);
                jobCancelLabel = new JLabel(OrbisGISIcon.getIcon("cancel"));
                //The label show the text of the DataSource Item
                JLabel jobLabel = new JLabel();
                jobLabel.setText(item.getLabel());
                jobPanel.add(jobCancelLabel);
                //Add the label into the Panel
                jobPanel.add(jobLabel);
                return jobPanel;
        }
        
        /**
         * Return true if the position provided is on the 
         * @param position
         * @return 
         */
        public boolean isPositionOnCancelImage(Point position) {
                
                if(jobCancelLabel!=null) {
                        return jobCancelLabel.getBounds().contains(position);
                } else {
                        return false;
                }
        }
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index,
      boolean isSelected, boolean cellHasFocus) {
                JobListItem item = (JobListItem) value;
                return makeRow(item);
        }
        
}
