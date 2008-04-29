/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
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
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.geoview.layerModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.types.NullCRS;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.source.Source;
import org.gdms.source.SourceManager;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geoview.persistence.LayerCollectionType;
import org.orbisgis.geoview.persistence.LayerType;
import org.orbisgis.pluginManager.PluginManager;

public class LayerFactory {

	public static LayerCollection createLayerCollection(String name) {
		return new LayerCollection(name);
	}

	public static ILayer createLayer(String name, File file)
			throws DriverLoadException, NoSuchTableException,
			DataSourceCreationException {
		DataSourceFactory dsf = OrbisgisCore.getDSF();
		dsf.getSourceManager().register(name, file);
		return new Layer(name, dsf.getDataSource(name), NullCRS.singleton);
	}

	public static ILayer createLayer(DataSource ds) {
		return new Layer(ds.getName(), ds, NullCRS.singleton);
	}

	public static ILayer createLayer(String sourceName)
			throws FileNotFoundException, IOException, DriverLoadException,
			NoSuchTableException, DataSourceCreationException,
			UnsupportedSourceException {
		Source src = OrbisgisCore.getDSF().getSourceManager().getSource(
				sourceName);
		if (src != null) {
			int type = src.getType();
			if ((type & (SourceManager.RASTER | SourceManager.VECTORIAL)) == 0) {
				throw new UnsupportedSourceException(
						"Cannot understand source type: " + type);
			} else {
				DataSource ds = OrbisgisCore.getDSF().getDataSource(sourceName);
				return LayerFactory.createLayer(ds);
			}
		} else {
			throw new UnsupportedSourceException("There is no source "
					+ "registered with the name: " + sourceName);
		}
	}

	public static ILayer createLayer(LayerType layer) throws LayerException {
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
						PluginManager.error("Cannot add layer to collection: "
								+ lyr.getName(), e);
					} catch (CRSException e) {
						PluginManager.error("Cannot add layer to collection: "
								+ lyr.getName(), e);
					}
				}
			}
		} else {
			try {
				ret = createLayer(layer.getSourceName());
				ret.setName(layer.getName());
			} catch (FileNotFoundException e) {
				PluginManager.error("Cannot recover layer " + layer.getName(),
						e);
			} catch (DriverLoadException e) {
				PluginManager.error("Cannot recover layer " + layer.getName(),
						e);
			} catch (IOException e) {
				PluginManager.error("Cannot recover layer " + layer.getName(),
						e);
			} catch (NoSuchTableException e) {
				PluginManager.error("Cannot recover layer " + layer.getName(),
						e);
			} catch (DataSourceCreationException e) {
				PluginManager.error("Cannot recover layer " + layer.getName(),
						e);
			} catch (UnsupportedSourceException e) {
				PluginManager.error("Cannot recover layer " + layer.getName(),
						e);
			}
		}
		return ret;
	}

	public static ILayer createLayer(File file)
			throws DriverLoadException, NoSuchTableException,
			DataSourceCreationException {
		DataSourceFactory dsf = OrbisgisCore.getDSF();
		String name = dsf.getSourceManager().nameAndRegister(file);
		return new Layer(name, dsf.getDataSource(name), NullCRS.singleton);
	}
}
