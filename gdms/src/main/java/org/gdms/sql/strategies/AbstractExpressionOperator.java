package org.gdms.sql.strategies;

import java.util.ArrayList;

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
				populateWithChildOperatorMetadata(arguments, alias);
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

	protected void populateWithChildOperatorMetadata(
			ArrayList<Expression> expressions, ArrayList<String> aliases)
			throws DriverException {
		if (getOperatorCount() > 0) {
			Metadata met = getOperator(0).getResultMetadata();
			for (int i = 0; i < met.getFieldCount(); i++) {
				expressions.add(new Field(met.getFieldName(i)));
				aliases.add(null);
			}
		}
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

}
