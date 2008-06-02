package org.gdms.triangulation.sweepLine4CDT;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import com.vividsolutions.jts.index.SpatialIndex;
import com.vividsolutions.jts.index.quadtree.Quadtree;

public class PSLG {
	private static GeometryFactory geometryFactory = new GeometryFactory();
	private static final double ALPHA = 0.3;

	private SortedSet<CDTVertex> vertices;
	private SpatialIndex verticesSpatialIndex;
	private Set<CDTTriangle> triangles;
	private SpatialIndex trianglesSpatialIndex;
	private CDTVertex firstArtificialPoint;
	private CDTVertex secondArtificialPoint;
	private CDTVertex firstVertex;
	private CDTSweepLine sweepLine;

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
		triangles = new HashSet<CDTTriangle>((int) (2 * rowCount));
		trianglesSpatialIndex = new Quadtree(); // new STRtree(10);

		for (long rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			final Geometry geometry = inSds.getGeometry(rowIndex);
			addVertexAndEdge(geometry);
		}

		final Envelope fullExtent = inSds.getFullExtent();
		final double yy = fullExtent.getMinY() - ALPHA * fullExtent.getHeight();

		firstVertex = vertices.first();
		firstArtificialPoint = addVertexAndEdge(geometryFactory
				.createPoint(new Coordinate(fullExtent.getMinX() - ALPHA
						* fullExtent.getWidth(), yy)));
		secondArtificialPoint = addVertexAndEdge(geometryFactory
				.createPoint(new Coordinate(fullExtent.getMaxX() + ALPHA
						* fullExtent.getWidth(), yy)));
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
		triangles = new HashSet<CDTTriangle>(2 * geometries.length);

		for (Geometry geometry : geometries) {
			addVertexAndEdge(geometry);
			fullExtent.expandToInclude(geometry.getEnvelopeInternal());
		}

		final double yy = fullExtent.getMinY() - ALPHA * fullExtent.getHeight();

		firstVertex = vertices.first();
		firstArtificialPoint = addVertexAndEdge(geometryFactory
				.createPoint(new Coordinate(fullExtent.getMinX() - ALPHA
						* fullExtent.getWidth(), yy)));
		secondArtificialPoint = addVertexAndEdge(geometryFactory
				.createPoint(new Coordinate(fullExtent.getMaxX() + ALPHA
						* fullExtent.getWidth(), yy)));
	}

	private CDTVertex addVertexAndEdge(final Point point) {
		CDTVertex cdtVertex = new CDTVertex(point);
		if (vertices.add(cdtVertex)) {
			verticesSpatialIndex.insert(point.getEnvelopeInternal(), cdtVertex);
		}
		return cdtVertex;
	}

	private void addVertexAndEdge(final LineString lineString) {
		final int numPoints = lineString.getNumPoints();

		for (int i = 0; i < numPoints; i++) {
			final Point point = lineString.getPointN(i);
			final CDTVertex cdtVertex = addVertexAndEdge(point);

			if (i > 0) {
				cdtVertex.addAnEdge(new LineSegment(point.getCoordinate(),
						lineString.getPointN(i - 1).getCoordinate()));
			}
			if (i < numPoints - 1) {
				cdtVertex.addAnEdge(new LineSegment(point.getCoordinate(),
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
		return new CDTSweepLine(new CDTVertex[] { firstArtificialPoint,
				firstVertex, secondArtificialPoint }, this);
	}

	/**
	 * This method is an implementation of the complete CDT algorithm described
	 * in the 3.2 section of the "Sweep-line algorithm for constrained Delaunay
	 * triangulation" article (V Domiter and B Zalik, p. 453).
	 */
	public void mesh() {
		// initialization
		sweepLine = getInitialSweepLine();
		final CDTTriangle firstTriangle = new CDTTriangle(firstArtificialPoint,
				firstVertex, secondArtificialPoint, this);
		addTriangle(firstTriangle);

		// sweeping (on the sorted set of vertices)
		int cpt = 0;
		for (CDTVertex vertex : getVertices()) {
			cpt++;
			// the 3 firsts points have already been processed
			if (cpt > 3) {
				if (vertex.getEdges().isEmpty()) {
					// vertex event
					int idx = sweepLine.firstUpdateOfAdvancingFront(vertex);
					printTriangles("1st update of SL");
					sweepLine.secondUpdateOfAdvancingFront(idx);
					printTriangles("2nd update of SL");
					sweepLine.thirdUpdateOfAdvancingFront();
				} else {
					// edge event
				}
			}
		}

		// finalization
		finalization();
	}

	private void removeVertex(final CDTVertex cdtVertex) {
		vertices.remove(cdtVertex);
		verticesSpatialIndex.remove(cdtVertex.getEnvelope(), cdtVertex);
	}

	@SuppressWarnings("unchecked")
	private void removeTriangles(final CDTVertex cdtVertex) {
		List<CDTTriangle> tmp = trianglesSpatialIndex.query(cdtVertex
				.getEnvelope());

		for (CDTTriangle cdtTriangle : tmp) {
			if (cdtTriangle.isAVertex(cdtVertex)) {
				trianglesSpatialIndex.remove(cdtTriangle.getEnvelope(),
						cdtTriangle);
				triangles.remove(cdtTriangle);
			}
		}
		// remove also corresponding vertex...
		removeVertex(cdtVertex);
	}

	/**
	 * This method is an implementation of the finalization section described in
	 * the "Sweep-line algorithm for constrained Delaunay triangulation" article
	 * (V Domiter and B Zalik, p. 459).
	 */
	private void finalization() {
		// add the bordering triangles (the edges of all those triangles should
		// form the convex hull of V - the set of vertices).
		sweepLine.finalization();

		// remove all the triangles defined by at least one artificial vertex
		removeTriangles(firstArtificialPoint);
		removeTriangles(secondArtificialPoint);

		// TODO what about lower part of the convex hull... fill in all the gap
		// created by artificial triangles removal
	}

	public void addTriangle(final CDTTriangle cdtTriangle) {
		if (triangles.add(cdtTriangle)) {
			trianglesSpatialIndex
					.insert(cdtTriangle.getEnvelope(), cdtTriangle);
		}
	}

	public Set<CDTTriangle> getTriangles() {
		return triangles;
	}

	private void printTriangles(final String msg) {
		int cpt = 0;
		System.out.println(msg);
		for (CDTTriangle cdtTriangle : triangles) {
			System.out.printf("[%d] %s\n", cpt++, cdtTriangle.toString());
		}
		System.out.println();
	}
}