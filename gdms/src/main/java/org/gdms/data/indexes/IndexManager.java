/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
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
package org.gdms.data.indexes;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.edition.PhysicalDirection;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.sql.strategies.IncompatibleTypesException;

public class IndexManager {

	private DataSourceFactory dsf;

	private Map<String, DataSourceIndex> emptyIndexes = new HashMap<String, DataSourceIndex>();

	private Map<IndexDefinition, IndexFile> indexDefinitionFile = new HashMap<IndexDefinition, IndexFile>();

	private Map<IndexDefinition, DataSourceIndex> indexDefinitionIndexesCache = new HashMap<IndexDefinition, DataSourceIndex>();

	private Map<String, ArrayList<String>> sourceFields = new HashMap<String, ArrayList<String>>();

	public IndexManager(DataSourceFactory dsf) {
		this.dsf = dsf;
	}

	/**
	 * Builds the specified index on the specified field of the datasource.
	 * Saves the index in a file
	 *
	 * @param dsName
	 * @param fieldName
	 * @param indexId
	 * @throws NoSuchTableException
	 * @throws IncompatibleTypesException
	 * @throws DriverLoadException
	 * @throws DriverException
	 * @throws NoSuchTableException
	 * @throws DataSourceCreationException
	 */
	public void buildIndex(String dsName, String fieldName, String indexId)
			throws IndexException, NoSuchTableException {
		dsName = dsf.getSourceManager().getMainNameFor(dsName);
		DataSourceIndex index = getNewIndex(indexId);
		if (index == null) {
			throw new UnsupportedOperationException("Cannot find " + indexId
					+ " index");
		}

		try {
			DataSource ds = dsf.getDataSource(dsName, DataSourceFactory.NORMAL);
			index.buildIndex(dsf, ds, fieldName);
			index.setDataSource(ds);
		} catch (DriverLoadException e) {
			throw new IndexException(e);
		} catch (NoSuchTableException e) {
			throw new IndexException(e);
		} catch (DataSourceCreationException e) {
			throw new IndexException(e);
		}

		IndexDefinition def = new IndexDefinition(dsName, fieldName);
		IndexFile indexFile = indexDefinitionFile.get(def);
		if (indexFile == null) {
			indexFile = new IndexFile(dsf.getTempFile(), indexId);
		}
//		index.save(new File(indexFile.fileName));
		indexDefinitionFile.put(def, indexFile);
		addField(dsName, fieldName);

		indexDefinitionIndexesCache.put(def, index);
	}

	private void addField(String dsName, String fieldName) {
		ArrayList<String> fields = sourceFields.get(dsName);
		if (fields == null) {
			fields = new ArrayList<String>();
			sourceFields.put(dsName, fields);
		}
		fields.add(fieldName);
	}

	/**
	 * Adds an index to the collection of indexes
	 *
	 * @param index
	 */
	public void addIndex(DataSourceIndex index) {
		emptyIndexes.put(index.getId(), index);
	}

	private DataSourceIndex getNewIndex(String indexId) {
		return emptyIndexes.get(indexId).getNewInstance();
	}

	/**
	 * Gets the index for the specified source name. All instances of DataSource
	 * that access to the specified source will use the same index instance.
	 *
	 * @param dsName
	 * @param fieldName
	 * @return
	 * @throws NoSuchTableException
	 * @throws DriverException
	 */
	public DataSourceIndex getIndex(String dsName, String fieldName)
			throws IndexException, NoSuchTableException {
		dsName = dsf.getSourceManager().getMainNameFor(dsName);
		IndexDefinition def = new IndexDefinition(dsName, fieldName);
		DataSourceIndex cachedIndex = indexDefinitionIndexesCache.get(def);
		if (cachedIndex == null) {
			IndexFile indexFile = indexDefinitionFile.get(def);
			if (indexFile != null) {
				DataSourceIndex index = getNewIndex(indexFile.indexId);
				index.load(new File(indexFile.fileName));
				try {
					index.setDataSource(dsf.getDataSource(dsName,
							DataSourceFactory.NORMAL));
				} catch (DriverLoadException e) {
					throw new IndexException(e);
				} catch (NoSuchTableException e) {
					throw new IndexException(e);
				} catch (DataSourceCreationException e) {
					throw new IndexException(e);
				}
				indexDefinitionIndexesCache.put(def, index);
				cachedIndex = index;
			} else {
				cachedIndex = null;
			}
		}

		return cachedIndex;
	}

	private class IndexFile {
		public String fileName;

		public String indexId;

		public IndexFile(String fileName, String indexId) {
			super();
			this.fileName = fileName;
			this.indexId = indexId;
		}
	}

	private class IndexDefinition {
		public String dsName;

		public String fieldName;

		public IndexDefinition(String dsName, String fieldName) {
			super();
			this.dsName = dsName;
			this.fieldName = fieldName;
		}

		@Override
		public boolean equals(Object obj) {
			IndexDefinition id = (IndexDefinition) obj;
			return dsName.equals(id.dsName) && fieldName.equals(id.fieldName);
		}

		@Override
		public int hashCode() {
			return dsName.hashCode() + fieldName.hashCode();
		}

	}

	public void indexesChanged(String dsName) {
		ArrayList<String> fieldNames = sourceFields.get(dsName);

		for (int i = 0; i < fieldNames.size(); i++) {
			IndexDefinition def = new IndexDefinition(dsName, fieldNames.get(i));
			indexDefinitionIndexesCache.remove(def);
		}
	}

	/**
	 * Gets the indexes of the specified source
	 *
	 * @param dsName
	 * @return
	 * @throws IndexException
	 * @throws NoSuchTableException
	 */
	public DataSourceIndex[] getIndexes(String dsName) throws IndexException,
			NoSuchTableException {
		ArrayList<String> fieldNames = sourceFields.get(dsName);

		if (fieldNames != null) {
			DataSourceIndex[] ret = new DataSourceIndex[fieldNames.size()];
			for (int i = 0; i < ret.length; i++) {
				ret[i] = getIndex(dsName, fieldNames.get(i));
			}

			return ret;
		} else {
			return new SpatialIndex[0];
		}
	}

	/**
	 * Queries the index of the specified source, with the specified query
	 *
	 * @param dsName
	 * @param indexQuery
	 * @return The iterator or null if there is no index in the specified field
	 * @throws NoSuchTableException
	 * @throws DriverException
	 */
	public Iterator<PhysicalDirection> queryIndex(String dsName,
			IndexQuery indexQuery) throws IndexException, NoSuchTableException {
		DataSourceIndex dsi;
		dsi = getIndex(dsName, indexQuery.getFieldName());
		if (dsi != null) {
			return dsi.getIterator(indexQuery);
		} else {
			return null;
		}
	}

}
