/*
 *    Geotools2 - OpenSource mapping toolkit
 *    http://geotools.org
 *    (C) 2002-2005, Geotools Project Managment Committee (PMC)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.gdms.triangulation.core;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class Circle {
	private Coordinate centre;

	private double radius;

	private double tolerance = 0.0001;

	private Envelope envelope;

	public Circle(Coordinate centre, double radius) {
		this.centre = centre;
		this.radius = radius;
		this.envelope = new Envelope(centre.x - radius, centre.x + radius,
				centre.y - radius, centre.y + radius);
	}

	public Coordinate getCenter() {
		return centre;
	}

	public double getRadius() {
		return radius;
	}

	public boolean contains(Coordinate coordinate) {
		double distanceFromCentre = centre.distance(coordinate);
		return distanceFromCentre < (this.radius + tolerance);
	}

	public Envelope getEnvelopeInternal() {
		return envelope;
	}

	public Geometry toGeometry() {
		GeometryFactory factory = new GeometryFactory();
		Point point = factory.createPoint(centre);
		return point.buffer(radius);
	}

	public String toString() {
		return "CIRCLE(" + centre.x + " " + centre.y + " " + radius + ")";
	}
}
