package org.gdms.sql.evaluator;

import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.instruction.IncompatibleTypesException;

public class GreaterThanOrEqual extends Operator {

	public GreaterThanOrEqual(Expression left, Expression right) {
		super(left, right);
	}

	public Value evaluate() throws IncompatibleTypesException, DriverException {
		Value leftValue = getLeftOperator().evaluate();
		Value rightValue = getRightOperator().evaluate();
		return leftValue.greaterEqual(rightValue);
	}

	public int getType() {
		return Type.BOOLEAN;
	}

}
