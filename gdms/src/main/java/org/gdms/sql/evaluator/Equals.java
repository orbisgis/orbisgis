package org.gdms.sql.evaluator;

import org.gdms.data.values.Value;
import org.gdms.sql.instruction.IncompatibleTypesException;

public class Equals extends Operator{

	public Equals(Node left, Node right) {
		super(left, right);
	}

	public Value evaluate() throws IncompatibleTypesException {
		Value leftValue = getLeftOperator().evaluate();
		Value rightValue = getRightOperator().evaluate();
		return leftValue.equals(rightValue);
	}

}
