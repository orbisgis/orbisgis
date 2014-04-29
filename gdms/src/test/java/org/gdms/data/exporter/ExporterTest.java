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
 * Copyright (C) 2007-2014 IRSTV FR CNRS 2488
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
package org.gdms.data.exporter;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import org.gdms.TestBase;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.DefaultSchema;
import org.gdms.data.schema.Schema;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.driver.DataSet;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.driver.io.FileExporter;

/**
 *
 * @author Antoine Gourlay
 */
public class ExporterTest extends TestBase {

        private static boolean flag1 = false;
        private static boolean flag2 = false;

        @Before
        public void setUp() throws Exception {
                super.setUpTestsWithoutEdition();
        }
        
        @Test
        public void exportingUnknownTableShouldFail() throws Exception {
                try {
                        sm.exportTo("toto", new File("someFile.toto"));
                        fail();
                } catch (NoSuchTableException noSuchTableException) {
                }
        }

        @Test
        public void testFileExporter() throws Exception {
                dsf.executeSQL("CREATE TABLE toto AS SELECT * FROM VALUES (42), (16), (7), (-42) AS t;");

                sm.getDriverManager().registerExporter(DummyExporter.class);

                flag1 = false;
                sm.exportTo("toto", new File("someFile.toto"));

                assertTrue(flag1);
        }

        public static class DummyExporter implements FileExporter {

                @Override
                public String[] getFileExtensions() {
                        return new String[]{".toto"};
                }

                @Override
                public void setFile(File file) throws DriverException {
                }

                @Override
                public Schema getSchema() throws DriverException {
                        Schema s = new DefaultSchema("test");
                        s.addTable(DriverManager.DEFAULT_SINGLE_TABLE_NAME, new DefaultMetadata(new Type[]{
                                        TypeFactory.createType(Type.INT)
                                }, new String[]{"someField"}));

                        return s;
                }

                @Override
                public void setDataSourceFactory(DataSourceFactory dsf) {
                }

                @Override
                public int getSupportedType() {
                        throw new UnsupportedOperationException();
                }

                @Override
                public int getType() {
                        throw new UnsupportedOperationException();
                }

                @Override
                public String getTypeName() {
                        throw new UnsupportedOperationException();
                }

                @Override
                public String getTypeDescription() {
                        throw new UnsupportedOperationException();
                }

                @Override
                public String getExporterId() {
                        return "toto file format exporter";
                }

                @Override
                public void open() throws DriverException {
                }

                @Override
                public void close() throws DriverException {
                }

                @Override
                public void export(DataSet d, String table) throws DriverException {
                        assertEquals(4, d.getRowCount());
                        assertEquals(1, d.getMetadata().getFieldCount());

                        assertEquals(42, d.getInt(0, 0));
                        assertEquals(16, d.getInt(1, 0));
                        assertEquals(7, d.getInt(2, 0));
                        assertEquals(-42, d.getInt(3, 0));

                        flag1 = true;
                }
        }

        @Test
        public void testFileExporterMultipleTables() throws Exception {
                dsf.executeSQL("CREATE TABLE totos.toto1 AS SELECT * FROM VALUES (42), (16), (7), (-42) AS t;");
                dsf.executeSQL("CREATE TABLE totos.toto2 AS SELECT * FROM VALUES ('hello'), ('hi') AS t;");

                sm.getDriverManager().registerExporter(DummyMTExporter.class);

                flag1 = false;
                flag2 = false;
                sm.exportTo("totos", new File("someFile.toto"));

                assertTrue(flag1);
                assertTrue(flag2);
        }

        public static class DummyMTExporter implements FileExporter {

                @Override
                public String[] getFileExtensions() {
                        return new String[]{".toto"};
                }

                @Override
                public void setFile(File file) throws DriverException {
                }

                @Override
                public Schema getSchema() throws DriverException {
                        Schema s = new DefaultSchema("test");
                        s.addTable("toto1", new DefaultMetadata(new Type[]{
                                        TypeFactory.createType(Type.INT)
                                }, new String[]{"someField"}));
                        s.addTable("toto2", new DefaultMetadata(new Type[]{
                                        TypeFactory.createType(Type.STRING)
                                }, new String[]{"someStringField"}));

                        return s;
                }

                @Override
                public void setDataSourceFactory(DataSourceFactory dsf) {
                }

                @Override
                public int getSupportedType() {
                        throw new UnsupportedOperationException();
                }

                @Override
                public int getType() {
                        throw new UnsupportedOperationException();
                }

                @Override
                public String getTypeName() {
                        throw new UnsupportedOperationException();
                }

                @Override
                public String getTypeDescription() {
                        throw new UnsupportedOperationException();
                }

                @Override
                public String getExporterId() {
                        return "toto file format exporter";
                }

                @Override
                public void open() throws DriverException {
                }

                @Override
                public void close() throws DriverException {
                }

                @Override
                public void export(DataSet d, String table) throws DriverException {
                        if ("toto1".equals(table)) {
                                assertEquals(4, d.getRowCount());
                                assertEquals(1, d.getMetadata().getFieldCount());

                                assertEquals(42, d.getInt(0, 0));
                                assertEquals(16, d.getInt(1, 0));
                                assertEquals(7, d.getInt(2, 0));
                                assertEquals(-42, d.getInt(3, 0));

                                flag1 = true;
                        } else if ("toto2".equals(table)) {
                                assertEquals(2, d.getRowCount());
                                assertEquals(1, d.getMetadata().getFieldCount());
                                
                                assertEquals("hello", d.getString(0, 0));
                                assertEquals("hi", d.getString(1, 0));
                                
                                flag2 = true;
                        } else {
                                fail("Found table " + table);
                        }
                }
        }
}
