/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.data.indexes.btree;

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

/**
 * @author Fernando Gonzalez Cortes
 *
 */
public class BTreeInteriorNode extends AbstractBTreeNode implements BTreeNode {

	private ArrayList<ChildReference> children;

	public BTreeInteriorNode(DiskBTree tree, int dir, int parentDir) {
		super(tree, dir, parentDir);
		children = new ArrayList<ChildReference>();
	}

	public BTreeInteriorNode(DiskBTree tree, int dir, int parentDir,
			BTreeNode left, BTreeNode right) throws IOException {
		this(tree, dir, parentDir);
		values.add(right.getSmallestValueNotIn(left));
		addChild(left);
		addChild(right);

	}

	private void insertChild(int i, BTreeNode node) {
		children.add(i, new ChildReference(tree, node));
		if (node != null) {
			node.setParentDir(dir);
		}
	}

	private void addChild(BTreeNode node) {
		children.add(new ChildReference(tree, node));
		if (node != null) {
			node.setParentDir(dir);
		}
	}

	public boolean isLeaf() {
		return false;
	}

	// /**
	// * Reorganizes the tree with the new leaf that have appeared. It must set
	// * the parent of the leave (still null)
	// *
	// * @param right
	// *
	// * @param v
	// * @return
	// * @throws IOException
	// */
	// public BTreeNode newNodeAppeared(Value smallestNotInOldNode,
	// BTreeNode originalNode, BTreeNode newNode) throws IOException {
	// if (valueCount == tree.getN()) {
	// // split the node, insert and reorganize the tree
	// BTreeInteriorNode m = tree.createInteriorNode(dir, getParentDir());
	// newNode.setParentDir(m.dir);
	//
	// // Create the value array with the new index
	// insertValueAndReferenceAfter(originalNode, smallestNotInOldNode,
	// newNode);
	//
	// // Move half the values to the new node
	// int mIndex = 0;
	// values[(tree.getN() + 1) / 2] = null;
	// for (int i = (tree.getN() + 1) / 2 + 1; i < values.length; i++) {
	// m.values[mIndex] = values[i];
	// m.setChild(mIndex, getChild(i));
	// mIndex++;
	// values[i] = null;
	// setChild(i, null);
	// }
	// m.setChild(mIndex, getChild(tree.getN() + 1));
	// setChild(tree.getN() + 1, null);
	// m.valueCount = mIndex;
	// valueCount = (tree.getN() + 1) / 2;
	// if (getParentDir() == -1) {
	// // We need a new root
	// BTreeInteriorNode newRoot = tree.createInteriorNode(dir, -1,
	// this, m);
	//
	// return newRoot;
	// } else {
	// return getParent().newNodeAppeared(
	// m.getSmallestValueNotIn(this), this, m);
	// }
	// } else {
	// insertValueAndReferenceAfter(originalNode, smallestNotInOldNode,
	// newNode);
	// return null;
	// }
	// }

	private BTreeNode getChild(int i) throws IOException {
		ChildReference childReference = children.get(i);
		if (!childReference.isLoaded()) {
			childReference.resolve();
		}
		return childReference.getReference();
	}

	private void insertValueAndReferenceAfter(BTreeNode refNode, Value v,
			BTreeNode node) throws IOException {
		// Look the place to insert the new value
		int index = getIndexOf(refNode);

		// insert at index
		values.add(index, v);
		insertChild(index + 1, node);
		node.setParentDir(dir);
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
		for (int i = 0; i < children.size(); i++) {
			if (getChild(i) == node) {
				childIndex = i;
				break;
			}
		}
		return childIndex;
	}

