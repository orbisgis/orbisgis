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
package org.contrib.algorithm.triangulation.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.contrib.algorithm.triangulation.jts.JtsGeometryUtil;
import org.contrib.algorithm.triangulation.jts.LineSegment3D;
import org.contrib.algorithm.triangulation.jts.Triangle;

import com.vividsolutions.jts.algorithm.Angle;
import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.index.ItemVisitor;
import com.vividsolutions.jts.index.quadtree.Quadtree;

public class TriangulatedIrregularNetwork {
	private static final int[] OPPOSITE_INDEXES = { 2, 1, 0 };

	private static final Logger LOG = Logger
			.getLogger(TriangulatedIrregularNetwork.class);

	private Quadtree circumCircleIndex = new Quadtree();

	private Quadtree triangleIndex = new Quadtree();

	private GeometryFactory geometryFactory;

	private PrecisionModel precisionModel;

	public TriangulatedIrregularNetwork(GeometryFactory geometryFactory,
			Triangle triangle) {
		this.geometryFactory = geometryFactory;
		this.precisionModel = geometryFactory.getPrecisionModel();

		addTriangle(triangle);
	}

	public TriangulatedIrregularNetwork(GeometryFactory geometryFactory,
			Polygon polygon) {
		this.geometryFactory = geometryFactory;
		this.precisionModel = geometryFactory.getPrecisionModel();
		CoordinateSequence coords = polygon.getExteriorRing()
				.getCoordinateSequence();
		addTriangle(Triangle.createClockwiseTriangle(coords.getCoordinate(0),
				coords.getCoordinate(1), coords.getCoordinate(2)));
	}

	public TriangulatedIrregularNetwork(GeometryFactory geometryFactory,
			Envelope envelope) {
		this.geometryFactory = geometryFactory;
		this.precisionModel = geometryFactory.getPrecisionModel();
		Coordinate c1 = new Coordinate(envelope.getMinX(), envelope.getMinY(),
				0);
		Coordinate c2 = new Coordinate(envelope.getMaxX(), envelope.getMinY(),
				0);
		Coordinate c3 = new Coordinate(envelope.getMaxX(), envelope.getMaxY(),
				0);
		Coordinate c4 = new Coordinate(envelope.getMinX(), envelope.getMaxY(),
				0);
		addTriangle(Triangle.createClockwiseTriangle(c1, c2, c3));
		addTriangle(Triangle.createClockwiseTriangle(c1, c3, c4));
	}

	public void insertNode(Point point) {
		Coordinate coordinate = point.getCoordinate();
		insertNode(coordinate);
	}

	public void insertNode(final Coordinate coordinate) {
		List<Triangle> triangles = circumCircleIndex.query(new Envelope(
				coordinate));
		if (!triangles.isEmpty()) {
			TreeSet<Coordinate> exterior = new TreeSet<Coordinate>(
					new Comparator<Coordinate>() {
						public int compare(Coordinate c1, Coordinate c2) {
							double angleC1 = Angle.angle(coordinate, c1);
							double angleC2 = Angle.angle(coordinate, c2);
							if (angleC1 < angleC2) {
								return 1;
							} else if (angleC1 > angleC2) {
								return -1;
							} else {
								return 0;
							}
						}
					});
			for (Triangle triangle : triangles) {
				Circle circle = triangle.getCircumcircle();
				if (circle.contains(coordinate)) {
					removeTriangle(triangle);
					if (!coordinate.equals2D(triangle.p0)) {
						exterior.add(triangle.p0);
					}
					if (!coordinate.equals2D(triangle.p1)) {
						exterior.add(triangle.p1);
					}
					if (!coordinate.equals2D(triangle.p2)) {
						exterior.add(triangle.p2);
					}
				}
			}

			if (!exterior.isEmpty()) {
				Coordinate previousCorner = exterior.last();
				for (Coordinate corner : exterior) {
					addTriangle(new Triangle(coordinate, previousCorner, corner));
					previousCorner = corner;
				}
			}
		}
	}

