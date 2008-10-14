package org.contrib.algorithm.triangulation.tin2;

import com.vividsolutions.jts.algorithm.CGAlgorithms;

public class Triangle {
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
		this.setOfVertices = setOfVertices;
		this.vtx1 = vtx1;
		this.vtx2 = vtx2;
		this.vtx3 = vtx3;
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
}