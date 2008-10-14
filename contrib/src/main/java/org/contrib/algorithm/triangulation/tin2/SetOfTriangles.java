package org.contrib.algorithm.triangulation.tin2;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SetOfTriangles {
	private int topIdx;
	private Map<Integer, Triangle> triangles;
	private SweepLine envelope;

	public SetOfTriangles() {
		triangles = new HashMap<Integer, Triangle>();
		envelope = new SweepLine();
		topIdx = 0;
	}

	public void add(final Triangle triangle) {
		topIdx++;
		triangles.put(topIdx, triangle);
	}

	public void mesh(final SetOfVertices vertices, final int index) {
		for (int i = 0; i < envelope.size(); i++) {
			// foreach external edge
			int iNext = (envelope.size() == i + 1) ? 0 : i + 1;
			Triangle neighbour = triangles.get(envelope
					.getNeighbourIndex(index));
			int edgeIdx = neighbour.getEdgeIndex(i, iNext);
			// test if this edge is visible from the new vertex and add a new
			// triangle if necessary
			if (neighbour.isAVisibleEdgeFrom(edgeIdx, index)) {
				add(new Triangle(vertices, envelope.getVertexIndex(i), envelope
						.getVertexIndex(iNext), index));
			}
		}
	}

	public Collection<Triangle> getTriangles() {
		return triangles.values();
	}
}