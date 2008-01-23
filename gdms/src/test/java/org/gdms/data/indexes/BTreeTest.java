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
			assertTrue("value: " + v[i], tree.getRow(v[i]) == i);
		}
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

		checkLookUp(tree);
	}
}
