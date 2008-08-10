package org.gdms.triangulation.sweepLine4CDT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.index.SpatialIndex;
import com.vividsolutions.jts.index.quadtree.Quadtree;

public class CDTSetOfTriangles {
	private CDTOrderedSetOfVertices orderedSetOfVertices;
	private SortedSet<CDTTriangle> set;
	private SpatialIndex spatialIndex;

	public CDTSetOfTriangles(final CDTOrderedSetOfVertices orderedSetOfVertices) {
		this.orderedSetOfVertices = orderedSetOfVertices;
		set = new TreeSet<CDTTriangle>();
		// build the spatial index...
		spatialIndex = new Quadtree(); // new STRtree(10);
	}

	private boolean add(final CDTTriangle triangle) {
		if (set.add(triangle)) {
			spatialIndex.insert(triangle.getEnvelope(), triangle);
			return true;
		}
		return false;
	}

	private void remove(final CDTTriangle triangle) {
		spatialIndex.remove(triangle.getEnvelope(), triangle);
		set.remove(triangle);
	}

	/**
	 * This methods returns an array of a CDTTriangle and 4 CDTVertex. The
	 * CDTTriangle is the one who share an edge with the current one. The two
	 * 1st CDTVertex correspond to the common edge, the 3rd one corresponds to
	 * the opposite vertex in the current triangle, and the 4th one corresponds
	 * to the opposite vertex in the cdtTriangle parameter.
	 * 
	 * If there is no common edge, null is returned.
	 * 
	 * @param triangle2
	 * @return
	 */
	private Object[] shareACommonEdge(final CDTTriangle triangle1,
			final CDTTriangle triangle2) {
		if (triangle2.isAVertex(triangle1.p0)) {
			if (triangle2.isAVertex(triangle1.p1)) {
				return new Object[] { triangle2, triangle1.p0, triangle1.p1,
						triangle1.p2,
						triangle2.getThirdVertex(triangle1.p0, triangle1.p1) };
			} else if (triangle2.isAVertex(triangle1.p2)) {
				return new Object[] { triangle2, triangle1.p0, triangle1.p2,
						triangle1.p1,
						triangle2.getThirdVertex(triangle1.p0, triangle1.p2) };
			}
		} else if (triangle2.isAVertex(triangle1.p1)) {
			if (triangle2.isAVertex(triangle1.p2)) {
				return new Object[] { triangle2, triangle1.p1, triangle1.p2,
						triangle1.p0,
						triangle2.getThirdVertex(triangle1.p1, triangle1.p2) };
			}
		}
		return null;
	}

	/**
	 * This methods returns an array of a CDTTriangle and 4 CDTVertex. The
	 * CDTTriangle is the one who share an edge with the current one. The two
	 * 1st CDTVertex correspond to the common edge, the 3rd one corresponds to
	 * the opposite vertex in the current triangle, and the 4th one corresponds
	 * to the opposite vertex in the cdtTriangle parameter.
	 * 
	 * If there is no triangle with a common edge that needs to be swapped, null
	 * is returned.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Object[] findANeighbourToSwapWith(final CDTTriangle triangle) {
		final List<CDTTriangle> sublistOftriangles = spatialIndex
				.query(triangle.getCircumCircle().getEnvelopeInternal());
		for (CDTTriangle cdtTriangle : sublistOftriangles) {
			if (!triangle.equals(cdtTriangle)) {
				Object[] tmp = shareACommonEdge(triangle, cdtTriangle);
				if (null != tmp) {
					int oppositeVertex = (Integer) tmp[4];
					// TODO replace with respectWeakerDelaunayProperty()
					if (!triangle.getCircumCircle().isLocatedOnTheCircumCircle(
							orderedSetOfVertices.get(oppositeVertex))
							&& !triangle
									.respectDelaunayProperty(oppositeVertex)) {
						return tmp;
					}
				}
			}
		}
		return null;
	}

	public SortedSet<CDTTriangle> getTriangles() {
		return set;
	}

	/**
	 * This method is known also as a Lawson's local optimization process. If
	 * the empty circle property is violated, the common edge of the two
	 * triangles are swapped. It is a recursive method.
	 */
	public void legalizeAndAdd(final CDTTriangle newTriangle) {
		final Stack<CDTTriangle> stack = new Stack<CDTTriangle>();
		stack.push(newTriangle);

		while (!stack.empty()) {
			final CDTTriangle triangle = stack.pop();
			final Object[] neighbours = findANeighbourToSwapWith(triangle);

			if (null == neighbours) {
				add(triangle);
			} else {
				CDTTriangle neighbour = (CDTTriangle) neighbours[0];
				CDTTriangle[] cdtTriangles = swap((Integer) neighbours[1],
						(Integer) neighbours[2], (Integer) neighbours[3],
						(Integer) neighbours[4]);

				remove(triangle);
				stack.remove(triangle);

				remove(neighbour);
				stack.remove(neighbour);

				stack.push(cdtTriangles[0]);
				stack.push(cdtTriangles[1]);
			}
		}
	}

