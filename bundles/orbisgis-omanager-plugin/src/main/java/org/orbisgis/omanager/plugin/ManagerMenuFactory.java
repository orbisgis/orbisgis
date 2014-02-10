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
package org.orbisgis.omanager.plugin;

import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.apache.felix.shell.gui.Plugin;
import org.apache.log4j.Logger;
import org.orbisgis.viewapi.components.actions.DefaultAction;
import org.orbisgis.viewapi.main.frames.ext.MainFrameAction;
import org.orbisgis.viewapi.main.frames.ext.MainWindow;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Create menu item and create the main menu when the user click on the menu item.
 * @author Nicolas Fortin
 */
public class ManagerMenuFactory implements MainFrameAction {
    private static final Logger LOGGER = Logger.getLogger("gui."+ManagerMenuFactory.class);
    public static final String MENU_MANAGE_PLUGINS = "A_MANAGE_PLUGINS";
    private static final I18n I18N = I18nFactory.getI18n(ManagerMenuFactory.class);
    private BundleContext bundleContext;
    private MainDialog mainPanel;
    private MainWindow target; // There is only one main window in the application, it can be stored here.
    private ServiceTracker<Plugin,Plugin> pluginTracker;

    /**
     * @param bundleContext Used to track OSGi bundle repository service, and to manage bundles.
     */
    public ManagerMenuFactory(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public List<Action> createActions(MainWindow target) {
        this.target = target;
        List<Action> actions = new ArrayList<Action>();
        actions.add(new DefaultAction(MENU_MANAGE_PLUGINS,I18N.tr("&Manage plugins"),
                new ImageIcon(ManagerMenuFactory.class.getResource("panel_icon.png")),
                EventHandler.create(ActionListener.class,this,"showManager")).setParent(MENU_TOOLS).setInsertFirst(true));
        return actions;
    }

    /**
     * Make and show the plug-ins manager
     */
    public void showManager() {
        if(mainPanel==null) {
            mainPanel = new MainDialog(target.getMainFrame(),bundleContext);
            mainPanel.setModal(false);
            // Track
            pluginTracker = new ServiceTracker<Plugin, Plugin>(bundleContext,Plugin.class,mainPanel);
            pluginTracker.open();
        }
        mainPanel.setVisible(true);
    }

    public void disposeActions(MainWindow target, List<Action> actions) {
        // Close the Dialog if created
        if(mainPanel != null) {
            pluginTracker.close();
            mainPanel.dispose();
        }
    }
}