	public void insertNodes(final LineString nodes) {
		CoordinateSequence coordinates = nodes.getCoordinateSequence();
		for (int i = 0; i < coordinates.size(); i++) {
			Coordinate coordinate = coordinates.getCoordinate(i);
			insertNode(coordinate);
		}
	}

	public void insertEdge(final LineString breakline) {
		CoordinateSequence coordinates = breakline.getCoordinateSequence();
		Coordinate previousCoordinate = coordinates.getCoordinate(0);
		for (int i = 1; i < coordinates.size(); i++) {
			Coordinate coordinate = coordinates.getCoordinate(i);
			LineSegment3D segment = new LineSegment3D(previousCoordinate,
					coordinate);
			insertEdge(segment);
			previousCoordinate = coordinate;
		}
	}

	public void insertEdge(final LineSegment3D breakline) {
		List<Triangle> triangles = getTriangles(breakline);
		for (Triangle triangle : triangles) {
			LineSegment intersection = triangle.intersection(breakline);
			if (intersection != null) {
				double length = intersection.getLength();
				if (length < 0.01) {
					addBreaklineIntersect(triangle, intersection.p0);
				} else {
					addBreaklineItersect(triangle, breakline, intersection);
				}
			}
		}
	}

	private void addBreaklineItersect(Triangle triangle,
			final LineSegment3D breakline, LineSegment intersectLine) {
		Coordinate lc0 = intersectLine.p0;
		Coordinate lc1 = intersectLine.p1;
		JtsGeometryUtil.addElevation(precisionModel, lc0, breakline);
		JtsGeometryUtil.addElevation(precisionModel, lc1, breakline);
		LineSegment lineSegment = new LineSegment(lc0, lc1);
		addBreaklineIntersect(triangle, lineSegment);
	}

	private void addBreaklineIntersect(final Triangle triangle,
			final LineSegment intersectLine) {
		Coordinate lc0 = intersectLine.p0;
		Coordinate lc1 = intersectLine.p1;
		double startCornerDistance = Double.MAX_VALUE;
		double startEdgeDistance = Double.MAX_VALUE;
		double endEdgeDistance = Double.MAX_VALUE;
		double endCornerDistance = Double.MAX_VALUE;
		int startClosestCorner = -1;
		int endClosestCorner = -1;
		int startClosestEdge = -1;
		int endClosestEdge = -1;
		Coordinate[] triCoords = triangle.getCoordinates();
		for (int i = 0; i < triCoords.length; i++) {
			Coordinate corner = triCoords[i];
			Coordinate nextCorner = triCoords[(i + 1) % 3];

			double startCorner = corner.distance(lc0);
			if (startClosestCorner == -1 || startCorner < startCornerDistance) {
				startClosestCorner = i;
				startCornerDistance = startCorner;
			}

			double endCorner = corner.distance(lc1);
			if (endClosestCorner == -1 || endCorner < endCornerDistance) {
				endClosestCorner = i;
				endCornerDistance = endCorner;
			}

			LineSegment edge = new LineSegment(corner, nextCorner);
			double startEdge = edge.distance(lc0);
			if (startClosestEdge == -1 || startEdge < startEdgeDistance) {
				startClosestEdge = i;
				startEdgeDistance = startEdge;
			}

			double endEdge = edge.distance(lc1);
			if (endClosestEdge == -1 || endEdge < endEdgeDistance) {
				endClosestEdge = i;
				endEdgeDistance = endEdge;
			}
		}
		// Start of algorithm

		if (startCornerDistance < 0.01) {
			// Touching Start corner
			if (endCornerDistance < 0.01) {
				// Touching two corners
				Triangle newTriangle = Triangle.createClockwiseTriangle(lc0,
						lc1, getOtherCoordinate(triCoords, startClosestCorner,
								endClosestCorner));
				replaceTriangle(triangle, newTriangle);
			} else {
				// Touching start corner
				double edgeDistance = endEdgeDistance;
				addTriangleTouchingOneCorner(triangle, triCoords, lc0, lc1,
						startClosestCorner, endClosestEdge, edgeDistance);
			}
		} else if (endCornerDistance < 0.01) {
			// Touching end corner
			double edgeDistance = startEdgeDistance;
			addTriangleTouchingOneCorner(triangle, triCoords, lc1, lc0,
					endClosestCorner, startClosestEdge, edgeDistance);
		} else if (startEdgeDistance < 0.01) {
			if (endEdgeDistance < 0.01) {
				addTriangleTouchingTwoEdges(triangle, triCoords, lc0, lc1,
						startClosestEdge, endClosestEdge);
			} else {
				addTriangleTouchingOneEdge(triangle, triCoords, lc0, lc1,
						startClosestEdge);
			}
		} else if (endEdgeDistance < 0.01) {
			addTriangleTouchingOneEdge(triangle, triCoords, lc1, lc0,
					endClosestEdge);

		} else {
			if (startCornerDistance <= endCornerDistance) {
				addContainedLine(triangle, triCoords, startClosestCorner, lc0,
						lc1);
			} else {
				addContainedLine(triangle, triCoords, endClosestCorner, lc1,
						lc0);
			}

		}
	}

