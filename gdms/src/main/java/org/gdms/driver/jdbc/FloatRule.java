package org.gdms.driver.jdbc;

import org.gdms.data.types.Constraint;
import org.gdms.data.types.Type;

public class FloatRule extends AbstractConversionRule implements ConversionRule {

	@Override
	public int[] getValidConstraints() {
		return addGlobalConstraints(Constraint.PRECISION, Constraint.SCALE);
	}

	@Override
	public int getOutputTypeCode() {
		return Type.FLOAT;
	}

	public String getTypeName() {
		return "float";
	}

	public boolean canApply(Type type) {
		return type.getTypeCode() == Type.FLOAT;
	}

}
