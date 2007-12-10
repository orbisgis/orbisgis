package org.gdms.sql.evaluator;

import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.instruction.IncompatibleTypesException;

public class GreaterThan extends Operator {

	public GreaterThan(Node left, Node right) {
		super(left, right);
	}

	public Value evaluate() throws IncompatibleTypesException, DriverException {
		Value leftValue = getLeftOperator().evaluate();
		Value rightValue = getRightOperator().evaluate();
		return leftValue.greater(rightValue);
	}

}
