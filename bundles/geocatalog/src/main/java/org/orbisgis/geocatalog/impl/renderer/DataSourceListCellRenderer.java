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
package org.orbisgis.geocatalog.impl.renderer;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;

import org.orbisgis.geocatalog.icons.GeocatalogIcon;
import org.orbisgis.geocatalog.impl.CatalogSourceItem;
import org.orbisgis.sif.common.ContainerItemProperties;
import org.orbisgis.sif.components.renderers.ListLaFRenderer;

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
                        dataSourceLabel.setIcon(GeocatalogIcon.getIcon(listItem.getSourceIconName()));
                }
                return cellRenderer;
        }
}
