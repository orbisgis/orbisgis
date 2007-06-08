package org.gdms.data.types;

public class PrimaryKeyConstraint extends AbstractBooleanConstraint {
	public ConstraintNames getConstraintName() {
		return ConstraintNames.PK;
	}
}