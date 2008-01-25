package org.gdms.data.indexes.btree;

import org.gdms.data.values.Value;

public abstract class AbstractBTreeNode implements BTreeNode {

	private static int nodes = 0;

	protected Value[] values;
	protected int valueCount;
	protected int n;
	private AbstractBTreeNode parent;
	protected String name;

	public AbstractBTreeNode(AbstractBTreeNode parent, int n) {
		this.parent = parent;
		values = new Value[n + 1]; // for intermediate node overload management
		valueCount = 0;
		this.n = n;
		this.name = "node-" + nodes;
		nodes++;
	}

	public void setParent(BTreeInteriorNode parent) {
		this.parent = parent;
	}

	protected int getIndexOf(Value v) {
		for (int i = 0; i < values.length; i++) {
			if (values[i].equals(v).getAsBoolean()) {
				return i;
			}
		}

		return -1;
	}

	protected abstract boolean isValid(int valueCount);

	protected BTreeNode adjustAfterDeletion(boolean smallestChanged) {
		if (isValid(valueCount)) {
			return null;
		} else {
			if (parent == null) {
				// If it's the root just change the root
				return getChildForNewRoot();
			} else {
				if (((BTreeInteriorNode) parent).moveFromNeighbour(this)) {
					return adjustAfterDeletion(false);
				} else {
					smallestChanged = ((BTreeInteriorNode) parent)
							.mergeWithNeighbour(this, smallestChanged);
					return ((BTreeInteriorNode) parent)
							.adjustAfterDeletion(smallestChanged);
				}
			}
		}
	}

	/**
	 * When the root has less than the valid number of elements this method is
	 * called to substitute the root
	 *
	 * @return
	 */
	protected abstract BTreeNode getChildForNewRoot();

	/**
	 * Moves the first element into the specified node. The parameter is an
	 * instance of the same class than this
	 *
	 * @param node
	 */
	protected abstract void moveFirstTo(AbstractBTreeNode treeInteriorNode);

	/**
	 * Moves the first element into the specified node. The parameter is an
	 * instance of the same class than this
	 *
	 * @param node
	 */
	protected abstract void moveLastTo(AbstractBTreeNode treeInteriorNode);

	/**
	 * Takes all the content of the left node and puts it at the end of this
	 * node
	 */
	protected abstract void mergeWithRight(AbstractBTreeNode rightNode);

	/**
	 * Takes all the content of the left node and puts it at the beginning of
	 * this node
	 */
	protected abstract void mergeWithLeft(AbstractBTreeNode leftNode);

	public BTreeInteriorNode getParent() {
		return (BTreeInteriorNode) parent;
	}

	/**
	 * Shifts the values array from the specified position the number of places
	 * specified in the 'places' argument
	 *
	 * @param index
	 *            index to start the shifting
	 * @param places
	 *            number of places to shift
	 */
	protected void shiftValuesFromIndexToRight(int index, int places) {
		for (int i = valueCount - 1; i >= index; i--) {
			values[i + places] = values[i];
		}
	}

}
