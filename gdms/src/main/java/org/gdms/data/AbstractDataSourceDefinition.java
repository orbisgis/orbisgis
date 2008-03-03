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

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.gdms.driver.ChecksumCalculator;
import org.gdms.driver.DriverException;
import org.gdms.driver.ReadOnlyDriver;
import org.gdms.source.Source;
import org.orbisgis.NullProgressMonitor;

public abstract class AbstractDataSourceDefinition implements
		DataSourceDefinition {

	private DataSourceFactory dsf;

	private ReadOnlyDriver driver;

	public void freeResources(String name)
			throws DataSourceFinalizationException {
	}

	public void setDataSourceFactory(DataSourceFactory dsf) {
		this.dsf = dsf;
	}

	public ReadOnlyDriver getDriver() {
		if (driver == null) {
			driver = getDriverInstance();
		}

		return driver;
	}

	protected abstract ReadOnlyDriver getDriverInstance();

	public void setDriver(ReadOnlyDriver driver) {
		this.driver = driver;
	}

	public DataSourceFactory getDataSourceFactory() {
		return dsf;
	}

	protected Source getSource(String name) {
		return getDataSourceFactory().getSourceManager().getSource(name);
	}

	public String calculateChecksum(DataSource openDS) throws DriverException {
		if (driver instanceof ChecksumCalculator) {
			return ((ChecksumCalculator) driver).getChecksum();
		} else {
			try {
				DataSource ds = openDS;
				if (ds == null) {
					ds = createDataSource("any", new NullProgressMonitor());
				}
				ds.open();
				String ret = new String(DigestUtilities.getBase64Digest(ds));
				ds.cancel();
				return ret;
			} catch (NoSuchAlgorithmException e) {
				throw new DriverException(e);
			} catch (DataSourceCreationException e) {
				throw new DriverException(e);
			}
		}
	}

	public ArrayList<String> getSourceDependencies() throws DriverException {
		return new ArrayList<String>(0);
	}

	public int getType() {
		return getDriver().getType();
	}

	public void initialize() throws DriverException {
	}

}
