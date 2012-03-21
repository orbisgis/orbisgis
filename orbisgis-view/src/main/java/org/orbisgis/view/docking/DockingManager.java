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
package org.orbisgis.view.docking;

import bibliothek.gui.DockStation;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.intern.DefaultCDockable;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.split.SplitDockProperty;
import bibliothek.gui.dock.util.PropertyKey;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.JFrame;
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

        private JFrame owner;   /*<! The main frame */
        
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
         * Free docking resources
         */
        public void dispose() {
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
		this.owner = owner;
                commonControl = new CControl(owner);
                //Set the default empty size of border docking, named flap
                commonControl.putProperty(FlapDockStation.MINIMUM_SIZE,  new Dimension(4,4));
                
                //DEFAULT property of a view
		commonControl.getController().getProperties().set( PropertyKey.DOCK_STATION_TITLE, I18N.getString("orbisgis.view.docking.stationTitle") );
		commonControl.getController().getProperties().set( PropertyKey.DOCK_STATION_ICON, OrbisGISIcon.getIcon("mini_orbisgis") );
				
                //StackDockStation will contain all instances of ReservedDockStation
		stackOfReservedDockStations = new StackDockStation();
                

                owner.add(commonControl.getContentArea());

                //Reduce the default height of the TOP flap bar to 0 px
                commonControl.getContentArea().getNorth().setMinimumSize(new Dimension(-1,0));

	}
        
        
	/**
	 * Shows a view, read the view properties to decide where to show it
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
            if( !views.containsKey( frame ) ) {
                //Create the DockingFrame item
                OrbisGISView dockItem = new OrbisGISView( frame );
                //Place the item in a dockstation
                String restrictedAreaName = frame.getDockingParameters().getDockingArea();
                if(!restrictedAreaName.isEmpty()) {
                    //Find if this restricted area was already created
                    ReservedDockStation reservedDockStation;
                    if(reservedDockStations.containsKey(restrictedAreaName)) {
                        reservedDockStation = reservedDockStations.get(restrictedAreaName);
                    } else {
                        //Create the restricted area
                        reservedDockStation = new ReservedDockStation(restrictedAreaName);
                        //Store the dockstation in the map, for future panels
                        reservedDockStations.put(restrictedAreaName, reservedDockStation);
                        //Set the dockstation in the stack
                        stackOfReservedDockStations.drop(reservedDockStation);
                        //Show the dock station
                        //frontend.show(reservedDockStation);
                    }
                    //Apply area parameters to panel parameters
                    frame.getDockingParameters().setDockingAreaParameters(reservedDockStation.getDockingAreaParameters());
                    root = reservedDockStation;
                    location = new SplitDockProperty();    
                }
                
                commonControl.addDockable(dockItem);
                dockItem.setVisible(true);
                views.put( frame, dockItem);
            }
	}
}
