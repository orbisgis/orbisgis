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

/**
 * @author Fernando Gonzalez Cortes
 *
 */
public class BTreeInteriorNode extends AbstractBTreeNode implements BTreeNode {

	private ChildReference[] children;

	public BTreeInteriorNode(DiskBTree tree, int dir, int parentDir) {
		super(tree, dir, parentDir);
		// one place if for overload management
		children = new ChildReference[tree.getN() + 2];
	}

	public BTreeInteriorNode(DiskBTree tree, int dir, int parentDir,
			BTreeNode left, BTreeNode right) throws IOException {
		this(tree, dir, parentDir);
		values[0] = right.getSmallestValueNotIn(left);
		valueCount++;
		setChild(0, left);
		setChild(1, right);

	}

	private void setChild(int i, BTreeNode node) {
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
	 * @param v
	 * @return
	 * @throws IOException
	 */
	public BTreeNode reorganize(Value smallestNotInOldNode, BTreeNode newNode)
			throws IOException {
		if (valueCount == tree.getN()) {
			// split the node, insert and reorganize the tree
			BTreeInteriorNode m = tree.createInteriorNode(dir, getParentDir());
			newNode.setParentDir(m.dir);

			// Create the value array with the new index
			insertValueAndReference(smallestNotInOldNode, newNode);

			// Move half the values to the new node
			int mIndex = 0;
			values[(tree.getN() + 1) / 2] = null;
			for (int i = (tree.getN() + 1) / 2 + 1; i < values.length; i++) {
				m.values[mIndex] = values[i];
				m.setChild(mIndex, getChild(i));
				mIndex++;
				values[i] = null;
				setChild(i, null);
			}
			m.setChild(mIndex, getChild(tree.getN() + 1));
			setChild(tree.getN() + 1, null);
			m.valueCount = mIndex;
			valueCount = (tree.getN() + 1) / 2;
			if (getParentDir() == -1) {
				// We need a new root
				BTreeInteriorNode newRoot = tree.createInteriorNode(dir, -1,
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

	private BTreeNode getChild(int i) throws IOException {
		if (!children[i].isLoaded()) {
			children[i].resolve();
		}
		return children[i].getReference();
	}

	private void insertValueAndReference(Value v, BTreeNode node)
			throws IOException {
		// Look the place to insert the new value
		int index = getIndexOf(v);

		// insert at index
		shiftValuesFromIndexToRight(index);
		shiftChildrenFromIndexToRight(index + 1);
		values[index] = v;
		setChild(index + 1, node);
		node.setParentDir(dir);
		valueCount++;
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
		try {

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
				BTreeNode v = getChild(i);
				if (v != null) {
					strChilds.append(separator).append(
							((AbstractBTreeNode) v).name);
				} else {
					strChilds.append(separator).append("null");
				}
				separator = ", ";
			}

			String ret = name + "\n (" + strValues.toString() + ") \n("
					+ strChilds + ")\n";
			for (int i = 0; i < valueCount + 1; i++) {
				BTreeNode node = getChild(i);
				if (node != null) {
					ret += node.toString();
				}
			}

			return ret;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Value getSmallestValueNotIn(BTreeNode treeNode) throws IOException {
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
	 * @throws IOException
	 */
	public BTreeLeaf getChildNodeFor(Value v) throws IOException {
		for (int i = 0; i < valueCount; i++) {
			if (values[i].isNull() || (v.less(values[i]).getAsBoolean())) {
				return getChild(i).getChildNodeFor(v);
			}
		}

		return getChild(valueCount).getChildNodeFor(v);
	}

	public boolean contains(Value v) throws IOException {
		for (int i = 0; i < valueCount; i++) {
			if (values[i].isNull() || (v.less(values[i]).getAsBoolean())) {
				return getChild(i).contains(v);
			}
		}

		return getChild(valueCount).contains(v);
	}

	public BTreeLeaf getFirstLeaf() throws IOException {
		return getChild(0).getFirstLeaf();
	}

	/**
	 * Notifies that there have been changes in the node specified as a
	 * parameter that may invalidate the values in this node
	 *
	 * @param changingNode
	 * @throws IOException
	 */
	public void smallestNotInLeftElementChanged(BTreeNode changingNode)
			throws IOException {
		// find the pointer
		int childIndex = getIndexOf(changingNode);

		if (childIndex > 0) {
			values[childIndex - 1] = changingNode
					.getSmallestValueNotIn(getChild(childIndex - 1));
		}
		if (childIndex < 2) {
			if (getParentDir() != -1) {
				getParent().smallestNotInLeftElementChanged(this);
			}
		}
	}

	/**
	 * Gets the index of the leaf in the children array
	 *
	 * @param node
	 * @return -1 if the node is not present
	 * @throws IOException
	 */
	private int getIndexOf(BTreeNode node) throws IOException {
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
	public void mergeWithNeighbour(AbstractBTreeNode node) throws IOException {
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
			shiftChildrenFromIndexToLeft(index);
			// remove the values of the left neighbour
			shiftValuesFromIndexToLeft(index);

			valueCount--;
			if ((index == 1) && (getParentDir() != -1)) {
				// values[0] has been modified
				getParent().smallestNotInLeftElementChanged(this);
			}
		} else {
			node.mergeWithRight(rightNeighbour);
			// Remove the pointer to the right neighbour
			shiftChildrenFromIndexToLeft(index + 2);
			// Remove the values to the right neighbour
			shiftValuesFromIndexToLeft(index + 1);

			valueCount--;

			if ((index == 0) && (getParentDir() != -1)) {
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

	protected void mergeWithRight(AbstractBTreeNode rightNode)
			throws IOException {
		BTreeInteriorNode node = (BTreeInteriorNode) rightNode;
		Value[] newValues = new Value[tree.getN() + 1];
		ChildReference[] newChildren = new ChildReference[tree.getN() + 1];
		System.arraycopy(values, 0, newValues, 0, valueCount);
		System.arraycopy(node.values, 0, newValues, valueCount + 1,
				node.valueCount);
		newValues[valueCount] = rightNode.getSmallestValueNotIn(this);
		System.arraycopy(children, 0, newChildren, 0, valueCount + 1);
		// Change the parent in the moving children
		for (int i = 0; i < node.valueCount + 1; i++) {
			node.getChild(i).setParentDir(dir);
		}
		System.arraycopy(node.children, 0, newChildren, valueCount + 1,
				node.valueCount + 1);
		values = newValues;
		children = newChildren;
		valueCount = valueCount + node.valueCount + 1;
	}

	protected void mergeWithLeft(AbstractBTreeNode leftNode) throws IOException {
		BTreeInteriorNode node = (BTreeInteriorNode) leftNode;
		Value[] newValues = new Value[tree.getN() + 1];
		ChildReference[] newChildren = new ChildReference[tree.getN() + 1];
		System.arraycopy(node.values, 0, newValues, 0, node.valueCount);
		System.arraycopy(values, 0, newValues, node.valueCount + 1, valueCount);
		newValues[node.valueCount] = this.getSmallestValueNotIn(leftNode);
		// Change the parent in the moving children
		for (int i = 0; i < node.valueCount + 1; i++) {
			node.getChild(i).setParentDir(dir);
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
	 * @throws IOException
	 */
	public boolean moveFromNeighbour(AbstractBTreeNode node) throws IOException {
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

	protected void moveFirstTo(AbstractBTreeNode node) throws IOException {
		BTreeInteriorNode n = (BTreeInteriorNode) node;
		n.values[n.valueCount] = this.getChild(0).getSmallestValueNotIn(
				n.getChild(n.valueCount));
		n.setChild(n.valueCount + 1, this.getChild(0));

		shiftValuesFromIndexToLeft(1);
		shiftChildrenFromIndexToLeft(1);
		n.valueCount++;
		this.valueCount--;
	}

	protected void moveLastTo(AbstractBTreeNode node) throws IOException {
		BTreeInteriorNode n = (BTreeInteriorNode) node;
		n.shiftChildrenFromIndexToRight(0);
		n.shiftValuesFromIndexToRight(0);
		n.values[0] = node.getSmallestValueNotIn(this);
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
	private AbstractBTreeNode getLeftNeighbour(int index) throws IOException {
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
	 * @throws IOException
	 */
	private AbstractBTreeNode getRightNeighbour(int index) throws IOException {
		if (index < valueCount) {
			return (AbstractBTreeNode) getChild(index + 1);
		} else {
			return null;
		}
	}

	@Override
	protected BTreeNode getChildForNewRoot() throws IOException {
		BTreeNode child = getChild(0);
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

		// Write the children direction
		if (valueCount > 0) {
			for (int i = 0; i < valueCount + 1; i++) {
				dos.writeInt(children[i].getDir());
			}
		}

		dos.close();

		return bos.toByteArray();
	}

	public static BTreeInteriorNode createInteriorNodeFromBytes(DiskBTree tree,
			int dir, int parentDir, int n, byte[] bytes) throws IOException {
		BTreeInteriorNode ret = new BTreeInteriorNode(tree, dir, parentDir);
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

		// Read the children directions
		for (int i = 0; i < ret.valueCount + 1; i++) {
			ret.children[i] = new ChildReference(tree, dis.readInt());
		}

		dis.close();

		return ret;
	}

	private static class ChildReference {
		private BTreeNode object;
		private int dir;
		private DiskBTree tree;

		public ChildReference(DiskBTree tree, BTreeNode object) {
			this.tree = tree;
			this.object = object;
			if (object == null) {
				this.dir = -1;
			} else {
				this.dir = ((AbstractBTreeNode) object).dir;
			}
		}

		public ChildReference(DiskBTree tree, int readInt) {
			this.tree = tree;
			this.dir = readInt;
			this.object = null;
		}

		public BTreeNode getReference() {
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

}
