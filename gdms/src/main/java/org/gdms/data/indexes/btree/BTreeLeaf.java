package org.gdms.data.indexes.btree;

import org.gdms.data.values.Value;

public class BTreeLeaf extends AbstractBTreeNode implements BTreeNode {

	private int[] rows;
	private BTreeLeaf rightNeighbour;

	public BTreeLeaf(BTreeInteriorNode parent, int n) {
		super(parent, n);
		rows = new int[n];
	}

	public BTreeLeaf getChildNodeFor(Value v) {
		return this;
	}

	public BTreeNode insert(Value v, int rowIndex) {
		if (valueCount == values.length) {
			// split the node, insert and reorganize the tree
			BTreeLeaf right = new BTreeLeaf(null, n);
			right.insert(v, rowIndex);
			for (int i = (n + 1) / 2; i < n; i++) {
				right.insert(values[i], rows[i]);
				values[i] = null;
				rows[i] = 0;
			}
			valueCount = (n + 1) / 2;
			this.rightNeighbour = right;

			if (parent == null) {
				// It's the root
				BTreeInteriorNode newRoot = new BTreeInteriorNode(null, n,
						values[0], this, right);

				return newRoot;
			} else {
				return parent.reorganize(right.values[0], right);
			}
		} else {
			// Look the place to insert the new value
			int index = valueCount;
			for (int i = 0; i < valueCount; i++) {
				if (v.less(values[i]).getAsBoolean()) {
					// will insert at i
					index = i;
					break;
				}
			}

			// insert in index
			for (int j = valueCount; j >= index + 1; j--) {
				values[j] = values[j - 1];
				rows[j] = rows[j - 1];
			}
			values[index] = v;
			rows[index] = rowIndex;
			valueCount++;

			return null;
		}
	}

	public boolean isLeave() {
		return true;
	}

	public int getIndex(Value value) {
		for (int i = 0; i < valueCount; i++) {
			if (values[i].equals(value).getAsBoolean()) {
				return rows[i];
			}
		}

		return -1;
	}

	@Override
	public String toString() {
		StringBuilder strValues = new StringBuilder("");
		String separator = "";
		for (Value v : this.values) {
			strValues.append(separator).append(v);
			separator = ", ";
		}

		return name + " (" + strValues.toString() + ") ";
	}
}