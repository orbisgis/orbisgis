package org.gdms.data.types;

import org.gdms.data.values.Value;

public class DefaultConstraint extends AbstractConstraint {
	private ConstraintNames constraintName;

	private String constraintValue;

	public DefaultConstraint(final ConstraintNames constraintName,
			final String constraintValue) {
		this.constraintName = constraintName;
		this.constraintValue = constraintValue;
	}

	public ConstraintNames getConstraintName() {
		return constraintName;
	}

	public String getConstraintValue() {
		return constraintValue;
	}

	public String check(Value value) {
		return null;
	}
}