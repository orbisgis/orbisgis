package org.gdms.sql.strategies.algebraic;

public class ComparisonOp extends DefaultOperator implements Expr {

	private int operator;
	private Expr leftExpression;
	private Expr rightExpression;

	public void setArithmeticOperator(int kind) {
		this.operator = kind;
	}

	public void setLeftExpression(Expr expr) {
		this.leftExpression = expr;
	}

	public void setRightExpression(Expr expr) {
		this.rightExpression = expr;
	}

}
