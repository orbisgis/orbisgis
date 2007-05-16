package org.gdms.driver;

import java.io.File;
import java.io.IOException;

import org.gdms.data.DataSource;
import org.gdms.data.metadata.DriverMetadata;

public interface FileDriver extends GDBMSDriver {
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
