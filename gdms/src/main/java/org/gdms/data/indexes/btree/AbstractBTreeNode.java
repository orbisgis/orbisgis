package org.gdms.data.indexes.btree;

import org.gdms.data.values.Value;

public class AbstractBTreeNode {

	private static int nodes = 0;

	protected Value[] values;
	protected int valueCount;
	protected int n;
	protected BTreeInteriorNode parent;
	protected String name;

	public AbstractBTreeNode(BTreeInteriorNode parent, int n) {
		this.parent = parent;
		values = new Value[n];
		valueCount = 0;
		this.n = n;
		this.name = "node-" + nodes;
		nodes++;
	}

	public void setParent(BTreeInteriorNode parent) {
		this.parent = parent;
	}
}
