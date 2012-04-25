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
package org.gdms.source;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import org.gdms.DBTestSource;
import org.gdms.TestBase;
import org.gdms.TestResourceHandler;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.SourceAlreadyExistsException;
import org.gdms.data.db.DBSource;
import org.gdms.data.edition.FakeDBTableSourceDefinition;
import org.gdms.data.edition.FakeFileSourceDefinition;
import org.gdms.data.edition.ReadAndWriteDriver;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.memory.MemorySourceDefinition;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.wms.WMSSource;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileDriverRegister;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.driver.memory.MemoryDataSetDriver;
import org.gdms.sql.strategies.SumQuery;

public class SourceManagementTest extends TestBase {

        private static final String SOURCE = "source";
        private static final String SOURCEMOD = "sourcd";
        private File testFile;
        private DBSource testDB;
        private WMSSource testWMS;
        private MemoryDataSetDriver obj;
        private String sql = "select count(the_geom) from myfile;";

        @Test
        public void testRegisterTwice() throws Exception {
                sm.register(SOURCE, getTempFile(".shp"));
                try {
                        sm.register(SOURCE, getTempFile(".shp"));
                        fail();
                } catch (SourceAlreadyExistsException e) {
                        // we check that the failed registration has broken nothing
                        sm.remove(SOURCE);
                        sm.register(SOURCE, testFile);
                        DataSource ds = dsf.getDataSource(SOURCE);
                        ds.open();
                        ds.close();
                }
        }

        @Test
        public void testRegisterAndRemove() throws Exception {
                File file = getTempFile(".shp");
                sm.register(SOURCE, file);
                sm.remove(SOURCE);
                FileDriverRegister fdr = sm.getDriverManager().getFileDriverRegister();
                assertFalse(fdr.contains(file));
        }

        @Test
        public void testRemoveAll() throws Exception {
                sm.register(SOURCE, testFile);

                Source src = sm.getSource(SOURCE);
                associateFile(src, "statisticsFile");
                associateString(src, "statistics");

                sm.removeAll();
                sm.register(SOURCE, testFile);
                src = sm.getSource(SOURCE);
                assertEquals(src.getStringPropertyNames().length, 0);
                assertEquals(src.getFilePropertyNames().length, 0);
        }

        @Test
        public void testRemoveFileProperty() throws Exception {
                sm.register(SOURCE, testFile);
                Source source = sm.getSource(SOURCE);
                String fileProp = "testFileProp";
                associateFile(source, fileProp);
                source.deleteProperty(fileProp);

                File dir = sm.getSourceInfoDirectory();
                File[] content = dir.listFiles(new FilenameFilter() {

                        @Override
                        public boolean accept(File dir, String name) {
                                return !name.startsWith(".");
                        }
                });
                assertEquals(content.length, 1);
                assertEquals(content[0].getName(), "directory.xml");
        }

        @Test
        public void testOverrideFileProperty() throws Exception {
                sm.register(SOURCE, testFile);
                Source source = sm.getSource(SOURCE);
                String fileProp = "testFileProp";
                associateFile(source, fileProp);

                File file = source.createFileProperty(fileProp);
                FileOutputStream fis = new FileOutputStream(file);
                fis.write("newcontent".getBytes());
                fis.close();

                source.deleteProperty(fileProp);
                File dir = sm.getSourceInfoDirectory();
                File[] content = dir.listFiles(new FilenameFilter() {

                        @Override
                        public boolean accept(File dir, String name) {
                                return !name.startsWith(".");
                        }
                });
                assertEquals(content.length, 1);
                assertEquals(content[0].getName(), "directory.xml");
        }

        @Test
        public void testRemoveStringProperty() throws Exception {
                sm.register(SOURCE, testFile);
                Source source = sm.getSource(SOURCE);
                String stringProp = "testFileProp";
                associateString(source, stringProp);
                source.deleteProperty(stringProp);

                assertEquals(source.getStringPropertyNames().length, 0);
                assertEquals(source.getFilePropertyNames().length, 0);
        }

