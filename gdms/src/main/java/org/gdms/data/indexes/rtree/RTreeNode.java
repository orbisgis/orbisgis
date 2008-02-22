package org.gdms.data.indexes.rtree;

import java.io.IOException;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public interface RTreeNode {

	public boolean isLeaf();

	/**
	 * Inserts the element. if there is not enough space reorganizes the tree
	 *
	 * @param v
	 * @param rowIndex
	 *            index where the value is at
	 * @return The new root if the reorganization of the tree changed the root
	 * @throws IOException
	 */
	public RTreeNode insert(Geometry v, int rowIndex) throws IOException;

	/**
	 * Deletes the element. The tree can be reorganized to match btree
	 * restrictions
	 *
	 * @param v
	 * @param row
	 * @return The new root if the reorganization of the tree changed the root
	 * @throws IOException
	 */
	public RTreeNode delete(Geometry v, int row) throws IOException;

	/**
	 * Sets the parent of a node
	 *
	 * @param m
	 */
	public void setParentDir(int parentDir);

	/**
	 * Get the leaf that contains the smallest values
	 *
	 * @return
	 * @throws IOException
	 */
	public RTreeLeaf getFirstLeaf() throws IOException;

	/**
	 * Checks that the tree is well formed. Throws any exception if it's not.
	 * Just for debugging purposes
	 *
	 * @throws IOException
	 */
	public void checkTree() throws IOException;

	/**
	 * Gets the parent of the node. The root node returns null
	 *
	 * @return
	 * @throws IOException
	 */
	public RTreeInteriorNode getParent() throws IOException;

	/**
	 * Gets the representation of this node as a byte array
	 *
	 * @return
	 * @throws IOException
	 */
	public byte[] getBytes() throws IOException;

	/**
	 * Saves permanently this node at disk
	 *
	 * @throws IOException
	 */
	public void save() throws IOException;

	/**
	 * Get the position in the file of this node
	 *
	 * @return
	 */
	public int getDir();

	/**
	 * Gets the envelope of all the geometries under this node
	 *
	 * @return
	 */
	public Envelope getEnvelope();

	/**
	 * Gets the rows that contains geometries that can possibly intersect with
	 * the specified envelopes
	 *
	 * @param value
	 * @return
	 */
	public int[] getRows(Envelope value);

	/**
	 * Gets all the values under this node
	 *
	 * @return
	 * @throws IOException
	 */
	public Geometry[] getAllValues() throws IOException;
}
