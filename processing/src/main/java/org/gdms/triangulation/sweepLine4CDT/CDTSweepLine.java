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
import com.vividsolutions.jts.operation.buffer.BufferOp;
import com.vividsolutions.jts.operation.buffer.BufferParameters;

public class CDTSweepLine {
	private static final double EPSILON = 1.0E-8;
	private static final double PIDIV2 = Math.PI / 2;
	private static final double TPIDIV2 = (3 * Math.PI) / 2;
	private static final GeometryFactory geometryFactory = new GeometryFactory();

	private static final BufferParameters bufParam = new BufferParameters();
	static {
		bufParam.setEndCapStyle(BufferParameters.CAP_FLAT);
	}

	private List<CDTVertex> slVertices;
	private PSLG pslg;

	public CDTSweepLine(CDTVertex[] cdtVertices, PSLG pslg) {
		this.slVertices = new LinkedList<CDTVertex>(Arrays.asList(cdtVertices));
		this.pslg = pslg;
	}

	public LineString getLineString() {
		Coordinate[] coordinates = new Coordinate[slVertices.size()];
		for (int i = 0; i < coordinates.length; i++) {
			coordinates[i] = slVertices.get(i).getCoordinate();
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

				// May 26, 2008 - arithmetic accuracy problem
				//				
				// ls = wktr.read("LINESTRING (0 0, 4.5 -0.6)");
				// p = wktr.read("POINT (1.0 -0.1333333333333333)");
				// ls.contains(p) ==> FALSE !
				//				
				// if (ls.buffer(EPSILON,).contains(projectedPoint)) {

				BufferOp bufOp = new BufferOp(ls, bufParam);

				if (bufOp.getResultGeometry(EPSILON).contains(projectedPoint)) {
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
		// TODO 2 following instructions should really be improved !
		final Coordinate projectedPointCoord = verticalProjectionPoint(vertex);
		final int[] nodesIndex = verticalProjectionEdge(projectedPointCoord);

		if (2 == nodesIndex.length) {
			// point event - case i (middle case)

			// add a new triangle...
			// TODO remove this useless test
			if (null != pslg) {
				CDTTriangle cdtTriangle = new CDTTriangle(slVertices
						.get(nodesIndex[0]), vertex, slVertices
						.get(nodesIndex[1]), pslg);
				if (!cdtTriangle.legalization()) {
					pslg.addTriangle(cdtTriangle);
				}
			}

			// and insert the new vertex at the right place between 2 existing
			// nodes in the current sweep-line
			slVertices.add(nodesIndex[1], vertex);

			// before returning the index of the new lineString node
			return nodesIndex[1];
		} else if (1 == nodesIndex.length) {
			// point event - case ii (left case)

			// add two new triangles...
			// TODO remove this useless test
			if (null != pslg) {
				CDTTriangle cdtTriangle1 = new CDTTriangle(slVertices
						.get(nodesIndex[0] - 1), vertex, slVertices
						.get(nodesIndex[0]), pslg);
				if (!cdtTriangle1.legalization()) {
					pslg.addTriangle(cdtTriangle1);
				}

				CDTTriangle cdtTriangle2 = new CDTTriangle(slVertices
						.get(nodesIndex[0]), vertex, slVertices
						.get(nodesIndex[0] + 1), pslg);
				if (!cdtTriangle2.legalization()) {
					pslg.addTriangle(cdtTriangle2);
				}
			}

			// and replace the node (that matches the projectedPoint) by the
			// new vertex in the current sweep-line
			slVertices.remove(nodesIndex[0]);
			slVertices.add(nodesIndex[0], vertex);

			// before returning the index of the new lineString node
			return nodesIndex[0];
		}
		throw new RuntimeException("Unreachable code");
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
			if (angle <= PIDIV2) {
				insertedNodeIndexUpdate = true;

				// add a new triangle...
				// TODO remove this useless test
				if (null != pslg) {
					CDTTriangle cdtTriangle = new CDTTriangle(slVertices
							.get(insertedNodeIndex - 2), slVertices
							.get(insertedNodeIndex - 1), slVertices
							.get(insertedNodeIndex), pslg);
					if (!cdtTriangle.legalization()) {
						pslg.addTriangle(cdtTriangle);
					}
				}

				// remove the vertex in the middle
				slVertices.remove(insertedNodeIndex - 1);

				// decrease the insertedNodeIndex
				insertedNodeIndex--;
			}
		}

		if (coordinates.size() > insertedNodeIndex + 2) {
			double angle = Angle.normalizePositive(Angle.angleBetweenOriented(
					coordinates.get(insertedNodeIndex), coordinates
							.get(insertedNodeIndex + 1), coordinates
							.get(insertedNodeIndex + 2)));
			if (angle >= TPIDIV2) {
				insertedNodeIndexUpdate = true;

				// add a new triangle...
				// TODO remove this useless test
				if (null != pslg) {
					CDTTriangle cdtTriangle = new CDTTriangle(slVertices
							.get(insertedNodeIndex), slVertices
							.get(insertedNodeIndex + 1), slVertices
							.get(insertedNodeIndex + 2), pslg);
					if (!cdtTriangle.legalization()) {
						pslg.addTriangle(cdtTriangle);
					}
				}

				// remove the vertex in the middle
				slVertices.remove(insertedNodeIndex + 1);
			}
		}

		if (insertedNodeIndexUpdate) {
			// System.err.println("secondUpdateOfAdvancingFront(): new
			// iteration");
			secondUpdateOfAdvancingFront(insertedNodeIndex);
		}
	}

	/**
	 * This method is an implementation of the 2nd heuristic described in the
	 * "Point event" section of the "Sweep-line algorithm for constrained
	 * Delaunay triangulation" article (V Domiter and B Zalik, p. 456). The main
	 * objective here is to reduce the ondulation of the advancing front.
	 * Indeed, after the insertion of a new vertex in the sweep-line, a basin
	 * may appear. It has to be detected first and afterwards filled with
	 * triangles (see "An efficient sweep-line Delaunay triangulation
	 * algorithm", B Zalik, in Computer-Aided Design, #37, p 1032, 2005).
	 */
	protected void thirdUpdateOfAdvancingFront() {
		// TODO
	}

	public void finalization() {
		boolean finalizationUpdate = false;

		int index = 1;
		while (index + 3 < slVertices.size()) {
			Coordinate a = slVertices.get(index).getCoordinate();
			Coordinate b = slVertices.get(index + 1).getCoordinate();
			Coordinate c = slVertices.get(index + 2).getCoordinate();
			// lets test sign(z component of ( ab ^ bc ))
			double tmp = (b.x - a.x) * (c.y - b.y) - (b.y - a.y) * (c.x - b.x);

			if (tmp > 0) {
				// add a new bordering triangle
				// TODO remove this useless test
				if (null != pslg) {
					CDTTriangle cdtTriangle = new CDTTriangle(slVertices
							.get(index), slVertices.get(index + 1), slVertices
							.get(index + 2), pslg);
					if (!cdtTriangle.legalization()) {
						pslg.addTriangle(cdtTriangle);
					}
					finalizationUpdate = true;
				}

				// remove the vertex in the middle
				slVertices.remove(slVertices.get(index + 1));
			} else {
				index++;
			}
		}

		if (finalizationUpdate) {
			// System.err.println("SL finalization(): new iteration");
			finalization();
		}
	}
}