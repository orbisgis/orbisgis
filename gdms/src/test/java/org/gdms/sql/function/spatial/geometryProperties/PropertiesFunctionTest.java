package org.gdms.sql.function.spatial.geometryProperties;

import org.gdms.data.values.Value;
import org.gdms.sql.FunctionTest;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

public class PropertiesFunctionTest extends FunctionTest {

	public void testArea() throws Exception {
		double d = testSpatialFunction(new Area(), g1, 1).getAsDouble();
		assertTrue(g1.getArea() == d);
	}

	public void testDimension() throws Exception {
		int d = testSpatialFunction(new Dimension(), g1, 1).getAsInt();
		assertTrue(g1.getDimension() == d);
		d = testSpatialFunction(new Dimension(), g2, 1).getAsInt();
		assertTrue(g2.getDimension() == d);
		d = testSpatialFunction(new Dimension(), g3, 1).getAsInt();
		assertTrue(g3.getDimension() == d);
	}

	public void testGeometryN() throws Exception {
		int d = testSpatialFunction(new GeometryN(), g1, 1).getAsInt();
		assertTrue(g1.getNumGeometries() == d);
	}

	public void testGeometryType() throws Exception {
		String v = testSpatialFunction(new GeometryType(), g1, 1).getAsString();
		assertTrue(g1.getGeometryType().equals(v));
	}

	public void testGetZValue() throws Exception {
		Value v = testSpatialFunction(new GetZValue(), new GeometryFactory()
				.createPoint(new Coordinate(0, 50)), 1);
		assertTrue(v.isNull());
		double d = testSpatialFunction(new GetZValue(),
				new GeometryFactory().createPoint(new Coordinate(0, 50, 23)), 1)
				.getAsDouble();
		assertTrue(d == 23);
	}

	public void testIsEmpty() throws Exception {
		boolean v = testSpatialFunction(new IsEmpty(),
				new GeometryFactory().createLinearRing(new Coordinate[0]), 1)
				.getAsBoolean();
		assertTrue(v);
	}

	public void testIsSimple() throws Exception {
		boolean v = testSpatialFunction(new IsSimple(), g2, 1).getAsBoolean();
		assertTrue(v == g2.isSimple());
	}

	public void testIsValid() throws Exception {
		boolean v = testSpatialFunction(new IsValid(), g2, 1).getAsBoolean();
		assertTrue(v == g2.isValid());
	}

	public void testLength() throws Exception {
		double v = testSpatialFunction(new Length(), g2, 1).getAsDouble();
		assertTrue(v == g2.getLength());
	}

	public void testNumPoints() throws Exception {
		int v = testSpatialFunction(new NumPoints(), g2, 1).getAsInt();
		assertTrue(v == g2.getNumPoints());
	}

}
