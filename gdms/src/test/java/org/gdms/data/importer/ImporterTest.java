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
package org.gdms.data.importer;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import org.gdms.TestBase;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.SourceAlreadyExistsException;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.DefaultSchema;
import org.gdms.data.schema.Schema;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.driver.io.FileImporter;
import org.gdms.driver.io.Importer;
import org.gdms.driver.io.RowWriter;

/**
 *
 * @author Antoine Gourlay
 */
public class ImporterTest extends TestBase {

        @Before
        public void setUp() throws Exception {
                super.setUpTestsWithoutEdition();
        }

        @Test
        public void importingShouldProduceADataSourceDefinition() throws Exception {
                File res = super.getAnyNonSpatialResource();
                dsf.getSourceManager().importFrom("toto", new DummyImportSourceDefinition(res));

                assertTrue(sm.exists("toto"));

                try {
                        dsf.getSourceManager().importFrom("toto", new DummyImportSourceDefinition(res));
                        fail();
                } catch (SourceAlreadyExistsException e) {
                }
        }

        private class DummyImportSourceDefinition implements ImportSourceDefinition {

                private File res;

                public DummyImportSourceDefinition(File res) {
                        this.res = res;
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
                public String getImporterId() {
                        throw new UnsupportedOperationException();
                }

                @Override
                public Importer getImporter() {
                        throw new UnsupportedOperationException();
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
                public DataSourceDefinition importSource(String tableName) throws DriverException {
                        return new FileSourceDefinition(res, tableName);
                }

                @Override
                public DataSourceDefinition[] importAllSources() throws DriverException {
                        throw new UnsupportedOperationException();
                }
        }

        @Test
        public void testFileImporters() throws Exception {
                File f = new File("test.toto");
                sm.getDriverManager().registerImporter(DummyImporter.class);
                sm.importFrom("target", f);
                
                assertTrue(sm.exists("target"));
                
                DataSource d = dsf.getDataSource("target");
                d.open();
                assertEquals(1, d.getFieldCount());
                assertEquals("someField", d.getFieldName(0));
                assertEquals(Type.INT, d.getFieldType(0).getTypeCode());
                
                assertEquals(5, d.getRowCount());
                
                assertEquals(5, d.getInt(0, 0));
                assertEquals(12, d.getInt(1, 0));
                assertEquals(42, d.getInt(2, 0));
                assertEquals(7, d.getInt(3, 0));
                assertEquals(-42, d.getInt(4, 0));
                d.close();
        }

        public static class DummyImporter implements FileImporter {

                public DummyImporter() {
                }

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
                public String getImporterId() {
                        return "toto file format importer";
                }

                @Override
                public void open() throws DriverException {
                }

                @Override
                public void close() throws DriverException {
                }

                @Override
                public void convertTable(String name, RowWriter v) throws DriverException {
                        v.addValues(new Value[]{ValueFactory.createValue(5)});
                        v.addValues(new Value[]{ValueFactory.createValue(12)});
                        v.addValues(new Value[]{ValueFactory.createValue(42)});
                        v.addValues(new Value[]{ValueFactory.createValue(7)});
                        v.addValues(new Value[]{ValueFactory.createValue(-42)});
                }
        }
}
