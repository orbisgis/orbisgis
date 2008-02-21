package org.gdms.driver.jdbc;

import org.gdms.data.types.Type;

public class BinaryRule extends AbstractConversionRule implements
		ConversionRule {

	public String getTypeName() {
		return "binary";
	}

	@Override
	protected int getOutputTypeCode() {
		return Type.BINARY;
	}

	public boolean canApply(Type type) {
		return type.getTypeCode() == Type.BINARY;
	}

}
