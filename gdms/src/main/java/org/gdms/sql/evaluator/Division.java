package org.gdms.sql.evaluator;

import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.strategies.IncompatibleTypesException;

public class Division extends ArithmeticOperator {

	public Division(Expression... children) {
		super(children);
	}

	public Value evaluate() throws EvaluationException,
			IncompatibleTypesException {
		return getLeftOperator().evaluate().producto(
				getRightOperator().evaluate().inversa());
	}

	@Override
	public Type getType() throws DriverException {
		int rightCode = getRightOperator().getType().getTypeCode();
		int leftCode = getLeftOperator().getType().getTypeCode();
		switch (leftCode) {
		case Type.BYTE:
		case Type.SHORT:
		case Type.INT:
		case Type.LONG:
			if ((rightCode == Type.BYTE) || (rightCode == Type.SHORT)
					|| (rightCode == Type.INT) || (rightCode == Type.LONG)) {
				return TypeFactory.createType(leftCode);
			} else {
				return TypeFactory.createType(Type.DOUBLE);
			}
		case Type.FLOAT:
		case Type.DOUBLE:
			return TypeFactory.createType(Type.DOUBLE);
		}
		return TypeFactory.createType(rightCode);
	}

	@Override
	protected String getOperatorSymbol() {
		return "/";
	}

	public Expression cloneExpression() {
		return new Division(getChildren());
	}

}
