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

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.data.values.ValueFactory;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.io.ParseException;

public class RTreeLeaf extends AbstractRTreeNode implements RTreeNode {

	private ArrayList<Envelope> geometries;
	private ArrayList<Integer> rows;
	private Envelope envelope;

	public RTreeLeaf(DiskRTree tree, int dir, int parentDir) {
		super(tree, dir, parentDir);
		rows = new ArrayList<Integer>(tree.getN() + 1);
		geometries = new ArrayList<Envelope>(tree.getN() + 1);
	}

	public RTreeNode splitNode() throws IOException {
		RTreeLeaf right = tree.createLeaf(tree, dir, getParentDir());

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
		for (int i = 0; i < geometries.size(); i++) {
			distances.add(new ChildReferenceDistance(i, ref
					.distance(getEnvelope(i))));
		}
		ArrayList<Envelope> sortedGeometries = new ArrayList<Envelope>();
		ArrayList<Integer> sortedRows = new ArrayList<Integer>();
		for (ChildReferenceDistance geometryDistance : distances) {
			int childIndex = geometryDistance.childIndex;
			sortedGeometries.add(geometries.get(childIndex));
			sortedRows.add(rows.get(childIndex));
		}

		// Add the minimum to the left node
		Envelope leftEnv = null;
		geometries.clear();
		rows.clear();
		int leftIndex = 0;
		while (!validIfNotRoot(geometries.size())) {
			Envelope child = sortedGeometries.get(leftIndex);
			geometries.add(child);
			rows.add(sortedRows.get(leftIndex));
			leftIndex++;
			if (leftEnv == null) {
				leftEnv = new Envelope(child);
			} else {
				leftEnv.expandToInclude(child);
			}
		}

		// Add the minimum to the right node
		Envelope rightEnv = null;
		right.geometries.clear();
		right.rows.clear();
		int rightIndex = sortedGeometries.size() - 1;
		while (!validIfNotRoot(right.geometries.size())) {
			Envelope child = sortedGeometries.get(rightIndex);
			right.geometries.add(child);
			right.rows.add(sortedRows.get(rightIndex));
			rightIndex--;
			if (rightEnv == null) {
				rightEnv = new Envelope(child);
			} else {
				rightEnv.expandToInclude(child);
			}
		}

		// Insert the remaining children in the first node until the impact is
		// greater than inserting in the second one
		int index = leftIndex;
		while ((index <= rightIndex)
				&& (getExpandImpact(leftEnv, sortedGeometries.get(index)) < getExpandImpact(
						rightEnv, sortedGeometries.get(index)))) {
			Envelope child = sortedGeometries.get(index);
			geometries.add(child);
			rows.add(sortedRows.get(index));
			leftEnv.expandToInclude(child);
			index++;
		}
		this.envelope = null;

		// Insert the remaining in m
		for (int i = index; i <= rightIndex; i++) {
			Envelope child = sortedGeometries.get(index);
			right.geometries.add(child);
			right.rows.add(sortedRows.get(index));
		}
		right.envelope = null;
		return right;
	}

	public void insert(Envelope v, int rowIndex) throws IOException {
		if ((geometries.size() == 0) || !getEnvelope().contains(v)) {
			geometries.add(v);
			rows.add(rowIndex);
			envelope = null;// TODO if isValid expandToInclude
		} else {
			geometries.add(v);
			rows.add(rowIndex);
		}
		if (!isValid() && (getParentDir() == -1)) {
			// new root
			tree.createInteriorNode(dir, -1, this, splitNode());
		}
	}

	public boolean isLeaf() {
		return true;
	}

	@Override
	public String toString() {
		StringBuilder strValues = new StringBuilder("");
		String separator = "";
		for (int i = 0; i < geometries.size(); i++) {
			Envelope v = geometries.get(i);
			strValues.append(separator).append(v.toString());
			separator = ", ";
		}

		return name + " (" + strValues.toString() + ") ";
	}

	//
	// private boolean contains(Geometry v) {
	// for (Geometry geom : values) {
	// if (geom.equals(v)) {
	// return true;
	// }
	// }
	//
	// return false;
	// }

	public Envelope[] getAllValues() throws IOException {
		return geometries.toArray(new Envelope[0]);
	}

	public void mergeWithLeft(RTreeNode leftNode) throws IOException {
		RTreeLeaf node = (RTreeLeaf) leftNode;
		ArrayList<Envelope> newValues = new ArrayList<Envelope>(tree.getN() + 1);
		ArrayList<Integer> newRows = new ArrayList<Integer>(tree.getN() + 1);
		newValues.addAll(node.geometries);
		newValues.addAll(geometries);
		newRows.addAll(node.rows);
		newRows.addAll(rows);

		this.geometries = newValues;
		this.rows = newRows;
		envelope = null;

		tree.removeNode(node.dir);
	}

	public void mergeWithRight(RTreeNode rightNode) throws IOException {
		RTreeLeaf node = (RTreeLeaf) rightNode;
		ArrayList<Envelope> newValues = new ArrayList<Envelope>(tree.getN() + 1);
		ArrayList<Integer> newRows = new ArrayList<Integer>(tree.getN() + 1);
		newValues.addAll(geometries);
		newValues.addAll(node.geometries);
		newRows.addAll(rows);
		newRows.addAll(node.rows);

		this.geometries = newValues;
		this.rows = newRows;
		envelope = null;

		tree.removeNode(node.dir);
	}

