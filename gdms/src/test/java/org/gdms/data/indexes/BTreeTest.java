package org.gdms.data.indexes;

import org.gdms.data.indexes.btree.BTree;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;

import junit.framework.TestCase;

public class BTreeTest extends TestCase {

	private Value[] v;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		v = new Value[] { ValueFactory.createValue(2),
				ValueFactory.createValue(3), ValueFactory.createValue(5),
				ValueFactory.createValue(7), ValueFactory.createValue(11),
				ValueFactory.createValue(13), ValueFactory.createValue(17),
				ValueFactory.createValue(19), ValueFactory.createValue(23),
				ValueFactory.createValue(29), ValueFactory.createValue(31),
				ValueFactory.createValue(37), ValueFactory.createValue(41),
				ValueFactory.createValue(43), ValueFactory.createValue(47) };
	}

	public void testLeafInsertion() throws Exception {
		BTree tree = new BTree(3);
		tree.insert(v[0], 0);
		tree.insert(v[2], 2);
		tree.insert(v[1], 1);

		checkLookUp(tree);
	}

	private void checkLookUp(BTree tree) {
		for (int i = 0; i < tree.size(); i++) {
			int[] indexes = tree.getRow(v[i]);
			if (!contains(i, indexes)) {
				assertTrue("value: " + v[i], false);
			}
		}
	}

	private boolean contains(int i, int[] indexes) {
		for (int j = 0; j < indexes.length; j++) {
			if (indexes[j] == i) {
				return true;
			}
		}

		return false;
	}

	public void testLeafAndIntermediateOverLoad() throws Exception {
		BTree tree = new BTree(3);
		tree.insert(v[0], 0);
		tree.insert(v[2], 2);
		tree.insert(v[1], 1);
		tree.insert(v[3], 3);
		tree.insert(v[5], 5);
		tree.insert(v[4], 4);
		tree.insert(v[6], 6);
		tree.insert(v[7], 7);
		tree.insert(v[8], 8);
		tree.insert(v[9], 9);

		checkLookUp(tree);
	}

	public void testRepeatedValues() throws Exception {
		BTree tree = new BTree(3);
		tree.insert(v[0], 0);
		tree.insert(v[0], 1);
		tree.insert(v[1], 2);
		tree.insert(v[1], 3);
		tree.insert(v[1], 4);
		tree.insert(v[2], 5);
		tree.insert(v[3], 8);
		tree.insert(v[2], 6);
		tree.insert(v[2], 7);
		tree.insert(v[3], 9);
		tree.insert(v[4], 10);
		assertTrue(contains(0, tree.getRow(v[0])));
		assertTrue(contains(1, tree.getRow(v[0])));
		assertTrue(contains(2, tree.getRow(v[1])));
		assertTrue(contains(3, tree.getRow(v[1])));
		assertTrue(contains(4, tree.getRow(v[1])));
		assertTrue(contains(5, tree.getRow(v[2])));
		assertTrue(contains(6, tree.getRow(v[2])));
		assertTrue(contains(7, tree.getRow(v[2])));
		assertTrue(contains(8, tree.getRow(v[3])));
		assertTrue(contains(9, tree.getRow(v[3])));
		assertTrue(contains(10, tree.getRow(v[4])));
	}
}
