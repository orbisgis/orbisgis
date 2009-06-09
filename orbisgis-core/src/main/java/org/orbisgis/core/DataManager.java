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

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.indexes.IndexManager;
import org.gdms.source.SourceManager;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerException;

public interface DataManager {

	/**
	 * Gets the DataSourceFactory of the system
	 *
	 * @return
	 */
	DataSourceFactory getDSF();

	/**
	 * Gets the manager of all the registered data sources
	 *
	 * @return
	 */
	SourceManager getSourceManager();

	/**
	 * Gets the index manager of the system
	 *
	 * @return
	 */
	IndexManager getIndexManager();

	/**
	 * Creates a layer on the source which name is equal to the specified name
	 *
	 * @param sourceName
	 * @return
	 * @throws LayerException
	 *             if the layer could not be created
	 */
	ILayer createLayer(String sourceName) throws LayerException;

	/**
	 * Creates a layer that accesses the specified DataSource. The DataSource
	 * must have been obtained from the DataSourceFactory accessible by this
	 * interface
	 *
	 * @param dataSource
	 * @return
	 * @throws LayerException If the layer cannot be created
	 */
	ILayer createLayer(DataSource dataSource) throws LayerException;

	/**
	 * Creates a layer collection with the specified name
	 *
	 * @param string
	 * @return
	 */
	ILayer createLayerCollection(String layerName);

	/**
	 * Creates a layer on the specified file with the specified name. The file
	 * is added as a source in the source manager
	 *
	 * @param name
	 * @param file
	 * @return
	 * @throws LayerException
	 *             If the layer could not be created
	 */
	ILayer createLayer(String name, File file) throws LayerException;

	/**
	 * Creates a layer on the specified file with a random name. The file is
	 * added as a source in the source manager
	 *
	 * @param file
	 * @return
	 * @throws LayerException
	 *             If the layer could not be created
	 */
	ILayer createLayer(File file) throws LayerException;

}
