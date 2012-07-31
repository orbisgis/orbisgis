/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
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

import java.io.File;

import com.vividsolutions.jts.geom.Envelope;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import org.gdms.TestBase;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.indexes.DefaultSpatialIndexQuery;
import org.gdms.data.indexes.IndexManager;
import org.gdms.data.indexes.SpatialIndexQuery;
import org.gdms.data.memory.MemorySourceDefinition;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.MetadataUtilities;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.MemoryDataSetDriver;
import org.gdms.source.SourceManager;

public class DataSourceFactoryTest extends TestBase {

        @Before
        public void setUp() throws Exception {
                super.setUpTestsWithEdition(false);
        }

        @Test
        public void testRemoveDataSources() throws Exception {
                sm.register("temp", super.getAnyNonSpatialResource());


                DataSource d = dsf.getDataSource("temp");
                sm.remove(d.getName());

                try {
                        dsf.getDataSource("temp");
                        fail();
                } catch (NoSuchTableException e) {
                }
        }

        @Test
        public void testRemoveWithSecondaryName() throws Exception {
                sm.register("temp", super.getAnyNonSpatialResource());

                sm.addName("temp", "newName");
                sm.remove("newName");
                assertNull(sm.getSource("temp"));
        }

        @Test
        public void testRemoveAllDataSources() throws Exception {
                sm.register("temp", super.getAnyNonSpatialResource());

                sm.removeAll();
                assertArrayEquals(new String[]{"spatial_ref_table"}, sm.getSourceNames());
        }

        @Test
        public void testOperationDataSourceName() throws Throwable {
                sm.register("temp", super.getAnyNonSpatialResource());
                DataSource d = dsf.getDataSourceFromSQL("select * from temp;");
                assertNotNull(dsf.getDataSource(d.getName()));
        }

        @Test
        public void testSeveralNames() throws Exception {
                sm.register("temp", super.getAnyNonSpatialResource());
                String dsName = "temp";
                testSeveralNames(dsName);
                testSeveralNames(dsf.getDataSourceFromSQL("select * from " + dsName + ";").getName());
        }

        private void testSeveralNames(String dsName) throws
                SourceAlreadyExistsException, DriverLoadException,
                DataSourceCreationException, DriverException,
                AlreadyClosedException, NoSuchTableException {
                String secondName = "secondName";
                sm.addName(dsName, secondName);
                checkNames(dsName, secondName);
                try {
                        sm.addName("e", "qosgsdq");
                        fail();
                } catch (NoSuchTableException ex) {
                }
                sm.removeName(secondName);
        }

        private void checkNames(String dsName, String secondName)
                throws DriverLoadException, NoSuchTableException,
                DataSourceCreationException, DriverException,
                AlreadyClosedException {
                assertEquals(sm.getSource(dsName), sm.getSource(secondName));
                DataSource ds1 = dsf.getDataSource(dsName);
                DataSource ds2 = dsf.getDataSource(secondName);
                ds1.open();
                Value[][] ds1c = getDataSourceContents(ds1);
                ds1.close();
                ds2.open();
                Value[][] ds2c = getDataSourceContents(ds2);
                ds2.close();
                assertTrue(equals(ds1c, ds2c));
        }

        @Test
        public void testChangeNameOnExistingDataSources() throws Exception {
                sm.register("file", super.getAnyNonSpatialResource());
                DataSource ds = dsf.getDataSourceFromSQL("select * from file;");
                sm.rename(ds.getName(), "sql");
                DataSource ds2 = dsf.getDataSource("sql");
                assertEquals(ds.getName(), ds2.getName());
        }

        @Test
        public void testSQLSources() throws Exception {
                sm.register("testH", super.getAnyNonSpatialResource());
                sm.register("sql", "select * from testH;");
                DataSource ds = dsf.getDataSource("sql");
                assertEquals((ds.getSource().getType() & SourceManager.SQL), SourceManager.SQL);
                assertFalse(ds.isEditable());
        }

        @Test(expected = SourceAlreadyExistsException.class)
        public void testSecondNameCollidesWithName() throws Exception {
                sm.register("file", super.getAnyNonSpatialResource());
                sm.register("shp", super.getAnySpatialResource());

                sm.addName("file", "shp");
        }

        @Test(expected = SourceAlreadyExistsException.class)
        public void testRegisteringCollission() throws Exception {
                MemorySourceDefinition def = new MemorySourceDefinition(
                        new MemoryDataSetDriver(null, null), "main");
                sm.register("test", def);

                sm.register("test", def);
        }

