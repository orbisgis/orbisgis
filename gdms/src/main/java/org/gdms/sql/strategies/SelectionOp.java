package org.gdms.sql.strategies;

import java.util.ArrayList;

import org.gdms.data.ExecutionException;
import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.sql.evaluator.EvaluationException;
import org.gdms.sql.evaluator.Expression;
import org.gdms.sql.evaluator.Field;
import org.gdms.sql.evaluator.FunctionOperator;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionManager;
import org.orbisgis.IProgressMonitor;

public class SelectionOp extends AbstractExpressionOperator implements Operator {

	private Expression expression;

	private ArrayList<Integer> indexes;

	public void setExpression(Expression operator) {
		this.expression = operator;
	}

	public ObjectDriver getResultContents(IProgressMonitor pm)
			throws ExecutionException {
		try {
			return new RowMappedDriver(getOperator(0).getResult(pm),
					getIndexes(pm));
		} catch (IncompatibleTypesException e) {
			throw new ExecutionException(e);
		} catch (EvaluationException e) {
			throw new ExecutionException(e);
		} catch (DriverException e) {
			throw new ExecutionException(e);
		}
	}

	private ArrayList<Integer> getIndexes(IProgressMonitor pm)
			throws IncompatibleTypesException, EvaluationException,
			ExecutionException, DriverException {
		if (indexes == null) {
			indexes = new ArrayList<Integer>();
			ObjectDriver ds = getOperator(0).getResult(pm);
			Field[] fieldReferences = expression.getFieldReferences();
			DefaultFieldContext selectionFieldContext = new DefaultFieldContext(
					ds);
			for (Field field : fieldReferences) {
				field.setFieldContext(selectionFieldContext);
			}
			for (int i = 0; i < ds.getRowCount(); i++) {
				if (i / 1000 == i / 1000.0) {
					pm.progressTo((int) (100 * i / ds.getRowCount()));
				}
				selectionFieldContext.setIndex(i);
				if (!expression.evaluate().isNull()
						&& expression.evaluate().getAsBoolean()) {
					indexes.add(i);
				}
			}
		}

		return indexes;
	}

	public Metadata getResultMetadata() throws DriverException {
		return getOperator(0).getResultMetadata();
	}

	@Override
	protected Expression[] getExpressions() throws DriverException,
			SemanticException {
		return new Expression[] { expression };
	}

	/**
	 * Checks there is no aggregate function in the where clause
	 *
	 * @see org.gdms.sql.strategies.AbstractExpressionOperator#validateFunctionReferences()
	 */
	@Override
	public void validateFunctionReferences() throws DriverException,
			SemanticException {
		super.validateFunctionReferences();
		FunctionOperator[] functions = expression.getFunctionReferences();
		for (FunctionOperator functionOperator : functions) {
			Function function = FunctionManager.getFunction(functionOperator
					.getFunctionName());
			if (function.isAggregate()) {
				throw new SemanticException("'Where' clause "
						+ "cannot contain aggregated functions");
			}
		}
	}
}
