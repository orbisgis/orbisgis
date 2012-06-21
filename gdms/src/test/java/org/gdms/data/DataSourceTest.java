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
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import org.gdms.TestBase;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.DefaultSchema;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.Schema;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.AbstractDataSet;
import org.gdms.driver.DataSet;
import org.gdms.driver.DriverException;
import org.gdms.driver.MemoryDriver;
import org.gdms.driver.memory.MemoryDataSetDriver;

public class DataSourceTest extends TestBase {
        
        @Before
        public void setUp() throws Exception {
               super.setUpTestsWithEdition(false);
               sm.register("test", super.getAnyNonSpatialResource());
        }

        @Test
        public void testReadWriteAccessInDataSourceOutOfTransaction()
                throws Exception {
                
                DataSource ds = dsf.getDataSource("test");

                try {
                        ds.getFieldValue(0, 0);
                        fail();
                } catch (ClosedDataSourceException e) {
                }
                try {
                        ds.getMetadata();
                        fail();
                } catch (ClosedDataSourceException e) {
                }
                try {
                        ds.getFieldIndexByName("");
                        fail();
                } catch (ClosedDataSourceException e) {
                }
                try {
                        ds.getRowCount();
                        fail();
                } catch (ClosedDataSourceException e) {
                }
                try {
                        ds.getScope(DataSource.X);
                        fail();
                } catch (ClosedDataSourceException e) {
                }
                try {
                        ds.isNull(0, 0);
                        fail();
                } catch (ClosedDataSourceException e) {
                }
                try {
                        ds.deleteRow(0);
                        fail();
                } catch (ClosedDataSourceException e) {
                }
                try {
                        ds.insertEmptyRow();
                        fail();
                } catch (ClosedDataSourceException e) {
                }
                try {
                        ds.insertEmptyRowAt(0);
                        fail();
                } catch (ClosedDataSourceException e) {
                }
                try {
                        ds.insertFilledRow(new Value[0]);
                        fail();
                } catch (ClosedDataSourceException e) {
                }
                try {
                        ds.insertFilledRowAt(0, new Value[0]);
                        fail();
                } catch (ClosedDataSourceException e) {
                }
                try {
                        ds.isModified();
                        fail();
                } catch (ClosedDataSourceException e) {
                }
                try {
                        ds.redo();
                        fail();
                } catch (ClosedDataSourceException e) {
                }
                try {
                        ds.setFieldValue(0, 0, null);
                        fail();
                } catch (ClosedDataSourceException e) {
                }
                try {
                        ds.undo();
                        fail();
                } catch (ClosedDataSourceException e) {
                }
        }

        @Test
        public void testSaveDataWithOpenDataSource() throws Exception {
                DataSource ds = dsf.getDataSource("test");

                ds.open();
                try {
                        ds.saveData(null);
                        fail();
                } catch (IllegalStateException e) {
                }
                ds.close();
        }

        @Test
        public void testRemovedDataSource() throws Exception {
                DataSource ds = dsf.getDataSource("test");

                ds.open();
                ds.close();
                dsf.getSourceManager().remove(ds.getName());

                try {
                        dsf.getDataSource("test");
                        fail();
                } catch (NoSuchTableException e) {
                }
                ds.open();
                ds.getFieldNames();
                ds.close();
        }

        @Test(expected = AlreadyClosedException.class)
        public void testAlreadyClosed() throws Exception {
                DataSource ds = dsf.getDataSource("test");

                ds.open();
                ds.close();

                // should fail
                ds.close();
        }

        @Test
        public void testFailedOpenClosedDataSource() throws Exception {
                File volatileFile = getTempCopyOf(super.getAnyNonSpatialResource());
                DataSource ds = dsf.getDataSource(volatileFile);
                volatileFile.delete();
                try {
                        ds.open();
                        fail();
                } catch (DriverException e) {
                        assertFalse(ds.isOpen());
                }
        }
        
        @Test
        public void testIterator() throws DriverException {
                 MemoryDataSetDriver omd = new MemoryDataSetDriver(
				new String[] { "field" }, new Type[] { TypeFactory
						.createType(Type.STRING) });
                omd.addValues(ValueFactory.createValue("toto1"));
                omd.addValues(ValueFactory.createValue("toto2"));
                omd.addValues(ValueFactory.createValue("toto3"));
                omd.addValues(ValueFactory.createValue("toto4"));
                
                DataSource ds = dsf.getDataSource(omd,"main", DataSourceFactory.EDITABLE);
		ds.open();
                Iterator<Value[]> i = ds.iterator();
                assertTrue(i.hasNext());
                Value[] v = i.next();
                assertEquals(1, v.length);
                assertEquals("toto1", v[0].getAsString());
                v = i.next();
                assertEquals("toto2", v[0].getAsString());
                i.remove();
                v = i.next();
                assertEquals("toto3", v[0].getAsString());
                i.next();
                assertFalse(i.hasNext());
                
                assertEquals(3, ds.getRowCount());
                assertEquals("toto3", ds.getString(1, 0));
        }

        private class NonEditableDriver extends AbstractDataSet implements MemoryDriver {

                @Override
                public String getDriverId() {
                        return null;
                }

                @Override
                public void setDataSourceFactory(DataSourceFactory dsf) {
                }

                @Override
                public void close() throws DriverException {
                }

                @Override
                public void open() throws DriverException {
                }

                @Override
                public int getSupportedType() {
                        return 0;
                }

                @Override
                public int getType() {
                        return 0;
                }

                @Override
                public String getTypeDescription() {
                        return null;
                }

                @Override
                public String getTypeName() {
                        return null;
                }

                @Override
                public boolean isCommitable() {
                        return false;
                }

                @Override
                public TypeDefinition[] getTypesDefinitions() {
                        return new TypeDefinition[0];
                }

                @Override
                public String validateMetadata(Metadata metadata) throws DriverException {
                        return null;
                }

                @Override
                public Schema getSchema() throws DriverException {
                        Schema s = new DefaultSchema("test0");
                        s.addTable("main", new DefaultMetadata());
                        return s;
                }

                @Override
                public Value getFieldValue(long rowIndex, int fieldId) throws DriverException {
                        return null;
                }

                @Override
                public long getRowCount() throws DriverException {
                        return 0;
                }

                @Override
                public Number[] getScope(int dimension) throws DriverException {
                        return null;
                }

                @Override
                public Metadata getMetadata() throws DriverException {
                        return new DefaultMetadata();
                }

                @Override
                public DataSet getTable(String name) {
                        if (!name.equals("main")) {
                                return null;
                        }
                        return this;
                }
        }

        @Test
        public void testCommitNonEditableDataSource() throws Exception {
                DataSource ds = dsf.getDataSource(new NonEditableDriver(), "main");

                ds.open();
                try {
                        ds.commit();
                        fail();
                } catch (NonEditableDataSourceException e) {
                }
                ds.close();
        }
}
