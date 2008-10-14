package org.contrib.algorithm.triangulation.tin2;

import java.util.ArrayList;
import java.util.List;

public class SweepLine {
	private List<Integer> vertices;
	private List<Integer> triangles;

	public SweepLine() {
		vertices = new ArrayList<Integer>();
		triangles = new ArrayList<Integer>();

		vertices.add(0);
		vertices.add(1);
		vertices.add(2);

		triangles.add(0);
		triangles.add(0);
		triangles.add(0);
	}

	/**
	 * Returns the number of external edges for the set of triangles
	 * 
	 * @return
	 */
	public int size() {
		return vertices.size();
	}

	public int getVertexIndex(int index) {
		return vertices.get(index);
	}

	public int getNeighbourIndex(int index) {
		return triangles .get(index);
	}
}