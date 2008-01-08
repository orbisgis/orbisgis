package org.gdms.sql.evaluator;

import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.instruction.IncompatibleTypesException;

public class Sum extends Operator {

	public Sum(Expression left, Expression right) {
		super(left, right);
	}

	public Value evaluate() throws IncompatibleTypesException, DriverException {
		return getLeftOperator().evaluate().suma(getRightOperator().evaluate());
	}

	public int getType() throws DriverException {
		// TODO Auto-generated method stub
		return 0;
	}

}
