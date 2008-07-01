package org.gdms.sql.customQuery.spatial.raster.convert;

import java.util.Arrays;
import java.util.LinkedList;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class SimplificationUtilities {
	private static final GeometryFactory gf = new GeometryFactory();
	private static final double EPSILON = 1.0E-4;

	public static boolean floatingPointNumbersEquality(final double a,
			final double b) {
		if (Double.isNaN(a)) {
			return Double.isNaN(b);
		} else {
			return Math.abs(a - b) < EPSILON;
		}
	}

	public static boolean areAligned(Coordinate a, Coordinate b, Coordinate c) {
		Coordinate u = new Coordinate(b.x - a.x, b.y - a.y, b.z - a.z);
		Coordinate v = new Coordinate(c.x - b.x, c.y - b.y, c.z - b.z);
		Coordinate crossProd = new Coordinate(u.y * v.z - u.z * v.y, -u.x * v.z
				+ u.z * v.x, u.x * v.y - u.y * v.x);
		if ((Double.isNaN(crossProd.x) || floatingPointNumbersEquality(
				crossProd.x, 0))
				&& (Double.isNaN(crossProd.y) || floatingPointNumbersEquality(
						crossProd.y, 0))
				&& (Double.isNaN(crossProd.z) || floatingPointNumbersEquality(
						crossProd.z, 0))) {
			return true;
		}
		return false;
	}

	public static LineString simplifyGeometry(LineString lineString) {
		final LinkedList<Coordinate> coordinates = new LinkedList<Coordinate>(
				Arrays.asList(lineString.getCoordinates()));
		int i = 0;
		while (i <= coordinates.size() - 3) {
			if (areAligned(coordinates.get(i), coordinates.get(i + 1),
					coordinates.get(i + 2))) {
				coordinates.remove(i + 1);
			} else {
				i++;
			}
		}
		return gf.createLineString(coordinates.toArray(new Coordinate[0]));
	}

	public static Polygon simplifyGeometry(Polygon polygon) {
		LineString tmpShell = simplifyGeometry(polygon.getExteriorRing());
		LinearRing shell = gf.createLinearRing(tmpShell.getCoordinates());
		LinearRing[] holes = new LinearRing[polygon.getNumInteriorRing()];
		for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
			LineString tmpHole = simplifyGeometry(polygon.getInteriorRingN(i));
			holes[i] = gf.createLinearRing(tmpHole.getCoordinates());
		}
		return gf.createPolygon(shell, holes);
	}

	private static GeometryCollection simplifyGeometry(GeometryCollection gc) {
		final Geometry[] result = new Geometry[gc.getNumGeometries()];
		for (int i = 0; i < gc.getNumGeometries(); i++) {
			result[i] = simplifyGeometry(gc.getGeometryN(i));
		}
		return gf.createGeometryCollection(result);
	}

	public static Geometry simplifyGeometry(Geometry inputGeometry) {
		if (inputGeometry instanceof Polygon) {
			return simplifyGeometry((Polygon) inputGeometry);
		} else if (inputGeometry instanceof GeometryCollection) {
			return simplifyGeometry((GeometryCollection) inputGeometry);
		} else {
			throw new IllegalArgumentException(
					"simplifyGeometry should only deal with [Multi-]Polygon(s) !");
		}
	}

	public static void main(String[] args) throws ParseException {
		LineString ls = (LineString) new WKTReader()
				.read("LINESTRING(0 999, 0 2, 0 1, 0 0, 1 0, 2 0 , 3 0, 15 0)");
		System.err.println(ls.toString());
		System.out.println(simplifyGeometry(ls).toString());
	}
}