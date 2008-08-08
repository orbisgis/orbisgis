package org.gdms.triangulation.sweepLine4CDT;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.vividsolutions.jts.index.SpatialIndex;
import com.vividsolutions.jts.index.quadtree.Quadtree;

public class CDTSetOfTriangles {
	private CDTOrderedSetOfVertices orderedSetOfVertices;
	private Set<CDTTriangle> set;
	private SpatialIndex spatialIndex;

	public CDTSetOfTriangles(final CDTOrderedSetOfVertices orderedSetOfVertices) {
		this.orderedSetOfVertices = orderedSetOfVertices;
		set = new HashSet<CDTTriangle>();
		// build the spatial index...
		spatialIndex = new Quadtree(); // new STRtree(10);
	}

	public boolean add(final int v0, final int v1, final int v2) {
		CDTTriangle tmp = new CDTTriangle(orderedSetOfVertices, v0, v1, v2);
		if (set.add(tmp)) {
			spatialIndex.insert(tmp.getEnvelope(), tmp);
			return true;
		}
		return false;
	}

	public boolean add(final CDTTriangle triangle) {
		if (set.add(triangle)) {
			spatialIndex.insert(triangle.getEnvelope(), triangle);
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public void remove(final int vertexIndex) {
		final List<CDTTriangle> tmp = spatialIndex.query(orderedSetOfVertices
				.getEnvelope(vertexIndex));

		for (CDTTriangle cdtTriangle : tmp) {
			if (cdtTriangle.isAVertex(vertexIndex)) {
				remove(cdtTriangle);
			}
		}
		// remove also corresponding vertex...
		// removeVertex(vertexIndex);
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
					if (!triangle.respectDelaunayProperty(oppositeVertex)) {
						return tmp;
					}
				}
			}
		}
		return null;
	}

	public Set<CDTTriangle> getTriangles() {
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
}