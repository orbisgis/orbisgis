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
package org.gdms.data.indexes;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.source.Source;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.orbisgis.progress.IProgressMonitor;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.utils.FileUtils;

public class IndexManager {

	private static final Logger logger = Logger.getLogger(IndexManager.class);

	public static final String INDEX_PROPERTY_PREFIX = "org.gdms.index";

	public static final String RTREE_SPATIAL_INDEX = "org.gdms.rtree";

	public static final String BTREE_ALPHANUMERIC_INDEX = "org.gdms.btree";

	private DataSourceFactory dsf;

	private Map<String, Class<? extends DataSourceIndex>> indexRegistry = new HashMap<String, Class<? extends DataSourceIndex>>();

	private Map<IndexDefinition, DataSourceIndex> indexCache = new HashMap<IndexDefinition, DataSourceIndex>();

	private ArrayList<IndexManagerListener> listeners = new ArrayList<IndexManagerListener>();

	public IndexManager(DataSourceFactory dsf) {
		this.dsf = dsf;
	}

	public void addIndexManagerListener(IndexManagerListener listener) {
		listeners.add(listener);
	}

	public boolean removeIndexManagerListener(IndexManagerListener listener) {
		return listeners.remove(listener);
	}

	/**
	 * Builds the specified index on the specified field of the datasource.
	 * Saves the index in a file
	 * 
	 * @param dsName
	 * @param fieldName
	 * @param indexId
	 * @param pm
	 * @throws NoSuchTableException
	 * @throws IncompatibleTypesException
	 * @throws DriverLoadException
	 * @throws DriverException
	 * @throws NoSuchTableException
	 * @throws DataSourceCreationException
	 */
	public void buildIndex(String dsName, String fieldName, String indexId,
			IProgressMonitor pm) throws IndexException, NoSuchTableException {
		if (pm == null) {
			pm = new NullProgressMonitor();
		}
		dsName = dsf.getSourceManager().getMainNameFor(dsName);

		Source src = dsf.getSourceManager().getSource(dsName);

		// Get index id if null
		DataSource ds;
		try {
			ds = dsf.getDataSource(dsName, DataSourceFactory.NORMAL);
			if (indexId == null) {
				int code = ds.getFieldType(ds.getFieldIndexByName(fieldName))
						.getTypeCode();
				if (code == Type.GEOMETRY) {
					indexId = RTREE_SPATIAL_INDEX;
				} else {
					indexId = BTREE_ALPHANUMERIC_INDEX;
				}
			}
		} catch (DriverException e) {
			throw new IndexException("Cannot access data to index", e);
		} catch (NoSuchTableException e) {
			throw new IndexException("The source doesn't exist", e);
		} catch (DataSourceCreationException e) {
			throw new IndexException("Cannot access the source", e);
		}
		String propertyName = INDEX_PROPERTY_PREFIX + "-" + fieldName + "-"
				+ indexId;

		// build index
		try {
			if (src.getFileProperty(propertyName) != null) {
				throw new IndexException("There is already an "
						+ "index on that field for that source: " + dsName
						+ "." + fieldName);
			}
			File indexFile = src.createFileProperty(propertyName);
			ds.open();
			DataSourceIndex index = null;
			index = instantiateIndex(indexId);
			if (index == null) {
				throw new UnsupportedOperationException("Cannot find "
						+ indexId + " index");
			}
			index.setFile(indexFile);
			index.setFieldName(fieldName);
			index.buildIndex(dsf, ds, pm);
			ds.close();
			if (pm.isCancelled()) {
				src.deleteProperty(propertyName);
				return;
			}
			index.save();
			index.close();

			IndexDefinition def = new IndexDefinition(dsName, fieldName);
			indexCache.put(def, index);
			fireIndexCreated(dsName, fieldName, indexId, pm);
		} catch (DriverLoadException e) {
			throw new IndexException("Cannot read source", e);
		} catch (IncompatibleTypesException e) {
			try {
				src.deleteProperty(propertyName);
			} catch (IOException e1) {
				logger.debug("Cannot create index and remove property", e1);
			}
			throw new IndexException("Cannot create an "
					+ "index with that field type: " + fieldName);
		} catch (IOException e) {
			throw new IndexException("Cannot associate index with source", e);
		} catch (DriverException e) {
			try {
				src.deleteProperty(propertyName);
			} catch (IOException e1) {
				logger.debug("Cannot create index and remove property", e1);
			}
			throw new IndexException("Cannot access data to index", e);
		}

	}