        @Test
        public void testRenameFirstName() throws Exception {
                sm.register("test3", super.getAnyNonSpatialResource());
                String newName = "test2";
                String newName2 = "test";
                sm.addName("test3", newName);
                sm.rename("test3", newName2);
                checkNames(newName, newName2);
        }

        @Test
        public void testRenameSecondName() throws Exception {
                sm.register("test", super.getAnyNonSpatialResource());
                String newName = "test2";
                sm.addName("test", newName);
                String otherName = "test4";
                sm.rename(newName, otherName);
                try {
                        dsf.getDataSource(newName);
                        fail();
                } catch (NoSuchTableException e) {
                }
                checkNames(otherName, "test");
        }

        @Test(expected = SourceAlreadyExistsException.class)
        public void testRenameFirstNameCollidesWithSecond() throws Exception {
                sm.register("test5", super.getAnyNonSpatialResource());
                String newName = "test";
                sm.addName("test5", newName);

                sm.rename("test5", newName);
        }

        @Test(expected = SourceAlreadyExistsException.class)
        public void testRenameSecondNameCollidesWithFirst() throws Exception {
                sm.register("test6", super.getAnyNonSpatialResource());
                String newName = "test";
                sm.addName("test6", newName);

                sm.rename(newName, "test6");
        }

        @Test
        public void testRemoveSourceRemovesAllNames() throws Exception {
                sm.register("test7", super.getAnyNonSpatialResource());
                String secondName = "secondName";
                sm.addName("test7", secondName);
                sm.remove("test7");
                assertFalse(sm.exists(secondName));
        }

        @Test
        public void testSecondNameWorksWithIndexes() throws Exception {
                String dsName = "test8";
                sm.register(dsName, super.getAnySpatialResource());
                String secondName = "secondName";
                DataSource ds = dsf.getDataSource(dsName);
                ds.open();
                String spatialFieldName = ds.getMetadata().getFieldName(MetadataUtilities.getSpatialFieldIndex(ds.getMetadata()));
                ds.close();
                sm.addName(dsName, secondName);
                dsf.getIndexManager().buildIndex(dsName, spatialFieldName,
                        IndexManager.RTREE_SPATIAL_INDEX, null);
                SpatialIndexQuery query = new DefaultSpatialIndexQuery(spatialFieldName,
                        new Envelope(0, 0, 0, 0));
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
                sm.register(dsName, super.getAnyNonSpatialResource());
                String secondName = "secondName";
                sm.addName(dsName, secondName);
                checkNames(dsName, secondName);
                sm.removeName(secondName);
                assertNull(sm.getSource(secondName));
        }

        @Test(expected = NoSuchTableException.class)
        public void testAddSecondNameRemoveAllAddSource() throws Exception {
                String dsName = "test10";
                sm.register(dsName, super.getAnyNonSpatialResource());
                String secondName = "secondName";
                sm.addName(dsName, secondName);
                sm.removeAll();
                sm.register(dsName, super.getAnySpatialResource());

                dsf.getDataSource(secondName);
        }

        @Test
        public void testExistsSecondName() throws Exception {
                String dsName = "test11";
                sm.register(dsName, super.getAnyNonSpatialResource());
                String secondName = "secondName";
                sm.addName(dsName, secondName);
                assertTrue(sm.exists(secondName));
        }

        @Test
        public void testResultDirectory() throws Exception {
                File resultDir = new File(currentWorkspace, "someresultdir");

                DataSourceFactory d = new DataSourceFactory();
                assertEquals(d.getTempDir(), d.getResultDir());
                d.setResultDir(resultDir);
                assertEquals(d.getResultDir(), resultDir);

                d = new DataSourceFactory(currentWorkspace.getAbsolutePath() + "/sources");
                assertEquals(d.getTempDir(), d.getResultDir());
                d.setResultDir(resultDir);
                assertEquals(d.getResultDir(), resultDir);

                d = new DataSourceFactory(currentWorkspace.getAbsolutePath() + "/sources",
                        currentWorkspace.getAbsolutePath() + "/anytemp/");
                assertEquals(d.getTempDir(), d.getResultDir());
                d.setResultDir(resultDir);
                assertEquals(d.getResultDir(), resultDir);
        }

        @Test
        public void testCreationFileAlreadyExists() throws Exception {
                DefaultMetadata metadata = new DefaultMetadata();
                metadata.addField("mystr", TypeFactory.createType(Type.STRING));
                File file = File.createTempFile("temp", ".gdms", currentWorkspace);
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
