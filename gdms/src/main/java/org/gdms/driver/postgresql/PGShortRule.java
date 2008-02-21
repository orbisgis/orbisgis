package org.gdms.driver.postgresql;

import org.gdms.data.types.Type;
import org.gdms.driver.jdbc.ConversionRule;

public class PGShortRule extends AbstractIntRule implements ConversionRule {

	@Override
	public int getOutputTypeCode() {
		return Type.SHORT;
	}

	public String getTypeName() {
		return "smallint";
	}

	public boolean canApply(Type type) {
		return type.getTypeCode() == Type.SHORT
				|| type.getTypeCode() == Type.BYTE;
	}

}
