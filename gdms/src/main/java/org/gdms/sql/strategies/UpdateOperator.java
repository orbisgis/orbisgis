package org.gdms.sql.strategies;

import java.util.ArrayList;

import org.gdms.data.ExecutionException;
import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.sql.evaluator.Equals;
import org.gdms.sql.evaluator.Expression;
import org.gdms.sql.evaluator.Field;
import org.orbisgis.IProgressMonitor;

public class UpdateOperator extends AbstractExpressionOperator implements
		Operator {

	private ArrayList<Field> fields = new ArrayList<Field>();
	private ArrayList<Expression> values = new ArrayList<Expression>();

	@Override
	protected Expression[] getExpressions() throws DriverException,
			SemanticException {
		ArrayList<Expression> ret = new ArrayList<Expression>();
		ret.addAll(fields);
		ret.addAll(values);

		return ret.toArray(new Expression[0]);
	}

	public ObjectDriver getResultContents(IProgressMonitor pm) throws ExecutionException {
		// TODO Auto-generated method stub
		return null;
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

}
