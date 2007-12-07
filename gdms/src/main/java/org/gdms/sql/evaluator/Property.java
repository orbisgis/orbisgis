package org.gdms.sql.evaluator;

import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.instruction.IncompatibleTypesException;

public class Property extends Operand {

	private String propertyName;
	private EvaluationContext ec;

	public Property(String propertyName) {
		this.propertyName = propertyName;
	}

	public Value evaluate() throws IncompatibleTypesException, DriverException {
		return ec.getDataSource().getFieldValue(ec.getRowIndex(),
				ec.getDataSource().getFieldIndexByName(propertyName));
	}

	@Override
	public void setEvaluationContext(EvaluationContext ec) {
		this.ec = ec;
	}
}
