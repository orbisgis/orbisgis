package org.gdms.sql.function.spatial.convert;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.spatial.GeometryValue;
import org.gdms.sql.FunctionTest;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.io.WKTReader;

public class SpatialFunctionTest extends FunctionTest {
	private WKTReader wktr;
	private Geometry g1;
	private Geometry g2;
	private Geometry g3;

	@Override
	protected void setUp() throws Exception {
		wktr = new WKTReader();
		g1 = wktr.read("MULTIPOLYGON (((0 0, 1 1, 0 1, 0 0)))");
		g2 = wktr.read("MULTILINESTRING ((0 0, 1 1, 0 1, 0 0))");
		g3 = wktr.read("MULTIPOINT (0 0, 1 1, 0 1, 0 0)");
	}

	public final void testToMultiline() throws Exception {
		ToMultiLine function = new ToMultiLine();
		Value result = evaluate(function, ValueFactory.createValue(g3));
		Geometry geom = ((GeometryValue) result).getGeom();
		assertTrue(geom.isEmpty());
		result = evaluate(function, ValueFactory.createValue(g2));
		Value result2 = evaluate(function, ValueFactory.createValue(g1));
		geom = ((GeometryValue) result).getGeom();
		Geometry geom2 = ((GeometryValue) result2).getGeom();
		assertTrue(geom instanceof MultiLineString);
		assertTrue(geom2 instanceof MultiLineString);
		assertTrue(geom2.equals(geom));
	}

	public final void testToMultipoint() throws Exception {
		ToMultiPoint function = new ToMultiPoint();
		Value result1 = evaluate(function, ValueFactory.createValue(g1));
		Value result2 = evaluate(function, ValueFactory.createValue(g2));
		Value result3 = evaluate(function, ValueFactory.createValue(g3));
		Geometry geom1 = ((GeometryValue) result1).getGeom();
		Geometry geom2 = ((GeometryValue) result2).getGeom();
		Geometry geom3 = ((GeometryValue) result3).getGeom();
		assertTrue(g3.equals(geom1));
		assertTrue(g3.equals(geom2));
		assertTrue(g3.equals(geom3));
	}
}