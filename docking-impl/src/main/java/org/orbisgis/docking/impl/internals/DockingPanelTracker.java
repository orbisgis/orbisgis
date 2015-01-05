/*
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
package org.orbisgis.docking.impl.internals;

import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.orbisgis.sif.docking.DockingManager;
import org.orbisgis.sif.docking.DockingPanel;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Show DockingPanel registered services.
 * When a bundle register the Service DockingPanel, this tracker catch the event
 * and register the provided DockingPanel instance to the DockingManager.
 * @author Nicolas Fortin
 */
public class DockingPanelTracker extends ServiceTracker<DockingPanel, String> {
        private static enum DP_EVT { ADDED, MODIFIED, REMOVED};
        private BundleContext hostContext;
        private DockingManager dockingManager;
        private static final Logger LOGGER = Logger.getLogger(DockingPanelTracker.class);

        public DockingPanelTracker(BundleContext context,DockingManager dockingManager) {
                super(context, DockingPanel.class, null);
                hostContext = context;
                this.dockingManager = dockingManager;
        }

        @Override
        public String addingService(ServiceReference<DockingPanel> reference) {
                return processOperation(new DockingPanelOperation(reference));
        }

        @Override
        public void modifiedService(ServiceReference<DockingPanel> reference, String dockId) {
                processOperation(new DockingPanelOperation(reference, dockId, DP_EVT.MODIFIED));
        }

        @Override
        public void removedService(ServiceReference<DockingPanel> reference, String dockId) {
                processOperation(new DockingPanelOperation(reference, dockId, DP_EVT.REMOVED));
        }
        private String processOperation(DockingPanelOperation operation) {
                if(SwingUtilities.isEventDispatchThread()) {
                        operation.run();
                } else {
                        try {
                                SwingUtilities.invokeAndWait(operation);
                        } catch(Exception ex) {
                                LOGGER.error(ex.getLocalizedMessage(),ex);
                        }
                }
                return operation.getDockId();
        }
        /**
         * Guaranty that the Add/Remove panel operation is done on swing thread.
         */
        private class DockingPanelOperation implements Runnable {
                private ServiceReference<DockingPanel> reference;
                private String dockId;
                private DP_EVT operation;

                public DockingPanelOperation(ServiceReference<DockingPanel> reference, String dockId, DP_EVT operation) {
                        this.reference = reference;
                        this.dockId = dockId;
                        this.operation = operation;
                }
                public DockingPanelOperation(ServiceReference<DockingPanel> reference) {
                        this.reference = reference;
                        this.dockId = null;
                        this.operation = DP_EVT.ADDED;
                }
                /**
                 * @return Docking Panel identifier
                 */
                public String getDockId() {
                        return dockId;
                }
                
                @Override
                public void run() {
                        switch(operation) {
                                case REMOVED:
                                        dockingManager.removeDockingPanel(dockId);
                                        break;
                                case MODIFIED: //Remove then Add
                                        dockingManager.removeDockingPanel(dockId);                                        
                                case ADDED:
                                        DockingPanel panel = hostContext.getService(reference);
                                        dockId = dockingManager.addDockingPanel(panel);
                                        break;
                        }
                }                
        }
}