	public void buildIndex(String tableName, String fieldName,
			IProgressMonitor pm) throws NoSuchTableException, IndexException {
		buildIndex(tableName, fieldName, null, pm);
	}

	private void fireIndexCreated(String dsName, String fieldName,
			String indexId, IProgressMonitor pm) throws IndexException {
		for (IndexManagerListener listener : listeners) {
			listener.indexCreated(dsName, fieldName, indexId, this, pm);
		}
	}

	private void fireIndexDeleted(String dsName, String fieldName,
			String indexId) throws IndexException {
		for (IndexManagerListener listener : listeners) {
			listener.indexDeleted(dsName, fieldName, indexId, this);
		}
	}

	/**
	 * Registers an index into the collection of indexes
	 * 
	 * @param index
	 */
	public void addIndex(String id, Class<? extends DataSourceIndex> index) {
		indexRegistry.put(id, index);
	}

	DataSourceIndex instantiateIndex(String indexId) throws IndexException {
		try {
			return indexRegistry.get(indexId).newInstance();
		} catch (InstantiationException e) {
			throw new IndexException("Cannot instantiate the index", e);
		} catch (IllegalAccessException e) {
			throw new IndexException("Cannot instantiate the index", e);
		}
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
		IndexDefinition def = new IndexDefinition(dsName, fieldName);
		DataSourceIndex ret = indexCache.get(def);
		if (ret == null) {
			String[] indexProperties = getIndexProperties(dsName);
			for (String indexProperty : indexProperties) {
				String propertyFieldName = getFieldName(indexProperty);
				if (propertyFieldName.equals(fieldName)) {
					DataSourceIndex index = instantiateIndex(getIndexId(indexProperty));
					Source src = dsf.getSourceManager().getSource(dsName);
					index.setFieldName(fieldName);
					index.setFile(src.getFileProperty(indexProperty));
					index.load();
					indexCache.put(def, index);
					ret = index;
					break;
				}
			}
		}

		return ret;
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

	public void indexesChanged(String dsName, DataSourceIndex[] modifiedIndexes)
			throws IOException {
		try {
			dsName = dsf.getSourceManager().getMainNameFor(dsName);
			String[] indexProperties = getIndexProperties(dsName);
			Source src = dsf.getSourceManager().getSource(dsName);
			for (String indexProperty : indexProperties) {
				// Search the modified index related to this property
				for (DataSourceIndex dataSourceIndex : modifiedIndexes) {
					if (dataSourceIndex.getFieldName().equals(
							getFieldName(indexProperty))) {
						// Get the file of the property and change the contents
						File dest = src.getFileProperty(indexProperty);
						File source = dataSourceIndex.getFile();
						try {
							dataSourceIndex.save();
							dataSourceIndex.close();
							BufferedOutputStream out = new BufferedOutputStream(
									new FileOutputStream(dest));
							BufferedInputStream in = new BufferedInputStream(
									new FileInputStream(source));
							FileUtils.copy(in, out);
							out.close();
							in.close();
						} catch (IOException e) {
							src.deleteProperty(indexProperty);
						} catch (IndexException e) {
							src.deleteProperty(indexProperty);
						}

						IndexDefinition def = new IndexDefinition(dsName,
								getFieldName(indexProperty));
						indexCache.remove(def);
					}
				}
			}
		} catch (NoSuchTableException e) {
			throw new IllegalArgumentException("bug: The source "
					+ "does not exist: " + dsName);
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
		ArrayList<DataSourceIndex> ret = new ArrayList<DataSourceIndex>();
		dsName = dsf.getSourceManager().getMainNameFor(dsName);
		String[] indexProperties = getIndexProperties(dsName);
		for (String indexProperty : indexProperties) {
			String fieldName = getFieldName(indexProperty);
			DataSourceIndex index = getIndex(dsName, fieldName);
			if (index != null) {
				ret.add(index);
			}
		}

		return ret.toArray(new DataSourceIndex[0]);
	}

	private String getIndexId(String propertyName) {
		String prop = propertyName
				.substring(INDEX_PROPERTY_PREFIX.length() + 1);
		String indexId = prop.substring(prop.indexOf('-') + 1);

		return indexId;
	}

	private String getFieldName(String propertyName) {
		String prop = propertyName
				.substring(INDEX_PROPERTY_PREFIX.length() + 1);
		String fieldName = prop.substring(0, prop.indexOf('-'));

		return fieldName;
	}

	private String[] getIndexProperties(String dsName) {
		ArrayList<String> ret = new ArrayList<String>();
		Source src = dsf.getSourceManager().getSource(dsName);
		if (src != null) {
			String[] fileProperties = src.getFilePropertyNames();
			for (String fileProperty : fileProperties) {
				if (fileProperty.startsWith(INDEX_PROPERTY_PREFIX)) {
					ret.add(fileProperty);
				}
			}
		} else {
			throw new IllegalArgumentException("The source doesn't exist: "
					+ dsName);
		}

		return ret.toArray(new String[0]);
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
	public int[] queryIndex(String dsName, IndexQuery indexQuery)
			throws IndexException, NoSuchTableException {
		DataSourceIndex dsi;
		dsi = getIndex(dsName, indexQuery.getFieldName());
		if (dsi != null) {
			if (!dsi.isOpen()) {
				dsi.load();
			}
			return dsi.getIterator(indexQuery);
		} else {
			return null;
		}
	}

	public String[] getIndexedFieldNames(String name) {
		String[] indexProperties = getIndexProperties(name);
		String[] ret = new String[indexProperties.length];
		for (int i = 0; i < ret.length; i++) {
			ret[0] = getFieldName(indexProperties[i]);
		}

		return ret;
	}

	/**
	 * Removes the index for the source. All the current DataSource instances
	 * are affected
	 * 
	 * @param dsName
	 * @param fieldName
	 * @throws IllegalArgumentException
	 *             If the source doesn't exist
	 * @throws IndexException
	 */
	public void deleteIndex(String dsName, String fieldName)
			throws NoSuchTableException, IndexException {
		try {
			dsName = dsf.getSourceManager().getMainNameFor(dsName);
			String[] indexProperties = getIndexProperties(dsName);
			for (String indexProperty : indexProperties) {
				if (indexProperty.startsWith(INDEX_PROPERTY_PREFIX + "-"
						+ fieldName + "-")) {
					Source src = dsf.getSourceManager().getSource(dsName);
					src.deleteProperty(indexProperty);
					IndexDefinition def = new IndexDefinition(dsName, fieldName);
					indexCache.remove(def);
					fireIndexDeleted(dsName, fieldName,
							getIndexId(indexProperty));
					return;
				}
			}
			throw new IllegalArgumentException(dsName + " does not have "
					+ "an index on the field'" + fieldName + "'");
		} catch (IOException e) {
			throw new IndexException("Cannot remove index property of "
					+ dsName + " at field " + fieldName);
		}
	}

	public AdHocIndex getAdHocIndex(ObjectDriver rightSource, String fieldName,
			String indexId, IProgressMonitor pm) throws IndexException,
			NoSuchTableException {
		if (pm == null) {
			pm = new NullProgressMonitor();
		}
		DataSourceIndex index = instantiateIndex(indexId);
		if (index == null) {
			throw new UnsupportedOperationException("Cannot find " + indexId
					+ " index");
		}

		try {
			DataSource ds = dsf.getDataSource(rightSource,
					DataSourceFactory.NORMAL);
			index.setFieldName(fieldName);
			ds.open();
			index.buildIndex(dsf, ds, pm);
			ds.close();
			return index;
		} catch (DriverLoadException e) {
			throw new IndexException("Cannot read source", e);
		} catch (IncompatibleTypesException e) {
			throw new IndexException("Cannot create an "
					+ "index with that field type: " + fieldName);
		} catch (DriverException e) {
			throw new IndexException("Cannot access data to index", e);
		}

	}

	public boolean isIndexed(String sourceName, String fieldName)
			throws NoSuchTableException {
		sourceName = dsf.getSourceManager().getMainNameFor(sourceName);
		String[] indexProperties = getIndexProperties(sourceName);
		for (String indexProperty : indexProperties) {
			if (indexProperty.startsWith(INDEX_PROPERTY_PREFIX + "-"
					+ fieldName + "-")) {
				return true;
			}
		}
		return false;
	}
}
