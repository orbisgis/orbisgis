package org.gdms.triangulation.sweepLine4CDT;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Triangle;

public class CDTCircumCircle {
	private static final double EPSILON = 1E-4;
	private Coordinate centre;
	private double radius;
	private Envelope envelope;

	public CDTCircumCircle(final Triangle triangle) {
		centre = Triangle.circumcentre(triangle.p0, triangle.p1, triangle.p2);
		radius = Math.sqrt((centre.x - triangle.p0.x)
				* (centre.x - triangle.p0.x) + (centre.y - triangle.p0.y)
				* (centre.y - triangle.p0.y));
		envelope = new Envelope(centre.x - radius, centre.x + radius, centre.y
				- radius, centre.y + radius);
		// gf.createPoint(centre).buffer(radius).getEnvelopeInternal();
	}

	public boolean contains(final Coordinate coordinate) {
		return centre.distance(coordinate) < radius + EPSILON;
	}

	public Envelope getEnvelopeInternal() {
		return envelope;
	}
}
