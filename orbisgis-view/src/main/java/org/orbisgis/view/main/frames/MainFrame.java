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

package org.orbisgis.view.main.frames;

import bibliothek.extension.gui.dock.preference.PreferenceTreeDialog;
import bibliothek.extension.gui.dock.preference.PreferenceTreeModel;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.beans.EventHandler;
import java.util.Locale;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import org.orbisgis.utils.I18N;
import org.orbisgis.view.components.menubar.MenuBarManager;
import org.orbisgis.view.components.menubar.MenuItemProperties;
import org.orbisgis.view.components.menubar.MenuProperties;
import org.orbisgis.view.docking.DockingManager;
import org.orbisgis.view.icons.OrbisGISIcon;

/**
 * Host of the {@link DockStation}s, this frame contain 
 * all other dockable frames
 */
public class MainFrame extends JFrame {
        //Main menu keys
        public final static String MENU_FILE = "file";
        public final static String MENU_EXIT = "exitapp";
        public final static String MENU_TOOLS = "tools";
        public final static String MENU_CONFIGURE = "configure";
        
        //The main frame show panels state,theme, and properties
        private DockingManager dockingManager=null;
        private MenuBarManager menuBar = new MenuBarManager();
        /**
	 * Creates a new frame. The content of the frame is not created by
	 * this constructor, clients must call {@link #setup(Core)}.
         * @param dockingManager 
	 */
	public MainFrame(){
		setTitle( "OrbisGIS "
                        + I18N.getString("orbisgis.org.orbisgis.version") + " - " + I18N.getString("orbisgis.org.orbisgis.versionName") + " - " + Locale.getDefault().getCountry() );
                setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
		setIconImage(OrbisGISIcon.getIconImage("mini_orbisgis")); 
                createMenu();
                this.setJMenuBar(menuBar.getRootBar());
	}

        public void setDockingManager(DockingManager dockingManager) {
            this.dockingManager = dockingManager;            
        }
        /**
         * Create the built-ins menu items
         */
        private void createMenu() {
            //File menu
            menuBar.addMenu("", new MenuProperties(MENU_FILE, new JMenu(I18N.getString("orbisgis.view.main.frames.menu.file"))));
            //Add exit item
            JMenuItem exitMenu = new JMenuItem(I18N.getString("orbisgis.view.main.frames.menu.exitapp"),OrbisGISIcon.getIcon("exit"));
            exitMenu.addActionListener(EventHandler.create(ActionListener.class,this,"onMenuExitApplication"));
            menuBar.addMenuItem(MENU_FILE,new MenuItemProperties(MENU_EXIT,exitMenu));
            //Add the tools menu
            menuBar.addMenu("", new MenuProperties(MENU_TOOLS, new JMenu(I18N.getString("orbisgis.view.main.frames.menu.tools"))));
            //Add preferencies menu item
            JMenuItem preferenciesMenu = new JMenuItem(I18N.getString("orbisgis.view.main.frames.menu.preferencies"),OrbisGISIcon.getIcon("preferences-system"));
            preferenciesMenu.addActionListener(EventHandler.create(ActionListener.class,this,"onMenuShowPreferencies"));
            menuBar.addMenuItem(MENU_TOOLS, new MenuItemProperties(MENU_TOOLS, preferenciesMenu));
            
        }
        /**
         * 
         * @return The menubar manager
         */
        public MenuBarManager getMenuBarManager() {
            return menuBar;
        }
        /**
         * The user click on preferencies menu item
         */
        public void onMenuShowPreferencies() {
            dockingManager.showPreferenceDialog();
        }
        /**
         * The user click on exit application menu item
         */
        public void onMenuExitApplication() {
            this.processWindowEvent(new WindowEvent(this,WindowEvent.WINDOW_CLOSING));
        }
        
}
