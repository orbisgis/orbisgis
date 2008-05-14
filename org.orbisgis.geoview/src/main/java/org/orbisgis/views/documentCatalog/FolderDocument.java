package org.orbisgis.views.documentCatalog;

import java.util.ArrayList;
import java.util.HashMap;

public class FolderDocument extends AbstractDocument implements IDocument {

	private ArrayList<IDocument> children = new ArrayList<IDocument>();

	public void addDocument(IDocument document) {
		children.add(document);
	}

	public IDocument getDocument(int index) {
		return children.get(index);
	}

	public int getDocumentCount() {
		return children.size();
	}

	public void openDocument() throws DocumentException {
	}

	public void closeDocument() throws DocumentException {
	}

	public HashMap<String, String> getPersistenceProperties()
			throws DocumentException {
		return null;
	}

	public void saveDocument() throws DocumentException {
	}

	public void setPersistenceProperties(HashMap<String, String> properties)
			throws DocumentException {
	}

	public boolean allowsChildren() {
		return true;
	}

}