	private void addTriangleTouchingTwoEdges(Triangle triangle,
			Coordinate[] coords, Coordinate lc0, Coordinate lc1, int startEdge,
			int endEdge) {
		Coordinate cPrevious = coords[startEdge];
		Coordinate cNext = coords[(startEdge + 1) % 3];
		Coordinate cOpposite = coords[(startEdge + 2) % 3];
		if (startEdge == endEdge) {
			if (cPrevious.distance(lc0) < cPrevious.distance(lc1)) {
				replaceTriangle(triangle,
						new Triangle[] {
								Triangle.createClockwiseTriangle(cPrevious,
										lc0, cOpposite),
								Triangle.createClockwiseTriangle(lc0, lc1,
										cOpposite),
								Triangle.createClockwiseTriangle(lc1, cNext,
										cOpposite), });
			} else {
				replaceTriangle(triangle,
						new Triangle[] {
								Triangle.createClockwiseTriangle(cPrevious,
										lc1, cOpposite),
								Triangle.createClockwiseTriangle(lc0, lc1,
										cOpposite),
								Triangle.createClockwiseTriangle(lc0, cNext,
										cOpposite) });
			}
		} else if (endEdge == ((startEdge + 1) % 3)) {
			replaceTriangle(triangle,
					new Triangle[] {
							Triangle.createClockwiseTriangle(cPrevious, lc0,
									cOpposite),
							Triangle.createClockwiseTriangle(lc0, lc1,
									cOpposite),
							Triangle.createClockwiseTriangle(lc0, cNext, lc1) });
		} else {
			replaceTriangle(triangle, new Triangle[] {
					Triangle.createClockwiseTriangle(cPrevious, lc0, lc1),
					Triangle.createClockwiseTriangle(lc0, cNext, lc1),
					Triangle.createClockwiseTriangle(lc1, cNext, cOpposite) });
		}
	}

	private void addTriangleTouchingOneEdge(Triangle triangle,
			Coordinate[] coords, Coordinate lc0, Coordinate lc1, int edgeIndex) {
		Coordinate cPrevious = coords[(edgeIndex) % 3];
		Coordinate cNext = coords[(edgeIndex + 1) % 3];
		Coordinate cOpposite = coords[(edgeIndex + 2) % 3];
		if (CGAlgorithms.computeOrientation(cPrevious, lc0, lc1) == CGAlgorithms.COLLINEAR) {
			replaceTriangle(triangle,
					new Triangle[] {
							Triangle.createClockwiseTriangle(cPrevious, lc0,
									cOpposite),
							Triangle.createClockwiseTriangle(cOpposite, lc0,
									lc1),
							Triangle.createClockwiseTriangle(cOpposite, lc1,
									cNext),
							Triangle.createClockwiseTriangle(lc0, lc1, cNext) });
		} else {
			replaceTriangle(triangle,
					new Triangle[] {
							Triangle.createClockwiseTriangle(cPrevious, lc0,
									lc1),
							Triangle.createClockwiseTriangle(cNext, lc0, lc1),
							Triangle.createClockwiseTriangle(cNext, lc1,
									cOpposite),
							Triangle.createClockwiseTriangle(cPrevious, lc1,
									cOpposite) });
		}
	}

