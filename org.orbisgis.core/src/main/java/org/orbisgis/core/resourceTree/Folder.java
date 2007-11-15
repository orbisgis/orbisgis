package org.orbisgis.core.resourceTree;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Folder implements IResourceType {

	private final Icon emptyIcon = new ImageIcon(Folder.class
			.getResource("folder.png"));

	private final Icon openIcon = new ImageIcon(Folder.class
			.getResource("folder_magnify.png"));

	public Icon getIcon(INode node, boolean isExpanded) {
		Icon icon = emptyIcon;
		if (node.getChildCount() != 0) {
			if (!isExpanded) {
				icon = openIcon;
			}

		}

		return icon;
	}

	public void addToTree(INode parent, INode toAdd)
			throws ResourceTypeException {
		if (parent.getResourceType() instanceof Folder) {
			parent.addNode(toAdd);
		} else {
			throw new ResourceTypeException(
					"The folder cannot be added to a node of type "
							+ parent.getResourceType().getClass()
									.getCanonicalName());
		}
	}

	public void moveResource(INode src, INode dst) throws ResourceTypeException {
		if (dst.getResourceType() instanceof Folder) {
			src.getParent().removeNode(src);
			dst.addNode(src);
		} else {
			throw new ResourceTypeException(
					"The folder cannot be moved to a node of type "
							+ dst.getResourceType().getClass()
									.getCanonicalName());
		}
	}

	public void removeFromTree(INode toRemove) throws ResourceTypeException {
		toRemove.getParent().removeNode(toRemove);
		INode[] children = toRemove.getChildren();
		for (INode node : children) {
			node.removeNode(node);
		}
	}

	public void setName(INode node, String newName)
			throws ResourceTypeException {
		node.setName(newName);
	}

}
