package org.gdms.triangulation.sweepLine4CDT;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

public class SweepLine {
	private final static GeometryFactory geometryFactory = new GeometryFactory();

	private LineString lineString;

	public SweepLine(final LineString lineString) {
		this.lineString = lineString;
	}

	public LineString getLineString() {
		return lineString;
	}

	protected Coordinate verticalProjectionPoint(final Vertex vertex) {
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

	public void firstUpdateOfAdvancingFront(final Vertex vertex) {
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
		} else {
			// point event - case i (middle case)
			final List<Coordinate> coordinates = new LinkedList<Coordinate>(
					Arrays.asList(lineString.getCoordinates()));
			// insert the new vertex at the right place between 2 existing nodes
			coordinates.add(nodesIndex[1], vertex.getCoordinate());
			// and rebuild an updated lineString...
			lineString = geometryFactory.createLineString(coordinates
					.toArray(new Coordinate[0]));
		}
	}
}