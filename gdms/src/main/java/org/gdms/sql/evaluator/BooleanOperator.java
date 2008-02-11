package org.gdms.sql.evaluator;

import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.gdms.sql.strategies.IncompatibleTypesException;

public abstract class BooleanOperator extends Operator {

	public BooleanOperator(Expression...children) {
		super(children);
	}

	public void validateExpressionTypes() throws IncompatibleTypesException,
			DriverException {
		validateOperand(getLeftOperator());
		validateOperand(getRightOperator());
	}

	private void validateOperand(Expression operand) throws DriverException {
		if (operand.getType().getTypeCode() != Type.BOOLEAN) {
			throw new IncompatibleTypesException(getClass().getName()
					+ " can only operate on boolean types");
		}
	}

}
