package org.gdms.data.types;

import org.gdms.data.values.Value;

public class LengthConstraint implements Constraint {
	private int constraintValue;

	public LengthConstraint(final int constraintValue) {
		this.constraintValue = constraintValue;
	}

	public ConstraintNames getConstraintName() {
		return ConstraintNames.LENGTH;
	}

	public String getConstraintValue() {
		return Integer.toString(constraintValue);
	}

	public String check(Value value) {
		return null;
	}
}