        @Test
        public void testAssociateFile() throws Exception {
                sm.register(SOURCE, testFile);
                String statistics = "statistics";
                Source source = sm.getSource(SOURCE);
                String rcStr = associateFile(source, statistics);

                assertEquals(sm.getSource(SOURCE).getFilePropertyNames().length, 1);

                sm.saveStatus();

                sm.removeAll();
                sm.register(SOURCE, testFile);

                assertEquals(sm.getSource(SOURCE).getFilePropertyNames().length, 1);

                String statsContent = source.getFilePropertyContentsAsString(statistics);
                assertEquals(statsContent, rcStr);

                File f = source.getFileProperty(statistics);
                DataInputStream dis = new DataInputStream(new FileInputStream(f));
                byte[] content = new byte[dis.available()];
                dis.readFully(content);
                assertEquals(new String(content), rcStr);
        }

        private String associateFile(Source source, String propertyName)
                throws Exception {
                if (source.hasProperty(propertyName)) {
                        source.deleteProperty(propertyName);
                }
                File stats = source.createFileProperty(propertyName);
                DataSource ds = dsf.getDataSource(source.getName());
                ds.open();
                long rc = ds.getRowCount();
                ds.close();

                FileOutputStream fis = new FileOutputStream(stats);
                String rcStr = Long.toString(rc);
                fis.write(rcStr.getBytes());
                fis.close();

                return rcStr;
        }

        @Test
        public void testAssociateStringProperty() throws Exception {
                sm.register(SOURCE, testFile);
                Source source = sm.getSource(SOURCE);
                String statistics = "statistics";
                String rcStr = associateString(source, statistics);

                assertEquals(sm.getSource(SOURCE).getStringPropertyNames().length, 1);

                sm.saveStatus();

                sm.removeAll();
                sm.register(SOURCE, testFile);

                assertEquals(sm.getSource(SOURCE).getStringPropertyNames().length, 1);

                String statsContent = source.getProperty(statistics);
                assertEquals(statsContent, rcStr);
        }

        private String associateString(Source source, String propertyName)
                throws Exception {
                DataSource ds = dsf.getDataSource(SOURCE);
                ds.open();
                long rc = ds.getRowCount();
                ds.close();

                String rcStr = Long.toString(rc);
                source.putProperty(propertyName, rcStr);
                return rcStr;
        }

        @Test
        public void testKeepPropertiesAfterRenaming() throws Exception {
                sm.register(SOURCE, testFile);
                Source source = sm.getSource(SOURCE);

                associateString(source, "test");
                associateFile(source, "testfile");
                assertEquals(sm.getSource(SOURCE).getFilePropertyNames().length, 1);
                assertEquals(sm.getSource(SOURCE).getStringPropertyNames().length, 1);

                String memento = sm.getMemento();

                sm.rename(SOURCE, SOURCEMOD);

                assertTrue(memento.length() > SOURCE.length() + 2);
                assertEquals(memento.substring(SOURCE.length() + 2),
                        sm.getMemento().substring(SOURCEMOD.length() + 2));

        }

        @Test
        public void testReturnNullWhenNoProperty() throws Exception {
                sm.register(SOURCE, testFile);
                Source source = sm.getSource(SOURCE);
                assertNull(source.getFileProperty("skjbnskb"));
                assertNull(source.getProperty("skjbnskb"));
        }

        @Test
        public void testMoveAndChangeSourceDirectory() throws Exception {
                sm.register(SOURCE, testFile);

                String statistics = "statistics";
                Source source = sm.getSource(SOURCE);
                associateFile(source, statistics);
                associateString(source, statistics);
                String memento = sm.getMemento();

                String newSourceInfoDir = TestResourceHandler.getNewSandBox(false).getAbsolutePath();
                sm.setSourceInfoDirectory(newSourceInfoDir);

                memento = sm.getMemento();
                sm.changeSourceInfoDirectory(newSourceInfoDir);
                assertEquals(memento, sm.getMemento());
        }

        @Test
        public void testSameSourceSameDSInstance() throws Exception {
                sm.register(SOURCE, testFile);

                DataSource ds1 = dsf.getDataSource(SOURCE, DataSourceFactory.NORMAL);
                DataSource ds2 = dsf.getDataSource(SOURCE, DataSourceFactory.NORMAL);
                ds1.open();
                assertTrue(ds2.isOpen());
                ds2.close();
        }

