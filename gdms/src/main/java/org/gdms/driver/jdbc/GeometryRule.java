package org.gdms.driver.jdbc;

import org.gdms.data.types.ConstraintNames;
import org.gdms.data.types.Type;

public class GeometryRule extends AbstractConversionRule implements
		ConversionRule {

	@Override
	public ConstraintNames[] getValidConstraints() {
		return addGlobalConstraints(ConstraintNames.GEOMETRY,
				ConstraintNames.CRS);
	}

	public int getOutputTypeCode() {
		return Type.GEOMETRY;
	}

	public String getTypeName() {
		return "geometry";
	}

	@Override
	public String getSQL(String fieldName, Type fieldType) {
		return null;
	}

	public boolean canApply(Type type) {
		return type.getTypeCode() == Type.GEOMETRY;
	}

}
