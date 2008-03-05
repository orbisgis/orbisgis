package org.gdms.driver.jdbc;

import org.gdms.data.types.Constraint;
import org.gdms.data.types.Type;

public class DoubleRule extends AbstractConversionRule implements
		ConversionRule {

	@Override
	public int[] getValidConstraints() {
		return addGlobalConstraints(Constraint.PRECISION, Constraint.SCALE);
	}

	@Override
	public int getOutputTypeCode() {
		return Type.DOUBLE;
	}

	public String getTypeName() {
		return "double";
	}

	public boolean canApply(Type type) {
		return type.getTypeCode() == Type.DOUBLE;
	}

}
