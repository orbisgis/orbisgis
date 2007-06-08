package org.gdms.data.types;

import org.gdms.data.values.Value;

public class RangeConstraint implements Constraint {
	private String min;
	private String max;

	public RangeConstraint(final String min, final String max) {
		this.min = min;
		this.max = max;
	}

	public ConstraintNames getConstraintName() {
		return ConstraintNames.RANGE;
	}

	public String getConstraintValue() {
		return "[" + min + "... " + max + "]";
	}

	public String check(Value value) {
		return null;
	}
}