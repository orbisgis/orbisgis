package org.gdms.data.indexes.rtree;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

/**
 * @author Fernando Gonzalez Cortes
 *
 */
public class RTreeInteriorNode extends AbstractRTreeNode implements RTreeNode {

	private ChildReference[] children;
	protected Envelope[] envelopes;

	public RTreeInteriorNode(DiskRTree tree, int dir, int parentDir) {
		super(tree, dir, parentDir);
		// one place if for overload management
		children = new ChildReference[tree.getN() + 1];
		envelopes = new Envelope[tree.getN() + 1];
	}

	public RTreeInteriorNode(DiskRTree tree, int dir, int parentDir,
			RTreeNode left, RTreeNode right) throws IOException {
		this(tree, dir, parentDir);
		envelopes[0] = left.getEnvelope();
		envelopes[1] = right.getEnvelope();
		valueCount++;
		setChild(0, left);
		setChild(1, right);

	}

	private void setChild(int i, RTreeNode node) {
		children[i] = new ChildReference(tree, node);
		if (node != null) {
			node.setParentDir(dir);
		}
	}

	public boolean isLeaf() {
		return false;
	}

	/**
	 * Reorganizes the tree with the new leaf that have appeared. It must set
	 * the parent of the leave (still null)
	 *
	 * @param right
	 *
	 * @param v
	 * @return
	 * @throws IOException
	 */
	public RTreeNode newNodeAppeared(RTreeNode originalNode, RTreeNode newNode)
			throws IOException {
		if (valueCount == tree.getN()) {
			// split the node, insert and reorganize the tree
			RTreeInteriorNode m = tree.createInteriorNode(dir, getParentDir());
			newNode.setParentDir(m.dir);

			// Create the value array with the new index
			insertValueAndReferenceAfter(originalNode, newNode);

			// Move half the values to the new node
			int mIndex = 0;
			envelopes[(tree.getN() + 1) / 2] = null;
			for (int i = (tree.getN() + 1) / 2 + 1; i < envelopes.length; i++) {
				m.envelopes[mIndex] = envelopes[i];
				m.setChild(mIndex, getChild(i));
				mIndex++;
				envelopes[i] = null;
				setChild(i, null);
			}
			m.setChild(mIndex, getChild(tree.getN() + 1));
			setChild(tree.getN() + 1, null);
			m.valueCount = mIndex;
			valueCount = (tree.getN() + 1) / 2;
			if (getParentDir() == -1) {
				// We need a new root
				RTreeInteriorNode newRoot = tree.createInteriorNode(dir, -1,
						this, m);

				return newRoot;
			} else {
				return getParent().newNodeAppeared(this, m);
			}
		} else {
			insertValueAndReferenceAfter(originalNode, newNode);
			return null;
		}
	}

	private RTreeNode getChild(int i) throws IOException {
		if (!children[i].isLoaded()) {
			children[i].resolve();
		}
		return children[i].getReference();
	}

	private void insertValueAndReferenceAfter(RTreeNode refNode, RTreeNode node)
			throws IOException {
		// Look the place to insert the new value
		int index = getIndexOf(refNode);

		// insert at index
		shiftEnvelopesFromIndexToRight(index + 1);
		shiftChildrenFromIndexToRight(index + 1);
		envelopes[index + 1] = node.getEnvelope();
		setChild(index + 1, node);
		node.setParentDir(dir);
		valueCount++;
	}

	/**
	 * Shifts one place to the right the values array from the specified
	 * position
	 *
	 * @param index
	 *            index to start the shifting
	 */
	protected void shiftEnvelopesFromIndexToRight(int index) {
		for (int i = valueCount - 1; i >= index; i--) {
			envelopes[i + 1] = envelopes[i];
		}
	}

	/**
	 * Shifts to the left the values array from the specified position the
	 * number of places specified in the 'places' argument
	 *
	 * @param index
	 *            index to start the shifting
	 */
	protected void shiftEnvelopesFromIndexToLeft(int index) {
		for (int j = index - 1; j + 1 < valueCount; j++) {
			envelopes[j] = envelopes[j + 1];
		}
	}

