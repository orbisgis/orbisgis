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
import java.util.HashMap;
import java.util.HashSet;

import org.gdms.data.ExecutionException;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.sql.customQuery.QueryManager;
import org.gdms.sql.evaluator.Expression;
import org.gdms.sql.evaluator.Field;
import org.gdms.sql.evaluator.FieldContext;
import org.gdms.sql.evaluator.FunctionOperator;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionManager;
import org.orbisgis.progress.IProgressMonitor;

public class ProjectionOp extends AbstractExpressionOperator implements
		Operator, SelectionTransporter {

	private ArrayList<SelectElement> selectElements;
	private String[] aliasList;
	private Expression[] expressionList;
	private ArrayList<Integer> internalFields = new ArrayList<Integer>();
	private ArrayList<Field> groupByFields = new ArrayList<Field>();
	private boolean distinct;

	public ProjectionOp() {
		selectElements = new ArrayList<SelectElement>();
		aliasList = null;
		expressionList = null;
	}

	public ObjectDriver getResultContents(IProgressMonitor pm)
			throws ExecutionException {
		ObjectDriver ret = null;
		try {
			if (onlyStar()) {
				ret = getOperator(0).getResult(pm);
			} else {
				ret = new ProjectionDriver(getOperator(0).getResult(pm),
						getExpressions(), getResultMetadata());
			}
		} catch (DriverException e) {
			throw new ExecutionException(
					"Cannot obtain the metadata of the result", e);
		} catch (SemanticException e) {
			throw new RuntimeException("The preprocessor has failed", e);
		}

		if (distinct) {
			ArrayList<Integer> indexes = new ArrayList<Integer>();
			HashSet<Value> set = new HashSet<Value>();

			try {
				pm.startTask("distinct...");
				for (int i = 0; i < ret.getRowCount(); i++) {
					if (i / 1000 == i / 1000.0) {
						if (pm.isCancelled()) {
							return null;
						} else {
							pm.progressTo((int) (100 * i / ret.getRowCount()));
						}
					}
					Value[] row = new Value[ret.getMetadata().getFieldCount()];
					for (int j = 0; j < row.length; j++) {
						row[j] = ret.getFieldValue(i, j);
					}
					ValueCollection rowValue = ValueFactory.createValue(row);

					if (!set.contains(rowValue)) {
						indexes.add(i);
						set.add(rowValue);
					}
				}
				pm.endTask();
				ret = new RowMappedDriver(ret, indexes);
			} catch (DriverException e) {
				throw new ExecutionException("Cannot perform"
						+ " distinct operation", e);
			}
		}

		return ret;
	}

	private boolean onlyStar() throws DriverException, SemanticException {
		return ((getExpressions().length == 1) && (getExpressions()[0] instanceof StarElement));
	}

	private String[] getAliases() throws DriverException, SemanticException {
		if (aliasList == null) {
			expandStars();
		}

		return aliasList;
	}

	private void expandStars() throws DriverException, SemanticException {
		// Replace the select elements by Expression instances
		ArrayList<Expression> expressions = new ArrayList<Expression>();
		ArrayList<String> aliases = new ArrayList<String>();
		HashMap<Expression, SelectElement> expressionSelectElement = new HashMap<Expression, SelectElement>();
		for (SelectElement element : selectElements) {
			if (element instanceof StarElement) {
				expandStar(expressions, expressionSelectElement, aliases, null,
						(AbstractStarElement) element);
			} else if (element instanceof TableStarElement) {
				String tableName = ((TableStarElement) element).tableName;
				expandStar(expressions, expressionSelectElement, aliases,
						tableName, (AbstractStarElement) element);
			} else if (element instanceof ExpressionElement) {
				ExpressionElement expressionElement = (ExpressionElement) element;
				expressions.add(expressionElement.expr);
				aliases.add(expressionElement.alias);
			} else {
				throw new RuntimeException("bug");
			}
		}

		aliasList = aliases.toArray(new String[0]);
		expressionList = expressions.toArray(new Expression[0]);

	}

	protected Expression[] getExpressions() throws DriverException,
			SemanticException {
		if (expressionList == null) {
			expandStars();
		}

		return expressionList;
	}

	public void addExpr(Expression expression, String alias) {
		selectElements.add(new ExpressionElement(expression, alias));
	}

	public void addStar() {
		selectElements.add(new StarElement());
	}

	public void addStarOf(String tableName) {
		selectElements.add(new TableStarElement(tableName));
	}

	/**
	 * The metadata returned by a projection operator consists of one field for
	 * each expression in the select clause. The only exception to this is when
	 * there is some aggregated functions they are removed and managed by a
	 * groupby operator further down. If the expression contains an alias, the
	 * name of the field is that alias. Otherwise, if the expression is just a
	 * field reference its name is used. Finally, if a complex expression is
	 * found and there is no alias, the string 'unknown' concatenated with the
	 * index of the field in the resulting metadata is used
	 *
	 * @see org.gdms.sql.strategies.Operator#getResultMetadata()
	 */
	public Metadata getResultMetadata() throws DriverException {
		DefaultMetadata ret = new DefaultMetadata();
		Expression[] expressions;
		try {
			expressions = getExpressions();
			for (int i = 0; i < expressions.length; i++) {
				String name = getFieldName(i);
				Type type = getFieldType(expressions[i]);
				try {
					ret.addField(name, type.getTypeCode(), type
							.getConstraints());
				} catch (InvalidTypeException e) {
					throw new RuntimeException("Bug: Invalid type "
							+ "not detected by preprocessor", e);
				}
			}
		} catch (SemanticException e1) {
			throw new RuntimeException("The preprocessor has failed", e1);
		}

		return ret;
	}

	private Type getFieldType(Expression expression) throws DriverException {
		return expression.getType();
	}

	private String getFieldName(int index) throws DriverException,
			SemanticException {
		String alias = getAliases()[index];
		if (alias != null) {
			return getAliases()[index];
		} else {
			Expression expr = getExpressions()[index];
			if (expr instanceof Field) {
				return ((Field) expr).getFieldName();
			} else {
				return "unknown" + index;
			}
		}
	}

	public static interface SelectElement {
	}

	private class ExpressionElement implements SelectElement {
		Expression expr;
		String alias;

		public ExpressionElement(Expression expr, String alias) {
			super();
			this.expr = expr;
			this.alias = alias;
		}
	}

	public static abstract class AbstractStarElement implements SelectElement {
		ArrayList<String> except = new ArrayList<String>();
		String prefix = null;
	}

	public static class TableStarElement extends AbstractStarElement implements
			SelectElement {
		String tableName;

		public TableStarElement(String tableName) {
			super();
			this.tableName = tableName;
		}
	}

	public static class StarElement extends AbstractStarElement implements
			SelectElement {
	}

	public boolean isAggregated() {
		try {
			Expression[] exprs = getExpressions();
			for (Expression expression : exprs) {
				FunctionOperator[] functions = expression
						.getFunctionReferences();
				for (FunctionOperator functionOperator : functions) {
					if (isAggregated(functionOperator)) {
						return true;
					}
				}
			}
		} catch (DriverException e) {
		} catch (SemanticException e) {
		}

		return false;
	}

	private boolean isAggregated(FunctionOperator functionOperator) {
		String functionName = functionOperator.getFunctionName();
		Function fcn = FunctionManager.getFunction(functionName);
		return (fcn != null) && fcn.isAggregate();
	}

	public Operator removeOperator(int i) {
		return children.remove(i);
	}

	/**
	 * This method helps in the movement of aggregate functions to a groupby
	 * operator further down. It changes the expressions that consist of a
	 * aggregate function into a reference to a field of the groupby result.
	 * Returns the aggregated expressions to be added in the groupby operator
	 * further down. This method assumes the instruction contains at least an
	 * aggregated function or a group by clause
	 *
	 * @param groupByFields
	 *            The names used in the group by clause
	 * @return
	 * @throws DriverException
	 *             Error accessing data
	 * @throws SemanticException
	 *             Semantic error expanding '*'
	 */
	public Expression[] transformExpressionsInGroupByReferences()
			throws DriverException, SemanticException {
		Expression[] exprs = getExpressions();

		// We build the array of functions in this operator
		int functionStartIndex = groupByFields.size();
		ArrayList<Expression> ret = new ArrayList<Expression>();
		for (int i = 0; i < exprs.length; i++) {
			Expression expression = exprs[i];
			ArrayList<FunctionOperator> aggregatedFunctions = getAggregatedFunctions(expression);
			for (FunctionOperator functionOperator : aggregatedFunctions) {
				ret.add(functionOperator);
				// Substitute the functions by field references to the group by
				int groupByIndex = functionStartIndex;
				functionStartIndex++;

				Field groupByField = new Field(GroupByOperator.FIELD_PREFIX
						+ groupByIndex);
				groupByFields.add(groupByField);
				// do the replacement
				if (expression == functionOperator) {
					exprs[i] = groupByField;
					if (aliasList[i] == null) {
						aliasList[i] = "unknown" + i;
					}
				} else {
					expression.replace(functionOperator, groupByField);
				}
			}
		}

		return ret.toArray(new Expression[0]);
	}

	private ArrayList<FunctionOperator> getAggregatedFunctions(
			Expression expression) {
		boolean aggregated = false;
		ArrayList<FunctionOperator> ret = new ArrayList<FunctionOperator>();
		if (expression instanceof FunctionOperator) {
			FunctionOperator fnc = (FunctionOperator) expression;
			if (isAggregated(fnc)) {
				aggregated = true;
				ret.add(fnc);
			}
		}
		if (!aggregated) {
			for (int i = 0; i < expression.getChildCount(); i++) {
				ret.addAll(getAggregatedFunctions(expression.getChild(i)));
			}
		}

		return ret;
	}

	/**
	 * @return true if the instruction is a custom query and false if it is not
	 *         or it cannot be stated yet
	 */
	public boolean isCustomQuery() {
		try {
			Expression[] exprs = getExpressions();
			boolean custom = false;
			for (Expression expression : exprs) {
				FunctionOperator[] functions = expression
						.getFunctionReferences();
				for (FunctionOperator functionOperator : functions) {
					String functionName = functionOperator.getFunctionName();
					if (QueryManager.getQuery(functionName) != null) {
						custom = true;
					}
				}
			}

			if (custom) {
				if (exprs.length > 1) {
					throw new SemanticException("Custom queries "
							+ "cannot have more than one "
							+ "expression in the select clause");
				}
				return true;
			} else {
				return false;
			}
		} catch (DriverException e) {
		} catch (SemanticException e) {
		}
		return false;
	}

	public int passFieldUp(Field field) throws DriverException {
		try {
			Expression[] exprs = getExpressions();

			int fieldIndex = -1;
			if (field.getTableName() == null) {
				for (int i = 0; i < exprs.length; i++) {
					if ((aliasList[i] != null)
							&& aliasList[i].equals(field.getFieldName())) {
						fieldIndex = returnIIfNotAmbiguous(field, fieldIndex, i);
					} else {
						if (exprs[i] instanceof Field) {
							Field expr = (Field) exprs[i];
							if (field.getFieldName()
									.equals(expr.getFieldName())) {
								fieldIndex = returnIIfNotAmbiguous(field,
										fieldIndex, i);
							}
						}
					}
				}
			}

			if (fieldIndex != -1) {
				return fieldIndex;
			} else {
				// The dependency will be satisfied further down
				fieldIndex = getOperator(0).passFieldUp(field);

				if (fieldIndex != -1) {
					// If the field is not in the selection add it
					boolean exists = false;
					for (int i = 0; i < exprs.length; i++) {
						Expression expression = exprs[i];
						if (expression instanceof Field) {
							Field selectedField = (Field) expression;
							if (selectedField.getFieldIndex() == fieldIndex) {
								exists = true;
								fieldIndex = i;
							}
						}
					}
					if (!exists) {
						Field newField = new Field(field.getTableName(), field
								.getFieldName());
						newField.setFieldIndex(fieldIndex);
						addField(newField);
						int indexInProjection = getExpressions().length - 1;
						internalFields.add(indexInProjection);
						fieldIndex = indexInProjection;
					}
				}
				return fieldIndex;
			}
		} catch (SemanticException e) {
			return -1;
		}
	}

	private int returnIIfNotAmbiguous(Field field, int fieldIndex, int i)
			throws SemanticException {
		if (fieldIndex == -1) {
			fieldIndex = i;
		} else {
			throw new SemanticException("Ambiguous field reference: "
					+ field.toString());
		}
		return fieldIndex;
	}

	@Override
	public int[] getInternalFields() {
		int[] ret = new int[internalFields.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = internalFields.get(i);
		}
		return ret;
	}

	public void setGroupByFieldNames(ArrayList<Field> arrayList) {
		this.groupByFields = arrayList;
	}

	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	/**
	 * Adds an internal field. This field will be used by
	 *
	 * @param newField
	 * @return
	 */
	private int addField(Field newField) {
		newField.setFieldContext(new FieldContext() {

			public Value getFieldValue(int fieldId) throws DriverException {
				throw new UnsupportedOperationException("Error");
			}

			public Type getFieldType(int fieldId) throws DriverException {
				return getOperator(0).getResultMetadata().getFieldType(fieldId);
			}

		});
		ArrayList<Expression> newExprs = new ArrayList<Expression>();
		ArrayList<String> newAliases = new ArrayList<String>();
		for (int i = 0; i < expressionList.length; i++) {
			newExprs.add(expressionList[i]);
			newAliases.add(aliasList[i]);
		}
		newExprs.add(newField);
		newAliases.add(newField.getFieldName());
		expressionList = newExprs.toArray(new Expression[0]);
		aliasList = newAliases.toArray(new String[0]);

		return expressionList.length - 1;
	}

	public void transportSelection(SelectionOp op) {
		throw new UnsupportedOperationException("Nested "
				+ "instructions not yet allowed");
	}

	public void addSelectElement(SelectElement elem) {
		selectElements.add(elem);
	}

}
