package org.gdms.driver.jdbc;

import org.gdms.data.types.ConstraintNames;
import org.gdms.data.types.Type;

public class FloatRule extends AbstractConversionRule implements ConversionRule {

	@Override
	public ConstraintNames[] getValidConstraints() {
		return addGlobalConstraints(ConstraintNames.PRECISION,
				ConstraintNames.SCALE);
	}

	@Override
	public int getOutputTypeCode() {
		return Type.FLOAT;
	}

	public String getTypeName() {
		return "float";
	}

	public boolean canApply(Type type) {
		return type.getTypeCode() == Type.FLOAT;
	}

}
