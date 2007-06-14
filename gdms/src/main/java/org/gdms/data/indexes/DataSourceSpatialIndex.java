/**
 *
 */
package org.gdms.data.indexes;

import java.util.Iterator;

import org.gdms.data.DataSource;
import org.gdms.data.edition.PhysicalDirection;
import org.gdms.data.indexes.quadtree.Quadtree;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.values.NullValue;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.spatial.GeometryValue;
import org.gdms.sql.strategies.FullIterator;
import org.gdms.sql.strategies.Row;

import com.vividsolutions.jts.geom.Geometry;

public class DataSourceSpatialIndex implements DataSourceIndex {

	private DataSource ds;

	private Quadtree index;

	private int fieldId;

	private String fieldName;

	public DataSourceSpatialIndex(int fieldId, String fieldName, Quadtree newIndex)
			throws DriverException {
		this.index = newIndex;
		this.fieldId = fieldId;
		this.fieldName = fieldName;
	}

	public void deleteRow(PhysicalDirection direction) throws DriverException {
		Metadata metadata = ds.getMetadata();
		for (int i = 0; i < metadata.getFieldCount(); i++) {
			if (metadata.getFieldType(i).getTypeCode() == Type.GEOMETRY) {
				Value v = direction.getFieldValue(i);
				if (!(v instanceof NullValue)) {
					Geometry g = ((GeometryValue) v).getGeom();
					index.remove(g.getEnvelopeInternal(), direction);
				}

			}
		}
	}

	@SuppressWarnings("unchecked")
	public Iterator<Row> getIterator(IndexQuery query) {
		SpatialIndexQuery q = (SpatialIndexQuery) query;

		return new SpatialIterator(ds, index.query(q.getArea()));
	}

	public void insertRow(PhysicalDirection direction, Value[] row)
			throws DriverException {
		Value newGeometry = row[fieldId];
		if (newGeometry instanceof NullValue) {
			/*
			 * The index cannot hold null geometries
			 */
			return;
		} else {
			Geometry g = ((GeometryValue) newGeometry).getGeom();
			index.insert(g.getEnvelopeInternal(), direction);
		}
	}

	public void setFieldValue(Value oldGeometry, Value newGeometry,
			PhysicalDirection direction) {
		if (!(oldGeometry instanceof NullValue)) {
			Geometry g = ((GeometryValue) oldGeometry).getGeom();
			index.remove(g.getEnvelopeInternal(), direction);
		}

		if (!(newGeometry instanceof NullValue)) {
			Geometry g = ((GeometryValue) newGeometry).getGeom();
			index.insert(g.getEnvelopeInternal(), direction);
		}
	}

	public String getId() {
		return SpatialIndex.SPATIAL_INDEX;
	}

	public String getFieldName() {
		return fieldName;
	}

	public Iterator<Row> getAll() throws DriverException {
		return new FullIterator(ds);
	}

	public void setDataSource(DataSource ds) {
		this.ds = ds;
	}

}