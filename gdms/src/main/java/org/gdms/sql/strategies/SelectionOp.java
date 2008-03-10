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

	private Expression[] expressions;

	private ArrayList<Integer> indexes;

	private int limit = -1;

	private int offset = -1;

	/**
	 * Set the expression to filter
	 *
	 * @param operator
	 */
	public void setExpression(Expression operator) {
		this.expressions = new Expression[] { operator };
	}

	/**
	 * Sets the expressions to filter. The overall value of the selection
	 * expression is the AND operation of all the expressions specified as
	 * parameters
	 *
	 * @param operator
	 */
	public void setExpressions(Expression[] operator) {
		this.expressions = operator;
	}

	public ObjectDriver getResultContents(IProgressMonitor pm)
			throws ExecutionException {
		try {
			ObjectDriver ret = new RowMappedDriver(
					getOperator(0).getResult(pm), getIndexes(pm));
			return ret;
		} catch (IncompatibleTypesException e) {
			throw new ExecutionException(e);
		} catch (EvaluationException e) {
			throw new ExecutionException(e);
		} catch (DriverException e) {
			throw new ExecutionException(e);
		}
	}

	@Override
	protected Field[] getFieldReferences() throws DriverException {
		try {
			return super.getFieldReferences();
		} catch (SemanticException e) {
			throw new RuntimeException("Shouldn't happen in this node");
		}
	}

	private ArrayList<Integer> getIndexes(IProgressMonitor pm)
			throws IncompatibleTypesException, EvaluationException,
			ExecutionException, DriverException {
		if (indexes == null) {
			indexes = new ArrayList<Integer>();
			ObjectDriver ds = getOperator(0).getResult(pm);
			if (pm.isCancelled()) {
				return null;
			} else {
				Field[] fieldReferences = getFieldReferences();
				DefaultFieldContext selectionFieldContext = new DefaultFieldContext(
						ds);
				for (Field field : fieldReferences) {
					field.setFieldContext(selectionFieldContext);
				}
				pm.startTask("Filtering");
				long sourceRowCount = ds.getRowCount();
				for (int i = 0; i < sourceRowCount; i++) {
					if (i / 1000 == i / 1000.0) {
						if (pm.isCancelled()) {
							return null;
						} else {
							pm.progressTo((int) (100 * i / sourceRowCount));
						}
					}
					selectionFieldContext.setIndex(i);
					if (evaluatesToTrue()) {
						indexes.add(i);
					}

					// Check limit and offset
					if (enough(indexes.size(), (int) sourceRowCount)) {
						break;
					}
				}
				pm.endTask();
			}

			if (offset != -1) {
				for (int i = 0; i < offset; i++) {
					indexes.remove(0);
				}
			}
		}

		return indexes;
	}

	/**
	 * Evaluates the and expressions and stops at the first false
	 *
	 * @return
	 * @throws IncompatibleTypesException
	 * @throws EvaluationException
	 */
	private boolean evaluatesToTrue() throws IncompatibleTypesException,
			EvaluationException {
		for (Expression expression : expressions) {
			if (!evaluatesToTrue(expression)) {
				return false;
			}
		}

		return true;
	}

	private boolean evaluatesToTrue(Expression expression)
			throws IncompatibleTypesException, EvaluationException {
		return !expression.evaluate().isNull()
				&& expression.evaluate().getAsBoolean();
	}

	private boolean enough(int resultCount, int rowCount) {
		if (limit != -1) {
			if (offset != -1) {
				return resultCount >= limit + offset;
			} else {
				return resultCount >= limit;
			}
		} else {
			return false;
		}
	}

	public Metadata getResultMetadata() throws DriverException {
		return getOperator(0).getResultMetadata();
	}

	@Override
	protected Expression[] getExpressions() throws DriverException,
			SemanticException {
		return expressions;
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
		FunctionOperator[] functions = getFunctionReferences();
		for (FunctionOperator functionOperator : functions) {
			Function function = FunctionManager.getFunction(functionOperator
					.getFunctionName());
			if (function.isAggregate()) {
				throw new SemanticException("'Where' clause "
						+ "cannot contain aggregated functions");
			}
		}
	}

	private FunctionOperator[] getFunctionReferences() {
		ArrayList<FunctionOperator> ret = new ArrayList<FunctionOperator>();
		for (Expression expression : expressions) {
			FunctionOperator[] functionReferences = expression
					.getFunctionReferences();
			for (FunctionOperator functionOperator : functionReferences) {
				ret.add(functionOperator);
			}
		}

		return ret.toArray(new FunctionOperator[0]);
	}

	@Override
	public void setLimit(int limit) {
		this.limit = limit;
	}

	@Override
	public void setOffset(int offset) {
		this.offset = offset;
	}

	public void removeExpression(Expression expression) {
		ArrayList<Expression> newExpressions = new ArrayList<Expression>();
		for (Expression testExpression : expressions) {
			if (testExpression != expression) {
				newExpressions.add(testExpression);
			}
		}

		expressions = newExpressions.toArray(new Expression[0]);
	}
}
