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

import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.sql.evaluator.Equals;
import org.gdms.sql.evaluator.EvaluationException;
import org.gdms.sql.evaluator.Expression;
import org.gdms.sql.evaluator.Field;
import org.gdms.sql.evaluator.FieldContext;
import org.orbisgis.progress.IProgressMonitor;

public class UpdateOperator extends AbstractExpressionOperator implements
		Operator {

	private ArrayList<Field> fields = new ArrayList<Field>();
	private ArrayList<Expression> values = new ArrayList<Expression>();
	private Expression filterExpression;

	@Override
	protected Expression[] getExpressions() throws DriverException,
			SemanticException {
		ArrayList<Expression> ret = new ArrayList<Expression>();
		ret.addAll(fields);
		ret.addAll(values);
		if (filterExpression != null) {
			ret.add(filterExpression);
		}

		return ret.toArray(new Expression[0]);
	}

	public ObjectDriver getResultContents(IProgressMonitor pm)
			throws ExecutionException {
		try {
			String sourceName = getTableName();
			final DataSource ds = getDataSourceFactory().getDataSource(
					sourceName);
			Field[] fieldReferences = getFieldReferences();
			DataSourceFieldIndex dsFieldContext = new DataSourceFieldIndex(ds);
			for (Field field : fieldReferences) {
				field.setFieldContext(dsFieldContext);
			}
			ds.open();
			for (int i = 0; i < ds.getRowCount(); i++) {
				dsFieldContext.setIndex(i);
				if ((filterExpression == null)
						|| evaluatesToTrue(new Expression[] { filterExpression })) {
					for (int j = 0; j < fields.size(); j++) {
						int fieldIndex = ds.getFieldIndexByName(fields.get(j)
								.getFieldName());
						ds.setFieldValue(i, fieldIndex, values.get(j)
								.evaluate());
					}
				}
			}
			ds.commit();
			ds.close();
		} catch (DriverLoadException e) {
			throw new ExecutionException("Cannot access source", e);
		} catch (NoSuchTableException e) {
			throw new ExecutionException("Cannot find source", e);
		} catch (AlreadyClosedException e) {
			throw new ExecutionException("Cannot modify source", e);
		} catch (DataSourceCreationException e) {
			throw new ExecutionException("Cannot access source", e);
		} catch (DriverException e) {
			throw new ExecutionException("Cannot modify source", e);
		} catch (NonEditableDataSourceException e) {
			throw new ExecutionException("The source is not editable", e);
		} catch (EvaluationException e) {
			throw new ExecutionException("Cannot evaluate update expression", e);
		} catch (SemanticException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	private boolean evaluatesToTrue(Expression[] exprs)
			throws EvaluationException {
		for (Expression expression : exprs) {
			if (!evaluatesToTrue(expression)) {
				return false;
			}
		}

		return true;
	}

	private static boolean evaluatesToTrue(Expression expression)
			throws IncompatibleTypesException, EvaluationException {
		Value expressionResult = expression.evaluate();
		return !expressionResult.isNull() && expressionResult.getAsBoolean();
	}

	public Metadata getResultMetadata() throws DriverException {
		return null;
	}

	public void addAssignment(Field field, Expression value) {
		fields.add(field);
		values.add(value);
	}

	/**
	 * Validates that the assignment is possible
	 *
	 * @see org.gdms.sql.strategies.AbstractExpressionOperator#validateExpressionTypes()
	 */
	@Override
	public void validateExpressionTypes() throws SemanticException,
			DriverException {
		for (int i = 0; i < fields.size(); i++) {
			Field field = fields.get(i);
			Expression value = values.get(i);
			Equals equals = new Equals(field, value);
			equals.validateExpressionTypes();
		}

		super.validateExpressionTypes();
	}

	public void setWhereExpression(Expression filterExpression) {
		this.filterExpression = filterExpression;
	}

	private final class DataSourceFieldIndex implements FieldContext {
		private final DataSource ds;
		private int rowIndex;

		private DataSourceFieldIndex(DataSource ds) {
			this.ds = ds;
		}

		public void setIndex(int i) {
			rowIndex = i;
		}

		@Override
		public Value getFieldValue(int fieldId) throws DriverException {
			return ds.getFieldValue(rowIndex, fieldId);
		}

		@Override
		public Type getFieldType(int fieldId) throws DriverException {
			return ds.getFieldType(fieldId);
		}
	}

}
