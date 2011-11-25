package org.gdms.sql.evaluator;

import org.gdms.data.ExecutionException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.gdms.sql.strategies.Operator;
import org.orbisgis.progress.ProgressMonitor;

public class ExistsOperator extends BooleanOperator {

	private org.gdms.sql.strategies.Operator select;

	public ExistsOperator(org.gdms.sql.strategies.Operator select) {
		this.select = select;
	}

	@Override
	protected Value evaluateExpression(ProgressMonitor pm) throws EvaluationException,
			IncompatibleTypesException {
		try {
			ObjectDriver res = select.getResult(pm);
			long count = res.getRowCount();

			if (count > 0) {
				return ValueFactory.createValue(true);
			}
			return ValueFactory.createValue(false);
		} catch (ExecutionException e) {
			throw new EvaluationException("Cannot evaluate subquery", e);
		} catch (DriverException e) {
			throw new EvaluationException("Cannot evaluate subquery", e);
		}
	}

	@Override
	public void validateExpressionTypes() throws IncompatibleTypesException,
			DriverException {
		// always valid
	}
	
	@Override
	public Expression cloneExpression() {
		return new ExistsOperator(select);

	}

	@Override
	public Type getType() throws DriverException, IncompatibleTypesException {
		return TypeFactory.createType(Type.BOOLEAN);
	}

	@Override
	public Operator[] getSubqueries() {
		return new Operator[] { select };
	}

}
