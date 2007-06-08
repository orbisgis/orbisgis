package org.gdms;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;

public class Geometries {

	private static GeometryFactory gf = new GeometryFactory();

	public static LineString getLinestring() {
		return gf.createLineString(new Coordinate[] { new Coordinate(0, 0),
				new Coordinate(10, 0), new Coordinate(110, 0),
				new Coordinate(10, 240), });
	}

	public static LinearRing getLinearRing() {
		return gf.createLinearRing(new Coordinate[] { new Coordinate(0, 0),
				new Coordinate(10, 0), new Coordinate(110, 0),
				new Coordinate(10, 240), new Coordinate(0, 0) });
	}

	public static MultiLineString getMultilineString() {
		return gf.createMultiLineString(new LineString[] { getLinestring() });
	}

	public static Geometry getPolygon() {
		return gf.createPolygon(getLinearRing(), new LinearRing[0]);
	}

	public static Geometry getPoint() {
		return gf.createPoint(new Coordinate(10, 10));
	}

}
