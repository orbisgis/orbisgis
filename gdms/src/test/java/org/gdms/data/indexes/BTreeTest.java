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
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import org.gdms.TestBase;
import org.gdms.TestResourceHandler;
import org.gdms.data.DataSource;
import org.gdms.data.indexes.btree.BTree;
import org.gdms.data.indexes.btree.DiskBTree;
import org.gdms.data.indexes.tree.IndexVisitor;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;

public class BTreeTest extends TestBase {

        private ArrayList<Value> v;
        private File indexFile;

        @Before
        public void setUp() throws Exception {
                super.setUpTestsWithoutEdition();
                v = new ArrayList<Value>();
                v.add(ValueFactory.createValue(23));
                v.add(ValueFactory.createValue(29));
                v.add(ValueFactory.createValue(2));
                v.add(ValueFactory.createValue(3));
                v.add(ValueFactory.createValue(5));
                v.add(ValueFactory.createValue(11));
                v.add(ValueFactory.createValue(7));
                v.add(ValueFactory.createValue(13));
                v.add(ValueFactory.createValue(17));
                v.add(ValueFactory.createValue(19));
                v.add(ValueFactory.createValue(31));
                v.add(ValueFactory.createValue(37));
                v.add(ValueFactory.createValue(41));
                v.add(ValueFactory.createValue(43));
                v.add(ValueFactory.createValue(47));

                indexFile = File.createTempFile("idx-", ".idx");
                indexFile.delete();
                indexFile.deleteOnExit();
        }

        private void checkLookUp(BTree tree) throws IOException {
                tree.checkTree();
                assertEquals(tree.size(), tree.getAllValues().length);
                Value[] keys = tree.getAllValues();
                for (int i = 0; i < keys.length; i++) {
                        int[] indexes = tree.query(keys[i]);
                        for (int index : indexes) {
                                assertTrue("value: " + keys[i], v.get(index).equals(keys[i]).getAsBoolean());
                        }
                }
        }

