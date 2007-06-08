package org.gdms.data.metadata;

import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;

/**
 * Defines the schema of a source
 */
public interface Metadata {

	/**
	 * Gets the number of fields
	 * 
	 * @return
	 * @throws DriverException
	 */
	public int getFieldCount() throws DriverException;

	/**
	 * Gets the name of the field.
	 * 
	 * @param fieldId
	 * @return
	 */
	public String getFieldName(int fieldId) throws DriverException;

	/**
	 * Gets the name of the field type. It should be one of the values returned
	 * by the getAvailableTypes method in the driver
	 * 
	 * @return
	 */
	public Type getFieldType(int fieldId) throws DriverException;
}