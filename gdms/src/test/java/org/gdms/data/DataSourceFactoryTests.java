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

import org.gdms.BaseTest;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.data.indexes.DefaultSpatialIndexQuery;
import org.gdms.data.indexes.IndexManager;
import org.gdms.data.indexes.SpatialIndexQuery;
import org.gdms.data.object.ObjectSourceDefinition;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.generic.GenericObjectDriver;
import org.gdms.source.SourceManager;

import com.vividsolutions.jts.geom.Envelope;
import java.io.FileFilter;
import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBSourceCreation;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.MetadataUtilities;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.ConstraintFactory;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.driver.driverManager.DriverManager;

public class DataSourceFactoryTests extends BaseTest {

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
				new GenericObjectDriver(null, null),"main");
		sm.register(name, def);
		try {
			sm.register(name, def);
			assertTrue(false);
		} catch (SourceAlreadyExistsException e) {
		}
	}

	public void testRenameFirstName() throws Exception {
		sm.register("test3", new File(BaseTest.internalData, "hedgerow.shp"));
		String newName = "test" + System.currentTimeMillis();
		String newName2 = "test" + System.currentTimeMillis() + 1;
		sm.addName("test3", newName);
		sm.rename("test3", newName2);
		checkNames(newName, newName2);
	}

	public void testRenameSecondName() throws Exception {
		sm.register("test4", new File(BaseTest.internalData, "hedgerow.shp"));
		String newName = "test" + System.currentTimeMillis();
		sm.addName("test4", newName);
		String otherName = "test" + System.currentTimeMillis() + 1;
		sm.rename(newName, otherName);
		try {
			dsf.getDataSource(newName);
			assertTrue(false);
		} catch (NoSuchTableException e) {
		}
		checkNames(otherName, "test4");
	}

	public void testRenameFirstNameCollidesWithSecond() throws Exception {
		sm.register("test5", new File(BaseTest.internalData, "hedgerow.shp"));
		String newName = "test" + System.currentTimeMillis();
		sm.addName("test5", newName);
		try {
			sm.rename("test5", newName);
			assertTrue(false);
		} catch (SourceAlreadyExistsException e) {
		}
	}

	public void testRenameSecondNameCollidesWithFirst() throws Exception {
		sm.register("test6", new File(BaseTest.internalData, "hedgerow.shp"));
		String newName = "test6_" + System.currentTimeMillis();
		sm.addName("test6", newName);
		try {
			sm.rename(newName, "test6");
			assertTrue(false);
		} catch (SourceAlreadyExistsException e) {
		}
	}

	public void testRemoveSourceRemovesAllNames() throws Exception {
		sm.register("test7", new File(BaseTest.internalData, "hedgerow.shp"));
		String secondName = "secondName" + System.currentTimeMillis();
		sm.addName("test7", secondName);
		sm.remove("test7");
		assertTrue(!sm.exists(secondName));
	}

	public void testSecondNameWorksWithIndexes() throws Exception {
                String dsName = "test8";
		sm.register(dsName, new File(BaseTest.internalData, "hedgerow.shp"));
		String secondName = "secondName" + System.currentTimeMillis();
                DataSource ds = dsf.getDataSource(dsName);
                ds.open();
                String spatialFieldName = ds.getMetadata().getFieldName(MetadataUtilities.getSpatialFieldIndex(ds.getMetadata()));
                ds.close();
		sm.addName(dsName, secondName);
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
		String dsName = "test9";
                sm.register(dsName, new File(BaseTest.internalData, "hedgerow.shp"));
		String secondName = "secondName" + System.currentTimeMillis();
		sm.addName(dsName, secondName);
		checkNames(dsName, secondName);
		sm.removeName(secondName);
		assertTrue(sm.getSource(secondName) == null);
	}

	public void testAddSecondNameRemoveAllAddSource() throws Exception {
		String dsName = "test10";
                sm.register(dsName, new File(BaseTest.internalData, "hedgerow.shp"));
		String secondName = "secondName" + System.currentTimeMillis();
		sm.addName(dsName, secondName);
		sm.removeAll();
		sm.register(dsName, new FileSourceDefinition(new File(BaseTest.internalData, "landcover2000.shp"), DriverManager.DEFAULT_SINGLE_TABLE_NAME));
		try {
			dsf.getDataSource(secondName);
			assertTrue(false);
		} catch (NoSuchTableException e) {
		}
	}

	public void testExistsSecondName() throws Exception {
		String dsName = "test11";
                sm.register(dsName, new File(BaseTest.internalData, "hedgerow.shp"));
		String secondName = "secondName" + System.currentTimeMillis();
		sm.addName(dsName, secondName);
		assertTrue(sm.exists(secondName));
	}

	public void testWarningSystem() throws Exception {
		BasicWarningListener wl = new BasicWarningListener();
		dsf.setWarninglistener(wl);
		dsf.createDataSource(new FileSourceCreation(new File("my.shp"), null) {

			@Override
			public DataSourceDefinition create(String name) throws DriverException {
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

	public void testCreationTableAlreadyExists() throws Exception {
		DefaultMetadata metadata = new DefaultMetadata();
		metadata.addField("mystr", TypeFactory.createType(Type.STRING,
				ConstraintFactory.createConstraint(Constraint.PK)));
		String file = BaseTest.backupDir + File.separator
				+ "tableAlreadyExists";
		File[] files = BaseTest.backupDir.listFiles(new FileFilter() {
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
		String filePath = BaseTest.backupDir + File.separator
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
					.getDataSource(new GenericObjectDriver(),"main"));
			assertTrue(false);
		} catch (IllegalArgumentException e) {
		}
	}

}
