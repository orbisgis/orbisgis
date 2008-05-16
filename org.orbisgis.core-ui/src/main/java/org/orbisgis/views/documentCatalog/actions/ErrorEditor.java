package org.orbisgis.views.documentCatalog.actions;

import java.awt.Component;
import java.util.HashMap;

import javax.swing.JLabel;

import org.orbisgis.editor.IEditor;
import org.orbisgis.progress.IProgressMonitor;
import org.orbisgis.views.documentCatalog.AbstractDocument;
import org.orbisgis.views.documentCatalog.DocumentException;
import org.orbisgis.views.documentCatalog.IDocument;

public class ErrorEditor implements IEditor {

	private String message;
	private String documentName;

	public ErrorEditor(String documentName, String message) {
		this.message = message;
		this.documentName = documentName;
	}

	public boolean acceptDocument(IDocument doc) {
		return false;
	}

	public IDocument getDocument() {
		return new DummyDocument();
	}

	public String getTitle() {
		return documentName;
	}

	public void setDocument(IDocument doc) {
	}

	public void delete() {
	}

	public Component getComponent() {
		return new JLabel(message);
	}

	public void initialize() {

	}

	public void loadStatus() {
	}

	public void saveStatus() {
	}

	private class DummyDocument extends AbstractDocument implements IDocument {

		public void addDocument(IDocument document) {

		}

		public boolean allowsChildren() {
			return false;
		}

		public void closeDocument(IProgressMonitor pm) throws DocumentException {

		}

		public IDocument getDocument(int index) {
			return null;
		}

		public int getDocumentCount() {
			return 0;
		}

		public String getName() {
			return message;
		}

		public HashMap<String, String> getPersistenceProperties()
				throws DocumentException {
			return null;
		}

		public void openDocument(IProgressMonitor pm) throws DocumentException {
		}

		public void saveDocument(IProgressMonitor pm) throws DocumentException {
		}

		public void setPersistenceProperties(HashMap<String, String> properties)
				throws DocumentException {
		}

		public void removeDocument(IDocument document) {

		}

	}

	public void closingEditor() {
	}

}