	private void addTriangleTouchingOneCorner(Triangle triangle,
			Coordinate[] coords, Coordinate lc0, Coordinate lc1,
			int startCorner, int endEdge, double endEdgeDistance) {
		if (endEdgeDistance < 1) {
			addTriangleCorderEdge(triangle, coords, lc0, lc1, startCorner,
					endEdge);
		} else {
			addTriangleStartCornerEndInside(triangle, coords, startCorner, lc0,
					lc1);
		}
	}

	private void addTriangleStartCornerEndInside(Triangle triangle,
			Coordinate[] coords, int cornerIndex, Coordinate cCorner,
			Coordinate cInside) {
		Coordinate cNext = coords[(cornerIndex + 1) % 3];
		Coordinate cPrevious = coords[(cornerIndex + 2) % 3];
		replaceTriangle(triangle, new Triangle[] {
				Triangle.createClockwiseTriangle(cCorner, cNext, cInside),
				Triangle.createClockwiseTriangle(cInside, cNext, cPrevious),
				Triangle.createClockwiseTriangle(cInside, cPrevious, cCorner) });
	}

	private void addTriangleCorderEdge(Triangle triangle, Coordinate[] coords,
			Coordinate lc0, Coordinate lc1, int startCorner, int startEdge) {
		Coordinate cNext = coords[(startCorner + 1) % 3];
		Coordinate cPrevious = coords[(startCorner + 2) % 3];
		if (startEdge == startCorner) {
			addTrangleCornerAndEdgeTouch(triangle, lc0, lc1, cNext, cPrevious);
		} else if (startEdge == (startCorner + 1) % 3) {
			addTrangleCornerAndEdgeTouch(triangle, cPrevious, lc1, cNext, lc0);
		} else {
			addTrangleCornerAndEdgeTouch(triangle, lc0, lc1, cPrevious, cNext);
		}
	}

	private void addTrangleCornerAndEdgeTouch(Triangle triangle,
			Coordinate cPrevious, Coordinate c, Coordinate cNext,
			Coordinate cOpposite) {
		replaceTriangle(triangle, new Triangle[] {
				Triangle.createClockwiseTriangle(cPrevious, c, cOpposite),
				Triangle.createClockwiseTriangle(c, cNext, cOpposite) });
	}

	private Coordinate getOtherCoordinate(Coordinate[] coords, int i1, int i2) {
		int index = getOtherIndex(i1, i2);
		return coords[index];
	}

	/**
	 * Get the index of the corner or a triangle opposite corners i1 -> i2. i1
	 * and i2 must have different values in the range 0..2.
	 * 
	 * @param i1
	 * @param i2
	 * @return
	 */
	private int getOtherIndex(int i1, int i2) {
		return OPPOSITE_INDEXES[i1 + i2 - 1];
	}

