package org.gdms.sql.function.alphanumeric;

import org.gdms.data.types.Type;
import org.gdms.data.values.NumericValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.FunctionTest;
import org.gdms.sql.function.FunctionException;

public class StringFunctionTest extends FunctionTest {

	public void testLength() throws Exception {
		// Test null input
		StrLength function = new StrLength();
		Value res = evaluate(function, ValueFactory.createNullValue());
		assertTrue(res.getType() == Type.NULL);

		// Test normal input
		res = evaluate(function, ValueFactory.createValue("aa"));
		assertTrue(((NumericValue) res).intValue() == 2);

		// Test too many parameters
		try {
			res = evaluate(function, ValueFactory.createValue("aa"),
					ValueFactory.createValue("aa"));
			assertTrue(false);
		} catch (FunctionException e) {

		}
	}
}
