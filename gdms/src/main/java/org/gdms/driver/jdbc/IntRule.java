package org.gdms.driver.jdbc;

import org.gdms.data.types.Constraint;
import org.gdms.data.types.Type;

public class IntRule extends AbstractConversionRule implements ConversionRule {

	@Override
	public int[] getValidConstraints() {
		return addGlobalConstraints(Constraint.PRECISION);
	}

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
