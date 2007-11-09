package org.gdms.source;

import java.io.File;
import java.io.IOException;

import org.gdms.driver.DriverException;

public interface Source {

	public static final int OTHER = 0;
	public static final int SHP = 1;
	public static final int CSV = 2;
	public static final int DBF = 3;
	public static final int GML = 4;
	public static final int H2 = 5;
	public static final int HSQLDB = 6;
	public static final int MEMORY = 7;
	public static final int SOLENE_VAL = 8;
	public static final int SOLENE_CIR = 9;
	public static final int POSTGRESQL = 10;

	/**
	 * Creates a property which content is stored in a file. If the property
	 * already exists it returns the associated File
	 *
	 * @param propertyName
	 *            name of the property
	 * @return The file to store the content
	 * @throws IOException
	 *             If the file cannot be created
	 */
	File createFileProperty(String propertyName) throws IOException;

	/**
	 * Gets the contents of the file associated with the property
	 *
	 * @param propertyName
	 *            name of the property we want to access
	 * @return The bytes stored in the associated file or null if the property
	 *         does not exist
	 * @throws IOException
	 */
	byte[] getFilePropertyContents(String propertyName) throws IOException;

	/**
	 * The same as getFilePropertyContents but building an string with the byte
	 * array
	 *
	 * @param propertyName
	 * @return
	 * @throws IOException
	 */
	String getFilePropertyContentsAsString(String propertyName)
			throws IOException;

	/**
	 * Creates (or modifies if it already exist) a string property.
	 *
	 * @param propertyName
	 * @param value
	 */
	void putProperty(String propertyName, String value);

	/**
	 * Gets the value of a string property or null if the property does not
	 * exist
	 *
	 * @param propertyName
	 *            Name of the property which value will be returned
	 * @return
	 */
	String getProperty(String propertyName);

	/**
	 * Returns true if the source has a property, either stored on a file or a
	 * string, with the specified name
	 *
	 * @param propertyName
	 * @return
	 */
	boolean hasProperty(String propertyName);

	/**
	 * Deletes the property. This method is independent of the type of storage
	 * of the property
	 *
	 * @param propertyName
	 * @throws IOException
	 */
	void deleteProperty(String propertyName) throws IOException;

	/**
	 * Gets the file associated with the specified property. if the property
	 * content is not stored on a file or the property does not exist this
	 * method will return null
	 *
	 * @param propertyName
	 * @return
	 */
	File getFileProperty(String propertyName);

	/**
	 * Gets the names of all properties with string values
	 *
	 * @return
	 * @throws IOException
	 */
	String[] getStringPropertyNames() throws IOException;

	/**
	 * Gets the names of all properties with values stored in files
	 *
	 * @return
	 */
	String[] getFilePropertyNames();

	/**
	 * Gets the name of the source
	 *
	 * @return
	 */
	String getName();

	/**
	 * @return true if the user specified a name when registering it. False if
	 *         the name was generated automatically
	 */
	public boolean isWellKnownName();

	/**
	 * Indicates if the source has been modified by another entity different
	 * from the DataSourceFactory this source belongs to. This call can be quite
	 * time consuming depending on the type of the source
	 *
	 * @return true if the source has not been modified and false otherwise
	 * @throws DriverException
	 */
	Boolean isUpToDate() throws DriverException;

	/**
	 * Gets all the sources that depend on this source
	 *
	 * @return
	 */
	String[] getReferencingSources();

	/**
	 * Gets all the sources this source depends on
	 *
	 * @return
	 */
	String[] getReferencedSources();

}