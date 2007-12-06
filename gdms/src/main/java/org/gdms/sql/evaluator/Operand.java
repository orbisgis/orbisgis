package org.gdms.sql.evaluator;

public abstract class Operand implements Node {

	public Node getLeftOperator() {
		return null;
	}

	public Node getRightOperator() {
		return null;
	}

	public void setLeftOperator(Node left) {
		throw new UnsupportedOperationException("Operators doesn't have childs");
	}

	public void setRightOperator(Node left) {
		throw new UnsupportedOperationException("Operators doesn't have childs");
	}

}
