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