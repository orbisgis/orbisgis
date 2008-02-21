package org.gdms.driver.jdbc;

import org.gdms.data.types.ConstraintNames;
import org.gdms.data.types.Type;

public class StringRule extends AbstractConversionRule implements
		ConversionRule {

	@Override
	public ConstraintNames[] getValidConstraints() {
		return addGlobalConstraints(ConstraintNames.LENGTH);
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
		int length = fieldType.getIntConstraint(ConstraintNames.LENGTH);
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
