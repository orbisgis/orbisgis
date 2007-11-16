package org.orbisgis.geocatalog.resources;

import javax.swing.Icon;

public interface IResourceType {

	void setName(INode node, String newName) throws ResourceTypeException;

	/**
	 * Moves the src node to the dst node. The type of the source node is the
	 * one this class implements so it can be safely casted
	 *
	 * @param src
	 * @param dst
	 * @throws ResourceTypeException
	 */
	void moveResource(INode src, INode dst) throws ResourceTypeException;

	/**
	 * Removes the specified node from the tree. The type of the node is the one
	 * this class implements so it can be safely casted
	 *
	 * @param toRemove
	 * @throws ResourceTypeException
	 */
	void removeFromTree(INode toRemove) throws ResourceTypeException;

	/**
	 * Adds the specified node to the specified parent node. The type of the
	 * node to add is the one this class implements, so it can be safely casted
	 *
	 * @param parent
	 * @param toAdd
	 * @throws ResourceTypeException
	 */
	void addToTree(INode parent, INode toAdd) throws ResourceTypeException;

	Icon getIcon(INode node, boolean isExpanded);
}
