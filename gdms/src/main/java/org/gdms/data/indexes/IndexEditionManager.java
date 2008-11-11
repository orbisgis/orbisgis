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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.driver.DriverException;
import org.orbisgis.progress.IProgressMonitor;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.utils.FileUtils;

public class IndexEditionManager {

	private DataSource ds;

	private IndexManager im;

	private ArrayList<DataSourceIndex> modifiedIndexes = null;

	private IndexManagerListener indexManagerListener;

	public IndexEditionManager(DataSourceFactory dsf, DataSource ds) {
		this.im = dsf.getIndexManager();
		this.ds = ds;
	}

	public void open() {
		modifiedIndexes = null;
	}

	public void commit(boolean rebuildIndexes) throws DriverException {
		if (modifiedIndexes != null) {
			if (rebuildIndexes) {
				String[] indexedFieldNames = new String[modifiedIndexes.size()];
				for (int i = 0; i < modifiedIndexes.size(); i++) {
					indexedFieldNames[i] = modifiedIndexes.get(i)
							.getFieldName();
				}
				for (String indexFieldName : indexedFieldNames) {
					try {
						im.deleteIndex(ds.getName(), indexFieldName);
						im.buildIndex(ds.getName(), indexFieldName,
								new NullProgressMonitor());
					} catch (NoSuchTableException e) {
						throw new DriverException("Cannot rebuild index", e);
					} catch (IndexException e) {
						throw new DriverException("Cannot rebuild index", e);
					}
				}
			} else {
				try {
					im.indexesChanged(ds.getName(), modifiedIndexes
							.toArray(new DataSourceIndex[0]));
				} catch (IOException e) {
					throw new DriverException("Cannot replace index content", e);
				}
			}
		}
		cancel();
	}

	public void cancel() {
		modifiedIndexes = null;
		if (im != null) {
			im.removeIndexManagerListener(indexManagerListener);
		}
	}

	public DataSourceIndex[] getDataSourceIndexes() throws IndexException {
		if (ds.isModified()) {
			return getModifiedIndexes();
		} else {
			try {
				return im.getIndexes(ds.getName());
			} catch (NoSuchTableException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private DataSourceIndex[] getModifiedIndexes() throws IndexException {
		if (modifiedIndexes == null) {
			try {
				DataSourceIndex[] toClone = im.getIndexes(ds.getName());
				modifiedIndexes = new ArrayList<DataSourceIndex>();
				for (int i = 0; i < toClone.length; i++) {
					if (toClone[i].isOpen()) {
						toClone[i].save();
						toClone[i].close();
					}
					File indexFile = toClone[i].getFile();
					File copied = new File(ds.getDataSourceFactory()
							.getTempFile());
					FileUtils.copy(indexFile, copied);
					DataSourceIndex cloned = toClone[i].getClass()
							.newInstance();
					cloned.setFieldName(toClone[i].getFieldName());
					cloned.setFile(copied);
					cloned.load();
					toClone[i].load();
					modifiedIndexes.add(cloned);
				}
			} catch (NoSuchTableException e) {
				throw new RuntimeException("bug!", e);
			} catch (IOException e) {
				throw new IndexException("Cannot duplicate index file", e);
			} catch (InstantiationException e) {
				throw new RuntimeException("bug!", e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException("bug!", e);
			}
			indexManagerListener = new IndexManagerListener() {

				public void indexCreated(String source, String field,
						String indexId, IndexManager im, IProgressMonitor pm)
						throws IndexException {
					try {
						if (source.equals(ds.getDataSourceFactory()
								.getSourceManager()
								.getMainNameFor(ds.getName()))) {
							addIndexWithDataInEdition(field, indexId, im, pm);
						}
					} catch (NoSuchTableException e) {
						throw new RuntimeException("bug!", e);
					}
				}

				public void indexDeleted(String source, String field,
						String indexId, IndexManager im) {
					try {
						if (source.equals(ds.getDataSourceFactory()
								.getSourceManager()
								.getMainNameFor(ds.getName()))) {
							DataSourceIndex toDelete = null;
							for (DataSourceIndex index : modifiedIndexes) {
								if (index.getFieldName().equals(field)) {
									toDelete = index;
									break;
								}
							}
							modifiedIndexes.remove(toDelete);
						}
					} catch (NoSuchTableException e) {
						throw new RuntimeException("bug!", e);
					}
				}

			};
			im.addIndexManagerListener(indexManagerListener);
		}

		return modifiedIndexes.toArray(new DataSourceIndex[0]);
	}

	private void addIndexWithDataInEdition(String fieldName, String indexId,
			IndexManager im, IProgressMonitor pm) throws IndexException {
		DataSourceIndex index = im.instantiateIndex(indexId);
		index.setFile(new File(ds.getDataSourceFactory().getTempFile()));
		index.setFieldName(fieldName);
		index.buildIndex(ds.getDataSourceFactory(), ds, pm);
		index.save();
		modifiedIndexes.add(index);
	}

	public int[] query(IndexQuery indexQuery) throws DriverException,
			IndexException {
		DataSourceIndex[] indexes = getDataSourceIndexes();
		for (DataSourceIndex dataSourceIndex : indexes) {
			if (dataSourceIndex.getFieldName()
					.equals(indexQuery.getFieldName())) {
				return dataSourceIndex.getIterator(indexQuery);
			}
		}

		return null;
	}
}
