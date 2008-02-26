package org.gdms.sql.evaluator;

import java.util.ArrayList;

public abstract class AbstractOperator extends AbstractExpression implements Expression {

	private Expression[] children;

	public AbstractOperator(Expression...children) {
		this.children = children;
	}

	public Field[] getFieldReferences() {
		ArrayList<Field> ret = new ArrayList<Field>();
		for (Expression argument : children) {
			Field[] fieldRefs = argument.getFieldReferences();
			for (Field fieldRef : fieldRefs) {
				ret.add(fieldRef);
			}
		}

		return ret.toArray(new Field[0]);
	}

	public FunctionOperator[] getFunctionReferences() {
		ArrayList<FunctionOperator> ret = new ArrayList<FunctionOperator>();
		for (Expression argument : children) {
			FunctionOperator[] functionRefs = argument.getFunctionReferences();
			for (FunctionOperator functionRef : functionRefs) {
				ret.add(functionRef);
			}
		}

		return ret.toArray(new FunctionOperator[0]);
	}

	public Expression getChild(int index) {
		return children[index];
	}

	public int getChildrenCount() {
		return children.length;
	}

	public void setChildren(Expression[] expressions) {
		children = expressions;
	}

	public Expression[] getChildren() {
		return children;
	}

	public void replace(Expression expression1, Expression expression2) {
		for (int i = 0; i < children.length; i++) {
			Expression expr = children[i];
			if (expr  == expression1) {
				children[i] = expression2;
			}
		}
	}

}
