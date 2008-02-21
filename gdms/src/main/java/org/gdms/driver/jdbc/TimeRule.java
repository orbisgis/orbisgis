package org.gdms.driver.jdbc;

import org.gdms.data.types.Type;

public class TimeRule extends AbstractConversionRule implements ConversionRule {

	@Override
	public int getOutputTypeCode() {
		return Type.TIME;
	}

	public String getTypeName() {
		return "time";
	}

	public boolean canApply(Type type) {
		return type.getTypeCode() == Type.TIME;
	}

}
