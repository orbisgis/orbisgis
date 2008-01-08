package org.gdms.sql.evaluator;

import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.instruction.IncompatibleTypesException;

public interface Expression {

	Expression getLeftOperator();

	Expression getRightOperator();

	void setLeftOperator(Expression left);

	void setRightOperator(Expression right);

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

	/**
	 * Gets the evaluation context of this expression
	 *
	 * @return
	 */
	EvaluationContext getEvaluationContext();

	/**
	 * Gets the type of the expression. It is one of the constants in
	 * {@link Type}
	 *
	 * @return
	 * @throws DriverException
	 */
	int getType() throws DriverException;

}
