package org.gdms.driver.postgresql;

import org.gdms.data.types.Type;
import org.gdms.driver.jdbc.ConversionRule;
import org.gdms.driver.jdbc.GeometryRule;

public class PGGeometryRule extends GeometryRule implements
		ConversionRule {

	@Override
	public String getSQL(String fieldName, Type fieldType) {
		return null;
	}

}
