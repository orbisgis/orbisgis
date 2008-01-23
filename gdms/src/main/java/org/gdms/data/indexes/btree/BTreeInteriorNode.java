package org.gdms.data.indexes.btree;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;

public class BTreeInteriorNode extends AbstractBTreeNode implements BTreeNode {

	private BTreeNode[] children;

	public BTreeInteriorNode(BTreeInteriorNode parent, int n) {
		super(parent, n);
		// one place if for overload management
		children = new BTreeNode[n + 2];
	}

	public BTreeInteriorNode(BTreeInteriorNode parent, int n, BTreeNode left,
			BTreeNode right) {
		this(parent, n);
		values[0] = right.getSmallestValueNotIn(left);
		valueCount++;
		setChild(0, left);
		setChild(1, right);

	}

	private void setChild(int i, BTreeNode node) {
		children[i] = node;
		if (node != null) {
			node.setParent(this);
		}
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
			insertValueAndReference(smallestNotInOldNode, newNode);

			// Move half the values to the new node
			int mIndex = 0;
			values[(n + 1) / 2] = null;
			for (int i = (n + 1) / 2 + 1; i < values.length; i++) {
				m.values[mIndex] = values[i];
				m.setChild(mIndex, getChild(i));
				mIndex++;
				values[i] = null;
				setChild(i, null);
			}
			m.setChild(mIndex, getChild(n + 1));
			setChild(n + 1, null);
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
			insertValueAndReference(smallestNotInOldNode, newNode);
			return null;
		}
	}

	private BTreeNode getChild(int i) {
		return children[i];
	}

	private void insertValueAndReference(Value v, BTreeNode node) {
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
		setChild(valueCount + 1, getChild(valueCount));
		for (int j = valueCount; j >= index + 1; j--) {
			values[j] = values[j - 1];
			setChild(j, getChild(j - 1));
		}
		values[index] = v;
		setChild(index + 1, node);
		node.setParent(this);
		valueCount++;
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
			Value ret = getChild(i).getSmallestValueNotIn(treeNode);
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
				return getChild(i).getChildNodeFor(v);
			}
		}

		return getChild(valueCount).getChildNodeFor(v);
	}

	public boolean contains(Value v) {
		for (int i = 0; i < valueCount; i++) {
			if (values[i].isNull() || (v.less(values[i]).getAsBoolean())) {
				return getChild(i).contains(v);
			}
		}

		return getChild(valueCount).contains(v);
	}
}
