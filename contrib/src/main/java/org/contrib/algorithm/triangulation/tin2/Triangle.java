package org.contrib.algorithm.triangulation.tin2;

import java.util.Formatter;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

public class Triangle {
	private final static GeometryFactory gf = new GeometryFactory();
	public static int NO_NEIGHBOUR = -1;

	private SetOfVertices setOfVertices;
	private int[] vtx;
	private int[] neighbour;

	public Triangle(final SetOfVertices setOfVertices, final int vtx0,
			final int vtx1, final int vtx2) {
		this(setOfVertices, vtx0, vtx1, vtx2, NO_NEIGHBOUR, NO_NEIGHBOUR,
				NO_NEIGHBOUR);
	}

	public Triangle(final SetOfVertices setOfVertices, final int vtx0,
			final int vtx1, final int vtx2, final int neighbour0,
			final int neighbour1, final int neighbour2) {
		this.setOfVertices = setOfVertices;

		final int orientation = CGAlgorithms.computeOrientation(setOfVertices
				.getCoordinate(vtx0), setOfVertices.getCoordinate(vtx1),
				setOfVertices.getCoordinate(vtx2));

		if ((CGAlgorithms.COUNTERCLOCKWISE == orientation)
				|| (CGAlgorithms.COLLINEAR == orientation)) {
			vtx = new int[] { vtx0, vtx1, vtx2 };
			neighbour = new int[] { neighbour0, neighbour1, neighbour2 };
		} else {
			vtx = new int[] { vtx0, vtx2, vtx1 };
			neighbour = new int[] { neighbour0, neighbour2, neighbour1 };
		}
	}

	public int getNeighbour(final int i) {
		return neighbour[i];
	}

	public int[] getVerticesForEdge(int edgeIdx) {
		return new int[] { vtx[edgeIdx], vtx[(edgeIdx + 1) % 3] };
	}

	public void setNeighbourForEdge(final int v1, final int v2,
			final int neighIdx) {
		if ((v1 == vtx[0]) && (v2 == vtx[1])) {
			neighbour[0] = neighIdx;
		} else if ((v1 == vtx[1]) && (v2 == vtx[2])) {
			neighbour[1] = neighIdx;
		} else if ((v1 == vtx[2]) && (v2 == vtx[0])) {
			neighbour[2] = neighIdx;
		} else {
			throw new RuntimeException("Unreachable source code");
		}
	}

	public Polygon getPolygon() {
		return gf.createPolygon(gf.createLinearRing(new Coordinate[] {
				setOfVertices.getCoordinate(vtx[0]),
				setOfVertices.getCoordinate(vtx[1]),
				setOfVertices.getCoordinate(vtx[2]),
				setOfVertices.getCoordinate(vtx[0]) }), null);
	}

	@Override
	public String toString() {
		return new Formatter().format(
				"vtx = {%d, %d, %d} neigh = {%d, %d, %d}", vtx[0], vtx[1],
				vtx[2], neighbour[0], neighbour[1], neighbour[2]).toString();
	}
}