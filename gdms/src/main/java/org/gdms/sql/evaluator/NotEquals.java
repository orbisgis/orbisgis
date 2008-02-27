package org.gdms.sql.evaluator;

import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.strategies.IncompatibleTypesException;

public class NotEquals extends ComparisonOperator {

	public NotEquals(Expression... children) {
		super(children);
	}

	public Value evaluateExpression() throws EvaluationException {
		Value leftValue = getLeftOperator().evaluate();
		Value rightValue = getRightOperator().evaluate();
		return leftValue.notEquals(rightValue);
	}

	public Type getType() {
		return TypeFactory.createType(Type.BOOLEAN);
	}

	public void validateExpressionTypes() throws IncompatibleTypesException,
			DriverException {
		if (getLeftOperator().getType().getTypeCode() != getRightOperator()
				.getType().getTypeCode()) {
			super.validateExpressionTypes();
		}
	}

	public Expression cloneExpression() {
		return new NotEquals(getChildren());
	}

}
