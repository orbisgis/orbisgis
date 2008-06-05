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
package org.gdms.data.object;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.data.DriverDataSource;
import org.gdms.data.FreeingResourcesException;
import org.gdms.data.edition.Commiter;
import org.gdms.data.edition.DeleteEditionInfo;
import org.gdms.data.edition.EditionInfo;
import org.gdms.data.edition.PhysicalDirection;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.ObjectReadWriteDriver;
import org.gdms.source.Source;
import org.orbisgis.progress.NullProgressMonitor;

public class ObjectDataSourceAdapter extends DriverDataSource implements
		Commiter {

	private ObjectDriver driver;

	public ObjectDataSourceAdapter(Source source, ObjectDriver driver) {
		super(source);
		this.driver = driver;
	}

	public void open() throws DriverException {
		driver.start();
	}

	public void commit() throws DriverException, FreeingResourcesException {
	}

	public void cancel() throws DriverException, AlreadyClosedException {
		driver.stop();
	}

	public void saveData(DataSource ds) throws DriverException {
		ds.open();
		((ObjectReadWriteDriver) driver).write(ds, new NullProgressMonitor());
		ds.cancel();
	}

	public ObjectDriver getDriver() {
		return driver;
	}

	public long[] getWhereFilter() throws IOException {
		return null;
	}

	public void commit(List<PhysicalDirection> rowsDirections,
			String[] fieldName, ArrayList<EditionInfo> schemaActions,
			ArrayList<EditionInfo> editionActions,
			ArrayList<DeleteEditionInfo> deletedPKs, DataSource modifiedSource)
			throws DriverException, FreeingResourcesException {
		((ObjectReadWriteDriver) driver).write(modifiedSource,
				new NullProgressMonitor());
		try {
			driver.stop();
		} catch (DriverException e) {
			throw new FreeingResourcesException(e);
		}
	}
}