package org.gdms.driver.h2;

import org.gdms.data.types.Type;
import org.gdms.driver.jdbc.ConversionRule;
import org.gdms.driver.jdbc.GeometryRule;

public class H2GeometryRule extends GeometryRule implements
		ConversionRule {

	public int[] getValidConstraints() {
		return new int[0];
	}

	public String getSQL(String fieldName, Type fieldType) {
		return "\"" + fieldName + "\" blob";
	}

}
