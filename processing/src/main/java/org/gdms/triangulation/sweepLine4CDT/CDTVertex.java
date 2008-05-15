package org.gdms.triangulation.sweepLine4CDT;

import java.util.SortedSet;
import java.util.TreeSet;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Triangle;

/**
 * The Vertex class embeds also : all the edges (as a sorted set of normalize
 * LineSegment) that reach it (I mean: the point that corresponds to this Vertex
 * is the end of each edge LineSegment of this set) and all the corresponding
 * triangles (as a sorted set of Triangle).
 */

public class CDTVertex implements Comparable<CDTVertex> {
	private Coordinate coordinate;
	private SortedSet<LineSegment> edges;
	private SortedSet<Triangle> triangles;

	public CDTVertex(final Point point) {
		coordinate = point.getCoordinate();
		edges = new TreeSet<LineSegment>();
		triangles = new TreeSet<Triangle>();
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

	public SortedSet<LineSegment> getEdges() {
		return edges;
	}

	public SortedSet<Triangle> getTriangles() {
		return triangles;
	}

	public int compareTo(CDTVertex o) {
		// return coordinate.compareTo(o.getCoordinate());
		final double deltaY = coordinate.y - o.getCoordinate().y;

		if (0 < deltaY) {
			return -1;
		} else if (0 > deltaY) {
			return 1;
		} else {
			final double deltaX = coordinate.x - o.getCoordinate().x;
			if (0 < deltaX) {
				return -1;
			} else if (0 > deltaX) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CDTVertex) {
			return coordinate.equals3D(((CDTVertex) obj).getCoordinate());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		// TODO is it a good hashcode ?
		return (int) (coordinate.x + coordinate.y);
	}
}