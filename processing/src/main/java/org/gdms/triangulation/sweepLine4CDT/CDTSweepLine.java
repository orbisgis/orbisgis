package org.gdms.triangulation.sweepLine4CDT;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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

	private LineString lineString;

	public CDTSweepLine(final LineString lineString) {
		this.lineString = lineString;
	}

	public LineString getLineString() {
		return lineString;
	}

	protected Coordinate verticalProjectionPoint(final CDTVertex vertex) {
		final LineString verticalAxis = geometryFactory
				.createLineString(new Coordinate[] {
						vertex.getCoordinate(),
						new Coordinate(vertex.getCoordinate().x, lineString
								.getEnvelopeInternal().getMinY()) });
		final Geometry intersection = lineString.intersection(verticalAxis);

		return new Coordinate(intersection.getEnvelopeInternal().getMinX(),
				intersection.getEnvelopeInternal().getMaxY());
	}

	protected int[] verticalProjectionEdge(final Coordinate projectedPointCoord) {
		final Point projectedPoint = geometryFactory
				.createPoint(projectedPointCoord);
		final Coordinate[] coordinates = lineString.getCoordinates();

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
			final Coordinate[] coordinates = lineString.getCoordinates();
			// just replace the node (that matches the projectedPoint) by the
			// new vertex
			coordinates[nodesIndex[0]] = vertex.getCoordinate();
			// and rebuild an updated lineString...
			lineString = geometryFactory.createLineString(coordinates);
			// return the index of the new lineString node
			return nodesIndex[0];
		} else {
			// point event - case i (middle case)
			final List<Coordinate> coordinates = new LinkedList<Coordinate>(
					Arrays.asList(lineString.getCoordinates()));
			// insert the new vertex at the right place between 2 existing nodes
			coordinates.add(nodesIndex[1], vertex.getCoordinate());
			// and rebuild an updated lineString...
			lineString = geometryFactory.createLineString(coordinates
					.toArray(new Coordinate[0]));
			// return the index of the new lineString node
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
				.asList(lineString.getCoordinates()));
		boolean insertedNodeIndexUpdate = false;

		if (2 <= insertedNodeIndex) {
			double angle = Angle.normalizePositive(Angle.angleBetweenOriented(
					coordinates.get(insertedNodeIndex), coordinates
							.get(insertedNodeIndex - 1), coordinates
							.get(insertedNodeIndex - 2)));
			if (angle < PIDIV2) {
				insertedNodeIndexUpdate = true;
				// remove the vertex in the middle
				coordinates.remove(insertedNodeIndex - 1);
				// and rebuild an updated lineString...
				lineString = geometryFactory.createLineString(coordinates
						.toArray(new Coordinate[0]));
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
				coordinates.remove(insertedNodeIndex + 1);
				// and rebuild an updated lineString...
				lineString = geometryFactory.createLineString(coordinates
						.toArray(new Coordinate[0]));
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