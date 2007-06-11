package org.gdms.data.types;

public class PrimaryKeyConstraint extends AbstractBooleanConstraint {

	@Override
	public ConstraintNames getConstraintName() {
		return ConstraintNames.PK;
	}
}