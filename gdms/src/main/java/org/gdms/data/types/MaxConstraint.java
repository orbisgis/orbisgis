package org.gdms.data.types;

import org.gdms.data.values.Value;

public class MaxConstraint extends AbstractConstraint {
	private String constraintValue;

	public MaxConstraint(final String constraintValue) {
		this.constraintValue = constraintValue;
	}

	public ConstraintNames getConstraintName() {
		return ConstraintNames.MAX;
	}

	public String getConstraintValue() {
		return constraintValue;
	}

	public String check(Value value) {
		return null;
	}
}