package org.gdms.driver.jdbc;

import org.gdms.data.types.Constraint;
import org.gdms.data.types.Type;

public class StringRule extends AbstractConversionRule implements
		ConversionRule {

	@Override
	public int[] getValidConstraints() {
		return addGlobalConstraints(Constraint.LENGTH);
	}

	@Override
	public int getOutputTypeCode() {
		return Type.STRING;
	}

	public String getTypeName() {
		return "varchar";
	}

	@Override
	public String getSQL(String fieldName, Type fieldType) {
		int length = fieldType.getIntConstraint(Constraint.LENGTH);
		if (length != -1) {
			return "\"" + fieldName + "\" " + getTypeName() + "(" + length
					+ ")";
		} else {
			return super.getSQL(fieldName, fieldType);
		}
	}

	public boolean canApply(Type type) {
		return type.getTypeCode() == Type.STRING;
	}

}
