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
import org.gdms.sql.customQuery.TableDefinition;
import org.gdms.sql.evaluator.EvaluationException;
import org.gdms.sql.evaluator.Expression;
import org.gdms.sql.evaluator.Field;
import org.gdms.sql.evaluator.FieldContext;
import org.gdms.sql.evaluator.FunctionOperator;
import org.orbisgis.progress.IProgressMonitor;

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

		TableDefinition[] tablesDef = getCustomQuery().geTablesDefinitions();
		if (tables.length != tablesDef.length) {
			throw new SemanticException("Invalid number of tables. "
					+ getCustomQuery().getName() + " expects "
					+ tablesDef.length);
		} else {
			for (int i = 0; i < tablesDef.length; i++) {
				if (!tablesDef[i].isValid(tables[i])) {
					throw new SemanticException("The table number " + i
							+ " is not valid." + tablesDef[i].getDescription());
				}
			}
		}
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
		FunctionOperator.validateArguments(types, getCustomQuery().getName(),
				getCustomQuery().getFunctionArguments());
	}

}