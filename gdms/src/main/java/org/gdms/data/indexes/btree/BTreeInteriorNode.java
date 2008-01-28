package org.gdms.data.indexes.btree;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;

/**
 * @author Fernando Gonzalez Cortes
 *
 */
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

	public boolean isLeaf() {
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
			if (getParent() == null) {
				// We need a new root
				BTreeInteriorNode newRoot = new BTreeInteriorNode(null, n,
						this, m);

				return newRoot;
			} else {
				return getParent().reorganize(m.getSmallestValueNotIn(this), m);
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
		shiftValuesFromIndexToRight(index, 1);
		for (int j = valueCount; j >= index + 1; j--) {
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
		for (int i = 0; i < valueCount; i++) {
			Value v = values[i];
			strValues.append(separator).append(v);
			separator = ", ";
		}
		StringBuilder strChilds = new StringBuilder("");
		separator = "";
		for (int i = 0; i < valueCount + 1; i++) {
			BTreeNode v = children[i];
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
		for (int i = 0; i < valueCount + 1; i++) {
			BTreeNode node = children[i];
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

	public BTreeLeaf getFirstLeaf() {
		return getChild(0).getFirstLeaf();
	}

	/**
	 * Notifies that there have been changes in the node specified as a
	 * parameter that may invalidate the values in this node
	 *
	 * @param changingNode
	 */
	public void smallestNotInLeftElementChanged(BTreeNode changingNode) {
		// find the pointer
		int childIndex = getIndexOf(changingNode);

		if (childIndex > 0) {
			values[childIndex - 1] = changingNode
					.getSmallestValueNotIn(getChild(childIndex - 1));
		}
		if (childIndex < 2) {
			BTreeInteriorNode parnt = getParent();
			if (parnt != null) {
				parnt.smallestNotInLeftElementChanged(this);
			}
		}
	}

	/**
	 * Gets the index of the leaf in the children array
	 *
	 * @param node
	 * @return -1 if the node is not present
	 */
	private int getIndexOf(BTreeNode node) {
		int childIndex = -1;
		for (int i = 0; i < valueCount + 1; i++) {
			if (getChild(i) == node) {
				childIndex = i;
				break;
			}
		}
		return childIndex;
	}

	/**
	 * Merges the node with one of its neighbours.
	 *
	 * @param node
	 * @param smallestChanged
	 *            If the node to merge have suffered a deletion in its smallest
	 *            value
	 * @return true if the merge of the node with one of its neighbours caused a
	 *         change the smallest value in this node
	 */
	public void mergeWithNeighbour(AbstractBTreeNode node) {
		int index = getIndexOf(node);
		AbstractBTreeNode smallest = null;
		AbstractBTreeNode rightNeighbour = getRightNeighbour(index);
		AbstractBTreeNode leftNeighbour = getLeftNeighbour(index);

		if ((rightNeighbour != null) && (leftNeighbour != null)) {
			smallest = rightNeighbour;
			if (leftNeighbour.valueCount < rightNeighbour.valueCount) {
				smallest = leftNeighbour;
			}
		} else if (rightNeighbour != null) {
			smallest = rightNeighbour;
		} else if (leftNeighbour != null) {
			smallest = leftNeighbour;
		} else {
			throw new RuntimeException("bug: root shouldn't reach this point");
		}
		if (smallest == leftNeighbour) {
			node.mergeWithLeft(leftNeighbour);
			// Remove the pointer to the left neighbour
			for (int i = index - 1; i < valueCount; i++) {
				setChild(i, getChild(i + 1));
			}
			// remove the values of the left neighbour
			for (int i = index - 1; i < valueCount; i++) {
				values[i] = values[i + 1];
			}
			valueCount--;
			if ((index == 1) && (getParent() != null)) {
				// values[0] has been modified
				getParent().smallestNotInLeftElementChanged(this);
			}
		} else {
			node.mergeWithRight(rightNeighbour);
			// Remove the pointer to the right neighbour
			for (int i = index + 1; i < valueCount; i++) {
				setChild(i, getChild(i + 1));
			}
			// Remove the values to the right neighbour
			for (int i = index; i < valueCount; i++) {
				values[i] = values[i + 1];
			}
			valueCount--;

			if ((index == 0) && (getParent() != null)) {
				// values[0] has been modified
				getParent().smallestNotInLeftElementChanged(this);
			}
		}

		// update the node index
		if (smallest == leftNeighbour) {
			index--;
		}

		if (index > 0) {
			values[index - 1] = node.getSmallestValueNotIn(getChild(index - 1));
		}
	}

	protected void mergeWithRight(AbstractBTreeNode rightNode) {
		BTreeInteriorNode node = (BTreeInteriorNode) rightNode;
		Value[] newValues = new Value[n + 1];
		BTreeNode[] newChildren = new BTreeNode[n + 1];
		System.arraycopy(values, 0, newValues, 0, valueCount);
		System.arraycopy(node.values, 0, newValues, valueCount + 1,
				node.valueCount);
		newValues[valueCount] = rightNode.getSmallestValueNotIn(this);
		System.arraycopy(children, 0, newChildren, 0, valueCount + 1);
		// Change the parent in the moving children
		for (int i = 0; i < node.valueCount + 1; i++) {
			node.getChild(i).setParent(this);
		}
		System.arraycopy(node.children, 0, newChildren, valueCount + 1,
				node.valueCount + 1);
		values = newValues;
		children = newChildren;
		valueCount = valueCount + node.valueCount + 1;
	}

	protected void mergeWithLeft(AbstractBTreeNode leftNode) {
		BTreeInteriorNode node = (BTreeInteriorNode) leftNode;
		Value[] newValues = new Value[n + 1];
		BTreeNode[] newChildren = new BTreeNode[n + 1];
		System.arraycopy(node.values, 0, newValues, 0, node.valueCount);
		System.arraycopy(values, 0, newValues, node.valueCount+1, valueCount);
		newValues[node.valueCount] = this.getSmallestValueNotIn(leftNode);
		// Change the parent in the moving children
		for (int i = 0; i < node.valueCount + 1; i++) {
			node.getChild(i).setParent(this);
		}
		System.arraycopy(node.children, 0, newChildren, 0, node.valueCount + 1);
		System.arraycopy(children, 0, newChildren, node.valueCount + 1,
				valueCount + 1);
		values = newValues;
		children = newChildren;
		valueCount = valueCount + node.valueCount + 1;
	}

	/**
	 * Selects the neighbour and moves the nearest element into this node if it
	 * is possible
	 *
	 * @param node
	 * @return true if it is possible to move from a neighbour and false
	 *         otherwise
	 */
	public boolean moveFromNeighbour(AbstractBTreeNode node) {
		int index = getIndexOf(node);
		AbstractBTreeNode rightNeighbour = getRightNeighbour(index);
		AbstractBTreeNode leftNeighbour = getLeftNeighbour(index);
		if ((rightNeighbour != null)
				&& (rightNeighbour.isValid(rightNeighbour.valueCount - 1))) {
			rightNeighbour.moveFirstTo(node);
			this.smallestNotInLeftElementChanged(rightNeighbour);
			this.smallestNotInLeftElementChanged(node);
			return true;
		} else if ((leftNeighbour != null)
				&& (leftNeighbour.isValid(leftNeighbour.valueCount - 1))) {
			leftNeighbour.moveLastTo(node);
			this.smallestNotInLeftElementChanged(node);
			return true;
		} else {
			return false;
		}
	}

	protected void moveFirstTo(AbstractBTreeNode node) {
		BTreeInteriorNode n = (BTreeInteriorNode) node;
		n.values[n.valueCount] = this.getChild(0).getSmallestValueNotIn(
				n.getChild(n.valueCount));
		n.setChild(n.valueCount + 1, this.getChild(0));

		shiftToLeft();
		n.valueCount++;
		this.valueCount--;
	}

	private void shiftToLeft() {
		for (int i = 1; i < valueCount; i++) {
			values[i - 1] = values[i];
			setChild(i - 1, getChild(i));
		}
		setChild(valueCount - 1, getChild(valueCount));
	}

	protected void moveLastTo(AbstractBTreeNode node) {
		BTreeInteriorNode n = (BTreeInteriorNode) node;
		n.shiftToRight();
		n.values[0] = node.getSmallestValueNotIn(this);
		n.setChild(0, this.getChild(valueCount));
		n.valueCount++;
		this.valueCount--;
	}

	private void shiftToRight() {
		for (int i = valueCount; i > 0; i--) {
			values[i] = values[i - 1];
			setChild(i + 1, getChild(i));
		}
		setChild(1, getChild(0));
	}

	/**
	 * Gets the neighbour on the left of the specified node
	 *
	 * @param index
	 * @return
	 */
	private AbstractBTreeNode getLeftNeighbour(int index) {
		if (index > 0) {
			return (AbstractBTreeNode) getChild(index - 1);
		} else {
			return null;
		}
	}

	/**
	 * Gets the neighbour on the right of the specified node
	 *
	 * @param index
	 * @return
	 */
	private AbstractBTreeNode getRightNeighbour(int index) {
		if (index < valueCount) {
			return (AbstractBTreeNode) getChild(index + 1);
		} else {
			return null;
		}
	}

	@Override
	protected BTreeNode getChildForNewRoot() {
		BTreeNode child = getChild(0);
		child.setParent(null);
		return child;
	}

	@Override
	protected boolean isValid(int valueCount) {
		if (getParent() == null) {
			return valueCount >= 1;
		} else {
			return valueCount + 1 >= ((n + 1) / 2);
		}
	}

	public void checkTree() {
		if (!isValid(valueCount)) {
			throw new RuntimeException(this + " Not enough childs");
		} else {
			for (int i = 0; i < valueCount + 1; i++) {
				if (i < valueCount) {
					if (getChild(i) instanceof BTreeLeaf) {
						BTreeLeaf leaf = (BTreeLeaf) getChild(i);
						if (leaf.getRightNeighbour() != getChild(i + 1)) {
							throw new RuntimeException(leaf
									+ " bad right neighbour");
						}
					}
				}
				if (getChild(i).getParent() != this) {
					throw new RuntimeException(this + " parent is wrong");
				}
				getChild(i).checkTree();

				if (i > 0) {
					if (getChild(i).getSmallestValueNotIn(getChild(i - 1))
							.notEquals(values[i - 1]).getAsBoolean()) {
						throw new RuntimeException("The " + i
								+ "th value is not right");
					}
				}
			}
		}
	}

	public BTreeNode delete(Value v) {
		throw new RuntimeException("Cannot delete in an interior node");
	}

}
