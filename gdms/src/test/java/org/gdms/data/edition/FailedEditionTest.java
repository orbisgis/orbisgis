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
package org.gdms.data.edition;

import org.junit.Before;
import org.junit.Test;
import java.util.Iterator;

import org.gdms.TestBase;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.indexes.DefaultSpatialIndexQuery;
import org.gdms.data.indexes.IndexQuery;
import org.gdms.data.memory.MemorySourceDefinition;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.SourceManager;

import static org.junit.Assert.*;

public class FailedEditionTest extends TestBase {

        private static final String SPATIAL_FIELD_NAME = "the_geom";
        private DataSourceFactory dsf;

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
                failedCommit(dsf.getDataSource("object"), new DefaultSpatialIndexQuery(ds.getFullExtent(),
                        SPATIAL_FIELD_NAME));
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
                failedCommit(ds, new DefaultSpatialIndexQuery(ds.getFullExtent(),
                        SPATIAL_FIELD_NAME));
        }

        @Before
        public void setUp() throws Exception {
                ReadDriver.initialize();
                ReadDriver.isEditable = true;
                ReadDriver.pk = true;

                dsf = new DataSourceFactory();
                dsf.setTempDir(TestBase.backupDir.getAbsolutePath());
                dsf.setResultDir(TestBase.backupDir);
                DriverManager dm = new DriverManager();
                dm.registerDriver(ReadAndWriteDriver.class);

                SourceManager sourceManager = dsf.getSourceManager();
                sourceManager.setDriverManager(dm);
                sourceManager.register("object", new MemorySourceDefinition(
                        new ReadAndWriteDriver(), "main"));
                final ReadAndWriteDriver readAndWriteDriver1 = new ReadAndWriteDriver();
                readAndWriteDriver1.setFile(null);
                sourceManager.register("writeFile", new FakeFileSourceDefinition(
                        readAndWriteDriver1));
                final ReadAndWriteDriver readAndWriteDriver2 = new ReadAndWriteDriver();
                readAndWriteDriver2.setFile(null);
                sourceManager.register("closeFile", new FakeFileSourceDefinition(
                        readAndWriteDriver2));
                final ReadAndWriteDriver readAndWriteDriver3 = new ReadAndWriteDriver();
                readAndWriteDriver3.setFile(null);
                sourceManager.register("copyFile", new FakeFileSourceDefinition(
                        readAndWriteDriver3));
                sourceManager.register("executeDB", new FakeDBTableSourceDefinition(
                        new ReadAndWriteDriver(), "jdbc:executefailing"));
                sourceManager.register("closeDB", new FakeDBTableSourceDefinition(
                        new ReadAndWriteDriver(), "jdbc:closefailing"));

                // what's the point in building an index on an emtpy source??
//		dsf.getIndexManager().buildIndex("object", SPATIAL_FIELD_NAME,
//				IndexManager.RTREE_SPATIAL_INDEX, null);
//		dsf.getIndexManager().buildIndex("writeFile", SPATIAL_FIELD_NAME,
//				IndexManager.RTREE_SPATIAL_INDEX, null);
//		dsf.getIndexManager().buildIndex("executeDB", SPATIAL_FIELD_NAME,
//				IndexManager.RTREE_SPATIAL_INDEX, null);
//		dsf.getIndexManager().buildIndex("closeDB", SPATIAL_FIELD_NAME,
//				IndexManager.RTREE_SPATIAL_INDEX, null);
//		dsf.getIndexManager().buildIndex("copyFile", SPATIAL_FIELD_NAME,
//				IndexManager.RTREE_SPATIAL_INDEX, null);
//		dsf.getIndexManager().buildIndex("closeFile", SPATIAL_FIELD_NAME,
//				IndexManager.RTREE_SPATIAL_INDEX, null);
        }

        private class FooQuery implements IndexQuery {

                public String getFieldName() {
                        return "";
                }

                public String getIndexId() {
                        return "";
                }

                public boolean isStrict() {
                        return false;
                }
        }
}
