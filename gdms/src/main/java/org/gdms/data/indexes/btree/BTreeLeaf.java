package org.gdms.data.indexes.btree;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;

public class BTreeLeaf extends AbstractBTreeNode implements BTreeNode {

	private int[] rows;
	private BTreeLeaf rightNeighbour;

	public BTreeLeaf(BTreeInteriorNode parent, int n) {
		super(parent, n);
		rows = new int[n + 1];
	}

	public BTreeLeaf getChildNodeFor(Value v) {
		return this;
	}

	public BTreeNode insert(Value v, int rowIndex) {
		if (valueCount == n) {
			// insert the value, split the node and reorganize the tree
			valueCount = insertValue(v, rowIndex, values, valueCount, rows);
			BTreeLeaf right = new BTreeLeaf(null, n);
			for (int i = (n + 1) / 2; i <= n; i++) {
				right.insert(values[i], rows[i]);
				values[i] = null;
				rows[i] = 0;
			}
			valueCount = (n + 1) / 2;
			right.rightNeighbour = this.rightNeighbour;
			this.rightNeighbour = right;

			if (getParent() == null) {
				// It's the root
				BTreeInteriorNode newRoot = new BTreeInteriorNode(null, n,
						this, right);

				return newRoot;
			} else {
				return getParent().reorganize(
						right.getSmallestValueNotIn(this), right);
			}
		} else {
			valueCount = insertValue(v, rowIndex, values, valueCount, rows);
			return null;
		}
	}

	private int insertValue(Value v, int rowIndex, Value[] values,
			int valueCount, int[] rows) {
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
		shiftValuesFromIndexToRight(index, 1);
		for (int j = valueCount; j >= index + 1; j--) {
			rows[j] = rows[j - 1];
		}
		values[index] = v;
		rows[index] = rowIndex;
		valueCount++;

		return valueCount;
	}

	public boolean isLeaf() {
		return true;
	}

	public int[] getIndex(Value value) {
		int[] thisRows = new int[n];
		int index = 0;
		for (int i = 0; i < valueCount; i++) {
			if (values[i].equals(value).getAsBoolean()) {
				thisRows[index] = rows[i];
				index++;
			}
		}

		if (index < valueCount) {
			int[] ret = new int[index];
			System.arraycopy(thisRows, 0, ret, 0, index);
			return ret;
		} else {
			int[] moreRows = null;
			if (rightNeighbour != null) {
				moreRows = rightNeighbour.getIndex(value);
			} else {
				moreRows = new int[0];
			}

			int[] ret = new int[index + moreRows.length];
			System.arraycopy(thisRows, 0, ret, 0, index);
			System.arraycopy(moreRows, 0, ret, index, moreRows.length);
			return ret;
		}
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

		return name + " (" + strValues.toString() + ") ";
	}

	public Value getSmallestValueNotIn(BTreeNode treeNode) {
		for (int i = 0; i < valueCount; i++) {
			if (!treeNode.contains(values[i])) {
				return values[i];
			}
		}

		return ValueFactory.createNullValue();

	}

	public boolean contains(Value v) {
		for (int i = 0; i < valueCount; i++) {
			if (values[i].equals(v).getAsBoolean()) {
				return true;
			}
		}
		return false;
	}

	public Value[] getAllValues() {
		Value[] thisRows = values;

		Value[] moreRows = null;
		if (rightNeighbour != null) {
			moreRows = rightNeighbour.getAllValues();
		} else {
			moreRows = new Value[0];
		}

		Value[] ret = new Value[valueCount + moreRows.length];
		System.arraycopy(thisRows, 0, ret, 0, valueCount);
		System.arraycopy(moreRows, 0, ret, valueCount, moreRows.length);
		return ret;

	}

	public BTreeLeaf getFirstLeaf() {
		return this;
	}

	//
	// public BTreeNode delete(Value v) {
	//
	// if (isValid(valueCount)) {
	// // no reorganization
	// return null;
	// } else {
	// // Ask neighbour
	// if ((rightNeighbour != null)
	// && rightNeighbour.moveSmallestElementTo(this)) {
	// parent.smallestNotInLeftElementChanged(rightNeighbour);
	// return null;
	// } else if ((leftNeighbour != null)
	// && leftNeighbour.moveGreatestElementTo(this)) {
	// parent.smallestNotInLeftElementChanged(this);
	// return null;
	// } else {
	// // No element in neighbour, merge the smaller one
	// BTreeLeaf smallest = rightNeighbour;
	// if (rightNeighbour == null) {
	// smallest = leftNeighbour;
	// } else if ((leftNeighbour != null)
	// && (leftNeighbour.valueCount < rightNeighbour.valueCount)) {
	// smallest = leftNeighbour;
	// } else {
	// // No merge: this is the root
	// return null;
	// }
	//
	// if (smallest == leftNeighbour) {
	// Value[] newValues = new Value[n + 1];
	// int[] newRows = new int[n + 1];
	// System.arraycopy(smallest.values, 0, newValues, 0,
	// smallest.valueCount);
	// System.arraycopy(values, 0, newValues, smallest.valueCount,
	// valueCount);
	// System.arraycopy(smallest.rows, 0, newRows, 0,
	// smallest.valueCount);
	// System.arraycopy(rows, 0, newRows, smallest.valueCount,
	// valueCount);
	//
	// // link the neighbours
	// leftNeighbour = smallest.leftNeighbour;
	// if (smallest.leftNeighbour != null) {
	// smallest.leftNeighbour.rightNeighbour = this;
	// }
	//
	// // notify parent
	// parent.smallestNotInLeftElementChanged(this);
	// return smallest.parent.deletedNode(smallest);
	// } else {
	// Value[] newValues = new Value[n + 1];
	// int[] newRows = new int[n + 1];
	// System.arraycopy(values, 0, newValues, 0, valueCount);
	// System.arraycopy(smallest.values, 0, newValues, valueCount,
	// smallest.valueCount);
	// System.arraycopy(rows, 0, newRows, 0, valueCount);
	// System.arraycopy(smallest.rows, 0, newRows, valueCount,
	// smallest.valueCount);
	//
	// // link the neighbours
	// rightNeighbour = smallest.rightNeighbour;
	// if (smallest.rightNeighbour != null) {
	// smallest.rightNeighbour.leftNeighbour = this;
	// }
	//
	// // notify parent
	// parent.smallestNotInLeftElementChanged(this);
	// return smallest.parent.deletedNode(smallest);
	// }
	// }
	// }
	// }

