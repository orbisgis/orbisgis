package org.orbisgis.views.documentCatalog;

import java.util.HashMap;

import javax.swing.Icon;

public class DocumentDecorator implements IDocument {

	private IDocument document;
	private int numEditors = 0;

	public DocumentDecorator(IDocument document) {
		this.document = document;
	}

	public void addDocument(IDocument document) {
		document.addDocument(document);
	}

	public IDocument getDocument(int index) {
		return document.getDocument(index);
	}

	public int getDocumentCount() {
		return document.getDocumentCount();
	}

	public Icon getIcon() {
		return document.getIcon();
	}

	public String getName() {
		return document.getName();
	}

	public void openDocument() throws DocumentException {
		if (numEditors == 0) {
			document.openDocument();
		}
		numEditors++;
	}

	public void setName(String name) {
		document.setName(name);
	}

	public IDocument getDocument() {
		return document;
	}

	public void closeDocument() throws DocumentException {
		document.closeDocument();
	}

	public HashMap<String, String> getPersistenceProperties()
			throws DocumentException {
		return document.getPersistenceProperties();
	}

	public void saveDocument() throws DocumentException {
		document.saveDocument();
	}

	public void setPersistenceProperties(HashMap<String, String> properties)
			throws DocumentException {
		document.setPersistenceProperties(properties);
	}

	public boolean allowsChildren() {
		return document.allowsChildren();
	}
}
