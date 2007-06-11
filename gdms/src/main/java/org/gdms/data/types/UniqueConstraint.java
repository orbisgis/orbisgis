package org.gdms.data.types;

public class UniqueConstraint extends AbstractBooleanConstraint {

	@Override
	public ConstraintNames getConstraintName() {
		return ConstraintNames.UNIQUE;
	}
}