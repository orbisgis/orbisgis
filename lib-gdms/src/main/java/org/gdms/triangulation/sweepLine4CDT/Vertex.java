package org.gdms.triangulation.sweepLine4CDT;

import java.util.Set;
import java.util.TreeSet;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.Point;

/**
 * The Vertex class embeds also all the edges, set of normalize LineSegment,
 * that reach it (I mean: the point that corresponds to this Vertex is the end
 * of each edge LineSegment of this set).
 */

public class Vertex implements Comparable<Vertex> {
	private Coordinate coordinate;
	private Set<LineSegment> edges;

	public Vertex(final Point point) {
		coordinate = point.getCoordinate();
		edges = new TreeSet<LineSegment>();
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}

	public void addAnEdge(final LineSegment lineSegment) {
		lineSegment.normalize();
		if (lineSegment.p1.equals3D(coordinate)) {
			edges.add(lineSegment);
		}
	}

	public Set<LineSegment> getEdges() {
		return edges;
	}

	@Override
	public boolean equals(Object obj) {
		return coordinate.equals(((Vertex) obj).getCoordinate());
	}

	public int compareTo(Vertex o) {
		return coordinate.compareTo(o.getCoordinate());
	}
}