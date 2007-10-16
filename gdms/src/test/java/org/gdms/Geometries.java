package org.gdms;

import org.gdms.data.types.GeometryConstraint;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

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

	public static Point getPoint3D() {
		return gf.createPoint(new Coordinate(10, 10, 50));
	}

	public static Geometry getGeometry(int geometryType) {
		switch (geometryType) {
		case GeometryConstraint.LINESTRING_2D:
			return getLinestring();
		case GeometryConstraint.MULTI_LINESTRING_2D:
			return getMultilineString();
		case GeometryConstraint.POINT_2D:
			return getPoint();
		case GeometryConstraint.POINT_3D:
			return getPoint3D();
		case GeometryConstraint.POLYGON_2D:
			return getPolygon();
		case GeometryConstraint.MIXED:
			return getPoint();
		default:
			throw new RuntimeException("To implement");
		}
	}

	public static LineString getLineString3D() {
		return gf.createLineString(new Coordinate[] { new Coordinate(0, 0, 0),
				new Coordinate(10, 2, 5), new Coordinate(110, 0, 4),
				new Coordinate(10, 240, 10), });
	}

	public static Polygon getPolygon3D() {
		return gf.createPolygon(getLinearRing3D(), new LinearRing[0]);
	}

	private static LinearRing getLinearRing3D() {
		return gf.createLinearRing(new Coordinate[] { new Coordinate(0, 2, 0),
				new Coordinate(10, 2, 0), new Coordinate(110, 22, 0),
				new Coordinate(10, 62, 240), new Coordinate(0, 2, 0) });
	}

	public static Geometry getMultiPoint3D() {
		Point[] points = new Point[2];
		points[0] = getPoint3D();
		points[1] = gf.createPoint(new Coordinate(23, 325, 74));
		return gf.createMultiPoint(points);
	}

	public static Geometry getMultilineString3D() {
		return gf.createMultiLineString(new LineString[] { getLineString3D(),
				getLineString3D() });
	}

	public static Geometry getMultiPolygon3D() {
		return gf.createMultiPolygon(new Polygon[] { getPolygon3D(),
				getPolygon3D() });
	}

}
