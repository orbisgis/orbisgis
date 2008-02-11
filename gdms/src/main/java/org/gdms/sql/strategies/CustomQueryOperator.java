package org.gdms.sql.strategies;

import org.gdms.data.DataSource;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.customQuery.QueryManager;
import org.gdms.sql.evaluator.EvaluationException;
import org.gdms.sql.evaluator.Expression;
import org.gdms.sql.evaluator.FunctionOperator;

public class CustomQueryOperator extends AbstractExpressionOperator implements
		Operator {

	private Expression[] expressions;
	private String functionName;
	private CustomQuery query;

	public CustomQueryOperator(Expression customQueryExpr) {
		this.expressions = new Expression[customQueryExpr.getChildrenCount()];
		for (int i = 0; i < expressions.length; i++) {
			expressions[i] = customQueryExpr.getChild(i);
		}

		FunctionOperator functionOperator = (FunctionOperator) customQueryExpr;
		functionName = functionOperator.getFunctionName();
	}

	public ObjectDriver getResultContents() throws ExecutionException {
		Value[] values = new Value[expressions.length];
		for (int i = 0; i < values.length; i++) {
			try {
				values[i] = expressions[i].evaluate();
			} catch (EvaluationException e) {
				throw new ExecutionException("Cannot evaluate the "
						+ "parameters of the function", e);
			}
		}

		DataSource[] tables = new DataSource[getOperatorCount()];
		for (int i = 0; i < tables.length; i++) {
			ObjectDriver source = getOperator(i).getResult();
			try {
				tables[i] = getDataSourceFactory().getDataSource(source);
			} catch (DriverException e) {
				throw new ExecutionException("Cannot obtain "
						+ "the sources in the sql", e);
			}
		}

		return getCustomQuery().evaluate(getDataSourceFactory(), tables, values);
	}

	public Metadata getResultMetadata() throws DriverException {
		return getCustomQuery().getMetadata();
	}

	private CustomQuery getCustomQuery() {
		if (query == null) {
			query = QueryManager.getQuery(functionName);
		}

		return query;
	}

	@Override
	protected Expression[] getExpressions() throws DriverException,
			SemanticException {
		return expressions;
	}

	@Override
	public void validateTableReferences() throws NoSuchTableException,
			SemanticException, DriverException {
		super.validateTableReferences();

		Metadata[] tables = new Metadata[getOperatorCount()];
		for (int i = 0; i < tables.length; i++) {
			tables[i] = getOperator(i).getResultMetadata();
		}

		getCustomQuery().validateTables(tables);
	}

	@Override
	public void validateExpressionTypes() throws SemanticException,
			DriverException {
		super.validateExpressionTypes();

		Type[] types = new Type[expressions.length];
		for (int i = 0; i < types.length; i++) {
			types[i] = expressions[i].getType();
		}
		getCustomQuery().validateTypes(types);
	}

	@Override
	public void validateFieldReferences() throws SemanticException,
			DriverException {
		for (Expression expr : expressions) {
			if (expr.getFieldReferences().length > 0) {
				throw new SemanticException("Custom query parameters "
						+ "cannot contain field references");
			}
		}
		super.validateFieldReferences();
	}
}