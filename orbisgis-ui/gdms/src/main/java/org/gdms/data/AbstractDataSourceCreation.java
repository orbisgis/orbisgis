/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC, scientific researcher, Fernando GONZALEZ
 * CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 **/
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
