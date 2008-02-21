package org.gdms.driver.jdbc;

import org.gdms.data.types.Type;

public class BooleanRule extends AbstractConversionRule implements
		ConversionRule {

	@Override
	public int getOutputTypeCode() {
		return Type.BOOLEAN;
	}

	public String getTypeName() {
		return "boolean";
	}

	public boolean canApply(Type type) {
		return type.getTypeCode() == Type.BOOLEAN;
	}
}
