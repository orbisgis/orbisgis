package org.gdms.triangulation.sweepLine4CDT;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

public class SweepLine {
	private final static GeometryFactory geometryFactory = new GeometryFactory();

	private LineString lineString;

	public SweepLine(final LineString lineString) {
		this.lineString = lineString;
	}

	public Coordinate projectAVertexOnSweepLine(final Vertex vertex) {
		final LineString verticalAxis = geometryFactory
				.createLineString(new Coordinate[] {
						vertex.getCoordinate(),
						new Coordinate(vertex.getCoordinate().x, lineString
								.getEnvelopeInternal().getMinY()) });
		final Geometry intersection = lineString.intersection(verticalAxis);

		return new Coordinate(intersection.getEnvelopeInternal().getMinX(),
				intersection.getEnvelopeInternal().getMaxY());
	}

	public LineString projectAVertexOnSweepLineAndExtractAnEdge(
			final Vertex vertex) {
		final Coordinate projectedPoint = projectAVertexOnSweepLine(vertex);

		final Coordinate[] coordinates = lineString.getCoordinates();

		return null;
	}
}