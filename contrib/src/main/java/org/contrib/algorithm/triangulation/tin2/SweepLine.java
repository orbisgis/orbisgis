package org.contrib.algorithm.triangulation.tin2;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import com.vividsolutions.jts.algorithm.CGAlgorithms;

public class SweepLine {
	private SetOfVertices vertices;
	private List<Integer> slVertices;
	private List<Integer> slTriangles;

	public SweepLine(final SetOfVertices vertices) {
		this.vertices = vertices;
		slVertices = new ArrayList<Integer>();
		slTriangles = new ArrayList<Integer>();

		final int orientation = CGAlgorithms.computeOrientation(vertices
				.getCoordinate(0), vertices.getCoordinate(1), vertices
				.getCoordinate(2));

		if ((CGAlgorithms.COUNTERCLOCKWISE == orientation)
				|| (CGAlgorithms.COLLINEAR == orientation)) {
			slVertices.add(0);
			slVertices.add(1);
			slVertices.add(2);
		} else {
			slVertices.add(0);
			slVertices.add(2);
			slVertices.add(1);
		}

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
					vertices.getCoordinate(slVertices.get(i)), vertices
							.getCoordinate(slVertices.get(iNext)), vertices
							.getCoordinate(pIdx))) {
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
		if (-1 == end) {
			end = tmpEnd;
		}

		if (begin > end) {
			end += size();
		}
		int[] result = new int[end - begin + 1];
		for (int index = begin, i = 0; i < result.length; index++, i++) {
			result[i] = index % size();
		}
		return result;
	}

	public void update(final int[] visibleVertices, final int vtxIdx,
			final int firstNewTri, final int lastNewTri) {
		// first of all set to -1 all previously visible vertices
		// from the sweep-line except the 1st on
		for (int i = 1; i < visibleVertices.length; i++) {
			slVertices.set(visibleVertices[i], -1);
		}
		// then add the new vertex and the 2 new triangles
		slTriangles.set(visibleVertices[0], firstNewTri);
		slVertices.add(visibleVertices[0] + 1, vtxIdx);
		slTriangles.add(visibleVertices[0] + 1, lastNewTri);
		// remove at least all useless vertices and triangles from the
		// sweep-line
		for (int i = 0; i < slVertices.size(); i++) {
			if (-1 == slVertices.get(i)) {
				slVertices.remove(i);
				slTriangles.remove(i);
			}
		}
	}

	@Override
	public String toString() {
		return new Formatter().format("[SL] vtx = %s --- tri = %s",
				slVertices.toString(), slTriangles.toString()).toString();
	}
}