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

import org.gdms.data.DataSourceCreation;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.DataSourceFinalizationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SourceAlreadyExistsException;
import org.gdms.data.db.DBSource;
import org.gdms.data.wms.WMSSource;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.strategies.SemanticException;
import org.gdms.sql.strategies.TableNotFoundException;

public interface SourceManager {

	/**
	 * The source has no known type
	 */
	public static final int UNKNOWN = 0;

	/**
	 * The source type can contain raster fields
	 */
	public static final int RASTER = 1;

	/**
	 * The source type can contain geometric fields
	 */
	public static final int VECTORIAL = 2;

	/**
	 * The source is stored in a file
	 */
	public static final int FILE = 4;

	/**
	 * The source is stored in a database
	 */
	public static final int DB = 8;

	/**
	 * The source is stored in memory
	 */
	public static final int MEMORY = 16;

	/**
	 * The source is the result of a SQL query
	 */
	public static final int SQL = 32;

	/**
	 * The source contains the parameters of a WMS connection
	 */
	public static final int WMS = 64;

	/**
	 * Sets the driver manager used to load the drivers of the sources
	 * 
	 * @param dm
	 */
	public abstract void setDriverManager(DriverManager dm);

	/**
	 * Adds a listener to the events in this class
	 * 
	 * @param e
	 * @return
	 */
	public abstract boolean addSourceListener(SourceListener e);

	/**
	 * Removes a listener to the events in this class
	 * 
	 * @param o
	 * @return
	 */
	public abstract boolean removeSourceListener(SourceListener o);

	/**
	 * Removes all the information about the sources
	 * 
	 * @throws IOException
	 */
	public abstract void removeAll() throws IOException;

	/**
	 * Try to remove the information about the source with the name or alias
	 * specified
	 * 
	 * @param name
	 * @return true if the source was found and removed and false if the source
	 *         was not found
	 * @throws IllegalStateException
	 *             If some source depends on the source being removed
	 */
	public abstract boolean remove(String name) throws IllegalStateException;

	/**
	 * Registers a file with the specified name
	 * 
	 * @param name
	 *            Name to register with
	 * @param file
	 *            file to register
	 * @throws SourceAlreadyExistsException
	 */
	public abstract void register(String name, File file)
			throws SourceAlreadyExistsException;

	/**
	 * Registers a database table with the specified name
	 * 
	 * @param name
	 *            Name to register
	 * @param dbTable
	 *            source to register
	 */
	public abstract void register(String name, DBSource dbTable)
			throws SourceAlreadyExistsException;

	/**
	 * Registers a wms source with the specified name
	 * 
	 * @param name
	 *            Name to register
	 * @param wmsSource
	 *            source to register
	 */
	public abstract void register(String name, WMSSource wmsSource)
			throws SourceAlreadyExistsException;

	/**
	 * Registers a object with the specified name
	 * 
	 * @param name
	 *            Name to register with
	 * @param driver
	 *            object to register
	 */
	public abstract void register(String name, ObjectDriver driver)
			throws SourceAlreadyExistsException;

	/**
	 * Generic register method
	 * 
	 * @param name
	 *            Name to register with
	 * @param def
	 *            definition of the source
	 */
	public abstract void register(String name, DataSourceDefinition def)
			throws SourceAlreadyExistsException;

	/**
	 * Get's a unique id
	 * 
	 * @return unique id
	 */
	public abstract String getUID();

	/**
	 * Returns a name that hasn't been used for any registration so far and
	 * based in the specified name
	 * 
	 * @param base
	 *            Base to obtain the unique name
	 * @return
	 */
	String getUniqueName(String base);

	/**
	 * Registers generating the name automatically
	 * 
	 * @param file
	 * @return the name of the registered source
	 */
	public abstract String nameAndRegister(File file);

	/**
	 * Registers generating the name automatically
	 * 
	 * @param dbTable
	 * @return the name of the registered source
	 */
	public abstract String nameAndRegister(DBSource dbTable);

	/**
	 * Registers generating the name automatically
	 * 
	 * @param wmsSource
	 * @return the name of the registered source
	 */
	public abstract String nameAndRegister(WMSSource wmsSource);

	/**
	 * Registers generating the name automatically
	 * 
	 * @param driver
	 * @return the name of the registered source
	 */
	public abstract String nameAndRegister(ObjectDriver driver);

	/**
	 * Registers generating the name automatically
	 * 
	 * @param query
	 * @return the name of the registered source
	 * @throws DriverException
	 * @throws SemanticException
	 * @throws ParseException
	 */
	public abstract String nameAndRegister(String query) throws ParseException,
			SemanticException, DriverException;

