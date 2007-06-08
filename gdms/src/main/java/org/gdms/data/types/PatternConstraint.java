package org.gdms.data.types;

import org.gdms.data.values.Value;

public class PatternConstraint implements Constraint {
	private String constraintValue;

	public PatternConstraint(final String constraintValue) {
		this.constraintValue = constraintValue;
	}

	public ConstraintNames getConstraintName() {
		return ConstraintNames.PATTERN;
	}

	public String getConstraintValue() {
		return constraintValue;
	}

	public String check(Value value) {
		return null;
	}
}