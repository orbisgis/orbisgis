package org.gdms.sql.function.spatial.operators;

import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.ColumnValue;
import org.gdms.sql.FunctionTest;
import org.gdms.sql.strategies.IncompatibleTypesException;

public class OperatorsTest extends FunctionTest {

	public void testBuffer() throws Exception {
		// Test null input
		Buffer function = new Buffer();
		Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
				ValueFactory.createNullValue()), new ColumnValue(Type.DOUBLE,
				ValueFactory.createValue(3)));
		assertTrue(res.isNull());

		// Test normal input value and type
		res = evaluate(function, ValueFactory.createValue(g1), ValueFactory
				.createValue(4));
		assertTrue(res.getType() == Type.GEOMETRY);
		assertTrue(res.getAsGeometry().equals(g1.buffer(4)));

		// Test too many parameters
		try {
			res = evaluate(function, ValueFactory.createValue(g2), ValueFactory
					.createValue(4), ValueFactory.createValue(4), ValueFactory
					.createValue(4));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}

		// Test wrong parameter type
		try {
			res = evaluate(function, ValueFactory.createValue(g2), ValueFactory
					.createValue(3), ValueFactory.createValue(3));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}
		try {
			res = evaluate(function, ValueFactory.createValue(g2), ValueFactory
					.createValue(""), ValueFactory.createValue(""));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}
		try {
			res = evaluate(function, ValueFactory.createValue(""), ValueFactory
					.createValue(3), ValueFactory.createValue(""));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}
	}

	public void testDifference() throws Exception {
		// Test null input
		Difference function = new Difference();
		Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
				ValueFactory.createNullValue()), new ColumnValue(Type.GEOMETRY,
				ValueFactory.createValue(g1)));
		assertTrue(res.isNull());

		// Test normal input value and type
		res = evaluate(function, ValueFactory.createValue(g2), ValueFactory
				.createValue(g1));
		assertTrue(res.getType() == Type.GEOMETRY);
		assertTrue(res.getAsGeometry().equalsExact(g2.difference(g1)));

		// Test too many parameters
		try {
			res = evaluate(function, ValueFactory.createValue(g1), ValueFactory
					.createValue(g1), ValueFactory.createValue(g3));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}

		// Test wrong parameter type
		try {
			res = evaluate(function, ValueFactory.createValue(g2), ValueFactory
					.createValue(true));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}
	}

	public void testGeomUnion() throws Exception {
		// Test null input
		GeomUnion function = new GeomUnion();
		Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
				ValueFactory.createNullValue()));
		assertTrue(res.isNull());

		// Test normal input value and type
		function = new GeomUnion();
		res = evaluate(function, ValueFactory.createValue(g2));
		Value res2 = evaluate(function, new ColumnValue(Type.GEOMETRY,
				ValueFactory.createNullValue()));
		assertTrue(res.equals(res2).getAsBoolean());

		// Test too many parameters
		try {
			res = evaluate(function, ValueFactory.createValue(g2), ValueFactory
					.createValue(g2));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}

		// Test wrong parameter type
		try {
			res = evaluate(function, ValueFactory.createValue(true));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}

		// Test it works with geometry collections
		evaluate(function, ValueFactory.createValue(geomCollection));
	}

	public void testIntersection() throws Exception {
		// Test null input
		Intersection function = new Intersection();
		Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
				ValueFactory.createValue(g2)), new ColumnValue(Type.GEOMETRY,
				ValueFactory.createNullValue()));
		assertTrue(res.isNull());

		// Test normal input value and type
		res = evaluate(function, ValueFactory.createValue(g3), ValueFactory
				.createValue(g2));
		assertTrue(res.getType() == Type.GEOMETRY);
		assertTrue(res.getAsGeometry().equalsExact(g3.intersection(g2)));

		// Test too many parameters
		try {
			res = evaluate(function, ValueFactory.createValue(g3), ValueFactory
					.createValue(g1), ValueFactory.createValue(g3));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}

		// Test wrong parameter type
		try {
			res = evaluate(function, ValueFactory.createValue(g3), ValueFactory
					.createValue(false));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}
	}

	public void testSymDifference() throws Exception {
		// Test null input
		SymDifference function = new SymDifference();
		Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
				ValueFactory.createValue(g3)), new ColumnValue(Type.GEOMETRY,
				ValueFactory.createNullValue()));
		assertTrue(res.isNull());

		// Test normal input value and type
		res = evaluate(function, ValueFactory.createValue(g2), ValueFactory
				.createValue(g2));
		assertTrue(res.getType() == Type.GEOMETRY);
		assertTrue(res.getAsGeometry().equalsExact(g2.symDifference(g2)));

		// Test too many parameters
		try {
			res = evaluate(function, ValueFactory.createValue(g2), ValueFactory
					.createValue(g1), ValueFactory.createValue(g3));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}

		// Test wrong parameter type
		try {
			res = evaluate(function, ValueFactory.createValue(true));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}
	}
}