	/**
	 * Shifts to the right the values array from the specified position the
	 * number of places specified in the 'places' argument
	 *
	 * @param index
	 *            index to start the shifting
	 * @param places
	 *            number of places to shift
	 * @throws IOException
	 */
	private void shiftChildrenFromIndexToRight(int index) throws IOException {
		for (int i = valueCount; i >= index; i--) {
			setChild(i + 1, getChild(i));
		}
	}

	/**
	 * Shifts to the left the values array from the specified position the
	 * number of places specified in the 'places' argument
	 *
	 * @param index
	 *            index to start the shifting
	 * @param places
	 *            number of places to shift
	 * @throws IOException
	 */
	private void shiftChildrenFromIndexToLeft(int index) throws IOException {
		for (int i = index - 1; i < valueCount; i++) {
			setChild(i, getChild(i + 1));
		}
	}

	public RTreeNode insert(Geometry v, int rowIndex) {
		throw new UnsupportedOperationException("Cannot insert "
				+ "value in an interior node");
	}

	public int getRow(Geometry value) {
		throw new UnsupportedOperationException("Cannot get the row "
				+ "in an interior node");
	}

	@Override
	public String toString() {
		try {

			StringBuilder strValues = new StringBuilder("");
			String separator = "";
			for (int i = 0; i < valueCount; i++) {
				Envelope v = envelopes[i];
				strValues.append(separator).append(v);
				separator = ", ";
			}
			StringBuilder strChilds = new StringBuilder("");
			separator = "";
			for (int i = 0; i < valueCount + 1; i++) {
				RTreeNode v = getChild(i);
				if (v != null) {
					strChilds.append(separator).append(
							((AbstractRTreeNode) v).name);
				} else {
					strChilds.append(separator).append("null");
				}
				separator = ", ";
			}

			String ret = name + "\n (" + strValues.toString() + ") \n("
					+ strChilds + ")\n";
			for (int i = 0; i < valueCount + 1; i++) {
				RTreeNode node = getChild(i);
				if (node != null) {
					ret += node.toString();
				}
			}

			return ret;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public RTreeLeaf getFirstLeaf() throws IOException {
		return getChild(0).getFirstLeaf();
	}

	/**
	 * Gets the index of the leaf in the children array
	 *
	 * @param node
	 * @return -1 if the node is not present
	 * @throws IOException
	 */
	private int getIndexOf(RTreeNode node) throws IOException {
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
	 * @throws IOException
	 */
	public void mergeWithNeighbour(AbstractRTreeNode node) throws IOException {
		int index = getIndexOf(node);
		AbstractRTreeNode smallest = null;
		AbstractRTreeNode rightNeighbour = getRightNeighbour(index);
		AbstractRTreeNode leftNeighbour = getLeftNeighbour(index);

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
			shiftChildrenFromIndexToLeft(index);
			// remove the values of the left neighbour
			shiftEnvelopesFromIndexToLeft(index);

			valueCount--;
		} else {
			node.mergeWithRight(rightNeighbour);
			// Remove the pointer to the right neighbour
			shiftChildrenFromIndexToLeft(index + 2);
			// Remove the values to the right neighbour
			shiftEnvelopesFromIndexToLeft(index + 1);

			valueCount--;
		}

		// TODO if the envelope is different notify the parent

	}

	protected void mergeWithRight(AbstractRTreeNode rightNode)
			throws IOException {
		RTreeInteriorNode node = (RTreeInteriorNode) rightNode;
		Envelope[] newValues = new Envelope[tree.getN() + 1];
		ChildReference[] newChildren = new ChildReference[tree.getN() + 1];
		System.arraycopy(envelopes, 0, newValues, 0, valueCount);
		System.arraycopy(node.envelopes, 0, newValues, valueCount + 1,
				node.valueCount);
		System.arraycopy(children, 0, newChildren, 0, valueCount + 1);
		// Change the parent in the moving children
		for (int i = 0; i < node.valueCount + 1; i++) {
			node.getChild(i).setParentDir(dir);
		}
		System.arraycopy(node.children, 0, newChildren, valueCount + 1,
				node.valueCount);
		envelopes = newValues;
		children = newChildren;
		valueCount = valueCount + node.valueCount + 1;

		tree.removeNode(rightNode.dir);
	}

	protected void mergeWithLeft(AbstractRTreeNode leftNode) throws IOException {
		RTreeInteriorNode node = (RTreeInteriorNode) leftNode;
		Envelope[] newValues = new Envelope[tree.getN() + 1];
		ChildReference[] newChildren = new ChildReference[tree.getN() + 1];
		System.arraycopy(node.envelopes, 0, newValues, 0, node.valueCount);
		System.arraycopy(envelopes, 0, newValues, node.valueCount + 1,
				valueCount);
		// Change the parent in the moving children
		for (int i = 0; i < node.valueCount + 1; i++) {
			node.getChild(i).setParentDir(dir);
		}
		System.arraycopy(node.children, 0, newChildren, 0, node.valueCount + 1);
		System.arraycopy(children, 0, newChildren, node.valueCount + 1,
				valueCount);
		envelopes = newValues;
		children = newChildren;
		valueCount = valueCount + node.valueCount + 1;

		tree.removeNode(leftNode.dir);
	}

	/**
	 * Selects the neighbour and moves the nearest element into this node if it
	 * is possible
	 *
	 * @param node
	 * @return true if it is possible to move from a neighbour and false
	 *         otherwise
	 * @throws IOException
	 */
	public boolean moveFromNeighbour(AbstractRTreeNode node) throws IOException {
		int index = getIndexOf(node);
		AbstractRTreeNode rightNeighbour = getRightNeighbour(index);
		AbstractRTreeNode leftNeighbour = getLeftNeighbour(index);
		if ((rightNeighbour != null)
				&& (rightNeighbour.isValid(rightNeighbour.valueCount - 1))) {
			rightNeighbour.moveFirstTo(node);
			// TODO notify parent if the envelope has changed
			return true;
		} else if ((leftNeighbour != null)
				&& (leftNeighbour.isValid(leftNeighbour.valueCount - 1))) {
			leftNeighbour.moveLastTo(node);
			// TODO notify parent if the envelope has changed
			return true;
		} else {
			return false;
		}
	}

	protected void moveFirstTo(AbstractRTreeNode node) throws IOException {
		RTreeInteriorNode n = (RTreeInteriorNode) node;
		n.envelopes[n.valueCount] = this.envelopes[0];
		n.setChild(n.valueCount, this.getChild(0));

		shiftEnvelopesFromIndexToLeft(1);
		shiftChildrenFromIndexToLeft(1);
		n.valueCount++;
		this.valueCount--;
	}

	protected void moveLastTo(AbstractRTreeNode node) throws IOException {
		RTreeInteriorNode n = (RTreeInteriorNode) node;
		n.shiftChildrenFromIndexToRight(0);
		n.shiftEnvelopesFromIndexToRight(0);
		n.envelopes[0] = this.envelopes[valueCount];
		n.setChild(0, this.getChild(valueCount));
		n.valueCount++;
		this.valueCount--;
	}

	/**
	 * Gets the neighbour on the left of the specified node
	 *
	 * @param index
	 * @return
	 * @throws IOException
	 */
	private AbstractRTreeNode getLeftNeighbour(int index) throws IOException {
		if (index > 0) {
			return (AbstractRTreeNode) getChild(index - 1);
		} else {
			return null;
		}
	}

	/**
	 * Gets the neighbour on the right of the specified node
	 *
	 * @param index
	 * @return
	 * @throws IOException
	 */
	private AbstractRTreeNode getRightNeighbour(int index) throws IOException {
		if (index < valueCount) {
			return (AbstractRTreeNode) getChild(index + 1);
		} else {
			return null;
		}
	}

	@Override
	protected RTreeNode getChildForNewRoot() throws IOException {
		RTreeNode child = getChild(0);
		child.setParentDir(-1);
		return child;
	}

	@Override
	protected boolean isValid(int valueCount) throws IOException {
		if (getParentDir() == -1) {
			return valueCount >= 1;
		} else {
			return valueCount + 1 >= ((tree.getN() + 1) / 2);
		}
	}

	public void checkTree() throws IOException {
		if (!isValid(valueCount)) {
			throw new RuntimeException(this + " Not enough childs");
		} else {
			for (int i = 0; i < valueCount + 1; i++) {
				if (i < valueCount) {
					if (getChild(i) instanceof RTreeLeaf) {
						RTreeLeaf leaf = (RTreeLeaf) getChild(i);
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
					// if (getChild(i).getSmallestValueNotIn(getChild(i - 1))
					// .notEquals(values[i - 1]).getAsBoolean()) {
					// throw new RuntimeException("The " + i
					// + "th value is not right");
					// }
				}
			}
		}
	}

	public RTreeNode delete(Geometry v, int row) {
		throw new RuntimeException("Cannot delete in an interior node");
	}

	public byte[] getBytes() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);

		// Write the number of values
		dos.writeInt(valueCount);

		// Write a ValueCollection with the used values
		Envelope[] used = new Envelope[valueCount];
		System.arraycopy(envelopes, 0, used, 0, valueCount);
		byte[] valuesBytes = getEnvelopeBytes();
		dos.writeInt(valuesBytes.length);
		dos.write(valuesBytes);

		// Write the children direction
		if (valueCount > 0) {
			for (int i = 0; i < valueCount + 1; i++) {
				dos.writeInt(children[i].getDir());
			}
		}

		dos.close();

		return bos.toByteArray();
	}

	private byte[] getEnvelopeBytes() {
		ByteBuffer bb = ByteBuffer.allocate(envelopes.length * 4 * 32);
		for (Envelope envelope : envelopes) {
			bb.putDouble(envelope.getMinX());
			bb.putDouble(envelope.getMinY());
			bb.putDouble(envelope.getMaxX());
			bb.putDouble(envelope.getMaxY());
		}
		bb.flip();
		byte[] ret = new byte[envelopes.length * 8];
		bb.get(ret);
		return ret;
	}

	private static Envelope[] getEnvelopes(byte[] valuesBytes) {
		ArrayList<Envelope> ret = new ArrayList<Envelope>();
		ByteBuffer bb = ByteBuffer.wrap(valuesBytes);
		while (bb.remaining() > 0) {
			ret.add(new Envelope(
					new Coordinate(bb.getDouble(), bb.getDouble()),
					new Coordinate(bb.getDouble(), bb.getDouble())));
		}

		return ret.toArray(new Envelope[0]);
	}

	public static RTreeInteriorNode createInteriorNodeFromBytes(DiskRTree tree,
			int dir, int parentDir, int n, byte[] bytes) throws IOException {
		RTreeInteriorNode ret = new RTreeInteriorNode(tree, dir, parentDir);
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		DataInputStream dis = new DataInputStream(bis);

		// Read the number of values
		ret.valueCount = dis.readInt();

		// Read the values
		int valuesBytesLength = dis.readInt();
		byte[] valuesBytes = new byte[valuesBytesLength];
		dis.read(valuesBytes);
		Envelope[] readEnvelopes = getEnvelopes(valuesBytes);
		System.arraycopy(readEnvelopes, 0, ret.envelopes, 0,
				readEnvelopes.length);

		// Read the children directions
		for (int i = 0; i < ret.valueCount + 1; i++) {
			ret.children[i] = new ChildReference(tree, dis.readInt());
		}

		dis.close();

		return ret;
	}

	private static class ChildReference {
		private RTreeNode object;
		private int dir;
		private DiskRTree tree;

		public ChildReference(DiskRTree tree, RTreeNode object) {
			this.tree = tree;
			this.object = object;
			if (object == null) {
				this.dir = -1;
			} else {
				this.dir = ((AbstractRTreeNode) object).dir;
			}
		}

		public ChildReference(DiskRTree tree, int readInt) {
			this.tree = tree;
			this.dir = readInt;
			this.object = null;
		}

		public RTreeNode getReference() {
			return object;
		}

		public boolean isLoaded() {
			return object != null;
		}

		public void resolve() throws IOException {
			if (dir == -1) {
				object = null;
			} else {
				object = tree.readNodeAt(dir);
			}
		}

		public int getDir() {
			return dir;
		}

	}

	public void save() throws IOException {
		tree.writeNodeAt(dir, this);

		for (int i = 0; i < valueCount + 1; i++) {
			ChildReference childRef = children[i];
			if (childRef.isLoaded()) {
				childRef.getReference().save();
			}
		}
	}

	public Envelope getEnvelope() {
		// TODO Auto-generated method stub
		return null;
	}

	public Geometry[] getAllValues() {
		// TODO Auto-generated method stub
		return null;
	}

	public int[] getRows(Envelope value) {
		// TODO Auto-generated method stub
		return null;
	}

}
