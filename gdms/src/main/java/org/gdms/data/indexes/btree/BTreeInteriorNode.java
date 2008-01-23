package org.gdms.data.indexes.btree;

import org.gdms.data.values.Value;

public class BTreeInteriorNode extends AbstractBTreeNode implements BTreeNode {

	BTreeNode[] childs;

	public BTreeInteriorNode(BTreeInteriorNode parent, int n) {
		super(parent, n);
		childs = new BTreeNode[n + 1];
	}

	public BTreeInteriorNode(BTreeInteriorNode parent, int n, Value value,
			BTreeLeaf left, BTreeLeaf right) {
		this(parent, n);
		values[0] = right.values[0];
		valueCount++;
		childs[0] = left;
		childs[1] = right;
		left.setParent(this);
		right.setParent(this);

	}

	public boolean isLeave() {
		return false;
	}

	/**
	 * Gets the node where the value can be or should be inserted
	 *
	 * @param v
	 * @return
	 */
	public BTreeLeaf getChildNodeFor(Value v) {
		for (int i = 0; i < valueCount; i++) {
			if (v.less(values[i]).getAsBoolean()) {
				return childs[i].getChildNodeFor(v);
			}
		}

		return childs[valueCount].getChildNodeFor(v);
	}

	/**
	 * Reorganizes the tree with the new leaf that have appeared. It must set
	 * the parent of the leave (still null)
	 *
	 * @param v
	 * @return
	 */
	public BTreeNode reorganize(Value v, BTreeNode right) {
		if (valueCount == n) {
			// split the node, insert and reorganize the tree
			BTreeInteriorNode m = new BTreeInteriorNode(null, n);
			m.childs[0] = right;
			right.setParent(m);
			int mIndex = 0;
			for (int i = (n + 1) / 2; i < n; i++) {
				m.values[mIndex] = values[i];
				m.childs[mIndex + 1] = childs[i + 1];
				mIndex++;
				values[i] = null;
				childs[i + 1] = null;
			}
			m.valueCount = mIndex;
			valueCount = (n + 1) / 2;

			return parent.reorganize(v, m);
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
				childs[j] = childs[j - 1];
			}
			values[index] = v;
			childs[index + 1] = right;
			right.setParent(this);
			valueCount++;
			return null;
		}
	}

	public BTreeNode insert(Value v, int rowIndex) {
		throw new UnsupportedOperationException("Cannot insert "
				+ "value in an interior node");
	}

	public int getRow(Value value) {
		throw new UnsupportedOperationException("Cannot get the row "
				+ "in an interior node");
	}

	@Override
	public String toString() {
		StringBuilder strValues = new StringBuilder("");
		String separator = "";
		for (Value v : this.values) {
			strValues.append(separator).append(v);
			separator = ", ";
		}
		StringBuilder strChilds = new StringBuilder("");
		separator = "";
		for (BTreeNode v : this.childs) {
			if (v != null) {
				strChilds.append(separator)
						.append(((AbstractBTreeNode) v).name);
			} else {
				strChilds.append(separator).append("null");
			}
			separator = ", ";
		}

		String ret = name + "\n (" + strValues.toString() + ") \n(" + strChilds
				+ ")\n";
		for (BTreeNode node : childs) {
			if (node != null) {
				ret += node.toString();
			}
		}

		return ret;
	}

}