        @Test
        public void testLeafAndIntermediateOverLoad() throws Exception {
                BTree tree = new DiskBTree(3, 256, false);
                tree.newIndex(indexFile);
                makeInsertions(tree, 0, 2, 1, 3, 5, 4, 6, 7, 8, 9);
                tree.close();
                tree = new DiskBTree(3, 256, false);
                setUp();
                tree.newIndex(indexFile);
                makeInsertions(tree, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
                tree.close();
        }

        @Test
        public void testDeletions() throws Exception {
                BTree tree = new DiskBTree(3, 256, false);
                tree.newIndex(indexFile);
                makeInsertions(tree, 0, 2, 1, 3, 5, 4, 6, 7, 8, 9);
                makeDeletions(tree, 2, 4, 6, 8, 9, 7, 5, 3, 1, 0);
                makeInsertions(tree, 0, 0, 1, 1, 1, 2, 2, 2, 3, 4);
                makeDeletions(tree, 1, 0, 1, 0, 2, 2, 4, 3, 1, 2);
                tree.close();
        }

        private void makeDeletions(BTree tree, int... vIndexes) throws IOException {
                for (int index : vIndexes) {
                        Value value = v.get(index);
                        tree.delete(value, index);
                        checkLookUp(tree);
                        tree.save();
                        checkLookUp(tree);
                }
        }

        private void makeInsertions(BTree tree, int... vIndexes) throws IOException {
                for (int index : vIndexes) {
                        tree.insert(v.get(index), index);
                        checkLookUp(tree);
                        tree.save();
                        checkLookUp(tree);
                }
        }

        @Test
        public void testRepeatedValues() throws Exception {
                BTree tree = new DiskBTree(3, 256, false);
                tree.newIndex(indexFile);
                makeInsertions(tree, 0, 0, 1, 1, 1, 2, 2, 2, 3, 4);
                makeDeletions(tree, 4, 2, 2, 1, 1, 0, 3, 2, 1, 0);
        }

        @Test
        public void testIndexRealData() throws Exception {
                File file = new File(TestResourceHandler.TESTRESOURCES, "hedgerow.shp");
                dsf.getSourceManager().register("hedges", file);
                testIndexRealData(new DiskBTree(3, 64, false), dsf.getDataSource(file), "type", 100.0);
                setUp();
                testIndexRealData(new DiskBTree(32, 64, false), dsf.getDataSource(file), "type", 100.0);
        }
        
        @Test
        public void testIndexRealDataFromSQL() throws Exception {
                File file = new File(TestResourceHandler.TESTRESOURCES, "hedgerow.shp");
                dsf.getSourceManager().register("hedges", file);
                DataSource ds = dsf.getDataSourceFromSQL("select * from hedges order by \"type\" ;");
                setUp();
                testIndexRealData(new DiskBTree(255, 512, false), ds, "type", 100.0);
                setUp();
                testIndexRealData(new DiskBTree(3, 256, false), ds, "type", 1000.0);
        }

        private void testIndexRealData(BTree tree, DataSource ds, String fieldName,
                double checkPeriod) throws Exception {
                ds.open();
                tree.newIndex(indexFile);
                int fieldIndex = ds.getFieldIndexByName(fieldName);
                if (fieldIndex == -1) {
                        throw new DriverException("The field " + fieldName + " does not exist!");
                }
                for (int i = 0; i < ds.getRowCount(); i++) {
                        if (i / (int) checkPeriod == i / checkPeriod) {
                                tree.checkTree();
                                tree.close();
                                tree.openIndex(indexFile);
                                tree.checkTree();
                                checkLookUp(tree, ds, fieldIndex);
                                tree.checkTree();
                        }
                        Value value = ds.getFieldValue(i, fieldIndex);
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
                        int size = tree.size();
                        tree.delete(value, i);
                        assertEquals(tree.size() + 1, size);
                }

                ds.close();
                tree.close();
        }

        private void checkLookUp(BTree tree, DataSource ds, int fieldIndex)
                throws IOException, DriverException {
                Value[] allValues = tree.getAllValues();
                for (Value value : allValues) {
                        int[] rows = tree.query(value);
                        for (int row : rows) {
                                assertTrue(ds.getFieldValue(row, fieldIndex).equals(value).getAsBoolean());
                        }
                }
        }

        @Test
        public void testSmallNode() throws Exception {
                testInsertions(3, 32);
        }

        private void testInsertions(int n, int blockSize) throws IOException,
                Exception {
                BTree tree = new DiskBTree(n, blockSize, false);
                tree.newIndex(indexFile);
                makeInsertions(tree, 0, 2, 1, 3, 5, 4, 6, 7, 8, 9);
                tree.close();
                tree = new DiskBTree(3, 64, false);
                setUp();
                tree.newIndex(indexFile);
                makeInsertions(tree, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
                tree.close();
        }

        @Test
        public void testNodeBiggerThanBlock() throws Exception {
                testInsertions(256, 32);
        }

        @Test
        public void testEmptyIndex() throws Exception {
                BTree tree = new DiskBTree(5, 64, false);
                tree.newIndex(indexFile);
                tree.save();
                tree.close();
                tree.openIndex(indexFile);
                assertEquals(tree.size(), 0);
                tree.checkTree();
        }

        @Test
        public void testIndexWithZeroElements() throws Exception {
                BTree tree = new DiskBTree(5, 64, false);
                tree.newIndex(indexFile);
                makeInsertions(tree, 0, 2, 1, 3, 5, 4, 6, 7, 8, 9);
                makeDeletions(tree, 0, 2, 1, 3, 5, 4, 6, 7, 8, 9);
                assertEquals(tree.size(), 0);
                tree.save();
                tree.close();
                tree.openIndex(indexFile);
                assertEquals(tree.size(), 0);
                assertEquals(tree.query(ValueFactory.createValue(0)).length, 0);
        }

        @Test
        public void testEmptySpaces() throws Exception {
                BTree tree = new DiskBTree(5, 32, false);
                tree.newIndex(indexFile);
                // populate the index
                makeInsertions(tree, 0, 2, 1, 3, 5, 4, 6, 7, 8, 9);
                tree.close();
                tree.openIndex(indexFile);
                // clean it and keep the number of empty nodes
                makeDeletions(tree, 0, 2, 1, 3, 5, 4, 6, 7, 8, 9);
                int emptyBlocks = ((DiskBTree) tree).getEmptyBlocks();
                assertEquals(emptyBlocks, 0);
                tree.close();
                tree.openIndex(indexFile);
                // The number of empty nodes have not changed after closing
                assertEquals(emptyBlocks, ((DiskBTree) tree).getEmptyBlocks());
                makeInsertions(tree, 0, 2, 1, 3, 5, 4, 6, 7, 8, 9);
                assertEquals(((DiskBTree) tree).getEmptyBlocks(), 0);
                // clean it again
                makeDeletions(tree, 0, 2, 1, 3, 5, 4, 6, 7, 8, 9);
                assertEquals(emptyBlocks, ((DiskBTree) tree).getEmptyBlocks());
        }

        @Test
        public void testRangeQueries() throws Exception {
                BTree tree = new DiskBTree(3, 256, false);
                tree.newIndex(indexFile);
                tree.insert(ValueFactory.createValue(0), 0);
                tree.insert(ValueFactory.createValue(0), 1);
                tree.insert(ValueFactory.createValue(1), 2);
                tree.insert(ValueFactory.createValue(1), 3);
                tree.insert(ValueFactory.createValue(1), 4);
                tree.insert(ValueFactory.createValue(2), 5);
                tree.insert(ValueFactory.createValue(2), 6);
                tree.insert(ValueFactory.createValue(2), 7);
                tree.insert(ValueFactory.createValue(3), 8);
                tree.insert(ValueFactory.createValue(4), 9);
                tree.checkTree();
                assertEquals(tree.rangeQuery(ValueFactory.createNullValue(), false,
                        ValueFactory.createValue(1), true).length, 5);
                assertEquals(tree.rangeQuery(ValueFactory.createNullValue(), false,
                        ValueFactory.createValue(1), false).length, 2);
                assertEquals(tree.rangeQuery(ValueFactory.createValue(3), true, ValueFactory.createValue(3), true).length, 1);
                assertEquals(tree.rangeQuery(ValueFactory.createValue(1), false, ValueFactory.createValue(4), false).length, 4);
                assertEquals(tree.rangeQuery(ValueFactory.createValue(1), false, ValueFactory.createNullValue(), false).length, 5);
                assertEquals(tree.rangeQuery(ValueFactory.createNullValue(), true,
                        ValueFactory.createNullValue(), false).length, 10);
                assertEquals(tree.rangeQuery(ValueFactory.createNullValue(), true,
                        ValueFactory.createValue(0), false).length, 0);
        }

        @Test
        public void testNotExistentValues() throws Exception {
                BTree tree = new DiskBTree(5, 32, false);
                tree.newIndex(indexFile);
                // populate the index
                makeInsertions(tree, 0, 2, 1, 3, 5, 4, 6, 7, 8, 9);
                String snapshot = tree.toString();
                tree.delete(ValueFactory.createValue(19257), 2834);
                tree.delete(ValueFactory.createValue(0), 2834);
                tree.checkTree();
                assertEquals(tree.query(ValueFactory.createValue(2834)).length, 0);
                String snapshot2 = tree.toString();
                assertEquals(snapshot, snapshot2);
                tree.close();
        }

        @Test
        public void testIndexVisitor() throws IOException {
                BTree tree = new DiskBTree(3, 256, false);
                tree.newIndex(indexFile);
                tree.insert(ValueFactory.createValue(0), 0);
                tree.insert(ValueFactory.createValue(0), 1);
                tree.insert(ValueFactory.createValue(1), 2);
                tree.insert(ValueFactory.createValue(1), 3);
                tree.insert(ValueFactory.createValue(1), 4);
                tree.insert(ValueFactory.createValue(2), 5);
                tree.insert(ValueFactory.createValue(2), 6);
                tree.insert(ValueFactory.createValue(2), 7);
                tree.insert(ValueFactory.createValue(3), 8);
                tree.insert(ValueFactory.createValue(4), 9);
                tree.checkTree();

                final int[] t = new int[]{0, 0, 1, 1, 1, 2, 2, 2, 3, 4};
                final IV iV = new IV(t);


                tree.query(ValueFactory.createValue(2), iV);

                assertTrue(iV.fired);
        }

        private static class IV implements IndexVisitor<Value> {

                boolean fired = false;
                int [] t;

                public IV(int[] t) {
                        this.t = t;
                }
                
                @Override
                public void visitElement(int row, Value env) {
                        fired = true;
                        assertEquals(t[row], env.getAsInt());
                }
        }
}