        @Test
        public void testPersistence() throws Exception {
                sm.register("myfile", testFile);
                if (hsqlDbAvailable) {
                        sm.register("db", testDB);
                }
                sm.register("wms", testWMS);
                sm.register("obj", obj);

                String fileContent = getContent("myfile");
                String dbContent = hsqlDbAvailable ? getContent("db") : null;
                String wmsContent = getContent("wms");
                String objContent = getContent("obj");

                sm.saveStatus();

                assertEquals(fileContent, getContent("myfile"));
                if (hsqlDbAvailable) {
                        assertEquals(dbContent, getContent("db"));
                }
                assertEquals(wmsContent, getContent("wms"));
                assertEquals(objContent, getContent("obj"));

                sm.removeAll();
                sm.register("myfile", super.getAnyNonSpatialResource());
                dsf.register("sql", sql);

                String sqlContent = getContent("sql");

                sm.saveStatus();

                assertEquals(sqlContent, getContent("sql"));
        }

        private String getContent(String name) throws Exception {
                DataSource ds = dsf.getDataSource(name);
                ds.open();
                String ret = ds.getAsString();
                ds.close();

                return ret;
        }

        @Test
        public void testSelectDependencies() throws Exception {
                Assume.assumeTrue(hsqlDbAvailable);

                sm.register("db", testDB);
                sm.register("file", testFile);
                String sql = "select 2*(file.id :: int) from db, file "
                        + "where (file.id :: int) <> 234;";
                dsf.register("sql", sql);
                DataSource ds = dsf.getDataSource("sql");
                assertTrue(setIs(ds.getReferencedSources(),
                        new String[]{"db", "file"}));
                ds = dsf.getDataSourceFromSQL(sql);
                assertTrue(setIs(ds.getReferencedSources(),
                        new String[]{"db", "file"}));
                sql = "file union file;";
                dsf.register("sql2", sql);
                ds = dsf.getDataSource("sql2");
                assertTrue(setIs(ds.getReferencedSources(), new String[]{"file"}));
                ds = dsf.getDataSourceFromSQL(sql);
                assertTrue(setIs(ds.getReferencedSources(), new String[]{"file"}));

                String[] srcDeps = dsf.getDataSource("file").getReferencedSources();
                assertEquals(srcDeps.length, 0);
        }

        private boolean setIs(String[] referencingSources, String[] test) {
                if (referencingSources.length != test.length) {
                        return false;
                } else {
                        ArrayList<String> set = new ArrayList<String>();
                        for (String string : referencingSources) {
                                set.add(string);
                        }
                        for (String string : test) {
                                set.remove(string);
                        }

                        return set.isEmpty();
                }
        }

        @Test
        public void testObjectDriverType() throws Exception {
                MemoryDataSetDriver driver = new MemoryDataSetDriver(new String[]{"pk",
                                "geom"}, new Type[]{TypeFactory.createType(Type.INT),
                                TypeFactory.createType(Type.GEOMETRY)});
                sm.register("spatial", driver);
                Source src = sm.getSource("spatial");
                assertEquals((src.getType() & SourceManager.MEMORY), SourceManager.MEMORY);
                assertEquals((src.getType() & SourceManager.VECTORIAL), SourceManager.VECTORIAL);
                driver = new MemoryDataSetDriver(new String[]{"pk"},
                        new Type[]{TypeFactory.createType(Type.INT)});
                sm.register("alpha", driver);
                src = sm.getSource("alpha");
                assertEquals((src.getType() & SourceManager.MEMORY), SourceManager.MEMORY);
                assertEquals((src.getType() & SourceManager.VECTORIAL), 0);
        }

        @Test
        public void testGetAlreadyRegisteredSourceAnonimously() throws Exception {
                sm.register("myfile", testFile);
                if (hsqlDbAvailable) {
                        sm.register("myDB", testDB);
                }
                sm.register("myWMS", testWMS);
                sm.register("myObj", obj);

                DataSource ds = dsf.getDataSource(testFile);
                assertEquals(ds.getName(), "myfile");

                if (hsqlDbAvailable) {
                        ds = dsf.getDataSource(testDB);
                        assertEquals(ds.getName(), "myDB");
                }

                ds = dsf.getDataSource(testWMS);
                assertEquals(ds.getName(), "myWMS");

                ds = dsf.getDataSource(obj, "main");
                assertEquals(ds.getName(), "myObj");

                sm.removeAll();
                sm.register("myfile", super.getAnySpatialResource());
                dsf.register("mySQL", sql);


                ds = dsf.getDataSourceFromSQL(sql);
                assertEquals(ds.getName(), "mySQL");
        }

