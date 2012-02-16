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
package org.orbisgis.view.geocatalog.renderer;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import org.orbisgis.view.icons.OrbisGISIcon;

/**
 * This is the cell renderer for DataSource in the GeoCatalog list
 */

public class DataSourceListCellRenderer implements ListCellRenderer {

    /**
     * Return a component that has been configured to display the specified value.
     * @param list - The JList we're painting.
     * @param value - The value returned by list.getModel().getElementAt(index).
     * @param index - The cells index.
     * @param isSelected - True if the specified cell was selected.
     * @param cellHasFocus - True if the specified cell has the focus. 
     * @return 
     */
     public Component getListCellRendererComponent(JList list,Object value,
                                                    int index,
                                                    boolean isSelected,
                                                    boolean cellHasFocus)
    {
        JLabel sourceComponent = new JLabel();
        sourceComponent.setIcon(OrbisGISIcon.getIcon("geofile"));
        return sourceComponent;
    }
    
}
