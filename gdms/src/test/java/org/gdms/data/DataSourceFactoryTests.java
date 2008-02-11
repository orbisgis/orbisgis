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

import java.io.File;

import org.gdms.SourceTest;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.data.indexes.SpatialIndex;
import org.gdms.data.indexes.SpatialIndexQuery;
import org.gdms.data.object.ObjectSourceDefinition;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.source.SourceManager;
import org.gdms.sql.strategies.TableNotFoundException;

import com.vividsolutions.jts.geom.Envelope;

public class DataSourceFactoryTests extends SourceTest {

	private SourceManager sm;

	@Override
	protected void setUp() throws Exception {
		sm = dsf.getSourceManager();
	}

	/**
	 * Tests the DataSource.remove method
	 *
	 * @throws RuntimeException
	 *             DOCUMENT ME!
	 */
	public void testRemoveDataSources() throws Exception {
		DataSource d = null;

		String dsName = super.getAnyNonSpatialResource();
		d = dsf.getDataSource(dsName);
		sm.remove(d.getName());

		try {
			d = dsf.getDataSource(dsName);
			assertTrue(false);
		} catch (NoSuchTableException e) {
		}
	}

	public void testRemoveWithSecondaryName() throws Exception {
		String dsName = super.getAnyNonSpatialResource();
		sm.addName(dsName, "newName");
		sm.remove("newName");
		assertTrue(sm.getSource(dsName) == null);
	}

	/**
	 * Tests the DataSourceFactory.removeAllDataSources method
	 *
	 * @throws Exception
	 */
	public void testRemoveAllDataSources() throws Exception {
		sm.removeAll();
		assertTrue(dsf.getSourceManager().isEmpty());
	}

	/**
	 * Tests the naming of operation layer datasource
	 *
	 * @throws Throwable
	 *             DOCUMENT ME!
	 */
	public void testOperationDataSourceName() throws Throwable {
		DataSource d = dsf.getDataSourceFromSQL("select * from "
				+ super.getAnyNonSpatialResource() + ";");
		assertTrue(dsf.getDataSource(d.getName()) != null);
	}

	public void testSeveralNames() throws Exception {
		String dsName = super.getAnyNonSpatialResource();
		testSeveralNames(dsName);
		testSeveralNames(dsf.getDataSourceFromSQL("select * from " + dsName).getName());
	}

	private void testSeveralNames(String dsName) throws TableNotFoundException,
			SourceAlreadyExistsException, DriverLoadException,
			NoSuchTableException, DataSourceCreationException, DriverException,
			AlreadyClosedException {
		String secondName = "secondName" + System.currentTimeMillis();
		sm.addName(dsName, secondName);
		checkNames(dsName, secondName);
		try {
			sm.addName("e" + System.currentTimeMillis(), "qosgsdq");
			assertTrue(false);
		} catch (TableNotFoundException e) {
		}
	}

	private void checkNames(String dsName, String secondName)
			throws DriverLoadException, NoSuchTableException,
			DataSourceCreationException, DriverException,
			AlreadyClosedException {
		assertTrue(dsf.getSourceManager().getSource(dsName) == dsf
				.getSourceManager().getSource(secondName));
		DataSource ds1 = dsf.getDataSource(dsName);
		DataSource ds2 = dsf.getDataSource(secondName);
		ds1.open();
		ds2.open();
		assertTrue(equals(getDataSourceContents(ds1),
				getDataSourceContents(ds2)));
		ds1.cancel();
		ds2.cancel();
	}

	public void testSecondNameCollidesWithName() throws Exception {
		String dsName1 = super.getAnyNonSpatialResource();
		String dsName2 = super.getAnySpatialResource();
		try {
			sm.addName(dsName1, dsName2);
			assertFalse(true);
		} catch (SourceAlreadyExistsException e) {
		}
	}

	public void testRegisteringCollission() throws Exception {
		String name = "e" + System.currentTimeMillis();
		ObjectSourceDefinition def = new ObjectSourceDefinition(
				new ObjectMemoryDriver(null, null));
		sm.register(name, def);
		try {
			sm.register(name, def);
			assertTrue(false);
		} catch (SourceAlreadyExistsException e) {
		}
	}

