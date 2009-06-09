/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.core;

import java.io.File;

import org.apache.log4j.Logger;
import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.indexes.IndexManager;
import org.gdms.data.types.NullCRS;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.source.Source;
import org.gdms.source.SourceManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.Layer;
import org.orbisgis.core.layerModel.LayerCollection;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.layerModel.WMSLayer;

public class DefaultDataManager implements DataManager {

	private static final Logger logger = Logger
			.getLogger(DefaultDataManager.class);
	private DataSourceFactory dsf;

	public DefaultDataManager(DataSourceFactory dsf) {
		this.dsf = dsf;
	}

	public DataSourceFactory getDSF() {
		return dsf;
	}

	public IndexManager getIndexManager() {
		return dsf.getIndexManager();
	}

	public SourceManager getSourceManager() {
		return dsf.getSourceManager();
	}

	public ILayer createLayer(String sourceName) throws LayerException {
		Source src = ((DataManager) Services.getService(DataManager.class))
				.getDSF().getSourceManager().getSource(sourceName);
		if (src != null) {
			int type = src.getType();
			if ((type & (SourceManager.RASTER | SourceManager.VECTORIAL | SourceManager.WMS)) != 0) {
				try {
					DataSource ds = ((DataManager) Services
							.getService(DataManager.class)).getDSF()
							.getDataSource(sourceName);
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

	public ILayer createLayer(DataSource ds) throws LayerException {
		int type = ds.getSource().getType();
		if ((type & SourceManager.WMS) == SourceManager.WMS) {
			return new WMSLayer(ds.getName(), ds, NullCRS.singleton);
		} else {
			boolean hasSpatialData = true;
			if ((type & SourceManager.VECTORIAL) == SourceManager.VECTORIAL) {
				SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(
						ds);
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
				return new Layer(ds.getName(), ds, NullCRS.singleton);
			} else {
				throw new LayerException("The source contains no spatial info");
			}
		}
	}

	public ILayer createLayerCollection(String layerName) {
		return new LayerCollection(layerName);
	}

	public ILayer createLayer(String name, File file) throws LayerException {
		DataSourceFactory dsf = ((DataManager) Services
				.getService(DataManager.class)).getDSF();
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

	public ILayer createLayer(File file) throws LayerException {
		DataSourceFactory dsf = ((DataManager) Services
				.getService(DataManager.class)).getDSF();
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