	public void moveFirstTo(RTreeNode node) {
		RTreeLeaf leaf = (RTreeLeaf) node;
		leaf.geometries.add(geometries.remove(0));
		leaf.rows.add(rows.remove(0));
		leaf.envelope = null;
		envelope = null;
	}

	public void moveLastTo(RTreeNode node) {
		RTreeLeaf leaf = (RTreeLeaf) node;
		leaf.geometries.add(0, geometries.remove(geometries.size() - 1));
		leaf.rows.add(0, rows.remove(rows.size() - 1));
		leaf.envelope = null;
		envelope = null;
	}

	public boolean delete(Envelope v, int row) throws IOException {
		int index = getIndexOf(v, row);
		if (index != -1) {
			simpleDeletion(index);
			envelope = null;
			return true;
		} else {
			return false;
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
		geometries.remove(index);
		rows.remove(index);
	}

	private int getIndexOf(Envelope v, int row) throws IOException {
		for (int i = 0; i < geometries.size(); i++) {
			if ((rows.get(i) == row) && (geometries.get(i).equals(v))) {
				return i;
			}
		}
		return -1;
	}

	public void checkTree() throws IOException {
		if (!isValid()) {
			throw new RuntimeException(this + " is not valid");
		} else {
			if (geometries.size() > 0) {
				Envelope testEnvelope = new Envelope(geometries.get(0));
				for (int i = 1; i < geometries.size(); i++) {
					testEnvelope.expandToInclude(geometries.get(i));
				}
				if (!testEnvelope.equals(getEnvelope())) {
					throw new RuntimeException("bad envelope");
				}
			}
		}
	}

	public byte[] getBytes() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);

		// Write the number of values
		dos.writeInt(geometries.size());

		// Write a ValueCollection with the used values
		Envelope[] used = geometries.toArray(new Envelope[0]);
		Value[] usedValues = new Value[4 * used.length];
		for (int i = 0; i < usedValues.length / 4; i++) {
			usedValues[4 * i] = ValueFactory.createValue(used[i].getMinX());
			usedValues[4 * i + 1] = ValueFactory.createValue(used[i].getMinY());
			usedValues[4 * i + 2] = ValueFactory.createValue(used[i].getMaxX());
			usedValues[4 * i + 3] = ValueFactory.createValue(used[i].getMaxY());
		}
		ValueCollection col = ValueFactory.createValue(usedValues);
		byte[] valuesBytes = col.getBytes();
		dos.writeInt(valuesBytes.length);
		dos.write(valuesBytes);

		// Write the row indexes
		for (int i = 0; i < geometries.size(); i++) {
			dos.writeInt(rows.get(i));
		}

		dos.close();

		return bos.toByteArray();
	}

	public static RTreeLeaf createLeafFromBytes(DiskRTree tree, int dir,
			int parentDir, int n, byte[] bytes) throws IOException,
			ParseException {
		RTreeLeaf ret = new RTreeLeaf(tree, dir, parentDir);
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		DataInputStream dis = new DataInputStream(bis);

		// Read the number of values
		int valueCount = dis.readInt();

		// Read the values
		int valuesBytesLength = dis.readInt();
		byte[] valuesBytes = new byte[valuesBytesLength];
		dis.read(valuesBytes);
		ValueCollection col = (ValueCollection) ValueCollection
				.readBytes(valuesBytes);
		Value[] values = col.getValues();
		ret.geometries = new ArrayList<Envelope>();
		for (int i = 0; i < values.length / 4; i++) {
			double minx = values[4 * i].getAsDouble();
			double miny = values[4 * i + 1].getAsDouble();
			double maxx = values[4 * i + 2].getAsDouble();
			double maxy = values[4 * i + 3].getAsDouble();
			ret.geometries.add(new Envelope(minx, maxx, miny, maxy));
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

	public Envelope getEnvelope() {
		if (envelope == null) {
			envelope = new Envelope(geometries.get(0));
			for (int i = 1; i < geometries.size(); i++) {
				envelope.expandToInclude(geometries.get(i));
			}
		}
		return envelope;
	}

	public int[] getRows(Envelope value) {
		int[] intersecting = new int[geometries.size()];
		int index = 0;
		for (int i = 0; i < geometries.size(); i++) {
			if (geometries.get(i).intersects(value)) {
				intersecting[index] = rows.get(i);
				index++;
			}
		}

		int[] ret = new int[index];
		System.arraycopy(intersecting, 0, ret, 0, index);

		return ret;
	}

	public boolean isValid() {
		return isValid(geometries.size());
	}

	private boolean isValid(int valueCount) {
		if (getParentDir() == -1) {
			return (valueCount >= 0) && (valueCount <= tree.getN());
		} else {
			return validIfNotRoot(valueCount);
		}
	}

	private boolean validIfNotRoot(int valueCount) {
		return (valueCount >= ((tree.getN() + 1) / 2))
				&& (valueCount <= tree.getN());
	}

	public boolean canGiveElement() {
		return isValid(geometries.size() - 1);
	}

	public RTreeNode getNewRoot() throws IOException {
		if (getParentDir() != -1) {
			return getParent().getNewRoot();
		} else {
			return this;
		}
	}

	public int getValueCount() {
		return geometries.size();
	}

	@Override
	protected Envelope getEnvelope(int index) throws IOException {
		return geometries.get(index);
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