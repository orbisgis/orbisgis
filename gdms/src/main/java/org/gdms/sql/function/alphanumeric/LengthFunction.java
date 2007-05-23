package org.gdms.sql.function.alphanumeric;

import org.gdms.data.values.StringValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

public class LengthFunction implements Function {

    public Value evaluate(Value[] args) throws FunctionException {
        if (args.length != 1) {
            throw new FunctionException("length takes one argument");
        }

        if (args[0] instanceof StringValue) {
            return ValueFactory.createValue(((StringValue) args[0]).getValue().length());
        } else {
            throw new FunctionException("length only operates with string arguments: " + args[0].getClass().getName());
        }
    }

    public String getName() {
        return "length";
    }

    public boolean isAggregate() {
        return false;
    }

    public Function cloneFunction() {
        return new LengthFunction();
    }

	/**
	 * @see org.gdms.sql.function.Function#getType()
	 */
	public int getType(int[] types) {
		
		return types[0];
	}

}
