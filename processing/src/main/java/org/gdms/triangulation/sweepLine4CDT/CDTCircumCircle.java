package org.gdms.triangulation.sweepLine4CDT;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Triangle;

public class CDTCircumCircle {
	private static final double EPSILON = 1E-4;
	private static final GeometryFactory gf = new GeometryFactory();

	private Coordinate centre;
	private double radius;
	private Envelope envelope;

	public CDTCircumCircle(final Coordinate p0, final Coordinate p1,
			final Coordinate p2) {
		centre = Triangle.circumcentre(p0, p1, p2);
		radius = Math.sqrt((centre.x - p0.x) * (centre.x - p0.x)
				+ (centre.y - p0.y) * (centre.y - p0.y));
		envelope = new Envelope(centre.x - radius, centre.x + radius, centre.y
				- radius, centre.y + radius);
		// getGeometry().getEnvelopeInternal();
	}

	public boolean contains(final Coordinate coordinate) {
		return centre.distance(coordinate) < radius + EPSILON;
	}

	public Envelope getEnvelopeInternal() {
		return envelope;
	}

	public Coordinate getCentre() {
		return centre;
	}

	public double getRadius() {
		return radius;
	}

	public Geometry getGeometry() {
		return gf.createPoint(centre).buffer(radius);
	}
}