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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.vividsolutions.jts.algorithm.Angle;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.operation.buffer.BufferParameters;

public class CDTSweepLine {
	private static final double PIDIV2 = 0.5 * Math.PI;
	private static final double TPIDIV2 = 1.5 * Math.PI;
	private static final double SPIDIV4 = 1.75 * Math.PI;
	private static final GeometryFactory geometryFactory = new GeometryFactory();

	private static final BufferParameters bufParam = new BufferParameters();
	static {
		bufParam.setEndCapStyle(BufferParameters.CAP_FLAT);
	}

	private CDTOrderedSetOfVertices orderedSetOfVertices;
	private CDTSetOfTriangles setOfTriangles;
	private List<Integer> slVertices;

	public CDTSweepLine(final CDTOrderedSetOfVertices orderedSetOfVertices,
			final CDTSetOfTriangles setOfTriangles, Integer[] cdtVertices) {
		this.orderedSetOfVertices = orderedSetOfVertices;
		this.setOfTriangles = setOfTriangles;
		this.slVertices = new LinkedList<Integer>(Arrays.asList(cdtVertices));
	}

	public Coordinate[] getCoordinates() {
		final Coordinate[] coordinates = new Coordinate[slVertices.size()];
		for (int i = 0; i < coordinates.length; i++) {
			coordinates[i] = orderedSetOfVertices.get(slVertices.get(i));
		}
		return coordinates;
	}

	public LineString getLineString() {
		return geometryFactory.createLineString(getCoordinates());
	}

	protected int[] matchingEdge(final int vertexIndex) {
		final double xproj = orderedSetOfVertices.get(vertexIndex).x;
		double yproj = Double.NEGATIVE_INFINITY;
		int flag = 0;

		int[] result = null;

		Coordinate b = orderedSetOfVertices.get(slVertices.get(0));

		for (int i = 1; i < slVertices.size(); i++) {
			Coordinate a = b;
			b = orderedSetOfVertices.get(slVertices.get(i));

			if (xproj == b.x) {
				flag++;
				if (((0 < flag) && (yproj < b.y)) || (0 == flag)) {
					yproj = b.y;
					result = new int[] { i };
				}
				// return new int[] { i };
			} else if ((xproj > a.x) && (xproj < b.x)) {
				double p = (b.y - a.y) / (b.x - a.x);
				double q = a.y - p * a.x;
				double ord = xproj * p + q;
				flag++;

				if (((0 < flag) && (yproj < ord)) || (0 == flag)) {
					yproj = ord;
					result = new int[] { i - 1, i };
				}
				// return new int[] { i - 1, i };
			}
		}

		if (flag != 1) {
			throw new RuntimeException("Unreachable code");
		}
		return result;
	}

