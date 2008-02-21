package org.gdms.driver.jdbc;

import org.gdms.data.types.Type;

public class TimestampRule extends AbstractConversionRule implements
		ConversionRule {

	@Override
	public int getOutputTypeCode() {
		return Type.TIMESTAMP;
	}

	public String getTypeName() {
		return "timestamp";
	}

	public boolean canApply(Type type) {
		return type.getTypeCode() == Type.TIMESTAMP;
	}

}
