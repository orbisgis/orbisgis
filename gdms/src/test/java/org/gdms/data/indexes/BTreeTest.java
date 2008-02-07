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
		BTree tree = new DiskBTree(3);
		tree.newIndex(indexFile);
		makeInsertions(tree, 0, 2, 1, 3, 5, 4, 6, 7, 8, 9);
		tree.close();
		tree = new DiskBTree(3);
		indexFile.delete();
		tree.newIndex(indexFile);
		makeInsertions(tree, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
		tree.close();
	}

	public void testDeletions() throws Exception {
		BTree tree = new DiskBTree(3);
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
			tree.delete(value);
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
		BTree tree = new DiskBTree(3);
		tree.newIndex(indexFile);
		makeInsertions(tree, 0, 0, 1, 1, 1, 2, 2, 2, 3, 4);
	}

	public void testIndexRealData() throws Exception {
		testIndexRealData(new DiskBTree(5));
		setUp();
		testIndexRealData(new DiskBTree(3));
	}

	private void testIndexRealData(BTree tree) throws Exception {
		DataSourceFactory dsf = new DataSourceFactory();
		dsf.getSourceManager()
				.register(
						"cantons",
						new File(SourceTest.externalData
								+ "shp/bigshape2D/cantons.dbf"));
		DataSource ds = dsf
				.executeSQL("select * from cantons order by PTOT99;");
		ds.open();
		tree.newIndex(indexFile);
		int fieldIndex = ds.getFieldIndexByName("CODECANT");
		long t1 = System.currentTimeMillis();
		for (int i = 0; i < ds.getRowCount(); i++) {
			if (i / 10 == i / 10.0) {
//				tree.save();
				System.out.println(i);
			}
			tree.checkTree();
			tree.insert(ds.getFieldValue(i, fieldIndex), i);
		}
		long t2 = System.currentTimeMillis();
		System.out.println("TOTAL: " + (t2 - t1));
		for (int i = 0; i < ds.getRowCount(); i++) {
			if (i / 10 == i / 10.0) {
//				tree.save();
				System.out.println(i);
			}
			Value value = ds.getFieldValue(i, fieldIndex);
			tree.checkTree();
			tree.delete(value);
		}

		ds.cancel();
//		tree.close();
	}

	public void testDisk() throws Exception {
		BTree tree = new DiskBTree(3);
		tree.newIndex(indexFile);
		for (int i = 0; i < 4; i++) {
			tree.insert(ValueFactory.createValue(i), i);
		}
		assertTrue(tree.size() == 4);
		for (int i = 0; i < 4; i++) {
			assertTrue(tree.getRow(ValueFactory.createValue(i))[0] == i);
		}
		tree.close();
		tree = new DiskBTree(5);
		tree.openIndex(indexFile);
		assertTrue(tree.size() == 4);
		for (int i = 0; i < 4; i++) {
			assertTrue(tree.getRow(ValueFactory.createValue(i))[0] == i);
		}
	}
}
