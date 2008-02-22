package org.gdms.data.indexes.rtree;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.data.values.ValueFactory;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public class RTreeLeaf extends AbstractRTreeNode implements RTreeNode {

	private Geometry[] values;
	private int[] rows;
	private RTreeLeaf rightNeighbourObject;
	private int rightNeighbourDir = -1;
	private RTreeLeaf leftNeighbourObject;
	private int leftNeighbourDir = -1;
	private Envelope envelope;

	public RTreeLeaf(DiskRTree tree, int dir, int parentDir) {
		super(tree, dir, parentDir);
		rows = new int[tree.getN() + 1];
		values = new Geometry[tree.getN() + 1];
	}

	public RTreeNode insert(Geometry v, int rowIndex) throws IOException {
		if (valueCount == tree.getN()) {
			// insert the value, split the node and reorganize the tree
			RTreeLeaf right = tree.createLeaf(tree, dir, getParentDir());
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
				RTreeInteriorNode newRoot = tree.createInteriorNode(dir, -1,
						this, right);

				return newRoot;
			} else {
				return getParent().newNodeAppeared(this, right);
			}
		} else {
			if (!getEnvelope().contains(v.getEnvelopeInternal())) {
				insertValue(v, rowIndex);
				envelope = null;
			} else {
				insertValue(v, rowIndex);
			}
			return null;
		}
	}

	private void insertValue(Geometry v, int rowIndex) throws IOException {
		// insert in index
		values[valueCount] = v;
		rows[valueCount] = rowIndex;
		valueCount++;
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
	public int[] getIndex(Geometry value) throws IOException {
		int[] thisRows = new int[tree.getN()];
		int index = 0;
		for (int i = 0; i < valueCount; i++) {
			if (values[i].equals(value)) {
				thisRows[index] = rows[i];
				index++;
			} else {
				break;
			}
		}

		// Look at the neighbour
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

	@Override
	public String toString() {
		StringBuilder strValues = new StringBuilder("");
		String separator = "";
		for (int i = 0; i < valueCount; i++) {
			Geometry v = values[i];
			strValues.append(separator).append(v.toText());
			separator = ", ";
		}

		return name + " (" + strValues.toString() + ") ";
	}

	public boolean contains(Geometry v) {
		for (Geometry geom : values) {
			if (geom.equals(v)) {
				return true;
			}
		}

		return false;
	}

	public Geometry[] getAllValues() throws IOException {
		Geometry[] thisRows = values;

		Geometry[] moreRows = null;
		if (getRightNeighbourDir() != -1) {
			moreRows = getRightNeighbour().getAllValues();
		} else {
			moreRows = new Geometry[0];
		}

		Geometry[] ret = new Geometry[valueCount + moreRows.length];
		System.arraycopy(thisRows, 0, ret, 0, valueCount);
		System.arraycopy(moreRows, 0, ret, valueCount, moreRows.length);
		return ret;

	}

	public RTreeLeaf getFirstLeaf() {
		return this;
	}

	@Override
	protected void mergeWithLeft(AbstractRTreeNode leftNode) throws IOException {
		RTreeLeaf node = (RTreeLeaf) leftNode;
		Geometry[] newValues = new Geometry[tree.getN() + 1];
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
	protected RTreeNode getChildForNewRoot() {
		return this;
	}

	@Override
	protected void mergeWithRight(AbstractRTreeNode rightNode)
			throws IOException {
		RTreeLeaf node = (RTreeLeaf) rightNode;
		Geometry[] newValues = new Geometry[tree.getN() + 1];
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

	private RTreeLeaf getLeftNeighbour() throws IOException {
		if (leftNeighbourDir == -1) {
			return null;
		} else if (leftNeighbourObject == null) {
			leftNeighbourObject = (RTreeLeaf) tree.readNodeAt(leftNeighbourDir);
		}
		return leftNeighbourObject;
	}

	public RTreeLeaf getRightNeighbour() throws IOException {
		if (rightNeighbourDir == -1) {
			return null;
		} else if (rightNeighbourObject == null) {
			rightNeighbourObject = (RTreeLeaf) tree
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
	protected void moveFirstTo(AbstractRTreeNode node) {
		RTreeLeaf leaf = (RTreeLeaf) node;
		leaf.values[leaf.valueCount] = values[0];
		leaf.rows[leaf.valueCount] = rows[0];
		leaf.valueCount++;
		shiftValuesFromIndexToLeft(1);
		shiftRowsFromIndexToLeft(1);
		valueCount--;
	}

	/**
	 * Shifts one place to the right the values array from the specified
	 * position
	 *
	 * @param index
	 *            index to start the shifting
	 */
	private void shiftValuesFromIndexToRight(int index) {
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
	private void shiftValuesFromIndexToLeft(int index) {
		for (int j = index - 1; j + 1 < valueCount; j++) {
			values[j] = values[j + 1];
		}
	}

	@Override
	protected void moveLastTo(AbstractRTreeNode node) {
		RTreeLeaf leaf = (RTreeLeaf) node;
		leaf.shiftRowsFromIndexToRight(0);
		leaf.shiftValuesFromIndexToRight(0);
		leaf.values[0] = values[valueCount - 1];
		leaf.rows[0] = rows[valueCount - 1];
		valueCount--;
		leaf.valueCount++;
	}

	public RTreeNode delete(Geometry v, int row) throws IOException {
		int index = getIndexOf(v, row);
		if (index != -1) {
			simpleDeletion(index);
			envelope = null;
			return adjustAfterDeletion();
		} else {
			// TODO We should tell the caller that nothing was deleted?
			return null;
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
	}

	private int getIndexOf(Geometry v, int row) throws IOException {
		for (int i = 0; i < valueCount; i++) {
			if ((rows[i] == row) && (values[i].equals(v))) {
				return i;
			}
		}
		return -1;
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

	public static RTreeLeaf createLeafFromBytes(DiskRTree tree, int dir,
			int parentDir, int n, byte[] bytes) throws IOException {
		RTreeLeaf ret = new RTreeLeaf(tree, dir, parentDir);
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

	public Envelope getEnvelope() {
		if (envelope == null) {
			envelope = new Envelope(values[0].getEnvelopeInternal());
			for (int i = 1; i < valueCount; i++) {
				envelope.expandToInclude(values[i].getEnvelopeInternal());
			}
		}
		return envelope;
	}

	public int[] getRows(Envelope value) {
		int[] intersecting = new int[valueCount];
		int index = 0;
		for (int i = 0; i < valueCount; i++) {
			if (values[i].getEnvelopeInternal().intersects(value)) {
				intersecting[index] = rows[i];
				index++;
			}
		}

		int[] ret = new int[index];
		System.arraycopy(intersecting, 0, ret, 0, index);

		return ret;
	}

}