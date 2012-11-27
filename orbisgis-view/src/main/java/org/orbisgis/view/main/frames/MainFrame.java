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
package org.orbisgis.view.main.frames;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.beans.EventHandler;
import java.util.Locale;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.orbisgis.core.workspace.CoreWorkspace;
import org.orbisgis.view.components.menubar.MenuBarManager;
import org.orbisgis.view.components.menubar.MenuItemProperties;
import org.orbisgis.view.components.menubar.MenuProperties;
import org.orbisgis.view.docking.DockingManager;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.workspace.ViewWorkspace;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Host of the {@link DockStation}s, this frame contain 
 * all other dockable frames.
 */
public class MainFrame extends JFrame {
        private static final I18n I18N = I18nFactory.getI18n(MainFrame.class);
        //Main menu keys
        public static final String MENU_FILE = "file";
        public static final String MENU_EXIT = "exitapp";
        public static final String MENU_TOOLS = "tools";
        public static final String MENU_CONFIGURE = "configure";
        public static final String MENU_LOOKANDFEEL = "lookAndFeel";
        public static final String MENU_WINDOWS = "windows";
        
        //The main frame show panels state,theme, and properties
        private DockingManager dockingManager=null;
        private MenuBarManager menuBar = new MenuBarManager();
        /**
	 * Creates a new frame. The content of the frame is not created by
	 * this constructor, clients must call {@link #setup(Core)}.
         * @param dockingManager 
	 */
	public MainFrame(){
                getContentPane().setLayout(new BorderLayout());
		setTitle( I18N.tr("OrbisGIS version {0} {1} {2}",
                        getVersion(),ViewWorkspace.CITY_VERSION,Locale.getDefault().getCountry()));
                setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
		setIconImage(OrbisGISIcon.getIconImage("mini_orbisgis")); 
	}
        
        public void init() {
                createMenu();
                this.setJMenuBar(menuBar.getRootBar());
                getContentPane().add(new MainFrameStatusBar(),BorderLayout.SOUTH);                
        }

        public static String getVersion() {
                if(CoreWorkspace.REVISION_VERSION!=0) {
                        return CoreWorkspace.MAJOR_VERSION+"."+CoreWorkspace.MINOR_VERSION;
                } else {
                        return CoreWorkspace.MAJOR_VERSION+"."+CoreWorkspace.MINOR_VERSION+"."+CoreWorkspace.REVISION_VERSION;
                }
        }
        public void setDockingManager(DockingManager dockingManager) {
            this.dockingManager = dockingManager;
            //Add Look And Feel menu
            menuBar.addMenu(MENU_TOOLS, new MenuProperties(MENU_LOOKANDFEEL, dockingManager.getLookAndFeelMenu()));      
            //Add the window menu
            menuBar.addMenu("", new MenuProperties(MENU_WINDOWS,dockingManager.getCloseableDockableMenu()));
        }
        /**
         * Create the built-ins menu items
         */
        private void createMenu() {
            //File menu
            menuBar.addMenu("", new MenuProperties(MENU_FILE, new JMenu(I18N.tr("&File"))));
            //Add exit item
            JMenuItem exitMenu = new JMenuItem(I18N.tr("&Exit"),OrbisGISIcon.getIcon("exit"));
            exitMenu.addActionListener(EventHandler.create(ActionListener.class,this,"onMenuExitApplication"));
            menuBar.addMenuItem(MENU_FILE,new MenuItemProperties(MENU_EXIT,exitMenu));
            //Add the tools menu
            menuBar.addMenu("", new MenuProperties(MENU_TOOLS, new JMenu(I18N.tr("&Tools"))));
            //Add preferencies menu item
            JMenuItem preferenciesMenu = new JMenuItem(I18N.tr("&Configuration"),OrbisGISIcon.getIcon("preferences-system"));
            preferenciesMenu.addActionListener(EventHandler.create(ActionListener.class,this,"onMenuShowPreferencies"));
            menuBar.addMenuItem(MENU_TOOLS, new MenuItemProperties(MENU_TOOLS, preferenciesMenu));
        }
        /**
         * 
         * @return The menu bar manager
         */
        public MenuBarManager getMenuBarManager() {
            return menuBar;
        }
        /**
         * The user click on preferences menu item
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
