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

	protected abstract boolean isValid(int valueCount);

	protected BTreeNode adjustAfterDeletion() {
		if (isValid(valueCount)) {
			return null;
		} else {
			if (parent == null) {
				// If it's the root just change the root
				return getChildForNewRoot();
			} else {
				if (getParent().moveFromNeighbour(this)) {
					return adjustAfterDeletion();
				} else {
					getParent().mergeWithNeighbour(this);
					return ((BTreeInteriorNode) parent).adjustAfterDeletion();
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
	 * Shifts one place to the right the values array from the specified
	 * position
	 *
	 * @param index
	 *            index to start the shifting
	 */
	protected void shiftValuesFromIndexToRight(int index) {
		for (int i = valueCount - 1; i >= index; i--) {
			values[i + 1] = values[i];
		}
	}

	/**
	 * Shifts to the left the values array from the specified position the
	 * number of places specified in the 'places' argument
	 *
	 * @param index
	 *            index to start the shifting
	 */
	protected void shiftValuesFromIndexToLeft(int index) {
		for (int j = index - 1; j + 1 < valueCount; j++) {
			values[j] = values[j + 1];
		}
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
		int index = valueCount;
		for (int i = 0; i < valueCount; i++) {
			if (values[i].isNull() || v.lessEqual(values[i]).getAsBoolean()) {
				index = i;
				break;
			}
		}
		return index;

		// TODO improve the search with a binary search
		// private int binarySearch(Value v) {
		// int low = 0;
		// int high = valueCount;
		// while (low <= high) {
		// int mid = (low + high) / 2;
		// if (values[mid].greater(v).getAsBoolean()) {
		// high = mid - 1;
		// } else if (values[mid].less(v).getAsBoolean()) {
		// low = mid + 1;
		// } else {
		// return mid; // found
		// }
		// }
		// return -1; // not found
		// }

	}

}
