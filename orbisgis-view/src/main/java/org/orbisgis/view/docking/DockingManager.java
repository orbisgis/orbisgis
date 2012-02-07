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

import bibliothek.gui.DockFrontend;
import bibliothek.gui.DockStation;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.util.PropertyKey;
import java.awt.Window;
import org.orbisgis.utils.I18N;
import org.orbisgis.view.icons.OrbisGISIcon;
/**
 * @brief Manage left,right,up,down,center docking stations.
 * 
 * This manager can save and load emplacement of views in XML.
 */
public class DockingManager {

	/** the {@link DockStation} in the center of the {@link MainFrame} */
	private SplitDockStation split;
	/** the {@link DockStation} at the right side of the {@link MainFrame} */
	private FlapDockStation right;
	/** the {@link DockStation} at the left side of the {@link MainFrame} */
	private FlapDockStation left;
	/** the {@link DockStation} at the down side of the {@link MainFrame} */
	private FlapDockStation down;
	/** the {@link DockStation} that represents the screen */
	private ScreenDockStation screen;
	
	/** link to the docking-frames */
	private DockFrontend frontend;
	
	/**
	 * Creates the new manager
	 * @param frontend link to the docking-frames
	 * @param owner the window used as parent for all dialogs
	 */
	public DockingManager( DockFrontend frontend, Window owner){
		this.frontend = frontend;
		
                //TODO add hide and close buttons
		//frontend.getController().addActionGuard( new ViewDeleteAction( ));
		//frontend.getController().addActionGuard( new Hide( frontend, views ));
		
                //DEFAULT property of a view
		frontend.getController().getProperties().set( PropertyKey.DOCK_STATION_TITLE, I18N.getString("") );
		frontend.getController().getProperties().set( PropertyKey.DOCK_STATION_ICON, OrbisGISIcon.getIcon("mini_orbisgis") );
				
		split = new SplitDockStation();
		right = new FlapDockStation();
		left = new FlapDockStation();
		down = new FlapDockStation();
		screen = new ScreenDockStation( owner );
		
		frontend.addRoot( "screen", screen );
		frontend.addRoot( "split", split );
		frontend.addRoot( "right", right );
		frontend.addRoot( "left", left );
		frontend.addRoot( "down", down );

		frontend.setDefaultStation( split );
	}
	
	/**
	 * Gets the {@link DockStation} that is on the right side of the {@link MainFrame}.
	 * @return the station in the right
	 */
	public FlapDockStation getRightDockStation(){
		return right;
	}
	
	/**
	 * Gets the {@link DockStation} which represents the screen.
	 * @return the screen-station
	 */
	public ScreenDockStation getScreen(){
		return screen;
	}
	
	/**
     * Gets the {@link DockStation} that is on the south side of the {@link MainFrame}.
     * @return the station in the south
     */
    public FlapDockStation getDownDockStation(){
		return down;
	}
	
    /**
     * Gets the {@link DockStation} that is in the center of the {@link MainFrame}.
     * @return the station in the center
     */
	public SplitDockStation getSplit(){
		return split;
	}
	
	/**
     * Gets the {@link DockStation} that is on the left side of the {@link MainFrame}.
     * @return the station in the left
     */
    public FlapDockStation getLeftDockStation(){
		return left;
	}
	
    /**
     * Gets the unique identifier which is used for a certain station.
     * @param station one of the stations known to this manager
     * @return the identifier or <code>null</code> if the station
     * is unknown
     */
	public String getDockStationSide( DockStation station ){
		if( station == split )
			return "split";
		if( station == right )
			return "right";
		if( station == left )
			return "left";
		if( station == down )
			return "south";
		if( station == screen )
			return "screen";
		
		return null;
	}
}
