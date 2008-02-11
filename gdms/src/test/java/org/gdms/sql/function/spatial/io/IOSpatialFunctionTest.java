package org.gdms.sql.function.spatial.io;

import org.gdms.data.types.Type;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.FunctionTest;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.WKTWriter;

public class IOSpatialFunctionTest extends FunctionTest {

	public void testAsWKT() throws Exception {
		String str = testSpatialFunction(new AsWKT(), g1, 1).getAsString();
		assertTrue(str.equals(new WKTWriter().write(g1)));
		Point p3d = new GeometryFactory().createPoint(new Coordinate(3, 3, 3));
		str = testSpatialFunction(new AsWKT(), p3d, 1).getAsString();
		assertTrue(str.equals(new WKTWriter(3).write(p3d)));
	}

	public void testGeomFromText() throws Exception {
		String wkt = new WKTWriter().write(g1);
		Geometry g = testSpatialFunction(new GeomFromText(),
				ValueFactory.createValue(wkt), Type.STRING, 1).getAsGeometry();
		assertTrue(g.equals(g1));
		Point p3d = new GeometryFactory().createPoint(new Coordinate(3, 3, 3));
		wkt = new WKTWriter(3).write(p3d);
		g = testSpatialFunction(new GeomFromText(),
				ValueFactory.createValue(wkt), Type.STRING, 1).getAsGeometry();
		assertTrue(g.equals(p3d));
	}
}
