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
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.sql.evaluator.EvaluationException;
import org.gdms.sql.evaluator.Expression;
import org.gdms.sql.evaluator.Field;
import org.orbisgis.progress.IProgressMonitor;

public class InsertOperator extends AbstractExpressionOperator implements
		Operator {

	private ArrayList<Expression> fields = new ArrayList<Expression>();
	private ArrayList<Expression> values = new ArrayList<Expression>();

	@Override
	protected Expression[] getExpressions() throws DriverException,
			SemanticException {
		return getFields().toArray(new Expression[0]);
	}

	private ArrayList<Expression> getFields() throws DriverException {
		if (fields == null) {
			fields = new ArrayList<Expression>();
			Metadata metadata = getOperator(0).getResultMetadata();
			for (int i = 0; i < metadata.getFieldCount(); i++) {
				fields.add(new Field(metadata.getFieldName(i)));
			}
		}
		return fields;
	}

	public ObjectDriver getResultContents(IProgressMonitor pm)
			throws ExecutionException {
		String sourceName = getTableName();
		try {
			DataSource ds = getDataSourceFactory().getDataSource(sourceName);
			ds.open();
			Value[] row = new Value[ds.getFieldCount()];
			for (int i = 0; i < fields.size(); i++) {
				Field field = (Field) fields.get(i);
				System.out.println(field.getFieldName());
				int fieldIndex = ds.getFieldIndexByName(field.getFieldName());
				row[fieldIndex] = values.get(i).evaluate();
			}
			ds.insertFilledRow(row);
			ds.commit();
			ds.close();
			return null;
		} catch (DriverLoadException e) {
			throw new ExecutionException("Cannot perform the insertion", e);
		} catch (NoSuchTableException e) {
			throw new ExecutionException("Cannot perform the insertion", e);
		} catch (AlreadyClosedException e) {
			throw new ExecutionException("Cannot perform the insertion", e);
		} catch (DataSourceCreationException e) {
			throw new ExecutionException("Cannot perform the insertion", e);
		} catch (DriverException e) {
			throw new ExecutionException("Cannot perform the insertion", e);
		} catch (EvaluationException e) {
			throw new ExecutionException("Cannot evaluate value to insert", e);
		} catch (NonEditableDataSourceException e) {
			throw new ExecutionException("The data source " + sourceName
					+ " is not editable", e);
		}
	}

	public Metadata getResultMetadata() throws DriverException {
		return null;
	}

	public void addField(String fieldName) {
		fields.add(new Field(fieldName));
	}

	public void addAllFields() {
		fields = null;
	}

	public void addFieldValue(Expression value) {
		values.add(value);
	}

	/**
	 * Validates that the number of values to insert is equal to the number of
	 * specified fields. Also checks that the assignment between the types is
	 * possible
	 *
	 * @see org.gdms.sql.strategies.AbstractExpressionOperator#validateExpressionTypes()
	 */
	@Override
	public void validateExpressionTypes() throws SemanticException,
			DriverException {
		if (getFields().size() != values.size()) {
			throw new SemanticException("There are a different "
					+ "number of values and fields");
		}

		for (int i = 0; i < getFields().size(); i++) {
			if (getFields().get(i).getType().getTypeCode() != values.get(i)
					.getType().getTypeCode()) {
				throw new IncompatibleTypesException("The types in the " + i
						+ "th assignment are not the same");
			}
		}
		super.validateExpressionTypes();
	}

	/**
	 * Validates that there is no field reference in the values to assign
	 *
	 * @see org.gdms.sql.strategies.AbstractExpressionOperator#validateFieldReferences()
	 */
	@Override
	public void validateFieldReferences() throws SemanticException,
			DriverException {
		for (Expression value : values) {
			if (value.getFieldReferences().length > 0) {
				throw new SemanticException("Values cannot "
						+ "have field references");
			}
		}
		super.validateFieldReferences();
	}
}