	public void testRenameFirstName() throws Exception {
		String dsName = super.getAnyNonSpatialResource();
		String newName = "test" + System.currentTimeMillis();
		String newName2 = "test" + System.currentTimeMillis() + 1;
		sm.addName(dsName, newName);
		sm.rename(dsName, newName2);
		checkNames(newName, newName2);
	}

	public void testRenameSecondName() throws Exception {
		String dsName = super.getAnyNonSpatialResource();
		String newName = "test" + System.currentTimeMillis();
		sm.addName(dsName, newName);
		String otherName = "test" + System.currentTimeMillis() + 1;
		sm.rename(newName, otherName);
		try {
			dsf.getDataSource(newName);
			assertTrue(false);
		} catch (NoSuchTableException e) {
		}
		checkNames(otherName, dsName);
	}

	public void testRenameFirstNameCollidesWithSecond() throws Exception {
		String dsName = super.getAnyNonSpatialResource();
		String newName = "test" + System.currentTimeMillis();
		sm.addName(dsName, newName);
		try {
			sm.rename(dsName, newName);
			assertTrue(false);
		} catch (SourceAlreadyExistsException e) {
		}
	}

	public void testRenameSecondNameCollidesWithFirst() throws Exception {
		String dsName = super.getAnyNonSpatialResource();
		String newName = "test" + System.currentTimeMillis();
		sm.addName(dsName, newName);
		try {
			sm.rename(newName, dsName);
			assertTrue(false);
		} catch (SourceAlreadyExistsException e) {
		}
	}

	public void testRemoveSourceRemovesAllNames() throws Exception {
		String dsName = super.getAnyNonSpatialResource();
		String secondName = "secondName" + System.currentTimeMillis();
		sm.addName(dsName, secondName);
		sm.remove(dsName);
		assertTrue(!sm.exists(secondName));
	}

	public void testSecondNameWorksWithIndexes() throws Exception {
		String dsName = super.getAnySpatialResource();
		String secondName = "secondName" + System.currentTimeMillis();
		sm.addName(dsName, secondName);
		String spatialFieldName = super.getSpatialFieldName(dsName);
		dsf.getIndexManager().buildIndex(dsName, spatialFieldName,
				SpatialIndex.SPATIAL_INDEX);
		SpatialIndexQuery query = new SpatialIndexQuery(
				new Envelope(0, 0, 0, 0), spatialFieldName);
		assertTrue(dsf.getIndexManager().getIndex(dsName, spatialFieldName) != null);
		assertTrue(dsf.getIndexManager().getIndexes(dsName) != null);
		assertTrue(dsf.getIndexManager().queryIndex(dsName, query) != null);
		assertTrue(dsf.getIndexManager().getIndex(secondName, spatialFieldName) != null);
		assertTrue(dsf.getIndexManager().getIndexes(secondName) != null);
		assertTrue(dsf.getIndexManager().queryIndex(secondName, query) != null);
	}

	public void testRemoveSecondaryName() throws Exception {
		String dsName = super.getAnySpatialResource();
		String secondName = "secondName" + System.currentTimeMillis();
		sm.addName(dsName, secondName);
		checkNames(dsName, secondName);
		sm.removeName(secondName);
		assertTrue(sm.getSource(secondName) == null);
	}

	public void testAddSecondNameRemoveAllAddSource() throws Exception {
		String dsName = super.getAnySpatialResource();
		String secondName = "secondName" + System.currentTimeMillis();
		sm.addName(dsName, secondName);
		sm.removeAll();
		sm.register(dsName, new FileSourceDefinition(new File("")));
		try {
			dsf.getDataSource(secondName);
			assertTrue(false);
		} catch (NoSuchTableException e) {
		}
	}

	public void testExistsSecondName() throws Exception {
		String dsName = super.getAnySpatialResource();
		String secondName = "secondName" + System.currentTimeMillis();
		sm.addName(dsName, secondName);
		assertTrue(sm.exists(secondName));
	}

	public void testWarningSystem() throws Exception {
		BasicWarningListener wl = new BasicWarningListener();
		dsf.setWarninglistener(wl);
		dsf.createDataSource(new FileSourceCreation(new File("my.shp"), null) {

			@Override
			public DataSourceDefinition create() throws DriverException {
				dsf.getWarningListener().throwWarning("Cannot add", null, null);
				return null;
			}
		});

		assertTrue(wl.warnings.size() == 1);
	}
}
