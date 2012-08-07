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
package org.orbisgis.view.docking.internals;


import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.MultipleCDockableFactory;
import org.orbisgis.view.docking.DockingPanel;
import org.orbisgis.view.docking.DockingPanelFactory;
import org.orbisgis.view.docking.DockingPanelLayout;

/**
 *
 */
public class InternalCommonFactory implements MultipleCDockableFactory<CustomMultipleCDockable, DockingPanelLayout> {
    
        private DockingPanelFactory factory;
        private CControl ccontrol;

        public InternalCommonFactory(DockingPanelFactory factory, CControl ccontrol) {
                this.factory = factory;
                this.ccontrol = ccontrol;
        }

            
        /* An empty layout is required to read a layout from an XML file or from a byte stream */
        @Override
        public DockingPanelLayout create(){
                return factory.makeEmptyLayout();
        }

        /* An optional method allowing to reuse 'dockable' when loading a new layout */
        @Override
        public boolean match( CustomMultipleCDockable dockable, DockingPanelLayout layout ){
                return factory.match(layout);
        }

        /* Called when applying a stored layout */
        @Override
        public CustomMultipleCDockable read( DockingPanelLayout layout ){
                DockingPanel panel = factory.create(layout);
                CustomMultipleCDockable dockable = OrbisGISView.createMultiple(panel, this, ccontrol);                
                return dockable;
        }

        /* Called when storing the current layout */
        @Override
        public DockingPanelLayout write( CustomMultipleCDockable dockable ){
                return factory.getLayout(dockable.getDockingPanel());
        }
}
