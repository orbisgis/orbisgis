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
	public void insert(Geometry v, int rowIndex) throws IOException;

	/**
	 * Deletes the element. The tree can be reorganized to match btree
	 * restrictions
	 *
	 * @param v
	 * @param row
	 * @return The new root if the reorganization of the tree changed the root
	 * @throws IOException
	 */
	public boolean delete(Geometry v, int row) throws IOException;

	/**
	 * Sets the parent of a node
	 *
	 * @param m
	 */
	public void setParentDir(int parentDir);

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
	public AbstractRTreeNode getParent() throws IOException;

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
	 * @throws IOException
	 */
	public Envelope getEnvelope() throws IOException;

	/**
	 * Gets the rows that contains geometries that can possibly intersect with
	 * the specified envelopes
	 *
	 * @param value
	 * @return
	 * @throws IOException
	 */
	public int[] getRows(Envelope value) throws IOException;

	/**
	 * Gets all the values under this node
	 *
	 * @return
	 * @throws IOException
	 */
	public Geometry[] getAllValues() throws IOException;

	/**
	 * Returns true if the node satisfies the conditions of a btree
	 *
	 * @return
	 * @throws IOException
	 */
	public boolean isValid() throws IOException;

	/**
	 * Splits the node of the tree
	 *
	 * @return
	 * @throws IOException
	 */
	public RTreeNode splitNode() throws IOException;

	/**
	 * Returns true if the node can give an element to a neighbour and be still
	 * valid
	 *
	 * @return
	 * @throws IOException
	 */
	public boolean canGiveElement() throws IOException;

	/**
	 * Moves the first element to the specified node. Both nodes are at the same
	 * level so they are implementations of the same class
	 *
	 * @param child
	 * @throws IOException
	 */
	public void moveFirstTo(RTreeNode child) throws IOException;

	/**
	 * Moves the last element to the specified node. Both nodes are at the same
	 * level so they are implementations of the same class
	 *
	 * @param child
	 * @throws IOException
	 */
	public void moveLastTo(RTreeNode child) throws IOException;

	/**
	 * Merges this node with the left one. The left node is specified as a
	 * parameter
	 *
	 * @param leftNeighbour
	 * @throws IOException
	 */
	public void mergeWithLeft(RTreeNode leftNeighbour) throws IOException;

	/**
	 * Merges this node with the right one. The right node is specified as a
	 * parameter
	 *
	 * @param rightNeighbour
	 * @throws IOException
	 */
	public void mergeWithRight(RTreeNode rightNeighbour) throws IOException;

	/**
	 * Gets the new root of the tree if this node is no longer the root
	 *
	 * @return
	 * @throws IOException
	 */
	public RTreeNode getNewRoot() throws IOException;

	public int getValueCount();
}
