package org.gdms.data.types;

import org.gdms.data.values.Value;

public class PrecisionConstraint implements Constraint {
	private int constraintValue;

	public PrecisionConstraint(final int constraintValue) {
		this.constraintValue = constraintValue;
	}

	public ConstraintNames getConstraintName() {
		return ConstraintNames.PRECISION;
	}

	public String getConstraintValue() {
		return Integer.toString(constraintValue);
	}

	public String check(Value value) {
		return null;
	}
}