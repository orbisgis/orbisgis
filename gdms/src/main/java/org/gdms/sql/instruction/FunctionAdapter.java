package org.gdms.sql.instruction;

import java.util.ArrayList;
import java.util.Iterator;

import org.gdms.data.DataSource;
import org.gdms.data.edition.PhysicalDirection;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.function.ComplexFunction;
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
	public Value evaluate() throws EvaluationException {
		String functionName = getEntity().first_token.image;

		Function func = getFunction();

		if (func == null) {
			throw new EvaluationException("No function called " + functionName);
		}

		Adapter[] params = this.getChilds()[0].getChilds();
		Value[] paramValues = new Value[params.length];

		for (int i = 0; i < paramValues.length; i++) {
			paramValues[i] = ((Expression) params[i]).evaluate();
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

	public String getFieldTable() throws DriverException {
		return null;
	}

	public Iterator<PhysicalDirection> filter(DataSource from)
			throws DriverException {
		if (getFunction() instanceof ComplexFunction) {
			ComplexFunction function = (ComplexFunction) this.getFunction();
			Adapter[] params = this.getChilds()[0].getChilds();
			String[] fieldNames = new String[params.length];
			Value[] args = new Value[params.length];
			ArrayList<Integer> tableToFilter = new ArrayList<Integer>();
			for (int i = 0; i < fieldNames.length; i++) {
				fieldNames[i] = ((Expression) params[i]).getFieldName();
				String tableName = ((Expression) params[i]).getFieldTable();
				if (from.getName().equals(tableName)) {
					tableToFilter.add(new Integer(i));
				}
				if (getInstructionContext().isBeingIterated(tableName)
						|| ((Expression) params[i]).isLiteral()) {
					try {
						args[i] = ((Expression) params[i]).evaluate();
					} catch (EvaluationException e) {
						throw new DriverException(e);
					}
				} else {
					args[i] = null;
				}
			}

			if (tableToFilter.size() > 0) {
				return function.filter(args, fieldNames, from, tableToFilter);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

}
