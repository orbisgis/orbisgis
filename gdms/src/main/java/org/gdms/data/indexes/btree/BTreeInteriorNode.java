package org.gdms.data.indexes.btree;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;

public class BTreeInteriorNode extends AbstractBTreeNode implements BTreeNode {

	BTreeNode[] children;

	public BTreeInteriorNode(BTreeInteriorNode parent, int n) {
		super(parent, n);
		children = new BTreeNode[n + 2]; // one place if for overload
		// management
	}

	public BTreeInteriorNode(BTreeInteriorNode parent, int n, BTreeNode left,
			BTreeNode right) {
		this(parent, n);
		values[0] = right.getSmallestValueNotIn(left);
		valueCount++;
		children[0] = left;
		children[1] = right;
		left.setParent(this);
		right.setParent(this);

	}

	public boolean isLeave() {
		return false;
	}

	/**
	 * Reorganizes the tree with the new leaf that have appeared. It must set
	 * the parent of the leave (still null)
	 *
	 * @param v
	 * @return
	 */
	public BTreeNode reorganize(Value smallestNotInOldNode, BTreeNode newNode) {
		if (valueCount == n) {
			// split the node, insert and reorganize the tree
			BTreeInteriorNode m = new BTreeInteriorNode(null, n);
			newNode.setParent(m);

			// Create the value array with the new index
			insertValueAndReference(smallestNotInOldNode, newNode, values,
					valueCount, children);

			// Move half the values to the new node
			int mIndex = 0;
			values[(n + 1) / 2] = null;
			for (int i = (n + 1) / 2 + 1; i < values.length; i++) {
				m.values[mIndex] = values[i];
				m.children[mIndex] = children[i];
				mIndex++;
				values[i] = null;
				children[i] = null;
			}
			children[n + 1] = null;
			m.children[mIndex] = newNode;
			m.valueCount = mIndex;
			valueCount = (n + 1) / 2;
			if (parent == null) {
				// We need a new root
				BTreeInteriorNode newRoot = new BTreeInteriorNode(null, n,
						this, m);

				return newRoot;
			} else {
				return parent.reorganize(m.getSmallestValueNotIn(this), m);
			}
		} else {
			valueCount = insertValueAndReference(smallestNotInOldNode, newNode,
					values, valueCount, children);
			return null;
		}
	}

	private int insertValueAndReference(Value v, BTreeNode node,
			Value[] values, int valueCount, BTreeNode[] children) {
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
			children[j] = children[j - 1];
		}
		values[index] = v;
		children[index + 1] = node;
		node.setParent(this);
		valueCount++;
		return valueCount;
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
		for (BTreeNode v : this.children) {
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
		for (BTreeNode node : children) {
			if (node != null) {
				ret += node.toString();
			}
		}

		return ret;
	}

	public Value getSmallestValueNotIn(BTreeNode treeNode) {
		for (int i = 0; i < valueCount + 1; i++) {
			Value ret = children[i].getSmallestValueNotIn(treeNode);
			if (ret != null) {
				return ret;
			}
		}

		return ValueFactory.createNullValue();
	}

	/**
	 * Gets the node where the value can be or should be inserted
	 *
	 * @param v
	 * @return
	 */
	public BTreeLeaf getChildNodeFor(Value v) {
		for (int i = 0; i < valueCount; i++) {
			if (values[i].isNull() || (v.less(values[i]).getAsBoolean())) {
				return children[i].getChildNodeFor(v);
			}
		}

		return children[valueCount].getChildNodeFor(v);
	}

	public boolean contains(Value v) {
		for (int i = 0; i < valueCount; i++) {
			if (values[i].isNull() || (v.less(values[i]).getAsBoolean())) {
				return children[i].contains(v);
			}
		}

		return children[valueCount].contains(v);
	}
}
