package org.gdms.driver;

import java.io.File;

public interface FileDriver extends ReadOnlyDriver {
	/**
	 */
	void open(File file) throws DriverException;

	/**
	 * Closes the file being accessed
	 *
	 * @throws DriverExceptio
	 *             if something fails
	 */
	void close() throws DriverException;

	/**
	 * Returns true if the driver can read the file specified as a parameter
	 *
	 * @param f
	 *            File to check
	 *
	 * @return DOCUMENT ME!
	 */
	boolean fileAccepted(File f);

	/**
	 * Returns a file name that will be accepted by the driver. Example: a ->
	 * a.csv
	 *
	 * @param fileName
	 *            base name
	 * @return
	 */
	String completeFileName(String fileName);
}