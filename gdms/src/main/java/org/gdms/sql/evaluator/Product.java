package org.gdms.sql.evaluator;

import org.gdms.data.values.Value;
import org.gdms.sql.strategies.IncompatibleTypesException;

public class Product extends ArithmeticOperator {

	public Product(Expression... children) {
		super(children);
	}

	public Value evaluate() throws EvaluationException,
			IncompatibleTypesException {
		return getLeftOperator().evaluate().producto(
				getRightOperator().evaluate());
	}

	@Override
	protected String getOperatorSymbol() {
		return "*";
	}

	public Expression cloneExpression() {
		return new Product(getChildren());
	}

}
