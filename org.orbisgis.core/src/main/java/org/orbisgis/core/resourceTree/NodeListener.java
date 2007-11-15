package org.orbisgis.core.resourceTree;


public interface NodeListener {

	void nodeAdded(INode parent, INode addedNode);

	void nodeRemoved(INode parent, INode removedNode);

	void nodeMoved(INode sourceParent, INode dstParent, INode movedNode);

}
