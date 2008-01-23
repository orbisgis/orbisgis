package org.orbisgis.geoview.rasterProcessing.tin;

import com.vividsolutions.jts.geom.Coordinate;

class Vertex {
	Coordinate coordinate;
	long gid;

	Vertex(final Coordinate coordinate, final long gid) {
		this.coordinate = coordinate;
		this.gid = gid;
	}
}