	/**
	 * Registers generating the name automatically
	 * 
	 * @param def
	 * @return the name of the registered source
	 */
	public abstract String nameAndRegister(DataSourceDefinition def);

	/**
	 * Adds a new name to the specified data source name. The main name of the
	 * data source will not change but the new name can be used to refer to the
	 * source in the same way as the main one
	 * 
	 * @param dsName
	 * @param newName
	 * @throws TableNotFoundException
	 */
	public abstract void addName(String dsName, String newName)
			throws TableNotFoundException, SourceAlreadyExistsException;

	/**
	 * Modifies the name of the specified source. If modifies either the main
	 * name either the aliases
	 * 
	 * @param dsName
	 * @param newName
	 * @throws SourceAlreadyExistsException
	 */
	public abstract void rename(String dsName, String newName)
			throws SourceAlreadyExistsException;

	/**
	 * @param sourceName
	 * @return true if there is a source with the specified name, false
	 *         otherwise
	 */
	public abstract boolean exists(String sourceName);

	/**
	 * Gets the main name of the source
	 * 
	 * @param dsName
	 * @return
	 * @throws NoSuchTableException
	 *             If there is no source with the specified name
	 */
	public abstract String getMainNameFor(String dsName)
			throws NoSuchTableException;

	/**
	 * Called to free resources
	 * 
	 * @throws DataSourceFinalizationException
	 */
	public abstract void shutdown() throws DataSourceFinalizationException;

	/**
	 * @return true if there is no source in the manager and false otherwise
	 */
	public abstract boolean isEmpty();

	/**
	 * Creates a source and returns a definition of the source that can be used
	 * to register it. This method does not register the created source
	 * 
	 * @param dsc
	 *            creation object
	 * @return
	 * @throws DriverException
	 */
	public abstract DataSourceDefinition createDataSource(DataSourceCreation dsc)
			throws DriverException;

	/**
	 * Gets the source with the specified name
	 * 
	 * @param name
	 * @return null if there is no source with that name
	 */
	public Source getSource(String name);

	/**
	 * Sets the directory where the registry of sources and its properties are
	 * stored. Each call to saveStatus will serialize the content of this
	 * manager to the specified directory. The current status is preserved
	 * 
	 * @param newDir
	 * @throws DriverException
	 */
	public abstract void setSourceInfoDirectory(String newDir)
			throws DriverException;

	/**
	 * Sets the directory where the registry of sources and its properties are
	 * stored. Each call to saveStatus will serialize the content of this
	 * manager to the specified directory. The specified directory is read and
	 * the status of this source manager is replaced by the one in the directory
	 * 
	 * @param newSourceInfoDir
	 * @throws IOException
	 */
	public abstract void changeSourceInfoDirectory(String newSourceInfoDir)
			throws IOException;

	/**
	 * Method for debugging purposes that obtains a snapshot of the system
	 * 
	 * @return
	 * @throws IOException
	 */
	public abstract String getMemento() throws IOException;

	/**
	 * saves all the information about the sources in the directory specified by
	 * the last call to setSourceInfoDirectory
	 * 
	 * @throws DriverException
	 */
	public abstract void saveStatus() throws DriverException;

	/**
	 * Gets the directory where the information of sources is stored
	 * 
	 * @return
	 */
	public abstract File getSourceInfoDirectory();

	/**
	 * Registers an sql instruction.
	 * 
	 * @param name
	 *            name to register
	 * @param sql
	 *            instruction to register
	 * @throws DriverException
	 * @throws SemanticException
	 * @throws ParseException
	 */
	public abstract void register(String name, String sql)
			throws SourceAlreadyExistsException, ParseException,
			SemanticException, DriverException;

	/**
	 * Gets the driver manager
	 * 
	 * @return
	 */
	public DriverManager getDriverManager();

	/**
	 * Removes the specified secondary name.
	 * 
	 * @param secondName
	 */
	public abstract void removeName(String secondName);

	/**
	 * Gets the name of the source that accesses the specified source
	 * definition. If the source definition is not accessed by any source it
	 * will return null
	 * 
	 * @param fileSourceDefinition
	 * @return
	 */
	public abstract String getSourceName(
			DataSourceDefinition dataSourceDefinition);

	/**
	 * Gets the list of the names of all the sources in the manager
	 * 
	 * @return
	 */
	public abstract String[] getSourceNames();

	/**
	 * Get all the alternative names for the specified source
	 * 
	 * @param sourceName
	 * @return
	 * @throws NoSuchTableException
	 *             If there is no source with the specified name
	 */
	public abstract String[] getAllNames(String sourceName)
			throws NoSuchTableException;

}