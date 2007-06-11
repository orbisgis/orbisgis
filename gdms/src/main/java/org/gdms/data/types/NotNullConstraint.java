package org.gdms.data.types;

public class NotNullConstraint extends AbstractBooleanConstraint {

	@Override
	public ConstraintNames getConstraintName() {
		return ConstraintNames.NOT_NULL;
	}
}