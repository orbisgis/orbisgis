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
import java.util.HashSet;
import java.util.Iterator;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.indexes.ExpressionBasedAlphaQuery;
import org.gdms.data.indexes.ExpressionBasedSpatialIndexQuery;
import org.gdms.data.indexes.IndexQuery;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.source.SourceManager;
import org.gdms.sql.evaluator.ComparisonOperator;
import org.gdms.sql.evaluator.Equals;
import org.gdms.sql.evaluator.Expression;
import org.gdms.sql.evaluator.Field;
import org.gdms.sql.evaluator.FunctionOperator;
import org.gdms.sql.evaluator.GreaterThan;
import org.gdms.sql.evaluator.GreaterThanOrEqual;
import org.gdms.sql.evaluator.LessThan;
import org.gdms.sql.evaluator.LessThanOrEqual;
import org.gdms.sql.evaluator.NotEquals;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionManager;
import org.gdms.sql.function.TwoOverlappingArgumentsFunction;
import org.orbisgis.progress.IProgressMonitor;

public class ScanOperator extends AbstractOperator {

	private String tableName;
	private String tableAlias;
	private DataSourceFactory dsf;
	private Metadata metadata = null;
	private DataSource dataSource;
	private IndexQuery[] queries = new IndexQuery[0];

	public ScanOperator(DataSourceFactory dsf, String tableName,
			String tableAlias) {
		this.tableName = tableName;
		this.tableAlias = tableAlias;
		this.dsf = dsf;
	}

	/**
	 * The result metadata of a scan operator is the metadata of the source it
	 * accesses
	 * 
	 * @see org.gdms.sql.strategies.Operator#getResultMetadata()
	 */
	public Metadata getResultMetadata() throws DriverException {
		if (metadata == null) {
			try {
				DataSource ds = dsf.getDataSource(tableName,
						DataSourceFactory.NORMAL);
				ds.open();
				Metadata metadata = ds.getMetadata();
				this.metadata = new DefaultMetadata(metadata);
				ds.close();
			} catch (DriverLoadException e) {
				throw new DriverException(e);
			} catch (NoSuchTableException e) {
				throw new DriverException(e);
			} catch (DataSourceCreationException e) {
				throw new DriverException(e);
			}
		}

		return metadata;
	}

	public ObjectDriver getResultContents(IProgressMonitor pm)
			throws ExecutionException {
		try {
			ObjectDriver ret = new DataSourceDriver(dataSource);
			if (queries.length > 0) {
				boolean all = true;
				HashSet<Integer> indexSet = new HashSet<Integer>();
				for (IndexQuery query : queries) {
					HashSet<Integer> newIndexSet = new HashSet<Integer>();
					Iterator<Integer> res = dataSource.queryIndex(query);
					while (res.hasNext()) {
						Integer elem = res.next();
						if (indexSet.contains(elem) || all) {
							newIndexSet.add(elem);
						}
					}
					indexSet = newIndexSet;
					all = false;
				}
				ArrayList<Integer> indexes = new ArrayList<Integer>(indexSet
						.size());
				indexes.addAll(indexSet);
				ret = new RowMappedDriver(ret, indexes);
			}
			return ret;
		} catch (DriverException e) {
			throw new ExecutionException("Cannot access "
					+ "the source in the SQL: " + tableName, e);
		}
	}

	public void initialize() throws DriverException {
		super.initialize();
		try {
			dataSource = dsf.getDataSource(tableName, DataSourceFactory.NORMAL);
			dataSource.open();
		} catch (NoSuchTableException e) {
			throw new DriverException("Cannot find the table " + tableName, e);
		} catch (DataSourceCreationException e) {
			throw new DriverException("Cannot access source " + tableName, e);
		}
	}

	@Override
	public void operationFinished() throws DriverException {
		dataSource.close();
	}

	@Override
	public OptimizationInfo getOptimizationInfo() {
		return new ScanOptimizationInfo(this, dataSource);
	}

	@Override
	public String getTableName() {
		return tableName;
	}

	@Override
	public String getTableAlias() {
		return tableAlias;
	}

	/**
	 * Checks the referenced source exists
	 * 
	 * @see org.gdms.sql.strategies.AbstractOperator#validateTableReferences()
	 */
	@Override
	public void validateTableReferences() throws NoSuchTableException,
			SemanticException, DriverException {
		if (!dsf.exists(tableName)) {
			throw new NoSuchTableException(tableName);
		}
		super.validateTableReferences();
	}

	@Override
	public String[] getReferencedTables() {
		return new String[] { tableName };
	}

	public boolean isIndexScan() {
		return (queries != null) && (queries.length > 0);
	}

	@Override
	public void setScanMode(IndexQuery[] indexQueries) {
		queries = indexQueries;
	}

