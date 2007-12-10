package org.gdms.sql.evaluator;



public abstract class Operator implements Node {

	private Node left;
	private Node right;

	public Operator(Node left, Node right) {
		this.left = left;
		this.right = right;
	}

	public Node getLeftOperator() {
		return left;
	}

	public Node getRightOperator() {
		return right;
	}

	public void setLeftOperator(Node left) {
		this.left = left;
	}

	public void setRightOperator(Node right) {
		this.right = right;
	}

	public void setEvaluationContext(EvaluationContext ec) {
		getRightOperator().setEvaluationContext(ec);
		getLeftOperator().setEvaluationContext(ec);
	}

}