	/**
	 * Split a triangle where the line segment i0 -> i1 is fully contained in
	 * the triangle. Creates 3 new triangles.
	 * 
	 * @param triangle
	 * @param coordinates
	 *            The coordinates of the triangle.
	 * @param index
	 *            The index of the closest corner to i0.
	 * @param l0
	 *            The start coordinate of the line.
	 * @param l1
	 *            The end coordinate of the line.
	 */
	private void addContainedLine(Triangle triangle,
			final Coordinate[] coordinates, final int index, Coordinate l0,
			Coordinate l1) {
		Coordinate t0 = coordinates[index];
		Coordinate t1 = coordinates[(index + 1) % 3];
		Coordinate t2 = coordinates[(index + 2) % 3];

		int c0i0i1Orientation = CGAlgorithms.computeOrientation(t0, l0, l1);
		if (c0i0i1Orientation == CGAlgorithms.COLLINEAR) {
			addTrianglesContained(triangle, t0, t1, t2, l0, l1);

		} else if (c0i0i1Orientation == CGAlgorithms.CLOCKWISE) {
			double angleCornerLine = Angle.angleBetween(t0, l0, l1);
			double angleCornerLineCorner = Angle.angleBetween(t0, l0, t2);
			if (angleCornerLine > angleCornerLineCorner) {
				addTrianglesContained(triangle, t0, t1, t2, l0, l1);
			} else if (angleCornerLine == angleCornerLineCorner) {
				addTrianglesContained(triangle, t2, t0, t1, l1, l0);
			} else {
				addTrianglesContained(triangle, t1, t2, t0, l0, l1);
			}

		} else {
			double angleCornerLine = Angle.angleBetween(t0, l0, l1);
			double angleCornerLineCorner = Angle.angleBetween(t0, l0, t1);
			if (angleCornerLine > angleCornerLineCorner) {
				addTrianglesContained(triangle, t0, t1, t2, l0, l1);
			} else if (angleCornerLine == angleCornerLineCorner) {
				addTrianglesContained(triangle, t1, t2, t0, l1, l0);
			} else {
				addTrianglesContained(triangle, t2, t0, t1, l1, l0);
			}
		}
	}

	/**
	 * Add the triangles where the line is fully contained in the triangle.
	 * There will be 5 triangles created. The triangle coordinate t0 will be
	 * part of two triangles, the other two triangle coordinates will be part of
	 * 3 triangles. l1 must not be closer than l0 to t0.
	 * 
	 * @param triangle
	 *            TODO
	 * @param t0
	 *            The first triangle coordinate.
	 * @param t1
	 *            The second triangle coordinate.
	 * @param t2
	 *            The third triangle coordinate.
	 * @param l0
	 *            The first line coordinate.
	 * @param l1
	 *            The second line coordinate.
	 */
	private void addTrianglesContained(Triangle triangle, Coordinate t0,
			Coordinate t1, Coordinate t2, Coordinate l0, Coordinate l1) {
		replaceTriangle(triangle, new Triangle[] {
				Triangle.createClockwiseTriangle(t0, t1, l0),
				Triangle.createClockwiseTriangle(l0, t1, l1),
				Triangle.createClockwiseTriangle(l1, t1, t2),
				Triangle.createClockwiseTriangle(l0, l1, t2),
				Triangle.createClockwiseTriangle(t0, l0, t2) });
	}

	private void addBreaklineIntersect(final Triangle triangle,
			final Coordinate intersectCoord) {
		Coordinate[] triCoords = triangle.getCoordinates();
		Coordinate previousCoord = triCoords[0];
		for (int i = 1; i < triCoords.length; i++) {
			Coordinate triCorner = triCoords[i];
			if (!triCorner.equals2D(intersectCoord)
					&& !previousCoord.equals2D(intersectCoord)) {
				double distance = new LineSegment(previousCoord, triCorner)
						.distance(intersectCoord);
				if (distance == 0) {
					Coordinate nextCoordinate = triCoords[(i + 1) % 3];
					replaceTriangle(triangle, new Triangle[] {
							Triangle.createClockwiseTriangle(intersectCoord,
									triCorner, nextCoordinate),
							Triangle.createClockwiseTriangle(intersectCoord,
									nextCoordinate, previousCoord) });
				}
			}
			previousCoord = triCorner;
		}
	}

	private void removeTriangle(final Triangle triangle) {
		circumCircleIndex.remove(triangle.getCircumcircle()
				.getEnvelopeInternal(), triangle);
		// triangleIndex.remove(triangle.getEnvelopeInternal(), triangle);
	}

