package org.gdms.driver.postgresql;

import org.gdms.data.types.Type;
import org.gdms.driver.jdbc.ConversionRule;

public class PGLongRule extends AbstractIntRule implements ConversionRule {

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
