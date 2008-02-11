package org.gdms.sql.function.spatial.predicates;

import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.ColumnValue;
import org.gdms.sql.FunctionTest;
import org.gdms.sql.function.Function;
import org.gdms.sql.strategies.IncompatibleTypesException;

import com.vividsolutions.jts.geom.Geometry;

public class PredicatesTest extends FunctionTest {

	public void testPredicates() throws Exception {
		testPredicate(new Contains(), g1, g2);
		testPredicate(new Crosses(), g1, g2);
		testPredicate(new Disjoint(), g1, g2);
		testPredicate(new Equals(), g1, g2);
		testPredicate(new Intersects(), g1, g2);
		testPredicate(new IsWithin(), g1, g2);
		testPredicate(new Overlaps(), g1, g2);
		testPredicate(new Touches(), g1, g2);
	}

	private void testPredicate(Function function, Geometry g1, Geometry g2)
			throws Exception {
		// Test null input
		Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
				ValueFactory.createValue(g1)), new ColumnValue(Type.GEOMETRY,
				ValueFactory.createNullValue()));
		assertTrue(res.isNull());

		// Test normal input value and type
		res = evaluate(function, ValueFactory.createValue(g2), ValueFactory
				.createValue(g1));
		assertTrue(res.getType() == Type.BOOLEAN);

		// Test too many parameters
		try {
			res = evaluate(function, ValueFactory.createValue(g2), ValueFactory
					.createValue(g2), ValueFactory.createValue(g3));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}

		// Test wrong parameter type
		try {
			res = evaluate(function, ValueFactory.createValue(g1), ValueFactory
					.createValue(true));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}
	}

	public void testIsWithinDistance() throws Exception {
		// Test null input
		IsWithinDistance function = new IsWithinDistance();
		Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
				ValueFactory.createValue(g2)), new ColumnValue(Type.GEOMETRY,
				ValueFactory.createValue(g3)), new ColumnValue(Type.DOUBLE,
				ValueFactory.createNullValue()));
		assertTrue(res.isNull());

		// Test normal input value and type
		res = evaluate(function, ValueFactory.createValue(g2), ValueFactory
				.createValue(g1), ValueFactory.createValue(14));
		assertTrue(res.getType() == Type.BOOLEAN);
		assertTrue(res.getAsBoolean() == g2.isWithinDistance(g1, 14));

		// Test too many parameters
		try {
			res = evaluate(function, ValueFactory.createValue(g2), ValueFactory
					.createValue(g2), ValueFactory.createValue(14),
					ValueFactory.createValue(1));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}

		// Test wrong parameter type
		try {
			res = evaluate(function, ValueFactory.createValue(false));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}
	}

	public void testRelate() throws Exception {
		// Test null input
		Relate function = new Relate();
		Value res = evaluate(function, new ColumnValue(Type.GEOMETRY,
				ValueFactory.createValue(g2)), new ColumnValue(Type.GEOMETRY,
				ValueFactory.createNullValue()));
		assertTrue(res.isNull());

		// Test normal input value and type
		res = evaluate(function, ValueFactory.createValue(g2), ValueFactory
				.createValue(g1));
		assertTrue(res.getType() == Type.STRING);
		assertTrue(res.getAsString().equals(g2.relate(g1).toString()));

		// Test too many parameters
		try {
			res = evaluate(function, ValueFactory.createValue(g2), ValueFactory
					.createValue(g2), ValueFactory.createValue(14));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}

		// Test wrong parameter type
		try {
			res = evaluate(function, ValueFactory.createValue(false));
			assertTrue(false);
		} catch (IncompatibleTypesException e) {
		}
	}
}
