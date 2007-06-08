package org.gdms.data.types;

public class ReadOnlyConstraint extends AbstractBooleanConstraint {
	public ConstraintNames getConstraintName() {
		return ConstraintNames.READONLY;
	}
}