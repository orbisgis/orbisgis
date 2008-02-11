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

		indexFile = new File(SourceTest.backupDir, "btreetest.idx");
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
		BTree tree = new DiskBTree(3, 256);
		tree.newIndex(indexFile);
		makeInsertions(tree, 0, 2, 1, 3, 5, 4, 6, 7, 8, 9);
		tree.close();
		tree = new DiskBTree(3, 256);
		setUp();
		tree.newIndex(indexFile);
		makeInsertions(tree, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
		tree.close();
	}

	public void testDeletions() throws Exception {
		BTree tree = new DiskBTree(3, 256);
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
		BTree tree = new DiskBTree(3, 256);
		tree.newIndex(indexFile);
		makeInsertions(tree, 0, 0, 1, 1, 1, 2, 2, 2, 3, 4);
		makeDeletions(tree, 4, 2, 2, 1, 1, 0, 3, 2, 1, 0);
	}

	public void testIndexRealData() throws Exception {
		DataSourceFactory dsf = new DataSourceFactory();
		File file = new File(SourceTest.externalData
				+ "shp/bigshape2D/cantons.dbf");
		dsf.getSourceManager().register("cantons", file);
		DataSource ds = dsf
				.getDataSourceFromSQL("select * from cantons order by \"PTOT99\";");
		File repeatedValuesFile = new File(SourceTest.externalData
				+ "shp/mediumshape2D/landcover2000.dbf");
//TODO		testIndexRealData(new DiskBTree(32, 64), dsf
//				.getDataSource(repeatedValuesFile), "type");
//		setUp();
		testIndexRealData(new DiskBTree(255, 512), ds, "CODECANT");
		setUp();
		testIndexRealData(new DiskBTree(3, 256), ds, "CODECANT");
	}

	private void testIndexRealData(BTree tree, DataSource ds, String fieldName)
			throws Exception {
		ds.open();
		tree.newIndex(indexFile);
		int fieldIndex = ds.getFieldIndexByName(fieldName);
		long t1 = System.currentTimeMillis();
		for (int i = 0; i < ds.getRowCount(); i++) {
			if (i / 100 == i / 100.0) {
				tree.close();
				tree.openIndex(indexFile);
				checkLookUp(tree, ds, fieldIndex);
				System.out.println(i);
			}
			tree.checkTree();
			tree.insert(ds.getFieldValue(i, fieldIndex), i);
		}
		long t2 = System.currentTimeMillis();
		System.out.println("TOTAL: " + (t2 - t1));
		for (int i = 0; i < ds.getRowCount(); i++) {
			if (i / 100 == i / 100.0) {
				tree.save();
				checkLookUp(tree, ds, fieldIndex);
				System.out.println(i);
			}
			Value value = ds.getFieldValue(i, fieldIndex);
			tree.checkTree();
			tree.delete(value, i);
		}

		ds.cancel();
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
		BTree tree = new DiskBTree(n, blockSize);
		tree.newIndex(indexFile);
		makeInsertions(tree, 0, 2, 1, 3, 5, 4, 6, 7, 8, 9);
		tree.close();
		tree = new DiskBTree(3, 16);
		setUp();
		tree.newIndex(indexFile);
		makeInsertions(tree, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
		tree.close();
	}

	public void testNodeBiggerThanBlock() throws Exception {
		testInsertions(256, 16);
	}

	public void testEmptyIndex() throws Exception {
		BTree tree = new DiskBTree(5, 64);
		tree.newIndex(indexFile);
		tree.save();
		tree.close();
		tree.openIndex(indexFile);
		assertTrue(tree.size() == 0);
		tree.checkTree();
	}

	public void testIndexWithZeroElements() throws Exception {
		BTree tree = new DiskBTree(5, 64);
		tree.newIndex(indexFile);
		makeInsertions(tree, 0, 2, 1, 3, 5, 4, 6, 7, 8, 9);
		makeDeletions(tree, 0, 2, 1, 3, 5, 4, 6, 7, 8, 9);
		tree.save();
		tree.close();
		tree.openIndex(indexFile);
		assertTrue(tree.size() == 0);
	}

	public void testEmptySpaces() throws Exception {
		BTree tree = new DiskBTree(5, 32);
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

}
