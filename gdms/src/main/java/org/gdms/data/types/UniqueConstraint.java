package org.gdms.data.types;

public class UniqueConstraint extends AbstractBooleanConstraint {
	public ConstraintNames getConstraintName() {
		return ConstraintNames.UNIQUE;
	}
}