package org.urbsat.function;

import org.gdms.data.values.DoubleValue;
import org.gdms.data.values.FloatValue;
import org.gdms.data.values.IntValue;
import org.gdms.data.values.LongValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.alphanumeric.Average;

public class MyFunction implements Function
{

	private Value result = null;
	
	private int constante = 12;
	
	public Function cloneFunction() {
		
		return new MyFunction();
	}

	public Value evaluate(Value[] args) throws FunctionException {
		
		
		return ValueFactory.createValue(constante);
	}

	public String getName() {
		
		return "MyFunction";
	}

	public int getType(int[] types) {
		return types[0];
	}

	public boolean isAggregate() {
		
		return false;
	}

}
