package org.gdms.triangulation.sweepLine4CDT;

import junit.framework.TestCase;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class SweepLineTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testProjectAVertexOnSweepLine() throws ParseException {
		final LineString ls = (LineString) new WKTReader()
				.read("LINESTRING(0 2, 2 3, 3 2, 5 3, 1 0, 5 1)");
		final Point p = (Point) new WKTReader().read("POINT(4 4)");
		final Coordinate c = new SweepLine(ls)
				.projectAVertexOnSweepLine(new Vertex(p));

		assertTrue(new Coordinate(4, 2.5).equals2D(c));
	}
}