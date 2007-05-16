package org.gdms.driver;

import org.gdms.data.DataSource;

/**
 * Interface to be implement by the Object driver that as also RW capabilities
 * 
 */
public interface ObjectReadWriteDriver extends ObjectDriver {
	public boolean isEditable();

	/**
	 * Writes the content in the DataWare to the specified file
	 * 
	 * @param dataWare
	 *            DataWare with the contents
	 */
	void write(DataSource dataWare) throws DriverException;
}