package org.gdms.data.indexes.rtree;

import java.io.IOException;

import com.vividsolutions.jts.geom.Envelope;

public abstract class AbstractRTreeNode implements RTreeNode {

	private static int nodes = 0;

	private int parentDir;
	protected String name;

	protected DiskRTree tree;

	protected int dir;

	private RTreeInteriorNode parent;

	public AbstractRTreeNode(DiskRTree btree, int dir, int parentDir) {
		this.tree = btree;
		this.dir = dir;
		this.parentDir = parentDir;
		this.name = "node-" + nodes;
		nodes++;
	}

	public void setParentDir(int parentDir) {
		if (this.parentDir != parentDir) {
			this.parentDir = parentDir;
			this.parent = null;
		}
	}

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

	protected double getExpandImpact(int ref, int i) throws IOException {
		Envelope op1 = new Envelope(getEnvelope(ref));
		double initialArea = op1.getWidth() * op1.getHeight();
		op1.expandToInclude(getEnvelope(i));
		double finalArea = op1.getWidth() * op1.getHeight();
		double diff = finalArea - initialArea;
		return diff;
	}

	protected abstract Envelope getEnvelope(int index) throws IOException;

	protected int getFarthestGeometry(int ref, Envelope refEnvelope)
			throws IOException {
		double maxDistance = Double.MIN_VALUE;
		int argmax = -1;
		for (int i = 0; i < getValueCount(); i++) {
			if (i == ref) {
				continue;
			} else {
				double distance = getEnvelope(i).distance(refEnvelope);
				if (distance > maxDistance) {
					maxDistance = distance;
					argmax = i;
				}

			}
		}
		return argmax;
	}

}
