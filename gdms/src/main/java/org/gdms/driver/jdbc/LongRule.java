package org.gdms.driver.jdbc;

import org.gdms.data.types.ConstraintNames;
import org.gdms.data.types.Type;

public class LongRule extends AbstractConversionRule implements ConversionRule {

	@Override
	public ConstraintNames[] getValidConstraints() {
		return addGlobalConstraints(ConstraintNames.PRECISION);
	}

	@Override
	public int getOutputTypeCode() {
		return Type.LONG;
	}

	public String getTypeName() {
		return "bigint";
	}

	public boolean canApply(Type type) {
		return type.getTypeCode() == Type.LONG;
	}

}
