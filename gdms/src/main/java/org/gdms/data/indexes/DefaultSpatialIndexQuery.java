package org.gdms.data.indexes;

import com.vividsolutions.jts.geom.Envelope;

public class DefaultSpatialIndexQuery implements SpatialIndexQuery {

	private Envelope area;

	private String fieldName;

	public DefaultSpatialIndexQuery(Envelope area, String fieldName) {
		super();
		this.area = area;
		this.fieldName = fieldName;
	}

	public Envelope getArea() {
		return area;
	}

	public String getFieldName() {
		return fieldName;
	}

	public boolean isStrict() {
		return false;
	}

}
