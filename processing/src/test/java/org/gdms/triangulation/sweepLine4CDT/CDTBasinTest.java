package org.gdms.triangulation.sweepLine4CDT;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.gdms.triangulation.sweepLine4CDT.CDTBasin.Edge;

import com.vividsolutions.jts.geom.Coordinate;

public class CDTBasinTest extends TestCase {
	private CDTBasin basin1;
	private CDTBasin basin2;

	protected void setUp() throws Exception {
		super.setUp();

		CDTVertex[] sl1 = new CDTVertex[] {
				new CDTVertex(new Coordinate(0, 1)),
				new CDTVertex(new Coordinate(2, 2)),
				new CDTVertex(new Coordinate(5, 1)),
				new CDTVertex(new Coordinate(6, 0)),
				new CDTVertex(new Coordinate(7, 4)),
				new CDTVertex(new Coordinate(13, 5)) };
		basin1 = new CDTBasin(Arrays.asList(sl1), 1, 3, 5);

		CDTVertex[] sl2 = new CDTVertex[] {
				new CDTVertex(new Coordinate(0, 7)),
				new CDTVertex(new Coordinate(1, 9)),
				new CDTVertex(new Coordinate(2, 7)),
				new CDTVertex(new Coordinate(3, 5)),
				new CDTVertex(new Coordinate(4, 4)),
				new CDTVertex(new Coordinate(5, 2)),
				new CDTVertex(new Coordinate(6, 0)),
				new CDTVertex(new Coordinate(7, 1)),
				new CDTVertex(new Coordinate(7, 3)),
				new CDTVertex(new Coordinate(8, 4)),
				new CDTVertex(new Coordinate(8, 5)),
				new CDTVertex(new Coordinate(9, 6)),
				new CDTVertex(new Coordinate(9, 7)),
				new CDTVertex(new Coordinate(10, 8)),
				new CDTVertex(new Coordinate(10, 9)),
				new CDTVertex(new Coordinate(16, 10)) };
		basin2 = new CDTBasin(Arrays.asList(sl2), 1, 6, 15);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetLinearRing() {
		assertFalse(basin1.getLinearRing().isValid());
		assertTrue(basin2.getLinearRing().isValid());
	}

	public void testGetPolygon() {
		assertFalse(basin1.getPolygon().isValid());
		assertTrue(basin2.getPolygon().isValid());
	}

	public void testNormalize() {
		assertEquals(basin1.getBasinLeftBorder(), 1);
		assertEquals(basin1.getBasinBed(), 3);
		assertEquals(basin1.getBasinRightBorder(), 5);
		basin1.normalize();
		assertEquals(basin1.getBasinLeftBorder(), 1);
		assertEquals(basin1.getBasinBed(), 3);
		assertEquals(basin1.getBasinRightBorder(), 4);

		assertEquals(basin2.getBasinLeftBorder(), 1);
		assertEquals(basin2.getBasinBed(), 6);
		assertEquals(basin2.getBasinRightBorder(), 15);
		basin2.normalize();
		assertEquals(basin2.getBasinLeftBorder(), 1);
		assertEquals(basin2.getBasinBed(), 6);
		assertEquals(basin2.getBasinRightBorder(), 15);
	}

	public void testMeshIntoEdges() {
		basin1.normalize();
		basin1.printU();
		System.out.println(basin1.getLinearRing());
		List<Edge> edges1 = basin1.meshIntoEdges();
		for (Edge edge : edges1) {
			System.err.printf("%d <-> %d\n", edge.begin, edge.end);
		}

		basin2.normalize();
		basin2.printU();
		System.out.println(basin2.getLinearRing());
		List<Edge> edges2 = basin2.meshIntoEdges();
		for (Edge edge : edges2) {
			System.err.printf("%d <-> %d\n", edge.begin, edge.end);
		}
	}
}