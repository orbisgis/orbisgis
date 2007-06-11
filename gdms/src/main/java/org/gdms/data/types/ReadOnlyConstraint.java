package org.gdms.data.types;

public class ReadOnlyConstraint extends AbstractBooleanConstraint {

	@Override
	public ConstraintNames getConstraintName() {
		return ConstraintNames.READONLY;
	}
}