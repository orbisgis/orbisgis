package org.gdms.driver.postgresql;

import org.gdms.data.types.Constraint;
import org.gdms.data.types.Type;
import org.gdms.driver.jdbc.AbstractConversionRule;
import org.gdms.driver.jdbc.ConversionRule;

public class PGDoubleRule extends AbstractConversionRule implements
		ConversionRule {

	@Override
	public String getSQL(String fieldName, Type fieldType) {
		return "\"" + fieldName + "\"" + getTypeExpr(fieldType);
	}

	private String getTypeExpr(Type fieldType) {
		int precision = fieldType.getIntConstraint(Constraint.PRECISION);
		int scale = fieldType.getIntConstraint(Constraint.SCALE);
		if (scale == -1) {
			return "double precision";
		} else if (scale < 15) {
			return "double precision";
		} else {
			return "numeric(" + precision + ", " + scale + ")";
		}
	}

	public boolean canApply(Type type) {
		return type.getTypeCode() == Type.FLOAT
				|| type.getTypeCode() == Type.DOUBLE;
	}

	public String getTypeName() {
		return "double precision";
	}

	@Override
	protected int getOutputTypeCode() {
		return Type.DOUBLE;
	}

}