/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.source;

import java.io.File;
import java.io.IOException;

import org.gdms.data.DataSourceDefinition;
import org.gdms.data.db.DBSource;
import org.gdms.data.wms.WMSSource;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;

public interface Source {

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

	/**
	 * Gets the definition of this source
	 * 
	 * @return
	 */
	DataSourceDefinition getDataSourceDefinition();

	/**
	 * Get the id of the driver used to access this source
	 * 
	 * @return
	 */
	String getDriverId();

	/**
	 * Get the type of the source as a bit-or of the constants:
	 * {@link SourceManager#FILE},{@link SourceManager#DB},
	 * {@link SourceManager#SQL},{@link SourceManager#VECTORIAL},
	 * {@link SourceManager#MEMORY},{@link SourceManager#RASTER},
	 * {@link SourceManager#SQL}
	 * 
	 * @return
	 */
	int getType();

	/**
	 * Get the name of the source type as a short description
	 * 
	 * @return
	 */
	String getTypeName();

	/**
	 * Gets the file of this source. If this source is not a file it returns
	 * null
	 * 
	 * @return
	 */
	File getFile();

	/**
	 * Gets the definition of the db source. If this source is not a database
	 * source it returns null
	 * 
	 * @return
	 */
	DBSource getDBSource();

	/**
	 * Get the WMSSource with the information of the WMS connection. If this
	 * source is not a wms source it returns null
	 * 
	 * @return
	 */
	WMSSource getWMSSource();

	/**
	 * Gets the source of the object source. If this source is not a object
	 * source it returns null
	 * 
	 * @return
	 */
	ObjectDriver getObject();

	/**
	 * Gets the source of this SQL source. If this source is not a SQL query it
	 * returns null
	 * 
	 * @return
	 */
	String getSQL();

	/**
	 * @return true if this source is a file. False otherwise
	 */
	boolean isFileSource();

	/**
	 * @return true if source is a database table. False otherwise
	 */
	boolean isDBSource();

	/**
	 * @return true if source is a wms layer. False otherwise
	 */
	boolean isWMSSource();

	/**
	 * @return source is an object. False otherwise
	 */
	boolean isObjectSource();

	/**
	 * @return source is a sql query. False otherwise
	 */
	boolean isSQLSource();

}