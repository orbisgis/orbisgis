/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. 
 * 
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.docking.internals;

import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.action.CAction;
import java.awt.Component;
import java.util.concurrent.atomic.AtomicBoolean;

import org.orbisgis.viewapi.docking.DockingPanel;

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
