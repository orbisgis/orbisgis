package org.gdms.data.indexes;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.gdms.data.indexes.btree.BTree;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;

public class BTreeTest extends TestCase {

	private ArrayList<Value> v;

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
	}

	private void checkLookUp(BTree tree) {
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
		BTree tree = new BTree(3);
		makeInsertions(tree, 0, 2, 1, 3, 5, 4, 6, 7, 8, 9);
		tree = new BTree(3);
		makeInsertions(tree, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0);
	}

	public void testDeletions() throws Exception {
		BTree tree = new BTree(3);
		makeInsertions(tree, 0, 2, 1, 3, 5, 4, 6, 7, 8, 9);
		makeDeletions(tree, 2, 4, 6, 8, 9, 7, 5, 3, 1, 0);
	}

	private void makeDeletions(BTree tree, int... vIndexes) {
		for (int index : vIndexes) {
			Value value = v.get(index);
			tree.delete(value);
			checkLookUp(tree);
		}
	}

	private void makeInsertions(BTree tree, int... vIndexes) {
		for (int index : vIndexes) {
			tree.insert(v.get(index), index);
			checkLookUp(tree);
		}
	}

	public void testRepeatedValues() throws Exception {
		BTree tree = new BTree(3);
		makeInsertions(tree, 0, 0, 1, 1, 1, 2, 2, 2, 3, 4);
	}
}
