package org.gdms.data.types;

import org.gdms.data.values.Value;

public class DefaultStringConstraint extends AbstractConstraint {

	private String defaultValue;

	public DefaultStringConstraint(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public DefaultStringConstraint(byte[] defaultValue) {
		this(new String(defaultValue));
	}

	public String check(Value value) {
		if (!value.isNull() && (!value.getAsString().equals(defaultValue))) {
			return "Default string value is " + defaultValue;
		}
		return null;
	}

	public byte[] getBytes() {
		return defaultValue.getBytes();
	}

	public int getConstraintCode() {
		return Constraint.DEFAULT_STRING_VALUE;
	}

	public String getConstraintValue() {
		return defaultValue;
	}

	public int getType() {
		return CONSTRAINT_TYPE_STRING_LITERAL;
	}

}
