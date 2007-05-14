package org.gdms.sql.function.alphanumeric;

import org.gdms.data.values.BooleanValue;
import org.gdms.data.values.Value;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.instruction.IncompatibleTypesException;

public class Max implements Function {

	private Value max = null;
	
	public Value evaluate(Value[] args) throws FunctionException {
		try {
			if (max == null) {
				max = args[0];
			} else {
				if (((BooleanValue)args[0].greater(max)).getValue()) {
					max = args[0];
				}
			}
		} catch (IncompatibleTypesException e) {
			throw new FunctionException(e);
		}
		
		return max;
	}

	public String getName() {
		return "max";
	}

	public boolean isAggregate() {
		return true;
	}

	public Function cloneFunction() {
		return new Max();
	}

}
