package org.gdms.sql;

import junit.framework.TestCase;

import org.gdms.data.values.Value;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.WarningException;

public abstract class FunctionTest extends TestCase {

	protected Value evaluate(Function function, Value... args)
			throws FunctionException, WarningException {
		return function.evaluate(args);
	}
}
