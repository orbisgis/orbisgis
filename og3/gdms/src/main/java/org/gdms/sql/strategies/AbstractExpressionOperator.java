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

import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.customQuery.QueryManager;
import org.gdms.sql.evaluator.Expression;
import org.gdms.sql.evaluator.Field;
import org.gdms.sql.evaluator.FieldContext;
import org.gdms.sql.evaluator.FunctionOperator;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionManager;
import org.gdms.sql.strategies.ProjectionOp.AbstractStarElement;
import org.gdms.sql.strategies.ProjectionOp.SelectElement;

public abstract class AbstractExpressionOperator extends AbstractOperator {

	protected abstract Expression[] getExpressions() throws DriverException,
			SemanticException;

	protected Field[] getFieldReferences() throws DriverException,
			SemanticException {
		ArrayList<Field> ret = new ArrayList<Field>();
		for (Expression expression : getExpressions()) {
			Field[] fieldReferences = expression.getFieldReferences();
			for (Field field : fieldReferences) {
				ret.add(field);
			}
		}

		return ret.toArray(new Field[0]);
	}

	/**
	 * Resolves the field references setting the index in the metadata of the
	 * nearest child operator that implements ChangesMetadata
	 *
	 * @see org.gdms.sql.strategies.AbstractOperator#validateFieldReferences()
	 */
	public void validateFieldReferences() throws SemanticException,
			DriverException {
		super.validateFieldReferences();

		Field[] fieldReferences = getFieldReferences();
		for (Field field : fieldReferences) {
			// One and only one operator should resolve this field reference
			int fieldIndex = -1;
			for (int i = 0; i < getOperatorCount(); i++) {
				Operator child = getOperator(i);
				int childFieldIndex = child.passFieldUp(field);
				if (childFieldIndex != -1) {
					if (fieldIndex == -1) {
						fieldIndex = childFieldIndex;
					} else {
						throw new SemanticException(
								"Ambiguous field reference: " + field);
					}
				}
			}

			if (fieldIndex == -1) {
				throw new SemanticException("Field not found: "
						+ field.toString());
			} else {
				field.setFieldIndex(fieldIndex);
			}
		}
	}

	protected void expandStar(ArrayList<Expression> expressions,
			HashMap<Expression, SelectElement> expressionSelectElement,
			ArrayList<String> aliases, String tableName,
			AbstractStarElement star)
			throws DriverException, SemanticException {
		String[] tableNames;
		if (tableName == null) {
			tableNames = getReferencedTables();
		} else {
			tableNames = new String[] { tableName };
		}

		boolean[] exceptUsed = new boolean[star.except.size()];
		for (String table : tableNames) {
			Metadata m = getBranchMetadata(table);
			if (m != null) {
				for (int i = 0; i < m.getFieldCount(); i++) {
					String fieldName = m.getFieldName(i);
					if (star.except.contains(fieldName)) {
						int exceptFieldIndex = star.except
								.indexOf(fieldName);
						if (exceptUsed[exceptFieldIndex]) {
							throw new SemanticException(
									"Ambiguous excluded field: "
											+ fieldName);
						}
						exceptUsed[exceptFieldIndex] = true;
					} else {
						Field field = new Field(table, fieldName);
						expressions.add(field);
						expressionSelectElement.put(field, star);
						if (star.prefix != null) {
							aliases.add(star.prefix + fieldName);
						} else {
							aliases.add(fieldName);
						}
					}
				}
				for (int i = 0; i < exceptUsed.length; i++) {
					if (!exceptUsed[i]) {
						throw new SemanticException("Exception field '"
								+ star.except.get(i) + "' does not exist");
					}
				}
			} else {
				throw new SemanticException("Table reference not found: "
						+ table);
			}
		}
	}

	/**
	 * Sets the field context for all the field references and expands the '*'
	 * in functions
	 *
	 * @see org.gdms.sql.strategies.AbstractOperator#prepareValidation()
	 */
	public void prepareValidation() throws DriverException, SemanticException {
		super.prepareValidation();

		// Set the field context in all field references
		FieldContext fieldContext = new FieldContext() {

			public Value getFieldValue(int fieldId) throws DriverException {
				throw new UnsupportedOperationException("Error");
			}

			public Type getFieldType(int fieldId) throws DriverException {
				return getOperator(0).getResultMetadata().getFieldType(fieldId);
			}

		};
		Field[] fieldReferences = getFieldReferences();
		for (Field field : fieldReferences) {
			field.setFieldContext(fieldContext);
		}

		/*// Expand '*' in all function references
		for (Expression expression : getExpressions()) {
			FunctionOperator[] functionReferences = expression
					.getFunctionReferences();
			for (FunctionOperator function : functionReferences) {
				if (function.hasStar()) {
					ArrayList<Expression> arguments = new ArrayList<Expression>();
					ArrayList<String> alias = new ArrayList<String>();
					expandStar(arguments, alias, null);
					for (Expression expr : arguments) {
						Field[] fields = expr.getFieldReferences();
						for (Field field : fields) {
							field.setFieldContext(fieldContext);
						}
					}
					function
							.replaceStarBy(arguments.toArray(new Expression[0]));
				}
			}
		}*/

	}

	/**
	 * Validates the types of the expressions in the operator
	 *
	 * @see org.gdms.sql.strategies.AbstractOperator#validateExpressionTypes()
	 */
	@Override
	public void validateExpressionTypes() throws SemanticException,
			DriverException {
		Expression[] exps = getExpressions();
		for (Expression expression : exps) {
			expression.validateTypes();
		}
		super.validateExpressionTypes();
	}

	/**
	 * Checks that the functions exist
	 *
	 * @see org.gdms.sql.strategies.AbstractOperator#validateFunctionReferences()
	 */
	@Override
	public void validateFunctionReferences() throws DriverException,
			SemanticException {
		for (Expression expression : getExpressions()) {
			FunctionOperator[] functionReferences = expression
					.getFunctionReferences();
			for (FunctionOperator function : functionReferences) {
				String functionName = function.getFunctionName();
				Function fnc = FunctionManager.getFunction(functionName);
				if (fnc == null) {
					CustomQuery query = QueryManager.getQuery(functionName);
					if (query == null) {
						throw new SemanticException("Function '" + functionName
								+ "' not found");
					}
				}
			}

		}
		super.validateFunctionReferences();
	}

}
