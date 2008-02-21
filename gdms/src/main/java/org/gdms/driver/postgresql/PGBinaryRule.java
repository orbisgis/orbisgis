package org.gdms.driver.postgresql;

import org.gdms.data.types.Type;
import org.gdms.driver.jdbc.BinaryRule;
import org.gdms.driver.jdbc.ConversionRule;

public class PGBinaryRule extends BinaryRule implements
		ConversionRule {

	@Override
	public int getOutputTypeCode() {
		return Type.BINARY;
	}

	public String getTypeName() {
		return "bytea";
	}

	public boolean canApply(Type type) {
		return type.getTypeCode() == Type.BINARY;
	}

}
