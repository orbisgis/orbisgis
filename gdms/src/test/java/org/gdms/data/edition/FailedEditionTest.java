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
package org.gdms.data.edition;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import org.gdms.TestBase;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.indexes.DefaultSpatialIndexQuery;
import org.gdms.data.indexes.IndexQuery;
import org.gdms.data.memory.MemorySourceDefinition;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.Driver;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverManager;

public class FailedEditionTest extends TestBase {

        private static final String SPATIAL_FIELD_NAME = "the_geom";

        private void failedCommit(DataSource ds, IndexQuery query)
                throws DriverException, NonEditableDataSourceException {
                ds.deleteRow(2);
                ds.setFieldValue(0, 1, ValueFactory.createValue("nouveau"));
                Value[] row = ds.getRow(0);
                row[1] = ValueFactory.createValue("aaaaa");
                ds.insertFilledRow(row);
                Value[][] table = super.getDataSourceContents(ds);
                Iterator<Integer> it = ds.queryIndex(query);
                try {
                        ReadDriver.failOnWrite = true;
                        ds.commit();
                        fail();
                } catch (DriverException e) {
                        assertTrue(equals(table, super.getDataSourceContents(ds)));
                        if (it != null) {
                                assertNotNull(ds.queryIndex(query));;
                        } else {
                                assertNull(ds.queryIndex(query));
                        }
                        ReadDriver.failOnWrite = false;
                        ds.commit();
                } finally {
                        ds.close();
                }
                ds.open();
                assertTrue(equals(table, super.getDataSourceContents(ds)));
                ds.close();
        }

        @Test
        public void testAlphanumericObjectfailedCommit() throws Exception {
                DataSource ds = dsf.getDataSource("object");
                ds.open();
                failedCommit(ds, new FooQuery());
        }

        @Test
        public void testSpatialObjectfailedCommit() throws Exception {
                DataSource ds = dsf.getDataSource("object");
                ds.open();
                failedCommit(ds, new DefaultSpatialIndexQuery(SPATIAL_FIELD_NAME, ds.getFullExtent()));
        }

        @Test
        public void testAlphanumericFileFailOnWrite() throws Exception {
                DataSource ds = dsf.getDataSource("writeFile");
                ds.open();
                failedCommit(ds, new FooQuery());
        }

        @Test
        public void testAlphanumericDBFailOnWrite() throws Exception {
                DataSource ds = dsf.getDataSource("executeDB");
                ds.open();
                ReadDriver.setCurrentDataSource(ds);
                failedCommit(ds, new FooQuery());
        }

        @Test
        public void testSpatialDBfailedOnWrite() throws Exception {
                DataSource ds = dsf.getDataSource("executeDB");
                ds.open();
                ReadDriver.setCurrentDataSource(ds);
                failedCommit(ds, new DefaultSpatialIndexQuery(SPATIAL_FIELD_NAME, ds.getFullExtent()));
        }

        @Before
        public void setUp() throws Exception {
                ReadDriver.initialize();
                ReadDriver.isEditable = true;

                super.setUpTestsWithEdition(false);
                DriverManager dm = new DriverManager();
                dm.registerDriver(ReadAndWriteDriver.class);

                sm.setDriverManager(dm);
                sm.register("object", new MemorySourceDefinition(
                        new ReadAndWriteDriver(), "main"));
                final ReadAndWriteDriver readAndWriteDriver1 = new ReadAndWriteDriver();
                readAndWriteDriver1.setFile(null);
                sm.register("writeFile", new FakeFileSourceDefinition(
                        readAndWriteDriver1));
                final ReadAndWriteDriver readAndWriteDriver2 = new ReadAndWriteDriver();
                readAndWriteDriver2.setFile(null);
                sm.register("closeFile", new FakeFileSourceDefinition(
                        readAndWriteDriver2));
                final ReadAndWriteDriver readAndWriteDriver3 = new ReadAndWriteDriver();
                readAndWriteDriver3.setFile(null);
                sm.register("copyFile", new FakeFileSourceDefinition(
                        readAndWriteDriver3));
                sm.register("executeDB", new FakeDBTableSourceDefinition(
                        new ReadAndWriteDriver(), "jdbc:executefailing"));
                sm.register("closeDB", new FakeDBTableSourceDefinition(
                        new ReadAndWriteDriver(), "jdbc:closefailing"));
        }

        private class FooQuery implements IndexQuery {

                public String[] getFieldNames() {
                        return new String[]{""};
                }

                public String getIndexId() {
                        return "";
                }

                public boolean isStrict() {
                        return false;
                }
        }
}
