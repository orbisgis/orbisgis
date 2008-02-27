package org.gdms.sql.evaluator;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.strategies.IncompatibleTypesException;

public class Substraction extends ArithmeticOperator {

	public Substraction(Expression... children) {
		super(children);
	}

	public Value evaluateExpression() throws EvaluationException,
			IncompatibleTypesException {
		return getLeftOperator().evaluate().suma(
				ValueFactory.createValue(-1).producto(
						getRightOperator().evaluate()));
	}

	@Override
	protected String getOperatorSymbol() {
		return "-";
	}

	public Expression cloneExpression() {
		return new Substraction(getChildren());
	}

}
