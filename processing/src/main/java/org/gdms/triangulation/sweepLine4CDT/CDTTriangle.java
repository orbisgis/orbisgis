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

import java.util.Arrays;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

public class CDTTriangle implements Comparable<CDTTriangle> {
	private final static GeometryFactory gf = new GeometryFactory();

	private CDTOrderedSetOfVertices orderedSetOfVertices;
	private Polygon pTriangle;
	private CDTCircumCircle circumCircle;
	public int p0;
	public int p1;
	public int p2;

	public CDTTriangle(final CDTOrderedSetOfVertices orderedSetOfVertices,
			final int v0, final int v1, final int v2) {
		this.orderedSetOfVertices = orderedSetOfVertices;

		// normalization process
		int[] tmp = new int[] { v0, v1, v2 };
		Arrays.sort(tmp);
		p0 = tmp[0];
		p1 = tmp[1];
		p2 = tmp[2];

		pTriangle = gf.createPolygon(gf.createLinearRing(new Coordinate[] {
				orderedSetOfVertices.get(p0), orderedSetOfVertices.get(p1),
				orderedSetOfVertices.get(p2), orderedSetOfVertices.get(p0) }),
				null);
		circumCircle = new CDTCircumCircle(orderedSetOfVertices.get(p0),
				orderedSetOfVertices.get(p1), orderedSetOfVertices.get(p2));
	}

	/**
	 * This method tests the classical "empty circumcircle rule". That is,
	 * current triangle is Delaunay if the unique circle on which lie its three
	 * vertices (ie the circumcircle) does not contain any other vertex.
	 * 
	 * @param vertexIndex
	 * @return
	 */
	public boolean respectDelaunayProperty(final int vertexIndex) {
		if (circumCircle.contains(orderedSetOfVertices.get(vertexIndex))) {
			if (pTriangle.contains(orderedSetOfVertices.getPoint(vertexIndex))) {
				throw new RuntimeException("Unreachable code");
			}
			return false;
		}
		return true;
	}

	public Polygon getPolygon() {
		return gf.createPolygon(gf.createLinearRing(new Coordinate[] {
				orderedSetOfVertices.get(p0), orderedSetOfVertices.get(p1),
				orderedSetOfVertices.get(p2), orderedSetOfVertices.get(p0) }),
				null);
	}

	@Override
	public String toString() {
		return "[ " + p0 + " " + p1 + " " + p2 + " ]";
		// return getPolygon().toString();
	}

	public boolean isAVertex(final int vertexIndex) {
		return (vertexIndex == p0) || (vertexIndex == p1)
				|| (vertexIndex == p2);
	}

	/**
	 * @param a
	 * @param b
	 * @return
	 */
	public int getThirdVertex(final int a, final int b) {
		if (a == p0) {
			if (b == p1) {
				return p2;
			} else if (b == p2) {
				return p1;
			}
		} else if (b == p0) {
			if (a == p1) {
				return p2;
			} else if (a == p2) {
				return p1;
			}
		} else if ((a == p1) && (b == p2)) {
			return p0;
		}
		throw new RuntimeException("Unreachable code");
	}

	public Envelope getEnvelope() {
		return getPolygon().getEnvelopeInternal();
	}

	public CDTCircumCircle getCircumCircle() {
		return circumCircle;
	}

	public int compareTo(final CDTTriangle other) {
		final int tmp0 = p0 - other.p0;
		if (0 == tmp0) {
			final int tmp1 = p1 - other.p1;
			if (0 == tmp1) {
				return p2 - other.p2;
			}
			return tmp1;
		}
		return tmp0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + p0;
		result = prime * result + p1;
		result = prime * result + p2;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final CDTTriangle other = (CDTTriangle) obj;
		if (p0 != other.p0)
			return false;
		if (p1 != other.p1)
			return false;
		if (p2 != other.p2)
			return false;
		return true;
	}
}