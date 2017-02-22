/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.view.main;

import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import org.apache.log4j.Logger;
import org.orbisgis.core.workspace.CoreWorkspaceImpl;
import org.orbisgis.view.main.frames.LoadingFrame;

/**
 * Hold an instance of Core and is able to re-launch this interface.
 * Listen to workspace folder update.
 * @author Nicolas Fortin
 */
public class CoreLauncher {
        // Listening to workspace change
        private static final Logger LOGGER = Logger.getLogger(CoreLauncher.class);
        private boolean debugMode;
        private CoreWorkspaceImpl coreWorkspace = new CoreWorkspaceImpl();
        private PropertyChangeListener workspaceChangeListener = EventHandler.create(PropertyChangeListener.class, this, "onWorkspaceChange");
        private Core viewCore;
        private AtomicBoolean restartingOrbisGIS = new AtomicBoolean(false);

        private static LoadingFrame showLoadingFrame() {
                final LoadingFrame loadingFrame = new LoadingFrame();
                SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                                loadingFrame.setVisible(true);
                        }
                });
                return loadingFrame;
        }

        /**
         * The property workspace folder of CoreWorkspaceImpl has been changed
         */
        public void onWorkspaceChange() {
                if(!restartingOrbisGIS.getAndSet(true)) {
                        SwingWorker worker = new SwingWorker() {
                                @Override
                                protected Object doInBackground() throws Exception {
                                        try {
                                                if (viewCore.shutdown(false)) {
                                                        launch();
                                                }
                                        } finally {
                                                restartingOrbisGIS.set(false);
                                        }
                                        return null;
                                }

                            @Override
                            public String toString() {
                                return "CoreLauncher#onWorkspaceChange";
                            }
                        };
                        worker.execute();
                }
        }

        public void init(boolean debugMode) {
                this.debugMode = debugMode;
                coreWorkspace.addPropertyChangeListener(CoreWorkspaceImpl.PROP_WORKSPACEFOLDER, workspaceChangeListener);
        }
        private static void stopApplication(final LoadingFrame loadingFrame) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    loadingFrame.dispose();
                    System.exit(0);
                }
            });

        }
        /**
         * Create the view component and set visible
         */
        public void launch() {
            // Load splash screen
            final LoadingFrame loadingFrame = showLoadingFrame();
            boolean showWorkspaceSelectionDialog = true;
            while(showWorkspaceSelectionDialog) {
                showWorkspaceSelectionDialog = false;
                try {
                    viewCore = new Core(coreWorkspace, debugMode, loadingFrame);
                    viewCore.startup(loadingFrame.getProgressMonitor());
                } catch (InterruptedException ex) {
                    // Do not print user cancel action.
                    // Close the splash screen
                    LOGGER.info("Loading of OrbisGIS canceled by user.");
                    stopApplication(loadingFrame);
                } catch (Exception ex) {
                    LOGGER.error(ex.getLocalizedMessage(),ex);
                    try {
                        coreWorkspace.setDefaultWorkspace(null);
                        coreWorkspace.setWorkspaceFolder(null);
                        if(viewCore != null) {
                            viewCore.dispose();
                        }
                        showWorkspaceSelectionDialog = true;
                    } catch (IOException ioex) {
                        LOGGER.error(ioex.getLocalizedMessage(), ioex);
                        stopApplication(loadingFrame);
                    }
                }
            }
        }
}
