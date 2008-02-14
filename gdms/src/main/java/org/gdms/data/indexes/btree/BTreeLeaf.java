package org.gdms.data.indexes.btree;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.data.values.ValueFactory;

public class BTreeLeaf extends AbstractBTreeNode implements BTreeNode {

	private int[] rows;
	private BTreeLeaf rightNeighbourObject;
	private int rightNeighbourDir = -1;
	private BTreeLeaf leftNeighbourObject;
	private int leftNeighbourDir = -1;

	public BTreeLeaf(DiskBTree tree, int dir, int parentDir) {
		super(tree, dir, parentDir);
		rows = new int[tree.getN() + 1];
	}

	public BTreeNode insert(Value v, int rowIndex) throws IOException {
		if (valueCount == tree.getN()) {
			// insert the value, split the node and reorganize the tree
			valueCount = insertValue(v, rowIndex);
			BTreeLeaf right = tree.createLeaf(tree, dir, getParentDir());
			for (int i = (tree.getN() + 1) / 2; i <= tree.getN(); i++) {
				right.insert(values[i], rows[i]);
				values[i] = null;
				rows[i] = 0;
			}
			valueCount = (tree.getN() + 1) / 2;

			// Link the leaves
			if (getRightNeighbourDir() != -1) {
				this.getRightNeighbour().setLeftNeighbourDir(right.dir);
			}
			right.setRightNeighbourDir(this.getRightNeighbourDir());
			right.setLeftNeighbourDir(this.dir);
			this.setRightNeighbourDir(right.dir);

			if (getParentDir() == -1) {
				// It's the root
				BTreeInteriorNode newRoot = tree.createInteriorNode(dir, -1,
						this, right);

				return newRoot;
			} else {
				return getParent().newNodeAppeared(
						right.getSmallestValueNotIn(this), this, right);
			}
		} else {
			valueCount = insertValue(v, rowIndex);
			return null;
		}
	}

