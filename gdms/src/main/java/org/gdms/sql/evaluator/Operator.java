package org.gdms.sql.evaluator;



public abstract class Operator implements Expression {

	private Expression left;
	private Expression right;
	private EvaluationContext ec;

	public Operator(Expression left, Expression right) {
		this.left = left;
		this.right = right;
	}

	public Expression getLeftOperator() {
		return left;
	}

	public Expression getRightOperator() {
		return right;
	}

	public void setLeftOperator(Expression left) {
		this.left = left;
	}

	public void setRightOperator(Expression right) {
		this.right = right;
	}

	public void setEvaluationContext(EvaluationContext ec) {
		getRightOperator().setEvaluationContext(ec);
		getLeftOperator().setEvaluationContext(ec);
		this.ec = ec;
	}

	public EvaluationContext getEvaluationContext() {
		return ec;
	}
}
