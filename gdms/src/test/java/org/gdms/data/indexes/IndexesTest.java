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
package org.gdms.data.indexes;

import org.junit.Before;
import org.junit.Test;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;


import org.gdms.TestBase;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.source.Source;
import org.gdms.source.SourceManager;
import org.gdms.data.types.IncompatibleTypesException;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.utils.FileUtils;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

import static org.junit.Assert.*;

import org.gdms.TestResourceHandler;

public class IndexesTest extends TestBase {

        private IndexManager im;

        @Test
        public void testIndexPersistence() throws Exception {
                im.buildIndex("source", "gid", IndexManager.BTREE_ALPHANUMERIC_INDEX,
                        null);
                sm.saveStatus();
                DataSource ds = dsf.getDataSource("source");
                ds.open();
                ds.queryIndex(new DefaultAlphaQuery("gid", null, true, ValueFactory.createValue(10), false));
                ds.close();
        }

        @Test
        public void testRemoveIndexFilesOnIndexRemoval() throws Exception {
                im.buildIndex("source", "gid", IndexManager.BTREE_ALPHANUMERIC_INDEX,
                        null);
                sm.saveStatus();
                Source src = sm.getSource("source");
                String propertyName = IndexManager.INDEX_PROPERTY_PREFIX + "-gid-"
                        + IndexManager.BTREE_ALPHANUMERIC_INDEX;
                File indexFile = src.getFileProperty(propertyName);
                im.deleteIndex("source", "gid");

                DataSource ds = dsf.getDataSource("source");
                ds.open();
                DefaultAlphaQuery DefaultAlphaQuery = new DefaultAlphaQuery("gid",
                        null, true, null, false);
                Iterator<Integer> queryResult = ds.queryIndex(DefaultAlphaQuery);
                assertTrue(queryResult instanceof FullIterator);
                ds.close();
                assertFalse(indexFile.exists());
        }

        @Test
        public void testCreateIndexTwice() throws Exception {
                int numFiles1 = sm.getSourceInfoDirectory().listFiles().length;
                im.buildIndex("source", "gid", IndexManager.BTREE_ALPHANUMERIC_INDEX,
                        null);
                int numFiles2 = sm.getSourceInfoDirectory().listFiles().length;
                assertEquals(numFiles2 - 1, numFiles1);
                try {
                        im.buildIndex("source", "gid",
                                IndexManager.BTREE_ALPHANUMERIC_INDEX, null);
                        fail();
                } catch (IndexException e) {
                }
                int numFiles3 = sm.getSourceInfoDirectory().listFiles().length;
                assertEquals(numFiles2, numFiles3);
                sm.saveStatus();
                int numFiles4 = sm.getSourceInfoDirectory().listFiles().length;
                assertEquals(numFiles3, numFiles4);
        }

        @Test
        public void testCreateIndexOnWrongField() throws Exception {
                int numFiles1 = sm.getSourceInfoDirectory().listFiles().length;
                try {
                        im.buildIndex("source", "the_geom",
                                IndexManager.BTREE_ALPHANUMERIC_INDEX, null);
                        fail();
                } catch (IndexException e) {
                }
                int numFiles2 = sm.getSourceInfoDirectory().listFiles().length;
                assertEquals(numFiles2, numFiles1);
                sm.saveStatus();
                int numFiles3 = sm.getSourceInfoDirectory().listFiles().length;
                assertEquals(numFiles3, numFiles2);

        }

        @Test
        public void testCancelIndexCreation() throws Exception {
                int numFiles1 = sm.getSourceInfoDirectory().listFiles().length;
                im.buildIndex("source", "the_geom",
                        IndexManager.RTREE_SPATIAL_INDEX,
                        new NullProgressMonitor() {

                                @Override
                                public boolean isCancelled() {
                                        return true;
                                }
                        });
                int numFiles2 = sm.getSourceInfoDirectory().listFiles().length;
                assertEquals(numFiles2, numFiles1);
                sm.saveStatus();
                int numFiles3 = sm.getSourceInfoDirectory().listFiles().length;
                assertEquals(numFiles3, numFiles2);

        }

        @Test
        public void testAlreadyInstantiatedDataSourceUsesIndex() throws Exception {
                DataSource ds = dsf.getDataSource("source");
                ds.open();
                im.buildIndex("source", "gid", IndexManager.BTREE_ALPHANUMERIC_INDEX,
                        null);
                DefaultAlphaQuery DefaultAlphaQuery = new DefaultAlphaQuery("gid",
                        null, true, ValueFactory.createValue(10), false);
                Iterator<Integer> it = ds.queryIndex(DefaultAlphaQuery);
                int count = getCount(it);
                im.deleteIndex("source", "gid");
                it = ds.queryIndex(DefaultAlphaQuery);
                assertTrue(it instanceof FullIterator);
                ds.close();
                assertEquals(count, 9);
        }

        private int getCount(Iterator<Integer> it) {
                int count = 0;
                while (it.hasNext()) {
                        it.next();
                        count++;
                }
                return count;
        }

        @Test(expected = IllegalArgumentException.class)
        public void testDeleteNonExistingIndex() throws Exception {
                im.deleteIndex("source", "gid");
        }

