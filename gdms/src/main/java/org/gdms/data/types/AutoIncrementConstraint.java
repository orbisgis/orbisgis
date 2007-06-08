package org.gdms.data.types;

public class AutoIncrementConstraint extends AbstractBooleanConstraint {
	public ConstraintNames getConstraintName() {
		return ConstraintNames.AUTO_INCREMENT;
	}
}