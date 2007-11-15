package org.orbisgis.core.resourceTree;

import javax.swing.Icon;

public interface IResource {

	int getChildCount();

	IResource getResourceAt(int index);

	IResource[] getResources();

	IResource[] getResourcesRecursively();

	IResource[] getResourcePath();

	IResourceType getResourceType();

	int getIndex(IResource resource);

	IResource getParentResource();

	void addResource(IResource resource) throws ResourceTypeException;

	void removeResource(IResource resource) throws ResourceTypeException;

	void moveTo(IResource dest) throws ResourceTypeException;

	void setResourceName(String name) throws ResourceTypeException;

	Icon getIcon(boolean expanded);

	String getName();
}
