package org.gdms.sql.function.alphanumeric;

import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.FunctionTest;
import org.gdms.sql.function.FunctionException;

public class NumericFunctionTest extends FunctionTest {

	public void testInt() throws Exception {
		// Test null input
		IntFunction function = new IntFunction();
		Value res = evaluate(function, ValueFactory.createNullValue());
		assertTrue(res.getType() == Type.NULL);

		// Test normal input value and type
		res = evaluate(function, ValueFactory.createValue("2"));
		assertTrue(res.getType() == Type.INT);
		assertTrue(res.getAsInt() == 2);

		// Test too many parameters
		try {
			res = evaluate(function, ValueFactory.createValue("54"),
					ValueFactory.createValue("7"));
			assertTrue(false);
		} catch (FunctionException e) {

		}

		// Test wrong parameter type
		try {
			res = evaluate(function, ValueFactory.createValue(true));
			assertTrue(false);
		} catch (FunctionException e) {
		}

	}
}
