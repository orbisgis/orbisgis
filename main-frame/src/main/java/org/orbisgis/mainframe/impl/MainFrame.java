/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
package org.orbisgis.mainframe.impl;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.beans.EventHandler;
import java.util.Locale;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayer;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.MenuElement;

import org.orbisgis.frameworkapi.CoreWorkspace;
import org.orbisgis.mainframe.api.MainFrameAction;
import org.orbisgis.mainframe.api.MainWindow;
import org.orbisgis.mainframe.icons.MainFrameIcon;
import org.orbisgis.sif.components.actions.ActionCommands;
import org.orbisgis.sif.components.actions.DefaultAction;

import org.orbisgis.sif.docking.DockingManager;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.log.LogReaderService;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Main window that contain all docking panels.
 */
@Component(service = MainWindow.class)
public class MainFrame extends JFrame implements MainWindow {
    private static final I18n I18N = I18nFactory.getI18n(MainFrame.class);

    //The main frame addDockingPanel panels state,theme, and properties
    private DockingManager dockingManager = null;
    private ActionCommands actions = new ActionCommands();
    private JMenuBar menuBar = new JMenuBar();
    private MainFrameStatusBar mainFrameStatusBar = new MainFrameStatusBar();
    private JPanel mainPanel = new JPanel(new BorderLayout());
    private LogListenerOverlay messageOverlay = new LogListenerOverlay();

    /**
     * Creates a new frame. The content of the frame is not created by
     * this constructor, clients must call {@link #init}.
     */
    public MainFrame() {
        setIconImage(MainFrameIcon.getIconImage("orbisgis"));
        add(new JLayer<>(mainPanel, messageOverlay));
    }

    @Reference
    public void setCoreWorkspace(CoreWorkspace coreWorkspace) {
        setTitle(I18N.tr("OrbisGIS version {0} {1} {2}", getVersion(coreWorkspace), coreWorkspace.getVersionQualifier
                (), Locale.getDefault().getCountry()));
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        mainFrameStatusBar.init(coreWorkspace);
    }
    public void unsetCoreWorkspace(CoreWorkspace coreWorkspace) {

    }

    @Override
    protected void addImpl(java.awt.Component comp, Object constraints, int index) {
        if (mainPanel == null || comp instanceof JLayer) {
            super.addImpl(comp, constraints, index);
        } else {
            mainPanel.add(comp, constraints, index);
        }
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addMenuItem(MainFrameAction mainFrameAction) {
        actions.addActionFactory(mainFrameAction, this);
    }

    public void removeMenuItem(MainFrameAction mainFrameAction) {
        actions.removeActionFactory(mainFrameAction);
    }

    public void init() {
        initActions();
        // Add actions in menu bar
        actions.registerContainer(menuBar);
        this.setJMenuBar(menuBar);
        getContentPane().add(mainFrameStatusBar, BorderLayout.SOUTH);
    }

    public static String getVersion(CoreWorkspace coreWorkspace) {
        if (coreWorkspace.getVersionRevision() != 0) {
            return coreWorkspace.getVersionMajor() + "." + coreWorkspace.getVersionMinor();
        } else {
            return coreWorkspace.getVersionMajor() + "." + coreWorkspace.getVersionMinor() + "." + coreWorkspace
                    .getVersionRevision();
        }
    }

    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC)
    public void setLogReaderService(LogReaderService logReaderService) {
        logReaderService.addLogListener(messageOverlay);
    }

    public void unsetLogReaderService(LogReaderService logReaderService) {
        logReaderService.removeLogListener(messageOverlay);
    }

    @Reference
    public void setDockingManager(DockingManager dockingManager) {
        this.dockingManager = dockingManager;

        // Add Window close menu
        menuBar.add(dockingManager.getCloseableDockableMenu());
        // Add l&f menu
        MenuElement toolsMenu = actions.getActionMenu(MainFrameAction.MENU_TOOLS, menuBar.getSubElements());
        if (toolsMenu != null && toolsMenu instanceof JMenu) {
            ((JMenu) toolsMenu).add(dockingManager.getLookAndFeelMenu());
        }
    }

    public void unsetDockingManager(DockingManager dockingManager) {
        menuBar.removeAll();
        this.dockingManager = null;
    }

    /**
     * Create the built-ins menu items
     */
    private void initActions() {
        actions.addAction(new DefaultAction(MainFrameAction.MENU_FILE, I18N.tr("&File")).setMenuGroup(true));
        actions.addAction(new DefaultAction(MainFrameAction.MENU_EXIT, I18N.tr("&Exit"), MainFrameIcon.getIcon
                ("exit"), EventHandler.create(ActionListener.class, this, "onMenuExitApplication")).setParent
                (MainFrameAction.MENU_FILE));
        actions.addAction(new DefaultAction(MainFrameAction.MENU_TOOLS, I18N.tr("&Tools")).setMenuGroup(true));
        actions.addAction(new DefaultAction(MainFrameAction.MENU_CONFIGURE, I18N.tr("&Configuration"), MainFrameIcon
                .getIcon("preferences-system"), EventHandler.create(ActionListener.class, this,
                "onMenuShowPreferences")).setParent(MainFrameAction.MENU_TOOLS));
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
        this.processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    @Override
    public JFrame getMainFrame() {
        return this;
    }

    /**
     * Extend the mainframe with a new menu
     *
     * @param action
     */
    public void addMenu(Action action) {
        actions.addAction(action);
    }

    /**
     * Extend the main status bar with a new component
     *
     * @param component
     * @param orientation
     */
    public void addToolBarComponent(JComponent component, String orientation) {
        mainFrameStatusBar.addComponent(component, orientation);
    }


}
