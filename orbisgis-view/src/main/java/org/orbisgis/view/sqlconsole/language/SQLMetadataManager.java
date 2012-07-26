/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.sqlconsole.language;

import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.Metadata;
import org.gdms.driver.Driver;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileDriver;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.source.CommitListener;
import org.gdms.source.SourceEvent;
import org.gdms.source.SourceListener;
import org.gdms.source.SourceRemovalEvent;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.view.background.BackgroundJob;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.background.UniqueJobID;

/**
 * Handles all Metadata-related caching for the {@link SQLLanguageSupport} class.
 * @author Antoine Gourlay
 */
public class SQLMetadataManager implements SourceListener {

        private DataManager dataManager;
        private BackgroundManager bm;
        // cached metadata
        private final Map<String, Metadata> cachedMetadatas = Collections.synchronizedMap(new TreeMap<String, Metadata>());
        // source loading & thread synchronisation
        private final BlockingDeque<String> sourcesToLoad = new LinkedBlockingDeque<String>();
        private Map<String, CommitListener> commitListeners = new HashMap<String, CommitListener>();
        private volatile boolean isLoadingSources = false;
        private final Object lock = new Object();
        private UniqueJobID jobID;
        private Set<SQLMetadataListener> listeners = new HashSet<SQLMetadataListener>();

        /**
         * Starts the MetadataManager.
         */
        public void start() {
                dataManager = Services.getService(DataManager.class);
                bm = Services.getService(BackgroundManager.class);

                // listen to SourceManager
                dataManager.getSourceManager().addSourceListener(this);

                // queue all currently available sources
                Collections.addAll(sourcesToLoad, dataManager.getSourceManager().getSourceNames());

                jobID = new UniqueJobID();
        }

        /**
         * Frees all external resources linking to this Provider
         *
         * This method MUST be called when unloading the SQLLanguageSupport.
         * If it is not called this class will never be garbage-collected.
         */
        public void stop() {
                // unlisten to SourceManager
                dataManager.getSourceManager().removeSourceListener(this);

                cachedMetadatas.clear();
        }

        boolean checkSourcesToLoad() {
                // maybe we are already loading -> exit
                synchronized (lock) {
                        if (isLoadingSources) {
                                return true;
                        }
                }

                // maybe there is no need to load
                if (sourcesToLoad.isEmpty()) {
                        return false;
                }

                // loading
                synchronized (lock) {
                        isLoadingSources = true;
                }

                bm.nonBlockingBackgroundOperation(jobID, new BackgroundJob() {

                        @Override
                        public void run(ProgressMonitor pm) {
                                String source;
                                while (true) {
                                        source = sourcesToLoad.poll();

                                        if (source != null) {
                                                if (pm.isCancelled()) {
                                                        return;
                                                }
                                                // caching the source
                                                getMetadata(source);
                                        } else {
                                                // there is no sources anymore -> exit
                                                break;
                                        }
                                }

                                // finished loading
                                synchronized (lock) {
                                        isLoadingSources = false;
                                }

                        }

                        @Override
                        public String getTaskName() {
                                return "Caching SQL Completions";
                        }
                });

                return true;
        }

        @Override
        public void sourceAdded(SourceEvent e) {
                if (!e.isWellKnownName()) {
                        return;
                }
                sourcesToLoad.add(e.getName());
        }

        @Override
        public void sourceRemoved(SourceRemovalEvent e) {
                Metadata m = cachedMetadatas.remove(e.getName());

                if (m == null) {
                        // maybe it was scheduled for loading
                        sourcesToLoad.remove(e.getName());
                } else {
                        fireMetadataRemoved(e.getName(), m);
                }
        }

        @Override
        public void sourceNameChanged(SourceEvent e) {
                Metadata m = cachedMetadatas.remove(e.getName());
                if (m != null) {
                        fireMetadataRemoved(e.getName(), m);
                        cachedMetadatas.put(e.getNewName(), m);
                        fireMetadataAdded(e.getNewName(), m);
                } else {
                        // this source wasn't cached
                        // maybe scheduled for loading
                        sourcesToLoad.remove(e.getName());
                        sourcesToLoad.add(e.getNewName());
                }
        }

        /**
         * Gets all cached source names.
         * @return a (possibly empty) array of source names.
         */
        public String[] getSourceNames() {
                return cachedMetadatas.keySet().toArray(new String[cachedMetadatas.size()]);
        }

        /**
         * Gets some metadata from the cache.
         * @param name the name of the source
         * @return the Metadata or null if there is no cached metadata for this source
         */
        public Metadata getMetadataFromCache(String name) {
                return cachedMetadatas.get(name);
        }

        /**
         * retrieve <code>Metadata</code> for a specific data source.
         * @param sourceName the name of the source
         * @return the <code>Metadata</code> object containing the field names and types
         */
        public Metadata getMetadata(final String sourceName) {
                // checking the cache
                Metadata m = cachedMetadatas.get(sourceName);
                if (m != null) {
                        return m;
                }

                // else we have to retrieve it from a DataSource
                // this is an expensive operation that should only be done once
                try {
                        DataSource ds = dataManager.getDataSourceFactory().
                                getDataSource(sourceName, DataSourceFactory.NORMAL);
                        final Driver driver = ds.getDriver();
                        synchronized (driver) {
                                if (driver instanceof FileDriver && ((FileDriver) driver).isOpen()) {
                                        final Metadata mm = ds.getMetadata();
                                        if (mm == null) {
                                                // major error, let's skip it.
                                                return null;
                                        }
                                        m = new DefaultMetadata(mm);
                                } else {
                                        ds.open();
                                        final Metadata mm = ds.getMetadata();
                                        if (mm == null) {
                                                // major error, let's skip it.
                                                return null;
                                        }
                                        m = new DefaultMetadata(mm);
                                        ds.close();
                                }
                        }
                        // then we cache it
                        cachedMetadatas.put(sourceName, m);
                        // and we add a commit listener
                        
                        final CommitListener newC = new CommitListener() {

                                @Override
                                public void isCommiting(String name, Object source) throws DriverException {
                                }

                                @Override
                                public void commitDone(String name) throws DriverException {
                                        Metadata m = cachedMetadatas.remove(name);
                                        if (m != null) {
                                                fireMetadataRemoved(name, m);
                                                sourcesToLoad.add(name);
                                        }
                                }

                                @Override
                                public String getName() {
                                        return sourceName;
                                }
                        };

                        CommitListener old = commitListeners.put(sourceName, newC);
                        if (old != null) {
                                dataManager.getSourceManager().removeCommitListener(old);
                        }
                        dataManager.getSourceManager().addCommitListener(newC);

                        fireMetadataAdded(sourceName, m);
                        return m;
                } catch (DriverLoadException ex) {
                        return null;
                } catch (NoSuchTableException ex) {
                        return null;
                } catch (DataSourceCreationException ex) {
                        return null;
                } catch (DriverException ex) {
                        return null;
                }
        }

        /**
         * Registers a MetadataListener.
         * @param l a listener
         */
        public void registerMetadataListener(SQLMetadataListener l) {
                listeners.add(l);
        }

        /**
         * Unregisters a MetadataListener.
         * @param l a (registered) listener
         */
        public void unregisterMetadataListener(SQLMetadataListener l) {
                listeners.remove(l);
        }

        private void fireMetadataAdded(String name, Metadata m) {
                for (SQLMetadataListener l : listeners) {
                        l.metadataAdded(name, m);
                }
        }

        private void fireMetadataRemoved(String name, Metadata m) {
                for (SQLMetadataListener l : listeners) {
                        l.metadataRemoved(name, m);
                }
        }
}
