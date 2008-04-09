package org.gdms.sql.strategies;

import java.util.ArrayList;

import org.gdms.data.DataSource;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.customQuery.QueryManager;
import org.gdms.sql.evaluator.EvaluationException;
import org.gdms.sql.evaluator.Expression;
import org.gdms.sql.evaluator.Field;
import org.gdms.sql.evaluator.FieldContext;
import org.gdms.sql.evaluator.FunctionOperator;
import org.orbisgis.IProgressMonitor;

public class CustomQueryOperator extends AbstractExpressionOperator implements
		Operator {

	private Expression[] expressions;
	private String functionName;
	private CustomQuery query;
	private ArrayList<Value> fieldNames;
	private ArrayList<Type> fieldTypes;

	public CustomQueryOperator(Expression customQueryExpr) {
		this.expressions = new Expression[customQueryExpr.getChildCount()];
		for (int i = 0; i < expressions.length; i++) {
			expressions[i] = customQueryExpr.getChild(i);
		}

		FunctionOperator functionOperator = (FunctionOperator) customQueryExpr;
		functionName = functionOperator.getFunctionName();
	}

	public ObjectDriver getResultContents(IProgressMonitor pm)
			throws ExecutionException {
		FieldContext fc = new FieldContext() {

			public Value getFieldValue(int fieldId) throws DriverException {
				return getFieldNames().get(fieldId);
			}

			public Type getFieldType(int fieldId) throws DriverException {
				return getFieldTypes().get(fieldId);
			}

		};
		Value[] values = new Value[expressions.length];
		for (int i = 0; i < values.length; i++) {
			try {
				Field[] fieldReferences = expressions[i].getFieldReferences();
				for (Field field : fieldReferences) {
					field.setFieldContext(fc);
				}
				values[i] = expressions[i].evaluate();
			} catch (EvaluationException e) {
				throw new ExecutionException("Cannot evaluate the "
						+ "parameters of the function", e);
			}
		}

		DataSource[] tables = null;
		if (getOperatorCount() > 0) {
			Operator scalar = getOperator(0);
			if (scalar instanceof SelectionOp) {
				try {
					tables = new DataSource[] { getDataSourceFactory()
							.getDataSource(scalar.getResult(pm)) };
				} catch (DriverException e) {
					throw new ExecutionException("Cannot obtain "
							+ "the sources in the sql", e);
				}
			} else {
				tables = new DataSource[scalar.getOperatorCount()];
				for (int i = 0; i < tables.length; i++) {
					ObjectDriver source = scalar.getOperator(i).getResult(pm);
					DataSource ds = null;
					if (source instanceof DataSourceDriver) {
						ds = ((DataSourceDriver) source).getDataSource();
					} else {
						try {
							ds = getDataSourceFactory().getDataSource(source);
						} catch (DriverException e) {
							throw new ExecutionException("Cannot obtain "
									+ "the sources in the sql", e);
						}
					}
					tables[i] = ds;
				}
			}
		} else {
			tables = new DataSource[0];
		}

		return getCustomQuery().evaluate(getDataSourceFactory(), tables,
				values, pm);
	}

	private ArrayList<Value> getFieldNames() throws DriverException {
		if (fieldNames == null) {
			getAllFieldNamesAndTypes();
		}
		return fieldNames;
	}

	private ArrayList<Type> getFieldTypes() throws DriverException {
		if (fieldTypes == null) {
			getAllFieldNamesAndTypes();
		}
		return fieldTypes;
	}

	private void getAllFieldNamesAndTypes() throws DriverException {
		fieldNames = new ArrayList<Value>();
		fieldTypes = new ArrayList<Type>();
		Metadata[] metadatas = getTablesMetadata();
		for (Metadata metadata : metadatas) {
			for (int i = 0; i < metadata.getFieldCount(); i++) {
				fieldNames.add(ValueFactory.createValue(metadata
						.getFieldName(i)));
				fieldTypes.add(metadata.getFieldType(i));
			}
		}

	}

	public Metadata getResultMetadata() throws DriverException {
		return getCustomQuery().getMetadata(getTablesMetadata());
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

		Metadata[] tables = getTablesMetadata();

		getCustomQuery().validateTables(tables);
	}

	private Metadata[] getTablesMetadata() throws DriverException {
		if (getOperatorCount() > 0) {
			Operator scalar = getOperator(0);
			Metadata[] tables = new Metadata[scalar.getOperatorCount()];
			for (int i = 0; i < tables.length; i++) {
				tables[i] = scalar.getOperator(i).getResultMetadata();
			}
			return tables;
		} else {
			return new Metadata[0];
		}
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

}