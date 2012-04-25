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

import java.io.File;
import java.io.IOException;

import org.gdms.TestBase;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.indexes.rtree.DiskRTree;
import org.gdms.data.indexes.tree.IndexVisitor;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.source.SourceManager;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.vividsolutions.jts.geom.Envelope;

import org.gdms.TestResourceHandler;

public class RTreeTest extends TestBase {

        private File indexFile;
        
        private void checkLookUp(DiskRTree tree, DataSource ds, int fieldIndex)
                throws Exception {
                tree.checkTree();
                assertEquals(tree.size(), tree.getAllValues().length);
                Envelope[] keys = tree.getAllValues();
                for (int i = 0; i < keys.length; i++) {
                        int[] indexes = tree.query(keys[i]);
                        assertTrue(contains(indexes, ds, fieldIndex, keys[i]));
                }

        }

        private boolean contains(int[] indexes, DataSource ds, int fieldIndex,
                Envelope geometry) throws Exception {
                for (int i : indexes) {
                        if (ds.getFieldValue(i, fieldIndex).getAsGeometry().getEnvelopeInternal().equals(geometry)) {
                                return true;
                        }
                }

                return false;
        }

        @Test
        public void testIndexNGreaterThanBlock() throws Exception {
                testIndexRealData("points", 256, 32, 1000.0);
        }

        @Test
        public void testIndexPoints() throws Exception {
                testIndexRealData("points", 16, 1024, 1000.0);
        }

        @Test
        public void testIndexPointsWithSmallN() throws Exception {
                testIndexRealData("points", 3, 32, 1000.0);
        }

        private void testIndexRealData(String source, int n, int blockSize,
                double checkPeriod) throws Exception {
                DiskRTree tree = new DiskRTree(n, blockSize, false);
                tree.newIndex(indexFile);
                testIndexRealData(source, checkPeriod, tree);
                tree.close();
        }

        private void testIndexRealData(String source, double checkPeriod, DiskRTree tree)
                throws NoSuchTableException, DataSourceCreationException,
                DriverException, IOException, Exception {
                DataSource ds = dsf.getDataSource(source);
                String fieldName = "the_geom";

                ds.open();
                int fieldIndex = ds.getFieldIndexByName(fieldName);
                for (int i = 0; i < ds.getRowCount(); i++) {
                        if (i / (int) checkPeriod == i / checkPeriod) {
                                tree.checkTree();
                                tree.close();
                                tree.openIndex(indexFile);
                                tree.checkTree();
                                checkLookUp(tree, ds, fieldIndex);
                        }
                        Envelope value = ds.getFieldValue(i, fieldIndex).getAsGeometry().getEnvelopeInternal();
                        tree.insert(value, i);
                }
                for (int i = 0; i < ds.getRowCount(); i++) {
                        if (i / (int) checkPeriod == i / checkPeriod) {
                                tree.checkTree();
                                tree.save();
                                tree.checkTree();
                                checkLookUp(tree, ds, fieldIndex);
                        }
                        Value value = ds.getFieldValue(i, fieldIndex);
                        tree.delete(value.getAsGeometry().getEnvelopeInternal(), i);
                }

                ds.close();
        }

        @Test
        public void testIndexVisitor() throws Exception {
                DiskRTree tree = new DiskRTree(16, 1024, false);
                tree.newIndex(indexFile);
                DataSource ds = dsf.getDataSource("points");
                String fieldName = "the_geom";

                ds.open();
                int fieldIndex = ds.getFieldIndexByName(fieldName);
                for (int i = 0; i < ds.getRowCount(); i++) {
                        Envelope value = ds.getFieldValue(i, fieldIndex).getAsGeometry().getEnvelopeInternal();
                        tree.insert(value, i);
                }
                Envelope e = ds.getGeometry((ds.getRowCount() - 1) / 2).getEnvelopeInternal();

                IV iV = new IV(ds);
                tree.query(e, iV);

                assertTrue(iV.fired);
        }

        private static class IV implements IndexVisitor<Envelope> {

                boolean fired = false;
                DataSource ds;

                public IV(DataSource ds) {
                        this.ds = ds;
                }

                @Override
                public void visitElement(int row, Envelope env) {
                        fired = true;
                        try {
                                assertTrue(env.contains(ds.getGeometry(row).getEnvelopeInternal()));
                        } catch (DriverException ex) {
                                fail();
                        }
                }
        }

        @Before
        public void setUp() throws Exception {
                indexFile = File.createTempFile("idx-", ".idx");
                indexFile.delete();
                indexFile.deleteOnExit();

                super.setUpTestsWithoutEdition();

                sm.register("points", new File(TestResourceHandler.TESTRESOURCES, "points.shp"));
                sm.register("lines", new File(TestResourceHandler.TESTRESOURCES, "hedgerow.shp"));
                sm.register("pols", new File(TestResourceHandler.TESTRESOURCES, "landcover2000.shp"));
        }
}
