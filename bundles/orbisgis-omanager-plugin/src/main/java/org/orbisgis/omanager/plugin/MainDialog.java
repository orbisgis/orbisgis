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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.beans.EventHandler;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionListener;
import org.apache.felix.shell.gui.Plugin;
import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * @author Nicolas Fortin
 */
public class MainDialog extends JDialog implements ServiceTrackerCustomizer<Plugin,Plugin> {
    private enum SWING_PLUGIN_JOB { ADD, REMOVE, UPDATE };
    private static final Dimension DEFAULT_DIMENSION = new Dimension(800,480);
    private static final I18n I18N = I18nFactory.getI18n(MainDialog.class);
    private static final Logger LOGGER = Logger.getLogger("gui."+MainDialog.class);
    private JList shellPlugins = new JList();
    private List<Plugin> loadedPlugins = new ArrayList<Plugin>();
    private BundleContext bundleContext;
    private JPanel centerComponent = new JPanel(new BorderLayout());
    private ListSelectionListener modelListener = EventHandler.create(ListSelectionListener.class,this,"onShellSelectionChange");

    /**
     * @param frame MainFrame, in order to place this dialog and release resource automatically.
     * @param bundleContext
     */
    public MainDialog(Frame frame, BundleContext bundleContext) {
        super(frame);
        this.bundleContext = bundleContext;
        JPanel contentPane = new JPanel(new BorderLayout());
        shellPlugins.setVisible(false);
        contentPane.add(shellPlugins,BorderLayout.WEST);
        contentPane.add(centerComponent,BorderLayout.CENTER);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setContentPane(contentPane);
        setSize(DEFAULT_DIMENSION);
        setTitle(I18N.tr("Plug-ins manager"));
        shellPlugins.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    /**
     * Called when the user select another shell on the left list.
     */
    public void onShellSelectionChange() {
        if(shellPlugins.isVisible()) {
            centerComponent.removeAll();
            int selected = shellPlugins.getSelectedIndex();
            if(selected >= 0 && selected < loadedPlugins.size()) {
                centerComponent.add(loadedPlugins.get(shellPlugins.getSelectedIndex()).getGUI(),BorderLayout.CENTER);
            }
            centerComponent.updateUI();
        }
    }
    public Plugin addingService(ServiceReference<Plugin> reference) {
        try {
            return doProcess(new SwingPluginProcess(SWING_PLUGIN_JOB.ADD,reference));
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(),e);
            return null;
        }
    }

    public void modifiedService(ServiceReference<Plugin> reference, Plugin service) {
        try {
            doProcess(new SwingPluginProcess(service,SWING_PLUGIN_JOB.UPDATE,reference));
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(),e);
        }
    }

    public void removedService(ServiceReference<Plugin> reference, Plugin service) {
        try {
            doProcess(new SwingPluginProcess(service,SWING_PLUGIN_JOB.REMOVE,reference));
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(),e);
        }
    }
    private void updateLoadedPluginsList() {
        DefaultListModel pluginNames = new DefaultListModel();
        for(Plugin plugin : loadedPlugins) {
            pluginNames.addElement(plugin.getName());
        }
        shellPlugins.setModel(pluginNames);
        // Set selection
        shellPlugins.addListSelectionListener(modelListener);
        shellPlugins.setVisible(pluginNames.size()>1);
    }
    private Plugin doProcess(SwingPluginProcess process) throws InvocationTargetException, InterruptedException {
        if(SwingUtilities.isEventDispatchThread()) {
            process.run();
            return process.getPlugin();
        } else {
            SwingUtilities.invokeAndWait(process);
            return process.getPlugin();
        }
    }

    /**
     * Manage services on the swing thread through this runnable.
     */
    private class SwingPluginProcess implements Runnable {
        private Plugin plugin;
        private SWING_PLUGIN_JOB job;
        private ServiceReference<Plugin> reference;

        private SwingPluginProcess(Plugin plugin, SWING_PLUGIN_JOB job, ServiceReference<Plugin> reference) {
            this.plugin = plugin;
            this.job = job;
            this.reference = reference;
        }
        /**
         * Only for Add
         * @param job
         * @param reference
         */
        private SwingPluginProcess(SWING_PLUGIN_JOB job, ServiceReference<Plugin> reference) {
            this.job = job;
            this.reference = reference;
        }
        private void doRemove() {
            loadedPlugins.remove(plugin);
            Object selected = shellPlugins.getSelectedValue();
            if((selected!=null && selected.equals(plugin.getName())) || loadedPlugins.isEmpty()) {
                centerComponent.removeAll();
                centerComponent.updateUI();
            }
            updateLoadedPluginsList();
            if(loadedPlugins.size()==1) {
                centerComponent.add(loadedPlugins.get(0).getGUI(),BorderLayout.CENTER);
            }
            centerComponent.updateUI();
        }
        private void doAdd() {
            plugin = bundleContext.getService(reference);
            loadedPlugins.add(plugin);
            updateLoadedPluginsList();
            if(!shellPlugins.isVisible()) {
                centerComponent.add(plugin.getGUI(),BorderLayout.CENTER);
                centerComponent.updateUI();
                shellPlugins.setSelectedValue(plugin.getName(),true);
            }
        }
        public void run() {
            switch (job) {
                case REMOVE:
                    doRemove();
                    break;
                case UPDATE: //Update remove then add
                    doRemove();
                case ADD:
                    doAdd();
            }
        }

        public Plugin getPlugin() {
            return plugin;
        }
    }
}