        @Test
        public void testCannotDeleteDependedSource() throws Exception {
                Assume.assumeTrue(hsqlDbAvailable);
                
                sm.register("db", testDB);
                sm.register("file", testFile);
                String sql = "select 2*StringToInt(file.id) from db, file "
                        + "where file.id <> '234';";
                sm.remove("file");
                sm.remove("db");

                sm.register("db", testDB);
                sm.register("file", testFile);
                dsf.register("sql", sql);

                try {
                        sm.remove("file");
                        fail();
                } catch (IllegalStateException e) {
                }
                try {
                        sm.remove("db");
                        fail();
                } catch (IllegalStateException e) {
                }

                sm.remove("sql");
                sm.remove("file");
                sm.remove("db");
        }

        @Test
        public void testCanDeleteIfDependentSourceIsNotWellKnown() throws Exception {
                Assume.assumeTrue(hsqlDbAvailable);
                
                sm.register("db", testDB);
                sm.register("file", testFile);
                dsf.executeSQL("select 2*StringToInt(file.id) from db, file "
                        + "where file.id <> '234';");
                sm.remove("file");
                sm.remove("db");
        }

        @Test
        public void testDependentDependingSync() throws Exception {
                Assume.assumeTrue(hsqlDbAvailable);
                
                sm.removeAll();
                sm.register("db", testDB);
                sm.register("file", testFile);
                String sql = "select 2*StringToInt(file.id) from db, file "
                        + "where file.id <> '234';";
                dsf.register("sql", sql);
                sql = "select * from sql, file;";
                dsf.register("sql2", sql);
                // Anonimous ds should not been taken into account for dependencies
                dsf.executeSQL(sql);
                Source src = sm.getSource("db");
                assertTrue(setIs(src.getReferencingSources(), new String[]{"sql",
                                "sql2"}));
                assertTrue(setIs(src.getReferencedSources(), new String[]{}));
                src = sm.getSource("file");
                assertTrue(setIs(src.getReferencingSources(), new String[]{"sql",
                                "sql2"}));
                assertTrue(setIs(src.getReferencedSources(), new String[]{}));
                src = sm.getSource("sql");
                assertTrue(setIs(src.getReferencingSources(), new String[]{"sql2"}));
                assertTrue(setIs(src.getReferencedSources(), new String[]{"file",
                                "db"}));
                src = sm.getSource("sql2");
                assertTrue(setIs(src.getReferencingSources(), new String[]{}));
                assertTrue(setIs(src.getReferencedSources(), new String[]{"file",
                                "db", "sql"}));

                sm.remove("sql2");
                src = sm.getSource("db");
                assertTrue(setIs(src.getReferencingSources(), new String[]{"sql"}));
                assertTrue(setIs(src.getReferencedSources(), new String[]{}));
                src = sm.getSource("file");
                assertTrue(setIs(src.getReferencingSources(), new String[]{"sql"}));
                assertTrue(setIs(src.getReferencedSources(), new String[]{}));
                src = sm.getSource("sql");
                assertTrue(setIs(src.getReferencingSources(), new String[]{}));
                assertTrue(setIs(src.getReferencedSources(), new String[]{"file",
                                "db"}));
                src = sm.getSource("sql2");
                assertNull(src);

                sm.remove("sql");
                src = sm.getSource("db");
                assertTrue(setIs(src.getReferencingSources(), new String[]{}));
                assertTrue(setIs(src.getReferencedSources(), new String[]{}));
                src = sm.getSource("file");
                assertTrue(setIs(src.getReferencingSources(), new String[]{}));
                assertTrue(setIs(src.getReferencedSources(), new String[]{}));
                src = sm.getSource("sql");
                assertNull(src);
        }

