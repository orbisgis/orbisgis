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

public class CDTCircumCircleTest extends TestCase {
	private static final double EPSILON = 1E-4;
	private static Coordinate p0 = new Coordinate(0, 0);
	private static Coordinate p1 = new Coordinate(10, 0);
	private static Coordinate p2 = new Coordinate(0, 10);
	private CDTCircumCircle cdtCircumCircle;

	protected void setUp() throws Exception {
		super.setUp();
		cdtCircumCircle = new CDTCircumCircle(p0, p1, p2);
	}

	public void testContains() {
		assertTrue(cdtCircumCircle.contains(new Coordinate()));
		assertTrue(cdtCircumCircle.contains(new Coordinate(10 + EPSILON / 10,
				10 + EPSILON / 10)));
		assertFalse(cdtCircumCircle.contains(new Coordinate(10 + EPSILON,
				10 + EPSILON)));
	}

	public void testGetEnvelopeInternal() {
		assertEquals(cdtCircumCircle.getEnvelopeInternal().getMinX(),
				cdtCircumCircle.getCentre().x - cdtCircumCircle.getRadius());
		assertEquals(cdtCircumCircle.getEnvelopeInternal().getMinY(),
				cdtCircumCircle.getCentre().y - cdtCircumCircle.getRadius());

		assertEquals(cdtCircumCircle.getEnvelopeInternal().getMaxX(),
				cdtCircumCircle.getCentre().x + cdtCircumCircle.getRadius());
		assertEquals(cdtCircumCircle.getEnvelopeInternal().getMaxY(),
				cdtCircumCircle.getCentre().y + cdtCircumCircle.getRadius());
	}
}