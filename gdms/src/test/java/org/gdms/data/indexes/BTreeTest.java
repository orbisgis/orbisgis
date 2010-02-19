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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.gdms.SourceTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.indexes.btree.BTree;
import org.gdms.data.indexes.btree.DiskBTree;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;

public class BTreeTest extends TestCase {

	private ArrayList<Value> v;
	private File indexFile;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

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

		indexFile = new File("src/test/resources/backup", "btreetest.idx");
		if (indexFile.exists()) {
			if (!indexFile.delete()) {
				throw new IOException("Cannot delete the index file");
			}
		}
	}

	private void checkLookUp(BTree tree) throws IOException {
		tree.checkTree();
		assertTrue(tree.size() == tree.getAllValues().length);
		Value[] keys = tree.getAllValues();
		for (int i = 0; i < keys.length; i++) {
			int[] indexes = tree.getRow(keys[i]);
			for (int index : indexes) {
				assertTrue("value: " + keys[i], v.get(index).equals(keys[i])
						.getAsBoolean());
			}
		}
	}

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

	public void testRepeatedValues() throws Exception {
		BTree tree = new DiskBTree(3, 256, false);
		tree.newIndex(indexFile);
		makeInsertions(tree, 0, 0, 1, 1, 1, 2, 2, 2, 3, 4);
		makeDeletions(tree, 4, 2, 2, 1, 1, 0, 3, 2, 1, 0);
	}

	public void testIndexRealData() throws Exception {
		DataSourceFactory dsf = new DataSourceFactory();
		File file = new File(SourceTest.internalData + "hedgerow.shp");
		dsf.getSourceManager().register("hedges", file);
		DataSource ds = dsf
				.getDataSourceFromSQL("select * from hedges order by type ;");
		File repeatedValuesFile = new File(SourceTest.internalData
				+ "landcover2000.dbf");
		testIndexRealData(new DiskBTree(3, 64, false), dsf
				.getDataSource(repeatedValuesFile), "type", 100.0);
		setUp();
		testIndexRealData(new DiskBTree(32, 64, false), dsf
				.getDataSource(repeatedValuesFile), "type", 100.0);
		setUp();
		testIndexRealData(new DiskBTree(255, 512, false), ds, "CODECANT", 100.0);
		setUp();
		testIndexRealData(new DiskBTree(3, 256, false), ds, "CODECANT", 1000.0);
	}

	private void testIndexRealData(BTree tree, DataSource ds, String fieldName,
			double checkPeriod) throws Exception {
		ds.open();
		tree.newIndex(indexFile);
		int fieldIndex = ds.getFieldIndexByName(fieldName);
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
			assertTrue(tree.size() + 1 == size);
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
				assertTrue(ds.getFieldValue(row, fieldIndex).equals(value)
						.getAsBoolean());
			}
		}
	}

	public void testSmallNode() throws Exception {
		testInsertions(3, 32);
	}

	private void testInsertions(int n, int blockSize) throws IOException,
			Exception {
		BTree tree = new DiskBTree(n, blockSize, false);
		tree.newIndex(indexFile);
		makeInsertions(tree, 0, 2, 1, 3, 5, 4, 6, 7, 8, 9);
		tree.close();
		tree = new DiskBTree(3, 16, false);
		setUp();
		tree.newIndex(indexFile);
		makeInsertions(tree, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
		tree.close();
	}

	public void testNodeBiggerThanBlock() throws Exception {
		testInsertions(256, 16);
	}

	public void testEmptyIndex() throws Exception {
		BTree tree = new DiskBTree(5, 64, false);
		tree.newIndex(indexFile);
		tree.save();
		tree.close();
		tree.openIndex(indexFile);
		assertTrue(tree.size() == 0);
		tree.checkTree();
	}

	public void testIndexWithZeroElements() throws Exception {
		BTree tree = new DiskBTree(5, 64, false);
		tree.newIndex(indexFile);
		makeInsertions(tree, 0, 2, 1, 3, 5, 4, 6, 7, 8, 9);
		makeDeletions(tree, 0, 2, 1, 3, 5, 4, 6, 7, 8, 9);
		assertTrue(tree.size() == 0);
		tree.save();
		tree.close();
		tree.openIndex(indexFile);
		assertTrue(tree.size() == 0);
		assertTrue(tree.getRow(ValueFactory.createValue(0)).length == 0);
	}

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
		assertTrue(emptyBlocks == 0);
		tree.close();
		tree.openIndex(indexFile);
		// The number of empty nodes have not changed after closing
		assertTrue(emptyBlocks == ((DiskBTree) tree).getEmptyBlocks());
		makeInsertions(tree, 0, 2, 1, 3, 5, 4, 6, 7, 8, 9);
		assertTrue(((DiskBTree) tree).getEmptyBlocks() == 0);
		// clean it again
		makeDeletions(tree, 0, 2, 1, 3, 5, 4, 6, 7, 8, 9);
		assertTrue(emptyBlocks == ((DiskBTree) tree).getEmptyBlocks());
	}

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
		assertTrue(tree.getRow(ValueFactory.createNullValue(), false,
				ValueFactory.createValue(1), true).length == 5);
		assertTrue(tree.getRow(ValueFactory.createNullValue(), false,
				ValueFactory.createValue(1), false).length == 2);
		assertTrue(tree.getRow(ValueFactory.createValue(3), true, ValueFactory
				.createValue(3), true).length == 1);
		assertTrue(tree.getRow(ValueFactory.createValue(1), false, ValueFactory
				.createValue(4), false).length == 4);
		assertTrue(tree.getRow(ValueFactory.createValue(1), false, ValueFactory
				.createNullValue(), false).length == 5);
		assertTrue(tree.getRow(ValueFactory.createNullValue(), true,
				ValueFactory.createNullValue(), false).length == 10);
		assertTrue(tree.getRow(ValueFactory.createNullValue(), true,
				ValueFactory.createValue(0), false).length == 0);
	}

	public void testNotExistentValues() throws Exception {
		BTree tree = new DiskBTree(5, 32, false);
		tree.newIndex(indexFile);
		// populate the index
		makeInsertions(tree, 0, 2, 1, 3, 5, 4, 6, 7, 8, 9);
		String snapshot = tree.toString();
		tree.delete(ValueFactory.createValue(19257), 2834);
		tree.delete(ValueFactory.createValue(0), 2834);
		tree.checkTree();
		assertTrue(tree.getRow(ValueFactory.createValue(2834)).length == 0);
		String snapshot2 = tree.toString();
		assertTrue(snapshot.equals(snapshot2));
		tree.close();
	}

}
