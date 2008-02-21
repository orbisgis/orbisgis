package org.gdms.driver.postgresql;

import org.gdms.data.types.Type;
import org.gdms.driver.jdbc.ConversionRule;

public class PGIntRule extends AbstractIntRule implements ConversionRule {

	@Override
	public int getOutputTypeCode() {
		return Type.INT;
	}

	public String getTypeName() {
		return "integer";
	}

	public boolean canApply(Type type) {
		return type.getTypeCode() == Type.INT;
	}

}
