package org.gdms.driver.jdbc;

import org.gdms.data.types.Constraint;
import org.gdms.data.types.Type;

public class ByteRule extends AbstractConversionRule implements ConversionRule {

	@Override
	public int[] getValidConstraints() {
		return addGlobalConstraints(Constraint.PRECISION);
	}

	@Override
	public int getOutputTypeCode() {
		return Type.BYTE;
	}

	public String getTypeName() {
		return "byte";
	}

	public boolean canApply(Type type) {
		return type.getTypeCode() == Type.BYTE;
	}

}
