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

public class BTreeLeaf extends AbstractBTreeNode implements BTreeNode {

	private ArrayList<Integer> rows;

	public BTreeLeaf(DiskBTree tree, int dir, int parentDir) {
		super(tree, dir, parentDir);
		rows = new ArrayList<Integer>();
	}

	public Value insert(Value v, int rowIndex) throws IOException {
		int index = getIndexOf(v);

		// insert in index
		Value ret = null;
		values.add(index, v);
		rows.add(index, rowIndex);
		if (index == 0) {
			ret = v;
		} else if (values.get(index - 1).isNull()) {
			ret = v;
		}

		if (!isValid() && (getParentDir() == -1)) {
			// new root
			tree.createInteriorNode(dir, -1, this, splitNode());
		}

		return ret;
	}

	public boolean isLeaf() {
		return true;
	}

	@Override
	public String toString() {
		StringBuilder strValues = new StringBuilder("");
		String separator = "";
		for (int i = 0; i < values.size(); i++) {
			Value v = values.get(i);
			strValues.append(separator).append(v);
			separator = ", ";
		}

		return name + " (" + strValues.toString() + ") ";
	}

	public Value getSmallestValueNotIn(BTreeNode treeNode) throws IOException {
		for (int i = 0; i < values.size(); i++) {
			if (!(treeNode.contains(values.get(i)))) {
				return values.get(i);
			} else if (values.get(values.size() - 1).equals(values.get(i))
					.getAsBoolean()) {
				return ValueFactory.createNullValue();
			}
		}

		return ValueFactory.createNullValue();

	}

	public Value[] getAllValues() throws IOException {
		return values.toArray(new Value[0]);

	}

	public BTreeLeaf getFirstLeaf() {
		return this;
	}

	public void mergeWithLeft(BTreeNode leftNode) throws IOException {
		BTreeLeaf leaf = (BTreeLeaf) leftNode;
		values.addAll(0, leaf.values);
		rows.addAll(0, leaf.rows);
		tree.removeNode(leaf.dir);
	}

	public void mergeWithRight(BTreeNode rightNode) throws IOException {
		BTreeLeaf leaf = (BTreeLeaf) rightNode;
		values.addAll(leaf.values);
		rows.addAll(leaf.rows);
		tree.removeNode(leaf.dir);
	}

	public void moveFirstTo(BTreeNode node) {
		BTreeLeaf leaf = (BTreeLeaf) node;
		leaf.values.add(values.remove(0));
		leaf.rows.add(rows.remove(0));
	}

	public void moveLastTo(BTreeNode node) {
		BTreeLeaf leaf = (BTreeLeaf) node;
		leaf.values.add(0, values.remove(values.size() - 1));
		leaf.rows.add(0, rows.remove(rows.size() - 1));
	}

	public boolean delete(Value v, int row) throws IOException {
		int index = getIndexOf(v, row);
		if (index != -1) {
			values.remove(index);
			rows.remove(index);
			return true;
		} else {
			return false;
		}
	}

	private int getIndexOf(Value v, int row) throws IOException {
		int index = getIndexOf(v);
		// If we don't find the value return -1
		if ((index == -1) || (index >= values.size())) {
			return -1;
		} else {
			// Look for the pair value-row
			while ((index < values.size())
					&& (values.get(index).equals(v).getAsBoolean())) {
				if (rows.get(index) == row) {
					return index;
				}
				index++;
			}
			return -1;
		}
	}

	@Override
	protected boolean isValid(int valueCount) throws IOException {
		if (getParentDir() == -1) {
			return (valueCount >= 0) && (valueCount <= tree.getN());
		} else {
			return (valueCount >= ((tree.getN() + 1) / 2))
					&& (valueCount <= tree.getN());
		}
	}

	public void checkTree() throws IOException {
		if (!isValid()) {
			throw new RuntimeException(this + " is not valid");
		}
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

		// Write the row indexes
		for (int i = 0; i < rows.size(); i++) {
			dos.writeInt(rows.get(i));
		}

		dos.close();

		return bos.toByteArray();
	}

	public static BTreeLeaf createLeafFromBytes(DiskBTree tree, int dir,
			int parentDir, int n, byte[] bytes) throws IOException {
		BTreeLeaf ret = new BTreeLeaf(tree, dir, parentDir);
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

		// Read the rowIndexes
		for (int i = 0; i < valueCount; i++) {
			ret.rows.add(dis.readInt());
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

	// public BTreeLeaf getLeafToInsert(Value v) throws IOException {
	// if (v.lessEqual(values[valueCount - 1]).getAsBoolean()) {
	// return this;
	// } else if (getRightNeighbourDir() == -1) {
	// return this;
	// } else if (getRightNeighbour().values[0].greater(v).getAsBoolean()) {
	// return this;
	// } else {
	// return getRightNeighbour().getLeafToInsert(v);
	// }
	// }

	public int[] getIndex(RangeComparator minComparator,
			RangeComparator maxComparator) throws IOException {
		if (values.size() ==0) {
			return new int[0];
		} else {
			int[] thisNode = new int[tree.getN()];
			int index = 0;
			Value lastValueInNode = values.get(values.size() - 1);
			if (minComparator.isInRange(lastValueInNode)) {
				boolean inRange = false;
				for (int i = 0; i < values.size(); i++) {
					if (minComparator.isInRange(values.get(i))
							&& maxComparator.isInRange(values.get(i))) {
						inRange = true;
						thisNode[index] = rows.get(i);
						index++;
					} else {
						if (inRange) {
							// We have finished our range
							break;
						}
					}
				}
			}

			int[] ret = new int[index];
			System.arraycopy(thisNode, 0, ret, 0, index);
			return ret;
		}
	}

	public boolean isValid() throws IOException {
		return isValid(values.size());
	}

	public BTreeNode splitNode() throws IOException {
		// insert the value, split the node and reorganize the tree
		BTreeLeaf right = tree.createLeaf(tree, dir, getParentDir());
		for (int i = (tree.getN() + 1) / 2; i < values.size();) {
			right.insert(values.remove(i), rows.remove(i));
		}

		return right;
	}

	public BTreeNode getNewRoot() throws IOException {
		if (getParentDir() != -1) {
			return getParent().getNewRoot();
		} else {
			return this;
		}
	}

	public Value getSmallestValue() throws IOException {
		return values.get(0);
	}

	public boolean contains(Value value) {
		for (Value testValue : values) {
			if (testValue.equals(value).getAsBoolean()) {
				return true;
			}
		}

		return false;
	}

	public void updateRows(int row, int inc) throws IOException {
		for (int i = 0; i < rows.size(); i++) {
			Integer currentRow = rows.get(i);
			if (currentRow >= row) {
				rows.set(i, currentRow + inc);
			}
		}
	}

}