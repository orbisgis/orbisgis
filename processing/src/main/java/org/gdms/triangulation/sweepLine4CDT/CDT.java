package org.gdms.triangulation.sweepLine4CDT;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.orbisgis.progress.IProgressMonitor;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

public class CDT {
	private static final double ALPHA = 0.3;

	private CDTOrderedSetOfVertices orderedSetOfVertices;
	private CDTSetOfTriangles setOfTriangles;
	private CDTSweepLine sweepLine;

	private Coordinate firstArtificialPoint;
	private Coordinate secondArtificialPoint;

	/**
	 * The aim of this constructor is to fill in the Planar Straight-Line Graph
	 * (PSLG) using the input spatial datasource. All input shapes are
	 * transformed into vertices and edges that are added to the PSLG.
	 * 
	 * @param inSds
	 * @throws DriverException
	 */
	public CDT(final SpatialDataSourceDecorator inSds) throws DriverException {
		final long rowCount = inSds.getRowCount();
		orderedSetOfVertices = new CDTOrderedSetOfVertices();
		setOfTriangles = new CDTSetOfTriangles(orderedSetOfVertices);

		// 1st step: add all the (constraining) vertices
		for (long rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			final Geometry geometry = inSds.getGeometry(rowIndex);
			orderedSetOfVertices.add(geometry.getCoordinates());
		}

		// 2nd step: compute the two first artificial vertices
		final Envelope fullExtent = inSds.getFullExtent();
		final double yy = fullExtent.getMinY() - ALPHA * fullExtent.getHeight();
		firstArtificialPoint = new Coordinate(fullExtent.getMinX() - ALPHA
				* fullExtent.getWidth(), yy);
		secondArtificialPoint = new Coordinate(fullExtent.getMaxX() + ALPHA
				* fullExtent.getWidth(), yy);
		orderedSetOfVertices.add(firstArtificialPoint);
		orderedSetOfVertices.add(secondArtificialPoint);

		orderedSetOfVertices.print();
	}

	/**
	 * This method is an implementation of the complete CDT algorithm described
	 * in the 3.2 section of the "Sweep-line algorithm for constrained Delaunay
	 * triangulation" article (V Domiter and B Zalik, p. 453).
	 * 
	 * @param pm
	 */
	public void mesh(final IProgressMonitor pm) {
		// build the 1st (artificial) triangle...
		Assert.assertTrue(0 == orderedSetOfVertices
				.search(firstArtificialPoint));
		Assert.assertTrue(1 == orderedSetOfVertices
				.search(secondArtificialPoint));
		setOfTriangles.add(0, 1, 2);

		// ... and the initial value of the sweep-line
		sweepLine = new CDTSweepLine(orderedSetOfVertices, setOfTriangles,
				new Integer[] { 0, 2, 1 });

		// sweeping (on the sorted set of vertices) - the 3 firsts vertices have
		// already been processed
		int nbOfVertices = orderedSetOfVertices.size();
		for (int i = 3; i < nbOfVertices; i++) {
			if (i / 100 == i / 100.0) {
				if (pm.isCancelled()) {
					break;
				} else {
					pm.progressTo((int) (100 * i / nbOfVertices));
				}
			}

			// vertex event
			int idx = sweepLine.firstUpdateOfAdvancingFront(i);
			idx = sweepLine.secondUpdateOfAdvancingFront(idx);
			// sweepLine.thirdUpdateOfAdvancingFront(idx);
			
			// TODO edge event
		}

		// finalization process
		finalization();
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
		setOfTriangles.remove(0);
		setOfTriangles.remove(1);

		// TODO what about lower part of the convex hull... fill in all the gap
		// created by artificial triangles removal
	}

	public Set<Polygon> getTriangles() {
		Set<Polygon> result = new HashSet<Polygon>(setOfTriangles.size());
		for (CDTTriangle triangle : setOfTriangles.getTriangles()) {
			result.add(triangle.getPolygon());
		}
		return result;
	}
}