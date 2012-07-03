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

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import org.orbisgis.view.geocatalog.CatalogSourceItem;
import org.orbisgis.view.icons.OrbisGISIcon;

/**
 * @brief Item renderer factory of GeoCatalog
 * This is the cell renderer factory for the DataSource List
 * in the GeoCatalog panel.
 * 
 */

public class DataSourceListCellRenderer implements ListCellRenderer {

    private static final Color SELECTED = Color.lightGray;      /*!< Item background color */
    private static final Color DESELECTED = Color.white;        /*!< Item background color */
    private static final Color SELECTED_FONT = Color.white;     /*!< Item font color */
    private static final Color DESELECTED_FONT = Color.black;   /*!< Item font color */
    /**
     * Return a component that has been configured to display the specified value.
     * @param list - The JList we're painting.
     * @param value - The value returned by list.getModel().getElementAt(index).
     * @param index - The cells index.
     * @param isSelected - True if the specified cell was selected.
     * @param cellHasFocus - True if the specified cell has the focus. 
     * @return 
     */
     @Override
     public Component getListCellRendererComponent(JList list,Object value,
                                                    int index,
                                                    boolean isSelected,
                                                    boolean cellHasFocus)
    {
        //The panel show the background of the DataSource Item
        JPanel sourcePanel = new JPanel();
        FlowLayout fl = new FlowLayout(FlowLayout.LEADING);
        fl.setHgap(0);
        sourcePanel.setLayout(fl);
        //The label show the Icon and Text of the DataSource Item
        JLabel sourceLabel = new JLabel();
        //Retrieve the item informations
        CatalogSourceItem listItem = (CatalogSourceItem)value;
        sourceLabel.setIcon(OrbisGISIcon.getIcon(listItem.getSourceIconName()));
        sourceLabel.setText(value.toString());
        //Retrieve the registered name of geocatalog item
        sourceLabel.setVisible(true);
        //Change the item panel background color
        //and the label text color depending on the selection state
        if (isSelected) {
                sourcePanel.setBackground(SELECTED);
                sourceLabel.setForeground(SELECTED_FONT);
        } else {
                sourcePanel.setBackground(DESELECTED);
                sourceLabel.setForeground(DESELECTED_FONT);
        }
        //Add the label into the Panel
        sourcePanel.add(sourceLabel);
        return sourcePanel;
    }
    
}
