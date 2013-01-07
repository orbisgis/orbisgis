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
import javax.swing.*;

import org.orbisgis.core.workspace.CoreWorkspace;
import org.orbisgis.view.components.actions.ActionCommands;
import org.orbisgis.view.components.actions.DefaultAction;
import org.orbisgis.view.docking.DockingManager;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.main.frames.ext.MainFrameAction;
import org.orbisgis.view.main.frames.ext.MainWindow;
import org.orbisgis.view.workspace.ViewWorkspace;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Main window that contain all docking panels.
 */
public class MainFrame extends JFrame implements MainWindow {
        private static final I18n I18N = I18nFactory.getI18n(MainFrame.class);
        
        //The main frame addDockingPanel panels state,theme, and properties
        private DockingManager dockingManager=null;
        private ActionCommands actions = new ActionCommands();
        private JMenuBar menuBar = new JMenuBar();
        /**
         * Creates a new frame. The content of the frame is not created by
         * this constructor, clients must call {@link #init()}.
         */
        public MainFrame(){
                    getContentPane().setLayout(new BorderLayout());
            setTitle( I18N.tr("OrbisGIS version {0} {1} {2}",
                            getVersion(),ViewWorkspace.CITY_VERSION,Locale.getDefault().getCountry()));
                    setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
            setIconImage(OrbisGISIcon.getIconImage("mini_orbisgis"));
        }
        
        public void init() {
                initActions();
                // Add actions in menu bar
                actions.registerContainer(menuBar);
                this.setJMenuBar(menuBar);
                getContentPane().add(new MainFrameStatusBar(this),BorderLayout.SOUTH);                
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

            // Add Window close menu
            menuBar.add(dockingManager.getCloseableDockableMenu());
            // Add l&f menu
            MenuElement toolsMenu = actions.getActionMenu(MainFrameAction.MENU_TOOLS,menuBar.getSubElements());
            if(toolsMenu !=null && toolsMenu instanceof JMenu) {
                ((JMenu)toolsMenu).add(dockingManager.getLookAndFeelMenu());
            }
        }

        /**
         * Create the built-ins menu items
         */
        private void initActions() {
            actions.addAction(new DefaultAction(MainFrameAction.MENU_FILE,I18N.tr("&File")).setMenuGroup(true));
            actions.addAction(new DefaultAction(MainFrameAction.MENU_EXIT, I18N.tr("&Exit"), OrbisGISIcon.getIcon("exit"),
                    EventHandler.create(ActionListener.class, this, "onMenuExitApplication"))
                    .setParent(MainFrameAction.MENU_FILE));

            actions.addAction(new DefaultAction(MainFrameAction.MENU_TOOLS,I18N.tr("&Tools")).setMenuGroup(true));
            actions.addAction(new DefaultAction(MainFrameAction.MENU_CONFIGURE,I18N.tr("&Configuration"),
                    OrbisGISIcon.getIcon("preferences-system"),
                    EventHandler.create(ActionListener.class,this,"onMenuShowPreferences"))
                    .setParent(MainFrameAction.MENU_TOOLS));

        }

        /**
         * The user click on preferences menu item
         */
        public void onMenuShowPreferences() {
            dockingManager.showPreferenceDialog();
        }

        /**
         * The user click on exit application menu item
         */
        public void onMenuExitApplication() {
            this.processWindowEvent(new WindowEvent(this,WindowEvent.WINDOW_CLOSING));
        }
        
}
