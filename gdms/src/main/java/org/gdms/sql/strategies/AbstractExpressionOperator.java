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

import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.source.SourceManager;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.customQuery.QueryManager;
import org.gdms.sql.evaluator.Expression;
import org.gdms.sql.evaluator.Field;
import org.gdms.sql.evaluator.FieldContext;
import org.gdms.sql.evaluator.FunctionOperator;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionManager;

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
			// Look the first operator that changes the metadata for the field
			// references
			int fieldIndex = -1;
			Operator prod = this;
			while (fieldIndex == -1) {
				prod = prod.getOperator(0);
				if (prod instanceof ChangesMetadata) {
					fieldIndex = ((ChangesMetadata) prod).getFieldIndex(field);
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
			ArrayList<String> aliases, String tableName)
			throws DriverException, TableNotFoundException {
		boolean tableFound = false;
		Operator[] products = getOperators(this, new OperatorFilter() {

			public boolean accept(Operator op) {
				return op instanceof ScalarProductOp;
			}

		});
		for (Operator operator : products) {
			ScalarProductOp op = (ScalarProductOp) operator;
			String[] tableNames;
			if (tableName == null) {
				tableNames = op.getReferencedTables();
			} else {
				tableNames = new String[] { tableName };
			}
			for (String table : tableNames) {
				try {
					Metadata m = op.getMetadata(table);
					if (m != null) {
						tableFound = true;
						for (int i = 0; i < m.getFieldCount(); i++) {
							expressions
									.add(new Field(table, m.getFieldName(i)));
							aliases.add(null);
						}
					}
				} catch (SemanticException e) {
					throw new RuntimeException(
							"The semantics should be validated "
									+ "before this method is called");
				}
			}
		}
		if (!tableFound) {
			throw new TableNotFoundException(tableName + " not found");
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

		// Expand '*' in all function references
		for (Expression expression : getExpressions()) {
			FunctionOperator[] functionReferences = expression
					.getFunctionReferences();
			for (FunctionOperator function : functionReferences) {
				ArrayList<Expression> arguments = new ArrayList<Expression>();
				ArrayList<String> alias = new ArrayList<String>();
				expandStar(arguments, alias, null);
				for (Expression expr : arguments) {
					Field[] fields = expr.getFieldReferences();
					for (Field field : fields) {
						field.setFieldContext(fieldContext);
					}
				}
				function.replaceStarBy(arguments.toArray(new Expression[0]));
			}
		}

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
						throw new SemanticException("The function "
								+ functionName + " does not exist");
					}
				}
			}

		}
		super.validateFunctionReferences();
	}

	@Override
	public void resolveFieldSourceReferences(SourceManager sm)
			throws DriverException, SemanticException {
		super.resolveFieldSourceReferences(sm);

		Field[] fields = getFieldReferences();
		for (Field field : fields) {
			String sourceName = null;
			Operator prod = this;
			while ((sourceName == null) && (prod.getOperatorCount() > 0)) {
				prod = prod.getOperator(0);
				if (prod instanceof ScalarProductOp) {
					sourceName = ((ScalarProductOp) prod).getSourceName(field);
				}
			}
			if (sourceName != null) {
				field.setSourceName(sm.getMainNameFor(sourceName));
			}
		}
	}

}
