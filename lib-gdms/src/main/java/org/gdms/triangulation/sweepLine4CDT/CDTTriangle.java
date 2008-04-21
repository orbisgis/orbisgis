package org.gdms.triangulation.sweepLine4CDT;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Triangle;
import com.vividsolutions.jts.index.SpatialIndex;

public class CDTTriangle {
	private final static GeometryFactory gf = new GeometryFactory();

	private Triangle triangle;
	private boolean[] containsConstraint = new boolean[] { false, false, false };

	private SpatialIndex verticesSpatialIndex;

	public CDTTriangle(final Triangle triangle, final PSLG pslg) {
		this.triangle = triangle;

		verticesSpatialIndex = pslg.getVerticesSpatialIndex();

		final Coordinate circumCentre = triangle.circumcentre(triangle.p0,
				triangle.p1, triangle.p2);
		final double circumRadius = Math.sqrt((circumCentre.x - triangle.p0.x)
				* (circumCentre.x - triangle.p0.x)
				+ (circumCentre.y - triangle.p0.y)
				* (circumCentre.y - triangle.p0.y));
		final Geometry circumCircle = gf.createPoint(circumCentre).buffer(
				circumRadius);

		verticesSpatialIndex.query(circumCircle.getEnvelopeInternal());
	}
}
