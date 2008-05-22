package org.gdms.triangulation.sweepLine4CDT;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.vividsolutions.jts.algorithm.Angle;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

public class CDTSweepLine {
	private static final double PIDIV2 = Math.PI / 2;
	private static final double TPIDIV2 = (3 * Math.PI) / 2;
	private static final GeometryFactory geometryFactory = new GeometryFactory();

	private List<CDTVertex> cdtVertices;
	private Set<CDTTriangle> triangles;

	public CDTSweepLine(CDTVertex[] cdtVertices, PSLG pslg) {
		this.cdtVertices = new LinkedList<CDTVertex>(Arrays.asList(cdtVertices));
		if (null != pslg) {
			triangles = pslg.getTriangles();
		}
	}

	public LineString getLineString() {
		Coordinate[] coordinates = new Coordinate[cdtVertices.size()];
		for (int i = 0; i < coordinates.length; i++) {
			coordinates[i] = cdtVertices.get(i).getCoordinate();
		}
		return geometryFactory.createLineString(coordinates);
	}

	protected Coordinate verticalProjectionPoint(final CDTVertex vertex) {
		final LineString verticalAxis = geometryFactory
				.createLineString(new Coordinate[] {
						vertex.getCoordinate(),
						new Coordinate(vertex.getCoordinate().x,
								getLineString().getEnvelopeInternal().getMinY()) });
		final Geometry intersection = getLineString()
				.intersection(verticalAxis);

		return new Coordinate(intersection.getEnvelopeInternal().getMinX(),
				intersection.getEnvelopeInternal().getMaxY());
	}

	protected int[] verticalProjectionEdge(final Coordinate projectedPointCoord) {
		final Point projectedPoint = geometryFactory
				.createPoint(projectedPointCoord);
		final Coordinate[] coordinates = getLineString().getCoordinates();

		for (int i = 0; i < coordinates.length; i++) {
			if (projectedPointCoord.equals(coordinates[i])) {
				// point event - case ii (left case)
				return new int[] { i };
			} else /* if (i + 1 < coordinates.length) */{
				final LineString ls = geometryFactory
						.createLineString(new Coordinate[] { coordinates[i],
								coordinates[i + 1] });
				if (ls.contains(projectedPoint)) {
					// point event - case i (middle case)
					return new int[] { i, i + 1 };
				}
			}
		}
		throw new RuntimeException("Unreachable code");
	}

	/**
	 * This method is an implementation of the 1st step in advancing front
	 * algorithm, described at the beginning of the "Point event" section of the
	 * "Sweep-line algorithm for constrained Delaunay triangulation" article (V
	 * Domiter and B Zalik, p. 455).
	 * 
	 * @param vertex
	 * @return
	 */
	protected int firstUpdateOfAdvancingFront(final CDTVertex vertex) {
		final Coordinate projectedPointCoord = verticalProjectionPoint(vertex);
		final int[] nodesIndex = verticalProjectionEdge(projectedPointCoord);

		if (1 == nodesIndex.length) {
			// point event - case ii (left case)

			// just replace the node (that matches the projectedPoint) by the
			// new vertex in the current sweep-line
			cdtVertices.remove(nodesIndex[0]);
			cdtVertices.add(nodesIndex[0], vertex);

			// and add two new triangles...
			// TODO

			// before returning the index of the new lineString node
			return nodesIndex[0];
		} else {
			// point event - case i (middle case)

			// insert the new vertex at the right place between 2 existing nodes
			// in the current sweep-line
			cdtVertices.add(nodesIndex[1], vertex);

			// and add a new triangle...
			// TODO

			// before returning the index of the new lineString node
			return nodesIndex[1];
		}
	}

	/**
	 * This method is an implementation of the 1st heuristic described in the
	 * "Point event" section of the "Sweep-line algorithm for constrained
	 * Delaunay triangulation" article (V Domiter and B Zalik, p. 456).
	 * 
	 * @param insertedNodeIndex
	 */
	protected void secondUpdateOfAdvancingFront(int insertedNodeIndex) {
		final List<Coordinate> coordinates = new LinkedList<Coordinate>(Arrays
				.asList(getLineString().getCoordinates()));
		boolean insertedNodeIndexUpdate = false;

		if (2 <= insertedNodeIndex) {
			double angle = Angle.normalizePositive(Angle.angleBetweenOriented(
					coordinates.get(insertedNodeIndex), coordinates
							.get(insertedNodeIndex - 1), coordinates
							.get(insertedNodeIndex - 2)));
			if (angle < PIDIV2) {
				insertedNodeIndexUpdate = true;
				// remove the vertex in the middle
				cdtVertices.remove(insertedNodeIndex - 1);

				// decrease the insertedNodeIndex
				insertedNodeIndex--;
			}
		}

		if (coordinates.size() > insertedNodeIndex + 2) {
			double angle = Angle.normalizePositive(Angle.angleBetweenOriented(
					coordinates.get(insertedNodeIndex), coordinates
							.get(insertedNodeIndex + 1), coordinates
							.get(insertedNodeIndex + 2)));
			if (angle > TPIDIV2) {
				insertedNodeIndexUpdate = true;
				// remove the vertex in the middle
				cdtVertices.remove(insertedNodeIndex + 1);
			}
		}

		if (insertedNodeIndexUpdate) {
			System.err.println("secondUpdateOfAdvancingFront(): new iteration");
			secondUpdateOfAdvancingFront(insertedNodeIndex);
		}
	}

	/**
	 * This method is an implementation of the 2nd heuristic described in the
	 * "Point event" section of the "Sweep-line algorithm for constrained
	 * Delaunay triangulation" article (V Domiter and B Zalik, p. 456).
	 * 
	 */
	protected void thirdUpdateOfAdvancingFront() {
		// TODO is it necessary ?
	}
}