package org.gdms.sql.evaluator;

import org.gdms.data.values.Value;

public interface Node {

	Node getLeftOperator();

	Node getRightOperator();

	void setLeftOperator(Node left);

	void setRightOperator(Node right);

	Value evaluate();
}
