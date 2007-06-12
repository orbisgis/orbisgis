/**
 *
 */
package org.gdms.data.indexes;

import java.util.Iterator;

import org.gdms.data.DataSource;
import org.gdms.data.indexes.quadtree.Quadtree;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.values.NullValue;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.spatial.GeometryValue;
import org.gdms.spatial.SpatialDataSource;
import org.gdms.sql.instruction.Row;
import org.gdms.sql.instruction.SpatialIterator;
import org.gdms.sql.strategies.FullIterator;

import com.vividsolutions.jts.geom.Geometry;

public class DataSourceSpatialIndex implements DataSourceIndex {

	private SpatialDataSource ds;

	private Quadtree index;

	private int fieldId;

	private String fieldName;

	public DataSourceSpatialIndex(DataSource ds, int fieldId, Quadtree newIndex)
			throws DriverException {
		this.ds = (SpatialDataSource) ds;
		this.index = newIndex;
		this.fieldId = fieldId;
		this.fieldName = ds.getFieldName(fieldId);
	}

	public void beforeDeletingRow(long rowIndex) throws DriverException {
		Metadata metadata = ds.getMetadata();
		for (int i = 0; i < metadata.getFieldCount(); i++) {
			if (metadata.getFieldType(i).getTypeCode() == Type.GEOMETRY) {
				Value v = ds.getFieldValue(rowIndex, i);
				if (!(v instanceof NullValue)) {
					Geometry g = ((GeometryValue) v).getGeom();
					index.remove(g.getEnvelopeInternal(), ds.getFID(rowIndex));
				}

			}
		}
	}

	@SuppressWarnings("unchecked")
	public Iterator<Row> getIterator(IndexQuery query) {
		SpatialIndexQuery q = (SpatialIndexQuery) query;

		return new SpatialIterator(ds, index.query(q.getArea()));
	}

	public void afterInsertingRow(long rowPosition, Value[] row)
			throws DriverException {
		Value newGeometry = row[fieldId];
		if (newGeometry instanceof NullValue) {
			/*
			 * The index cannot hold null geometries
			 */
			return;
		} else {
			Geometry g = ((GeometryValue) newGeometry).getGeom();
			index.insert(g.getEnvelopeInternal(), ds.getFID(rowPosition));
		}
	}

	public void beforeSettingFieldValue(Value oldGeometry, Value newGeometry,
			long rowIndex) {
		if (!(oldGeometry instanceof NullValue)) {
			Geometry g = ((GeometryValue) oldGeometry).getGeom();
			index.remove(g.getEnvelopeInternal(), ds.getFID(rowIndex));
		}

		if (!(newGeometry instanceof NullValue)) {
			Geometry g = ((GeometryValue) newGeometry).getGeom();
			index.insert(g.getEnvelopeInternal(), ds.getFID(rowIndex));
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

}