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
import java.util.Comparator;
import java.util.TreeSet;

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
import org.orbisgis.utils.I18N;

public class DeleteOperator extends AbstractExpressionOperator implements
		Operator {

	private Expression[] expressions = new Expression[0];

	@Override
	protected Expression[] getExpressions() {
		return expressions;
	}

	public void setExpressions(Expression... expressions) {
		this.expressions = expressions;
	}

	public ObjectDriver getResultContents(IProgressMonitor pm)
			throws ExecutionException {
		ObjectDriver source = getOperator(0).getResult(pm);
		try {
			Field[] fieldReferences = getFieldReferences();
			DefaultFieldContext selectionFieldContext = new DefaultFieldContext(
					source);
			for (Field field : fieldReferences) {
				field.setFieldContext(selectionFieldContext);
			}

			ArrayList<Integer> indexes = new ArrayList<Integer>();
			for (int i = 0; i < source.getRowCount(); i++) {
				selectionFieldContext.setIndex(i);
				if ((expressions == null) || evaluatesToTrue(expressions, pm)) {
					indexes.add(i);
				}
			}

			TreeSet<Integer> sorted = new TreeSet<Integer>(
					new Comparator<Integer>() {

						@Override
						public int compare(Integer o1, Integer o2) {
							return o2.compareTo(o1);
						}
					});
			sorted.addAll(indexes);

			String sourceName = getTableName();
			final DataSource ds = getDataSourceFactory().getDataSource(
					sourceName);

			ds.open();
			for (Integer integer : sorted) {
				ds.deleteRow(integer);
			}
			ds.commit();

			ds.close();

		} catch (IncompatibleTypesException e) {
			throw new ExecutionException(I18N.getString("Cannot filter table"), e);
		} catch (EvaluationException e) {
			throw new ExecutionException(I18N.getString("Cannot filter table"), e);
		} catch (DriverException e) {
			throw new ExecutionException(I18N.getString("Cannot filter table"), e);
		} catch (DriverLoadException e) {
			throw new ExecutionException(I18N.getString("Cannot delete rows"), e);
		} catch (NoSuchTableException e) {
			throw new ExecutionException(I18N.getString("Table not found"), e);
		} catch (DataSourceCreationException e) {
			throw new ExecutionException(I18N.getString("Cannot access table"), e);
		} catch (NonEditableDataSourceException e) {
			throw new ExecutionException(I18N
					.getString("The source is not editable"), e);
		} catch (SemanticException e) {
			throw new ExecutionException("", e);
		}

		return null;
	}

	public Metadata getResultMetadata() throws DriverException {
		return null;
	}

	private boolean evaluatesToTrue(Expression[] exprs, IProgressMonitor pm)
			throws EvaluationException {
		for (Expression expression : exprs) {
			if (!evaluatesToTrue(expression, pm)) {
				return false;
			}
		}

		return true;
	}

	private static boolean evaluatesToTrue(Expression expression,
			IProgressMonitor pm) throws IncompatibleTypesException,
			EvaluationException {
		Value expressionResult = expression.evaluate(pm);
		return !expressionResult.isNull() && expressionResult.getAsBoolean();
	}

}
