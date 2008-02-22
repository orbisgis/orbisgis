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
//		envelopes = new Envelope[btree.getN() + 1]; // for intermediate node
		// overload management
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

	protected abstract boolean isValid(int valueCount) throws IOException;

	protected RTreeNode adjustAfterDeletion() throws IOException {
		if (isValid(valueCount)) {
			return null;
		} else {
			if (parentDir == -1) {
				// If it's the root just change the root
				return getChildForNewRoot();
			} else {
				if (getParent().moveFromNeighbour(this)) {
					return adjustAfterDeletion();
				} else {
					getParent().mergeWithNeighbour(this);
					return ((RTreeInteriorNode) getParent())
							.adjustAfterDeletion();
				}
			}
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
		if (parent == null) {
			parent = (RTreeInteriorNode) tree.readNodeAt(parentDir);
		}
		return parent;
	}

//	/**
//	 * Shifts one place to the right the values array from the specified
//	 * position
//	 *
//	 * @param index
//	 *            index to start the shifting
//	 */
//	protected void shiftValuesFromIndexToRight(int index) {
//		for (int i = valueCount - 1; i >= index; i--) {
//			envelopes[i + 1] = envelopes[i];
//		}
//	}
//
//	/**
//	 * Shifts to the left the values array from the specified position the
//	 * number of places specified in the 'places' argument
//	 *
//	 * @param index
//	 *            index to start the shifting
//	 */
//	protected void shiftValuesFromIndexToLeft(int index) {
//		for (int j = index - 1; j + 1 < valueCount; j++) {
//			envelopes[j] = envelopes[j + 1];
//		}
//	}

//	/**
//	 * Gets the index of a value. If the value exist it returns its index.
//	 * Otherwise it returns the place where it should be inserted
//	 *
//	 * @param v
//	 *            search key
//	 * @param values
//	 *            keys to search
//	 * @param valueCount
//	 *            number of values
//	 * @return The index in the value array where this value will be inserted
//	 */
//	protected int getIndexOf(Envelope v) {
//		int index = valueCount;
//		for (int i = 0; i < valueCount; i++) {
//			if (envelopes[i].intersects(v)) {
//				index = i;
//				break;
//			}
//		}
//		return index;
//	}

	public int getParentDir() {
		return parentDir;
	}

	public int getDir() {
		return dir;
	}
}
