package org.gdms.driver.jdbc;

import org.gdms.data.types.Type;

public class DateRule extends AbstractConversionRule implements ConversionRule {

	@Override
	public int getOutputTypeCode() {
		return Type.DATE;
	}

	public String getTypeName() {
		return "date";
	}

	public boolean canApply(Type type) {
		return type.getTypeCode() == Type.DATE;
	}

}
