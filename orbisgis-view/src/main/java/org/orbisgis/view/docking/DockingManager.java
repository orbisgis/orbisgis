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

import bibliothek.extension.gui.dock.DockingFramesPreference;
import bibliothek.extension.gui.dock.preference.PreferenceTreeModel;
import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.RectGradientPainter;
import bibliothek.gui.DockController;
import bibliothek.gui.DockFrontend;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.flap.button.ButtonContent;
import bibliothek.gui.dock.support.lookandfeel.ComponentCollector;
import bibliothek.gui.dock.support.lookandfeel.LookAndFeelList;
import bibliothek.gui.dock.themes.BasicTheme;
import bibliothek.gui.dock.util.PropertyKey;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.util.*;
import javax.swing.JFrame;
import org.orbisgis.utils.I18N;
import org.orbisgis.view.docking.internals.OrbisGISView;
import org.orbisgis.view.icons.OrbisGISIcon;
/**
 * @brief Manage left,right,down,center docking stations.
 * 
 * This manager can save and load emplacement of views in XML.
 */
public final class DockingManager implements ComponentCollector {

        JFrame owner;   /*<! The main frame */
        
        private PreferenceTreeModel preferences; /*< Organizes {@link PreferenceModel}s in a tree */
        private DockFrontend frontend; /*!< link to the docking-frames */
        private LookAndFeelList lookAndFeels;    /*!< /*!< link to the docking-frames */

        private Map<DockingPanel,Dockable> views = new HashMap<DockingPanel,Dockable>();
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
            frontend.getController().kill();
        }
        /**
         * @return Docking Frames dockable instances
         */
        public Collection<Dockable> getDockables() {
            return views.values();
        }
        /**
         * @return Dockable instance, null if not exists
         */
        public Dockable getDockable(DockingPanel panel) {
            return views.get(panel);
        }
        
        /**
        * Help DockingFrames to update the Look And Feel of all views.
        * @return All views of OrbisGis
        */
        public Collection<Component> listComponents() {
            List<Component> components = new ArrayList<Component>();

            components.add(owner);
            for( Dockable d : frontend.getController().getRegister().listDockables() ){
                    components.add( d.getComponent() );
            }
            return components;
        }
        
	/**
	 * Creates the new manager
	 * @param owner the window used as parent for all dialogs
	 */
	public DockingManager( JFrame owner){
		this.frontend = new DockFrontend();
		this.owner = owner;
                

                final DockController controller = new DockController();

                controller.setTheme( new BasicTheme() );
                controller.getProperties().set( EclipseTheme.PAINT_ICONS_WHEN_DESELECTED, true );
                controller.getProperties().set( EclipseTheme.TAB_PAINTER, RectGradientPainter.FACTORY );
                controller.getProperties().set( FlapDockStation.BUTTON_CONTENT, ButtonContent.ICON_AND_TEXT_ONLY );

                frontend = new DockFrontend( controller, owner );
                preferences = new DockingFramesPreference( controller );

                lookAndFeels = LookAndFeelList.getDefaultList();
                lookAndFeels.addComponentCollector( this );
                //TODO add hide and close buttons
		//frontend.getController().addActionGuard( new ViewDeleteAction( ));
		//frontend.getController().addActionGuard( new Hide( frontend, views ));
		
                //DEFAULT property of a view
		frontend.getController().getProperties().set( PropertyKey.DOCK_STATION_TITLE, I18N.getString("orbisgis.view.docking.stationTitle") );
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
                
                
                Container content = owner.getContentPane();

                content.setLayout( new BorderLayout() );

                content.add( getSplit(), BorderLayout.CENTER );
                content.add( getDownDockStation().getComponent(), BorderLayout.SOUTH );
                content.add( getRightDockStation().getComponent(), BorderLayout.EAST );
                content.add( getLeftDockStation().getComponent(), BorderLayout.WEST );
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
		if( !views.containsKey( frame ) ){
                        Dockable dockItem = new OrbisGISView( frame.getDockingParameters() );
			if( root == null || location == null ){
                            frontend.getDefaultStation().drop( dockItem );
			} else {
                            if( !root.drop( dockItem, location )){
                                    frontend.getDefaultStation().drop( dockItem );
                            }
			}
			views.put( frame, dockItem);
		}
		frontend.getController().setFocusedDockable( views.get(frame), false );
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
		if( station.equals(split)) {
			return "split";
                }
		if( station.equals(right)) {
			return "right";
                }
		if( station.equals(left)) {
			return "left";
                }
		if( station.equals(down)) {
			return "down";
                }
		if( station.equals(screen)) {
			return "screen";	
                }
		return null;
	}
}
