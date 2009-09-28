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

import java.io.File;
import java.io.FileFilter;

import org.gdms.SourceTest;
import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBSourceCreation;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.data.indexes.DefaultSpatialIndexQuery;
import org.gdms.data.indexes.IndexManager;
import org.gdms.data.indexes.SpatialIndexQuery;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.object.ObjectSourceDefinition;
import org.gdms.data.types.PrimaryKeyConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
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
		testSeveralNames(dsf.getDataSourceFromSQL("select * from " + dsName)
				.getName());
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
		ds1.close();
		ds2.close();
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

	public void testChangeNameOnExistingDataSources() throws Exception {
		DataSourceFactory dsf = new DataSourceFactory();
		dsf.getSourceManager().removeAll();
		dsf.getSourceManager().register("file",
				new File("src/test/resources/test.csv"));
		DataSource ds = dsf.getDataSourceFromSQL("select * from file");
		dsf.getSourceManager().rename(ds.getName(), "sql");
		DataSource ds2 = dsf.getDataSource("sql");
		assertTrue(ds.getName() == ds2.getName());
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
				IndexManager.RTREE_SPATIAL_INDEX, null);
		SpatialIndexQuery query = new DefaultSpatialIndexQuery(new Envelope(0,
				0, 0, 0), spatialFieldName);
		assertTrue(dsf.getIndexManager().getIndex(dsName, spatialFieldName) != null);
		assertTrue(dsf.getIndexManager().getIndexedFieldNames(dsName) != null);
		assertTrue(dsf.getIndexManager().queryIndex(dsName, query) != null);
		assertTrue(dsf.getIndexManager().getIndex(secondName, spatialFieldName) != null);
		assertTrue(dsf.getIndexManager().getIndexedFieldNames(secondName) != null);
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

	public void testResultDirectory() throws Exception {
		File resultDir = new File("src/test/resources/temp");

		DataSourceFactory d = new DataSourceFactory();
		assertTrue(d.getTempDir().equals(d.getResultDir()));
		d.setResultDir(resultDir);
		assertTrue(d.getResultDir().equals(resultDir));

		d = new DataSourceFactory("src/test/resources/backup/sources");
		assertTrue(d.getTempDir().equals(d.getResultDir()));
		d.setResultDir(resultDir);
		assertTrue(d.getResultDir().equals(resultDir));

		d = new DataSourceFactory("src/test/resources/backup/sources",
				"src/test/resources/temp");
		assertTrue(d.getTempDir().equals(d.getResultDir()));
		d.setResultDir(resultDir);
		assertTrue(d.getResultDir().equals(resultDir));
	}

	public void testSQLSources() throws Exception {
		dsf.getSourceManager().register("sql",
				"select * from " + super.getAnyNonSpatialResource() + ";");
		DataSource ds = dsf.getDataSource("sql");
		assertTrue((ds.getSource().getType() & SourceManager.SQL) == SourceManager.SQL);
		assertTrue(ds.isEditable() == false);
	}

	public void testCreationTableAlreadyExists() throws Exception {
		DefaultMetadata metadata = new DefaultMetadata();
		metadata.addField("mystr", TypeFactory.createType(Type.STRING,
				new PrimaryKeyConstraint()));
		String file = SourceTest.backupDir + File.separator
				+ "tableAlreadyExists";
		File[] files = SourceTest.backupDir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getName().startsWith("tableAlreadyExists");
			}
		});
		for (File dbFile : files) {
			dbFile.delete();
		}
		DBSource source = new DBSource(null, -1, file, null, null, "testtable",
				"jdbc:h2");
		DBSourceCreation sc = new DBSourceCreation(source, metadata);

		dsf.createDataSource(sc);
		try {
			dsf.createDataSource(sc);
			assertTrue(false);
		} catch (DriverException e) {
		}
	}

	public void testCreationFileAlreadyExists() throws Exception {
		DefaultMetadata metadata = new DefaultMetadata();
		metadata.addField("mystr", TypeFactory.createType(Type.STRING));
		String filePath = SourceTest.backupDir + File.separator
				+ "fileAlreadyExists.gdms";
		File file = new File(filePath);
		file.delete();
		FileSourceCreation sc = new FileSourceCreation(file, metadata);

		dsf.createDataSource(sc);
		try {
			dsf.createDataSource(sc);
			assertTrue(false);
		} catch (DriverException e) {
		}
	}

	public void testCreationNotRegisteredSource() throws Exception {
		try {
			dsf.saveContents("notexists", dsf
					.getDataSource(new ObjectMemoryDriver()));
			assertTrue(false);
		} catch (IllegalArgumentException e) {
		}
	}

}
