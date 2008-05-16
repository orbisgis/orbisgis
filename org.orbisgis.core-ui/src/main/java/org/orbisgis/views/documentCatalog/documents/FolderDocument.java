package org.orbisgis.views.documentCatalog.documents;

import java.util.ArrayList;
import java.util.HashMap;

import org.orbisgis.progress.IProgressMonitor;
import org.orbisgis.views.documentCatalog.AbstractDocument;
import org.orbisgis.views.documentCatalog.DocumentException;
import org.orbisgis.views.documentCatalog.IDocument;

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

	public void openDocument(IProgressMonitor pm) throws DocumentException {
	}

	public void closeDocument(IProgressMonitor pm) throws DocumentException {
	}

	public HashMap<String, String> getPersistenceProperties()
			throws DocumentException {
		return null;
	}

	public void saveDocument(IProgressMonitor pm) throws DocumentException {
	}

	public void setPersistenceProperties(HashMap<String, String> properties)
			throws DocumentException {
	}

	public boolean allowsChildren() {
		return true;
	}

}
