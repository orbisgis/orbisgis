package org.gdms.utilities;

import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateList;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class RandomGeometryUtilities {
	private static final Random RAND = new Random();
	private static final GeometryFactory gf = new GeometryFactory();
	private int maxNodesPerLineMinus3;
	private int maxHolesPerPolygon;

	public RandomGeometryUtilities() {
		this(12, 4);
	}

	public RandomGeometryUtilities(final int maxNodesPerLineMinus3,
			final int maxHolesPerPolygon) {
		this.maxNodesPerLineMinus3 = maxNodesPerLineMinus3;
		this.maxHolesPerPolygon = maxHolesPerPolygon;
	}

	private int getNumberOfNodesPerLine() {
		// In a LinearRing number of points must be 0 or >3
		return RAND.nextInt(maxNodesPerLineMinus3) + 3;
	}

	private int getNumberOfHolesPerPolygon() {
		return RAND.nextInt(maxHolesPerPolygon);
	}

	public Coordinate nextCoordinate() {
		return new Coordinate(RAND.nextLong(), RAND.nextLong(), RAND.nextLong());
	}

	public Coordinate nextCoordinate(final Envelope envelope) {
		return new Coordinate(RAND.nextInt((int) envelope.getWidth())
				+ envelope.getMinX(), RAND.nextInt((int) envelope.getHeight())
				+ envelope.getMinY(), RAND.nextLong());
	}

	public Coordinate[] nextCoordinates(final int n) {
		final Coordinate[] result = new Coordinate[n];
		for (int i = 0; i < n; i++) {
			result[i] = nextCoordinate();
		}
		return result;
	}

	public Point nextPoint() {
		return gf.createPoint(nextCoordinate());
	}

	public Point nextPoint(final Envelope envelope) {
		return gf.createPoint(nextCoordinate(envelope));
	}

	public Point[] nextPoints(final int n) {
		final Point[] result = new Point[n];
		for (int i = 0; i < n; i++) {
			result[i] = nextPoint();
		}
		return result;
	}

	public LineSegment nextLineSegment() {
		return new LineSegment(nextCoordinate(), nextCoordinate());
	}

	public LineSegment[] nextLineSegments(final int n) {
		final LineSegment[] result = new LineSegment[n];
		for (int i = 0; i < n; i++) {
			result[i] = nextLineSegment();
		}
		return result;
	}

	public LineString nextLineString() {
		final int n = getNumberOfNodesPerLine();
		final SortedSet<Coordinate> nodes = new TreeSet<Coordinate>();
		while (n > nodes.size()) {
			nodes.add(nextCoordinate());
		}
		return gf.createLineString(nodes.toArray(new Coordinate[0]));
	}

	public LineString nextLineString(final Envelope envelope) {
		final int n = getNumberOfNodesPerLine();
		final SortedSet<Coordinate> nodes = new TreeSet<Coordinate>();
		while (n > nodes.size()) {
			nodes.add(nextCoordinate(envelope));
		}
		return gf.createLineString(nodes.toArray(new Coordinate[0]));
	}

	public LineString[] nextLineStrings(final int n) {
		final LineString[] result = new LineString[n];
		for (int i = 0; i < n; i++) {
			result[i] = nextLineString();
		}
		return result;
	}

	public LinearRing nextLinearRing() {
		LinearRing result;
		do {
			final CoordinateList cl = new CoordinateList(nextLineString()
					.getCoordinates());
			cl.closeRing();
			result = gf.createLinearRing(cl.toCoordinateArray());
		} while (!result.isValid());
		return result;
	}

	public LinearRing nextLinearRing(final Envelope envelope) {
		LinearRing result;
		do {
			final CoordinateList cl = new CoordinateList(nextLineString(
					envelope).getCoordinates());
			cl.closeRing();
			result = gf.createLinearRing(cl.toCoordinateArray());
		} while (!result.isValid());
		return result;
	}

	public LinearRing[] nextLinearRings(final int n) {
		final LinearRing[] result = new LinearRing[n];
		for (int i = 0; i < n; i++) {
			result[i] = nextLinearRing();
		}
		return result;
	}

	public Polygon nextNoHolePolygon() {
		return gf.createPolygon(nextLinearRing(), null);
	}

	public Polygon nextNoHolePolygon(final Envelope envelope) {
		return gf.createPolygon(nextLinearRing(envelope), null);
	}

	public Polygon[] nextNoHolePolygons(final int n) {
		final Polygon[] result = new Polygon[n];
		for (int i = 0; i < n; i++) {
			result[i] = nextNoHolePolygon();
		}
		return result;
	}

	 public Polygon nextPolygon() {
		final LinearRing shell = nextLinearRing();
		final int nbHoles = 1; // getNumberOfHolesPerPolygon();
		final LinearRing[] holes = new LinearRing[nbHoles];
		for (int i = 0; i < nbHoles; i++) {
			do {
				holes[i] = nextLinearRing(shell.getEnvelopeInternal());
			} while (!shell.contains(holes[i]));
		}
		return gf.createPolygon(shell, holes);
	}
	//
	// public Polygon[] nextPolygons(final int n) {
	// final Polygon[] result = new Polygon[n];
	// for (int i = 0; i < n; i++) {
	// result[i] = nextPolygon();
	// }
	// return result;
	// }

	public Geometry nextGeometry() {
		switch (RAND.nextInt(4)) {
		case 0:
			return nextPoint();
		case 1:
			return nextLineString();
		case 2:
			return nextLinearRing();
		case 3:
			return nextNoHolePolygon();
			// return nextPolygon();
		}
		throw new RuntimeException("Unreachable code");
	}

	public Geometry[] nextGeometries(final int n) {
		final Geometry[] result = new Geometry[n];
		for (int i = 0; i < n; i++) {
			result[i] = nextGeometry();
		}
		return result;
	}
}