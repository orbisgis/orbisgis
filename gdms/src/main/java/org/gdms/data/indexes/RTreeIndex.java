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

import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.indexes.rtree.DiskRTree;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.evaluator.EvaluationException;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.orbisgis.progress.IProgressMonitor;

import com.vividsolutions.jts.geom.Geometry;

public class RTreeIndex implements DataSourceIndex {

	private String fieldName;
	private int fieldId;
	private DiskRTree index;
	private File indexFile;

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public void buildIndex(DataSourceFactory dsf, DataSource dataSource,
			IProgressMonitor pm) throws IndexException {
		try {
			fieldId = dataSource.getFieldIndexByName(fieldName);
			if (dataSource.getMetadata().getFieldType(fieldId).getTypeCode() != Type.GEOMETRY) {
				throw new IndexException(fieldName + " is not spatial");
			}
			index = new DiskRTree(255, 1024);
			if (indexFile != null) {
				index.newIndex(indexFile);
			}
			long rowCount = dataSource.getRowCount();
			for (int i = 0; i < rowCount; i++) {
				if (i / 1000 == i / 1000.0) {
					if (pm.isCancelled()) {
						return;
					}
					pm.progressTo((int) (100 * i / rowCount));
				}
				Value fieldValue = dataSource.getFieldValue(i, fieldId);
				if (fieldValue.getType() != Type.NULL) {
					Geometry g = fieldValue.getAsGeometry();
					if (g != null) {
						if (!g.isEmpty()) {
							index.insert(g.getEnvelopeInternal(),
									new Integer(i));
						}
					}
				}
			}
		} catch (IOException e) {
			throw new IndexException("Cannot create the index", e);
		} catch (AlreadyClosedException e) {
			throw new IndexException(e);
		} catch (DriverException e) {
			throw new IndexException(e);
		}
	}

	public void deleteRow(Value value, int row) throws IndexException {
		if (!value.isNull()) {
			Geometry g = value.getAsGeometry();
			try {
				index.delete(g.getEnvelopeInternal(), row);
			} catch (IOException e) {
				throw new IndexException("Cannot delete at the index", e);
			}
		}
	}

	public String getFieldName() {
		return fieldName;
	}

	public int[] getIterator(IndexQuery query)
			throws IndexException {
		SpatialIndexQuery q = (SpatialIndexQuery) query;
		try {
			return index.getRow(q.getArea());
		} catch (IOException e) {
			throw new IndexException("Cannot access the index", e);
		} catch (IncompatibleTypesException e) {
			throw new IndexException("Cannot compute the value to "
					+ "query the index", e);
		} catch (EvaluationException e) {
			throw new IndexException("Cannot compute the value to "
					+ "query the index", e);
		}
	}

	public void insertRow(Value value, int row) throws IndexException {
		Value newGeometry = value;
		try {
			if (newGeometry.isNull()) {
				index.updateRows(row, 1);
				/*
				 * The index cannot hold null geometries
				 */
				return;
			} else {
				Geometry g = newGeometry.getAsGeometry();
				index.insert(g.getEnvelopeInternal(), row);
			}
		} catch (IOException e) {
			throw new IndexException("Cannot insert at the index", e);
		}
	}

	public void load() throws IndexException {
		try {
			index = new DiskRTree(255, 1024);
			index.openIndex(indexFile);
		} catch (IOException e) {
			throw new IndexException("Cannot load index from file", e);
		}
	}

	public void save() throws IndexException {
		try {
			index.save();
		} catch (IOException e) {
			throw new IndexException("Cannot save index", e);
		}
	}

	public void setFieldValue(Value oldGeometry, Value newGeometry, int rowIndex)
			throws IndexException {
		if (!oldGeometry.isNull()) {
			Geometry g = oldGeometry.getAsGeometry();
			try {
				index.delete(g.getEnvelopeInternal(), rowIndex);
			} catch (IOException e) {
				throw new IndexException("Cannot delete old value from index");
			}
		}

		if (!newGeometry.isNull()) {
			Geometry g = newGeometry.getAsGeometry();
			try {
				index.insert(g.getEnvelopeInternal(), rowIndex);
			} catch (IOException e) {
				throw new IndexException("Cannot perform modification "
						+ "in index. Index is corrupted. "
						+ "It's recommended to rebuild the index from scratch");
			}
		}
	}

	public File getFile() {
		return indexFile;
	}

	public void setFile(File file) {
		this.indexFile = file;
	}

	public void close() throws IOException {
		if (index != null) {
			index.close();
			index = null;
		}
	}

	public boolean isOpen() {
		return index != null;
	}

}
