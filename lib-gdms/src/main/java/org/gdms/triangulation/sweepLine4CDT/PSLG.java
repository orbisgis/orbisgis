package org.gdms.triangulation.sweepLine4CDT;

import java.util.Set;
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

	private Set<Vertex> vertices;
	private Vertex[] sortedArrayOfVertices;
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

		// vertices = new HashSet<Vertex>((int) rowCount);
		// TODO question of efficiency...
		vertices = new TreeSet<Vertex>(new VertexComparator());

		verticesSpatialIndex = new Quadtree();
		// verticesSpatialIndex = new STRtree(10);

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

	private void addVertexAndEdge(final Point point) {
		vertices.add(new Vertex(point));
		verticesSpatialIndex.insert(point.getEnvelopeInternal(), point);
	}

	private void addVertexAndEdge(final LineString lineString) {
		final int numPoints = lineString.getNumPoints();

		for (int i = 0; i < numPoints; i++) {
			final Point point = lineString.getPointN(i);
			final Vertex vertex = new Vertex(point);
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

	public Vertex[] getSortedArrayOfVertices() {
		if (null == sortedArrayOfVertices) {
			sortedArrayOfVertices = vertices.toArray(new Vertex[0]);
			// flush the set of vertices to let the JVM collect the
			// corresponding heap space
			vertices = null;
			
			// TODO question of efficiency...
			// Arrays.sort(sortedArrayOfVertices, new VertexComparator());
		}
		return sortedArrayOfVertices;
	}

	public Point getFirstArtificialPoint() {
		return firstArtificialPoint;
	}

	public Point getSecondArtificialPoint() {
		return secondArtificialPoint;
	}

	public LineString getInitialSweepLine() {
		return geometryFactory.createLineString(new Coordinate[] {
				getFirstArtificialPoint().getCoordinate(),
				getSortedArrayOfVertices()[0].getCoordinate(),
				getSecondArtificialPoint().getCoordinate() });
	}

	public SpatialIndex getVerticesSpatialIndex() {
		return verticesSpatialIndex;
	}
}