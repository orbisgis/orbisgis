package org.gdms.data.indexes.btree;

import java.io.IOException;
import java.util.ArrayList;

import org.gdms.data.values.Value;

public abstract class AbstractBTreeNode implements BTreeNode {

	private static int nodes = 0;

	protected ArrayList<Value> values;
	private int parentDir;
	protected String name;

	protected DiskBTree tree;

	protected int dir;

	private BTreeInteriorNode parent;

	public AbstractBTreeNode(DiskBTree btree, int dir, int parentDir) {
		this.tree = btree;
		this.dir = dir;
		this.parentDir = parentDir;
		values = new ArrayList<Value>();
		this.name = "node-" + nodes;
		nodes++;
	}

	public void setParentDir(int parentDir) {
		if (this.parentDir != parentDir) {
			this.parentDir = parentDir;
			this.parent = null;
		}
	}

	protected abstract boolean isValid(int valueCount) throws IOException;

	public BTreeInteriorNode getParent() throws IOException {
		if ((parent == null) && (parentDir != -1)) {
			parent = (BTreeInteriorNode) tree.readNodeAt(parentDir);
		}
		return parent;
	}

	/**
	 * Gets the index of a value. If the value exist it returns its index.
	 * Otherwise it returns the place where it should be inserted
	 *
	 * @param v
	 *            search key
	 * @param values
	 *            keys to search
	 * @param valueCount
	 *            number of values
	 * @return The index in the value array where this value will be inserted
	 */
	protected int getIndexOf(Value v) {
		int index = values.size();
		for (int i = 0; i < values.size(); i++) {
			if (values.get(i).isNull() || v.lessEqual(values.get(i)).getAsBoolean()) {
				index = i;
				break;
			}
		}
		return index;
	}

	public int getParentDir() {
		return parentDir;
	}

	public int getDir() {
		return dir;
	}

	public boolean canGiveElement() throws IOException {
		return isValid(values.size() - 1);
	}
}
