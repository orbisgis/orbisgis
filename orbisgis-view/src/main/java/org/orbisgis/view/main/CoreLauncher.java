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
package org.orbisgis.view.main;

import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import org.apache.log4j.Logger;
import org.orbisgis.core.workspace.CoreWorkspace;
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
        private CoreWorkspace coreWorkspace = new CoreWorkspace();
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
         * The property workspace folder of CoreWorkspace has been changed
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
                coreWorkspace.addPropertyChangeListener(CoreWorkspace.PROP_WORKSPACEFOLDER, workspaceChangeListener);
        }
        
        /**
         * Create the view component and set visible
         */
        public void launch() {
                // Load splash screen
                final LoadingFrame loadingFrame = showLoadingFrame();
                try {
                        viewCore = new Core(coreWorkspace, debugMode, loadingFrame);
                        viewCore.startup(loadingFrame);
                } catch (Throwable ex) {
                        LOGGER.error(ex.getLocalizedMessage(),ex);
                        // Close the splash screen
                        SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                        loadingFrame.dispose();
                                }
                        });
                }                
        }
}
