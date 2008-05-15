package org.orbisgis.views.geocatalog;

import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.orbisgis.PersistenceException;
import org.orbisgis.Services;
import org.orbisgis.pluginManager.workspace.Workspace;
import org.orbisgis.resource.IResource;
import org.orbisgis.resource.IResourceType;
import org.orbisgis.resource.ResourceFactory;
import org.orbisgis.resource.ResourceTypeException;
import org.orbisgis.view.IView;
import org.orbisgis.views.geocatalog.persistence.Resource;
import org.orbisgis.window.EPWindowHelper;

public class CatalogView implements IView {

	private static final String CATALOG_PERSISTENCE_FILE = "org.orbisgis.GeoCatalog.xml";

	private Catalog catalog;

	public CatalogView() {
		catalog = new Catalog();
	}

	public void delete() {

	}

	public Component getComponent() {
		return catalog;
	}

	public void initialize() {

	}

	public void loadStatus() throws PersistenceException {
		Workspace ws = (Workspace) Services
				.getService("org.orbisgis.Workspace");
		File catalogFile = ws.getFile(CATALOG_PERSISTENCE_FILE);
		if (catalogFile.exists()) {
			try {
				JAXBContext jc = JAXBContext.newInstance(
						"org.orbisgis.views.geocatalog.persistence",
						EPWindowHelper.class.getClassLoader());
				org.orbisgis.views.geocatalog.persistence.Catalog cat = (org.orbisgis.views.geocatalog.persistence.Catalog) jc
						.createUnmarshaller().unmarshal(catalogFile);
				ResourceTreeModel treeModel = catalog.getTreeModel();
				IResource newRoot = populate(cat.getResource().get(0),
						treeModel);
				treeModel.setRootNode(newRoot);
			} catch (JAXBException e) {
				throw new PersistenceException("Cannot load geocatalog", e);
			} catch (InstantiationException e) {
				throw new PersistenceException("Cannot load geocatalog", e);
			} catch (IllegalAccessException e) {
				throw new PersistenceException("Cannot load geocatalog", e);
			} catch (ClassNotFoundException e) {
				throw new PersistenceException("Cannot load geocatalog", e);
			}
		}
	}

	private IResource populate(Resource xmlNode, ResourceTreeModel treeModel)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		String resourceTypeClassName = xmlNode.getTypeClass();
		IResourceType resourceType = (IResourceType) Class.forName(
				resourceTypeClassName).newInstance();
		String xmlName = xmlNode.getName();
		IResource newResource = ResourceFactory.createResource(xmlName,
				resourceType, treeModel);
		List<Resource> xmlChildren = xmlNode.getResource();
		for (int i = 0; i < xmlChildren.size(); i++) {
			try {
				newResource
						.addResource(populate(xmlChildren.get(i), treeModel));
			} catch (ResourceTypeException e) {
				Services.getErrorManager().error(
						"Cannot recover resource: " + xmlName, e);
			} catch (InstantiationException e) {
				Services.getErrorManager().error(
						"Cannot recover resource: " + xmlName, e);
			} catch (IllegalAccessException e) {
				Services.getErrorManager().error(
						"Cannot recover resource: " + xmlName, e);
			} catch (ClassNotFoundException e) {
				Services.getErrorManager().error(
						"Cannot recover resource: " + xmlName, e);
			}
		}

		return newResource;
	}

	private void populate(Resource xmlNode, IResource node) {
		xmlNode.setName(node.getName());
		xmlNode.setTypeClass(node.getResourceType().getClass()
				.getCanonicalName());
		for (int i = 0; i < node.getChildCount(); i++) {
			Resource xmlChild = new Resource();
			populate(xmlChild, node.getResourceAt(i));
			xmlNode.getResource().add(xmlChild);
		}
	}

	public void saveStatus() throws PersistenceException {
		org.orbisgis.views.geocatalog.persistence.Catalog catalog = new org.orbisgis.views.geocatalog.persistence.Catalog();
		Resource root = new Resource();
		populate(root, this.catalog.getTreeModel().getRoot());
		catalog.getResource().add(root);
		Workspace ws = (Workspace) Services
				.getService("org.orbisgis.Workspace");
		File file = ws.getFile(CATALOG_PERSISTENCE_FILE);
		try {
			JAXBContext jc = JAXBContext.newInstance(
					"org.orbisgis.views.geocatalog.persistence", EPWindowHelper.class
							.getClassLoader());
			jc.createMarshaller().marshal(catalog, new PrintWriter(file));
		} catch (JAXBException e) {
			throw new PersistenceException("Cannot save geocatalog", e);
		} catch (FileNotFoundException e) {
			throw new PersistenceException("Cannot write the file: " + file);
		}
	}

}
