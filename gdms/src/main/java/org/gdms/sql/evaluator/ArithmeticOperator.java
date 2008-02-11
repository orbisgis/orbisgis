package org.gdms.sql.evaluator;

import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.driver.DriverException;
import org.gdms.sql.strategies.IncompatibleTypesException;

public abstract class ArithmeticOperator extends Operator {

	public ArithmeticOperator(Expression...children) {
		super(children);
	}

	public Type getType() throws DriverException {
		int rightCode = getRightOperator().getType().getTypeCode();
		int leftCode = getLeftOperator().getType().getTypeCode();
		switch (leftCode) {
		case Type.SHORT:
			if (rightCode == Type.BYTE) {
				return TypeFactory.createType(leftCode);
			}
			break;
		case Type.INT:
			if ((rightCode == Type.BYTE) || (rightCode == Type.SHORT)) {
				return TypeFactory.createType(leftCode);
			}
			break;
		case Type.LONG:
			if ((rightCode == Type.BYTE) || (rightCode == Type.SHORT)
					|| (rightCode == Type.INT)) {
				return TypeFactory.createType(leftCode);
			}
			break;
		case Type.FLOAT:
			if ((rightCode == Type.BYTE) || (rightCode == Type.SHORT)
					|| (rightCode == Type.INT) || (rightCode == Type.LONG)) {
				return TypeFactory.createType(leftCode);
			}
			break;
		case Type.DOUBLE:
			return TypeFactory.createType(Type.DOUBLE);
		}
		return TypeFactory.createType(rightCode);
	}

	public void validateExpressionTypes() throws IncompatibleTypesException,
			DriverException {
		if ((!TypeFactory
				.isNumerical(getLeftOperator().getType().getTypeCode()))
				|| (!TypeFactory.isNumerical(getRightOperator().getType()
						.getTypeCode()))) {
			throw new IncompatibleTypesException("Cannot apply '"
					+ getOperatorSymbol() + "' to non numerical types");
		}
	}

	protected abstract String getOperatorSymbol();

}
