package org.gdms.sql.function.spatial.geometry;

import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.ColumnValue;
import org.gdms.sql.FunctionTest;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.spatial.ConvexHull;
import org.gdms.sql.strategies.IncompatibleTypesException;

public class ConvexHullTest extends FunctionTest {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testEvaluate() throws FunctionException {
		Function function = new ConvexHull();

		// Test null input
		Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
				ValueFactory.createNullValue()));
		assertTrue(res.isNull());

		// Test normal input value and type
		res = evaluate(function, ValueFactory.createValue(g1));
		assertTrue(res.getType() == Type.GEOMETRY);
		assertTrue(res.getAsGeometry().contains(g1));
		assertTrue(g1.contains(res.getAsGeometry()));
		System.out.println(res.getAsGeometry());
		assertTrue(res.getAsGeometry().getNumGeometries() == 1);

		// Test too many parameters
		try {
			res = evaluate(function, ValueFactory.createValue(g2), ValueFactory
					.createValue(4));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}

		// Test wrong parameter type
		try {
			res = evaluate(function, ValueFactory.createValue(123));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}

		try {
			res = evaluate(function, ValueFactory.createValue(""));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}
	}

	public void testGetName() {
		assertEquals("ConvexHull", new ConvexHull().getName());
	}

	public void testIsAggregate() {
		assertFalse(new ConvexHull().isAggregate());
	}

	public void testGetType() {
		assertEquals(Type.GEOMETRY, new ConvexHull().getType(null)
				.getTypeCode());
	}
}