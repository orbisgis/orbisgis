package org.gdms.geometryUtils;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPoint;

public class GeometryConverter {

	/**
	 * Convert a geometry into a MultiPoint
	 * @param geometry
	 * @return MutiPoint
	 */
	public static MultiPoint toMultiPoint(Geometry geometry) {

		return new GeometryFactory()
				.createMultiPoint(geometry.getCoordinates());
	}

}
