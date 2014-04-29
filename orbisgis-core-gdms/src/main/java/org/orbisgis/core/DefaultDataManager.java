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
package org.orbisgis.core;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.DataSourceListener;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.OCCounterDecorator;
import org.gdms.data.SourceAlreadyExistsException;
import org.gdms.data.indexes.IndexManager;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.source.Source;
import org.gdms.source.SourceEvent;
import org.gdms.source.SourceListener;
import org.gdms.source.SourceManager;
import org.gdms.source.SourceRemovalEvent;
import org.orbisgis.coremap.layerModel.ILayer;
import org.orbisgis.coremap.layerModel.Layer;
import org.orbisgis.coremap.layerModel.LayerCollection;
import org.orbisgis.coremap.layerModel.LayerException;
import org.orbisgis.utils.FileUtils;

public class DefaultDataManager implements DataManager, SourceListener {

	private static final Logger logger = Logger
			.getLogger(DefaultDataManager.class);
	private DataSourceFactory dsf;
    private Map<String,DataSource> createdDataSources = new HashMap<String, DataSource>();
    private EditableSourceListener editableSourceListener = new EditableSourceListener();

	public DefaultDataManager(DataSourceFactory dsf) {
		this.dsf = dsf;
        dsf.getSourceManager().addSourceListener(this);
	}

        @Override
	public DataSourceFactory getDataSourceFactory() {
		return dsf;
	}

        @Override
	public IndexManager getIndexManager() {
		return dsf.getIndexManager();
	}

        @Override
	public SourceManager getSourceManager() {
		return dsf.getSourceManager();
	}

        public ILayer createLayer(String sourceName) throws LayerException {
                Source src = dsf.getSourceManager().getSource(sourceName);
                if (src != null) {
                        int type = src.getType();
                        if ((type & (SourceManager.RASTER | SourceManager.SQL |
                                SourceManager.VECTORIAL | SourceManager.STREAM)) != 0) {
                                try {
                                        DataSource ds = dsf.getDataSource(sourceName);
                                        return createLayer(ds);
                                } catch (DriverLoadException e) {
                                        throw new LayerException("Cannot instantiate layer", e);
                                } catch (NoSuchTableException e) {
                                        throw new LayerException("Cannot instantiate layer", e);
                                } catch (DataSourceCreationException e) {
                                        throw new LayerException("Cannot instantiate layer", e);
                                }
                        } else {
                                throw new LayerException("There is no spatial information: "
                                        + type);
                        }
                } else {
                        throw new LayerException("There is no source "
                                + "registered with the name: " + sourceName);
                }
        }

        @Override
        public ILayer createLayer(DataSource sds) throws LayerException {
                int type = sds.getSource().getType();

                boolean hasSpatialData = true;
                /*
                        * Two special cases:
                        *  - if there is no vectorial information (this should not happen except for SQL,
                        *    but still...), we look for one, and for that we need to open the source to
                        *    get the metadata.
                        *  - if it is a SQL query, we do not do the above, we just go on. This implicitly
                        *    implies that ExecuteScriptProcess knows what it is doing, which is usually
                        *    ok.
                        * 
                        * Hopefully this will be removed if/when we get a SourceManager that keeps all
                        * metadata referenced and accessible at all times.
                        */
                if ((type & SourceManager.VECTORIAL) == 0 && (type & SourceManager.STREAM) == 0 && 
                        (type & SourceManager.SQL) != SourceManager.SQL) {
                        int sfi;
                        try {
                                sds.open();
                                sfi = sds.getSpatialFieldIndex();
                                try {
                                        sds.close();
                                } catch (AlreadyClosedException e) {
                                        // ignore
                                        logger.debug("Cannot close", e);
                                }
                                hasSpatialData = (sfi != -1);
                        } catch (DriverException e) {
                                throw new LayerException("Cannot check source contents", e);
                        }
                }
                if (hasSpatialData) {
                        return new Layer(sds.getName(), sds);
                } else {
                        throw new LayerException("The source contains no spatial info");
                }

        }

        @Override
	public ILayer createLayerCollection(String layerName) {
		return new LayerCollection(layerName);
	}

        @Override
	public ILayer createLayer(String name, File file) throws LayerException {
		dsf.getSourceManager().register(name, file);
		try {
			DataSource dataSource = dsf.getDataSource(name);
			return createLayer(dataSource);
		} catch (DriverLoadException e) {
			throw new LayerException("Cannot find a suitable driver for "
					+ file.getAbsolutePath(), e);
		} catch (NoSuchTableException e) {
			throw new LayerException("bug!", e);
		} catch (DataSourceCreationException e) {
			throw new LayerException("Cannot instantiate layer", e);
		}
	}

        @Override
	public ILayer createLayer(File file) throws LayerException {
		String name = dsf.getSourceManager().nameAndRegister(file);
		try {
			DataSource dataSource = dsf.getDataSource(name);
			return createLayer(dataSource);
		} catch (DriverLoadException e) {
			throw new LayerException("Cannot find a suitable driver for "
					+ file.getAbsolutePath(), e);
		} catch (NoSuchTableException e) {
			throw new LayerException("bug!", e);
		} catch (DataSourceCreationException e) {
			throw new LayerException("Cannot instantiate layer", e);
		}
	}

    @Override
    public DataSource getDataSource(String sourceName) throws NoSuchTableException, DataSourceCreationException {
        DataSource source = createdDataSources.get(sourceName);
        if(source==null) {
            source = dsf.getDataSource(sourceName);
            // Store only editable source
            if(source.isEditable()) {
                createdDataSources.put(sourceName, source);
                source.addDataSourceListener(editableSourceListener);
                source = new OCCounterDecorator(source);
            }
        } else {
            // Additional close will not close the root DataSource if it is used by another call of getDataSource.
            source = new OCCounterDecorator(source);
        }
        return source;
    }

    @Override
    public DataSource getDataSource(URI uri) throws NoSuchTableException, DataSourceCreationException {
        SourceManager sm = dsf.getSourceManager();
        try {
            if (sm.exists(uri)) {
                return getDataSource(sm.getNameFor(uri));
            } else {
                String sourceName = sm.getUniqueName(FileUtils.getNameFromURI(uri));
                sm.register(sourceName, uri);
                return getDataSource(sourceName);
            }
        } catch (SourceAlreadyExistsException ex) {
            throw new DataSourceCreationException(ex);
        }
    }

    @Override
    public void sourceAdded(SourceEvent e) {
        // Create DataSource on demand only
    }

    @Override
    public void sourceRemoved(SourceRemovalEvent e) {
        removeStoredDataSource(e.getName());
    }

    @Override
    public void sourceNameChanged(SourceEvent e) {
        // Update index
        DataSource source = createdDataSources.get(e.getName());
        if(source!=null) {
            createdDataSources.put(e.getNewName(),source);
            createdDataSources.remove(e.getName());
        }
    }

    private void removeStoredDataSource(String dataSourceName) {
        DataSource source = createdDataSources.remove(dataSourceName);
        if(source!=null) {
            source.removeDataSourceListener(editableSourceListener);
        }
    }
    @Override
    public void dispose() {
        dsf.getSourceManager().removeSourceListener(this);
        createdDataSources.clear();
    }

    /**
     * Remove closed DataSource
     */
    private class EditableSourceListener implements DataSourceListener {
        @Override
        public void open(DataSource ds) {
        }

        @Override
        public void cancel(DataSource ds) {
            if(ds!=null && !ds.isOpen() && !ds.isModified()) {
                removeStoredDataSource(ds.getName());
            }
        }

        @Override
        public void commit(DataSource ds) {
        }
    }
}
