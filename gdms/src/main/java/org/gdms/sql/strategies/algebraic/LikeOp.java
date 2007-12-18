package org.gdms.sql.strategies.algebraic;

public class LikeOp extends DefaultOperator implements Expr {

	private Expr expression;
	private Expr pattern;

	public void setExpression(Expr expr) {
		this.expression = expr;
	}

	public void setPattern(Expr expr) {
		this.pattern = expr;
	}

}
