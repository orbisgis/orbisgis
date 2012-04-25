/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.data;

import org.gdms.driver.Driver;
import org.gdms.driver.DriverException;


/**
 * Abstract base class for DataSourceCreation objects
 */
public abstract class AbstractDataSourceCreation implements DataSourceCreation {

	private DataSourceFactory dataSourceFactory;

	private Driver driver;

        /**
         * Retrieve the driver to be used for the data source creation
         * @return
         */
	public final Driver getDriver() {
		if (driver == null) {
			driver = getDriverInstance();
		}

		return driver;
	}

        /**
         * Actual implementation of the getDriver() method. This method MUST be
         * overwritten by the children, as the datasource creation machnism relies on it.
         * @return
         */
	protected abstract Driver getDriverInstance();

        /**
         * Sets the dataSourceFactory associated with this object
         * @param dsf
         */
        @Override
	public final void setDataSourceFactory(DataSourceFactory dsf) {
		this.dataSourceFactory = dsf;
	}

        /**
         * Get the data source factory associated to this.
         * @return
         */
	public final DataSourceFactory getDataSourceFactory() {
		return dataSourceFactory;
	}

        @Override
        public String[] getAvailableTables() throws DriverException {
                return getDriver().getSchema().getTableNames();
        }
}
