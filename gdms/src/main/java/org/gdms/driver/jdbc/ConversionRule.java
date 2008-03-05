package org.gdms.driver.jdbc;

import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;

public interface ConversionRule extends TypeDefinition {

	/**
	 * Returns true if the rule can be applied to the specified type
	 *
	 * @return
	 */
	boolean canApply(Type type);

	/**
	 * Gets the SQL representation of the specified type. fieldType will have
	 * the type code returned by getTypeCode()
	 *
	 * @param fieldName
	 *            Name of the field
	 * @param fieldType
	 *            GDMS Type of the field
	 * @return
	 */
	String getSQL(String fieldName, Type fieldType);

	/**
	 * Gets the name of the type in SQL statements
	 *
	 * @return
	 */
	String getTypeName();

	public int[] getValidConstraints();
}
