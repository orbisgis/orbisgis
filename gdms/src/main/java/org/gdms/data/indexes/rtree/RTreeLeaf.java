package org.gdms.data.indexes.rtree;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.data.values.ValueFactory;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public class RTreeLeaf extends AbstractRTreeNode implements RTreeNode {

	private Geometry[] values;
	private int[] rows;
	private Envelope envelope;

	public RTreeLeaf(DiskRTree tree, int dir, int parentDir) {
		super(tree, dir, parentDir);
		rows = new int[tree.getN() + 1];
		values = new Geometry[tree.getN() + 1];
	}

	public RTreeNode splitNode() throws IOException {
		RTreeLeaf right = tree.createLeaf(tree, dir, getParentDir());

		// Find farthest envelopes
		Envelope ref = values[0].getEnvelopeInternal();
		int farthest1 = getFarthestGeometry(0, ref);
		int farthest2 = getFarthestGeometry(farthest1, values[farthest1]
				.getEnvelopeInternal());

		ArrayList<Integer> toRight = new ArrayList<Integer>();
		ArrayList<Integer> toLeft = new ArrayList<Integer>();
		// Move farthest2 to "right"
		toLeft.add(farthest1);
		toRight.add(farthest2);

		// Split the remaining in the two nodes
		for (int i = 0; i < valueCount; i++) {
			if ((i == farthest1) || (i == farthest2)) {
				continue;
			} else {
				double diff1 = getExpandImpact(farthest1, i);
				double diff2 = getExpandImpact(farthest2, i);
				if (diff1 > diff2) {
					toRight.add(i);
				} else {
					toLeft.add(i);
				}
			}
		}

		// configure right node
		for (int i = 0; i < toRight.size(); i++) {
			int elementIndex = toRight.get(i);
			right.insert(values[elementIndex], rows[elementIndex]);
		}

		// configure left node
		Geometry[] newValues = new Geometry[tree.getN() + 1];
		int[] newRows = new int[tree.getN() + 1];
		for (int i = 0; i < toLeft.size(); i++) {
			Integer elementIndex = toLeft.get(i);
			newValues[i] = values[elementIndex];
			newRows[i] = rows[elementIndex];
		}
		values = newValues;
		rows = newRows;
		valueCount = toLeft.size();
		envelope = null;

		return right;
	}

	private double getExpandImpact(int ref, int i) {
		Envelope op1 = new Envelope(values[ref].getEnvelopeInternal());
		double initialArea = op1.getWidth() * op1.getHeight();
		op1.expandToInclude(values[i].getEnvelopeInternal());
		double finalArea = op1.getWidth() * op1.getHeight();
		double diff = finalArea - initialArea;
		return diff;
	}

	private int getFarthestGeometry(int ref, Envelope refEnvelope) {
		double maxDistance = Double.MIN_VALUE;
		int argmax = -1;
		for (int i = 0; i < valueCount; i++) {
			if (i == ref) {
				continue;
			} else {
				double distance = values[i].getEnvelopeInternal().distance(
						refEnvelope);
				if (distance > maxDistance) {
					maxDistance = distance;
					argmax = i;
				}

			}
		}
		return argmax;
	}

	public void insert(Geometry v, int rowIndex) throws IOException {
		if ((valueCount == 0)
				|| !getEnvelope().contains(v.getEnvelopeInternal())) {
			insertValue(v, rowIndex);
			envelope = null;// TODO si isValid expandToInclude
		} else {
			insertValue(v, rowIndex);
		}
		if (!isValid() && (getParentDir() == -1)) {
			// new root
			tree.createInteriorNode(dir, -1, this,
					splitNode());
		}
	}

	private void insertValue(Geometry v, int rowIndex) throws IOException {
		// insert in index ordered by the x coordinate
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

		int[] ret = new int[index];
		System.arraycopy(thisRows, 0, ret, 0, index);
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
		Geometry[] ret = new Geometry[valueCount];
		System.arraycopy(values, 0, ret, 0, valueCount);
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

		tree.removeNode(rightNode.dir);
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
			return null;// adjustAfterDeletion();
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

	public void checkTree() throws IOException {
		if (!isValid()) {
			throw new RuntimeException(this + " is not valid");
		} else {
			Envelope testEnvelope = new Envelope(values[0]
					.getEnvelopeInternal());
			for (int i = 1; i < valueCount; i++) {
				testEnvelope.expandToInclude(values[i].getEnvelopeInternal());
			}

			if (!testEnvelope.equals(getEnvelope())) {
				throw new RuntimeException("bad envelope");
			}
		}
	}

	public byte[] getBytes() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);

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

	public boolean isValid() {
		if (getParentDir() == -1) {
			return (valueCount >= 0) && (valueCount <= tree.getN());
		} else {
			return (valueCount >= ((tree.getN() + 1) / 2))
					&& (valueCount <= tree.getN());
		}
	}

}