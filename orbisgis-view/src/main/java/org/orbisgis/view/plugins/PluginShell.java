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
package org.orbisgis.view.plugins;

import java.awt.BorderLayout;
import java.io.PrintStream;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import org.apache.felix.shell.ShellService;
import org.apache.log4j.Logger;
import org.orbisgis.view.docking.DockingPanel;
import org.orbisgis.view.docking.DockingPanelParameters;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * OSGI Shell command GUI. GUI access to the Apache Felix Shell service.
 * @url http://felix.apache.org/site/apache-felix-shell.html
 * @author Nicolas Fortin
 */
public class PluginShell extends JPanel implements DockingPanel {
        private static final Logger LOGGER = Logger.getLogger("gui."+PluginShell.class);
        private DockingPanelParameters parameters = new DockingPanelParameters();
        private static final I18n I18N = I18nFactory.getI18n(PluginShell.class);
        private final BundleContext hostBundle;
        private JTextField commandField = new JTextField();
        private JTextPane outputField = new JTextPane();
        private ServiceTracker tracker;
        private ShellService shellEngine = new EmptyEngine();

        public PluginShell(final BundleContext hostBundle) {
                super(new BorderLayout());
                this.hostBundle = hostBundle;
                tracker = new ShellServiceTracker(hostBundle);
                tracker.open();
                parameters.setName("plugin-shell");
                parameters.setTitle(I18N.tr("Plugin Shell"));
                outputField.setEditable(false);
                outputField.setText(I18N.tr("Awaiting plugin shell.."));
                commandField.setEditable(false);
                // Initialising components
                // The shell is composed by a logging part and a command line part
                add(outputField, BorderLayout.CENTER);
                add(commandField, BorderLayout.SOUTH);
                SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                                try {
                                        Bundle shellBundle = hostBundle.installBundle("osgi-shell.jar", getClass().getResourceAsStream("org.apache.felix.shell.jar"));
                                        shellBundle.start();
                                } catch(BundleException ex) {
                                        LOGGER.error("Cannot install shell bundle",ex);
                                }
                        }
                });
        }
        
        /**
         * shellEngine is properly set, initialise some components
         */
        private void onEngineReady() {
                commandField.setEditable(true);    
                outputField.setText(I18N.tr("Plugin shell, type \"help\" for command list."));
        }

        @Override
        public DockingPanelParameters getDockingParameters() {
                return parameters;
        }

        @Override
        public JComponent getComponent() {
                return this;
        }
        
        private class ShellServiceTracker extends ServiceTracker<ShellService, Integer> {

                private int trackedServiceCount = 0;

                public ShellServiceTracker(BundleContext context) {
                        super(context, ShellService.class, null);
                }

                @Override
                public Integer addingService(ServiceReference<ShellService> reference) {
                        if (trackedServiceCount == 0) {
                                shellEngine = hostBundle.getService(reference);
                                onEngineReady();
                        }
                        return ++trackedServiceCount;
                }

                @Override
                public void modifiedService(ServiceReference<ShellService> reference, Integer service) {
                        if (service == 1) {
                                shellEngine = hostBundle.getService(reference);
                        }
                }

                @Override
                public void removedService(ServiceReference<ShellService> reference, Integer service) {
                        if (service == 1) {
                                shellEngine = new EmptyEngine();
                        }
                        trackedServiceCount--;
                }
        }
        
        /**
         * Use this empty engine when the OSGI engine is not available
         */
        private class EmptyEngine implements ShellService {

                @Override
                public String[] getCommands() {
                        return new String[0];
                }

                @Override
                public String getCommandUsage(String string) {
                        return "";
                }

                @Override
                public String getCommandDescription(String string) {
                        return "";
                }

                @Override
                public ServiceReference getCommandReference(String string) {
                        return null;
                }

                @Override
                public void executeCommand(String string, PrintStream stream, PrintStream stream1) throws Exception {
                }
        }
}
