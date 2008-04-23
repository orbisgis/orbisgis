package org.gdms.triangulation.sweepLine4CDT;

import junit.framework.TestCase;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class SweepLineTest extends TestCase {
	private static LineString initialLineString;
	private static LineString firstUpdateLineString_1;
	private static LineString firstUpdateLineString_2;

	private SweepLine sweepLine;

	private static Vertex vertex1;
	private static Vertex vertex2;

	static {
		try {
			initialLineString = (LineString) new WKTReader()
					.read("LINESTRING(0 2, 2 3, 3 2, 5 3, 1 0, 5 1)");

			firstUpdateLineString_1 = (LineString) new WKTReader()
					.read("LINESTRING(0 2, 2 3, 3 2, 4 4, 5 3, 1 0, 5 1)");
			firstUpdateLineString_2 = (LineString) new WKTReader()
					.read("LINESTRING(0 2, 2 3, 3 2, 4 4, 5 4, 1 0, 5 1)");

			vertex1 = new Vertex((Point) new WKTReader().read("POINT(4 4)"));
			vertex2 = new Vertex((Point) new WKTReader().read("POINT(5 4)"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	protected void setUp() throws Exception {
		super.setUp();

		sweepLine = new SweepLine(initialLineString);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testVerticalProjectionPoint() throws ParseException {
		final Coordinate c1 = sweepLine.verticalProjectionPoint(vertex1);
		assertTrue(new Coordinate(4, 2.5).equals2D(c1));

		final Coordinate c2 = sweepLine.verticalProjectionPoint(vertex2);
		assertTrue(new Coordinate(5, 3).equals2D(c2));
	}

	public void testVerticalProjectionEdge() {
		final Coordinate c1 = sweepLine.verticalProjectionPoint(vertex1);
		final int[] t1 = sweepLine.verticalProjectionEdge(c1);
		assertEquals(t1.length, 2);
		assertEquals(t1[0], 2);
		assertEquals(t1[1], 3);

		final Coordinate c2 = sweepLine.verticalProjectionPoint(vertex2);
		final int[] t2 = sweepLine.verticalProjectionEdge(c2);
		assertEquals(t2.length, 1);
		assertEquals(t2[0], 3);
	}

	public void testFirstUpdateOfAdvancingFront() {
		sweepLine.firstUpdateOfAdvancingFront(vertex1);
		// TODO : why is following assertion false ?
		// assertEquals(sweepLine.getLineString(), firstUpdateLineString_1);
		assertTrue(sweepLine.getLineString().equals(firstUpdateLineString_1));
		assertTrue(sweepLine.getLineString().equalsExact(
				firstUpdateLineString_1));

		sweepLine.firstUpdateOfAdvancingFront(vertex2);
		// TODO : why is following assertion false ?
		// assertEquals(sweepLine.getLineString(), firstUpdateLineString_2);
		assertTrue(sweepLine.getLineString().equals(firstUpdateLineString_2));
		assertTrue(sweepLine.getLineString().equalsExact(
				firstUpdateLineString_2));
	}
}