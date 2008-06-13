/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.triangulation.sweepLine4CDT;

import junit.framework.TestCase;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class CDTSweepLineTest extends TestCase {
	private static LineString initialLineString;
	private static LineString firstUpdateLineString_1;
	private static LineString firstUpdateLineString_2;
	private static LineString secondUpdateLineString_1;
	private static LineString secondUpdateLineString_2;

	private CDTSweepLine sweepLine;

	private static CDTVertex vertex1;
	private static CDTVertex vertex2;

	static {
		try {
			initialLineString = (LineString) new WKTReader()
					.read("LINESTRING(0 2, 2 3, 3 2, 5 3, 1 0, 5 1)");
			firstUpdateLineString_1 = (LineString) new WKTReader()
					.read("LINESTRING(0 2, 2 3, 3 2, 4 4, 5 3, 1 0, 5 1)");
			firstUpdateLineString_2 = (LineString) new WKTReader()
					.read("LINESTRING(0 2, 2 3, 3 2, 4 4, 5 4, 1 0, 5 1)");

			secondUpdateLineString_1 = (LineString) new WKTReader()
					.read("LINESTRING(0 2, 2 3, 4 4, 5 3, 1 0, 5 1)");
			secondUpdateLineString_2 = (LineString) new WKTReader()
					.read("LINESTRING(0 2, 2 3, 4 4, 5 4, 5 1)");

			vertex1 = new CDTVertex((Point) new WKTReader().read("POINT(4 4)"));
			vertex2 = new CDTVertex((Point) new WKTReader().read("POINT(5 4)"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	protected void setUp() throws Exception {
		super.setUp();
		sweepLine = new CDTSweepLine(new CDTVertex[] {
				new CDTVertex(new Coordinate(0, 2)),
				new CDTVertex(new Coordinate(2, 3)),
				new CDTVertex(new Coordinate(3, 2)),
				new CDTVertex(new Coordinate(5, 3)),
				new CDTVertex(new Coordinate(1, 0)),
				new CDTVertex(new Coordinate(5, 1)) }, null);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetLineString() {
		assertTrue(sweepLine.getLineString().equals(initialLineString));
	}

	// public void testVerticalProjectionPoint() throws ParseException {
	// final Coordinate c1 = sweepLine.verticalProjectionPoint(vertex1);
	// assertTrue(new Coordinate(4, 2.5).equals2D(c1));
	//
	// final Coordinate c2 = sweepLine.verticalProjectionPoint(vertex2);
	// assertTrue(new Coordinate(5, 3).equals2D(c2));
	// }
	//
	// public void testVerticalProjectionEdge() {
	// final Coordinate c1 = sweepLine.verticalProjectionPoint(vertex1);
	// final int[] t1 = sweepLine.verticalProjectionEdge(c1);
	// assertEquals(t1.length, 2);
	// assertEquals(t1[0], 2);
	// assertEquals(t1[1], 3);
	//
	// final Coordinate c2 = sweepLine.verticalProjectionPoint(vertex2);
	// final int[] t2 = sweepLine.verticalProjectionEdge(c2);
	// assertEquals(t2.length, 1);
	// assertEquals(t2[0], 3);
	// }

	public void testFirstUpdateOfAdvancingFront() {
		int idx = sweepLine.firstUpdateOfAdvancingFront(vertex1);
		assertEquals(idx, 3);
		// TODO : why is following assertion false ?
		// assertEquals(sweepLine.getLineString(), firstUpdateLineString_1);
		assertTrue(sweepLine.getLineString().equals(firstUpdateLineString_1));
		assertTrue(sweepLine.getLineString().equalsExact(
				firstUpdateLineString_1));

		idx = sweepLine.firstUpdateOfAdvancingFront(vertex2);
		assertEquals(idx, 4);
		// TODO : why is following assertion false ?
		// assertEquals(sweepLine.getLineString(), firstUpdateLineString_2);
		assertTrue(sweepLine.getLineString().equals(firstUpdateLineString_2));
		assertTrue(sweepLine.getLineString().equalsExact(
				firstUpdateLineString_2));
	}

	public void testSecondUpdateOfAdvancingFront() {
		sweepLine.secondUpdateOfAdvancingFront(sweepLine
				.firstUpdateOfAdvancingFront(vertex1));
		assertTrue(sweepLine.getLineString().equals(secondUpdateLineString_1));
		assertTrue(sweepLine.getLineString().equalsExact(
				secondUpdateLineString_1));

		sweepLine.secondUpdateOfAdvancingFront(sweepLine
				.firstUpdateOfAdvancingFront(vertex2));
		assertTrue(sweepLine.getLineString().equals(secondUpdateLineString_2));
		assertTrue(sweepLine.getLineString().equalsExact(
				secondUpdateLineString_2));
	}
}