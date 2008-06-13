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
package org.gdms.data.indexes.rtree;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

/**
 * @author Fernando Gonzalez Cortes
 *
 */
public class RTreeInteriorNode extends AbstractRTreeNode implements RTreeNode {

	private ArrayList<ChildReference> children;
	private Envelope envelope = null;

	public RTreeInteriorNode(DiskRTree tree, int dir, int parentDir) {
		super(tree, dir, parentDir);
		// one place for overload management
		children = new ArrayList<ChildReference>(tree.getN() + 1);
	}

	public RTreeInteriorNode(DiskRTree tree, int dir, int parentDir,
			RTreeNode left, RTreeNode right) throws IOException {
		this(tree, dir, parentDir);
		addChild(left);
		addChild(right);
	}

	private void addChild(RTreeNode node) {
		children.add(new ChildReference(tree, node));
		if (node != null) {
			node.setParentDir(dir);
		}
	}

	private void insertChild(int i, RTreeNode node) {
		children.add(i, new ChildReference(tree, node));
		if (node != null) {
			node.setParentDir(dir);
		}
	}

	public boolean isLeaf() {
		return false;
	}

	public RTreeNode splitNode() throws IOException {
		RTreeInteriorNode m = tree.createInteriorNode(dir, getParentDir());

		// Get a reference to sort the nodes
		Envelope ref = getEnvelope(0);
		int refIndex = getFarthestGeometry(0, ref);
		ref = getEnvelope(refIndex);

		// Sort nodes by its distance
		TreeSet<ChildReferenceDistance> distances = new TreeSet<ChildReferenceDistance>(
				new Comparator<ChildReferenceDistance>() {

					public int compare(ChildReferenceDistance o1,
							ChildReferenceDistance o2) {
						int dist = (int) (o2.distance - o1.distance);
						if (dist != 0) {
							return dist;
						} else {
							return o2.childIndex - o1.childIndex;
						}
					}

				});
		for (int i = 0; i < children.size(); i++) {
			distances.add(new ChildReferenceDistance(i, ref
					.distance(getEnvelope(i))));
		}
		ArrayList<ChildReference> sortedChildren = new ArrayList<ChildReference>();
		for (ChildReferenceDistance childReferenceDistance : distances) {
			sortedChildren.add(children.get(childReferenceDistance.childIndex));
		}

		// Add the minimum to the left node
		Envelope leftEnv = null;
		children.clear();
		int leftIndex = 0;
		while (!validIfNotRoot(children.size())) {
			ChildReference child = sortedChildren.get(leftIndex);
			child.resolve();
			children.add(child);
			leftIndex++;
			if (leftEnv == null) {
				leftEnv = new Envelope(child.getEnvelope());
			} else {
				leftEnv.expandToInclude(child.getEnvelope());
			}
		}

		// Add the minimum to the right node
		Envelope rightEnv = null;
		m.children.clear();
		int rightIndex = sortedChildren.size() - 1;
		while (!validIfNotRoot(m.children.size())) {
			ChildReference child = sortedChildren.get(rightIndex);
			m.children.add(child);
			child.resolve();
			child.getReference().setParentDir(m.dir);
			rightIndex--;
			if (rightEnv == null) {
				rightEnv = new Envelope(child.getEnvelope());
			} else {
				rightEnv.expandToInclude(child.getEnvelope());
			}
		}

		// Insert the remaining children in the first node until the impact is
		// greater than inserting in the second one
		int index = leftIndex;
		while ((index <= rightIndex)
				&& (getExpandImpact(leftEnv, sortedChildren.get(index)
						.getEnvelope()) < getExpandImpact(rightEnv,
						sortedChildren.get(index).getEnvelope()))) {
			ChildReference child = sortedChildren.get(index);
			children.add(child);
			leftEnv.expandToInclude(child.getEnvelope());
			index++;
		}
		this.envelope = null;

		// Insert the remaining in m
		for (int i = index; i <= rightIndex; i++) {
			ChildReference child = sortedChildren.get(index);
			child.resolve();
			child.getReference().setParentDir(m.dir);
			m.children.add(child);
		}
		m.envelope = null;
		return m;
	}

	private RTreeNode getChild(int i) throws IOException {
		if (!children.get(i).isLoaded()) {
			children.get(i).resolve();
		}
		return children.get(i).getReference();
	}