        @Test
        public void testEditIndexedDataSource() throws Exception {
                im.buildIndex("source", "gid", IndexManager.BTREE_ALPHANUMERIC_INDEX,
                        null);
                DefaultAlphaQuery DefaultAlphaQuery = new DefaultAlphaQuery("gid",
                        null, true, ValueFactory.createValue(10), false);
                DataSource ds1 = dsf.getDataSource("source");
                DataSource ds2 = dsf.getDataSource("source");
                ds1.open();
                ds2.open();
                ds2.deleteRow(0);
                int countOriginal = getCount(ds1.queryIndex(DefaultAlphaQuery));
                int countEdited = getCount(ds2.queryIndex(DefaultAlphaQuery));
                ds1.close();
                ds2.close();

                assertEquals(countOriginal - 1, countEdited);
        }

        @Test
        public void testAlphaDeletion() throws Exception {
                im.buildIndex("source", "gid", IndexManager.BTREE_ALPHANUMERIC_INDEX,
                        null);

                DefaultAlphaQuery DefaultAlphaQuery = new DefaultAlphaQuery("gid",
                        null, false, null, false);
                testDeletion(DefaultAlphaQuery);
        }

        @Test
        public void testSpatialDeletion() throws Exception {
                im.buildIndex("source", "the_geom", IndexManager.RTREE_SPATIAL_INDEX,
                        null);

                DataSource ds = dsf.getDataSource("source");
                ds.open();
                Envelope env = ds.getFullExtent();
                ds.close();
                SpatialIndexQuery spatialQuery = new DefaultSpatialIndexQuery("the_geom", env);
                testDeletion(spatialQuery);
        }

        private void testDeletion(IndexQuery query) throws NoSuchTableException,
                DataSourceCreationException, DriverException {
                DataSource ds = dsf.getDataSource("source");
                ds.open();
                for (int i = 0; i < ds.getRowCount();) {
                        ds.deleteRow(0);
                }
                Iterator<Integer> it = ds.queryIndex(query);
                int count = getCount(it);
                assertEquals(count, 0);
                ds.close();
        }

        @Test
        public void testAlphaInsertionAtTheBeginning() throws Exception {
                im.buildIndex("source", "gid", IndexManager.BTREE_ALPHANUMERIC_INDEX,
                        null);
                IndexQuery DefaultAlphaQuery = new DefaultAlphaQuery("gid", null,
                        false, null, false);
                testInsertionAtTheBeginning(DefaultAlphaQuery);
        }

        @Test
        public void testSpatialInsertionAtTheBeginning() throws Exception {
                im.buildIndex("source", "the_geom", IndexManager.RTREE_SPATIAL_INDEX,
                        null);

                DataSource ds = dsf.getDataSource("source");
                ds.open();
                Envelope env = ds.getFullExtent();
                ds.close();
                SpatialIndexQuery spatialQuery = new DefaultSpatialIndexQuery("the_geom", env);
                testInsertionAtTheBeginning(spatialQuery);
        }

        private void testInsertionAtTheBeginning(IndexQuery DefaultAlphaQuery)
                throws NoSuchTableException, DataSourceCreationException,
                DriverException {
                DataSource ds = dsf.getDataSource("source");
                ds.open();
                ds.insertFilledRowAt(0, ds.getRow(2));
                ds.insertEmptyRowAt(0);
                Iterator<Integer> it = ds.queryIndex(DefaultAlphaQuery);
                // There is no element at row 0. There is no repeated element
                HashSet<Integer> repeated = new HashSet<Integer>();
                while (it.hasNext()) {
                        int row = it.next();
                        assertTrue(Integer.toString(row), !repeated.contains(row));
                        repeated.add(row);
                        assertTrue(row > 0);
                }
                ds.close();
        }

        @Test
        public void testReplaceBaseIndexOnCommit() throws Exception {
                im.buildIndex("source", "gid", IndexManager.BTREE_ALPHANUMERIC_INDEX,
                        null);
                im.buildIndex("source", "the_geom", IndexManager.RTREE_SPATIAL_INDEX,
                        null);
                DataSource ds = dsf.getDataSource("source");
                ds.open();
                for (int i = 0; i < ds.getRowCount();) {
                        ds.deleteRow(0);
                }
                ds.commit();
                checkReplacedIndex(ds);
                ds.close();
                checkReplacedIndex(ds);
                checkReplacedIndex(dsf.getDataSource("source"));
        }

        private void checkReplacedIndex(DataSource ds) throws DriverException {
                ds.open();
                DefaultAlphaQuery DefaultAlphaQuery = new DefaultAlphaQuery("gid",
                        null, false, null, false);
                SpatialIndexQuery spatialQuery = new DefaultSpatialIndexQuery("the_geom",
                        ds.getFullExtent());
                Iterator<Integer> it = ds.queryIndex(DefaultAlphaQuery);
                int count = getCount(it);
                assertEquals(count, 0);
                it = ds.queryIndex(spatialQuery);
                count = getCount(it);
                assertEquals(count, 0);
                ds.close();
        }

