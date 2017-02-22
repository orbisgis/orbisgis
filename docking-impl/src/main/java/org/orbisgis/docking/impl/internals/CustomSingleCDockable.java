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
package org.orbisgis.docking.impl.internals;

import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.action.CAction;
import java.awt.Component;
import java.util.concurrent.atomic.AtomicBoolean;

import org.orbisgis.sif.docking.DockingPanel;

/**
 * A custom cdockable that contains a reference to the dockingPanel instance.
 * @author Nicolas Fortin
 */
public class CustomSingleCDockable extends DefaultSingleCDockable implements CustomPanelHolder {
        private DockingPanel dockingPanel;
        private AtomicBoolean changeParameter = new AtomicBoolean(false);
        public CustomSingleCDockable(DockingPanel dockingPanel, String id, Component content, CAction... actions) {
                super(id, content, actions);
                this.dockingPanel = dockingPanel;
        }
        @Override
        public DockingPanel getDockingPanel() {
                return dockingPanel;
        }

        /**
         * In order to veto the visiblity change, this method has to be overridden.
         * @param visible New state
         */
        @Override
        public void setVisible(boolean visible) {
            if(!changeParameter.getAndSet(true)) {
                try {
                    dockingPanel.getDockingParameters().setVisible(visible);
                    super.setVisible(visible);
                    dockingPanel.getDockingParameters().setVisible(isVisible());
                } finally {
                    changeParameter.set(false);
                }
            }
        }
}