	private void insertValueAndReferenceAfter(RTreeNode refNode, RTreeNode node)
			throws IOException {
		// Look the place to insert the new value
		int index = getIndexOf(refNode);

		// insert at index
		insertChild(index + 1, node);
		node.setParentDir(dir);
	}

	public void insert(Envelope v, int rowIndex) throws IOException {
		// See the children that contain the geometry
		for (int i = 0; i < children.size(); i++) {
			if (getEnvelope(i).contains(v)) {
				doInsert(v, rowIndex, i);
				return;
			}
		}

		// Take the child with less impact
		double min = Double.MAX_VALUE;
		int argmin = -1;
		for (int i = 0; i < children.size(); i++) {
			Envelope test = new Envelope(getEnvelope(i));
			double initialArea = test.getWidth() * test.getHeight();
			test.expandToInclude(v);
			double finalArea = test.getWidth() * test.getHeight();
			double diff = finalArea - initialArea;
			if (diff < min) {
				argmin = i;
				min = diff;
			}
		}
		doInsert(v, rowIndex, argmin);
	}

	private void doInsert(Envelope v, int rowIndex, int i) throws IOException {
		getChild(i).insert(v, rowIndex);
		Envelope newEnvelope = getChild(i).getEnvelope();
		getEnvelope().expandToInclude(newEnvelope);
		children.get(i).getEnvelope().expandToInclude(newEnvelope);
		if (!getChild(i).isValid()) {
			// If it's invalid the parent will split it
			RTreeNode newNode = getChild(i).splitNode();
			newNode.setParentDir(dir);
			insertValueAndReferenceAfter(getChild(i), newNode);
			children.get(i).setEnvelope(null);
			children.get(i + 1).setEnvelope(null);
			invalidateEnvelope();
			// If this node is the root we need a new one
			if (!isValid() && (getParentDir() == -1)) {
				RTreeNode m = splitNode();
				tree.createInteriorNode(dir, -1, this, m);
			}
		}
	}

	private void invalidateEnvelope() throws IOException {
		envelope = null;
		if (getParentDir() != -1) {
			getParent().invalidateEnvelope();
		}
	}

	public int getRow(Envelope value) {
		throw new UnsupportedOperationException("Cannot get the row "
				+ "in an interior node");
	}

