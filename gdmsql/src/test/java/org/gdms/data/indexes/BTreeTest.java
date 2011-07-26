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
package org.gdms.data.indexes;

import org.junit.Test;
import org.junit.Before;
import java.io.File;
import java.io.IOException;


import org.gdms.SQLBaseTest;
import org.gdms.data.DataSource;
import org.gdms.data.SQLDataSourceFactory;
import org.gdms.data.indexes.btree.BTree;
import org.gdms.data.indexes.btree.DiskBTree;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

import static org.junit.Assert.*;

public class BTreeTest {

        private File indexFile;

        @Before
        public void setUp() throws Exception {
                indexFile = new File(SQLBaseTest.internalData, "btreetest.idx");
                if (indexFile.exists()) {
                        if (!indexFile.delete()) {
                                throw new IOException("Cannot delete the index file");
                        }
                }
        }

        @Test
        public void testIndexRealData() throws Exception {
                SQLDataSourceFactory dsf = new SQLDataSourceFactory();
                dsf.setTempDir(SQLBaseTest.backupDir.getAbsolutePath());
                dsf.setResultDir(SQLBaseTest.backupDir);
                File file = new File(SQLBaseTest.internalData, "hedgerow.shp");
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
                        int[] rows = tree.getRow(value);
                        for (int row : rows) {
                                assertTrue(ds.getFieldValue(row, fieldIndex).equals(value).getAsBoolean());
                        }
                }
        }
}