	// private boolean moveGreatestElementTo(BTreeLeaf treeLeaf) {
	// if (isValid(valueCount - 1)) {
	// // It can delete
	// leftNeighbour.insert(values[valueCount - 1], rows[valueCount - 1]);
	// delete(values[0]);
	// return true;
	// } else {
	// return false;
	// }
	// }

	// private boolean moveSmallestElementTo(BTreeLeaf treeLeaf) {
	// if (isValid(valueCount - 1)) {
	// // It can delete
	// leftNeighbour.insert(values[0], rows[0]);
	// delete(values[0]);
	// return true;
	// } else {
	// return false;
	// }
	// }

	@Override
	protected void mergeWithLeft(AbstractBTreeNode leftNode) {
		BTreeLeaf node = (BTreeLeaf) leftNode;
		Value[] newValues = new Value[n + 1];
		int[] newRows = new int[n + 1];
		System.arraycopy(node.values, 0, newValues, 0, node.valueCount);
		System.arraycopy(values, 0, newValues, node.valueCount, valueCount);
		System.arraycopy(node.rows, 0, newRows, 0, node.valueCount);
		System.arraycopy(rows, 0, newRows, node.valueCount, valueCount);

		this.values = newValues;
		this.rows = newRows;
		valueCount = node.valueCount + valueCount;
	}

	@Override
	protected BTreeNode getChildForNewRoot() {
		return this;
	}

	@Override
	protected void mergeWithRight(AbstractBTreeNode rightNode) {
		BTreeLeaf node = (BTreeLeaf) rightNode;
		Value[] newValues = new Value[n + 1];
		int[] newRows = new int[n + 1];
		System.arraycopy(values, 0, newValues, 0, valueCount);
		System
				.arraycopy(node.values, 0, newValues, valueCount,
						node.valueCount);
		System.arraycopy(rows, 0, newRows, 0, valueCount);
		System.arraycopy(node.rows, 0, newRows, valueCount, node.valueCount);

		this.values = newValues;
		this.rows = newRows;
		valueCount = node.valueCount + valueCount;
		this.rightNeighbour = rightNeighbour.rightNeighbour;
	}

	@Override
	protected void moveFirstTo(AbstractBTreeNode node) {
		BTreeLeaf leaf = (BTreeLeaf) node;
		leaf.values[leaf.valueCount] = values[0];
		leaf.rows[leaf.valueCount] = rows[0];
		leaf.valueCount++;
		shiftToLeft();
		valueCount--;
	}

	private void shiftToLeft() {
		for (int i = 1; i < valueCount; i++) {
			values[i - 1] = values[i];
			rows[i - 1] = rows[i];
		}
	}

	private void shiftToRight() {
		for (int i = valueCount; i > 0; i--) {
			values[i] = values[i - 1];
			rows[i] = rows[i - 1];
		}
	}

	@Override
	protected void moveLastTo(AbstractBTreeNode node) {
		BTreeLeaf leaf = (BTreeLeaf) node;
		leaf.shiftToRight();
		leaf.values[0] = values[valueCount - 1];
		leaf.rows[0] = rows[valueCount - 1];
		valueCount--;
		leaf.valueCount++;
	}

	public BTreeNode delete(Value v) {
		return adjustAfterDeletion(simpleDeletion(v));
	}

	/**
	 * Removes the element from the leaf. If the leaf is still valid and the
	 * deleted value is the smallest then it notifies its parent
	 *
	 * @param v
	 * @return If the smallest value have been modified
	 */
	public boolean simpleDeletion(Value v) {
		int index = getIndexOf(v);

		// delete the index
		for (int j = index; j < valueCount; j++) {
			values[j] = values[j + 1];
			rows[j] = rows[j + 1];
		}
		valueCount--;

		if (isValid(valueCount) && index == 0 && getParent() != null) {
			getParent().smallestNotInLeftElementChanged(this);
		}

		return index == 0;
	}

	@Override
	protected boolean isValid(int valueCount) {
		if (getParent() == null) {
			return valueCount >= 0;
		} else {
			return valueCount >= ((n + 1) / 2);
		}
	}

	public void checkTree() {
		if (!isValid(valueCount)) {
			throw new RuntimeException(this + " is not valid");
		}
	}

	public AbstractBTreeNode getRightNeighbour() {
		return rightNeighbour;
	}

	public void setRightNeighbour(BTreeLeaf rightNeighbour) {
		this.rightNeighbour = rightNeighbour;
	}

}