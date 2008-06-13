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
package org.gdms.triangulation.jts;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.gdms.triangulation.core.Circle;

import com.vividsolutions.jts.algorithm.Angle;
import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.algorithm.RobustLineIntersector;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTWriter;

public class Triangle extends com.vividsolutions.jts.geom.Triangle {
	private Envelope envelope;

	private Circle circumcircle;

	/**
	 * Create a new Triangle with the coordinates in a clockwise direction.
	 * 
	 * @param c0
	 *            The first coordinate.
	 * @param c1
	 *            The second coordinate.
	 * @param c2
	 *            The third coordinate.
	 * @return The Triangle.
	 */
	public static Triangle createClockwiseTriangle(final Coordinate c0,
			final Coordinate c1, final Coordinate c2) {
		try {
			if (CGAlgorithms.computeOrientation(c0, c1, c2) == CGAlgorithms.CLOCKWISE) {
				return new Triangle(c0, c1, c2);
			} else {
				return new Triangle(c0, c2, c1);
			}
		} catch (IllegalStateException e) {
			throw e;
		}

	}

	/**
	 * Construct a new Triangle.
	 * 
	 * @param c0
	 *            The first coordinate.
	 * @param c1
	 *            The second coordinate.
	 * @param c2
	 *            The third coordinate.
	 */
	public Triangle(final Coordinate c0, final Coordinate c1,
			final Coordinate c2) {
		super(c0, c1, c2);
		envelope = new Envelope(c0, c1);
		envelope.expandToInclude(c2);
		createCircumcircle();
	}

	/**
	 * Computes the circumcircle of a triangle. The circumcircle is the smallest
	 * circle which encloses the triangle.
	 * 
	 * @return The circumcircle of the triangle.
	 */
	public Circle getCircumcircle() {
		return circumcircle;
	}

	private void createCircumcircle() {
		double angleB = Angle.angleBetween(p0, p1, p2);

		double radius = p0.distance(p2) / Math.sin(angleB) * 0.5;
		Coordinate coordinate = getCircumcentre();
		circumcircle = new Circle(coordinate, radius);
	}

	/**
	 * Computes the circumcentre of a triangle. The circumcentre is the centre
	 * of the circumcircle, the smallest circle which encloses the triangle.
	 * 
	 * @return The circumcentre of the triangle.
	 */
	public Coordinate getCircumcentre() {
		return circumcentre(p0, p1, p2);
	}

	/**
	 * Get the envelope of the Triangle.
	 * 
	 * @return The envelope.
	 */
	public Envelope getEnvelopeInternal() {
		return envelope;
	}

	/**
	 * Returns true if the coordinate lies inside or on the edge of the
	 * Triangle.
	 * 
	 * @param coordinate
	 *            The coordinate.
	 * @return True if the coordinate lies inside or on the edge of the
	 *         Triangle.
	 */
	public boolean contains(final Coordinate coordinate) {
		int triangleOrientation = CGAlgorithms.computeOrientation(p0, p1, p2);
		int p0p1Orientation = CGAlgorithms.computeOrientation(p0, p1,
				coordinate);
		if (p0p1Orientation != triangleOrientation
				&& p0p1Orientation != CGAlgorithms.COLLINEAR) {
			return false;
		}
		int p1p2Orientation = CGAlgorithms.computeOrientation(p1, p2,
				coordinate);
		if (p1p2Orientation != triangleOrientation
				&& p1p2Orientation != CGAlgorithms.COLLINEAR) {
			return false;
		}
		int p2p0Orientation = CGAlgorithms.computeOrientation(p2, p0,
				coordinate);
		if (p2p0Orientation != triangleOrientation
				&& p2p0Orientation != CGAlgorithms.COLLINEAR) {
			return false;
		}
		return true;
	}

	public LineSegment intersection(LineSegment line) {
		Coordinate lc0 = line.p0;
		Coordinate lc1 = line.p1;
		boolean lc0Contains = contains(lc0);
		boolean lc1Contains = contains(lc1);
		if (lc0Contains && lc1Contains) {
			return line;
		} else {
			Set<Coordinate> coordinates = new HashSet<Coordinate>();
			addIntersection(coordinates, lc0, lc1, p0, p1);
			addIntersection(coordinates, lc0, lc1, p1, p2);
			addIntersection(coordinates, lc0, lc1, p2, p0);

			Iterator<Coordinate> coordIterator = coordinates.iterator();
			if (coordIterator.hasNext()) {
				Coordinate c1 = coordIterator.next();
				if (coordIterator.hasNext()) {
					Coordinate c2 = coordIterator.next();
					if (coordIterator.hasNext()) {
						System.err.println("Too many intersect");
					}
					return new LineSegment(c1, c2);
				} else {
					return new LineSegment(c1, c1);
				}
			} else {
				return null;
			}
		}
	}

	private void addIntersection(Set<Coordinate> coordinates, Coordinate lc0,
			Coordinate lc1, Coordinate c0, Coordinate c1) {
		RobustLineIntersector intersector = new RobustLineIntersector();
		intersector.computeIntersection(lc0, lc1, c0, c1);
		for (int i = 0; i < intersector.getIntersectionNum(); i++) {
			coordinates.add(intersector.getIntersection(i));
		}
	}

	public Coordinate[] getCoordinates() {
		return new Coordinate[] { p0, p1, p2 };
	}

	public Polygon getPolygon() {
		GeometryFactory factory = new GeometryFactory();
		LinearRing shell = factory.createLinearRing(new Coordinate[] { p0, p1,
				p2, p0 });
		return factory.createPolygon(shell, null);
	}

	public boolean equals(final Triangle triangle) {
		HashSet<Coordinate> coords = new HashSet<Coordinate>();
		coords.add(triangle.p0);
		coords.add(triangle.p1);
		coords.add(triangle.p2);
		coords.add(p0);
		coords.add(p1);
		coords.add(p2);
		return coords.size() == 3;
	}

	public String toString() {
		return new WKTWriter(3).write(getPolygon());
	}
}
