package org.gdms.sql.instruction;

import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionManager;

/**
 * DOCUMENT ME!
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public class FunctionAdapter extends AbstractExpression implements Expression {
	private Function function;

	/**
	 * @see org.gdms.sql.instruction.Expression#getFieldName()
	 */
	public String getFieldName() {
		return null;
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#simplify()
	 */
	public void simplify() {
		// Nunca se simplifica una funci�n
	}

	public String getFunctionName() {
		return getEntity().first_token.image;
	}

	public boolean isAggregated() {
		return FunctionManager.getFunction(getFunctionName()).isAggregate();
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#evaluate(long)
	 */
	public Value evaluate(long row) throws EvaluationException {
		String functionName = getEntity().first_token.image;

		Function func = getFunction();

		if (func == null) {
			throw new EvaluationException("No function called " + functionName);
		}

		Adapter[] params = this.getChilds()[0].getChilds();
		Value[] paramValues = new Value[params.length];

		for (int i = 0; i < paramValues.length; i++) {
			paramValues[i] = ((Expression) params[i]).evaluate(row);
		}

		try {
			return func.evaluate(paramValues);
		} catch (FunctionException e) {
			throw new EvaluationException("Function error", e);
		}
	}

	/**
	 * @return
	 */
	private Function getFunction() {
		if (function == null) {
			function = FunctionManager.getFunction(getFunctionName());

		}

		return function;
	}

	/**
	 * @see org.gdms.sql.instruction.Expression#isLiteral()
	 */
	public boolean isLiteral() {
		return Utilities.checkExpressions(this.getChilds()[0].getChilds());
	}

	/**
	 * @see org.gdbms.engine.instruction.Expression#getType()
	 */
	public int getType() throws DriverException {
		Adapter[] params = this.getChilds()[0].getChilds();
		int[] paramTypes = new int[params.length];

		for (int i = 0; i < params.length; i++) {
			paramTypes[i] = ((Expression) params[i]).getType();
		}

		return getFunction().getType(paramTypes);
	}

}
