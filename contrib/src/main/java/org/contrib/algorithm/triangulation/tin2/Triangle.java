package org.contrib.algorithm.triangulation.tin2;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

public class Triangle {
	private final static GeometryFactory gf = new GeometryFactory();
	public static int NO_NEIGHBOUR = -1;

	private SetOfVertices setOfVertices;
	private int vtx1;
	private int vtx2;
	private int vtx3;
	private int neighbour1 = NO_NEIGHBOUR;
	private int neighbour2 = NO_NEIGHBOUR;
	private int neighbour3 = NO_NEIGHBOUR;

	public Triangle(final SetOfVertices setOfVertices, final int vtx1,
			final int vtx2, final int vtx3) {
		this(setOfVertices, vtx1, vtx2, vtx3, NO_NEIGHBOUR, NO_NEIGHBOUR,
				NO_NEIGHBOUR);
	}

	public Triangle(final SetOfVertices setOfVertices, final int vtx1,
			final int vtx2, final int vtx3, final int neighbour1,
			final int neighbour2, final int neighbour3) {
		this.setOfVertices = setOfVertices;
		this.vtx1 = vtx1;
		this.vtx2 = vtx2;
		this.vtx3 = vtx3;
		this.neighbour1 = neighbour1;
		this.neighbour2 = neighbour2;
		this.neighbour3 = neighbour3;
	}

	public int getNeighbour1() {
		return neighbour1;
	}

	public int getNeighbour2() {
		return neighbour2;
	}

	public int getNeighbour3() {
		return neighbour3;
	}

	public void setNeighbourForEdge(final int v1, final int v2,
			final int neighIdx) {
		if ((v1 == vtx1) && (v2 == vtx2)) {
			neighbour1 = neighIdx;
		} else if ((v1 == vtx2) && (v2 == vtx3)) {
			neighbour2 = neighIdx;
		} else if ((v1 == vtx3) && (v2 == vtx1)) {
			neighbour3 = neighIdx;
		} else {
			throw new RuntimeException("Unreachable source code");
		}
	}

	public void setNeighbourForEdge(final int edgeIdx, final int neighIdx) {
		switch (edgeIdx) {
		case 1:
			neighbour1 = neighIdx;
			break;
		case 2:
			neighbour2 = neighIdx;
			break;
		case 3:
			neighbour3 = neighIdx;
			break;
		default:
			throw new RuntimeException("Unreachable source code");
		}
	}

	public boolean isAVisibleEdgeFrom(final int edgeIdx, final int vertexIdx) {
		switch (edgeIdx) {
		case 1:
			return (NO_NEIGHBOUR == neighbour1)
					&& (CGAlgorithms.CLOCKWISE == CGAlgorithms
							.computeOrientation(setOfVertices
									.getCoordinate(vtx1), setOfVertices
									.getCoordinate(vtx2), setOfVertices
									.getCoordinate(vertexIdx)));
		case 2:
			return (NO_NEIGHBOUR == neighbour2)
					&& (CGAlgorithms.CLOCKWISE == CGAlgorithms
							.computeOrientation(setOfVertices
									.getCoordinate(vtx2), setOfVertices
									.getCoordinate(vtx3), setOfVertices
									.getCoordinate(vertexIdx)));
		case 3:
			return (NO_NEIGHBOUR == neighbour3)
					&& (CGAlgorithms.CLOCKWISE == CGAlgorithms
							.computeOrientation(setOfVertices
									.getCoordinate(vtx3), setOfVertices
									.getCoordinate(vtx1), setOfVertices
									.getCoordinate(vertexIdx)));
		}
		throw new RuntimeException("Unreachable source code");
	}

	public int getEdgeIndex(final int p, final int q) {
		if ((p == vtx1) && (q == vtx2)) {
			return 1;
		} else if ((p == vtx2) && (q == vtx3)) {
			return 2;
		} else if ((p == vtx3) && (q == vtx1)) {
			return 3;
		}
		throw new RuntimeException("Unreachable source code");
	}

	public Polygon getPolygon() {
		return gf.createPolygon(gf.createLinearRing(new Coordinate[] {
				setOfVertices.getCoordinate(vtx1),
				setOfVertices.getCoordinate(vtx2),
				setOfVertices.getCoordinate(vtx3),
				setOfVertices.getCoordinate(vtx1) }), null);
	}
}