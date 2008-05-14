package org.orbisgis.rasterProcessing.tin;

class Edge {
	long startVertexIdx;
	long endVertexIdx;

	Edge(final long startVertexIdx, final long endVertexIdx) {
		this.startVertexIdx = startVertexIdx;
		this.endVertexIdx = endVertexIdx;
	}
}