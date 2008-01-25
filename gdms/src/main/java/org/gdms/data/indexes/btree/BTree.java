package org.gdms.data.indexes.btree;

import org.gdms.data.values.Value;

public class BTree {

	private BTreeNode root;

	private int size = 0;

	public BTree(int n) {
		root = new BTreeLeaf(null, n);
	}

	public void insert(Value v, int rowIndex) {
		// find the apropiate leave
		BTreeNode node = root.getChildNodeFor(v);

		// Perform the insertion
		BTreeNode newRoot = node.insert(v, rowIndex);
		if (newRoot != null) {
			root = newRoot;
		}

		size++;
	}

	public void delete(Value v) {
		// find the apropiate leave
		BTreeNode node = root.getChildNodeFor(v);

		// Perform the deletion
		BTreeNode newRoot = node.delete(v);
		if (newRoot != null) {
			root = newRoot;
		}

		size--;
	}

	public int[] getRow(Value value) {
		BTreeLeaf node = root.getChildNodeFor(value);
		return node.getIndex(value);
	}

	public Value[] getAllValues() {
		BTreeLeaf firstLeaf = root.getFirstLeaf();
		return firstLeaf.getAllValues();
	}

	public int size() {
		return size;
	}

	public void checkTree() {
		root.checkTree();
	}

	@Override
	public String toString() {
		return root.toString();
	}
}