	public static IndexQuery[] getQuery(JoinContext joinContext,
			Field indexField, Expression expression) throws DriverException {
		if (expression instanceof ComparisonOperator) {
			return getAlphaQuery(joinContext, indexField, expression);
		} else if (expression instanceof FunctionOperator) {
			FunctionOperator fop = (FunctionOperator) expression;
			Function fct = FunctionManager.getFunction(fop.getFunctionName());
			if (fct instanceof TwoOverlappingArgumentsFunction) {
				IndexQuery spatialQuery = getSpatialQuery(joinContext,
						indexField, expression);
				if (spatialQuery != null) {
					return new IndexQuery[] { spatialQuery };
				} else {
					return null;
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public static IndexQuery getSpatialQuery(JoinContext joinContext,
			Field indexField, Expression expression) throws DriverException {
		Expression[] fieldAndExpression = getFieldAndExpressionToUseIndex(
				joinContext, indexField, expression);
		if (fieldAndExpression == null) {
			return null;
		} else {
			Field field = (Field) fieldAndExpression[0];
			Expression exp = fieldAndExpression[1];
			return new ExpressionBasedSpatialIndexQuery(exp, field
					.getFieldName());
		}
	}

	public static IndexQuery[] getAlphaQuery(JoinContext joinContext,
			Field indexField, Expression expression) throws DriverException {
		Expression[] fieldAndExpression = getFieldAndExpressionToUseIndex(
				joinContext, indexField, expression);
		if (fieldAndExpression == null) {
			return null;
		} else {
			Field field = (Field) fieldAndExpression[0];
			Expression exp = fieldAndExpression[1];
			if (expression.getClass().equals(Equals.class)) {
				// Return the query only if the values can be orderer
				LessThan l = new LessThan(field, exp);
				try {
					l.validateExpressionTypes();
					return new IndexQuery[] { new ExpressionBasedAlphaQuery(
							field.getFieldName(), exp) };
				} catch (IncompatibleTypesException e) {
					return null;
				}
			} else if (expression.getClass().equals(LessThan.class)) {
				return new IndexQuery[] { new ExpressionBasedAlphaQuery(field
						.getFieldName(), null, true, exp, false) };
			} else if (expression.getClass().equals(LessThanOrEqual.class)) {
				return new IndexQuery[] { new ExpressionBasedAlphaQuery(field
						.getFieldName(), null, true, exp, true) };
			} else if (expression.getClass().equals(NotEquals.class)) {
				return null;
			} else if (expression.getClass().equals(GreaterThan.class)) {
				return new IndexQuery[] { new ExpressionBasedAlphaQuery(field
						.getFieldName(), exp, false, null, true) };
			} else if (expression.getClass().equals(GreaterThanOrEqual.class)) {
				return new IndexQuery[] { new ExpressionBasedAlphaQuery(field
						.getFieldName(), exp, true, null, true) };
			} else {
				throw new RuntimeException("this comparison "
						+ "type is not considered by indexes");
			}
		}
	}

	public static Expression[] getFieldAndExpressionToUseIndex(
			JoinContext joinContext, Field indexField, Expression expression)
			throws DriverException {
		Field field;
		int expressionChild;
		if ((expression.getChild(0) instanceof Field)
				&& (((Field) expression.getChild(0)).equals(indexField))) {
			field = (Field) expression.getChild(0);
			expressionChild = 1;
		} else if ((expression.getChild(1) instanceof Field)
				&& (((Field) expression.getChild(1)).equals(indexField))) {
			field = (Field) expression.getChild(1);
			expressionChild = 0;
		} else {
			return null;
		}
		Expression exp = expression.getChild(expressionChild);

		if (exp.isLiteral() || joinContext.isEvaluable(exp)) {
			return new Expression[] { field, exp };
		} else {
			return null;
		}
	}

	public int passFieldUp(Field field) throws DriverException {
		Metadata metadata = getResultMetadata();
		for (int i = 0; i < metadata.getFieldCount(); i++) {
			if (metadata.getFieldName(i).equals(field.getFieldName())
					&& referencesThisTable(field)) {
				return i;
			}
		}

		return -1;
	}

	public boolean referencesThisTable(Field field) {
		if (field.getTableName() == null) {
			return true;
		} else {
			if ((field.getTableName().equals(getTableAlias()))
					|| (field.getTableName().equals(getTableName()))) {
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public String toString() {
		return ScanOperator.class.getSimpleName() + "(" + tableName + ","
				+ tableAlias + ")";
	}

	@Override
	public String getFieldSource(SourceManager sm, Field field) {
		try {
			if (passFieldUp(field) != -1) {
				return sm.getMainNameFor(tableName);
			} else {
				return null;
			}
		} catch (SemanticException e) {
			return null;
		} catch (DriverException e) {
			return null;
		}
	}
}
