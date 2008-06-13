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
package org.orbisgis;

import java.io.File;
import java.util.List;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.indexes.IndexManager;
import org.gdms.data.types.NullCRS;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.source.Source;
import org.gdms.source.SourceManager;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.Layer;
import org.orbisgis.layerModel.LayerCollection;
import org.orbisgis.layerModel.LayerException;
import org.orbisgis.layerModel.persistence.LayerCollectionType;
import org.orbisgis.layerModel.persistence.LayerType;

public class DefaultDataManager implements DataManager {

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
		Source src = ((DataManager) Services
				.getService("org.orbisgis.DataManager")).getDSF()
				.getSourceManager().getSource(sourceName);
		if (src != null) {
			int type = src.getType();
			if ((type & (SourceManager.RASTER | SourceManager.VECTORIAL)) == 0) {
				throw new LayerException("Cannot understand source type: "
						+ type);
			} else {
				try {
					DataSource ds = ((DataManager) Services
							.getService("org.orbisgis.DataManager")).getDSF()
							.getDataSource(sourceName);
					return createLayer(ds);
				} catch (DriverLoadException e) {
					throw new LayerException("Cannot instantiate layer", e);
				} catch (NoSuchTableException e) {
					throw new LayerException("Cannot instantiate layer", e);
				} catch (DataSourceCreationException e) {
					throw new LayerException("Cannot instantiate layer", e);
				}
			}
		} else {
			throw new LayerException("There is no source "
					+ "registered with the name: " + sourceName);
		}
	}

	public ILayer createLayer(DataSource ds) {
		return new Layer(ds.getName(), ds, NullCRS.singleton);
	}

	public ILayer createLayer(LayerType layer) throws LayerException {
		ILayer ret = null;
		if (layer instanceof LayerCollectionType) {
			LayerCollectionType xmlLayerCollection = (LayerCollectionType) layer;
			ret = createLayerCollection(layer.getName());
			List<LayerType> xmlChildren = xmlLayerCollection.getLayer();
			for (LayerType layerType : xmlChildren) {
				ILayer lyr = createLayer(layerType);
				if (lyr != null) {
					try {
						ret.addLayer(lyr);
					} catch (LayerException e) {
						Services.getErrorManager().error(
								"Cannot add layer to collection: "
										+ lyr.getName(), e);
					}
				}
			}
		} else {
			try {
				ret = createLayer(layer.getSourceName());
				ret.setName(layer.getName());
				ret.setVisible(layer.isVisible());
			} catch (LayerException e) {
				Services.getErrorManager().error(
						"Cannot recover layer: " + layer.getName(), e);
			}
		}
		return ret;
	}

	public ILayer createLayerCollection(String layerName) {
		return new LayerCollection(layerName);
	}

	public ILayer createLayer(String name, File file) throws LayerException {
		DataSourceFactory dsf = ((DataManager) Services
				.getService("org.orbisgis.DataManager")).getDSF();
		dsf.getSourceManager().register(name, file);
		try {
			return new Layer(name, dsf.getDataSource(name), NullCRS.singleton);
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
				.getService("org.orbisgis.DataManager")).getDSF();
		String name = dsf.getSourceManager().nameAndRegister(file);
		try {
			return new Layer(name, dsf.getDataSource(name), NullCRS.singleton);
		} catch (DriverLoadException e) {
			throw new LayerException("Cannot find a suitable driver for "
					+ file.getAbsolutePath(), e);
		} catch (NoSuchTableException e) {
			throw new LayerException("bug!", e);
		} catch (DataSourceCreationException e) {
			throw new LayerException("Cannot instantiate layer", e);
		}
	}

	/**
	 * @see org.orbisgis.core.DataManager#registerWithUniqueName(java.lang.String,
	 *      org.gdms.data.DataSourceDefinition)
	 */
	public String registerWithUniqueName(String name, DataSourceDefinition dsd) {
		int extensionStart = name.lastIndexOf('.');
		String nickname = name;
		if (extensionStart != -1) {
			nickname = name.substring(0, name.indexOf(name
					.substring(extensionStart)));
		}
		String tmpName = nickname;
		int i = 0;
		while (dsf.exists(tmpName)) {
			i++;
			tmpName = nickname + "_" + i;
		}

		dsf.registerDataSource(tmpName, dsd);

		return tmpName;
	}

}
