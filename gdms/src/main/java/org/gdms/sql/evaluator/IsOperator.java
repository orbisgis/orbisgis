package org.gdms.sql.evaluator;

import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.sql.strategies.IncompatibleTypesException;

public class IsOperator extends AbstractOperator {

	private boolean not;

	public IsOperator(Expression ref, boolean not) {
		super(ref);
		this.not = not;
	}

	@Override
	protected void validateExpressionTypes() throws IncompatibleTypesException,
			DriverException {
		// always valid
	}

	public Value evaluateExpression() throws EvaluationException {
		boolean ret = getChild(0).evaluate().isNull();
		if (not) {
			ret = !ret;
		}
		return ValueFactory.createValue(ret);
	}

	public Type getType() throws DriverException {
		return TypeFactory.createType(Type.BOOLEAN);
	}

	public Expression cloneExpression() {
		return new IsOperator(getChildren()[0], not);
	}

}
