package org.gdms.driver.jdbc;

import org.gdms.data.types.Constraint;
import org.gdms.data.types.Type;

public class ShortRule extends AbstractConversionRule implements ConversionRule {

	@Override
	public int[] getValidConstraints() {
		return addGlobalConstraints(Constraint.PRECISION);
	}

	@Override
	public int getOutputTypeCode() {
		return Type.SHORT;
	}

	public String getTypeName() {
		return "smallint";
	}

	public boolean canApply(Type type) {
		return type.getTypeCode() == Type.SHORT;
	}

}
