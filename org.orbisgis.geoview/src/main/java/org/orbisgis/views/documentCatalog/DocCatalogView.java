package org.orbisgis.views.documentCatalog;

import java.awt.Component;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.orbisgis.PersistenceException;
import org.orbisgis.Services;
import org.orbisgis.pluginManager.workspace.Workspace;
import org.orbisgis.view.IView;
import org.orbisgis.views.documentCatalog.documents.FolderDocument;
import org.orbisgis.views.documentCatalog.persistence.Document;
import org.orbisgis.views.documentCatalog.persistence.Property;

public class DocCatalogView implements IView {

	private Component catalog;
	private IDocument root = new FolderDocument();

	public void delete() {

	}

	public Component getComponent() {
		if (catalog == null) {
			catalog = new DocumentCatalog(root);
		}

		return catalog;
	}

	public void initialize() {

	}

	public void loadStatus() throws PersistenceException {
		Workspace ws = (Workspace) Services
				.getService("org.orbisgis.Workspace");
		File file = ws.getFile("org.orbisgis.view.DocumentCatalog.xml");
		if (file.exists()) {
			try {
				JAXBContext jc = getJaxbContext();
				Document rootDocument = (Document) jc.createUnmarshaller()
						.unmarshal(
								new BufferedInputStream(new FileInputStream(
										file)));
				root = createDocumentTree(rootDocument);
			} catch (IOException e) {
				throw new PersistenceException("Cannot recover "
						+ "document catalog", e);
			} catch (JAXBException e) {
				throw new PersistenceException("Cannot recover "
						+ "document tree", e);
			} catch (InstantiationException e) {
				throw new PersistenceException("Cannot recover "
						+ "document tree", e);
			} catch (IllegalAccessException e) {
				throw new PersistenceException("Cannot recover "
						+ "document tree", e);
			} catch (ClassNotFoundException e) {
				throw new PersistenceException("Cannot recover "
						+ "document tree", e);
			} catch (DocumentException e) {
				throw new PersistenceException("Cannot recover "
						+ "document tree", e);
			}
		}
	}

	/**
	 * Write a tree of documents with the name and icon as attributes and the
	 * persistent properties as sub elements
	 *
	 * @see org.orbisgis.view.IView#saveStatus()
	 */
	public void saveStatus() throws PersistenceException {
		Workspace ws = (Workspace) Services
				.getService("org.orbisgis.Workspace");
		File file = ws.getFile("org.orbisgis.view.DocumentCatalog.xml");
		try {
			Document rootDocument = getPersistenceTree(root);
			JAXBContext jc = getJaxbContext();
			jc.createMarshaller().marshal(rootDocument,
					new BufferedOutputStream(new FileOutputStream(file)));
		} catch (DocumentException e) {
			throw new PersistenceException("Cannot save document", e);
		} catch (IOException e) {
			throw new PersistenceException("Cannot write tree", e);
		} catch (JAXBException e) {
			throw new PersistenceException("Cannot write tree", e);
		}
	}

	private JAXBContext getJaxbContext() throws JAXBException {
		return JAXBContext.newInstance(
				"org.orbisgis.views.documentCatalog.persistence",
				DocCatalogView.class.getClassLoader());
	}

	private IDocument createDocumentTree(Document node)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, DocumentException {
		String clazz = node.getClazz();
		IDocument doc = (IDocument) Class.forName(clazz).newInstance();
		doc.setName(node.getName());

		// set the properties
		List<Property> properties = node.getProperty();
		HashMap<String, String> persistenceProperties = new HashMap<String, String>();
		for (Property property : properties) {
			persistenceProperties.put(property.getName(), property.getValue());
		}
		doc.setPersistenceProperties(persistenceProperties);

		// set the children
		List<Document> children = node.getDocument();
		for (Document child : children) {
			doc.addDocument(createDocumentTree(child));
		}

		return doc;
	}

	private Document getPersistenceTree(IDocument node)
			throws DocumentException {
		Document ret = new Document();
		String clazz = node.getClass().getCanonicalName();
		ret.setClazz(clazz);
		ret.setName(node.getName());
		HashMap<String, String> properties = node.getPersistenceProperties();
		if (properties != null) {
			Iterator<String> it = properties.keySet().iterator();
			while (it.hasNext()) {
				String propertyName = it.next();
				Property property = new Property();
				property.setName(propertyName);
				property.setValue(properties.get(propertyName));
				ret.getProperty().add(property);
			}
		}
		for (int i = 0; i < node.getDocumentCount(); i++) {
			ret.getDocument().add(getPersistenceTree(node.getDocument(i)));
		}

		return ret;
	}
}
