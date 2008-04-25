package org.gdms.triangulation.sweepLine4CDT;

import java.util.SortedSet;
import java.util.TreeSet;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.Triangle;
import com.vividsolutions.jts.index.SpatialIndex;
import com.vividsolutions.jts.index.quadtree.Quadtree;

public class PSLG {
	private static GeometryFactory geometryFactory = new GeometryFactory();
	private static final double ALPHA = 0.3;

	private SortedSet<CDTVertex> vertices;
	private SpatialIndex verticesSpatialIndex;
	private Point firstArtificialPoint;
	private Point secondArtificialPoint;

	/**
	 * The aim of this constructor is to fill in the Planar Straight-Line Graph
	 * (PSLG) using the input spatial datasource. All input shapes are
	 * transformed into vertices and edges that are added to the PSLG.
	 * 
	 * @param inSds
	 * @throws DriverException
	 */
	public PSLG(final SpatialDataSourceDecorator inSds) throws DriverException {
		final long rowCount = inSds.getRowCount();
		vertices = new TreeSet<CDTVertex>();
		verticesSpatialIndex = new Quadtree(); // new STRtree(10);

		for (long rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			final Geometry geometry = inSds.getGeometry(rowIndex);
			addVertexAndEdge(geometry);
		}

		final Envelope fullExtent = inSds.getFullExtent();
		final double yy = fullExtent.getMinY() - ALPHA * fullExtent.getHeight();

		firstArtificialPoint = geometryFactory.createPoint(new Coordinate(
				fullExtent.getMinX() - ALPHA * fullExtent.getWidth(), yy));
		secondArtificialPoint = geometryFactory.createPoint(new Coordinate(
				fullExtent.getMaxX() + ALPHA * fullExtent.getWidth(), yy));
	}

	/**
	 * The aim of this constructor is to fill in the Planar Straight-Line Graph
	 * (PSLG) using the input array of JTS geometries. All input shapes are
	 * transformed into vertices and edges that are added to the PSLG.
	 * 
	 * @param geometries
	 */
	public PSLG(final Geometry[] geometries) {
		vertices = new TreeSet<CDTVertex>();
		verticesSpatialIndex = new Quadtree(); // new STRtree(10);
		Envelope fullExtent = geometries[0].getEnvelopeInternal();

		for (Geometry geometry : geometries) {
			addVertexAndEdge(geometry);
			fullExtent.expandToInclude(geometry.getEnvelopeInternal());
		}

		final double yy = fullExtent.getMinY() - ALPHA * fullExtent.getHeight();

		firstArtificialPoint = geometryFactory.createPoint(new Coordinate(
				fullExtent.getMinX() - ALPHA * fullExtent.getWidth(), yy));
		secondArtificialPoint = geometryFactory.createPoint(new Coordinate(
				fullExtent.getMaxX() + ALPHA * fullExtent.getWidth(), yy));
	}

	private void addVertexAndEdge(final Point point) {
		vertices.add(new CDTVertex(point));
		verticesSpatialIndex.insert(point.getEnvelopeInternal(), point);
	}

	private void addVertexAndEdge(final LineString lineString) {
		final int numPoints = lineString.getNumPoints();

		for (int i = 0; i < numPoints; i++) {
			final Point point = lineString.getPointN(i);
			final CDTVertex vertex = new CDTVertex(point);
			verticesSpatialIndex.insert(point.getEnvelopeInternal(), point);

			if (i > 0) {
				vertex.addAnEdge(new LineSegment(point.getCoordinate(),
						lineString.getPointN(i - 1).getCoordinate()));
			}
			if (i < numPoints - 1) {
				vertex.addAnEdge(new LineSegment(point.getCoordinate(),
						lineString.getPointN(i + 1).getCoordinate()));
			}
		}
	}

	private void addVertexAndEdge(final Polygon polygon) {
		addVertexAndEdge(polygon.getExteriorRing());

		final int nbOfHoles = polygon.getNumInteriorRing();
		for (int i = 0; i < nbOfHoles; i++) {
			addVertexAndEdge(polygon.getInteriorRingN(i));
		}
	}

	private void addVertexAndEdge(final GeometryCollection geometry) {
		final GeometryCollection gc = (GeometryCollection) geometry;
		for (int i = 0; i < gc.getNumGeometries(); i++) {
			addVertexAndEdge(gc.getGeometryN(i));
		}
	}

	private void addVertexAndEdge(final Geometry geometry) {
		if (geometry instanceof Point) {
			addVertexAndEdge((Point) geometry);
		} else if (geometry instanceof LineString) {
			addVertexAndEdge((LineString) geometry);
		} else if (geometry instanceof Polygon) {
			addVertexAndEdge((Polygon) geometry);
		} else if (geometry instanceof GeometryCollection) {
			addVertexAndEdge((GeometryCollection) geometry);
		}
	}

	public SortedSet<CDTVertex> getVertices() {
		return vertices;
	}

	public SpatialIndex getVerticesSpatialIndex() {
		return verticesSpatialIndex;
	}

	private CDTSweepLine getInitialSweepLine() {
		return new CDTSweepLine(geometryFactory
				.createLineString(new Coordinate[] {
						firstArtificialPoint.getCoordinate(),
						vertices.first().getCoordinate(),
						secondArtificialPoint.getCoordinate() }));
		// vertices.add(new Vertex(firstArtificialPoint));
		// vertices.add(new Vertex(secondArtificialPoint));
	}

	public void mesh() {
		final CDTSweepLine sweepLine = getInitialSweepLine();
		for (CDTVertex vertex : getVertices()) {
			if (vertex.getEdges().isEmpty()) {
				// vertex event
				int idx = sweepLine.firstUpdateOfAdvancingFront(vertex);
				sweepLine.secondUpdateOfAdvancingFront(idx);
				sweepLine.thirdUpdateOfAdvancingFront();

			} else {
				// edge event
			}
		}
		finalization();
	}

	/**
	 * This method is an implementation of the finalization section described in
	 * the "Sweep-line algorithm for constrained Delaunay triangulation" article
	 * (p. 459).
	 */
	private void finalization() {
		// remove all the triangles defined by at least one artificial point
		vertices.remove(new CDTVertex(firstArtificialPoint));
		vertices.remove(new CDTVertex(secondArtificialPoint));

		// add the bordering triangles (the edges of all those triangles should
		// form the convex hull of V - the set of vertices).
	}

	public SortedSet<Triangle> getTriangles() {
		return null;
	}
}