	@Override
	public String toString() {
		try {

			StringBuilder strValues = new StringBuilder("");
			String separator = "";
			for (int i = 0; i < children.size(); i++) {
				Envelope v = getEnvelope(i);
				strValues.append(separator).append(v);
				separator = ", ";
			}
			StringBuilder strChilds = new StringBuilder("");
			separator = "";
			for (int i = 0; i < children.size(); i++) {
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
			for (int i = 0; i < children.size(); i++) {
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

	/**
	 * Gets the index of the leaf in the children array
	 *
	 * @param node
	 * @return -1 if the node is not present
	 * @throws IOException
	 */
	private int getIndexOf(RTreeNode node) throws IOException {
		int childIndex = -1;
		for (int i = 0; i < children.size() + 1; i++) {
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
	 * @return
	 * @return true if we can merge. False otherwise
	 * @throws IOException
	 */
	public boolean mergeWithNeighbour(RTreeNode node) throws IOException {
		int index = getIndexOf(node);
		RTreeNode smallest = null;
		RTreeNode rightNeighbour = getRightNeighbour(index);
		RTreeNode leftNeighbour = getLeftNeighbour(index);

		if ((rightNeighbour != null) && (leftNeighbour != null)) {
			smallest = rightNeighbour;
			if (leftNeighbour.getValueCount() < rightNeighbour.getValueCount()) {
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
			// Remove the pointer to the left neighbour
			children.remove(index - 1);
			children.get(index - 1).setEnvelope(null);
		} else {
			node.mergeWithRight(rightNeighbour);
			// Remove the pointer to the right neighbour
			children.remove(index + 1);
			children.get(index).setEnvelope(null);
		}

		return true;
	}

	public void mergeWithRight(RTreeNode rightNode) throws IOException {
		RTreeInteriorNode node = (RTreeInteriorNode) rightNode;
		ArrayList<ChildReference> newChildren = new ArrayList<ChildReference>();
		newChildren.addAll(children);
		// Change the parent in the moving children
		for (int i = 0; i < node.children.size(); i++) {
			node.getChild(i).setParentDir(dir);
		}
		newChildren.addAll(node.children);

		children = newChildren;

		tree.removeNode(node.dir);
	}

	public void mergeWithLeft(RTreeNode leftNode) throws IOException {
		RTreeInteriorNode node = (RTreeInteriorNode) leftNode;
		ArrayList<ChildReference> newChildren = new ArrayList<ChildReference>();
		// Change the parent in the moving children
		for (int i = 0; i < node.children.size(); i++) {
			node.getChild(i).setParentDir(dir);
		}
		newChildren.addAll(node.children);
		newChildren.addAll(children);

		children = newChildren;

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
	public boolean moveFromNeighbour(int index) throws IOException {
		RTreeNode rightNeighbour = getRightNeighbour(index);
		RTreeNode leftNeighbour = getLeftNeighbour(index);
		if ((rightNeighbour != null) && (rightNeighbour.canGiveElement())) {
			rightNeighbour.moveFirstTo(getChild(index));
			children.get(index + 1).setEnvelope(null);
			children.get(index).setEnvelope(null);
			return true;
		} else if ((leftNeighbour != null) && (leftNeighbour.canGiveElement())) {
			leftNeighbour.moveLastTo(getChild(index));
			children.get(index - 1).setEnvelope(null);
			children.get(index).setEnvelope(null);
			return true;
		} else {
			return false;
		}
	}

	public void moveFirstTo(RTreeNode node) throws IOException {
		RTreeInteriorNode n = (RTreeInteriorNode) node;
		RTreeNode first = getChild(0);
		children.remove(0);
		n.addChild(first);
		invalidateEnvelope();
	}

	public void moveLastTo(RTreeNode node) throws IOException {
		RTreeInteriorNode n = (RTreeInteriorNode) node;
		n.insertChild(0, this.getChild(children.size() - 1));
		children.remove(children.size() - 1);
		invalidateEnvelope();
	}

	/**
	 * Gets the neighbour on the left of the specified node
	 *
	 * @param index
	 * @return
	 * @throws IOException
	 */
	private RTreeNode getLeftNeighbour(int index) throws IOException {
		if (index > 0) {
			return getChild(index - 1);
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
	private RTreeNode getRightNeighbour(int index) throws IOException {
		if (index < children.size() - 1) {
			return getChild(index + 1);
		} else {
			return null;
		}
	}

	public boolean isValid() throws IOException {
		return isValid(children.size());
	}

	public boolean canGiveElement() throws IOException {
		return isValid(children.size() - 1);
	}

	public boolean isValid(int valueCount) throws IOException {
		if (getParentDir() == -1) {
			return (valueCount >= 1) && (valueCount <= tree.getN());
		} else {
			return validIfNotRoot(valueCount);
		}
	}

	private boolean validIfNotRoot(int valueCount) {
		return (valueCount >= ((tree.getN() + 1) / 2))
				&& (valueCount <= tree.getN());
	}

	public void checkTree() throws IOException {
		if (!isValid()) {
			throw new RuntimeException(this + " Not vaylid");
		} else {
			for (int i = 0; i < children.size(); i++) {
				Envelope test = children.get(i).getEnvelope();
				children.get(i).setEnvelope(null);
				if (!test.equals(children.get(i).getEnvelope())) {
					throw new RuntimeException("Bad local envelope" + test);
				}

				if (getChild(i).getParent() != this) {
					throw new RuntimeException(this + " parent is wrong");
				}
				getChild(i).checkTree();

				if (!getChild(i).getEnvelope().equals(getEnvelope(i))) {
					throw new RuntimeException("bad local envelope");
				}
			}
			Envelope global = new Envelope(getChild(0).getEnvelope());
			for (int i = 1; i < children.size(); i++) {
				global.expandToInclude(getChild(i).getEnvelope());
			}
			if (!global.equals(getEnvelope())) {
				throw new RuntimeException("Bad global envelope");
			}
		}
	}

	public boolean delete(Envelope v, int row) throws IOException {
		// Look for the children that can contain the node
		for (int i = 0; i < children.size(); i++) {
			if (getEnvelope(i).contains(v)) {
				if (getChild(i).delete(v, row)) {
					children.get(i).setEnvelope(null);
					invalidateEnvelope();
					if (!getChild(i).isValid()) {
						// move from neighbour
						if (!moveFromNeighbour(i)) {
							if (!mergeWithNeighbour(getChild(i))) {
								// If we cannot merge create new root
								tree.removeNode(this.dir);
								getChild(0).setParentDir(-1);
							}
						}
					}
					return true;
				}
			}
		}

		return false;
	}

	public byte[] getBytes() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);

		// Write the number of values
		dos.writeInt(children.size());

		// Write the children direction
		if (children.size() > 0) {
			for (int i = 0; i < children.size(); i++) {
				dos.writeInt(children.get(i).getDir());
				Envelope envelope = children.get(i).getEnvelope();
				dos.writeDouble(envelope.getMinX());
				dos.writeDouble(envelope.getMinY());
				dos.writeDouble(envelope.getMaxX());
				dos.writeDouble(envelope.getMaxY());
			}
		}

		dos.close();

		return bos.toByteArray();
	}

	public static AbstractRTreeNode createInteriorNodeFromBytes(DiskRTree tree,
			int dir, int parentDir, int n, byte[] bytes) throws IOException {
		RTreeInteriorNode ret = new RTreeInteriorNode(tree, dir, parentDir);
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		DataInputStream dis = new DataInputStream(bis);

		// Read the number of values
		int valueCount = dis.readInt();

		// Read the children directions
		for (int i = 0; i < valueCount; i++) {
			int nodeDir = dis.readInt();
			double minx = dis.readDouble();
			double miny = dis.readDouble();
			double maxx = dis.readDouble();
			double maxy = dis.readDouble();
			Envelope env = new Envelope(new Coordinate(minx, miny),
					new Coordinate(maxx, maxy));
			ret.children.add(new ChildReference(tree, nodeDir, env));
		}

		dis.close();

		return ret;
	}

	private static class ChildReference {
		private RTreeNode object;
		private int dir;
		private DiskRTree tree;
		private Envelope envelope;

		public ChildReference(DiskRTree tree, RTreeNode object) {
			this.tree = tree;
			this.object = object;
			if (object == null) {
				this.dir = -1;
			} else {
				this.dir = ((AbstractRTreeNode) object).dir;
			}
		}

		public void setEnvelope(Envelope envelope) {
			this.envelope = envelope;
		}

		ChildReference(DiskRTree tree, int dir, Envelope envelope) {
			this.tree = tree;
			this.dir = dir;
			this.object = null;
			this.envelope = envelope;
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

		public Envelope getEnvelope() throws IOException {
			if (envelope == null) {
				resolve();
				envelope = getReference().getEnvelope();
			}
			return envelope;
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

	public Envelope getEnvelope() throws IOException {
		if (envelope == null) {
			envelope = new Envelope(getChild(0).getEnvelope());
			for (int i = 1; i < children.size(); i++) {
				envelope.expandToInclude(getChild(i).getEnvelope());
			}
		}

		return envelope;
	}

	public Envelope[] getAllValues() throws IOException {
		ArrayList<Envelope> ret = new ArrayList<Envelope>();
		for (int i = 0; i < children.size(); i++) {
			Envelope[] temp = getChild(i).getAllValues();
			for (Envelope geometry : temp) {
				ret.add(geometry);
			}
		}

		return ret.toArray(new Envelope[0]);
	}

	public int[] getRows(Envelope value) throws IOException {
		ArrayList<int[]> childrenResult = new ArrayList<int[]>();
		int size = 0;
		for (int i = 0; i < children.size(); i++) {
			if (value.intersects(getEnvelope(i))) {
				int[] rows = getChild(i).getRows(value);
				size += rows.length;
				childrenResult.add(rows);
			}
		}

		int[] ret = new int[size];
		int currentPos = 0;
		for (int i = 0; i < childrenResult.size(); i++) {
			int[] childRows = childrenResult.get(i);
			System.arraycopy(childRows, 0, ret, currentPos, childRows.length);
			currentPos += childRows.length;
		}

		return ret;
	}

	public RTreeNode getNewRoot() throws IOException {
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

	public int getValueCount() {
		return children.size();
	}

	@Override
	protected Envelope getEnvelope(int index) throws IOException {
		return children.get(index).getEnvelope();
	}

	public void updateRows(int row, int inc) throws IOException {
		for (int i = 0; i < children.size(); i++) {
			getChild(i).updateRows(row, inc);
		}
	}
}
