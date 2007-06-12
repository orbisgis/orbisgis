package org.gdms.data.types;

import org.gdms.data.values.Value;

public class MinConstraint extends AbstractConstraint {
	private String constraintValue;

	public MinConstraint(final String constraintValue) {
		this.constraintValue = constraintValue;
	}

	public ConstraintNames getConstraintName() {
		return ConstraintNames.MIN;
	}

	public String getConstraintValue() {
		return constraintValue;
	}

	public String check(Value value) {
		return null;
	}
}