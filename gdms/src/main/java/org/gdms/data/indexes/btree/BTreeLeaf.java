package org.gdms.data.indexes.btree;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;

public class BTreeLeaf extends AbstractBTreeNode implements BTreeNode {

	private int[] rows;
	private BTreeLeaf rightNeighbour;
	private BTreeLeaf leftNeighbour;

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
			valueCount = insertValue(v, rowIndex);
			BTreeLeaf right = new BTreeLeaf(null, n);
			for (int i = (n + 1) / 2; i <= n; i++) {
				right.insert(values[i], rows[i]);
				values[i] = null;
				rows[i] = 0;
			}
			valueCount = (n + 1) / 2;

			// Link the leaves
			if (rightNeighbour != null) {
				this.rightNeighbour.leftNeighbour = right;
			}
			right.rightNeighbour = this.rightNeighbour;
			right.leftNeighbour = this;
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
			valueCount = insertValue(v, rowIndex);
			return null;
		}
	}

	private int insertValue(Value v, int rowIndex) {
		// Look the place to insert the new value
		int index = getIndexOf(v);

		// insert in index
		shiftValuesFromIndexToRight(index);
		shiftRowsFromIndexToRight(index);
		values[index] = v;
		rows[index] = rowIndex;
		valueCount++;

		return valueCount;
	}

	/**
	 * Shifts to right the rows array from the specified position the number of
	 * places specified in the 'places' argument
	 *
	 * @param index
	 *            index to start the shifting
	 */
	private void shiftRowsFromIndexToLeft(int index) {
		for (int j = index - 1; j + 1 < valueCount; j++) {
			rows[j] = rows[j + 1];
		}
	}

	/**
	 * Shifts the rows array from the specified position the number of places
	 * specified in the 'places' argument
	 *
	 * @param index
	 *            index to start the shifting
	 */
	private void shiftRowsFromIndexToRight(int index) {
		for (int i = valueCount - 1; i >= index; i--) {
			rows[i + 1] = rows[i];
		}
	}

	public boolean isLeaf() {
		return true;
	}

	/**
	 * Gets all the rows that contain the specified value
	 *
	 * @param value
	 * @return
	 */
	public int[] getIndex(Value value) {
		int[] thisRows = new int[n];
		int index = 0;
		for (int i = getIndexOf(value); i < valueCount; i++) {
			if (values[i].equals(value).getAsBoolean()) {
				thisRows[index] = rows[i];
				index++;
			} else {
				break;
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
		int index = getIndexOf(v);
		return (index < valueCount) && values[index].equals(v).getAsBoolean();
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
		if (node.leftNeighbour != null) {
			node.leftNeighbour.rightNeighbour = this;
		}
		this.leftNeighbour = node.leftNeighbour;
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
		if (rightNeighbour.rightNeighbour != null) {
			rightNeighbour.rightNeighbour.leftNeighbour = this;
		}
		this.rightNeighbour = rightNeighbour.rightNeighbour;
	}

	@Override
	protected void moveFirstTo(AbstractBTreeNode node) {
		BTreeLeaf leaf = (BTreeLeaf) node;
		leaf.values[leaf.valueCount] = values[0];
		leaf.rows[leaf.valueCount] = rows[0];
		leaf.valueCount++;
		shiftValuesFromIndexToLeft(1);
		shiftRowsFromIndexToLeft(1);
		valueCount--;
	}

	@Override
	protected void moveLastTo(AbstractBTreeNode node) {
		BTreeLeaf leaf = (BTreeLeaf) node;
		leaf.shiftRowsFromIndexToRight(0);
		leaf.shiftValuesFromIndexToRight(0);
		leaf.values[0] = values[valueCount - 1];
		leaf.rows[0] = rows[valueCount - 1];
		valueCount--;
		leaf.valueCount++;
	}

	public BTreeNode delete(Value v) {
		simpleDeletion(v);
		return adjustAfterDeletion();
	}

	/**
	 * Removes the element from the leaf. If the leaf is still valid and the
	 * deleted value is the smallest then it notifies its parent
	 *
	 * @param v
	 * @return If the smallest value have been modified
	 */
	public void simpleDeletion(Value v) {
		int index = getIndexOf(v);

		// delete the index
		shiftValuesFromIndexToLeft(index + 1);
		shiftRowsFromIndexToLeft(index + 1);
		valueCount--;

		if (isValid(valueCount) && index == 0 && getParent() != null) {
			getParent().smallestNotInLeftElementChanged(this);
		}
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

}