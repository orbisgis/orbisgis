package org.gdms.sql.function;

import org.gdms.data.types.Type;
import org.gdms.data.values.Value;

public class FunctionValidator {

	public static void assertNotNull(Value... values) throws FunctionException {
		for (Value value : values) {
			if (value.getType() == Type.NULL) {
				throw new FunctionException("Cannot operate in null values");
			}
		}
	}

}
