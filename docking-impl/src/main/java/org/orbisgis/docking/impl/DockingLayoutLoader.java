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
package org.orbisgis.docking.impl;

import org.orbisgis.sif.docking.DockingManager;
import org.orbisgis.sif.docking.DockingPanel;
import org.orbisgis.sif.edition.EditorDockable;
import org.orbisgis.sif.edition.EditorFactory;
import org.orbisgis.wkguiapi.ViewWorkspace;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Apply docking layout to DockingManager using user Preferences
 * @author Nicolas Fortin
 */
@Component(immediate = true)
public class DockingLayoutLoader {
    private ViewWorkspace viewWorkspace;
    private DockingManager dockingManager;
    private AtomicLong lastDockableUpdate = new AtomicLong(0);
    private AtomicBoolean watchDockableUpdateThread = new AtomicBoolean(false);
    private boolean initialised = false;

    @Reference
    public void setViewWorkspace(ViewWorkspace viewWorkspace) {
        this.viewWorkspace = viewWorkspace;
    }

    public void unsetViewWorkspace(ViewWorkspace viewWorkspace) {
        this.viewWorkspace = null;
    }

    @Reference
    public void setDockingManager(DockingManager dockingManager) {
        this.dockingManager = dockingManager;
    }

    public void unsetDockingManager(DockingManager dockingManager) {
        this.dockingManager = null;
    }

    @Activate
    public void init() {
        initialised = true;
        refreshConfig();
    }

    public void deactivate() {
        initialised = false;
    }


    public void refreshConfig() {
        if(initialised) {
            SwingUtilities.invokeLater(new ReloadLayout(dockingManager, viewWorkspace.getDockingLayoutPath()));
        }
    }

    private void updateDockableState() {
        if(initialised) {
            lastDockableUpdate.set(System.currentTimeMillis());
            if (!watchDockableUpdateThread.getAndSet(true)) {
                LoadDockingLayout loadDockingLayout = new LoadDockingLayout(lastDockableUpdate, watchDockableUpdateThread, this);
                loadDockingLayout.execute();
            }
        }
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC, policyOption =
            ReferencePolicyOption.GREEDY)
    public void addEditor(EditorDockable editor) {
        updateDockableState();
    }

    public void removeEditor(EditorDockable editor) {

    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC, policyOption =
            ReferencePolicyOption.GREEDY)
    public void addEditorFactory(EditorFactory editorFactory) {
        updateDockableState();
    }

    public void removeEditorFactory(EditorFactory editorFactory) {

    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC, policyOption =
            ReferencePolicyOption.GREEDY)
    public void addDockingPanel(DockingPanel frame) {
        updateDockableState();
    }
    public void removeDockingPanel(DockingPanel frame) {

    }

    private static class LoadDockingLayout extends SwingWorker {
        private AtomicLong lastDockableUpdate;
        private AtomicBoolean watchDockableUpdateThread;
        private static final long MINIMAL_WAIT_TIME = 500;
        private DockingLayoutLoader dockingLayoutLoader;

        public LoadDockingLayout(AtomicLong lastDockableUpdate, AtomicBoolean watchDockableUpdateThread,
                                 DockingLayoutLoader dockingLayoutLoader) {
            this.lastDockableUpdate = lastDockableUpdate;
            this.watchDockableUpdateThread = watchDockableUpdateThread;
            this.dockingLayoutLoader = dockingLayoutLoader;
        }

        @Override
        protected Object doInBackground() throws Exception {
            try {
                while (lastDockableUpdate.get() + MINIMAL_WAIT_TIME > System.currentTimeMillis()) {
                    Thread.sleep(MINIMAL_WAIT_TIME / 2);
                }
                return null;
            } finally {
                watchDockableUpdateThread.set(false);
            }
        }

        @Override
        protected void done() {
            dockingLayoutLoader.refreshConfig();
        }
    }

    private static class ReloadLayout implements Runnable {
        private DockingManager dockingManager;
        private String dockingLayoutPath;

        public ReloadLayout(DockingManager dockingManager, String dockingLayoutPath) {
            this.dockingManager = dockingManager;
            this.dockingLayoutPath = dockingLayoutPath;
        }

        @Override
        public void run() {
            dockingManager.setDockingLayoutPersistanceFilePath(dockingLayoutPath);
        }
    }
}
