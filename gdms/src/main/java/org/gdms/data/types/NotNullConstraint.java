package org.gdms.data.types;

public class NotNullConstraint extends AbstractBooleanConstraint {
	public ConstraintNames getConstraintName() {
		return ConstraintNames.NOT_NULL;
	}
}