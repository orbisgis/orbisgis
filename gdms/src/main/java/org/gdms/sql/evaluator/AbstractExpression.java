package org.gdms.sql.evaluator;

import org.gdms.driver.DriverException;
import org.gdms.sql.strategies.IncompatibleTypesException;

public abstract class AbstractExpression implements Expression {

	protected abstract void validateExpressionTypes()
			throws IncompatibleTypesException, DriverException;

	public final void validateTypes() throws IncompatibleTypesException,
			DriverException {
		validateExpressionTypes();
		for (int i = 0; i < getChildrenCount(); i++) {
			((AbstractExpression) getChild(i)).validateTypes();
		}
	}

	public Expression[] getPath(Field field) {
		for (int i = 0; i < getChildrenCount(); i++) {
			Expression[] path = getChild(i).getPath(field);
			if (path != null) {
				Expression[] ret = new Expression[path.length + 1];
				ret[0] = this;
				System.arraycopy(path, 0, ret, 1, path.length);
			}
		}

		if (this == field) {
			return new Expression[]{this};
		} else {
			return null;
		}
	}
}
