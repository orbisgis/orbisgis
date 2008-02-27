package org.gdms.sql.evaluator;

import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.sql.strategies.IncompatibleTypesException;

public class LikeOperator extends AbstractOperator {

	private boolean not;

	public LikeOperator(Expression ref, Expression pattern, boolean not) {
		super(new Expression[] { ref, pattern });
		this.not = not;
	}

	private Expression getRef() {
		return getChild(0);
	}

	private Expression getPattern() {
		return getChild(1);
	}

	@Override
	protected void validateExpressionTypes() throws IncompatibleTypesException,
			DriverException {
		if (getRef().getType().getTypeCode() != Type.STRING) {
			throw new IncompatibleTypesException(" like operator "
					+ "only operates with strings");
		} else if (getPattern().getType().getTypeCode() != Type.STRING) {
			throw new IncompatibleTypesException(" like pattern "
					+ "must be a string");
		}
	}

	public Value evaluateExpression() throws EvaluationException {
		Value value = getRef().evaluate();
		if (value.isNull()) {
			return ValueFactory.createValue(false);
		} else {
			Value ret = value.like(getPattern().evaluate());
			if (not) {
				ret.inversa();
			}

			return ret;
		}
	}

	public Type getType() throws DriverException {
		return TypeFactory.createType(Type.BOOLEAN);
	}

	public Expression cloneExpression() {
		return new LikeOperator(getChild(0), getChild(1), not);
	}

}
