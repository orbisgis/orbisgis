/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
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

/**
 *
 */
public interface DataSourceDefinition {
	/**
	 * Creates a DataSource with the information of this object
	 *
	 * @param tableName
	 *            name of the DataSource
	 * @param tableAlias
	 *            alias of the DataSource
	 * @return DataSource
	 */
	public DataSource createDataSource(String tableName)
			throws DataSourceCreationException;

	/**
	 * Creates this source with the content specified in the parameter
	 *
	 * @param contents
	 */
	public void createDataSource(DataSource contents) throws DriverException;

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
	 * @return
	 * @throws DriverException
	 */
	public String calculateChecksum() throws DriverException;

	/**
	 * Gets the names of the sources this source depends on. Usually it will be
	 * an empty array but definitions that consist in an sql instruction may
	 * return several values
	 *
	 * @return
	 */
	public ArrayList<String> getSourceDependencies();

	/**
	 * Gets the type of the source defined by this type
	 * @return
	 */
	public int getType();

}
