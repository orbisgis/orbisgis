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

import java.util.SortedSet;
import java.util.TreeSet;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.Point;

/**
 * The Vertex class embeds also all the edges (as a sorted set of normalized
 * LineSegments) that reach it (I mean: the point that corresponds to this
 * Vertex is the end of each edge LineSegment of this set).
 */

public class CDTVertex implements Comparable<CDTVertex> {
	private static GeometryFactory geometryFactory = new GeometryFactory();
	private Coordinate coordinate;
	private SortedSet<LineSegment> edges;

	public CDTVertex(final Coordinate point) {
		coordinate = point;
		edges = new TreeSet<LineSegment>();
	}

	public CDTVertex(final Point point) {
		this(point.getCoordinate());
	}

	public void addAnEdge(final LineSegment lineSegment) {
		lineSegment.normalize();
		if (lineSegment.p1.equals3D(coordinate)) {
			edges.add(lineSegment);
		}
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}

	public Envelope getEnvelope() {
		return geometryFactory.createPoint(coordinate).getEnvelopeInternal();
	}

	/**
	 * This getter method returns a sorted set of all the constraining edges
	 * that reach the current vertex.
	 * 
	 * @return
	 */
	public SortedSet<LineSegment> getEdges() {
		return edges;
	}

	public int compareTo(CDTVertex o) {
		// return coordinate.compareTo(o.getCoordinate());
		final double deltaY = coordinate.y - o.getCoordinate().y;

		if (0 > deltaY) {
			return -1;
		} else if (0 < deltaY) {
			return 1;
		} else {
			final double deltaX = coordinate.x - o.getCoordinate().x;
			if (0 > deltaX) {
				return -1;
			} else if (0 < deltaX) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((coordinate == null) ? 0 : coordinate.hashCode());
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
		final CDTVertex other = (CDTVertex) obj;
		if (coordinate == null) {
			if (other.coordinate != null)
				return false;
		} else if (!coordinate.equals3D(other.coordinate))
			return false;
		return true;
	}
}