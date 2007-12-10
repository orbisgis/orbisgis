package org.gdms.sql.evaluator;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.instruction.IncompatibleTypesException;

public class Literal extends Operand {

	private String literal;
	private Value value = null;

	public Literal(String literal) {
		this.literal = literal;
	}

	public Value evaluate() throws IncompatibleTypesException {
		if (value == null) {
			try {
				value = ValueFactory.createValue(Integer.parseInt(literal));
			} catch (NumberFormatException e) {
				try {
					value = ValueFactory.createValue(Double
							.parseDouble(literal));
				} catch (NumberFormatException e1) {
					value = ValueFactory.createValue(literal);
				}
			}
		}

		return value;
	}

}
