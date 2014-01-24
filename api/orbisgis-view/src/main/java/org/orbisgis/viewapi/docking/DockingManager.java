/*
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
package org.orbisgis.viewapi.docking;

import java.util.List;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;

/**
 * 
 * Docking system manager. Its implementation uncouple 
 * OrbisGIS and DockingFrame library.
 * @author Nicolas Fortin
 */
public interface DockingManager {
	/**
	 * Shows a view at the given location as child
         * This view can only be hidden by the user, and is not adapted to multiple instance editors
	 * of <code>root</code>.
	 * @param frame the <code>DockingPanel</code> for which a view should be opened
         * @return Dockable unique ID
	 */
        public String addDockingPanel( DockingPanel frame);
        
        /**
         * Remove docking panel id
         * @param dockId 
         */
        public void removeDockingPanel( String dockId);

        /**
         * Display this action on the owner frame
         * @param action
         * @return Unique ID
         */
        public String addToolbarItem(Action action);

        /**
         * @param action Action instance to remove
         * @return True if the action has been found and deleted
         */
        public boolean removeToolbarItem(Action action);

        /**
         * @return the managed frame
         */
        public JFrame getOwner();

        /**
         * 
         * @return The look and feel menu
         */
        public JMenu getLookAndFeelMenu();
        /**
         * 
         * @return The menu that shows items declared in the docking
         */
        public JMenu getCloseableDockableMenu();
        
        /**
         * Save the docking layout
         */
        public void saveLayout();
        /**
         * Show the preference dialog, on the owner,
         * with at least the preference model of DockingFrames
         */
        public void showPreferenceDialog();
        /**
         * The multiple instances panels can be shown at the next start of application
         * if their factory is registered 
         * before loading the layout {@link setDockingStateFile}
         * @param factoryName
         * @param factory  
         */
        public void registerPanelFactory(String factoryName,DockingPanelFactory factory);
        
        /**
         * Free docking resources and save the layout
         */
        public void dispose();
        
        /**
         * Get the current opened panels
         * @return 
         */
        public List<DockingPanel> getPanels();
        
        
        /**
         * DockingManagerImpl will load and save the panels layout
         * in the specified file. Load the layout if the file exists.
         * @param dockingStateFilePath Destination of the default persistence file
         */
        public void setDockingLayoutPersistanceFilePath(String dockingStateFilePath);
        
        /**
         * Create a new dockable corresponding to this layout
         * @param factoryId The factory id registerPanelFactory:factoryName
         * @param panelLayout 
         */
        public void show(String factoryId, DockingPanelLayout panelLayout);
}
