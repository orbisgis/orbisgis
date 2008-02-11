package org.gdms.sql.strategies;

import java.util.ArrayList;

import org.gdms.data.ExecutionException;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.sql.evaluator.Expression;
import org.gdms.sql.evaluator.Field;

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

	public ObjectDriver getResultContents() throws ExecutionException {
		return null;
	}

	public Metadata getResultMetadata() throws DriverException {
		return new DefaultMetadata();
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
