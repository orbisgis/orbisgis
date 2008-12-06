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
package org.gdms.data;

import java.util.ArrayList;

import org.gdms.driver.DriverException;
import org.gdms.source.directory.DefinitionType;
import org.orbisgis.progress.IProgressMonitor;

/**
 * Class to be implemented to add new types of sources to the system.
 */
public interface DataSourceDefinition {
	/**
	 * Creates a DataSource with the information of this object
	 * 
	 * @param tableName
	 *            name of the DataSource
	 * @param pm
	 *            To indicate progress or being canceled
	 * @return DataSource
	 */
	public DataSource createDataSource(String tableName, IProgressMonitor pm)
			throws DataSourceCreationException;

	/**
	 * Creates this source with the content specified in the parameter
	 * 
	 * @param contents
	 */
	public void createDataSource(DataSource contents, IProgressMonitor pm)
			throws DriverException;

	/**
	 * if any, frees the resources taken when the DataSource was created
	 * 
	 * @param name
	 *            DataSource registration name
	 * 
	 * @throws DataSourceFinalizationException
	 *             If the operation fails
	 */
	public void freeResources(String name)
			throws DataSourceFinalizationException;

	/**
	 * Gives to the DataSourceDefinition a reference of the DataSourceFactory
	 * where the DataSourceDefinition is registered
	 * 
	 * @param dsf
	 */
	public void setDataSourceFactory(DataSourceFactory dsf);

	/**
	 * Returns a xml object to save the definition at disk
	 * 
	 * @return
	 */
	public DefinitionType getDefinition();

	/**
	 * Calculates the checksum of the source
	 * 
	 * @param openDS
	 *            An instance to an open DataSource that accesses the source
	 *            this object defines. Null if there is no open DataSource
	 * 
	 * @return
	 * @throws DriverException
	 */
	public String calculateChecksum(DataSource openDS) throws DriverException;

	/**
	 * Gets the names of the sources this source depends on. Usually it will be
	 * an empty array but definitions that consist in an sql instruction may
	 * return several values
	 * 
	 * @return
	 * @throws DriverException
	 */
	public ArrayList<String> getSourceDependencies() throws DriverException;

	/**
	 * Gets the type of the source accessed by this definition
	 * 
	 * @return
	 */
	public int getType();

	/**
	 * Get the source type description of the source accessed by this definition
	 * 
	 * @return
	 */
	public String getTypeName();

	/**
	 * Method that lets the DataSourceDefinitions perform any kind of
	 * initialization
	 * 
	 * @throws DriverException
	 *             If the source is not valid and cannot be initializated
	 */
	public void initialize() throws DriverException;

	/**
	 * Return true if this definition represents the same source as the
	 * specified one
	 * 
	 * @param dsd
	 * @return
	 */
	boolean equals(DataSourceDefinition dsd);

	/**
	 * Get the id of the driver used to access this source definition
	 * 
	 * @return
	 */
	public String getDriverId();

}
