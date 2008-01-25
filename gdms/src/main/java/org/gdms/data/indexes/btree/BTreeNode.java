package org.gdms.data.indexes.btree;

import org.gdms.data.values.Value;

public interface BTreeNode {
	public boolean isLeaf();

	/**
	 * Gets the node where the value can be or should be inserted
	 *
	 * @param v
	 * @return
	 */
	public BTreeLeaf getChildNodeFor(Value v);

	/**
	 * Returns true if this node or any of its decendants contain the specified
	 * value
	 *
	 * @param v
	 * @return
	 */
	public boolean contains(Value v);

	/**
	 * Inserts the element. if there is not enough space reorganizes the tree
	 *
	 * @param v
	 * @param rowIndex
	 *            index where the value is at
	 * @return The new root if the reorganization of the tree changed the root
	 */
	public BTreeNode insert(Value v, int rowIndex);

	/**
	 * Deletes the element. The tree can be reorganized to match btree
	 * restrictions
	 *
	 * @param v
	 * @return The new root if the reorganization of the tree changed the root
	 */
	public BTreeNode delete(Value v);

	/**
	 * Sets the parent of a node
	 *
	 * @param m
	 */
	public void setParent(BTreeInteriorNode m);

	/**
	 * Gets the smaller value in the subtree represented by this node and its
	 * children that is not present in the subtree represented by the 'treeNode'
	 * node and its children
	 *
	 * @param treeNode
	 * @return
	 */
	public Value getSmallestValueNotIn(BTreeNode treeNode);

	/**
	 * Get the leaf that contains the smallest values
	 *
	 * @return
	 */
	public BTreeLeaf getFirstLeaf();

	/**
	 * Checks that the tree is well formed. Throws any exception if it's not.
	 * Just for debugging purposes
	 */
	public void checkTree();

	/**
	 * Gets the parent of the node. The root node returns null
	 *
	 * @return
	 */
	public BTreeInteriorNode getParent();
}
