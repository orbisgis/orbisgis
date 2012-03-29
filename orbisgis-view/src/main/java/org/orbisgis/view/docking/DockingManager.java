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
 *I
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
package org.orbisgis.view.docking;

import bibliothek.gui.DockStation;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.intern.DefaultCDockable;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.util.PropertyKey;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.JFrame;
import org.apache.log4j.Logger;
import org.orbisgis.utils.I18N;
import org.orbisgis.view.docking.internals.OrbisGISView;
import org.orbisgis.view.docking.internals.ReservedDockStation;
import org.orbisgis.view.icons.OrbisGISIcon;
/**
 * @brief Manage left,right,down,center docking stations.
 * 
 * This manager can save and load emplacement of views in XML.
 */
public final class DockingManager {
        private static final Logger LOGGER = Logger.getLogger(DockingManager.class);
        File dockingState=null;
        private CControl commonControl; /*!< link to the docking-frames */

        private Map<DockingPanel,OrbisGISView> views = new HashMap<DockingPanel,OrbisGISView>();
        
        //Some docking panels must not be mixed with other panels,
        //reservedDockStation is dedicated to this features
        private Map<String,ReservedDockStation> reservedDockStations = new HashMap<String,ReservedDockStation>();
        
        /** the {@link StackDockStation} inserted in {@link split} */
        private StackDockStation stackOfReservedDockStations;
	
        /**
         * Return the docked panels
         * @return The set of panels managed by this docking manager.
         */
	public Set<DockingPanel> getPanels() {
            return views.keySet();
        }
        
        /**
         * Load the docking layout 
         */
        private void loadLayout() {
            if(dockingState!=null) {
                if(dockingState.exists()) {
                    try {
                        commonControl.readXML(dockingState);
                    } catch (IOException ex) {
                        LOGGER.error(I18N.getString("orbisgis.view.DockingManager.layoutloadfailed"), ex);
                    }
                }
            }            
        }
        /**
         * Save the docking layout
         */
        public void saveLayout() {
            if(dockingState!=null) {
                try {
                    commonControl.writeXML(dockingState);
                } catch (IOException ex) {
                    LOGGER.error(I18N.getString("orbisgis.view.DockingManager.layoutsavefailes"), ex);
                }    
            }
        }
        
        /**
         * The multiple instances panels can be shown at the next start of application
         * if their factory is registered 
         * before loading the layout {@link setDockingStateFile}
         */
        public void registerPanelFactory() {
            
        }
        
        /**
         * Free docking resources and save the layout
         */
        public void dispose() {
            saveLayout();
            commonControl.destroy();
        }

        /**
         * For UnitTest purpose
         * @return DefaultCDockable instance, null if not exists
         */
        public DefaultCDockable getDockable(DockingPanel panel) {
            return views.get(panel);
        }
        
	/**
	 * Creates the new manager
	 * @param owner the window used as parent for all dialogs
	 */
	public DockingManager( JFrame owner){
		//this.frontend = new DockFrontend();
                commonControl = new CControl(owner);
                //Set the default empty size of border docking, named flap
                commonControl.putProperty(FlapDockStation.MINIMUM_SIZE,  new Dimension(4,4));
                
                //DEFAULT property of a view
		commonControl.getController().getProperties().set( PropertyKey.DOCK_STATION_TITLE, I18N.getString("orbisgis.view.docking.stationTitle") );
		commonControl.getController().getProperties().set( PropertyKey.DOCK_STATION_ICON, OrbisGISIcon.getIcon("mini_orbisgis") );
				
                //StackDockStation will contain all instances of ReservedDockStation
		//stackOfReservedDockStations = new StackDockStation();
                

                owner.add(commonControl.getContentArea());

                //Reduce the default height of the TOP flap bar to 0 px
                commonControl.getContentArea().getNorth().setMinimumSize(new Dimension(-1,0));

	}
        
        /**
         * DockingManager will load and save the panels layout
         * in the specified file. Load the layout if the file exists.
         * @param dockingState The filename
         * @throws IOException 
         */
        public void setDockingLayoutPersistanceFilePath(String dockingStateFilePath) {
            LOGGER.debug("Loading Docking Frames Layout :\n"+dockingStateFilePath);
            this.dockingState = new File(dockingStateFilePath);
            loadLayout();
        }

	/**
	 * Shows a view at the given location as child
	 * of <code>root</code>.
	 * @param frame the <code>DockingPanel</code> for which a view should be opened
	 */
        public void show( DockingPanel frame) {
            show(frame,null,null);
        }
        
	/**
	 * Shows a view at the given location as child
	 * of <code>root</code>.
	 * @param frame the <code>DockingPanel</code> for which a view should be opened
	 * @param root the preferred parent, might be <code>null</code>
	 * @param location the preferred location, relative to <code>root</code>. Might
	 * be <code>null</code>.
	 */
	public void show( DockingPanel frame, DockStation root, DockableProperty location ){
            OrbisGISView dockItem;
            if( !views.containsKey( frame ) ) {
                //Create the DockingFrame item
                if(frame.getDockingParameters().getName().isEmpty()) {
                    //If the dev doesn't define a name on the panel
                    //We set the name as the name of the class
                    frame.getDockingParameters().setName(frame.getClass().getCanonicalName());
                }
                dockItem = new OrbisGISView( frame );
                //Place the item in a dockstation
                String restrictedAreaName = frame.getDockingParameters().getDockingArea();
                if(!restrictedAreaName.isEmpty()) {
                    //TODO Create the restricted area feature    
                }                
                commonControl.addDockable(dockItem);
                views.put( frame, dockItem);
            } else {
                dockItem = views.get(frame);
            }
            dockItem.setVisible(true);
	}
}
