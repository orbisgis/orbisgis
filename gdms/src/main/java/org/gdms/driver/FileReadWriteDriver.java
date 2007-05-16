package org.gdms.driver;

import java.io.File;
import java.io.IOException;

import org.gdms.data.DataSource;
import org.gdms.data.metadata.DriverMetadata;

/**
 * Interface to be implement by the File drivers that as also RW capabilities
 * 
 */

public interface FileReadWriteDriver extends FileDriver, ReadWriteDriver {
	/**
	 * Copies the datasource from file in to file out
	 * 
	 * @param in
	 * @param out
	 */
	void copy(File in, File out) throws IOException;

	/**
	 * Writes the content of the DataWare to the specified file.
	 * 
	 * @param dataWare
	 *            DataWare with the contents
	 */
	void writeFile(File file, DataSource dataSource) throws DriverException;

	/**
	 * Creates a new file with the given field names and types
	 * 
	 * @param path
	 *            Path to the new file
	 * @param dsm
	 *            Metadata of the source
	 * 
	 * @throws DriverException
	 *             If the creation fails
	 */
	void createSource(String path, DriverMetadata dsm) throws DriverException;
}