package org.gdms.driver.jdbc;

import org.gdms.data.types.ConstraintNames;
import org.gdms.data.types.Type;

public class ByteRule extends AbstractConversionRule implements
		ConversionRule {

	@Override
	public ConstraintNames[] getValidConstraints() {
		return addGlobalConstraints(ConstraintNames.PRECISION);
	}

	@Override
	public int getOutputTypeCode() {
		return Type.BYTE;
	}

	public String getTypeName() {
		return "byte";
	}

	public boolean canApply(Type type) {
		return type.getTypeCode() == Type.BYTE;
	}

}
