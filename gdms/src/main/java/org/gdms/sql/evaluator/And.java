package org.gdms.sql.evaluator;

import org.gdms.data.values.Value;
import org.gdms.sql.instruction.IncompatibleTypesException;

public class And extends Operator{

	public And(Node left, Node right) {
		super(left, right);
	}

	public Value evaluate() throws IncompatibleTypesException {
		Value leftValue = getLeftOperator().evaluate();
		Value rightValue = getRightOperator().evaluate();
		return leftValue.and(rightValue);
	}

}
