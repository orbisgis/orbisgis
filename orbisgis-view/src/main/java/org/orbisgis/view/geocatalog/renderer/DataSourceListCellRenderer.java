/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.geocatalog.renderer;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;

import org.orbisgis.sif.common.ContainerItemProperties;
import org.orbisgis.view.components.renderers.ListLaFRenderer;
import org.orbisgis.view.geocatalog.CatalogSourceItem;
import org.orbisgis.view.icons.OrbisGISIcon;

/**
 * Item renderer factory of GeoCatalog This is the cell renderer factory
 * for the DataSource List in the GeoCatalog panel.
 *
 */
public class DataSourceListCellRenderer extends ListLaFRenderer<ContainerItemProperties> {
        private static final long serialVersionUID = 1L;

        public DataSourceListCellRenderer(JList list) {
                super(list);
        }

        /**
         * Return a component that has been configured to display the specified
         * value.
         *
         * @param list - The JList we're painting.
         * @param value - The value returned by
         * list.getModel().getElementAt(index).
         * @param index - The cells index.
         * @param isSelected - True if the specified cell was selected.
         * @param cellHasFocus - True if the specified cell has the focus.
         * @return
         */
        @Override
        public Component getListCellRendererComponent(JList list, ContainerItemProperties value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {
                Component cellRenderer = lookAndFeelRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (cellRenderer instanceof JLabel) {
                        //Retrieve the item informations
                        CatalogSourceItem listItem = (CatalogSourceItem) value;
                        JLabel dataSourceLabel = (JLabel) cellRenderer;
                        //Customise the component
                        dataSourceLabel.setIcon(OrbisGISIcon.getIcon(listItem.getSourceIconName()));
                }
                return cellRenderer;
        }
}
