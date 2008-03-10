package org.gdms.data.indexes.rtree;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKBWriter;

public class RTreeLeaf extends AbstractRTreeNode implements RTreeNode {

	private ArrayList<Geometry> geometries;
	private ArrayList<Integer> rows;
	private Envelope envelope;

	public RTreeLeaf(DiskRTree tree, int dir, int parentDir) {
		super(tree, dir, parentDir);
		rows = new ArrayList<Integer>(tree.getN() + 1);
		geometries = new ArrayList<Geometry>(tree.getN() + 1);
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
		ArrayList<Geometry> sortedGeometries = new ArrayList<Geometry>();
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
			Geometry child = sortedGeometries.get(leftIndex);
			geometries.add(child);
			rows.add(sortedRows.get(leftIndex));
			leftIndex++;
			if (leftEnv == null) {
				leftEnv = new Envelope(child.getEnvelopeInternal());
			} else {
				leftEnv.expandToInclude(child.getEnvelopeInternal());
			}
		}

		// Add the minimum to the right node
		Envelope rightEnv = null;
		right.geometries.clear();
		right.rows.clear();
		int rightIndex = sortedGeometries.size() - 1;
		while (!validIfNotRoot(right.geometries.size())) {
			Geometry child = sortedGeometries.get(rightIndex);
			right.geometries.add(child);
			right.rows.add(sortedRows.get(rightIndex));
			rightIndex--;
			if (rightEnv == null) {
				rightEnv = new Envelope(child.getEnvelopeInternal());
			} else {
				rightEnv.expandToInclude(child.getEnvelopeInternal());
			}
		}

		// Insert the remaining children in the first node until the impact is
		// greater than inserting in the second one
		int index = leftIndex;
		while ((index <= rightIndex)
				&& (getExpandImpact(leftEnv, sortedGeometries.get(index)
						.getEnvelopeInternal()) < getExpandImpact(rightEnv,
						sortedGeometries.get(index).getEnvelopeInternal()))) {
			Geometry child = sortedGeometries.get(index);
			geometries.add(child);
			rows.add(sortedRows.get(index));
			leftEnv.expandToInclude(child.getEnvelopeInternal());
			index++;
		}
		this.envelope = null;

		// Insert the remaining in m
		for (int i = index; i <= rightIndex; i++) {
			Geometry child = sortedGeometries.get(index);
			right.geometries.add(child);
			right.rows.add(sortedRows.get(index));
		}
		right.envelope = null;
		return right;

		/*
		 * // Find farthest envelopes Envelope ref =
		 * geometries.get(0).getEnvelopeInternal(); int farthest1 =
		 * getFarthestGeometry(0, ref); int farthest2 =
		 * getFarthestGeometry(farthest1, geometries
		 * .get(farthest1).getEnvelopeInternal());
		 *
		 * ArrayList<Integer> toRight = new ArrayList<Integer>(); ArrayList<Integer>
		 * toLeft = new ArrayList<Integer>(); // Move farthest2 to "right"
		 * toLeft.add(farthest1); toRight.add(farthest2); // Split the remaining
		 * in the two nodes for (int i = 0; i < geometries.size(); i++) { if ((i ==
		 * farthest1) || (i == farthest2)) { continue; } else { double diff1 =
		 * getExpandImpact(farthest1, i); double diff2 =
		 * getExpandImpact(farthest2, i); if (diff1 > diff2) { toRight.add(i); }
		 * else { toLeft.add(i); } } } // configure right node for (int i = 0; i <
		 * toRight.size(); i++) { int elementIndex = toRight.get(i);
		 * right.insert(geometries.get(elementIndex), rows.get(elementIndex)); } //
		 * configure left node ArrayList<Geometry> newValues = new ArrayList<Geometry>(tree.getN() +
		 * 1); ArrayList<Integer> newRows = new ArrayList<Integer>(tree.getN() +
		 * 1); for (int i = 0; i < toLeft.size(); i++) { Integer elementIndex =
		 * toLeft.get(i); newValues.add(geometries.get(elementIndex));
		 * newRows.add(rows.get(elementIndex)); } geometries = newValues; rows =
		 * newRows; envelope = null;
		 *
		 * return right;
		 */
	}

	public void insert(Geometry v, int rowIndex) throws IOException {
		if ((geometries.size() == 0)
				|| !getEnvelope().contains(v.getEnvelopeInternal())) {
			geometries.add(v);
			rows.add(rowIndex);
			envelope = null;// TODO si isValid expandToInclude
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
		for (int i = 0; i < geometries.size(); i++) {
			if (geometries.get(i).equals(value)) {
				thisRows[index] = rows.get(i);
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
		for (int i = 0; i < geometries.size(); i++) {
			Geometry v = geometries.get(i);
			strValues.append(separator).append(v.toText());
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

	public Geometry[] getAllValues() throws IOException {
		return geometries.toArray(new Geometry[0]);
	}

	public void mergeWithLeft(RTreeNode leftNode) throws IOException {
		RTreeLeaf node = (RTreeLeaf) leftNode;
		ArrayList<Geometry> newValues = new ArrayList<Geometry>(tree.getN() + 1);
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
		ArrayList<Geometry> newValues = new ArrayList<Geometry>(tree.getN() + 1);
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

	public boolean delete(Geometry v, int row) throws IOException {
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

	private int getIndexOf(Geometry v, int row) throws IOException {
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
				Envelope testEnvelope = new Envelope(geometries.get(0)
						.getEnvelopeInternal());
				for (int i = 1; i < geometries.size(); i++) {
					testEnvelope.expandToInclude(geometries.get(i)
							.getEnvelopeInternal());
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
		Geometry[] used = geometries.toArray(new Geometry[0]);
		GeometryCollection col = new GeometryFactory()
				.createGeometryCollection(used);
		WKBWriter writer = new WKBWriter(3);
		byte[] valuesBytes = writer.write(col);
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
		GeometryCollection gc = (GeometryCollection) new WKBReader()
				.read(valuesBytes);
		ret.geometries = new ArrayList<Geometry>();
		for (int i = 0; i < gc.getNumGeometries(); i++) {
			ret.geometries.add(gc.getGeometryN(i));
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
			envelope = new Envelope(geometries.get(0).getEnvelopeInternal());
			for (int i = 1; i < geometries.size(); i++) {
				envelope.expandToInclude(geometries.get(i)
						.getEnvelopeInternal());
			}
		}
		return envelope;
	}

	public int[] getRows(Envelope value) {
		int[] intersecting = new int[geometries.size()];
		int index = 0;
		for (int i = 0; i < geometries.size(); i++) {
			if (geometries.get(i).getEnvelopeInternal().intersects(value)) {
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
		return geometries.get(index).getEnvelopeInternal();
	}

}