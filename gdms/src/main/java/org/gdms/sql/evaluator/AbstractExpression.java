package org.gdms.sql.evaluator;

import java.util.ArrayList;

import org.gdms.driver.DriverException;
import org.gdms.sql.strategies.IncompatibleTypesException;

public abstract class AbstractExpression implements Expression {

	protected abstract void validateExpressionTypes()
			throws IncompatibleTypesException, DriverException;

	public final void validateTypes() throws IncompatibleTypesException,
			DriverException {
		validateExpressionTypes();
		for (int i = 0; i < getChildCount(); i++) {
			((Expression) getChild(i)).validateTypes();
		}
	}

	public Expression[] getPath(Field field) {
		for (int i = 0; i < getChildCount(); i++) {
			Expression[] path = getChild(i).getPath(field);
			if (path != null) {
				Expression[] ret = new Expression[path.length + 1];
				ret[0] = this;
				System.arraycopy(path, 0, ret, 1, path.length);
			}
		}

		if (this == field) {
			return new Expression[] { this };
		} else {
			return null;
		}
	}

	public Expression changeOrForNotAnd() {
		return changeOrForNotAnd(this);
	}

	public Expression[] splitAnds() {
		return splitAnds(this).toArray(new Expression[0]);
	}

	private ArrayList<Expression> splitAnds(Expression expression) {
		ArrayList<Expression> ret = new ArrayList<Expression>();
		if (expression instanceof And) {
			for (int i = 0; i < expression.getChildCount(); i++) {
				ret.addAll(splitAnds(expression.getChild(i)));
			}
		} else if ((expression instanceof Not)
				&& (expression.getChild(0) instanceof And)) {
			ret.add(new Not(expression.getChild(0)));
		} else {
			ret.add(expression);
		}

		return ret;
	}

	private Expression changeOrForNotAnd(Expression expr) {
		if (expr instanceof Or) {
			Expression[] children = new Expression[expr.getChildCount()];
			for (int i = 0; i < children.length; i++) {
				children[i] = new Not(changeOrForNotAnd(expr.getChild(i)));
			}
			And and = new And(children);
			Not not = new Not(and);
			return not;
		} else if (expr instanceof And) {
			Expression[] children = new Expression[expr.getChildCount()];
			for (int i = 0; i < children.length; i++) {
				children[i] = changeOrForNotAnd(expr.getChild(i));
			}
			return new And(children);
		} else if (expr instanceof Not) {
			return new Not(changeOrForNotAnd(expr.getChild(0)));
		} else {
			return expr;
		}
	}

}