	public Value insert(Value v, int rowIndex) throws IOException {
		Value ret = null;

		// See the children that will contain the value
		int index = getChildForValue(v);

		while ((index < children.size() - 1)
				&& (getChild(index + 1).getSmallestValue().lessEqual(v)
						.getAsBoolean())) {
			index++;
		}

		// delegate the insert
		BTreeNode child = getChild(index);
		child.insert(v, rowIndex);

		// update the value
		if (index > 0) {
			values.set(index - 1, child
					.getSmallestValueNotIn(getChild(index - 1)));
		}

		if (!child.isValid()) {
			// If it's invalid the parent will split it
			BTreeNode newNode = child.splitNode();
			newNode.setParentDir(dir);
			Value splitedSmallestValue = newNode.getSmallestValueNotIn(child);
			insertValueAndReferenceAfter(child, splitedSmallestValue, newNode);

			// update the value
			if (index > 0) {
				values.set(index - 1, child
						.getSmallestValueNotIn(getChild(index - 1)));
			}

			// If this node is the root we need a new one
			if (!isValid() && (getParentDir() == -1)) {
				BTreeNode m = splitNode();
				tree.createInteriorNode(dir, -1, this, m);
			}
		}

		return ret;
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
			for (int i = 0; i < values.size(); i++) {
				Value v = values.get(i);
				strValues.append(separator).append(v);
				separator = ", ";
			}
			StringBuilder strChilds = new StringBuilder("");
			separator = "";
			for (int i = 0; i < children.size(); i++) {
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
			for (int i = 0; i < children.size(); i++) {
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
		for (int i = 0; i < children.size(); i++) {
			Value ret = getChild(i).getSmallestValueNotIn(treeNode);
			if (!ret.isNull()) {
				return ret;
			}
		}

		return ValueFactory.createNullValue();
	}

	public BTreeLeaf getFirstLeaf() throws IOException {
		return getChild(0).getFirstLeaf();
	}

	/**
	 * Merges the node with one of its neighbours.
	 *
	 * @param node
	 * @param smallestChanged
	 *            If the node to merge have suffered a deletion in its smallest
	 *            value
	 * @return true if we can merge. False otherwise
	 * @throws IOException
	 */
	public boolean mergeWithNeighbour(BTreeNode node) throws IOException {
		int index = getIndexOf(node);
		AbstractBTreeNode smallest = null;
		AbstractBTreeNode rightNeighbour = getRightNeighbour(index);
		AbstractBTreeNode leftNeighbour = getLeftNeighbour(index);

		if ((rightNeighbour != null) && (leftNeighbour != null)) {
			smallest = rightNeighbour;
			if (leftNeighbour.values.size() < rightNeighbour.values.size()) {
				smallest = leftNeighbour;
			}
		} else if (rightNeighbour != null) {
			smallest = rightNeighbour;
		} else if (leftNeighbour != null) {
			smallest = leftNeighbour;
		} else {
			return false;
		}
		if (smallest == leftNeighbour) {
			node.mergeWithLeft(leftNeighbour);
			children.remove(index - 1);
			values.remove(index - 1);
		} else {
			node.mergeWithRight(rightNeighbour);
			children.remove(index + 1);
			values.remove(index);
		}

		// update the node index
		if (smallest == leftNeighbour) {
			index--;
		}

		if (index > 0) {
			values.set(index - 1, node
					.getSmallestValueNotIn(getChild(index - 1)));
		}

		return true;
	}

	public void mergeWithRight(BTreeNode rightNode) throws IOException {
		BTreeInteriorNode node = (BTreeInteriorNode) rightNode;
		// values.add(values.size(), rightNode.getSmallestValueNotIn(this));
		int first = values.size();
		for (int i = 0; i < node.values.size(); i++) {
			values.add(node.values.get(i));
			ChildReference childRef = node.children.get(i);
			childRef.resolve();
			childRef.getReference().setParentDir(this.dir);
			children.add(childRef);
		}
		ChildReference childRef = node.children.get(node.children.size() - 1);
		childRef.resolve();
		childRef.getReference().setParentDir(this.dir);
		children.add(childRef);
		values.add(first, getChild(first + 1).getSmallestValueNotIn(
				getChild(first)));

		tree.removeNode(node.dir);
	}

	public void mergeWithLeft(BTreeNode leftNode) throws IOException {
		BTreeInteriorNode node = (BTreeInteriorNode) leftNode;
		for (int i = 0; i < node.values.size(); i++) {
			values.add(i, node.values.get(i));
			ChildReference childRef = node.children.get(i);
			childRef.resolve();
			childRef.getReference().setParentDir(this.dir);
			children.add(i, childRef);
		}
		int middleIndex = node.values.size();
		ChildReference childRef = node.children.get(node.children.size() - 1);
		childRef.resolve();
		childRef.getReference().setParentDir(this.dir);
		children.add(middleIndex, childRef);
		values.add(middleIndex, getChild(middleIndex + 1)
				.getSmallestValueNotIn(getChild(middleIndex)));

		tree.removeNode(node.dir);
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
	public boolean moveFromNeighbour(BTreeNode node) throws IOException {
		int index = getIndexOf(node);
		BTreeNode rightNeighbour = getRightNeighbour(index);
		BTreeNode leftNeighbour = getLeftNeighbour(index);
		if ((rightNeighbour != null) && (rightNeighbour.canGiveElement())) {
			rightNeighbour.moveFirstTo(node);
			values.set(index, rightNeighbour.getSmallestValueNotIn(node));
			if (index > 0) {
				values
						.set(index - 1, node
								.getSmallestValueNotIn(leftNeighbour));
			}
			return true;
		} else if ((leftNeighbour != null) && (leftNeighbour.canGiveElement())) {
			leftNeighbour.moveLastTo(node);
			values.set(index - 1, node.getSmallestValueNotIn(leftNeighbour));
			if (index > 1) {
				values.set(index - 2, leftNeighbour
						.getSmallestValueNotIn(getLeftNeighbour(index - 1)));
			}
			return true;
		} else {
			return false;
		}
	}

	public void moveFirstTo(BTreeNode node) throws IOException {
		BTreeInteriorNode n = (BTreeInteriorNode) node;
		ChildReference childRef = children.remove(0);
		childRef.resolve();
		childRef.getReference().setParentDir(node.getDir());
		n.children.add(childRef);
		values.remove(0);
		n.values.add(n.getChild(n.children.size() - 1).getSmallestValueNotIn(
				n.getChild(n.children.size() - 2)));
	}

	public void moveLastTo(BTreeNode node) throws IOException {
		BTreeInteriorNode n = (BTreeInteriorNode) node;
		values.remove(values.size() - 1);
		ChildReference childRef = children.remove(children.size() - 1);
		childRef.resolve();
		childRef.getReference().setParentDir(node.getDir());
		n.children.add(0, childRef);
		n.values.add(0, n.getChild(1).getSmallestValueNotIn(n.getChild(0)));
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
		if (index < values.size()) {
			return (AbstractBTreeNode) getChild(index + 1);
		} else {
			return null;
		}
	}

	@Override
	protected boolean isValid(int valueCount) throws IOException {
		if (getParentDir() == -1) {
			return (valueCount >= 1) && (valueCount <= tree.getN());
		} else {
			return (valueCount + 1 >= ((tree.getN() + 1) / 2))
					&& (valueCount <= tree.getN());
		}
	}

	public boolean contains(Value v) throws IOException {
		int index = getChildForValue(v);
		if (getChild(index).contains(v)) {
			return true;
		} else {
			while ((index < children.size() - 1)
					&& (getChild(index + 1).getSmallestValue().lessEqual(v)
							.getAsBoolean())) {
				index++;
				if (getChild(index).contains(v)) {
					return true;
				}
			}

			return false;
		}
	}

	public void checkTree() throws IOException {
		if (!isValid(values.size())) {
			throw new RuntimeException(this + " Not enough childs");
		} else {
			for (int i = 0; i < children.size(); i++) {
				if (getChild(i).getParent() != this) {
					throw new RuntimeException(this + " parent is wrong");
				}
				getChild(i).checkTree();

				if (i > 0) {
					Value smallestValueNotIn = getChild(i)
							.getSmallestValueNotIn(getChild(i - 1));
					if (smallestValueNotIn.isNull()) {
						if (!values.get(i - 1).isNull()) {
							throw new RuntimeException("The " + i
									+ "th value is not right");
						}
					} else {
						if (!smallestValueNotIn.equals(values.get(i - 1))
								.getAsBoolean()) {
							throw new RuntimeException("The " + i
									+ "th value is not right");
						}
					}
				}
			}
		}
	}

	public int getChildForValue(Value v) {
		int index = values.size();
		for (int i = 0; i < values.size(); i++) {
			if (v.less(values.get(i)).getAsBoolean()) {
				index = i;
				break;
			}
		}

		while ((index > 0) && (values.get(index - 1).isNull())) {
			index--;
		}

		return index;
	}

	public boolean delete(Value v, int row) throws IOException {
		int index = getChildForValue(v);
		BTreeNode child = getChild(index);

		boolean done = false;
		while (!done) {
			if (child.delete(v, row)) {
				// update value
				if (index > 0) {
					Value smaller = child
							.getSmallestValueNotIn(getChild(index - 1));
					values.set(index - 1, smaller);
				}
				if (index < values.size()) {
					Value smaller = getChild(index + 1).getSmallestValueNotIn(
							getChild(index));
					values.set(index, smaller);
				}

				// Check validity
				if (!child.isValid()) {
					// move from neighbour
					if (!moveFromNeighbour(child)) {
						if (!mergeWithNeighbour(child)) {
							// If we cannot merge create new root
							tree.removeNode(this.dir);
							getChild(0).setParentDir(-1);
						} else if ((getParentDir() == -1) && (!isValid())) {
							tree.removeNode(this.dir);
							getChild(0).setParentDir(-1);
						}
					}
				}
				return true;
			} else {
				index++;
			}

			if (index >= children.size()) {
				done = true;
			} else {
				child = getChild(index);
				if (child.getSmallestValue().greater(v).getAsBoolean()) {
					done = true;
				}
			}
		}

		return false;
	}

	public byte[] getBytes() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);

		// Write the number of values
		dos.writeInt(values.size());

		// Write a ValueCollection with the used values
		ValueCollection vc = ValueFactory.createValue(values
				.toArray(new Value[0]));
		byte[] valuesBytes = vc.getBytes();
		dos.writeInt(valuesBytes.length);
		dos.write(valuesBytes);

		// Write the children direction
		if (values.size() > 0) {
			for (int i = 0; i < children.size(); i++) {
				dos.writeInt(children.get(i).getDir());
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
		int valueCount = dis.readInt();

		// Read the values
		int valuesBytesLength = dis.readInt();
		byte[] valuesBytes = new byte[valuesBytesLength];
		dis.read(valuesBytes);
		ValueCollection vc = (ValueCollection) ValueFactory.createValue(
				Type.COLLECTION, valuesBytes);
		Value[] readvalues = vc.getValues();
		for (Value value : readvalues) {
			ret.values.add(value);
		}

		// Read the children directions
		for (int i = 0; i < valueCount + 1; i++) {
			ret.children.add(new ChildReference(tree, dis.readInt()));
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

		for (int i = 0; i < children.size(); i++) {
			ChildReference childRef = children.get(i);
			if (childRef.isLoaded()) {
				childRef.getReference().save();
			}
		}
	}

	public boolean isValid() throws IOException {
		return isValid(values.size());
	}

	public BTreeNode splitNode() throws IOException {
		BTreeInteriorNode m = tree.createInteriorNode(dir, getParentDir());
		m.setParentDir(this.dir);

		// Move half the values to the new node
		int mIndex = 0;
		for (int i = (tree.getN() + 1) / 2 + 1; i < values.size();) {
			m.values.add(values.remove(i));
			ChildReference child = children.remove(i);
			child.resolve();
			child.getReference().setParentDir(m.dir);
			m.addChild(child.getReference());
			mIndex++;
		}
		values.remove((tree.getN() + 1) / 2);
		ChildReference child = children.remove((tree.getN() + 1) / 2 + 1);
		child.resolve();
		child.getReference().setParentDir(m.dir);
		m.addChild(child.getReference());

		return m;
	}

	public BTreeNode getNewRoot() throws IOException {
		if (getParentDir() != -1) {
			return getParent().getNewRoot();
		} else {
			if (getChild(0).getParent() == null) {
				return getChild(0).getNewRoot();
			} else {
				return this;
			}
		}
	}

	public int[] getIndex(RangeComparator minComparator,
			RangeComparator maxComparator) throws IOException {
		int[] minChildRange = minComparator.getRange(this);
		int[] maxChildRange = maxComparator.getRange(this);

		int minChild = Math.max(minChildRange[0], maxChildRange[0]);
		int maxChild = Math.min(minChildRange[1], maxChildRange[1]);

		int[] childResult = getChild(minChild).getIndex(minComparator,
				maxComparator);
		ArrayList<int[]> childrenResult = new ArrayList<int[]>();
		int index = minChild + 1;
		int numResults = 0;
		while (index <= maxChild) {
			numResults += childResult.length;
			childrenResult.add(childResult);
			childResult = getChild(index)
					.getIndex(minComparator, maxComparator);
			index++;
		}
		numResults += childResult.length;
		childrenResult.add(childResult);
		int[] ret = new int[numResults];
		int acum = 0;
		for (int[] is : childrenResult) {
			System.arraycopy(is, 0, ret, acum, is.length);
			acum += is.length;
		}

		return ret;
	}

	public Value[] getAllValues() throws IOException {
		ArrayList<Value> ret = new ArrayList<Value>();
		for (int i = 0; i < children.size(); i++) {
			Value[] temp = getChild(i).getAllValues();
			for (Value geometry : temp) {
				ret.add(geometry);
			}
		}

		return ret.toArray(new Value[0]);
	}

	public Value getSmallestValue() throws IOException {
		return getChild(0).getSmallestValue();
	}

	public void updateRows(int row, int inc) throws IOException {
		for (int i = 0; i < children.size(); i++) {
			getChild(i).updateRows(row, inc);
		}
	}

}
