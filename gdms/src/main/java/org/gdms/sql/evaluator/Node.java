package org.gdms.sql.evaluator;

import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.instruction.IncompatibleTypesException;

public interface Node {

	Node getLeftOperator();

	Node getRightOperator();

	void setLeftOperator(Node left);

	void setRightOperator(Node right);

	/**
	 * Evaluates this expression tree. If the tree contains field references and
	 * no DataSource was specified it throws NullPointerException
	 *
	 * @return The result of the evaluation
	 * @throws IncompatibleTypesException
	 * @throws DriverException
	 */
	Value evaluate() throws IncompatibleTypesException, DriverException;

	/**
	 * Sets the EvaluationContext used to evaluate this method. It's not
	 * necessary to invoke this method if the expression tree doesn't contain
	 * field references
	 *
	 * @param ec
	 *            TODO
	 */
	void setEvaluationContext(EvaluationContext ec);

}
