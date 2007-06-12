package org.gdms.data.types;

import org.gdms.data.values.Value;

public abstract class AbstractBooleanConstraint extends AbstractConstraint {
	public String check(Value value) {
		return null;
	}

	public abstract ConstraintNames getConstraintName();

	final public String getConstraintValue() {
		return "true";
	}
}