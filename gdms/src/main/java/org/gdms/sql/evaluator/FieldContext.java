package org.gdms.sql.evaluator;

import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

public interface FieldContext {

	/**
	 * Gets the value of the specified field in the current context
	 *
	 * @param fieldId
	 * @return
	 * @throws DriverException
	 */
	Value getFieldValue(int fieldId) throws DriverException;

	/**
	 * Gets the type of the specified field in the current context
	 *
	 * @param fieldId
	 * @return
	 * @throws DriverException
	 */
	Type getFieldType(int fieldId) throws DriverException;

}