	private int insertValue(Value v, int rowIndex) throws IOException {
		// Look the place to insert the new value
		int index = getIndexOf(v);

		// insert in index
		shiftValuesFromIndexToRight(index);
		shiftRowsFromIndexToRight(index);
		values[index] = v;
		rows[index] = rowIndex;
		valueCount++;

		if ((valueCount > index + 1)
				&& v.less(values[index + 1]).getAsBoolean()) {
			if (getParentDir() != -1) {
				getParent().smallestNotInLeftElementChanged(this);
			}
		}

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
	 * @throws IOException
	 */
	public int[] getIndex(Value value) throws IOException {
		int[] thisRows = new int[tree.getN()];
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
			if (getRightNeighbourDir() != -1) {
				moreRows = getRightNeighbour().getIndex(value);
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

	public Value getSmallestValueNotIn(BTreeNode treeNode) throws IOException {
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

	public Value[] getAllValues() throws IOException {
		Value[] thisRows = values;

		Value[] moreRows = null;
		if (getRightNeighbourDir() != -1) {
			moreRows = getRightNeighbour().getAllValues();
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
	protected void mergeWithLeft(AbstractBTreeNode leftNode) throws IOException {
		BTreeLeaf node = (BTreeLeaf) leftNode;
		Value[] newValues = new Value[tree.getN() + 1];
		int[] newRows = new int[tree.getN() + 1];
		System.arraycopy(node.values, 0, newValues, 0, node.valueCount);
		System.arraycopy(values, 0, newValues, node.valueCount, valueCount);
		System.arraycopy(node.rows, 0, newRows, 0, node.valueCount);
		System.arraycopy(rows, 0, newRows, node.valueCount, valueCount);

		this.values = newValues;
		this.rows = newRows;
		valueCount = node.valueCount + valueCount;
		if (node.getLeftNeighbourDir() != -1) {
			node.getLeftNeighbour().setRightNeighbourDir(dir);
		}
		this.setLeftNeighbourDir(node.getLeftNeighbourDir());

		tree.removeNode(leftNode.dir);
	}

	@Override
	protected BTreeNode getChildForNewRoot() {
		return this;
	}

	@Override
	protected void mergeWithRight(AbstractBTreeNode rightNode)
			throws IOException {
		BTreeLeaf node = (BTreeLeaf) rightNode;
		Value[] newValues = new Value[tree.getN() + 1];
		int[] newRows = new int[tree.getN() + 1];
		System.arraycopy(values, 0, newValues, 0, valueCount);
		System
				.arraycopy(node.values, 0, newValues, valueCount,
						node.valueCount);
		System.arraycopy(rows, 0, newRows, 0, valueCount);
		System.arraycopy(node.rows, 0, newRows, valueCount, node.valueCount);

		this.values = newValues;
		this.rows = newRows;
		valueCount = node.valueCount + valueCount;
		if (node.getRightNeighbourDir() != -1) {
			node.getRightNeighbour().setLeftNeighbourDir(dir);
		}
		this.setRightNeighbourDir(node.getRightNeighbourDir());

		tree.removeNode(rightNode.dir);
	}

	public void setLeftNeighbourDir(int dir) {
		leftNeighbourDir = dir;
		leftNeighbourObject = null;
	}

	public void setRightNeighbourDir(int dir) {
		rightNeighbourDir = dir;
		rightNeighbourObject = null;
	}

	private BTreeLeaf getLeftNeighbour() throws IOException {
		if (leftNeighbourDir == -1) {
			return null;
		} else if (leftNeighbourObject == null) {
			leftNeighbourObject = (BTreeLeaf) tree.readNodeAt(leftNeighbourDir);
		}
		return leftNeighbourObject;
	}

	public BTreeLeaf getRightNeighbour() throws IOException {
		if (rightNeighbourDir == -1) {
			return null;
		} else if (rightNeighbourObject == null) {
			rightNeighbourObject = (BTreeLeaf) tree
					.readNodeAt(rightNeighbourDir);
		}
		return rightNeighbourObject;
	}

	private int getRightNeighbourDir() {
		return rightNeighbourDir;
	}

	private int getLeftNeighbourDir() {
		return leftNeighbourDir;
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

	public BTreeNode delete(Value v, int row) throws IOException {
		int index = getIndexOf(v, row);
		if (index != -1) {
			simpleDeletion(index);
			return adjustAfterDeletion();
		} else if (getRightNeighbourDir() == -1) {
			return null;
		} else if (values[valueCount - 1].notEquals(v).getAsBoolean()) {
			return null;
		} else {
			return getRightNeighbour().delete(v, row);
		}
	}

	/**
	 * Removes the element from the leaf. If the leaf is still valid and the
	 * deleted value is the smallest then it notifies its parent
	 *
	 * @param v
	 * @param row
	 * @return If the smallest value have been modified
	 * @throws IOException
	 */
	public void simpleDeletion(int index) throws IOException {
		// delete the index
		shiftValuesFromIndexToLeft(index + 1);
		shiftRowsFromIndexToLeft(index + 1);
		valueCount--;

		if (isValid(valueCount) && index == 0 && getParentDir() != -1) {
			getParent().smallestNotInLeftElementChanged(this);
		}
	}

	private int getIndexOf(Value v, int row) throws IOException {
		int index = getIndexOf(v);
		// If we don't find the value return -1
		if ((index == -1) || (index >= valueCount)) {
			return -1;
		} else {
			// Look for the pair value-row
			while (values[index].equals(v).getAsBoolean()) {
				if (rows[index] == row) {
					return index;
				}
				index++;
				// If we have searched all the node...
				if (index >= valueCount) {
					// ... and there is no right neighbour then return -1
					if (rightNeighbourDir == -1) {
						return -1;
					} else {
						// ... and there is right neighbour then delegate
						return getRightNeighbour().getIndexOf(v, row);
					}
				}

			}
			return -1;
		}
	}

	@Override
	protected boolean isValid(int valueCount) throws IOException {
		if (getParentDir() == -1) {
			return valueCount >= 0;
		} else {
			return valueCount >= ((tree.getN() + 1) / 2);
		}
	}

	public void checkTree() throws IOException {
		if (!isValid(valueCount)) {
			throw new RuntimeException(this + " is not valid");
		}
	}

	public byte[] getBytes() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);

		// Write the direction of the neighbours
		dos.writeInt(rightNeighbourDir);
		dos.writeInt(leftNeighbourDir);

		// Write the number of values
		dos.writeInt(valueCount);

		// Write a ValueCollection with the used values
		Value[] used = new Value[valueCount];
		System.arraycopy(values, 0, used, 0, valueCount);
		ValueCollection vc = ValueFactory.createValue(used);
		byte[] valuesBytes = vc.getBytes();
		dos.writeInt(valuesBytes.length);
		dos.write(valuesBytes);

		// Write the row indexes
		for (int i = 0; i < valueCount; i++) {
			dos.writeInt(rows[i]);
		}

		dos.close();

		return bos.toByteArray();
	}

	public static BTreeLeaf createLeafFromBytes(DiskBTree tree, int dir,
			int parentDir, int n, byte[] bytes) throws IOException {
		BTreeLeaf ret = new BTreeLeaf(tree, dir, parentDir);
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		DataInputStream dis = new DataInputStream(bis);

		// Read the direction of the parent and neighbours
		ret.rightNeighbourDir = dis.readInt();
		ret.leftNeighbourDir = dis.readInt();

		// Read the number of values
		ret.valueCount = dis.readInt();

		// Read the values
		int valuesBytesLength = dis.readInt();
		byte[] valuesBytes = new byte[valuesBytesLength];
		dis.read(valuesBytes);
		ValueCollection vc = (ValueCollection) ValueFactory.createValue(
				Type.COLLECTION, valuesBytes);
		Value[] readvalues = vc.getValues();
		System.arraycopy(readvalues, 0, ret.values, 0, readvalues.length);

		// Read the rowIndexes
		for (int i = 0; i < ret.valueCount; i++) {
			ret.rows[i] = dis.readInt();
		}

		dis.close();

		return ret;
	}

	public void save() throws IOException {
		tree.writeNodeAt(dir, this);
	}

	public BTreeLeaf getChildNodeFor(Value v) {
		return this;
	}

	public BTreeLeaf getLeafToInsert(Value v) throws IOException {
		if (v.lessEqual(values[valueCount - 1]).getAsBoolean()) {
			return this;
		} else if (getRightNeighbourDir() == -1) {
			return this;
		} else if (getRightNeighbour().values[0].greater(v).getAsBoolean()) {
			return this;
		} else {
			return getRightNeighbour().getLeafToInsert(v);
		}
	}

	public int[] getIndex(RangeComparator minComparator,
			RangeComparator maxComparator) throws IOException {
		int[] thisNode = new int[tree.getN()];
		int index = 0;
		Value lastValueInNode = values[valueCount - 1];
		if (minComparator.isInRange(lastValueInNode)) {
			boolean inRange = false;
			for (int i = 0; i < valueCount; i++) {
				if (minComparator.isInRange(values[i])
						&& maxComparator.isInRange(values[i])) {
					inRange = true;
					thisNode[index] = rows[i];
					index++;
				} else {
					if (inRange) {
						// We have finished our range
						break;
					}
				}
			}
		}

		if ((index < valueCount) && !maxComparator.isInRange(lastValueInNode)) {
			int[] ret = new int[index];
			System.arraycopy(thisNode, 0, ret, 0, index);
			return ret;
		} else {
			int[] moreRows = null;
			if (getRightNeighbourDir() != -1
					&& maxComparator.isInRange(lastValueInNode)) {
				moreRows = getRightNeighbour().getIndex(minComparator,
						maxComparator);
			} else {
				moreRows = new int[0];
			}

			int[] ret = new int[index + moreRows.length];
			System.arraycopy(thisNode, 0, ret, 0, index);
			System.arraycopy(moreRows, 0, ret, index, moreRows.length);
			return ret;
		}
	}
}