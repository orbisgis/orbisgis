package org.gdms.data.indexes.rtree;

import java.io.IOException;

public abstract class AbstractRTreeNode implements RTreeNode {

	private static int nodes = 0;

//	protected Envelope[] envelopes;
	protected int valueCount;
	private int parentDir;
	protected String name;

	protected DiskRTree tree;

	protected int dir;

	private RTreeInteriorNode parent;

	public AbstractRTreeNode(DiskRTree btree, int dir, int parentDir) {
		this.tree = btree;
		this.dir = dir;
		this.parentDir = parentDir;
		valueCount = 0;
		this.name = "node-" + nodes;
		nodes++;
	}

	public void setParentDir(int parentDir) {
		if (this.parentDir != parentDir) {
			this.parentDir = parentDir;
			this.parent = null;
		}
	}

	/**
	 * When the root has less than the valid number of elements this method is
	 * called to substitute the root
	 *
	 * @return
	 * @throws IOException
	 */
	protected abstract RTreeNode getChildForNewRoot() throws IOException;

	/**
	 * Moves the first element into the specified node. The parameter is an
	 * instance of the same class than this
	 *
	 * @param node
	 * @throws IOException
	 */
	protected abstract void moveFirstTo(AbstractRTreeNode treeInteriorNode)
			throws IOException;

	/**
	 * Moves the first element into the specified node. The parameter is an
	 * instance of the same class than this
	 *
	 * @param node
	 * @throws IOException
	 */
	protected abstract void moveLastTo(AbstractRTreeNode treeInteriorNode)
			throws IOException;

	/**
	 * Takes all the content of the left node and puts it at the end of this
	 * node
	 *
	 * @throws IOException
	 */
	protected abstract void mergeWithRight(AbstractRTreeNode rightNode)
			throws IOException;

	/**
	 * Takes all the content of the left node and puts it at the beginning of
	 * this node
	 *
	 * @throws IOException
	 */
	protected abstract void mergeWithLeft(AbstractRTreeNode leftNode)
			throws IOException;

	public RTreeInteriorNode getParent() throws IOException {
		if ((parent == null) && (parentDir != -1)) {
			parent = (RTreeInteriorNode) tree.readNodeAt(parentDir);
		}
		return parent;
	}

	public int getParentDir() {
		return parentDir;
	}

	public int getDir() {
		return dir;
	}
}
