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
package org.orbisgis.docking.impl.edition;

import org.orbisgis.sif.docking.DockingPanel;
import org.orbisgis.sif.docking.DockingPanelFactory;
import org.orbisgis.sif.docking.DockingPanelLayout;
import org.orbisgis.sif.edition.EditorFactory;

/**
 * MultipleEditorFactory must define EditorPanels instead of DockingPanel
 * then this decorator 
 * make the link between a MultipleEditorFactory and a DcokingPanelFactory.
 */

public class EditorPanelFactoryDecorator implements DockingPanelFactory {
        private EditorFactory multipleEditorFactory;

        public EditorPanelFactoryDecorator(EditorFactory multipleEditorFactory) {
                this.multipleEditorFactory = multipleEditorFactory;
        }

        @Override
        public DockingPanelLayout makeEmptyLayout() {
                return multipleEditorFactory.makeEmptyLayout();
        }

        @Override
        public boolean match(DockingPanelLayout layout) {
                return multipleEditorFactory.match(layout);
        }

        @Override
        public DockingPanel create(DockingPanelLayout layout) {
                DockingPanel panel = multipleEditorFactory.create(layout);
                if(panel!=null) {
                        panel.getDockingParameters().setLayout(layout);
                }
                return panel;
        }        
}
