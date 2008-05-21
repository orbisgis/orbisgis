package org.gdms.triangulation.sweepLine4CDT;

import junit.framework.TestCase;

import com.vividsolutions.jts.geom.Coordinate;

public class CDTCircumCircleTest extends TestCase {
	private static final double EPSILON = 1E-4;
	private static Coordinate p0 = new Coordinate(0, 0);
	private static Coordinate p1 = new Coordinate(10, 0);
	private static Coordinate p2 = new Coordinate(0, 10);
	private CDTCircumCircle cdtCircumCircle;

	protected void setUp() throws Exception {
		super.setUp();
		cdtCircumCircle = new CDTCircumCircle(p0, p1, p2);
	}

	public void testContains() {
		assertTrue(cdtCircumCircle.contains(new Coordinate()));
		assertTrue(cdtCircumCircle.contains(new Coordinate(10 + EPSILON / 10,
				10 + EPSILON / 10)));
		assertFalse(cdtCircumCircle.contains(new Coordinate(10 + EPSILON,
				10 + EPSILON)));
	}

	public void testGetEnvelopeInternal() {
		assertEquals(cdtCircumCircle.getEnvelopeInternal().getMinX(),
				cdtCircumCircle.getCentre().x - cdtCircumCircle.getRadius());
		assertEquals(cdtCircumCircle.getEnvelopeInternal().getMinY(),
				cdtCircumCircle.getCentre().y - cdtCircumCircle.getRadius());

		assertEquals(cdtCircumCircle.getEnvelopeInternal().getMaxX(),
				cdtCircumCircle.getCentre().x + cdtCircumCircle.getRadius());
		assertEquals(cdtCircumCircle.getEnvelopeInternal().getMaxY(),
				cdtCircumCircle.getCentre().y + cdtCircumCircle.getRadius());
	}
}