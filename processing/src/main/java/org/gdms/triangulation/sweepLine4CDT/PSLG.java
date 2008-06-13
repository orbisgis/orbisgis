/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.triangulation.sweepLine4CDT;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.orbisgis.progress.IProgressMonitor;

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
		final long t0 = System.currentTimeMillis();

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

		System.err.printf("PSLG initialization process : %d ms\n", System
				.currentTimeMillis()
				- t0);
	}

	/**
	 * The aim of this constructor is to fill in the Planar Straight-Line Graph
	 * (PSLG) using the input array of JTS geometries. All input shapes are
	 * transformed into vertices and edges that are added to the PSLG.
	 * 
	 * @param geometries
	 */
	public PSLG(final Geometry[] geometries) {
		final long t0 = System.currentTimeMillis();
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

		System.err.printf("PSLG initialization process : %d ms\n", System
				.currentTimeMillis()
				- t0);
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

	private SortedSet<CDTVertex> getVertices() {
		return vertices;
	}

	public SpatialIndex getVerticesSpatialIndex() {
		return verticesSpatialIndex;
	}

	public SpatialIndex getTrianglesSpatialIndex() {
		return trianglesSpatialIndex;
	}

	private CDTSweepLine getInitialSweepLine() {
		return new CDTSweepLine(new CDTVertex[] { firstArtificialPoint,
				firstVertex, secondArtificialPoint }, this);
	}

	/**
	 * This method is an implementation of the complete CDT algorithm described
	 * in the 3.2 section of the "Sweep-line algorithm for constrained Delaunay
	 * triangulation" article (V Domiter and B Zalik, p. 453).
	 * 
	 * @param pm
	 */
	public void mesh(final IProgressMonitor pm) {
		final long t0 = System.currentTimeMillis();

		// initialization
		sweepLine = getInitialSweepLine();
		final CDTTriangle firstTriangle = new CDTTriangle(firstArtificialPoint,
				firstVertex, secondArtificialPoint, this);
		addTriangle(firstTriangle);

		long delta1 = 0;
		long delta2 = 0;
		long delta3 = 0;

		// sweeping (on the sorted set of vertices)
		int nbOfVertices = getVertices().size();
		int cpt = 0;
		for (CDTVertex vertex : getVertices()) {

			if (cpt / 100 == cpt / 100.0) {
				if (pm.isCancelled()) {
					break;
				} else {
					pm.progressTo((int) (100 * cpt / nbOfVertices));
				}
			}

			cpt++;
			// the 3 firsts points have already been processed
			if (cpt > 3) {
				if (vertex.getEdges().isEmpty()) {
					// vertex event
					long ta = System.currentTimeMillis();
					int idx = sweepLine.firstUpdateOfAdvancingFront(vertex);
					delta1 += System.currentTimeMillis() - ta;
					// printTriangles("1st update of SL");

					long tb = System.currentTimeMillis();
					idx = sweepLine.secondUpdateOfAdvancingFront(idx);
					delta2 += System.currentTimeMillis() - tb;
					// printTriangles("2nd update of SL");

					long tc = System.currentTimeMillis();
					sweepLine.thirdUpdateOfAdvancingFront(idx);
					delta3 += System.currentTimeMillis() - tc;

				} else {
					// edge event
				}
			}
		}

		System.err.printf("sum of firstUpdateOfAdvancingFront : %d ms\n",
				delta1);
		System.err.printf("sum of secondUpdateOfAdvancingFront : %d ms\n",
				delta2);
		System.err.printf("sum of thirdUpdateOfAdvancingFront : %d ms\n",
				delta3);

		final long t1 = System.currentTimeMillis();
		System.err.printf("PSLG sweeping process : %d ms\n", t1 - t0);

		// finalization
		finalization();

		System.err.printf("PSLG finalization process : %d ms\n", System
				.currentTimeMillis()
				- t1);
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
				removeTriangle(cdtTriangle);
			}
		}
		// remove also corresponding vertex...
		removeVertex(cdtVertex);
	}

	private void removeTriangle(final CDTTriangle cdtTriangle) {
		trianglesSpatialIndex.remove(cdtTriangle.getEnvelope(), cdtTriangle);
		triangles.remove(cdtTriangle);
	}

	/**
	 * This method is an implementation of the finalization section described in
	 * the "Sweep-line algorithm for constrained Delaunay triangulation" article
	 * (V Domiter and B Zalik, p. 459).
	 */
	private void finalization() {
		// add the bordering triangles (the edges of all those triangles should
		// form the convex hull of V - the set of vertices).
		sweepLine.smoothing();

		// remove all the triangles defined by at least one artificial vertex
		removeTriangles(firstArtificialPoint);
		removeTriangles(secondArtificialPoint);

		// TODO what about lower part of the convex hull... fill in all the gap
		// created by artificial triangles removal
	}

	private void addTriangle(final CDTTriangle cdtTriangle) {
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

	private CDTTriangle[] swap(CDTVertex v1, CDTVertex v2, CDTVertex v3,
			CDTVertex v4) {
		// swap the common edge (from [v1, v2] to [v3, v4]) of the two triangles
		return new CDTTriangle[] { new CDTTriangle(v1, v3, v4, this),
				new CDTTriangle(v2, v3, v4, this) };
	}

	/**
	 * This method is known also as a Lawson's local optimization process. If
	 * the empty circle property is violated, the common edge of the two
	 * triangles are swapped. It is a recursive method.
	 */
	public void legalizeAndAdd(final CDTTriangle newCDTTriangle) {
		Stack<CDTTriangle> stack = new Stack<CDTTriangle>();
		stack.push(newCDTTriangle);

		while (!stack.empty()) {
			CDTTriangle triangle = stack.pop();
			Object[] neighbours = triangle.findANeighbourToSwapWith();

			if (null == neighbours) {
				addTriangle(triangle);
			} else {
				CDTTriangle neighbour = (CDTTriangle) neighbours[0];
				CDTTriangle[] cdtTriangles = swap((CDTVertex) neighbours[1],
						(CDTVertex) neighbours[2], (CDTVertex) neighbours[3],
						(CDTVertex) neighbours[4]);

				removeTriangle(triangle);
				stack.remove(triangle);

				removeTriangle(neighbour);
				stack.remove(neighbour);

				stack.push(cdtTriangles[0]);
				stack.push(cdtTriangles[1]);
			}
		}
	}
}