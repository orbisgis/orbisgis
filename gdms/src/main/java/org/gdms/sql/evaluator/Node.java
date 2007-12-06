package org.gdms.sql.evaluator;

import org.gdms.data.values.Value;
import org.gdms.sql.instruction.IncompatibleTypesException;

public interface Node {

	Node getLeftOperator();

	Node getRightOperator();

	void setLeftOperator(Node left);

	void setRightOperator(Node right);

	Value evaluate() throws IncompatibleTypesException;
}
