package org.gdms.sql.evaluator;


public abstract class Operand implements Expression {

	protected EvaluationContext ec;

	public Expression getLeftOperator() {
		return null;
	}

	public Expression getRightOperator() {
		return null;
	}

	public void setLeftOperator(Expression left) {
		throw new UnsupportedOperationException("Operators doesn't have childs");
	}

	public void setRightOperator(Expression left) {
		throw new UnsupportedOperationException("Operators doesn't have childs");
	}

	public void setEvaluationContext(EvaluationContext ec) {
		this.ec = ec;
	}

	public EvaluationContext getEvaluationContext() {
		return ec;
	}
}
