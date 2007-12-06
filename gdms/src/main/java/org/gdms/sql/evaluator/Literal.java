package org.gdms.sql.evaluator;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.instruction.IncompatibleTypesException;

public class Literal extends Operand {

	private String literal;

	public Literal(String literal) {
		this.literal = literal;
	}

	public Value evaluate() throws IncompatibleTypesException {
		return ValueFactory.createValue(literal);
	}

}