	/**
	 * This method is an implementation of the 1st step in advancing front
	 * algorithm, described at the beginning of the "Point event" section of the
	 * "Sweep-line algorithm for constrained Delaunay triangulation" article (V
	 * Domiter and B Zalik, p. 455).
	 * 
	 * @param vertexIndex
	 * @return
	 */
	protected int firstUpdateOfAdvancingFront(final int vertexIndex) {
		final int[] nodesIndex = matchingEdge(vertexIndex);

		if (2 == nodesIndex.length) {
			// point event - case i (middle case)

			// add a new triangle...
			CDTTriangle cdtTriangle = new CDTTriangle(orderedSetOfVertices,
					slVertices.get(nodesIndex[0]), vertexIndex, slVertices
							.get(nodesIndex[1]));
			setOfTriangles.legalizeAndAdd(cdtTriangle);

			// and insert the new vertex at the right place between 2 existing
			// nodes in the current sweep-line
			slVertices.add(nodesIndex[1], vertexIndex);

			// before returning the index of the new lineString node
			return nodesIndex[1];
		} else if (1 == nodesIndex.length) {
			// point event - case ii (left case)

			// add two new triangles...
			CDTTriangle cdtTriangle1 = new CDTTriangle(orderedSetOfVertices,
					slVertices.get(nodesIndex[0] - 1), vertexIndex, slVertices
							.get(nodesIndex[0]));
			setOfTriangles.legalizeAndAdd(cdtTriangle1);

			CDTTriangle cdtTriangle2 = new CDTTriangle(orderedSetOfVertices,
					slVertices.get(nodesIndex[0]), vertexIndex, slVertices
							.get(nodesIndex[0] + 1));
			setOfTriangles.legalizeAndAdd(cdtTriangle2);

			// and replace the node (that matches the projectedPoint) by the
			// new vertex in the current sweep-line
			slVertices.remove(nodesIndex[0]);
			slVertices.add(nodesIndex[0], vertexIndex);

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
	 * @return
	 */
	protected int secondUpdateOfAdvancingFront(int insertedNodeIndex) {
		boolean insertedNodeIndexUpdate;
		do {
			insertedNodeIndexUpdate = false;

			final List<Coordinate> coordinates = new LinkedList<Coordinate>(
					Arrays.asList(getCoordinates()));
			if (2 <= insertedNodeIndex) {
				final double angle = Angle.normalizePositive(Angle
						.angleBetweenOriented(coordinates
								.get(insertedNodeIndex), coordinates
								.get(insertedNodeIndex - 1), coordinates
								.get(insertedNodeIndex - 2)));
				if (angle <= PIDIV2) {
					insertedNodeIndexUpdate = true;

					// add a new triangle...
					final CDTTriangle cdtTriangle = new CDTTriangle(
							orderedSetOfVertices, slVertices
									.get(insertedNodeIndex - 2), slVertices
									.get(insertedNodeIndex - 1), slVertices
									.get(insertedNodeIndex));
					setOfTriangles.legalizeAndAdd(cdtTriangle);

					// remove the vertex in the middle
					slVertices.remove(insertedNodeIndex - 1);

					// decrease the insertedNodeIndex
					insertedNodeIndex--;
				}
			}

			if (coordinates.size() > insertedNodeIndex + 2) {
				final double angle = Angle.normalizePositive(Angle
						.angleBetweenOriented(coordinates
								.get(insertedNodeIndex), coordinates
								.get(insertedNodeIndex + 1), coordinates
								.get(insertedNodeIndex + 2)));
				if (angle >= TPIDIV2) {
					insertedNodeIndexUpdate = true;

					// add a new triangle...
					final CDTTriangle cdtTriangle = new CDTTriangle(
							orderedSetOfVertices, slVertices
									.get(insertedNodeIndex), slVertices
									.get(insertedNodeIndex + 1), slVertices
									.get(insertedNodeIndex + 2));
					setOfTriangles.legalizeAndAdd(cdtTriangle);

					// remove the vertex in the middle
					slVertices.remove(insertedNodeIndex + 1);
				}
			}
		} while (insertedNodeIndexUpdate);
		return insertedNodeIndex;
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
	 * 
	 * @param idx
	 */
	protected void thirdUpdateOfAdvancingFront(int idx) {
		// TODO : what about "smoothing(idx);" to fill in the left part ?
		int[] leftBasin = findBasinOnTheLeftSide(idx);
		if (null != leftBasin) {
			System.err.printf("leftBasin %d %d %d\n", leftBasin[0],
					leftBasin[1], leftBasin[2]);

			CDTBasin basin = new CDTBasin(slVertices, leftBasin[0],
					leftBasin[1], leftBasin[2], orderedSetOfVertices,
					setOfTriangles);
			basin.fillIn();

			// update the sweep-line
			for (int i = 0; i < leftBasin[2] - leftBasin[0] - 1; i++) {
				slVertices.remove(leftBasin[0] + 1);
			}
		}

		int[] rightBasin = findBasinOnTheRightSide(idx);
		if (null != rightBasin) {
			System.err.printf("rightBasin %d %d %d\n", rightBasin[0],
					rightBasin[1], rightBasin[2]);

			CDTBasin basin = new CDTBasin(slVertices, rightBasin[0],
					rightBasin[1], rightBasin[2], orderedSetOfVertices,
					setOfTriangles);
			basin.fillIn();

			// update the sweep-line
			for (int i = 0; i < rightBasin[2] - rightBasin[0] - 1; i++) {
				slVertices.remove(rightBasin[0] + 1);
			}
		}
	}

	protected int[] findBasinOnTheRightSide(final int idx) {
		int basinBed = -1;
		int basinRightBorder = -1;

		if (idx < slVertices.size() - 2) {
			Coordinate curr = orderedSetOfVertices.get(slVertices.get(idx));
			for (int i = idx + 1; i < slVertices.size(); i++) {
				Coordinate prev = curr;
				curr = orderedSetOfVertices.get(slVertices.get(i));
				if ((curr.y < prev.y) && (-1 == basinRightBorder)) {
					basinBed = i;
				} else if ((curr.y > prev.y) && (basinBed > idx)) {
					basinRightBorder = i;
				} else {
					break;
				}
			}
		}

		if ((idx < basinBed) && (basinBed < basinRightBorder)) {
			return new int[] { idx, basinBed, basinRightBorder };
		} else {
			return null;
		}
	}

	protected int[] findBasinOnTheLeftSide(final int idx) {
		int basinBed = -1;
		int basinLeftBorder = -1;

		if (idx > 2) {
			Coordinate curr = orderedSetOfVertices.get(slVertices.get(idx));
			for (int i = idx - 1; i >= 0; i--) {
				Coordinate prev = curr;
				curr = orderedSetOfVertices.get(slVertices.get(i));
				if ((curr.y < prev.y) && (-1 == basinLeftBorder)) {
					basinBed = i;
				} else if ((curr.y > prev.y) && (basinBed < idx)) {
					basinLeftBorder = i;
				} else {
					break;
				}
			}
		}

		if ((-1 < basinLeftBorder) && (basinLeftBorder < basinBed)
				&& (basinBed < idx)) {
			return new int[] { basinLeftBorder, basinBed, idx };
		} else {
			return null;
		}
	}

	protected void fillInBasin(final int[] basin) {
		if (null != basin) {
			final int basinLeftBorder = basin[0];
			final int basinBed = basin[1];
			final int basinRightBorder = basin[2];

			int left = basinBed - 1;
			int opposite = basinBed;
			int right = basinBed + 1;
			while ((left > basinLeftBorder) && (right < basinRightBorder)) {
				// first of all, create a new triangle...
				CDTTriangle newCDTTriangle = new CDTTriangle(
						orderedSetOfVertices, slVertices.get(left), slVertices
								.get(opposite), slVertices.get(right));
				setOfTriangles.legalizeAndAdd(newCDTTriangle);

				// then update the sweep-line...
				slVertices.remove(opposite);

				// before going on...
				double angle1 = Angle
						.normalizePositive(Angle
								.angleBetweenOriented(orderedSetOfVertices
										.get(slVertices.get(left - 1)),
										orderedSetOfVertices.get(slVertices
												.get(left)),
										orderedSetOfVertices.get(slVertices
												.get(right))));
				double angle2 = Angle
						.normalizePositive(Angle
								.angleBetweenOriented(orderedSetOfVertices
										.get(slVertices.get(left)),
										orderedSetOfVertices.get(slVertices
												.get(right)),
										orderedSetOfVertices.get(slVertices
												.get(right + 1))));
				if (Math.abs(SPIDIV4 - angle1) < Math.abs(SPIDIV4 - angle2)) {
					opposite = left;
					left--;
				} else {
					opposite = right;
					right++;
				}

				// if (opposite < basinBed) {
				// opposite = right;
				// right++;
				// } else {
				// opposite = left;
				// left--;
				// }
			}
			System.err.printf("\tEnd of basin [ %d -> %d -> %d ] fill in !\n",
					basin[0], basin[1], basin[2]);
		}
	}

	/**
	 * The objective of this method is to add new bordering triangles (the edges
	 * of all those triangles should form the convex hull of the set of
	 * vertices) in between the first sweep-line vertex and the vertex that has
	 * "endIndex" as an index.
	 * 
	 * @param endIndex
	 */
	protected void smoothing(int endIndex) {
		boolean finalizationUpdate;

		do {
			finalizationUpdate = false;
			int index = 1;
			while (index + 3 < endIndex) {
				final Coordinate a = orderedSetOfVertices.get(slVertices
						.get(index));
				final Coordinate b = orderedSetOfVertices.get(slVertices
						.get(index + 1));
				final Coordinate c = orderedSetOfVertices.get(slVertices
						.get(index + 2));
				// lets test sign(z component of ( ab ^ bc ))
				final double tmp = (b.x - a.x) * (c.y - b.y) - (b.y - a.y)
						* (c.x - b.x);

				if (tmp > 0) {
					// add a new bordering triangle
					final CDTTriangle cdtTriangle = new CDTTriangle(
							orderedSetOfVertices, slVertices.get(index),
							slVertices.get(index + 1), slVertices
									.get(index + 2));
					setOfTriangles.legalizeAndAdd(cdtTriangle);
					finalizationUpdate = true;

					// remove the vertex in the middle
					slVertices.remove(slVertices.get(index + 1));

					endIndex--;
				} else {
					index++;
				}
			}
		} while (finalizationUpdate);
	}

	/**
	 * The objective of this method is to add new bordering triangles (the edges
	 * of all those triangles should form the convex hull of the set of
	 * vertices) in between the first and the last sweep-line vertices. It is a
	 * recursive method.
	 */
	public void smoothing() {
		smoothing(slVertices.size());
	}
}