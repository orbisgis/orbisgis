package org.contrib.algorithm.triangulation.tin2;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.algorithm.CGAlgorithms;

public class SweepLine {
	private SetOfVertices vertices;
	private List<Integer> slVertices;
	private List<Integer> slTriangles;

	public SweepLine(final SetOfVertices setOfVertices) {
		this.vertices = setOfVertices;
		slVertices = new ArrayList<Integer>();
		slTriangles = new ArrayList<Integer>();

		slVertices.add(0);
		slVertices.add(1);
		slVertices.add(2);

		slTriangles.add(0);
		slTriangles.add(0);
		slTriangles.add(0);
	}

	/**
	 * Returns the number of external edges for the set of triangles
	 * 
	 * @return
	 */
	public int size() {
		return slVertices.size();
	}

	public int getVertexIndex(int index) {
		return slVertices.get(index);
	}

	public int getNeighbourIndex(int index) {
		return slTriangles.get(index);
	}

	public int[] getVisiblePart(int pIdx) {
		int begin = -1;
		int end = -1;
		int tmpBegin = -1;
		int tmpEnd = -1;

		for (int i = 0; i < size(); i++) {
			final int iNext = (size() == i + 1) ? 0 : i + 1;
			if (CGAlgorithms.CLOCKWISE == CGAlgorithms.computeOrientation(
					vertices.getCoordinate(i), vertices.getCoordinate(iNext),
					vertices.getCoordinate(pIdx))) {
				// the edge [i, iNext] is visible from pIdx
				if ((-1 == tmpBegin) && (-1 == tmpEnd)) {
					tmpBegin = i;
					tmpEnd = i;
				} else if (-1 == end) {
					tmpEnd = i;
				} else {
					begin = i;
					break;
				}
			} else {
				// the edge [i, iNext] is not visible from pIdx
				if ((tmpEnd != -1) && (end == -1)) {
					end = tmpEnd;
				}
			}
		}
		if (-1 == begin) {
			begin = tmpBegin;
		}

		if (begin > end) {
			end += size();
		}
		int[] result = new int[end - begin + 1];
		for (int i = begin, j = 0; i < end; i++) {
			result[j] = i % size();
		}
		return result;
	}

	public void update(final int[] visibleVertices, final int vtxIdx,
			final int firstNewTri, final int lastNewTri) {
		slVertices.set(visibleVertices[1], vtxIdx);
		slTriangles.set(visibleVertices[0], firstNewTri);
		slTriangles.set(visibleVertices[1], lastNewTri);

		for (int i = 2; i < visibleVertices.length - 1; i++) {
			slVertices.remove(visibleVertices[i]);
			slTriangles.remove(visibleVertices[i]);
		}
	}
}