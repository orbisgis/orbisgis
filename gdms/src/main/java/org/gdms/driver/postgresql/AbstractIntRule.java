package org.gdms.driver.postgresql;

import org.gdms.data.types.Constraint;
import org.gdms.data.types.Type;
import org.gdms.driver.jdbc.AbstractConversionRule;
import org.gdms.driver.jdbc.ConversionRule;

public abstract class AbstractIntRule extends AbstractConversionRule implements
		ConversionRule {

	@Override
	public String getSQL(String fieldName, Type fieldType) {
		return "\"" + fieldName + "\" " + getTypeExpr(fieldType)
				+ getGlobalConstraintExpr(fieldType);
	}

	private String getTypeExpr(Type fieldType) {
		int precision = fieldType.getIntConstraint(Constraint.PRECISION);
		if (precision == -1) {
			return getTypeName();
		} else if (precision < 5) {
			return "smallint";
		} else if (precision < 10) {
			return "integer";
		} else if (precision < 19) {
			return "bigint";
		} else if (precision < 307) {
			return "double precision";
		} else {
			return "numeric(" + precision + ")";
		}
	}

}
