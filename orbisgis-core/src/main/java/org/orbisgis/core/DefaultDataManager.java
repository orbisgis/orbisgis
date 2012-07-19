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
package org.orbisgis.core;

import java.io.File;
import org.apache.log4j.Logger;
import org.gdms.data.*;
import org.gdms.data.indexes.IndexManager;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.source.Source;
import org.gdms.source.SourceManager;
import org.orbisgis.core.layerModel.*;

public class DefaultDataManager implements DataManager {

	private static final Logger logger = Logger
			.getLogger(DefaultDataManager.class);
	private DataSourceFactory dsf;

	public DefaultDataManager(DataSourceFactory dsf) {
		this.dsf = dsf;
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
                                SourceManager.VECTORIAL | SourceManager.WMS)) != 0) {
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
                if ((type & SourceManager.WMS) == SourceManager.WMS) {
                        return new WMSLayer(sds.getName(), sds);
                } else {
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
                        if ((type & SourceManager.VECTORIAL) == 0 && 
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

}
