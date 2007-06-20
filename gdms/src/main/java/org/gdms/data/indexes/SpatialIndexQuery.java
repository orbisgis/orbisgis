package org.gdms.data.indexes;

import com.vividsolutions.jts.geom.Envelope;

public class SpatialIndexQuery implements IndexQuery {

	private Envelope area;

	private String fieldName;

	public SpatialIndexQuery(Envelope area, String fieldName) {
		super();
		this.area = area;
		this.fieldName = fieldName;
	}

	public String getIndexId() {
		return SpatialIndex.SPATIAL_INDEX;
	}

	public Envelope getArea() {
		return area;
	}

	public String getFieldName() {
		return fieldName;
	}

}