        @Test
        public void testDataSourceInEditionLosesIndex() throws Exception {
                im.buildIndex("source", "gid", IndexManager.BTREE_ALPHANUMERIC_INDEX,
                        null);
                im.buildIndex("source", "the_geom", IndexManager.RTREE_SPATIAL_INDEX,
                        null);
                IndexQuery DefaultAlphaQuery = new DefaultAlphaQuery("gid",
                        ValueFactory.createValue(Integer.MAX_VALUE), false,
                        ValueFactory.createValue(Integer.MIN_VALUE), false);
                DataSource ds = dsf.getDataSource("source");
                ds.open();
                Geometry geom = ds.getFieldValue(1, 0).getAsGeometry();
                ds.deleteRow(0);
                Iterator<Integer> it = ds.queryIndex(DefaultAlphaQuery);
                int count = getCount(it);
                assertEquals(count, 0);
                im.deleteIndex("source", "gid");
                // Check the alpha index is gone
                it = ds.queryIndex(DefaultAlphaQuery);
                count = getCount(it);
                assertEquals(count, ds.getRowCount());
                // Check the spatial index remains
                SpatialIndexQuery spatialQuery = new DefaultSpatialIndexQuery( "the_geom", geom.getEnvelopeInternal());
                it = ds.queryIndex(spatialQuery);
                assertTrue(contains(ds, it, geom));
                ds.close();
        }

        private boolean contains(DataSource ds, Iterator<Integer> it, Geometry geom)
                throws IncompatibleTypesException, DriverException {
                while (it.hasNext()) {
                        int row = it.next();
                        Geometry geom2 = ds.getFieldValue(row, 0).getAsGeometry();
                        if (geom2.equals(geom)) {
                                return true;
                        }
                }

                return false;
        }

        @Test
        public void testCreateIndexOnEditedSource() throws Exception {
                DataSource ds = dsf.getDataSource("source");
                ds.open();
                ds.setInt(0, "gid", 999999);
                im.buildIndex("source", "gid", IndexManager.BTREE_ALPHANUMERIC_INDEX,
                        null);
                IndexQuery DefaultAlphaQuery = new DefaultAlphaQuery("gid",
                        ValueFactory.createValue(999999));
                Iterator<Integer> it = ds.queryIndex(DefaultAlphaQuery);
                assertEquals(it.next(), (Integer) 0);
                assertFalse(it.hasNext());
                DataSource ds2 = dsf.getDataSource("source");
                ds2.open();
                it = ds2.queryIndex(DefaultAlphaQuery);
                assertFalse(it.hasNext());
                ds2.close();
                ds.close();
        }

        @Test
        public void testIndexedEditionAfterCommit() throws Exception {
                DataSource ds = dsf.getDataSource("source");
                ds.open();
                ds.deleteRow(0);
                ds.commit();
                ds.deleteRow(0);
                im.buildIndex("source", "gid", IndexManager.BTREE_ALPHANUMERIC_INDEX,
                        null);
                DefaultAlphaQuery tenQuery = new DefaultAlphaQuery("gid", null, true,
                        ValueFactory.createValue(10), true);
                int indexResultCount = getCount(ds.queryIndex(tenQuery));
                assertTrue(indexResultCount <= 10);
                ds.commit();
                ds.close();
        }

        @Test
        public void testSyncWithIndexedSource() throws Exception {
                DefaultAlphaQuery tenQuery = new DefaultAlphaQuery("gid", null, true,
                        ValueFactory.createValue(10), true);
                DataSource ds = dsf.getDataSource("source");
                ds.open();
                int indexResultCount = getCount(ds.queryIndex(tenQuery));
                im.buildIndex("source", "gid", IndexManager.BTREE_ALPHANUMERIC_INDEX,
                        null);
                int count2 = getCount(ds.queryIndex(tenQuery));
                assertTrue(count2 < indexResultCount);
                ds.deleteRow(0);
                ds.syncWithSource();
                int countReverted = getCount(ds.queryIndex(tenQuery));
                assertTrue(countReverted < indexResultCount);
                ds.close();
        }

        @Test
        public void testCreateIndexRevertAndModify() throws Exception {
                DataSource ds = dsf.getDataSource("source");
                ds.open();
                im.buildIndex("source", "the_geom", IndexManager.RTREE_SPATIAL_INDEX,
                        null);
                ds.setFieldValue(3, 0, ds.getFieldValue(1, 0));
                ds.syncWithSource();
                ds.setFieldValue(3, 0, ds.getFieldValue(1, 0));

                SpatialIndexQuery spatialQuery = new DefaultSpatialIndexQuery("the_geom",
                        ds.getFullExtent());
                assertEquals(getCount(ds.queryIndex(spatialQuery)), ds.getRowCount());
                ds.close();
        }

        @Before
        public void setUp() throws Exception {
                super.setUpTestsWithEdition(false);
                im = dsf.getIndexManager();
                sm.removeAll();
                File destshp = getTempCopyOf(new File(TestResourceHandler.TESTRESOURCES, "hedgerow.shp"));
                sm.register("source", destshp);
        }
}
