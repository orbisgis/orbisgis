package org.gdms.driver;

import java.io.File;

public interface FileDriver extends ReadOnlyDriver {
	/**
	 */
	void open(File file) throws DriverException;

	/**
	 * Cierra el Fichero sobre el que se estaba accediendo
	 *
	 * @param conn
	 *            TODO
	 *
	 * @throws DriverException
	 *             Si se produce algun error
	 */
	void close() throws DriverException;

	/**
	 * devuelve true si el driver puede leer el fichero que se pasa como
	 * parametro, false en caso contrario
	 *
	 * @param f
	 *            Fichero que se quiere comprobar
	 *
	 * @return DOCUMENT ME!
	 */
	boolean fileAccepted(File f);

	/**
	 * Returns a file name that will be accepted by the driver. Example: a ->
	 * a.txt
	 *
	 * @param fileName
	 *            base name
	 * @return
	 */
	String completeFileName(String fileName);
}