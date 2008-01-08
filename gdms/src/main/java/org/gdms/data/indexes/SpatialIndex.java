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
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
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
/**
 *
 */
package org.gdms.data.indexes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Iterator;

import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.edition.OriginalDirection;
import org.gdms.data.edition.PhysicalDirection;
import org.gdms.data.indexes.quadtree.Quadtree;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.sql.strategies.FullIterator;

import com.vividsolutions.jts.geom.Geometry;

public class SpatialIndex implements DataSourceIndex {

	public static final String SPATIAL_INDEX = SpatialIndex.class.getName();

	private DataSource ds;

	private Quadtree index;

	private int fieldId;

	private String fieldName;

	public void deleteRow(PhysicalDirection direction) throws DriverException {
		Metadata metadata = ds.getMetadata();
		for (int i = 0; i < metadata.getFieldCount(); i++) {
			if (metadata.getFieldType(i).getTypeCode() == Type.GEOMETRY) {
				Value v = direction.getFieldValue(i);
				if (!v.isNull()) {
					Geometry g = v.getAsGeometry();
					index.remove(g.getEnvelopeInternal(), direction);
				}

			}
		}
	}

	@SuppressWarnings("unchecked")
	public Iterator<PhysicalDirection> getIterator(IndexQuery query) {
		SpatialIndexQuery q = (SpatialIndexQuery) query;

		return new SpatialIterator(index.query(q.getArea()));
	}

	public void insertRow(PhysicalDirection direction, Value[] row)
			throws DriverException {
		Value newGeometry = row[fieldId];
		if (newGeometry.isNull()) {
			/*
			 * The index cannot hold null geometries
			 */
			return;
		} else {
			Geometry g = newGeometry.getAsGeometry();
			index.insert(g.getEnvelopeInternal(), direction);
		}
	}

	public void setFieldValue(Value oldGeometry, Value newGeometry,
			PhysicalDirection direction) {
		if (!oldGeometry.isNull()) {
			Geometry g = oldGeometry.getAsGeometry();
			index.remove(g.getEnvelopeInternal(), direction);
		}

		if (!newGeometry.isNull()) {
			Geometry g = newGeometry.getAsGeometry();
			index.insert(g.getEnvelopeInternal(), direction);
		}
	}

	public String getId() {
		return SPATIAL_INDEX;
	}

	public String getFieldName() {
		return fieldName;
	}

	public Iterator<PhysicalDirection> getAll() throws DriverException {
		return new FullIterator(ds);
	}

	public void buildIndex(DataSourceFactory dsf, DataSource dataSource,
			String fieldName) throws IndexException {
		try {
			this.fieldName = fieldName;
			dataSource.open();
			fieldId = dataSource.getFieldIndexByName(fieldName);
			if (dataSource.getMetadata().getFieldType(fieldId).getTypeCode() != Type.GEOMETRY) {
				throw new IndexException(fieldName + " is not spatial");
			}
			index = new Quadtree();
			for (int i = 0; i < dataSource.getRowCount(); i++) {
				Value fieldValue = dataSource.getFieldValue(i, fieldId);
				if (fieldValue.getType() != Type.NULL) {
					Geometry g = fieldValue.getAsGeometry();
					if (g != null) {
						if (!g.isEmpty()) {
							index.insert(g.getEnvelopeInternal(),
									new OriginalDirection(dataSource, i));
						}
					}
				}
			}
			dataSource.cancel();
		} catch (DriverLoadException e) {
			throw new IndexException(e);
		} catch (AlreadyClosedException e) {
			throw new IndexException(e);
		} catch (DriverException e) {
			throw new IndexException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public DataSourceIndex cloneIndex(DataSource ds) throws IndexException {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			save(bos);
			ByteArrayInputStream bis = new ByteArrayInputStream(bos
					.toByteArray());
			SpatialIndex index = new SpatialIndex();
			index.load(bis);
			return index;
		} catch (IOException e) {
			throw new IndexException(e);
		} catch (ClassNotFoundException e) {
			throw new IndexException(e);
		} catch (DriverException e) {
			throw new IndexException(e);
		}
	}

	public DataSourceIndex getNewInstance() {
		return new SpatialIndex();
	}

	public void save(File file) throws IndexException {
		try {
			save(new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			throw new IndexException(e);
		} catch (DriverException e) {
			throw new IndexException(e);
		}
	}

	private void save(OutputStream stream) throws DriverException {
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(stream);
			oos.writeObject(index);
			oos.writeObject(fieldId);
			oos.writeObject(fieldName);
			oos.close();
		} catch (IOException e) {
			throw new DriverException(e);
		}
	}

	private void load(InputStream is) throws IOException,
			ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(is);
		index = (Quadtree) ois.readObject();
		fieldId = (Integer) ois.readObject();
		fieldName = (String) ois.readObject();
	}

	public void load(File file) throws IndexException {
		try {
			load(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			throw new IndexException(e);
		} catch (IOException e) {
			throw new IndexException(e);
		} catch (ClassNotFoundException e) {
			throw new IndexException(e);
		}
	}

	public void setDataSource(DataSource dataSource) {
		this.ds = dataSource;
	}

}