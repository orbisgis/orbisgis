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

import org.gdms.data.types.PrimaryKeyConstraint;
import java.io.File;

import org.gdms.TestBase;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.data.indexes.DefaultSpatialIndexQuery;
import org.gdms.data.indexes.IndexManager;
import org.gdms.data.indexes.SpatialIndexQuery;
import org.gdms.data.memory.MemorySourceDefinition;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.MemoryDataSetDriver;
import org.gdms.source.SourceManager;

import com.vividsolutions.jts.geom.Envelope;
import java.io.FileFilter;
import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBSourceCreation;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.MetadataUtilities;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.driver.driverManager.DriverManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DataSourceFactoryTests extends TestBase {

        private SourceManager sm;

        @Before
        public void setUp() throws Exception {
                sm = dsf.getSourceManager();
        }

        /**
         * Tests the DataSource.remove method
         *
         * @throws RuntimeException
         *             DOCUMENT ME!
         */
        @Test(expected = NoSuchTableException.class)
        public void testRemoveDataSources() throws Exception {
                DataSource d = null;

                String dsName = super.getAnyNonSpatialResource();
                d = dsf.getDataSource(dsName);
                sm.remove(d.getName());

                d = dsf.getDataSource(dsName);
        }

        @Test
        public void testRemoveWithSecondaryName() throws Exception {
                String dsName = super.getAnyNonSpatialResource();
                sm.addName(dsName, "newName");
                sm.remove("newName");
                assertNull(sm.getSource(dsName));
        }

        /**
         * Tests the DataSourceFactory.removeAllDataSources method
         *
         * @throws Exception
         */
        @Test
        public void testRemoveAllDataSources() throws Exception {
                sm.removeAll();
                assertTrue(dsf.getSourceManager().isEmpty());
        }
        
        @Test
        public void testOperationDataSourceName() throws Throwable {
                dsf.getSourceManager().remove("big");
                dsf.getSourceManager().register(
                        "big",
                        new FileSourceCreation(new File(TestBase.internalData
                        + "hedgerow.shp"), null));
                DataSource d = dsf.getDataSourceFromSQL("select * from big;");
                assertNotNull(dsf.getDataSource(d.getName()));
        }

        @Test
        public void testSeveralNames() throws Exception {
                String dsName = super.getNonDBSmallResources()[0];
                testSeveralNames(dsName);
                testSeveralNames(dsf.getDataSourceFromSQL("select * from " + dsName + ";").getName());
        }

        private void testSeveralNames(String dsName) throws
                SourceAlreadyExistsException, DriverLoadException,
                DataSourceCreationException, DriverException,
                AlreadyClosedException, NoSuchTableException {
                String secondName = "secondName" + System.currentTimeMillis();
                sm.addName(dsName, secondName);
                checkNames(dsName, secondName);
                try {
                        sm.addName("e" + System.currentTimeMillis(), "qosgsdq");
                        fail();
                } catch (NoSuchTableException ex) {
                }

        }

        private void checkNames(String dsName, String secondName)
                throws DriverLoadException, NoSuchTableException,
                DataSourceCreationException, DriverException,
                AlreadyClosedException {
                assertEquals(dsf.getSourceManager().getSource(dsName),dsf.getSourceManager().getSource(secondName));
                DataSource ds1 = dsf.getDataSource(dsName);
                DataSource ds2 = dsf.getDataSource(secondName);
                ds1.open();
                ds2.open();
                assertTrue(equals(getDataSourceContents(ds1),
                        getDataSourceContents(ds2)));
                ds1.close();
                ds2.close();
        }
        
        @Test
        public void testChangeNameOnExistingDataSources() throws Exception {
                DataSourceFactory dsf = new DataSourceFactory();
                dsf.setTempDir(TestBase.backupDir.getAbsolutePath());
                dsf.setResultDir(TestBase.backupDir);
                dsf.getSourceManager().removeAll();
                dsf.getSourceManager().register("file",
                        new File(TestBase.internalData, "test.csv"));
                DataSource ds = dsf.getDataSourceFromSQL("select * from file;");
                dsf.getSourceManager().rename(ds.getName(), "sql");
                DataSource ds2 = dsf.getDataSource("sql");
                assertEquals(ds.getName(), ds2.getName());
        }

        @Test
        public void testSQLSources() throws Exception {
                dsf.getSourceManager().register("testH", new File(TestBase.internalData, "hedgerow.shp"));
                dsf.register("sql",
                        "select * from testH;");
                DataSource ds = dsf.getDataSource("sql");
                assertEquals((ds.getSource().getType() & SourceManager.SQL), SourceManager.SQL);
                assertFalse(ds.isEditable());
        }

        @Test(expected = SourceAlreadyExistsException.class)
        public void testSecondNameCollidesWithName() throws Exception {
                String dsName1 = super.getAnyNonSpatialResource();
                String dsName2 = super.getAnySpatialResource();

                sm.addName(dsName1, dsName2);
        }

        @Test(expected = SourceAlreadyExistsException.class)
        public void testRegisteringCollission() throws Exception {
                String name = "e" + System.currentTimeMillis();
                MemorySourceDefinition def = new MemorySourceDefinition(
                        new MemoryDataSetDriver(null, null), "main");
                sm.register(name, def);

                sm.register(name, def);
        }

        @Test
        public void testRenameFirstName() throws Exception {
                sm.register("test3", new File(TestBase.internalData, "hedgerow.shp"));
                String newName = "test" + System.currentTimeMillis();
                String newName2 = "test" + System.currentTimeMillis() + 1;
                sm.addName("test3", newName);
                sm.rename("test3", newName2);
                checkNames(newName, newName2);
        }

        @Test
        public void testRenameSecondName() throws Exception {
                sm.register("test4", new File(TestBase.internalData, "hedgerow.shp"));
                String newName = "test" + System.currentTimeMillis();
                sm.addName("test4", newName);
                String otherName = "test" + System.currentTimeMillis() + 1;
                sm.rename(newName, otherName);
                try {
                        dsf.getDataSource(newName);
                        fail();
                } catch (NoSuchTableException e) {
                }
                checkNames(otherName, "test4");
        }

        @Test(expected = SourceAlreadyExistsException.class)
        public void testRenameFirstNameCollidesWithSecond() throws Exception {
                sm.register("test5", new File(TestBase.internalData, "hedgerow.shp"));
                String newName = "test" + System.currentTimeMillis();
                sm.addName("test5", newName);

                sm.rename("test5", newName);
        }

        @Test(expected = SourceAlreadyExistsException.class)
        public void testRenameSecondNameCollidesWithFirst() throws Exception {
                sm.register("test6", new File(TestBase.internalData, "hedgerow.shp"));
                String newName = "test6_" + System.currentTimeMillis();
                sm.addName("test6", newName);

                sm.rename(newName, "test6");
        }

        @Test
        public void testRemoveSourceRemovesAllNames() throws Exception {
                sm.register("test7", new File(TestBase.internalData, "hedgerow.shp"));
                String secondName = "secondName" + System.currentTimeMillis();
                sm.addName("test7", secondName);
                sm.remove("test7");
                assertFalse(sm.exists(secondName));
        }

        @Test
        public void testSecondNameWorksWithIndexes() throws Exception {
                String dsName = "test8";
                sm.register(dsName, new File(TestBase.internalData, "hedgerow.shp"));
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
                assertNotNull(dsf.getIndexManager().getIndex(dsName, spatialFieldName));
                assertNotNull(dsf.getIndexManager().getIndexedFieldNames(dsName));
                assertNotNull(dsf.getIndexManager().queryIndex(dsName, query));
                assertNotNull(dsf.getIndexManager().getIndex(secondName, spatialFieldName));
                assertNotNull(dsf.getIndexManager().getIndexedFieldNames(secondName));
                assertNotNull(dsf.getIndexManager().queryIndex(secondName, query));
        }

        @Test
        public void testRemoveSecondaryName() throws Exception {
                String dsName = "test9";
                sm.register(dsName, new File(TestBase.internalData, "hedgerow.shp"));
                String secondName = "secondName" + System.currentTimeMillis();
                sm.addName(dsName, secondName);
                checkNames(dsName, secondName);
                sm.removeName(secondName);
                assertNull(sm.getSource(secondName));
        }

        @Test(expected = NoSuchTableException.class)
        public void testAddSecondNameRemoveAllAddSource() throws Exception {
                String dsName = "test10";
                sm.register(dsName, new File(TestBase.internalData, "hedgerow.shp"));
                String secondName = "secondName" + System.currentTimeMillis();
                sm.addName(dsName, secondName);
                sm.removeAll();
                sm.register(dsName, new FileSourceDefinition(new File(TestBase.internalData, "landcover2000.shp"), DriverManager.DEFAULT_SINGLE_TABLE_NAME));

                dsf.getDataSource(secondName);
        }

        @Test
        public void testExistsSecondName() throws Exception {
                String dsName = "test11";
                sm.register(dsName, new File(TestBase.internalData, "hedgerow.shp"));
                String secondName = "secondName" + System.currentTimeMillis();
                sm.addName(dsName, secondName);
                assertTrue(sm.exists(secondName));
        }

        @Test
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

                assertEquals(wl.warnings.size(), 1);
        }

        @Test
        public void testResultDirectory() throws Exception {
                File resultDir = new File("src/test/resources/temp");

                DataSourceFactory d = new DataSourceFactory();
                assertEquals(d.getTempDir(), d.getResultDir());
                d.setResultDir(resultDir);
                assertEquals(d.getResultDir(), resultDir);

                d = new DataSourceFactory(TestBase.backupDir + "sources");
                assertEquals(d.getTempDir(), d.getResultDir());
                d.setResultDir(resultDir);
                assertEquals(d.getResultDir(), resultDir);

                d = new DataSourceFactory(TestBase.backupDir + "sources",
                        "src/test/resources/temp");
                assertEquals(d.getTempDir(), d.getResultDir());
                d.setResultDir(resultDir);
                assertEquals(d.getResultDir(), resultDir);
        }

        @Test
        public void testCreationTableAlreadyExists() throws Exception {
                DefaultMetadata metadata = new DefaultMetadata();
                metadata.addField("mystr", TypeFactory.createType(Type.STRING,
                        new PrimaryKeyConstraint()));
                String file = TestBase.backupDir + File.separator
                        + "tableAlreadyExists";
                File[] files = TestBase.backupDir.listFiles(new FileFilter() {

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
                        fail();
                } catch (DriverException e) {
                }
        }

        @Test
        public void testCreationFileAlreadyExists() throws Exception {
                DefaultMetadata metadata = new DefaultMetadata();
                metadata.addField("mystr", TypeFactory.createType(Type.STRING));
                String filePath = TestBase.backupDir + File.separator
                        + "fileAlreadyExists.gdms";
                File file = new File(filePath);
                file.delete();
                FileSourceCreation sc = new FileSourceCreation(file, metadata);

                dsf.createDataSource(sc);
                try {
                        dsf.createDataSource(sc);
                        fail();
                } catch (DriverException e) {
                }
        }

        @Test(expected = IllegalArgumentException.class)
        public void testCreationNotRegisteredSource() throws Exception {
                dsf.saveContents("notexists", dsf.getDataSource(new MemoryDataSetDriver(), "main"));
        }
}
