package org.gdms.driver;

import org.gdms.data.InternalDataSource;

/**
 * Interface to be implement by the Object driver that as also RW capabilities
 *
 */
public interface ObjectReadWriteDriver extends ObjectDriver, ReadWriteDriver {
	/**
	 * Writes the content in the DataWare to the specified file
	 *
	 * @param dataWare
	 *            DataWare with the contents
	 */
	void write(InternalDataSource dataSource) throws DriverException;
}