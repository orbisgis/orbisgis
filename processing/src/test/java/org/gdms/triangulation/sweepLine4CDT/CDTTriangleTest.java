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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

import junit.framework.TestCase;

public class CDTTriangleTest extends TestCase {
	private static final double EPSILON = 1E-4;
	private static Coordinate p0 = new Coordinate(0, 0);
	private static Coordinate p1 = new Coordinate(10, 0);
	private static Coordinate p2 = new Coordinate(0, 10);
	private CDTTriangle dtTriangle;
	private CDTTriangle cdtTriangle;

	protected void setUp() throws Exception {
		super.setUp();

		dtTriangle = new CDTTriangle(new CDTVertex(p0), new CDTVertex(p1),
				new CDTVertex(p2), null);

		CDTVertex v0 = new CDTVertex(p0);
		CDTVertex v1 = new CDTVertex(p1);
		CDTVertex v2 = new CDTVertex(p2);
		LineSegment ls = new LineSegment(v1.getCoordinate(), v2.getCoordinate());
		v1.addAnEdge(ls);
		v2.addAnEdge(ls);
		cdtTriangle = new CDTTriangle(v0, v1, v2, null);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testEquals() {
		assertTrue(cdtTriangle.equals(dtTriangle));

		CDTTriangle tmpTriangle = new CDTTriangle(new CDTVertex(p1),
				new CDTVertex(p0), new CDTVertex(p2), null);
		assertTrue(tmpTriangle.equals(dtTriangle));

		Coordinate p2b = new Coordinate(0 + EPSILON, 10);
		tmpTriangle = new CDTTriangle(new CDTVertex(p1), new CDTVertex(p0),
				new CDTVertex(p2b), null);
		assertFalse(tmpTriangle.equals(dtTriangle));
	}

	public void testLegalization() {
	}

	public void testRespectDelaunayProperty() {
		assertFalse(dtTriangle.respectDelaunayProperty(new Coordinate()));
		assertFalse(dtTriangle.respectDelaunayProperty(new Coordinate(
				10 + EPSILON / 10, 10 + EPSILON / 10)));
		assertTrue(dtTriangle.respectDelaunayProperty(new Coordinate(
				10 + EPSILON, 10 + EPSILON)));
	}

	public void testRespectWeakerDelaunayProperty() {
		Coordinate newVertex = new Coordinate(6, 6);
		assertFalse(dtTriangle.respectWeakerDelaunayProperty(newVertex));
		assertTrue(cdtTriangle.respectWeakerDelaunayProperty(newVertex));
	}

	public void testPointsAreLocatedOnTheSameSideOfTheLineConstraint() {
		try {
			CDTTriangle.pointsAreLocatedOnEachSidesOfTheAxis(p0, p1,
					p2, new Coordinate());
			fail();
		} catch (RuntimeException e) {
		}

		assertFalse(CDTTriangle.pointsAreLocatedOnEachSidesOfTheAxis(
				p0, p1, p2, p2));
		assertFalse(CDTTriangle.pointsAreLocatedOnEachSidesOfTheAxis(
				p0, p1, p2, new Coordinate(-0.00025, 10.5)));
		assertTrue(CDTTriangle.pointsAreLocatedOnEachSidesOfTheAxis(
				p0, p1, p2, new Coordinate(5, -15)));
	}
}