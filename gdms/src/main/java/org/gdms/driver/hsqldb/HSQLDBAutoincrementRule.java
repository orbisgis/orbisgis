package org.gdms.driver.hsqldb;

import org.gdms.data.types.ConstraintNames;
import org.gdms.data.types.Type;
import org.gdms.driver.jdbc.AbstractConversionRule;
import org.gdms.driver.jdbc.ConversionRule;

public class HSQLDBAutoincrementRule extends AbstractConversionRule implements
		ConversionRule {

	public boolean canApply(Type type) {
		return (type.getTypeCode() == Type.INT)
				&& type.getBooleanConstraint(ConstraintNames.AUTO_INCREMENT);
	}

	public String getTypeName() {
		return "identity";
	}

	@Override
	protected int getOutputTypeCode() {
		return Type.INT;
	}

}
