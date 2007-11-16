package org.orbisgis.geocatalog.resources;

public class ResourceFactory {

	public static IResource createResource(String name, IResourceType type,
			ResourceTreeModel resourceTreeModel) {
		ResourceDecorator ret = new ResourceDecorator(name, type);
		ret.setTreeModel(resourceTreeModel);

		return ret;
	}

	public static IResource createResource(String name, IResourceType type) {
		return new ResourceDecorator(name, type);
	}

}
