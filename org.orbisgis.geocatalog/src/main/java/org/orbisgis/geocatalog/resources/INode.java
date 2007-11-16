package org.orbisgis.geocatalog.resources;

public interface INode {

	IResourceType getResourceType();

	int getChildCount();

	INode getChildAt(int index);

	INode[] getChildren();

	INode[] getChildrenRecursively();

	INode[] getPath();

	int getIndex(INode node);

	INode getParent();

	void addNode(INode node);

	void addNode(INode node, int index);

	void removeNode(INode node);

	void setParent(INode node);

	public String getName();

	public void setName(String name);
}
