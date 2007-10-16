package org.urbsat.utilities;

import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

public class CreateRugoxel implements Function {
	static int gasp =0;
	public Function cloneFunction() {

		return new CreateRugoxel();
	}

	public Value evaluate(Value[] args) throws FunctionException {
		String ar1 = args[1].toString();
		
	 return ValueFactory.createValue("j");
	}

	public String getName() {

		return "CreateRugoxel";
	}

	public int getType(int[] types) {
		return Type.INT;
	}

	public boolean isAggregate() {

		return true;
	}

}