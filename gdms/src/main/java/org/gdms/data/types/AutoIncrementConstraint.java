package org.gdms.data.types;

public class AutoIncrementConstraint extends AbstractBooleanConstraint {

	@Override
	public ConstraintNames getConstraintName() {
		return ConstraintNames.AUTO_INCREMENT;
	}
}