	private void addTriangle(Triangle triangle) {
		Circle circle = triangle.getCircumcircle();
		circumCircleIndex.insert(circle.getEnvelopeInternal(), triangle);
		// triangleIndex.insert(triangle.getEnvelopeInternal(), triangle);
	}

	public void buildIndex() {
		triangleIndex = new Quadtree();
		for (Triangle triangle : (List<Triangle>) circumCircleIndex.queryAll()) {
			triangleIndex.insert(triangle.getEnvelopeInternal(), triangle);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Triangle> getTriangles() {
		return triangleIndex.queryAll();

	}

	public List<Triangle> getTriangles(final Coordinate coordinate) {
		Envelope envelope = new Envelope(coordinate);
		final List<Triangle> triangles = new ArrayList<Triangle>();
		triangleIndex.query(envelope, new ItemVisitor() {
			public void visitItem(Object object) {
				Triangle triangle = (Triangle) object;
				if (triangle.contains(coordinate)) {
					triangles.add(triangle);
				}
			}
		});
		return triangles;
	}

	public List<Triangle> getTriangles(final LineSegment segment) {
		Envelope envelope = new Envelope(segment.p0, segment.p1);
		return getTriangles(envelope);
	}

	public List<Triangle> getTriangles(final Envelope envelope) {
		final List<Triangle> triangles = new ArrayList<Triangle>();
		triangleIndex.query(envelope, new ItemVisitor() {
			public void visitItem(Object object) {
				Triangle triangle = (Triangle) object;
				Envelope triangleEnvelope = triangle.getEnvelopeInternal();
				if (triangleEnvelope.intersects(envelope)) {
					triangles.add(triangle);
				}
			}
		});
		return triangles;
	}

	public Circle getCircle(Polygon polygon) {
		CoordinateSequence coordinates = polygon.getExteriorRing()
				.getCoordinateSequence();
		Coordinate a = coordinates.getCoordinate(0);
		Coordinate b = coordinates.getCoordinate(1);
		Coordinate c = coordinates.getCoordinate(2);
		double angleB = Angle.angleBetween(a, b, c);

		double radius = a.distance(c) / Math.sin(angleB) * 0.5;
		Coordinate coordinate = Triangle.circumcentre(a, b, c);
		return new Circle(coordinate, radius);
	}

	public double getElevation(Coordinate coordinate) {
		List<Triangle> triangles = getTriangles(coordinate);
		for (Triangle triangle : triangles) {
			Coordinate t0 = triangle.p0;
			Coordinate t1 = triangle.p1;
			Coordinate t2 = triangle.p2;
			Coordinate closestCorner = t0;
			LineSegment3D oppositeEdge = new LineSegment3D(t1, t2);
			double closestDistance = coordinate.distance(closestCorner);
			double t1Distance = coordinate.distance(t1);
			if (closestDistance > t1Distance) {
				closestCorner = t1;
				oppositeEdge = new LineSegment3D(t2, t0);
				closestDistance = t1Distance;
			}
			if (closestDistance > coordinate.distance(t2)) {
				closestCorner = t2;
				oppositeEdge = new LineSegment3D(t0, t1);
			}
			LineSegment segment = JtsGeometryUtil.addLength(new LineSegment(
					closestCorner, coordinate), 0, t0.distance(t1)
					+ t1.distance(t2) + t0.distance(t2));
			Coordinate intersectCoordinate = oppositeEdge
					.intersection3D(segment);
			if (intersectCoordinate != null) {
				segment = new LineSegment(t0, intersectCoordinate);

				return JtsGeometryUtil.getElevation(segment, coordinate);
			}
		}
		return Double.NaN;
	}

	private void replaceTriangle(Triangle triangle, Triangle[] newTriangles) {
		removeTriangle(triangle);
		for (Triangle newTriangle : newTriangles) {
			addTriangle(newTriangle);
		}
	}

	private void replaceTriangle(Triangle triangle, Triangle newTriangle) {
		removeTriangle(triangle);
		addTriangle(newTriangle);
	}
}
