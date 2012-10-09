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
package org.gdms.source;

import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import org.gdms.TestBase;
import org.gdms.TestResourceHandler;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SourceAlreadyExistsException;
import org.gdms.data.db.DBSource;
import org.gdms.data.edition.FakeDBTableSourceDefinition;
import org.gdms.data.edition.FakeFileSourceDefinition;
import org.gdms.data.edition.ReadAndWriteDriver;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.memory.MemorySourceDefinition;
import org.gdms.data.stream.StreamSource;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileDriverRegister;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.driver.memory.MemoryDataSetDriver;
import org.gdms.sql.strategies.SumQuery;
import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

public class SourceManagementTest extends TestBase {

        private static final String SOURCE = "source";
        private static final String SOURCEMOD = "sourcd";
        private File testFile;
        private DBSource testDB;
        private StreamSource testWMS;
        private MemoryDataSetDriver obj;
        private String sql = "select count(*) from myfile;";

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
                // 1 file : directory.xml
                assertEquals(1, content.length);
                String[] res = new String[]{content[0].getName()};
                Arrays.sort(res);
                String[] comp = new String[]{"directory.xml"};
                assertArrayEquals(res, comp);
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
                assertEquals(1, content.length);
                String[] res = new String[]{content[0].getName()};
                Arrays.sort(res);
                String[] comp = new String[]{"directory.xml"};
                assertArrayEquals(res, comp);
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

                sm.shutdown();
                sm.init();

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

                sm.shutdown();
                sm.init();

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

