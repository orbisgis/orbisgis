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
package org.gdms.source;

import org.junit.Before;
import org.junit.Test;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;


import org.gdms.DBTestSource;
import org.gdms.TestBase;
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

import static org.junit.Assert.*;

public class SourceManagementTest {

        private static final String SOURCE = "source";
        private static final String SOURCEMOD = "sourcd";
        private SourceManager sm;
        private DataSourceFactory dsf;
        private File testFile;
        private DBSource testDB;
        private WMSSource testWMS;
        private MemoryDataSetDriver obj;

        @Test
        public void testRegisterTwice() throws Exception {
                sm.remove(SOURCE);
                sm.register(SOURCE, new File("b.shp"));
                try {
                        sm.register(SOURCE, new File("a.shp"));
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
                sm.remove(SOURCE);
                File file = new File("b.shp");
                sm.register(SOURCE, file);
                sm.remove(SOURCE);
                FileDriverRegister fdr = sm.getDriverManager().getFileDriverRegister();
                assertFalse(fdr.contains(file));
        }

        @Test
        public void testRemoveAll() throws Exception {
                Source src = sm.getSource(SOURCE);
                associateFile(src, "statisticsFile");
                associateString(src, "statistics");
                sm.removeAll();
                setUp();
                src = sm.getSource(SOURCE);
                assertEquals(src.getStringPropertyNames().length, 0);
                assertEquals(src.getFilePropertyNames().length, 0);
        }

        @Test
        public void testRemoveFileProperty() throws Exception {
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
                Source source = sm.getSource(SOURCE);
                String stringProp = "testFileProp";
                associateString(source, stringProp);
                source.deleteProperty(stringProp);

                assertEquals(source.getStringPropertyNames().length, 0);
                assertEquals(source.getFilePropertyNames().length, 0);
        }

        @Test
        public void testAssociateFile() throws Exception {
                String statistics = "statistics";
                Source source = sm.getSource(SOURCE);
                String rcStr = associateFile(source, statistics);

                assertEquals(sm.getSource(SOURCE).getFilePropertyNames().length, 1);

                sm.saveStatus();
                instantiateDSF();

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
                Source source = sm.getSource(SOURCE);
                String statistics = "statistics";
                String rcStr = associateString(source, statistics);

                assertEquals(sm.getSource(SOURCE).getStringPropertyNames().length, 1);

                sm.saveStatus();
                instantiateDSF();

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
                Source source = sm.getSource(SOURCE);
                assertNull(source.getFileProperty("skjbnskb"));
                assertNull(source.getProperty("skjbnskb"));
        }

        @Test
        public void testMoveAndChangeSourceDirectory() throws Exception {
                String statistics = "statistics";
                Source source = sm.getSource(SOURCE);
                associateFile(source, statistics);
                associateString(source, statistics);
                String memento = sm.getMemento();

                String newSourceInfoDir = TestBase.backupDir
                        + "source-management2";
                sm.setSourceInfoDirectory(newSourceInfoDir);

                instantiateDSF();

                memento = sm.getMemento();
                sm.changeSourceInfoDirectory(newSourceInfoDir);
                assertEquals(memento, sm.getMemento());
        }

        @Test
        public void testSameSourceSameDSInstance() throws Exception {
                DataSource ds1 = dsf.getDataSource(SOURCE, DataSourceFactory.NORMAL);
                DataSource ds2 = dsf.getDataSource(SOURCE, DataSourceFactory.NORMAL);
                ds1.open();
                assertTrue(ds2.isOpen());
                ds2.close();
        }

        @Test
        public void testPersistence() throws Exception {
                sm.removeAll();

                DBTestSource dbTestSource = new DBTestSource("testhsqldb",
                        "org.hsqldb.jdbcDriver", TestBase.internalData
                        + "testhsqldb.sql", testDB);

                sm.register("myfile", testFile);
                sm.register("db", testDB);
                sm.register("wms", testWMS);
                sm.register("obj", obj);

                String fileContent = getContent("myfile");
                String dbContent = getContent("db");
                String wmsContent = getContent("wms");
                String objContent = getContent("obj");

                sm.saveStatus();
                instantiateDSF();

                assertEquals(fileContent, getContent("myfile"));
                assertEquals(dbContent, getContent("db"));
                assertEquals(wmsContent, getContent("wms"));
                assertEquals(objContent, getContent("obj"));

        }

        private String getContent(String name) throws Exception {
                DataSource ds = dsf.getDataSource(name);
                ds.open();
                String ret = ds.getAsString();
                ds.close();

                return ret;
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
                sm.removeAll();

                sm.register("myfile", testFile);
                sm.register("myDB", testDB);
                sm.register("myWMS", testWMS);
                sm.register("myObj", obj);

                DataSource ds = dsf.getDataSource(testFile);
                assertEquals(ds.getName(), "myfile");

                ds = dsf.getDataSource(testDB);
                assertEquals(ds.getName(), "myDB");

                ds = dsf.getDataSource(testWMS);
                assertEquals(ds.getName(), "myWMS");

                ds = dsf.getDataSource(obj, "main");
                assertEquals(ds.getName(), "myObj");
        }

        @Test
        public void testCannotRegisterTwice() throws Exception {
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
        }

        @Test
        public void testSaveWithAnOpenHSQLDBDataSource() throws Exception {
                sm.remove("db");
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
                sm.remove("object");
                sm.remove("file");
                sm.remove("db");
                SourceManager sourceManager = dsf.getSourceManager();
                sourceManager.setDriverManager(dm);
                sourceManager.register("object", new MemorySourceDefinition(
                        new ReadAndWriteDriver(), "main"));
                sourceManager.register("file", new FakeFileSourceDefinition(
                        new ReadAndWriteDriver()));
                sourceManager.register("db", new FakeDBTableSourceDefinition(
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
                instantiateDSF();
                try {
                        sm.removeAll();
                } catch (IOException e) {
                }
                testFile = new File(TestBase.internalData + "test.csv");
                testDB = new DBSource(null, 0, TestBase.backupDir
                        + "testhsqldb", "sa", "", "gisapps", "jdbc:hsqldb:file");
                testWMS = new WMSSource("127.0.0.1", "cantons", "EPSG:1234",
                        "format/pig");
                obj = new MemoryDataSetDriver();
                sm.remove(SOURCE);
                sm.register(SOURCE, testFile);
        }

        private void instantiateDSF() {
                dsf = new DataSourceFactory(TestBase.backupDir
                        + "source-management");
                sm = dsf.getSourceManager();

        }
}
