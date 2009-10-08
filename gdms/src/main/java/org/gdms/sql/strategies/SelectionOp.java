/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.sql.strategies;

import java.util.ArrayList;

import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.indexes.IndexManager;
import org.gdms.data.indexes.IndexQuery;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.sql.customQuery.QueryManager;
import org.gdms.sql.evaluator.EvaluationException;
import org.gdms.sql.evaluator.Expression;
import org.gdms.sql.evaluator.Field;
import org.gdms.sql.evaluator.FunctionOperator;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionManager;
import org.orbisgis.progress.IProgressMonitor;

public class SelectionOp extends AbstractExpressionOperator implements Operator {

	private Expression[] expressions;

	private ArrayList<Integer> indexes;

	private int limit = -1;

	private int offset = -1;

	private IndexQuery[] queries = null;

	/**
	 * Set the expression to filter
	 *
	 * @param operator
	 */
	public void setExpression(Expression operator) {
		this.expressions = operator.splitAnds();
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
		Operator operator = getOperator(0);
		try {
			if ((expressions.length == 0) && (limit == -1) && (offset == -1)) {
				return operator.getResult(pm);
			} else {
				ObjectDriver opResult = operator.getResult(pm);
				if (pm.isCancelled()) {
					return null;
				} else {
					ObjectDriver ret = new RowMappedDriver(opResult,
							getIndexes(pm));
					return ret;
				}
			}
		} catch (IncompatibleTypesException e) {
			throw new ExecutionException("Invalid condition expression", e);
		} catch (EvaluationException e) {
			throw new ExecutionException("Cannot evaluate condition", e);
		} catch (DriverException e) {
			throw new ExecutionException("Error accessing the source", e);
		}
	}

	/**
	 * returns null if the scan strategy has not been yet chosen. Returns the
	 * array of index queries used in the scan strategy. Zero length array means
	 * table-scan
	 *
	 * @return
	 */
	public IndexQuery[] getIndexQueries() {
		return queries;
	}

	public void chooseScanStrategy(IndexManager indexManager)
			throws DriverException, NoSuchTableException {
		Operator op = getOperator(0);
		if (op instanceof ScanOperator) {
			ScanOperator scan = (ScanOperator) op;

			ArrayList<IndexQuery> queries = new ArrayList<IndexQuery>();
			ArrayList<Expression> toRemove = new ArrayList<Expression>();
			for (int i = 0; i < expressions.length; i++) {
				boolean filterNecessary = true;
				Expression expression = expressions[i];
				Field[] fields = expression.getFieldReferences();
				for (Field field : fields) {
					if (indexManager.isIndexed(scan.getTableName(), field
							.getFieldName())) {
						IndexQuery[] query = ScanOperator.getQuery(
								new JoinContext() {

									public boolean isEvaluable(Expression exp) {
										return false;
									}

								}, field, expression);
						if (query != null) {
							boolean allStrict = true;
							for (IndexQuery indexQuery : query) {
								if (!indexQuery.isStrict()) {
									allStrict = false;
								}
								queries.add(indexQuery);
							}
							if (allStrict) {
								filterNecessary = false;
							}
						}
					}

				}
				if (!filterNecessary) {
					toRemove.add(expression);
				}
			}
			this.queries = queries.toArray(new IndexQuery[0]);

			// Remove unnecessary expressions
			for (Expression expression : toRemove) {
				removeExpression(expression);
			}

			// Set the scan mode for the child
			getOperator(0).setScanMode(this.queries);
		} else {
			throw new RuntimeException("bug!");
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

	@Override
	public void initialize() throws DriverException {
		super.initialize();
		indexes = null;
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
		Value expressionResult = expression.evaluate();
		return !expressionResult.isNull() && expressionResult.getAsBoolean();
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
	public Expression[] getExpressions() throws DriverException,
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
			if (function != null) {
				if (function.isAggregate()) {
					throw new SemanticException("'Where' clause "
							+ "cannot contain aggregated functions");
				}
			} else {
				if (QueryManager.getQuery(functionOperator.getFunctionName()) != null) {
					throw new SemanticException(
							"Custom queries not valid in where clause: "
									+ functionOperator.getFunctionName());
				} else {
					throw new SemanticException("Function '"
							+ functionOperator.getFunctionName()
							+ "' not found");
				}
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

	public void setQueries(IndexQuery[] queries) {
		this.queries = queries;
	}

	public void substituteChild(Operator newScalarProduct) {
		children.remove(0);
		children.add(newScalarProduct);
	}

	@Override
	public String toString() {
		StringBuffer indexScansString = new StringBuffer();
		if (queries != null) {
			for (IndexQuery indexQuery : queries) {
				indexScansString.append(indexQuery.getFieldName()).append("-");
			}
		}
		String ret = this.getClass().getSimpleName() + "-" + indexScansString
				+ "(";
		for (int i = 0; i < children.size(); i++) {
			ret = ret + children.get(i);
		}
		return ret + ")";
	}

	@Override
	public int passFieldUp(Field field) throws DriverException,
			AmbiguousFieldReferenceException {
		return getOperator(0).passFieldUp(field);
	}
}
