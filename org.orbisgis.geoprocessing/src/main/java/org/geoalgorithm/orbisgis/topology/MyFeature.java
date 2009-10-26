package org.geoalgorithm.orbisgis.topology;

import com.vividsolutions.jts.geom.Geometry;

public class MyFeature {

	private Integer value;
	private Geometry point;

	public MyFeature(Integer value, Geometry point) {

		this.value=value;
		this.point = point;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public Geometry getGeometry() {
		return point;
	}

	public void setPoint(Geometry point) {
		this.point = point;
	}



}