        @Test
        public void testCannotRegisterTwice() throws Exception {
                Assume.assumeTrue(hsqlDbAvailable);
                
                sm.removeAll();

                sm.register("myfile", testFile);
                sm.register("myDB", testDB);
                sm.register("myWMS", testWMS);
                sm.register("myObj", obj);

                try {
                        sm.register("a", testFile);
                        fail();
                } catch (SourceAlreadyExistsException e) {
                }
                try {
                        sm.register("b", testDB);
                        fail();
                } catch (SourceAlreadyExistsException e) {
                }
                try {
                        sm.register("w", testWMS);
                        fail();
                } catch (SourceAlreadyExistsException e) {
                }
                try {
                        sm.register("c", obj);
                        fail();
                } catch (SourceAlreadyExistsException e) {
                }
                try {
                        sm.nameAndRegister(testFile);
                        fail();
                } catch (SourceAlreadyExistsException e) {
                }
                try {
                        sm.nameAndRegister(testDB);
                        fail();
                } catch (SourceAlreadyExistsException e) {
                }
                try {
                        sm.nameAndRegister(testWMS);
                        fail();
                } catch (SourceAlreadyExistsException e) {
                }
                try {
                        sm.nameAndRegister(obj, "main");
                        fail();
                } catch (SourceAlreadyExistsException e) {
                }

                sm.removeAll();
                sm.register("myfile", super.getAnySpatialResource());
                dsf.register("mySQL", sql);

                try {
                        dsf.register("d", sql);
                        fail();
                } catch (SourceAlreadyExistsException e) {
                }

                try {
                        dsf.nameAndRegister(sql);
                        fail();
                } catch (SourceAlreadyExistsException e) {
                }
        }

        @Test
        public void testSQLSourceType() throws Exception {
                sm.register(SOURCE, testFile);
                dsf.register("alphasql", "select * from " + SOURCE + ";");
                assertEquals((sm.getSource("alphasql").getType() & SourceManager.SQL), SourceManager.SQL);
        }

        @Test
        public void testCustomQueryDependences() throws Exception {
                sm.register(SOURCE, testFile);
                SumQuery sq = new SumQuery();
                if (dsf.getFunctionManager().getFunction(sq.getName()) == null) {
                        dsf.getFunctionManager().addFunction(SumQuery.class);
                }
                dsf.register("sum", "select * from sumquery(" + SOURCE + ", 'id');");
                String[] deps = sm.getSource("sum").getReferencedSources();
                assertEquals(deps.length, 1);
                assertEquals(deps[0], SOURCE);
        }

        @Test
        public void testDependingNotWellKnownSourcesRemoved() throws Exception {
                sm.register(SOURCE, testFile);
                DataSource ds = dsf.getDataSourceFromSQL("select * from " + SOURCE + ";");
                assertEquals(ds.getReferencedSources().length, 1);

                String nwkn = ds.getName();
                dsf.getSourceManager().remove(SOURCE);
                assertFalse(dsf.getSourceManager().exists(nwkn));
        }

        @Test
        public void testSaveWithAnOpenHSQLDBDataSource() throws Exception {
                Assume.assumeTrue(hsqlDbAvailable);
                
                sm.register("db", testDB);
                DataSource ds = dsf.getDataSource("db");
                ds.open();
                sm.saveStatus();
                ds.getFieldValue(0, 0);
                ds.close();
        }

        @Test
        public void testUnknownSources() throws Exception {
                try {
                        sm.register("toto", new FileSourceCreation(new File("toto.pptx"), null));
                        fail();
                } catch (DriverLoadException e) {
                }
        }

        @Test
        public void testListenCommits() throws Exception {
                DriverManager dm = new DriverManager();
                dm.registerDriver(ReadAndWriteDriver.class);
                
                sm.setDriverManager(dm);
                sm.register("object", new MemorySourceDefinition(
                        new ReadAndWriteDriver(), "main"));
                sm.register("file", new FakeFileSourceDefinition(
                        new ReadAndWriteDriver()));
                sm.register("db", new FakeDBTableSourceDefinition(
                        new ReadAndWriteDriver(), "jdbc:closefailing"));

                testListenCommits(dsf.getDataSource("object"));
                testListenCommits(dsf.getDataSource("file"));
                testListenCommits(dsf.getDataSource("db"));

        }

        private void testListenCommits(DataSource ds) throws DriverException {
                ds.open();
                ds.close();
        }

        @Before
        public void setUp() throws Exception {
                super.setUpTestsWithEdition(false);

                testFile = new File(TestResourceHandler.OTHERRESOURCES, "test.csv");
                testDB = new DBSource(null, 0, TestResourceHandler.OTHERRESOURCES
                        + "testhsqldb", "sa", "", "gisapps", "jdbc:hsqldb:file");
                testWMS = new WMSSource("127.0.0.1", "cantons", "EPSG:1234",
                        "format/pig");
                obj = new MemoryDataSetDriver();
        }
}
