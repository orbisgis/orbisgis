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

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.beans.EventHandler;
import java.util.Locale;
import javax.swing.*;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.varia.DenyAllFilter;
import org.apache.log4j.varia.LevelMatchFilter;
import org.apache.log4j.varia.LevelRangeFilter;
import org.orbisgis.core.workspace.CoreWorkspace;
import org.orbisgis.view.components.actions.ActionCommands;
import org.orbisgis.view.components.actions.DefaultAction;
import org.orbisgis.view.components.actions.MenuItemServiceTracker;
import org.orbisgis.view.docking.DockingManager;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.main.frames.ext.MainFrameAction;
import org.orbisgis.view.main.frames.ext.MainWindow;
import org.orbisgis.view.workspace.ViewWorkspace;
import org.osgi.framework.BundleContext;
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
        private MenuItemServiceTracker<MainWindow,MainFrameAction> menuBarActionTracker;
        private MainFrameStatusBar mainFrameStatusBar = new MainFrameStatusBar(this);
        private JPanel mainPanel = new JPanel(new BorderLayout());
        private MessageOverlay messageOverlay = new MessageOverlay();
        private OverlayLoggerTarget guiLoggerTarget = new OverlayLoggerTarget(messageOverlay);
        private OverlayLoggerTarget popupLoggerTarget = new OverlayLoggerTarget(messageOverlay);
        private OverlayLoggerTarget errorLoggerTarget = new OverlayLoggerTarget(messageOverlay);

        /**
         * Creates a new frame. The content of the frame is not created by
         * this constructor, clients must call {@link #init}.
         */
        public MainFrame(){
            setTitle(I18N.tr("OrbisGIS version {0} {1} {2}",
                    getVersion(), ViewWorkspace.CITY_VERSION, Locale.getDefault().getCountry()));
                    setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
            setIconImage(OrbisGISIcon.getIconImage("mini_orbisgis"));
            add(new JLayer<>(mainPanel, messageOverlay));
        }

        @Override
        public void dispose() {
            try {
                if(menuBarActionTracker!=null) {
                    menuBarActionTracker.close();
                }
                guiLoggerTarget.disposeLogger();
                popupLoggerTarget.disposeLogger();
                errorLoggerTarget.disposeLogger();
            } finally {
                super.dispose();
            }
        }

        @Override
        protected void addImpl(Component comp, Object constraints, int index) {
            if(mainPanel==null || comp instanceof JLayer) {
                super.addImpl(comp, constraints, index);
            } else {
                mainPanel.add(comp, constraints, index);
            }
        }

        public void init(BundleContext context) {
            initActions();
            // Add actions in menu bar
            actions.registerContainer(menuBar);
            this.setJMenuBar(menuBar);
            getContentPane().add(mainFrameStatusBar, BorderLayout.SOUTH);
            // Track for new menu items
            menuBarActionTracker = new MenuItemServiceTracker<MainWindow, MainFrameAction>(context, MainFrameAction.class, actions, this);
            menuBarActionTracker.open();
            mainFrameStatusBar.init();



            // Init link between LOG4J and MessageOverlay system.
            // Root logger, from fatal to warning
            LevelRangeFilter filter = new LevelRangeFilter();
            filter.setLevelMax(Level.FATAL);
            filter.setLevelMin(Level.WARN);
            filter.setAcceptOnMatch(true);
            errorLoggerTarget.addFilter(filter);
            LevelMatchFilter guiFilter = new LevelMatchFilter();
            guiFilter.setLevelToMatch(Level.INFO.toString());
            // gui
            guiLoggerTarget.addFilter(guiFilter);
            guiLoggerTarget.addFilter(new DenyAllFilter());
            guiLoggerTarget.initLogger(Logger.getLogger("gui.popup"));
            popupLoggerTarget.addFilter(guiFilter);
            popupLoggerTarget.addFilter(new DenyAllFilter());
            popupLoggerTarget.initLogger(Logger.getLogger("popup"));
            errorLoggerTarget.initLogger(Logger.getRootLogger());
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

        @Override
        public JFrame getMainFrame() {
            return this;
        }
        
        /**
         * Extend the mainframe with a new menu
         * @param action 
         */
        public void addMenu(Action action){
            actions.addAction(action);
        }
        
        /**
         * Extend the main status bar with a new component
         * @param component
         * @param orientation 
         */
        public void addToolBarComponent(JComponent component, String orientation){
            mainFrameStatusBar.addComponent(component, orientation);
        }
        
       
}
