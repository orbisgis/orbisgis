package org.gdms.sql.evaluator;

import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.strategies.IncompatibleTypesException;

public class Not extends AbstractOperator {

	public Not(Expression expr) {
		super(expr);
	}

	public Value evaluateExpression() throws EvaluationException {
		Value result = getChild(0).evaluate();
		if (result.isNull()) {
			return result;
		} else {
			return result.inversa();
		}
	}

	public Type getType() {
		return TypeFactory.createType(Type.BOOLEAN);
	}

	@Override
	protected void validateExpressionTypes() throws IncompatibleTypesException,
			DriverException {
		if (getChild(0).getType().getTypeCode() != Type.BOOLEAN) {
			throw new IncompatibleTypesException("not "
					+ "operator can only operate on booleans");
		}
	}

	public Expression cloneExpression() {
		return new Not(getChild(0));
	}

}
