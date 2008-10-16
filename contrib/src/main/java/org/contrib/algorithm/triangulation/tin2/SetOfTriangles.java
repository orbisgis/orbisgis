package org.contrib.algorithm.triangulation.tin2;

import java.util.HashMap;
import java.util.Map;

public class SetOfTriangles {
	private SetOfVertices vertices;
	private Map<Integer, Triangle> triangles;
	private int topIdx;

	private SweepLine sweepLine;

	public SetOfTriangles(final SetOfVertices vertices) {
		this.vertices = vertices;
		triangles = new HashMap<Integer, Triangle>();
		sweepLine = new SweepLine(vertices);
		topIdx = -1;
	}

	public int add(final Triangle triangle) {
		topIdx++;
		triangles.put(topIdx, triangle);
		return topIdx;
	}

	public void mesh(final int vtxIdx) {
		int[] visibleVertices = sweepLine.getVisiblePart(vtxIdx);

		// first of all, add all the new triangles built using the new vertex
		// and each external visible edge
		int firstNewTri = Triangle.NO_NEIGHBOUR;
		int prevTri = Triangle.NO_NEIGHBOUR;
		for (int i : visibleVertices) {
			// identify the 2nd vertex of the visible edge
			final int iNext = (sweepLine.size() == i + 1) ? 0 : i + 1;
			// identify the bordering triangle in the set of triangles
			final int neighTri = sweepLine.getNeighbourIndex(i);
			// build the new triangle
			final Triangle triangle = new Triangle(vertices, sweepLine
					.getVertexIndex(i), vtxIdx,
					sweepLine.getVertexIndex(iNext), prevTri,
					Triangle.NO_NEIGHBOUR, neighTri);
			// add it to the set of triangles
			int currTri = add(triangle);
			// update the 2 adjacent triangles already build
			triangles.get(neighTri).setNeighbourForEdge(
					sweepLine.getVertexIndex(i),
					sweepLine.getVertexIndex(iNext), currTri);
			if (Triangle.NO_NEIGHBOUR != prevTri) {
				triangles.get(prevTri).setNeighbourForEdge(2, currTri);
			} else {
				firstNewTri = currTri;
			}
			// and iterate once again
			prevTri = currTri;
		}
		// at least, update the sweep-line itself
		sweepLine.update(visibleVertices, vtxIdx, firstNewTri, prevTri);
	}

	public Map<Integer, Triangle> getTriangles() {
		return triangles;
	}
}