	public int size() {
		return set.size();
	}

	private CDTTriangle[] swap(final int v1, final int v2, final int v3,
			final int v4) {
		// swap the common edge (from [v1, v2] to [v3, v4]) of the two triangles
		return new CDTTriangle[] {
				new CDTTriangle(orderedSetOfVertices, v1, v3, v4),
				new CDTTriangle(orderedSetOfVertices, v2, v3, v4) };
	}

	public String toString() {
		return "Triangles = " + set.toString();
	}

	@SuppressWarnings("unchecked")
	public void focusOnArtificialVertices() {
		// the indices of the two artificial vertices are 0 and 1
		final List<CDTTriangle> subList = spatialIndex
				.query(orderedSetOfVertices.getEnvelope(0));
		subList.addAll(spatialIndex.query(orderedSetOfVertices.getEnvelope(1)));

		// first of all, compute the "bottom sweep-line" and remove all the
		// artificial triangles
		final Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (CDTTriangle t : subList) {
			if (((0 == t.p0) || (1 == t.p0))) {
				if (1 != t.p1) {
					if (CGAlgorithms.COUNTERCLOCKWISE == CGAlgorithms
							.computeOrientation(orderedSetOfVertices.get(t.p0),
									orderedSetOfVertices.get(t.p1),
									orderedSetOfVertices.get(t.p2))) {
						map.put(t.p1, t.p2);
					} else {
						map.put(t.p2, t.p1);
					}
				}
				remove(t);
			}
		}
		Integer begin = null;
		for (int key : map.keySet()) {
			if (!map.containsValue(key)) {
				if (null != begin) {
					throw new RuntimeException("Unreachable code");
				}
				begin = key;
				break;
			}
		}
		final List<Integer> bottomSL = new ArrayList<Integer>(map.size() + 1);
		for (int i = 0; i < map.size() + 1; i++) {
			if (null == begin) {
				throw new RuntimeException("Unreachable code");
			}
			bottomSL.add(begin);
			begin = map.get(begin);
		}

		// then, fill in all the gap produced by these removal in order to
		// obtain a union of triangles that is convex
		System.err.println(bottomSL);

		boolean finalizationUpdate;
		int endIndex = bottomSL.size();
		do {
			finalizationUpdate = false;
			int index = 0;
			while (index + 3 < endIndex) {
				final Coordinate a = orderedSetOfVertices.get(bottomSL
						.get(index));
				final Coordinate b = orderedSetOfVertices.get(bottomSL
						.get(index + 1));
				final Coordinate c = orderedSetOfVertices.get(bottomSL
						.get(index + 2));
				// lets test sign(z component of ( ab ^ bc ))
				final double tmp = (b.x - a.x) * (c.y - b.y) - (b.y - a.y)
						* (c.x - b.x);

				if (tmp > 0) {
					// add a new bordering triangle
					final CDTTriangle cdtTriangle = new CDTTriangle(
							orderedSetOfVertices, bottomSL.get(index), bottomSL
									.get(index + 1), bottomSL.get(index + 2));
					legalizeAndAdd(cdtTriangle);
					finalizationUpdate = true;

					// remove the vertex in the middle
					bottomSL.remove(bottomSL.get(index + 1));

					endIndex--;
				} else {
					index++;
				}
			}
		} while (finalizationUpdate);
	}
}