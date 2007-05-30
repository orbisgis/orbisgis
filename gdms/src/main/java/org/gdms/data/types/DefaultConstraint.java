package org.gdms.data.types;

public class DefaultConstraint implements Constraint {
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

	public boolean check() {
		return true;
	}
}