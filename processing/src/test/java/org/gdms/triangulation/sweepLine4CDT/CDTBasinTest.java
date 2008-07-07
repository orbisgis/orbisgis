package org.gdms.triangulation.sweepLine4CDT;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

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
		basin1 = new CDTBasin(Arrays.asList(sl1), 1, 3, 5, null);

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
		basin2 = new CDTBasin(Arrays.asList(sl2), 1, 6, 15, null);
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
		System.out.println(basin1.getLinearRing());
		List<CDTEdge> edges1 = basin1.meshIntoEdges();
		for (CDTEdge edge : edges1) {
			System.out.printf("%d <-> %d\n", edge.getBegin(), edge.getEnd());
		}
		assertEquals(edges1.size(), 1);
		assertTrue(edges1.get(0).equals(new CDTEdge(2, 4)));

		basin2.normalize();
		System.out.println(basin2.getLinearRing());
		List<CDTEdge> edges2 = basin2.meshIntoEdges();
		for (CDTEdge edge : edges2) {
			System.out.printf("%d <-> %d\n", edge.getBegin(), edge.getEnd());
		}
		assertEquals(edges2.size(), 12);
		assertTrue(edges2.get(0).equals(new CDTEdge(1, 14)));
		assertTrue(edges2.get(1).equals(new CDTEdge(1, 13)));
		assertTrue(edges2.get(2).equals(new CDTEdge(2, 13)));
		assertTrue(edges2.get(3).equals(new CDTEdge(2, 12)));
		assertTrue(edges2.get(4).equals(new CDTEdge(2, 11)));
		assertTrue(edges2.get(5).equals(new CDTEdge(3, 11)));
		assertTrue(edges2.get(6).equals(new CDTEdge(3, 10)));
		assertTrue(edges2.get(7).equals(new CDTEdge(4, 10)));
		assertTrue(edges2.get(8).equals(new CDTEdge(4, 9)));
		assertTrue(edges2.get(9).equals(new CDTEdge(4, 8)));
		assertTrue(edges2.get(10).equals(new CDTEdge(5, 8)));
		assertTrue(edges2.get(11).equals(new CDTEdge(5, 7)));
	}

	public void testMeshIntoTriangles() {
		basin1.normalize();
		List<CDTTriangle> triangles1 = basin1.meshIntoTriangles();
		fail();
	}
}