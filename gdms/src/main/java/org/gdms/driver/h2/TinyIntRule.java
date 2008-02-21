package org.gdms.driver.h2;

import org.gdms.data.types.Type;
import org.gdms.driver.jdbc.ConversionRule;
import org.gdms.driver.postgresql.AbstractIntRule;

public class TinyIntRule extends AbstractIntRule implements
		ConversionRule {

	@Override
	public int getOutputTypeCode() {
		return Type.BYTE;
	}

	public String getTypeName() {
		return "tinyint";
	}

	public boolean canApply(Type type) {
		return type.getTypeCode() == Type.BYTE;
	}

}
