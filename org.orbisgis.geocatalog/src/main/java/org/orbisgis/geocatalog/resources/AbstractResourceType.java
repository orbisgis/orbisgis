package org.orbisgis.geocatalog.resources;

public abstract class AbstractResourceType implements IResourceType {

	public void addToTree(INode parent, INode toAdd)
			throws ResourceTypeException {
		while (parent != null) {
			if (parent.getResourceType() instanceof Folder) {
				parent.addNode(toAdd);
				return;
			} else {
				parent = parent.getParent();
			}
		}
		throw new ResourceTypeException(
				"The folder cannot be added to a node of type "
						+ parent.getResourceType().getClass()
								.getCanonicalName());
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
	}

	public void setName(INode node, String newName)
			throws ResourceTypeException {
		node.setName(newName);
	}

}
