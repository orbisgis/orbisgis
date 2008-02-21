package org.gdms.driver.jdbc;

import org.gdms.data.types.ConstraintNames;
import org.gdms.data.types.Type;

public class AutonumericRule extends AbstractConversionRule implements
		ConversionRule {

	public boolean canApply(Type type) {
		return (type.getTypeCode() == Type.INT)
				&& type.getBooleanConstraint(ConstraintNames.AUTO_INCREMENT);
	}

	public String getTypeName() {
		return "serial";
	}

	@Override
	protected int getOutputTypeCode() {
		return 0;
	}

}