                String newmem = sm.getMemento().replace(SOURCEMOD, SOURCE);
                assertEquals(memento, newmem);
        }

        @Test
        public void testReturnNullWhenNoProperty() throws Exception {
                sm.register(SOURCE, testFile);
                Source source = sm.getSource(SOURCE);
                assertNull(source.getFileProperty("skjbnskb"));
                assertNull(source.getProperty("skjbnskb"));
        }
        
        @Test
        public void testEmpty() throws Exception {
                assertFalse(sm.isEmpty());
                assertTrue(sm.isEmpty(true));
                assertFalse(sm.isEmpty(false));
                
                sm.register(SOURCE, testFile);
                
                assertFalse(sm.isEmpty());
                assertFalse(sm.isEmpty(true));
                
                sm.remove(SOURCE);
                
                assertFalse(sm.isEmpty());
                assertTrue(sm.isEmpty(true));
                
                sm.removeAll();
                
                assertFalse(sm.isEmpty());
                assertTrue(sm.isEmpty(true));
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

                assertEquals(memento, sm.getMemento());
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
                sm.register("obj", obj);

                String fileContent = getContent("myfile");
                String dbContent = hsqlDbAvailable ? getContent("db") : null;
                String objContent = getContent("obj");

                sm.saveStatus();

                assertEquals(fileContent, getContent("myfile"));
                if (hsqlDbAvailable) {
                        assertEquals(dbContent, getContent("db"));
                }
                assertEquals(objContent, getContent("obj"));

                sm.removeAll();
                sm.register("myfile", super.getAnyNonSpatialResource());
                sm.register("sql", sql);

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
                sm.register("file", testFile);
                String sql = "select 2*(file.id :: int) from file "
                        + "where (file.id :: int) <> 234;";
                sm.register("sql", sql);
                DataSource ds = dsf.getDataSource("sql");
                assertTrue(setIs(ds.getReferencedSources(),
                        new String[]{"file"}));
                ds = dsf.getDataSourceFromSQL(sql);
                assertTrue(setIs(ds.getReferencedSources(),
                        new String[]{"file"}));
                sql = "select * from file union select * from file;";
                sm.register("sql2", sql);
                ds = dsf.getDataSource("sql2");
                assertTrue(setIs(ds.getReferencedSources(), new String[]{"file"}));
                ds = dsf.getDataSourceFromSQL(sql);
                assertTrue(setIs(ds.getReferencedSources(), new String[]{"file"}));

                String[] srcDeps = dsf.getDataSource("file").getReferencedSources();
                assertEquals(0, srcDeps.length);
        }

        private boolean setIs(String[] referencingSources, String[] test) {
                if (referencingSources.length != test.length) {
                        return false;
                } else {
                        ArrayList<String> set = new ArrayList<String>();
                        set.addAll(Arrays.asList(referencingSources));
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
                sm.register("mySQL", sql);


                ds = dsf.getDataSourceFromSQL(sql);
                assertEquals(ds.getName(), "mySQL");
        }

        @Test
        public void testCannotDeleteDependedSource() throws Exception {
                sm.register("file", testFile);
                String sql = "select 2*(file.id :: int) from file "
                        + "where file.id <> '234';";
                sm.remove("file");
                
                sm.register("file", testFile);
                sm.register("sql", sql);

                try {
                        sm.remove("file");
                        fail();
                } catch (IllegalStateException e) {
                }
        }

        @Test
        public void testCanDeleteIfDependentSourceIsNotWellKnown() throws Exception {
                sm.register("file", testFile);
                dsf.executeSQL("select 2*(file.id :: int) from file "
                        + "where file.id <> '234';");
                sm.remove("file");
        }

        @Test
        public void testDependentDependingSync() throws Exception {
                sm.removeAll();
                sm.register("file", testFile);
                String sql = "select 2*(file.id :: int) from file "
                        + "where file.id <> '234';";
                sm.register("sql", sql);
                sql = "select * from \"sql\", file;";
                sm.register("sql2", sql);
                // Anonimous ds should not been taken into account for dependencies
                dsf.executeSQL(sql);
                Source src = sm.getSource("file");
                assertTrue(setIs(src.getReferencingSources(), new String[]{"sql",
                                "sql2"}));
                assertTrue(setIs(src.getReferencedSources(), new String[]{}));
                src = sm.getSource("sql");
                assertTrue(setIs(src.getReferencingSources(), new String[]{"sql2"}));
                src = sm.getSource("sql2");
                assertTrue(setIs(src.getReferencingSources(), new String[]{}));
                assertTrue(setIs(src.getReferencedSources(), new String[]{"file",
                                "sql"}));

                sm.remove("sql2");
                src = sm.getSource("file");
                assertTrue(setIs(src.getReferencingSources(), new String[]{"sql"}));
                assertTrue(setIs(src.getReferencedSources(), new String[]{}));
                src = sm.getSource("sql");
                assertTrue(setIs(src.getReferencingSources(), new String[]{}));
                src = sm.getSource("sql2");
                assertNull(src);

                sm.remove("sql");
                src = sm.getSource("file");
                assertTrue(setIs(src.getReferencingSources(), new String[]{}));
                assertTrue(setIs(src.getReferencedSources(), new String[]{}));
                src = sm.getSource("sql");
                assertNull(src);
        }

        @Test
        public void testCannotRegisterTwice() throws Exception {
                sm.register("myfile", testFile);
                sm.register("myWMS", testWMS);
                sm.register("myObj", obj);

                try {
                        sm.register("a", testFile);
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
                        sm.nameAndRegister(testWMS);
                        fail();
                } catch (SourceAlreadyExistsException e) {
                }
                try {
                        sm.nameAndRegister(obj, "main");
                        fail();
                } catch (SourceAlreadyExistsException e) {
                }

                sm.register("mySQL", sql);

                try {
                        sm.register("d", sql);
                        fail();
                } catch (SourceAlreadyExistsException e) {
                }
        }

        @Test
        public void testSQLSourceType() throws Exception {
                sm.register(SOURCE, testFile);
                sm.register("alphasql", "select * from " + SOURCE + ";");
                assertEquals((sm.getSource("alphasql").getType() & SourceManager.SQL), SourceManager.SQL);
        }

        @Test
        public void testCustomQueryDependences() throws Exception {
                sm.register(SOURCE, testFile);
                SumQuery sq = new SumQuery();
                if (dsf.getFunctionManager().getFunction(sq.getName()) == null) {
                        dsf.getFunctionManager().addFunction(SumQuery.class);
                }
                sm.register("sum", "select * from sumquery(" + SOURCE + ", 'id');");
                String[] deps = sm.getSource("sum").getReferencedSources();
                assertEquals(1, deps.length);
                assertEquals(SOURCE, deps[0]);
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
        
        @Test
        public void testCreateFromURI() throws Exception {
                URI uri = testFile.toURI();
                sm.register("test", uri);
                
                assertTrue(sm.getSource("test").isFileSource());
                
                uri = URI.create("postgresql://www.host.com:1234/db_name?table=toto&schema=tata&"
                        + "user=me&password=changeme");
                
                sm.register("test2", uri);
                assertTrue(sm.getSource("test2").isDBSource());
                
                DBSource s = sm.getSource("test2").getDBSource();
                assertEquals("www.host.com", s.getHost());
                assertEquals("db_name", s.getDbName());
                assertEquals(1234, s.getPort());
                assertEquals("jdbc:postgresql", s.getPrefix());
                assertEquals("toto", s.getTableName());
                assertEquals("tata", s.getSchemaName());
                assertEquals("me", s.getUser());
                assertEquals("changeme", s.getPassword());
        }
        
        @Test
        public void testURIManagement() throws Exception {
                URI uri = testFile.toURI();
                assertFalse(sm.exists(uri));
                
                try {
                        sm.getNameFor(uri);
                        fail();
                } catch (NoSuchTableException e) {
                        // should fail
                }
                
                sm.register("test", uri);
                assertTrue(sm.exists(uri));
                
                assertEquals("test", sm.getNameFor(uri));
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
                testWMS = new StreamSource("http://127.0.0.1", 80, "cantons", "wms", "format/pig", "EPSG:1234");
                obj = new MemoryDataSetDriver();
        }
}
