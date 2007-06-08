package org.gdms.data.types;

import org.gdms.data.values.Value;

public class ScaleConstraint implements Constraint {
	private int constraintValue;

	public ScaleConstraint(final int constraintValue) {
		this.constraintValue = constraintValue;
	}

	public ConstraintNames getConstraintName() {
		return ConstraintNames.SCALE;
	}

	public String getConstraintValue() {
		return Integer.toString(constraintValue);
	}

	public String check(Value value) {
		return null;
	}
}